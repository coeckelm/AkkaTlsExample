import sbt._

object Version {
  val akkaVer         = "2.5.20"
  val akkaHttp        = "10.1.7"
  val akkaStreamKafka = "0.22"
  val logbackVer      = "1.2.3"
  val scalaVer        = "2.12.6"
  val scalaParsersVer = "1.0.4"
  val scalaTestVer    = "3.0.5"
  val apacheDeamonVersion = "1.1.0"
  val scalaLoggingVersion = "3.9.0"
}

object Dependencies {
  val circeVersion = "0.9.3"
  val dependencies: Seq[ModuleID] = Seq(
    "com.typesafe.akka"         %% "akka-actor"                 % Version.akkaVer,
    "com.typesafe.akka"         %% "akka-slf4j"                 % Version.akkaVer,
    "com.typesafe.akka"         %% "akka-cluster-tools"         % Version.akkaVer,
    "com.typesafe.akka"         %% "akka-cluster"               % Version.akkaVer,
    "com.typesafe.akka"         %% "akka-cluster-sharding"      % Version.akkaVer,
    "com.typesafe.akka"         %% "akka-distributed-data"      % Version.akkaVer,
    "com.typesafe.akka"         %% "akka-persistence"           % Version.akkaVer,
    "com.typesafe.akka"         %% "akka-slf4j"                 % Version.akkaVer,
    "com.typesafe.akka"         %% "akka-stream-kafka"          % Version.akkaStreamKafka,
    "com.typesafe.akka"         %% "akka-http"                  % Version.akkaHttp,
    "com.typesafe.akka"         %% "akka-http-spray-json"       % Version.akkaHttp,
    "ch.qos.logback"            %  "logback-classic"            % Version.logbackVer,
    "com.typesafe.akka"         %% "akka-http"                  % Version.akkaHttp,
    "com.typesafe.akka"         %% "akka-http-spray-json"       % Version.akkaHttp,
    "com.typesafe.akka"         %% "akka-http-xml"              % Version.akkaHttp,
    "com.typesafe.akka"         %% "akka-stream"                % Version.akkaVer,
    "com.typesafe.akka"         %% "akka-testkit"               % Version.akkaVer            % Test,
    "org.scalatest"             %% "scalatest"                  % Version.scalaTestVer       % Test,
    "org.fusesource.leveldbjni"  % "leveldbjni-all"             % "1.8",
    "de.heikoseeberger" %% "akka-http-circe" % "1.21.0",
    "commons-daemon"              % "commons-daemon"              % Version.apacheDeamonVersion,
    "com.typesafe.scala-logging"  %% "scala-logging"              % Version.scalaLoggingVersion
  ) ++ Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-optics",
    "io.circe" %% "circe-generic-extras"
  ).map(_ % circeVersion)
}
