package de.keyruu.homepage.ui.components

import scalatags.Text.all._
import scalatags.stylesheet._

object HeaderStyles extends StyleSheet {
  initStyleSheet()

  val header = cls(
    position := "sticky",
    top := 0,
    zIndex := 999,
    background := "rgba(10, 10, 20, 0.7)",
    borderBottom := "1px solid rgba(var(--gray), 0.2)",
    padding := "0 1.5rem",
    boxShadow := "0 8px 16px rgba(0, 0, 0, 0.2)"
  )

  val nav = cls(
    display := "flex",
    alignItems := "center",
    justifyContent := "space-between",
    maxWidth := "1200px",
    margin := "0 auto",
    height := "64px"
  )

  val logo = cls(
    display := "inline-block",
    textDecoration := "none"
  )

  val internalLinks = cls(
    display := "flex"
  )

  val link = cls(
    padding := "0.5rem 0.25rem",
    fontWeight := 500,
    color := "rgba(220, 230, 245, 0.85)",
    position := "relative",
    textDecoration := "none",
    transition := "color 0.3s ease"
  )

  val linkUnderline = cls(
    position := "absolute",
    left := 0,
    bottom := "0px",
    width := "100%",
    height := "2px",
    backgroundColor := "var(--accent)",
    transition := "transform 0.4s ease, transform-origin 0s"
  )

  val linkHover = cls(
    color := "var(--accent)"
  )

  val linkActive = cls(
    fontWeight := "bolder",
    color := "var(--accent)"
  )

  // Media query styles
  val navMobile = cls(
    flexDirection := "column",
    alignItems := "flex-start",
    height := "auto",
    padding := "1rem 0"
  )

  val internalLinksMobile = cls(
    flexWrap := "wrap"
  )
}

object Header:
  def apply(currentPath: String): Frag =
    val styleSheet = tag("style")(
      HeaderStyles.styleSheetText,
      // Add pseudo-element and hover styles that can't be done with non-cascading
      raw(s"""
        .${HeaderStyles.header.name} {
          backdrop-filter: blur(12px);
          -webkit-backdrop-filter: blur(12px);
        }
        .${HeaderStyles.internalLinks.name} {
          gap: 1rem;
        }
        .${HeaderStyles.link.name}::after {
          content: '';
          position: absolute;
          left: 0;
          bottom: 0px;
          width: 100%;
          height: 2px;
          background-color: var(--accent);
          transform: scaleX(0);
          transform-origin: right;
          transition: transform 0.4s ease, transform-origin 0s;
        }
        .${HeaderStyles.link.name}:hover::after,
        .${HeaderStyles.link.name}.${HeaderStyles.linkActive.name}::after,
        me .internal-links a.active::after {
          transform: scaleX(1);
          transform-origin: left;
        }
        @media (max-width: 720px) {
          .${HeaderStyles.nav.name} {
            flex-direction: column;
            align-items: flex-start;
            height: auto;
            padding: 1rem 0;
          }
          .${HeaderStyles.internalLinks.name} {
            flex-wrap: wrap;
            gap: 0.75rem 1rem;
          }
        }
      """)
    )

    frag(
      styleSheet,
      header(HeaderStyles.header)(
        tag("nav")(HeaderStyles.nav)(
          a(
            HeaderStyles.logo,
            href := "/",
            img(
              src := "/public/images/keyruu_logo.png",
              alt := "Keyruu",
              height := "60px"
            )
          ),
          div(HeaderStyles.internalLinks)(
            HeaderLink("/", "home", currentPath),
            HeaderLink("/blog", "blog", currentPath),
            HeaderLink("/about", "about", currentPath),
            HeaderLink("/projects", "projects", currentPath),
            HeaderLink("/contact", "contact", currentPath)
          )
        )
      )
    )
