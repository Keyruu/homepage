package de.keyruu.homepage.logic.markdown

import org.commonmark.node.CustomNode
import org.commonmark.node.BlockQuote
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlWriter
import java.{util => ju}
import org.commonmark.node.Node
import org.commonmark.renderer.html.HtmlNodeRendererContext

import scala.jdk.CollectionConverters._
import org.commonmark.Extension
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.renderer.html.HtmlNodeRendererFactory
import org.commonmark.node.CustomBlock
import org.commonmark.parser.block.AbstractBlockParser
import org.commonmark.parser.block.ParserState
import org.commonmark.parser.block.BlockContinue
import org.commonmark.parser.InlineParser
import org.commonmark.parser.SourceLine
import org.commonmark.parser.block.AbstractBlockParserFactory
import org.commonmark.parser.block.MatchedBlockParser
import org.commonmark.parser.block.BlockStart
import org.commonmark.parser.SourceLines
import org.commonmark.parser.Parser
import org.commonmark.node.Block

class CalloutNode extends CustomBlock:
  var calloutType: String = ""
  var expandable: Boolean = false

// Parser for callout blocks
class CalloutBlockParser extends AbstractBlockParser:
  private var calloutNode: CalloutNode = _
  private val contentLines = new ju.ArrayList[SourceLine]

  def this(calloutType: String, expandable: Boolean) = {
    this()
    calloutNode = new CalloutNode()
    calloutNode.calloutType = calloutType
    calloutNode.expandable = expandable
  }

  override def isContainer: Boolean = true

  override def canContain(block: Block): Boolean = true

  override def getBlock: CalloutNode = calloutNode

  override def tryContinue(parserState: ParserState): BlockContinue =
    val line = parserState.getLine.getContent.toString
    if (line.trim.startsWith(">")) {
      val skipChars = if (line.startsWith("> ")) 2 else 1
      BlockContinue.atIndex(parserState.getIndex + skipChars)
    } else {
      BlockContinue.none()
    }

  override def addLine(line: SourceLine): Unit =
    contentLines.add(line)

  override def parseInlines(
      inlineParser: org.commonmark.parser.InlineParser
  ): Unit =
    if (!contentLines.isEmpty) {
      val sourceLines = SourceLines.of(contentLines)
      inlineParser.parse(sourceLines, calloutNode)
    }

class CalloutParserFactory extends AbstractBlockParserFactory:
  override def tryStart(
      state: ParserState,
      matchedBlockParser: MatchedBlockParser
  ): BlockStart =
    val line = state.getLine.getContent.toString.trim

    if (line.startsWith("> [!")) {
      val calloutPattern = """^> \[!(\w+)\](-?).*$""".r

      line match {
        case calloutPattern(calloutType, expandableMarker) =>
          val expandable = expandableMarker == "-"

          val parser = new CalloutBlockParser(calloutType, expandable)
          BlockStart.of(parser).atIndex(state.getIndex + line.length)
        case _ =>
          BlockStart.none()
      }
    } else {
      BlockStart.none()
    }

class CalloutNodeRenderer(context: HtmlNodeRendererContext)
    extends NodeRenderer:
  private val html: HtmlWriter = context.getWriter

  override def getNodeTypes: ju.Set[Class[? <: Node]] =
    Set[Class[? <: Node]](classOf[CalloutNode]).asJava

  override def render(node: Node): Unit = node match {
    case callout: CalloutNode =>
      val wrapperTag = if (callout.expandable) "details" else "div"

      val attrs = new ju.HashMap[String, String]()
      attrs.put("data-callout", "")
      attrs.put("data-callout-type", callout.calloutType)

      html.tag(wrapperTag, attrs)

      val titleTag = if (callout.expandable) "summary" else "div"
      val titleAttrs = new ju.HashMap[String, String]()
      titleAttrs.put("data-callout-title", "")

      html.tag(titleTag, titleAttrs)
      html.text(callout.calloutType.capitalize)
      html.tag(s"/$titleTag")

      html.tag("div", ju.Map.of("data-callout-body", ""))

      var child = callout.getFirstChild
      while (child != null) {
        context.render(child)
        child = child.getNext
      }

      html.tag("/div")
      html.tag(s"/$wrapperTag")

    case _ => {}
  }

class CalloutExtension
    extends Extension
    with Parser.ParserExtension
    with HtmlRenderer.HtmlRendererExtension:

  override def extend(parserBuilder: Parser.Builder): Unit = {
    parserBuilder.customBlockParserFactory(new CalloutParserFactory())
  }

  override def extend(rendererBuilder: HtmlRenderer.Builder): Unit = {
    rendererBuilder.nodeRendererFactory(new HtmlNodeRendererFactory {
      override def create(context: HtmlNodeRendererContext): NodeRenderer =
        new CalloutNodeRenderer(context)
    })
  }
