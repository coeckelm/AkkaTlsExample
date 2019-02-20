import sbt.Keys._
import sbt._
//import sbtassembly.{AssemblyKeys, MergeStrategy}
//import com.lightbend.sbt.javaagent.JavaAgent.JavaAgentKeys

object CommonSettings {
  lazy val commonSettings = Seq(
    organization := "com.lightbend.training",
    version := "1.3.0",
    scalaVersion := Version.scalaVer,
    scalacOptions ++= CompileOptions.compileOptions,
    unmanagedSourceDirectories in Compile := List((scalaSource in Compile).value, (javaSource in Compile).value),
    unmanagedSourceDirectories in Test := List((scalaSource in Compile).value, (javaSource in Compile).value),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
    parallelExecution in Test := false,
    logBuffered in Test := false,
    parallelExecution in ThisBuild := false,
    parallelExecution in GlobalScope := false,
    fork in Test := true,
    libraryDependencies ++= Dependencies.dependencies
  ) 
  
  lazy val configure: Project => Project = (proj: Project) => {
    proj
    .settings(CommonSettings.commonSettings: _*)
//      .settings(      
//        AssemblyKeys.assembly := Def.task {
//        JavaAgentKeys.resolvedJavaAgents.value.filter(_.agent.name == "Cinnamon").foreach { agent =>
//          sbt.IO.copyFile(agent.artifact, target.value / "cinnamon-agent.jar")
//        }
//        AssemblyKeys.assembly.value
//      }.value,
//        assemblyMergeStrategy in assembly := {
//          case "cinnamon-reference.conf" => MergeStrategy.concat
//          case x =>
//            val oldStrategy = (assemblyMergeStrategy in assembly).value
//            oldStrategy(x)
//        }
//      )
  }
}
