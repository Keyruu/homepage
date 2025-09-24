package de.keyruu.homepage.logic.markdown

import org.commonmark.node.{AbstractVisitor, Text, Code, HtmlInline}

class WordCountVisitor extends AbstractVisitor:
  var wordCount = 0

  override def visit(text: Text): Unit = {
    val words = text.getLiteral
      .split("\\s+") // Split on whitespace only
      .map(_.trim)
      .filter(_.nonEmpty)
      .filter(
        _.exists(Character.isLetterOrDigit)
      )

    wordCount += words.length
    visitChildren(text)
  }
