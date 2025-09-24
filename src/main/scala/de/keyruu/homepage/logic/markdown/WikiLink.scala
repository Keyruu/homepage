package de.keyruu.homepage.logic.markdown

import java.util as ju

import scala.jdk.CollectionConverters.*

import org.commonmark.Extension
import org.commonmark.node.CustomNode
import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.parser.InlineParser
import org.commonmark.parser.Parser
import org.commonmark.parser.SourceLines
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.renderer.html.HtmlWriter
import org.commonmark.renderer.html.HtmlNodeRendererFactory
import org.commonmark.parser.beta.InlineContentParserFactory
import org.commonmark.parser.beta.Position
import org.commonmark.parser.beta.InlineContentParser
import org.commonmark.parser.beta.InlineParserState
import org.commonmark.parser.beta.ParsedInline
import org.commonmark.parser.PostProcessor
import scala.util.matching.Regex

class WikiLink extends CustomNode:
  var target = ""
  var text: Option[String] = None

class WikiLinkPostProcessor extends PostProcessor:
  private val wikiLinkPattern: Regex = """\[\[([^\]]+?)(?:\|([^\]]+?))?\]\]""".r

  override def process(node: Node): Node = {
    processNode(node)
    node
  }

  private def processNode(node: Node): Unit = {
    node match {
      case text: Text =>
        val literal = text.getLiteral
        val matches = wikiLinkPattern.findAllMatchIn(literal).toList

        if (matches.nonEmpty) {
          var lastEnd = 0
          val parent = text.getParent
          var insertAfter: Node = text

          matches.foreach { m =>
            val start = m.start
            val end = m.end
            val target = m.group(1).trim
            val displayText = Option(m.group(2)).map(_.trim)

            // Add text before the match if any
            if (start > lastEnd) {
              val beforeText = literal.substring(lastEnd, start)
              val beforeNode = new Text(beforeText)
              insertAfter.insertAfter(beforeNode)
              insertAfter = beforeNode
            }

            // Create and insert WikiLink node
            val wikiLink = new WikiLink()
            wikiLink.target = target
            wikiLink.text = displayText
            insertAfter.insertAfter(wikiLink)
            insertAfter = wikiLink

            lastEnd = end
          }

          // Add remaining text after last match if any
          if (lastEnd < literal.length) {
            val afterText = literal.substring(lastEnd)
            val afterNode = new Text(afterText)
            insertAfter.insertAfter(afterNode)
          }

          // Remove the original text node
          text.unlink()
        }

      case _ =>
        // Process children
        var child = node.getFirstChild
        while (child != null) {
          val next =
            child.getNext // Store next before processing (in case child is replaced)
          processNode(child)
          child = next
        }
    }
  }

class WikiLinkRenderer(context: HtmlNodeRendererContext) extends NodeRenderer:
  private val html: HtmlWriter = context.getWriter()

  override def getNodeTypes(): ju.Set[Class[? <: Node]] = {
    Set[Class[? <: Node]](classOf[WikiLink]).asJava
  }

  override def render(node: Node): Unit = node match
    case wikiLink: WikiLink =>
      val href = s"/blog/${wikiLink.target.replace(" ", "-").toLowerCase}"

      html.tag(
        "a",
        Map(
          "href" -> href,
          "class" -> "internal"
        ).asJava
      )

      html.text(wikiLink.text.getOrElse(wikiLink.target))
      html.tag("/a")

    case _ => {}

class WikiLinkExtension
    extends Extension
    with Parser.ParserExtension
    with HtmlRenderer.HtmlRendererExtension:

  override def extend(parserBuilder: Parser.Builder): Unit = {
    parserBuilder.postProcessor(
      new WikiLinkPostProcessor()
    )
  }

  override def extend(rendererBuilder: HtmlRenderer.Builder): Unit = {
    rendererBuilder.nodeRendererFactory(new HtmlNodeRendererFactory {
      override def create(context: HtmlNodeRendererContext): NodeRenderer =
        new WikiLinkRenderer(context)
    })
  }
