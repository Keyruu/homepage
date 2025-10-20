package de.keyruu.homepage.api

import de.keyruu.homepage.data.BlogRepo
import de.keyruu.homepage.logic.ErrorMapper.*
import zio.*
import zio.http.*
import scalatags.Text
import scalatags.Text.all._
import de.keyruu.homepage.data.blog.BlogPost
import de.keyruu.homepage.ui.layouts.BlogPostPage
import de.keyruu.homepage.ui.layouts.BlogPage

case class BlogRoutes private (repo: BlogRepo):
  def routes: Routes[Any, Response] = Routes(
    Method.GET / "blog" -> handler { (req: Request) =>
      scalatagsToResponse(BlogPage(repo.getAllPosts()))
    },
    Method.GET / "blog" / string("slug") -> handler {
      (slug: String, req: Request) =>
        repo.getPost(slug) match {
          case Some(post) =>
            scalatagsToResponse(BlogPostPage(post))
          case None =>
            Response.notFound(s"Post '$slug' not found")
        }
    }
  )

  private def scalatagsToResponse(view: Frag): Response =
    val htmlContent = "<!DOCTYPE html>\n" + view.render
    Response
      .text(htmlContent)
      .addHeader(Header.ContentType(MediaType.text.html))

object BlogRoutes:
  val layer = ZLayer.derive[BlogRoutes]
