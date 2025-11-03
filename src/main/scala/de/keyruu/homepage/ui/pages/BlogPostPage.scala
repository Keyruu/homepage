package de.keyruu.homepage.ui.layouts

import scalatags.Text.all._
import de.keyruu.homepage.data.blog.BlogPost
import de.keyruu.homepage.ui.components.{
  TableOfContents,
  FormattedDate,
  Header,
  Footer
}
import de.keyruu.homepage.ui.Head
import de.keyruu.homepage.logic.markdown.TocHeading

object BlogPostPage:
  def apply(post: BlogPost): Frag =
    html(lang := "en")(
      head(
        Head(post.title, post.description, s"/blog/${post.slug}"),
        // Blog CSS (Prism themes)
        link(rel := "stylesheet", href := "/public/dist/blog.css"),
        // Blog bundle includes Prism.js and TOC
        script(src := "/public/dist/blog.js", `type` := "module")
      ),
      body(
        cls := ("line-numbers"),
        attr("data-prismjs-copy") := "Copy",
        Header(s"/blog/${post.slug}"),
        tag("main")(
          cls := "flex flex-col items-center max-w-full m-0 mx-auto lg:w-[calc(100%-2em)] w-full"
        )(
          div(
            cls := "fixed top-4 ml-[60rem] mt-[10rem] w-[250px] max-h-[90vh] overflow-y-auto p-4 rounded-lg hidden xl:block"
          )(
            TableOfContents(post.headings)
          ),
          tag("article")(
            cls := "prose w-[720px] lg:max-w-[calc(100%-2em)] max-w-full mx-auto p-4 text-gray-light relative"
          )(
            tag("section")(
              div(cls := "mb-4 py-4 text-center leading-none")(
                div(cls := "mb-2 text-gray")(
                  FormattedDate(post.pubDate),
                  raw(" • "),
                  readingTime(post.wordCount)
                ),
                h1(id := "title", cls := "m-0 mb-2")(post.title),
                hr()
              )
            ),
            raw(post.html)
          )
        ),
        Footer()
      )
    )

  def readingTime(wordCount: Int): String =
    val wordsPerMinute = 150.0
    val minutes = Math.ceil(wordCount.toDouble / wordsPerMinute).toInt
    s"$minutes min read"
