package io.notion

import io.notion.repository.{DbError, DbSuccess}
import org.mongodb.scala.result.InsertOneResult
import zio._

package object utils {

  implicit class InsertOneResultWrapper[A <: InsertOneResult](a: A) {
    def fold(
        wasAcknowledged: => Boolean,
        onSuccess: => DbSuccess,
        onFailure: => DbError
    ): ZIO[Any, Nothing, Either[DbError, DbSuccess]] =
      ZIO.succeed(if (wasAcknowledged) Right(onSuccess) else Left(onFailure))

  }
}
