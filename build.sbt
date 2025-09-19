ThisBuild / scalaVersion := "3.7.1"
ThisBuild / organization := "de.keyruu"
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "homepage",
    libraryDependencies ++= Seq(
      // ZIO Core
      "dev.zio" %% "zio" % "2.1.21",
      "dev.zio" %% "zio-streams" % "2.1.21",
      "dev.zio" %% "zio-prelude" % "1.0.0-RC41",

      // HTTP Server
      "dev.zio" %% "zio-http" % "3.5.1",

      // Database (ZIO Quill)
      "io.getquill" %% "quill-zio" % "4.8.6",
      "io.getquill" %% "quill-jdbc-zio" % "4.8.6",
      "org.xerial" % "sqlite-jdbc" % "3.47.2.0",
      "com.zaxxer" % "HikariCP" % "6.2.1",

      // HTML Templating
      "com.lihaoyi" %% "scalatags" % "0.13.1",
      "org.commonmark" % "commonmark" % "0.26.0",
      "org.commonmark" % "commonmark-ext-yaml-front-matter" % "0.26.0",

      // Config
      "dev.zio" %% "zio-config" % "4.0.2",
      "dev.zio" %% "zio-config-typesafe" % "4.0.2",

      // Logging
      "dev.zio" %% "zio-logging" % "2.4.0",
      "dev.zio" %% "zio-logging-slf4j2" % "2.4.0",
      "ch.qos.logback" % "logback-classic" % "1.5.18"
    ),

    // Main class
    Compile / mainClass := Some("de.keyruu.homepage.Main"),

    // Assembly settings
    assembly / mainClass := Some("de.keyruu.homepage.Main"),
    assembly / assemblyJarName := "homepage.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case "module-info.class"           => MergeStrategy.discard
      case x                             => MergeStrategy.first
    }
  )
