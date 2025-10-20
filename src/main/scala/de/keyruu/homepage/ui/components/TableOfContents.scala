package de.keyruu.homepage.ui.components

import scalatags.Text.all._
import scalatags.stylesheet._
import de.keyruu.homepage.logic.markdown.TocHeading

object TocStyles extends StyleSheet {
  initStyleSheet()

  val toc = cls(
    position := "relative",
    paddingLeft := 0
  )

  val list = cls(
    listStyleType := "none",
    paddingLeft := "1.2rem",
    marginBottom := 0
  )

  val listItem = cls(
    listStyleType := "none"
  )

  val link = cls(
    display := "block",
    textDecoration := "none",
    color := "rgba(var(--gray-light), 0.5)",
    padding := "0.25rem 0.5rem",
    &.hover(
      color := "var(--accent)"
    )
  )

  val progress = cls(
    pointerEvents := "none",
    position := "absolute",
    top := 0,
    left := 0,
    width := "100%",
    height := "100%",
    color := "var(--accent)"
  )

  val marker = cls(
    transition := "stroke-dasharray 0.5s ease-in-out"
  )

  val arrowhead = cls(
    transition := "transform 0.5s ease-in-out"
  )
}

object TableOfContents:
  def apply(headings: List[TocHeading]) =
    val toc = buildToc(headings)

    frag(
      tag("style")(
        TocStyles.styleSheetText,
        // Add the active class styles that toc.js expects
        raw(s"""
          nav.toc a.active {
            color: var(--foreground);
          }
          nav.toc a.active:hover {
            color: var(--accent);
          }
        """)
      ),
      tag("nav")(cls := s"toc ${TocStyles.toc.name}")(
        ul(TocStyles.list)(
          TableOfContentsHeading(
            TocHeading(
              depth = 1,
              text = "↴",
              slug = "title",
              subheadings = List.empty
            )
          ),
          toc.map(heading => TableOfContentsHeading(heading))
        ),
        raw(s"""<svg class="${TocStyles.progress.name}" xmlns="http://www.w3.org/2000/svg">
          <path
            class="toc-marker ${TocStyles.marker.name}"
            fill="none"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-width="3"
            stroke-dashoffset="1"
            stroke-linejoin="round"
            marker-end="url(#arrowhead)"
          ></path>
          <polygon
            class="toc-arrowhead ${TocStyles.arrowhead.name}"
            fill="currentColor"
            points="0,0 10,5 0,10"
            style="display: none;"
          ></polygon>
        </svg>""")
      )
    )

  private def buildToc(headings: List[TocHeading]): List[TocHeading] =
    val toc = scala.collection.mutable.ListBuffer[TocHeading]()
    val parentHeadings = scala.collection.mutable.Map[Int, TocHeading]()

    headings.foreach { h =>
      val heading = h.copy(subheadings = List.empty)
      parentHeadings(heading.depth) = heading

      if heading.depth == 1 then toc += heading
      else
        parentHeadings.get(heading.depth - 1).foreach { parent =>
          val updatedParent = parent.copy(
            subheadings = parent.subheadings :+ heading
          )
          parentHeadings(parent.depth) = updatedParent

          if parent.depth == 1 then
            val index = toc.indexWhere(_.slug == parent.slug)
            if index >= 0 then toc.update(index, updatedParent)
        }
    }

    toc.toList
