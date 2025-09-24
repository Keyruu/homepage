package de.keyruu.homepage.ui.components

import scalatags.Text.all._
import de.keyruu.homepage.logic.markdown.TocHeading

object TableOfContents:
  def apply(headings: List[TocHeading]) =
    val toc = buildToc(headings)

    tag("nav")(
      cls := "toc",
      tag("style")("""
        me {
          position: relative;
          padding-left: 0;
        }

        me ul {
          list-style-type: none;
          padding-left: 1.2rem;
          margin-bottom: 0;
        }

        me ul li {
          list-style-type: none;
        }

        me svg.toc-progress {
          pointer-events: none;
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          color: var(--accent);
        }

        me path.toc-marker {
          transition: stroke-dasharray 0.5s ease-in-out;
        }

        me polygon.toc-arrowhead {
          transition:  transform 0.5s ease-in-out;
        }

        me a {
          display: block;
          text-decoration: none;
          color: rgba(var(--gray-light), 0.5);
          padding: 0.25rem 0.5rem;
        }

        me a:hover, me a.active:hover {
          color: var(--accent);
        }

        me a.active {
          color: var(--foreground);
        }
      """),
      ul(
        TableOfContentsHeading(
          TocHeading(
            depth = 1,
            text =
              "↴", // Unicode: U+21B4 - alternatively use HTML entity &#8628;
            slug = "title",
            subheadings = List.empty
          )
        ),
        toc.map(heading => TableOfContentsHeading(heading))
      ),
      raw("""<svg class="toc-progress" xmlns="http://www.w3.org/2000/svg">
        <path
          class="toc-marker"
          fill="none"
          stroke="currentColor"
          stroke-linecap="round"
          stroke-width="3"
          stroke-dashoffset="1"
          stroke-linejoin="round"
          marker-end="url(#arrowhead)"
        ></path>
        <polygon
          class="toc-arrowhead"
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

          // Update the parent in the toc list if it's a top-level heading
          if parent.depth == 1 then
            val index = toc.indexWhere(_.slug == parent.slug)
            if index >= 0 then toc.update(index, updatedParent)
        }
    }

    toc.toList
