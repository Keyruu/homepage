import mill._, scalalib._
import mill.modules.Jvm

object app extends ScalaModule {
  def scalaVersion = "3.7.1"

  def ivyDeps = Agg(
    // ZIO Core
    ivy"dev.zio::zio:2.1.21",
    ivy"dev.zio::zio-streams:2.1.21",

    // HTTP Server
    ivy"dev.zio::zio-http:3.5.1",

    // Database (ZIO Quill)
    ivy"io.getquill::quill-zio:4.8.6",
    ivy"io.getquill::quill-jdbc-zio:4.8.6",
    ivy"org.xerial:sqlite-jdbc:3.47.2.0",
    ivy"com.zaxxer:HikariCP:6.2.1",

    // HTML Templating (keeping scalatags, works great with ZIO)
    ivy"com.lihaoyi::scalatags:0.13.1",
    ivy"org.commonmark:commonmark:0.25.1",

    // Config
    ivy"dev.zio::zio-config:4.0.2",
    ivy"dev.zio::zio-config-typesafe:4.0.2",

    // Logging
    ivy"dev.zio::zio-logging:2.4.0",
    ivy"dev.zio::zio-logging-slf4j2:2.4.0",
    ivy"ch.qos.logback:logback-classic:1.5.18"
  )

  // Main class for the application
  def mainClass = Some("Main")

  // Create a fat JAR
  def assembly = T {
    mill.modules.Assembly.createAssembly(
      Agg.from(localClasspath().map(_.path)),
      manifest = mill.modules.Jvm.createManifest(mainClass()),
      prependShellScript = ""
    )
  }

  // GraalVM Native Image configuration
  def nativeImageName = "homepage"
  
  def nativeImageGraalVmJvmId = "graalvm-java21:24.1.1"
  
  def nativeImageClassPath = T {
    runClasspath()
  }

  def nativeImageOptions = T {
    Seq(
      "--no-fallback",
      "--enable-http",
      "--enable-https", 
      "--install-exit-handlers",
      "-H:+ReportExceptionStackTraces",
      "--initialize-at-build-time=ch.qos.logback",
      "--initialize-at-build-time=org.slf4j",
      "--initialize-at-run-time=io.netty",
      "-H:+StaticExecutableWithDynamicLibC",
      "--features=org.graalvm.home.HomeFinderFeature"
    )
  }

  def nativeImage = T {
    val classpath = nativeImageClassPath().map(_.path).mkString(":")
    val main = mainClass().getOrElse(throw new Exception("No main class defined"))
    val outputPath = T.dest / nativeImageName
    
    os.proc(
      "native-image",
      "-cp", classpath,
      main,
      "-o", outputPath.toString,
      nativeImageOptions()
    ).call(cwd = T.dest)
    
    PathRef(outputPath)
  }
}
