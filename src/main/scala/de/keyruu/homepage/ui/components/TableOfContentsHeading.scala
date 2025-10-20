package de.keyruu.homepage.ui.components

import scalatags.Text.all._
import de.keyruu.homepage.logic.markdown.TocHeading

object TableOfContentsHeading:
  def apply(heading: TocHeading): Frag =
    li(TocStyles.listItem)(
      a(TocStyles.link)(
        href := s"#${heading.slug}",
        heading.text
      ),
      if heading.subheadings.nonEmpty then
        ul(TocStyles.list)(
          heading.subheadings.map(subheading =>
            TableOfContentsHeading(subheading)
          )
        )
      else frag()
    )
