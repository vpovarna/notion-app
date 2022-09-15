package io.notion.program

import java.util.UUID

import io.notion.config.NotionAppConfig
import io.notion.repository.DBConfig
import io.notion.repository.mongo.MongoDatabaseInitializer
import io.notion.utils.Helpers.GenericObservable
import org.mongodb.scala.{Document, MongoCollection}
import zio._

object Program {

  def getMongoCollection(
      dbConfig: DBConfig
  ): ZIO[Any, Throwable, MongoCollection[Document]] =
    for {
      mongoDatabaseContext <- MongoDatabaseInitializer(dbConfig).initialize
      collection <- ZIO.attempt(
        mongoDatabaseContext.mongoDatabase.getCollection(dbConfig.collection)
      )
    } yield collection

  def run(): ZIO[Any, Any, Unit] = for {
    dbConfig <- NotionAppConfig.make()
    collection <- getMongoCollection(dbConfig)
    noteTitle <- Console.readLine("Note Title:")
    noteText <- Console.readLine("Note Text:")

    _ <- ZIO.attempt(
      collection
        .insertOne(
          Document(
            "_id" -> UUID.randomUUID().toString,
            "title" -> noteTitle,
            "body" -> noteText,
            "count" -> 1,
            "info" -> Document("f" -> 303, "y" -> 102)
          )
        )
        .printResults()
    )
    _ <- ZIO.succeed(
      collection
        .find()
        .results()
        .foreach(file => Console.printLine(s" - $file"))
    )
  } yield ()

}
