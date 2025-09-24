package de.keyruu.homepage.logic.markdown

import org.commonmark.renderer.html.AttributeProvider
import org.commonmark.node.Node
import java.{util => ju}
import org.commonmark.node.Link
import org.commonmark.Extension
import org.commonmark.renderer.html.HtmlRenderer.HtmlRendererExtension
import org.commonmark.renderer.html.HtmlRenderer.Builder
import org.commonmark.renderer.html.AttributeProviderFactory
import org.commonmark.renderer.html.AttributeProviderContext

class LinkAttributeProvider extends AttributeProvider:
  def setAttributes(
      node: Node,
      tagName: String,
      attributes: ju.Map[String, String]
  ): Unit =
    if (node.isInstanceOf[Link]) {
      attributes.put("class", "external")
      attributes.put("target", "_blank")
    }

class LinkExtension extends Extension with HtmlRendererExtension:
  def extend(rendererBuilder: Builder): Unit =
    rendererBuilder.attributeProviderFactory(new AttributeProviderFactory {
      override def create(
          context: AttributeProviderContext
      ): AttributeProvider =
        new LinkAttributeProvider
    })
