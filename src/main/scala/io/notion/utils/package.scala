package io.notion

import io.notion.repository.statuses.{DbError, DbSuccess}
import zio._

package object utils {

  type DBOperation = Either[DbError, DbSuccess]

  implicit class InsertOneResultWrapper[A](a: A) {
    def fold(
        wasAcknowledged: => Boolean,
        onSuccess: => DbSuccess,
        onFailure: => DbError
    ): ZIO[Any, Nothing, DBOperation] =
      ZIO.succeed(if (wasAcknowledged) Right(onSuccess) else Left(onFailure))
  }
}
