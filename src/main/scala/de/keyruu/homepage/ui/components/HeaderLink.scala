package de.keyruu.homepage.ui.components

import scalatags.Text.all._

object HeaderLink:
  def apply(linkHref: String, linkText: String, currentPath: String): Frag =
    val isActive = isLinkActive(linkHref, currentPath)

    a(
      href := linkHref,
      cls := (if isActive then "active" else ""),
      linkText,
      tag("style")("""
        me {
          display: inline-block;
          text-decoration: none;
        }
        me.active {
          font-weight: bolder;
          text-decoration: underline;
        }
      """)
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
