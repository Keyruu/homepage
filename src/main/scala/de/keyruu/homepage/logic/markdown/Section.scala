package de.keyruu.homepage.logic.markdown

import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlWriter
import org.commonmark.node.Node
import java.{util => ju}
import org.commonmark.node.Heading
import org.commonmark.node.Document
import scala.jdk.CollectionConverters._
import org.commonmark.renderer.text.TextContentRenderer
import de.keyruu.homepage.logic.markdown.HeadingUtil.normalize
import org.commonmark.Extension
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.renderer.html.HtmlNodeRendererFactory
import de.keyruu.homepage.logic.markdown.HeadingUtil.extractText

class SectionRenderer(context: HtmlNodeRendererContext) extends NodeRenderer:
  private val html: HtmlWriter = context.getWriter
  private var inSection = false
  private var currentSectionLevel = 0
  private val renderer = TextContentRenderer.builder().build()

  override def getNodeTypes: ju.Set[Class[? <: Node]] =
    Set[Class[? <: Node]](classOf[Heading], classOf[Document]).asJava

  override def render(node: Node): Unit = node match {
    case heading: Heading =>
      val level = heading.getLevel

      if (inSection) {
        html.tag("/section")
        html.line
      }

      html.line
      val text = renderer.render(heading)
      val id = normalize(extractText(heading))
      html.tag("section")
      html.line
      inSection = true
      currentSectionLevel = level

      html.tag(s"h$level", Map("id" -> id).asJava)
      html.tag("a", Map("href" -> s"#$id", "class" -> "heading").asJava)
      html.text("#")
      html.tag("/a")
      html.text(" ")
      renderChildren(heading)
      html.tag(s"/h$level")
      html.line

    case doc: Document =>
      renderChildren(node)
      if (inSection) {
        html.tag("/section")
        html.line
      }

    case _ => // should not happen
  }

  private def renderChildren(parent: Node): Unit =
    var node = parent.getFirstChild
    while (node != null) {
      val next = node.getNext
      context.render(node)
      node = next
    }

class SectionizerExtension
    extends Extension
    with HtmlRenderer.HtmlRendererExtension:

  override def extend(rendererBuilder: HtmlRenderer.Builder): Unit = {
    rendererBuilder.nodeRendererFactory(new HtmlNodeRendererFactory {
      override def create(context: HtmlNodeRendererContext): NodeRenderer =
        new SectionRenderer(context)
    })
  }
