package de.keyruu.homepage.data

import scala.jdk.CollectionConverters.*
import zio.*
import java.nio.file.Paths
import java.nio.file.Files
import java.nio.file.Path
import org.commonmark.parser.Parser
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import java.{util => ju}
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import java.time.LocalDate

class Content:
  private var posts: Map[String, BlogPost] = Map.empty
  private var postsByTag: Map[String, List[BlogPost]] = Map.empty

  def init(): Task[Unit] =
    for {
      allPosts <- loadAllPosts("content/blog")
      _ <- ZIO.attempt {
        posts = allPosts.map(p => p.slug -> p).toMap
        postsByTag = allPosts
          .flatMap(p => p.tags.map(tag => tag -> p))
          .groupMap(_._1)(_._2)
      }
    } yield ()

  def loadAllPosts(path: String): Task[List[BlogPost]] =
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
      posts <- ZIO.collectAll(paths.map(loadPost))
    } yield posts
      .filter(!_.draft)
      .sortBy(_.pubDate)(using Ordering[LocalDate].reverse)

  def loadPost(path: Path): Task[BlogPost] =
    for {
      content <- ZIO.attempt(Files.readString(path))
      parser = Parser.builder
        .extensions(ju.List.of(YamlFrontMatterExtension.create))
        .build
      document = parser.parse(content)
      visitor = new YamlFrontMatterVisitor()
      _ = document.accept(visitor)
      frontmatterMap = visitor.getData
      post <- ZIO
        .fromEither(BlogPost.fromFrontmatter(frontmatterMap))
        .mapError(errors => new Exception(errors.mkString(", ")))
    } yield post
