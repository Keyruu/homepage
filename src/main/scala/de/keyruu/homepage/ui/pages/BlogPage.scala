package de.keyruu.homepage.ui.layouts

import scalatags.Text.all._
import scalatags.stylesheet._
import de.keyruu.homepage.data.blog.BlogPost
import de.keyruu.homepage.ui.components.{FormattedDate, Header, Footer}
import de.keyruu.homepage.ui.Head
import de.keyruu.homepage.ui.CustomStyles._
import scalatags.stylesheet.StyleSheetFrag

object BlogPageStyles extends StyleSheet:
  initStyleSheet()

  val main = cls(
    width := "720px"
  )

  val postList = cls(
    display := "flex",
    flexDirection := "column",
    listStyleType := "none",
    margin := 0,
    padding := 0,
    gap := "2rem"
  )

  val title = cls(
    margin := 0,
    lineHeight := 1,
    &.hover(
      color := "var(--highlight)",
      textDecoration := "underline"
    )
  )

  val date = cls(
    margin := 0,
    color := "rgb(var(--gray))"
  )

object BlogPage:
  def apply(posts: List[BlogPost]): Frag =
    html(lang := "en")(
      head(
        Head("Blog", "Blog posts about tech and development", "/blog"),
        tag("style")(
          BlogPageStyles.styleSheetText,
          raw(s"""
            @media (max-width: 720px) {
              .${BlogPageStyles.postList.name} {
                gap: 0.5em;
              }
              .${BlogPageStyles.postList.name} li {
                width: 100%;
                text-align: center;
              }
              .${BlogPageStyles.postList.name} li:first-child {
                margin-bottom: 0;
              }
              .${BlogPageStyles.postList.name} li:first-child .${BlogPageStyles.title.name} {
                font-size: 1.563em;
              }
            }
          """)
        )
      ),
      body(
        Header("/blog"),
        tag("main")(
          cls := "w-[720px]",
          tag("section")(
            ul(
              cls := "flex flex-col list-none m-0 p-0 gap-8",
              posts.map { post =>
                li(
                  a(href := s"/blog/${post.slug}/")(
                    h4(
                      cls := "m-0 leading-none hover:underline hover:text-highlight",
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
          )
        ),
        Footer()
      )
    )
