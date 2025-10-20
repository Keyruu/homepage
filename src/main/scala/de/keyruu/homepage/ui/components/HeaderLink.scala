package de.keyruu.homepage.ui.components

import scalatags.Text.all._

object HeaderLink:
  def apply(linkHref: String, linkText: String, currentPath: String): Frag =
    val isActive = isLinkActive(linkHref, currentPath)

    a(
      HeaderStyles.link,
      if isActive then HeaderStyles.linkActive else (),
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
