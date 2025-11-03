package de.keyruu.homepage.ui.components

import scalatags.Text.all._
import de.keyruu.homepage.logic.markdown.TocHeading

object TableOfContents:
  def apply(headings: List[TocHeading]) =
    val toc = buildToc(headings)

    tag("nav")(cls := "toc relative pl-0")(
      ul(cls := "list-none pl-5 mb-0")(
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
      raw(s"""<svg class="pointer-events-none absolute top-0 left-0 w-full h-full text-accent" xmlns="http://www.w3.org/2000/svg">
        <path
          class="toc-marker transition-[stroke-dasharray] duration-500 ease-in-out"
          fill="none"
          stroke="currentColor"
          stroke-linecap="round"
          stroke-width="3"
          stroke-dashoffset="1"
          stroke-linejoin="round"
          marker-end="url(#arrowhead)"
        ></path>
        <polygon
          class="toc-arrowhead transition-transform duration-500 ease-in-out"
          fill="currentColor"
          points="0,0 10,5 0,10"
          style="display: none;"
        ></polygon>
      </svg>""")
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
