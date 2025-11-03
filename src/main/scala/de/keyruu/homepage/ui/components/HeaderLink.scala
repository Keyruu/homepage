package de.keyruu.homepage.ui.components

import scalatags.Text.all._

object HeaderLink:
  def apply(linkHref: String, linkText: String, currentPath: String): Frag =
    val isActive = isLinkActive(linkHref, currentPath)
    val baseClasses = "header-nav-link py-2 px-1 font-medium relative no-underline transition-colors duration-300"
    val activeClasses = if isActive then " active" else ""

    a(
      cls := baseClasses + activeClasses,
      href := linkHref,
      linkText
    )

  private def isLinkActive(linkHref: String, currentPath: String): Boolean =
    val normalizedPath =
      if currentPath == "/" then "/" else currentPath.stripSuffix("/")
    val normalizedHref =
      if linkHref == "/" then "/" else linkHref.stripSuffix("/")

    if normalizedHref == "/" then normalizedPath == "/"
    else
      normalizedPath == normalizedHref || normalizedPath.startsWith(
        normalizedHref + "/"
      )
