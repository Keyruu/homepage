package de.keyruu.homepage.ui.layouts

import scalatags.Text.all._
import de.keyruu.homepage.data.blog.BlogPost
import de.keyruu.homepage.ui.components.{FormattedDate, Header, Footer}
import de.keyruu.homepage.ui.Head
import java.time.LocalDate

object BlogPage:
  def apply(posts: List[BlogPost]): Frag =
    html(lang := "en")(
      head(
        Head("Blog", "Blog posts about tech and development", "/blog")
      ),
      body(
        Header("/blog"),
        tag("main")(
          cls := "w-[720px] mx-auto mt-8",
          tag("section")(
            ul(
              cls := "flex flex-col list-none m-0 p-0 gap-2 md:gap-4",
              posts
                .groupBy(_.pubDate.getYear)
                .toList
                .sortBy(-_._1) // Sort by year descending
                .map { case (year, yearPosts) =>
                  frag(
                    h5(
                      cls := "font-bold mt-8 first:mt-0 text-gray font-[Zodiak]",
                      year.toString
                    ),
                    yearPosts
                      .sortBy(_.pubDate)(using Ordering[LocalDate].reverse)
                      .map { post =>
                        li(
                          cls := "w-full text-center md:text-left",
                          a(href := s"/blog/${post.slug}/")(
                            h4(
                              cls := "m-0 leading-none text-gray-light hover:underline text-[1.563em] md:text-[1.5em] font-[Zodiak] font-bold",
                              post.title
                            )
                          ),
                          p(
                            cls := "m-0 text-gray",
                            FormattedDate(post.pubDate),
                            raw(" • "),
                            post.tags.map { tagName =>
                              a(href := s"/blog/tag/$tagName")(
                                s"#$tagName "
                              )
                            }
                          )
                        )
                      }
                  )
                }
            )
          )
        ),
        Footer()
      )
    )
