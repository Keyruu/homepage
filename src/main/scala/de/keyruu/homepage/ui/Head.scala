package de.keyruu.homepage.ui

import scalatags.Text.all._

object Head:
  def apply(
      title: String,
      description: String,
      currentUrl: String, // Pass the current URL from the request
      siteUrl: String = "https://keyruu.de", // Your site base URL
      image: String = "/blog-placeholder-1.jpg"
  ): Frag =
    val canonicalUrl = s"$siteUrl$currentUrl"
    val imageUrl = if image.startsWith("http") then image else s"$siteUrl$image"
    val rssUrl = s"$siteUrl/rss.xml"

    frag(
      meta(charset := "utf-8"),
      meta(name := "viewport", content := "width=device-width,initial-scale=1"),
      link(
        rel := "icon",
        `type` := "image/svg+xml",
        href := "/public/favicon.svg"
      ),
      link(rel := "sitemap", href := "/sitemap-index.xml"),
      link(
        rel := "alternate",
        `type` := "application/rss+xml",
        attr("title") := "Keyruu",
        href := rssUrl
      ),
      meta(name := "generator", content := "Keyruu"),

      // Canonical URL
      link(rel := "canonical", href := canonicalUrl),

      // Primary Meta Tags
      tag("title")(title),
      meta(name := "title", content := title),
      meta(name := "description", content := description),

      // Open Graph / Facebook
      meta(attr("property") := "og:type", content := "website"),
      meta(attr("property") := "og:url", content := canonicalUrl),
      meta(attr("property") := "og:title", content := title),
      meta(attr("property") := "og:description", content := description),
      meta(attr("property") := "og:image", content := imageUrl),

      // Twitter
      meta(
        attr("property") := "twitter:card",
        content := "summary_large_image"
      ),
      meta(attr("property") := "twitter:url", content := canonicalUrl),
      meta(attr("property") := "twitter:title", content := title),
      meta(attr("property") := "twitter:description", content := description),
      meta(attr("property") := "twitter:image", content := imageUrl),

      // CSS
      link(rel := "stylesheet", href := "/public/styles/global.css"),

      // Main JS bundle (Alpine.js for all pages)
      script(src := "/public/dist/main.js", `type` := "module")
    )
