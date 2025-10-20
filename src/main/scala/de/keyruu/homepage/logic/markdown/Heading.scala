package de.keyruu.homepage.logic.markdown

import org.commonmark.node.Heading
import org.commonmark.node.AbstractVisitor
import scala.collection.mutable.ListBuffer
import org.commonmark.renderer.text.TextContentRenderer
import zio.ZIO
import de.keyruu.homepage.logic.markdown.HeadingUtil.normalize
import org.commonmark.node.{Node, Text, Link, Code}
import de.keyruu.homepage.logic.markdown.HeadingUtil.extractText

case class TocHeading(
    depth: Int,
    text: String,
    slug: String,
    subheadings: List[TocHeading] = List.empty
)

class HeadingVisitor extends AbstractVisitor:
  val headingsBuffer: ListBuffer[TocHeading] = ListBuffer.empty;

  override def visit(heading: Heading): Unit =
    val text = extractText(heading).trim()
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

  def extractText(node: Node): String = {
    val sb = new StringBuilder

    var child = node.getFirstChild
    while (child != null) {
      child match {
        case text: Text =>
          sb.append(text.getLiteral)
        case link: Link =>
          // For links, only get the link text, not the URL
          sb.append(extractText(link))
        case code: Code =>
          sb.append(code.getLiteral)
        case _ =>
          // Recursively extract text from other nodes
          sb.append(extractText(child))
      }
      child = child.getNext
    }

    sb.toString
  }
