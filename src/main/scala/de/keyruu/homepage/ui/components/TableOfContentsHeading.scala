package de.keyruu.homepage.ui.components

import scalatags.Text.all._
import de.keyruu.homepage.logic.markdown.TocHeading

object TableOfContentsHeading:
  def apply(heading: TocHeading): Frag =
    li(cls := "list-none")(
      a(
        cls := "block no-underline py-1 px-2",
        href := s"#${heading.slug}",
        heading.text
      ),
      if heading.subheadings.nonEmpty then
        ul(cls := "list-none pl-5 mb-0")(
          heading.subheadings.map(subheading =>
            TableOfContentsHeading(subheading)
          )
        )
      else frag()
    )
