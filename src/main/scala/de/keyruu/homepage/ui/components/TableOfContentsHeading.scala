package de.keyruu.homepage.ui.components

import scalatags.Text.all._
import de.keyruu.homepage.logic.markdown.TocHeading

object TableOfContentsHeading:
  def apply(heading: TocHeading): Frag =
    li(
      a(
        href := s"#${heading.slug}",
        heading.text
      ),
      if heading.subheadings.nonEmpty then
        ul(
          heading.subheadings.map(subheading =>
            TableOfContentsHeading(subheading)
          )
        )
      else frag(),
      tag("style")("""
        me ul {
          list-style-type: none;
          padding-left: 1.2rem;
        }

        me > a {
          display: block;
          text-decoration: none;
          color: rgba(var(--gray-light), 0.5);
          padding: 0.25rem 0.5rem;
        }

        me > a:hover, me > a.active:hover {
          color: var(--accent);
        }

        me > a.active {
          color: var(--foreground);
        }
      """)
    )
