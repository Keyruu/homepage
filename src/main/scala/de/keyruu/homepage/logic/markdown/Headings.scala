package de.keyruu.homepage.logic.markdown

import org.commonmark.node.Heading
import org.commonmark.node.AbstractVisitor
import scala.collection.mutable.ListBuffer
import org.commonmark.renderer.text.TextContentRenderer
import zio.ZIO
import de.keyruu.homepage.logic.markdown.HeadingUtil.normalize

case class TocHeading(
    depth: Int,
    text: String,
    slug: String,
    subheadings: List[TocHeading] = List.empty
)

class HeadingVisitor extends AbstractVisitor:
  val headingsBuffer: ListBuffer[TocHeading] = ListBuffer.empty;
  private val renderer = TextContentRenderer.builder().build()

  override def visit(heading: Heading): Unit =
    val text = renderer.render(heading).trim()
    val slug = normalize(text)

    headingsBuffer.addOne(
      TocHeading(
        depth = heading.getLevel(),
        slug = slug,
        text = text
      )
    )

object HeadingUtil:
  def normalize(text: String) =
    text.toLowerCase
      .replaceAll("[^a-zA-Z0-9\\s-]+", "")
      .replaceAll("\\s+", "-")
