package de.keyruu.homepage.ui.components

import scalatags.Text.all._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object FormattedDate:
  def apply(date: LocalDate): Frag =
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)
    val formattedDate = date.format(formatter)

    tag("time")(
      attr("datetime") := date.toString,
      formattedDate
    )
