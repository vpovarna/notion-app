package io.mynote.repository.mongo

import org.mongodb.scala.{Document, MongoCollection, Observable}
import zio._

trait MongoCollections {
  def getAll(): ZIO[Any, Throwable, Observable[String]]
  def getMongoCollection(
      collection: String
  ): ZIO[Any, Throwable, MongoCollection[Document]]
}

final case class MongoCollectionLive(mongoDatabaseContext: MongoDatabaseContext)
    extends MongoCollections {

  def getAll(): ZIO[Any, Throwable, Observable[String]] = for {
    collections <- ZIO.attempt(
      mongoDatabaseContext.mongoDatabase.listCollectionNames()
    )
  } yield collections

  def getMongoCollection(
      collection: String
  ): ZIO[Any, Throwable, MongoCollection[Document]] =
    for {
      collection <- ZIO.attempt(
        mongoDatabaseContext.mongoDatabase.getCollection(collection)
      )
    } yield collection

}
