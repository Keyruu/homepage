package de.keyruu.homepage.data.blog

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.util as ju

import scala.jdk.CollectionConverters._

import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension
import org.commonmark.parser.Parser
import zio.*
import org.commonmark.renderer.html.HtmlRenderer
import de.keyruu.homepage.logic.markdown.HeadingVisitor
import org.commonmark.renderer.html.HtmlNodeRendererFactory
import org.commonmark.renderer.NodeRenderer
import de.keyruu.homepage.logic.markdown.SectionRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext
import de.keyruu.homepage.logic.markdown.WikiLinkExtension
import de.keyruu.homepage.logic.markdown.SectionizerExtension
import de.keyruu.homepage.logic.markdown.LinkExtension
import de.keyruu.homepage.logic.markdown.CalloutExtension
import de.keyruu.homepage.logic.markdown.WordCountVisitor

case class BlogStore(
    val posts: Map[String, BlogPost],
    val postsByTag: Map[String, List[BlogPost]]
)

object BlogStore:
  val layer: ZLayer[Any, Throwable, BlogStore] =
    ZLayer.fromZIO {
      for {
        allPosts <- loadAllPosts("src/main/resources/content/blog")
        posts = allPosts.map(p => p.slug -> p).toMap
        postsByTag = allPosts
          .flatMap(p => p.tags.map(tag => tag -> p))
          .groupMap(_._1)(_._2)
      } yield new BlogStore(posts, postsByTag)
    }

  private def loadAllPosts(path: String): Task[List[BlogPost]] =
    for {
      paths <- ZIO.attempt {
        val dir = Paths.get(path)
        if Files.exists(dir) then
          Files
            .list(dir)
            .iterator
            .asScala
            .filter(_.toString.endsWith(".md"))
            .toList
        else List.empty
      }
      results <- ZIO.foreach(paths) { path =>
        loadPost(path)
          .map(Some(_))
          .catchAll { error =>
            val message = error match {
              case e: ValidationException =>
                s"Validation failed for ${path}: ${e.getMessage}"
              case e => s"Failed to load ${path}: ${e.getMessage}"
            }
            ZIO.logError(message) *> ZIO.succeed(None)
          }
      }
    } yield results.flatten
      .filter(!_.draft)
      .sortBy(_.pubDate)(using Ordering[LocalDate].reverse)

  private def loadPost(path: Path): Task[BlogPost] =
    for {
      markdown <- ZIO.attempt(
        Files.readString(path, java.nio.charset.StandardCharsets.UTF_8)
      )
      extensions = List(
        TablesExtension.create,
        new WikiLinkExtension,
        new SectionizerExtension,
        new CalloutExtension
      )
      parserExtensions = extensions :+ YamlFrontMatterExtension.create
      parser = Parser.builder
        .extensions(parserExtensions.asJava)
        .build
      document = parser.parse(markdown)
      frontmatterVisitor = new YamlFrontMatterVisitor()
      _ = document.accept(frontmatterVisitor)
      frontmatterMap = frontmatterVisitor.getData
      headingVisitor = new HeadingVisitor()
      _ = document.accept(headingVisitor)
      headings = headingVisitor.headingsBuffer.toList
      wordCountVisitor = new WordCountVisitor()
      _ = document.accept(wordCountVisitor)
      wordCount = wordCountVisitor.wordCount
      rendererExtensions = extensions :+ new LinkExtension
      renderer = HtmlRenderer.builder
        .extensions(rendererExtensions.asJava)
        .build
      html = renderer.render(document)
      post <- ZIO
        .fromEither(
          BlogPost.fromFrontmatter(
            frontmatterMap,
            markdown,
            html,
            headings,
            wordCount
          )
        )
        .mapError(errors => new ValidationException(errors))
    } yield post
