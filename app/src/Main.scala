package app

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

object MinimalApplication extends cask.MainRoutes {
  @cask.get("/")
  def hello() = {
    "Hello World!"
  }

  @cask.post("/do-thing")
  def doThing(request: cask.Request) = {
    request.text().reverse
  }

  initialize()
}

def hello(): Unit =
  println("Hello world!")
  println(msg)
  val parser = Parser.builder().build();
  val document = parser.parse("This is *Markdown*");
  val renderer = HtmlRenderer.builder().build();
  // "<p>This is <em>Markdown</em></p>\n"
  println(renderer.render(document))

def msg = "I was compiled by Scala 3. :)"
