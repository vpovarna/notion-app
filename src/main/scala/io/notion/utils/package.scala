package io.notion

import io.notion.repository.status.{DbError, DbSuccess}
import org.mongodb.scala.result.InsertOneResult
import zio._

package object utils {

  type DbOperation = Either[DbError, DbSuccess]

  implicit class InsertOneResultWrapper[A <: InsertOneResult](a: A) {
    def fold(
        wasAcknowledged: => Boolean,
        onSuccess: => DbSuccess,
        onFailure: => DbError
    ): ZIO[Any, Nothing, DbOperation] =
      ZIO.succeed(if (wasAcknowledged) Right(onSuccess) else Left(onFailure))
  }
}
