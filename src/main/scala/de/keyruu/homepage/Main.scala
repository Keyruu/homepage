package de.keyruu.homepage

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import zio.*
import zio.http.*

object Main extends ZIOAppDefault:
  def run =
    Server
      .serve(MainRoutes())
      .provide(
        Server.defaultWithPort(8080)
      )
