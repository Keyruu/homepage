package de.keyruu.homepage.logic

import zio.*
import zio.http.*

object ErrorMapper {
  extension [E <: Throwable, A](task: ZIO[Any, E, A])
    def defaultErrorMappings: ZIO[Any, Response, A] = task.mapError { case _ =>
      Response(
        status = Status.InternalServerError
      )
    }
}
