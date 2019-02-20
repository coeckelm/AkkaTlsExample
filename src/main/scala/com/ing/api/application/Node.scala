package com.ing.api.application

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.UseHttp2.Negotiated
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.ServerSettings
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.stream.{ActorMaterializer, TLSClientAuth}
import com.typesafe.sslconfig.akka.AkkaSSLConfig
import com.typesafe.sslconfig.akka.util.AkkaLoggerFactory
import com.typesafe.sslconfig.ssl.ConfigSSLContextBuilder
import javax.net.ssl.SSLContext

import scala.concurrent._
import scala.util.{Failure, Success, Try}

/**
  * Provide functionality to start http or/and https server with implicit routes
  */
trait Node {

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val executionContext: ExecutionContextExecutor

  /**
    * Override to implement the routes that will be served by this http server.
    */
  protected def routes: Route

  private val nodeSettings: NodeSettings = NodeSettings(system)

  var bindings: Set[Future[ServerBinding]] = Set[Future[ServerBinding]]()

  /**
    * Start the servers configurared in the configuration
    * Note that this method is blocking.
    *
    */
  def startup(): Future[Done] = {
    // startup HTTP server if enabled
    if (nodeSettings.enableHttp) {
      bindings = bindings + bindServer(routes, nodeSettings.httpSettings)
    }

    // startup HTTPS server if enabled
    if (nodeSettings.enableHttps) {
      bindings = bindings + bindServer(
        routes,
        nodeSettings.httpsSettings,
        Some(createServerHttpsContext(AkkaSSLConfig()))
      )
    }
    // for each server binding call the postBinding method on complete to log server startup
    bindings.foreach { futureBinding =>
      futureBinding.onComplete {
        case Success(binding) =>
          postBinding(binding)
        case Failure(cause) =>
          postBindingFailure(cause)
      }
    }

    val started = Promise[Done]()
    //When all bindings complete
    Future.sequence(bindings).onComplete {
      case Success(value) => // all bindings completed successfully
        system.log.info("Node bindings started successfull") // log node startup success
        waitForShutdownSignal(system)
          .onComplete(_ => shutdown()) // register shutdown hook, when shutdownhook completes, stop the server
        started.trySuccess(Done)       // register started as success
      case Failure(cause) => // at least 1 binding failed
        system.log.error("Node bindings starting failed", cause) // log cause of failure
        shutdown()                                               // stop the node
        started.tryFailure(cause) // register started as failure
    }
    started.future
  }

  private def shutdown(): Unit = {
    val futureUnbindings: Set[Future[Done]] =
      bindings.map { futureBinding =>
        futureBinding
          .flatMap { binding: ServerBinding =>
          {
            val unbinding: Future[Done] = binding.unbind()
            unbinding.onComplete(attempt => postServerShutdown(attempt, system))
            unbinding
          }
          }
      }
    Future.sequence(futureUnbindings).onComplete(_ => system.terminate())
  }

  def bindServer(
                  routes: Route,
                  httpSettings: HttpSettings,
                  connectionContext: Option[ConnectionContext] = None
                ): Future[ServerBinding] =
    Http().bindAndHandle(
      handler = routes,
      interface = httpSettings.httpHost,
      port = httpSettings.httpPort,
      connectionContext = connectionContext.getOrElse(Http().defaultServerHttpContext),
      settings = ServerSettings(system)
    )

  def createServerHttpsContext(sslConfig: AkkaSSLConfig): HttpsConnectionContext = {
    val config = sslConfig.config

    val mkLogger = new AkkaLoggerFactory(system)

    // initial ssl context!
    val sslContext = if (sslConfig.config.default) {
      system.log.debug("buildSSLContext: ssl-config.default is true, using default SSLContext")
      sslConfig.validateDefaultTrustManager(config)
      SSLContext.getDefault
    } else {
      val keyManagerFactory   = sslConfig.buildKeyManagerFactory(config)
      val trustManagerFactory = sslConfig.buildTrustManagerFactory(config)
      new ConfigSSLContextBuilder(mkLogger, config, keyManagerFactory, trustManagerFactory).build()
    }

    // protocols!
    val defaultParams    = sslContext.getDefaultSSLParameters
    val defaultProtocols = defaultParams.getProtocols
    val protocols        = sslConfig.configureProtocols(defaultProtocols, config)
    defaultParams.setProtocols(protocols)

    // ciphers!
    val defaultCiphers = defaultParams.getCipherSuites
    val cipherSuites   = sslConfig.configureCipherSuites(defaultCiphers, config)
    defaultParams.setCipherSuites(cipherSuites)

    // auth!
    import com.typesafe.sslconfig.ssl.{ClientAuth => SslClientAuth}

    val clientAuth = config.sslParametersConfig.clientAuth match {
      case SslClientAuth.Default => None
      // need to set the ClientAuthentication also on the SSL Parameters.They overwrite the clientAuth setting on the HttpsConnectionContext
      case SslClientAuth.Want =>
        defaultParams.setWantClientAuth(true)
        Some(TLSClientAuth.Want)
      case SslClientAuth.Need =>
        defaultParams.setNeedClientAuth(true)
        Some(TLSClientAuth.Need)
      case SslClientAuth.None =>
        defaultParams.setNeedClientAuth(false)
        Some(TLSClientAuth.None)
    }

    // hostname!
    if (!sslConfig.config.loose.disableHostnameVerification) {
      defaultParams.setEndpointIdentificationAlgorithm("HTTPS")
    }

    new HttpsConnectionContext(
      sslContext,
      Some(sslConfig),
      Some(cipherSuites.toList),
      Some(defaultProtocols.toList),
      clientAuth,
      Some(defaultParams),
      Negotiated
    )
  }

  /**
    * Hook that will be called just after the server termination. Override this method if you want to perform some cleanup actions after the server is stopped.
    * The `attempt` parameter is represented with a Try type that is successful only if the server was successfully shut down.
    */
  protected def postServerShutdown(attempt: Try[Done], system: ActorSystem): Unit =
    system.log.info("Shutting down the server")

  /**
    * Hook that will be called just after the Http server binding is done. Override this method if you want to perform some actions after the server is up.
    */
  protected def postBinding(binding: Http.ServerBinding): Unit =
    system.log.info(s"Server online at ${binding.localAddress.getHostName}:${binding.localAddress.getPort}/")

  /**
    * Hook that will be called in case the Http server binding fails. Override this method if you want to perform some actions after the server binding failed.
    */
  protected def postBindingFailure(cause: Throwable): Unit =
    system.log.error(cause, s"Error starting the server ${cause.getMessage}")

  /**
    * Hook that lets the user specify the future that will signal the shutdown of the server whenever completed.
    * Application can only be stopped by receiving a kill signal
    */
  protected def waitForShutdownSignal(system: ActorSystem)(implicit ec: ExecutionContext): Future[Done] = {
    val promise = Promise[Done]()
    sys.addShutdownHook {
      promise.trySuccess(Done)
    }
    promise.future
  }
}
