package io.notion

import java.io.IOException

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

  implicit class DBStatusWrapper[A <: DBOperation](dbOperation: DBOperation) {
    def printResult(): IO[IOException, Unit] = dbOperation.fold(
      dbError => Console.printLine(dbError.msg),
      dbSuccess => Console.printLine(dbSuccess.msg)
    )
  }

}
