package de.keyruu.homepage

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import zio.*
import zio.http.*
import de.keyruu.homepage.data.blog.BlogStore
import de.keyruu.homepage.data.BlogRepo
import de.keyruu.homepage.api.BlogRoutes
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault:
  override val bootstrap: ULayer[Unit] =
    Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  def run =
    ZIO
      .serviceWithZIO[BlogRoutes] { blogRoutes =>
        val app = blogRoutes.routes @@
          Middleware.serveResources(Path.empty / "public") @@
          Middleware.serveResources(Path.empty / "images", "content/images")
        Server.serve(app)
      }
      .provide(
        BlogStore.layer,
        BlogRepo.layer,
        BlogRoutes.layer,
        Server.defaultWithPort(8080)
      )
