package de.keyruu.homepage.ui

import scalatags.generic.Style

// Create a custom stylesheet that extends the base one with additional CSS properties
object CustomStyles:
  val gap = new Style("gap", "gap")
  val backdropFilter = new Style("backdrop-filter", "backdropFilter")
  val transform = new Style("transform", "transform")
  val content = new Style("content", "content")
  val gridTemplateColumns =
    new Style("grid-template-columns", "gridTemplateColumns")
  val gridGap = new Style("grid-gap", "gridGap")
  val flexWrap = new Style("flex-wrap", "flexWrap")
  val userSelect = new Style("user-select", "userSelect")
  val webkitUserSelect = new Style("-webkit-user-select", "webkitUserSelect")
  val mozUserSelect = new Style("-moz-user-select", "mozUserSelect")
  val msUserSelect = new Style("-ms-user-select", "msUserSelect")
  val clipPath = new Style("clip-path", "clipPath")
  val webkitClipPath = new Style("-webkit-clip-path", "webkitClipPath")
  val scrollbarWidth = new Style("scrollbar-width", "scrollbarWidth")
  val scrollbarColor = new Style("scrollbar-color", "scrollbarColor")
