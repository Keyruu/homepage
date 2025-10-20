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
import de.keyruu.homepage.data.blog.BlogStore
import de.keyruu.homepage.data.blog.BlogPost

case class BlogRepo private (store: BlogStore):
  def getPost(slug: String): Option[BlogPost] =
    store.posts.get(slug)

  def getAllPosts(): List[BlogPost] =
    store.posts.values.toList.sortBy(_.pubDate).reverse

  def getPostsByTag(tag: String): Task[List[BlogPost]] =
    ZIO.succeed(store.postsByTag.getOrElse(tag, List.empty))

  def getAllTags(): Task[Set[String]] =
    ZIO.succeed(store.postsByTag.keySet)

  def searchPosts(query: String): List[BlogPost] =
    val lowerQuery = query.toLowerCase
    getAllPosts().filter { post =>
      post.title.toLowerCase.contains(lowerQuery) ||
      post.description.toLowerCase.contains(lowerQuery) ||
      post.tags.exists(_.toLowerCase.contains(lowerQuery))
    }

object BlogRepo:
  val layer: ZLayer[BlogStore, Nothing, BlogRepo] =
    ZLayer.derive[BlogRepo]
