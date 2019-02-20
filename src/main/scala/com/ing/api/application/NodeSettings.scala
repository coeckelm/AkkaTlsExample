package com.ing.api.application

import com.ing.api.config.{SettingsRoot, SettingsRootCompanion}
import com.typesafe.sslconfig.util.EnrichedConfig

/**
  * Node settings
  *
  */
object NodeSettings extends SettingsRootCompanion[NodeSettings] {
  override val path: String = "node"

  private val DefaultHost      = "localhost"
  private val DefaultHttpPort  = 80
  private val DefaultHttpsPort = 443

  def parseHttpSettings(config: Option[EnrichedConfig], defaultHost: String, defaultPort: Int): HttpSettings =
    config match {
      case Some(c) =>
        val httpHost = c.getOptional[String]("host").getOrElse(defaultHost)
        val httpPort = c.getOptional[Int]("port").getOrElse(defaultPort)
        HttpSettings(httpHost, httpPort)
      case _ => HttpSettings(defaultHost, defaultPort)
    }

  override protected def makeSettings(config: EnrichedConfig): NodeSettings = {
    val enableHttp    = config.getOptional[Boolean]("enable-http").getOrElse(false)
    val enableHttps   = config.getOptional[Boolean]("enable-https").getOrElse(false)
    val httpSettings  = parseHttpSettings(config.getOptional[EnrichedConfig]("http"), DefaultHost, DefaultHttpPort)
    val httpsSettings = parseHttpSettings(config.getOptional[EnrichedConfig]("https"), DefaultHost, DefaultHttpsPort)

    new NodeSettings(
      enableHttp,
      enableHttps,
      httpSettings,
      httpsSettings
    )
  }
}

final case class NodeSettings(
                               enableHttp: Boolean,
                               enableHttps: Boolean,
                               httpSettings: HttpSettings,
                               httpsSettings: HttpSettings
                             ) extends SettingsRoot

final case class HttpSettings(
                               httpHost: String,
                               httpPort: Int
                             ) extends SettingsRoot
