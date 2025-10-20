package de.keyruu.homepage.ui.layouts

import scalatags.Text.all._
import scalatags.stylesheet._
import de.keyruu.homepage.data.blog.BlogPost
import de.keyruu.homepage.ui.components.{
  TableOfContents,
  FormattedDate,
  Header,
  Footer
}
import de.keyruu.homepage.ui.Head
import de.keyruu.homepage.logic.markdown.TocHeading

object BlogPostStyles extends StyleSheet {
  initStyleSheet()

  val main = cls(
    display := "flex",
    flexDirection := "column",
    alignItems := "center",
    maxWidth := "100%",
    margin := "0 auto",
    width := "calc(100% - 2em)"
  )

  val prose = cls(
    width := "720px",
    maxWidth := "calc(100% - 2em)",
    margin := "auto",
    padding := "1em",
    color := "rgb(var(--gray-light))",
    position := "relative"
  )

  val proseList = cls(
    paddingLeft := "1.5em",
    marginBottom := "1em"
  )

  val proseListItem = cls(
    listStyleType := "disc"
  )

  val proseListItemCircle = cls(
    listStyleType := "circle"
  )

  val proseListItemSquare = cls(
    listStyleType := "square"
  )

  val tocContainer = cls(
    position := "fixed",
    top := "1rem",
    marginLeft := "60rem",
    marginTop := "10rem",
    width := "250px",
    maxHeight := "90vh",
    overflowY := "auto",
    padding := "1rem",
    borderRadius := "8px",
    boxShadow := "var(--box-shadow)"
  )

  val title = cls(
    marginBottom := "1em",
    padding := "1em 0",
    textAlign := "center",
    lineHeight := 1
  )

  val titleHeading = cls(
    margin := "0 0 0.5em 0"
  )

  val date = cls(
    marginBottom := "0.5em",
    color := "rgb(var(--gray))"
  )

  val heading = cls(
    color := "rgb(var(--gray))"
  )

  val headingHover = cls(
    color := "rgb(var(--light-gray))"
  )
}

object BlogPostPage:
  def apply(post: BlogPost): Frag =
    html(lang := "en")(
      head(
        Head(post.title, post.description, s"/blog/${post.slug}"),
        link(
          rel := "stylesheet",
          href := "/public/styles/prism.css"
        ),
        link(
          rel := "stylesheet",
          href := "/public/css/vendor/prism-line-numbers.min.css"
        ),
        link(
          rel := "stylesheet",
          href := "/public/css/vendor/prism-toolbar.min.css"
        ),
        script(src := "/public/js/vendor/prism.min.js"),
        script(src := "/public/js/vendor/prism-autoloader.min.js"),
        script(src := "/public/js/vendor/prism-line-numbers.min.js"),
        script(src := "/public/js/vendor/prism-toolbar.min.js"),
        script(src := "/public/js/vendor/prism-copy-to-clipboard.min.js"),
        script(src := "/public/js/vendor/css-scope-inline.min.js"),
        tag("style")(
          BlogPostStyles.styleSheetText,
          // Add hover and nested list styles
          raw(s"""
            .${BlogPostStyles.heading.name}:hover {
              color: rgb(var(--light-gray));
            }
            .${BlogPostStyles.prose.name} ul,
            .${BlogPostStyles.prose.name} ol {
              padding-left: 1.5em;
              margin-bottom: 1em;
            }
            .${BlogPostStyles.prose.name} ul li {
              list-style-type: disc;
            }
            .${BlogPostStyles.prose.name} ul li::marker {
              color: rgb(var(--gray-light));
            }
            .${BlogPostStyles.prose.name} ul ul li {
              list-style-type: circle;
            }
            .${BlogPostStyles.prose.name} ul ul ul li {
              list-style-type: square;
            }
          """)
        )
      ),
      body(
        cls := ("line-numbers"),
        attr("data-prismjs-copy") := "Copy",
        Header(s"/blog/${post.slug}"),
        tag("main")(BlogPostStyles.main)(
          div(BlogPostStyles.tocContainer)(
            TableOfContents(post.headings)
          ),
          tag("article")(BlogPostStyles.prose)(
            tag("section")(
              div(BlogPostStyles.title)(
                div(BlogPostStyles.date)(
                  FormattedDate(post.pubDate),
                  raw(" • "),
                  readingTime(post.wordCount)
                ),
                h1(id := "title", BlogPostStyles.titleHeading)(post.title),
                hr()
              )
            ),
            raw(post.html)
          )
        ),
        Footer(),
        script(src := "/public/js/toc.js")
      )
    )

  def readingTime(wordCount: Int): String =
    val wordsPerMinute = 150.0
    val minutes = Math.ceil(wordCount.toDouble / wordsPerMinute).toInt
    s"$minutes min read"
