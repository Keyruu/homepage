package de.keyruu.homepage.ui.components

import scalatags.Text.all._

object Header:
  def apply(currentPath: String): Frag =
    header(
      cls := "sticky top-0 z-[999] bg-[rgba(10,10,20,0.7)] border-b border-[rgba(90,105,130,0.2)] px-6 shadow-[0_8px_16px_rgba(0,0,0,0.2)]",
      style := "backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px);"
    )(
      tag("nav")(
        cls := "flex items-center justify-between max-w-[1200px] mx-auto h-16 flex-col md:flex-row md:h-16 py-4 md:py-0"
      )(
        a(
          cls := "inline-block no-underline",
          href := "/",
          img(
            src := "/public/images/keyruu_logo.png",
            alt := "Keyruu",
            height := "60px"
          )
        ),
        div(
          cls := "flex gap-4 flex-wrap md:gap-4"
        )(
          HeaderLink("/", "home", currentPath),
          HeaderLink("/blog", "blog", currentPath),
          HeaderLink("/about", "about", currentPath),
          HeaderLink("/projects", "projects", currentPath),
          HeaderLink("/contact", "contact", currentPath)
        )
      )
    )
