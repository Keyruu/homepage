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

object BlogLayout:
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
          href := "https://cdnjs.cloudflare.com/ajax/libs/prism/1.30.0/plugins/line-numbers/prism-line-numbers.min.css"
        ),
        link(
          rel := "stylesheet",
          href := "https://cdnjs.cloudflare.com/ajax/libs/prism/1.30.0/plugins/toolbar/prism-toolbar.min.css"
        ),
        script(
          src := "https://cdnjs.cloudflare.com/ajax/libs/prism/1.30.0/prism.min.js"
        ),
        script(
          src := "https://cdnjs.cloudflare.com/ajax/libs/prism/1.30.0/plugins/autoloader/prism-autoloader.min.js"
        ),
        script(
          src := "https://cdnjs.cloudflare.com/ajax/libs/prism/1.30.0/plugins/line-numbers/prism-line-numbers.min.js"
        ),
        script(
          src := "https://cdnjs.cloudflare.com/ajax/libs/prism/1.30.0/plugins/toolbar/prism-toolbar.min.js"
        ),
        script(
          src := "https://cdnjs.cloudflare.com/ajax/libs/prism/1.30.0/plugins/copy-to-clipboard/prism-copy-to-clipboard.min.js"
        ),
        script(
          src := "https://cdn.jsdelivr.net/gh/gnat/css-scope-inline@main/script.js"
        ),
        tag("style")("""
          main {
            display: flex;
            flex-direction: column;
            align-items: center;
            max-width: 100%;
            margin: 0 auto;
            width: calc(100% - 2em);
          }
          .prose {
            width: 720px;
            max-width: calc(100% - 2em);
            margin: auto;
            padding: 1em;
            color: rgb(var(--gray-light));
            position: relative;
          }
          .toc-container {
            position: fixed;
            top: 1rem;
            margin-left: 60rem;
            margin-top: 10rem;
            width: 250px;
            max-height: 90vh;
            overflow-y: auto;
            padding: 1rem;
            border-radius: 8px;
            box-shadow: var(--box-shadow);
          }
          .title {
            margin-bottom: 1em;
            padding: 1em 0;
            text-align: center;
            line-height: 1;
          }
          .title h1 {
            margin: 0 0 0.5em 0;
          }
          .date {
            margin-bottom: 0.5em;
            color: rgb(var(--gray));
          }
          .last-updated-on {
            font-style: italic;
          }
          me nav.toc {
            position: relative;
            padding-left: 0;
          }
          a.heading {
            color: rgb(var(--gray));
          }
          a.heading:hover {
            color: rgb(var(--light-gray));
          }
          ul,
          ol {
              padding-left: 1.5em;
              margin-bottom: 1em;
          }

          ul li {
              list-style-type: disc;
          }

          /* Make bullets accent colored */
          ul li::marker {
              color: rgb(var(--gray-light)); /* your accent color */
          }

          /* Nested lists */
          ul ul li {
              list-style-type: circle;
          }

          ul ul ul li {
              list-style-type: square;
          }
        """)
      ),
      body(
        cls := ("line-numbers"),
        attr("data-prismjs-copy") := "Copy",
        Header(s"/blog/${post.slug}"),
        tag("main")(
          div(cls := "toc-container")(
            TableOfContents(post.headings)
          ),
          tag("article")(cls := "prose")(
            tag("section")(
              div(cls := "title")(
                div(cls := "date")(
                  FormattedDate(post.pubDate),
                  raw(" • "),
                  readingTime(post.wordCount)
                ),
                h1(id := "title")(post.title),
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
