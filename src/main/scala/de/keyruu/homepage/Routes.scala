package de.keyruu.homepage

import zio.*
import zio.http.*
import scalatags.Text.all._
import zio.http.template.Attributes
import zio.http.template.Html

object MainRoutes:
  def apply(): Routes[Any, Response] =
    Routes(
      Method.GET / "" -> handler { (req: Request) =>
        ZIO.succeed {
          val htmlContent = html(
            head(
              tag("title")("Greeting")
            ),
            body(
              h1("Hello")
            )
          )

          Response.html(Html.raw(htmlContent.render))
        }
      }
    )
