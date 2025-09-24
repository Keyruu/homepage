package de.keyruu.homepage.data.blog

import java.time.LocalDate
import zio.prelude.*
import scala.jdk.CollectionConverters.*
import ContentValidation.*
import de.keyruu.homepage.logic.markdown.TocHeading

final case class BlogPost(
    title: String,
    description: String,
    pubDate: LocalDate,
    tags: List[String] = List.empty,
    aliases: List[String] = List.empty,
    draft: Boolean = false,
    slug: String,
    markdown: String = "",
    html: String,
    headings: List[TocHeading],
    wordCount: Int
)

object BlogPost:
  def validate(
      frontmatter: java.util.Map[String, java.util.List[String]],
      markdown: String,
      html: String,
      headings: List[TocHeading],
      wordCount: Int
  ): Validation[String, BlogPost] =
    val data = frontmatter.asScala.view.mapValues(_.asScala.toList).toMap

    def firstValue(key: String): String =
      data.get(key).flatMap(_.headOption).getOrElse("")

    def allValues(key: String): List[String] =
      data.getOrElse(key, List.empty)

    def boolean(key: String): Boolean =
      firstValue(key).equalsIgnoreCase("true")

    val titleValidation =
      nonEmpty("title")(firstValue("title"))
        .andThen(lengthBetween("title", 1, 200))

    val descriptionValidation =
      nonEmpty("description")(firstValue("description"))
        .andThen(lengthBetween("description", 10, 500))

    val pubDateValidation =
      if boolean("draft") then
        Validation.succeed(LocalDate.now()) // Default date for drafts
      else
        nonEmpty("pubDate")(firstValue("pubDate"))
          .flatMap(parseDate("pubDate"))

    val slugValidation = slug(firstValue("slug"))

    Validation.validateWith(
      titleValidation,
      descriptionValidation,
      pubDateValidation,
      slugValidation
    ) { (title, description, pubDate, slug) =>
      BlogPost(
        title = title,
        description = description,
        pubDate = pubDate,
        tags = allValues("tags"),
        aliases = allValues("aliases"),
        draft = boolean("draft"),
        slug = slug,
        markdown = markdown,
        html = html,
        headings = headings,
        wordCount = wordCount
      )
    }

  def fromFrontmatter(
      frontmatter: java.util.Map[String, java.util.List[String]],
      markdown: String,
      html: String,
      headings: List[TocHeading],
      wordCount: Int
  ): Either[List[String], BlogPost] =
    validate(frontmatter, markdown, html, headings, wordCount).toEither.left
      .map(_.toList)
