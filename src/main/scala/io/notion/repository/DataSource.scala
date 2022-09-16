package io.notion.repository

import io.notion.repository.mongo.MongoDatabaseInitializer
import org.mongodb.scala.{Document, MongoCollection}
import zio.ZIO

final case class DataSource(dbConfig: DBConfig) {

  def getMongoCollection: ZIO[Any, Throwable, MongoCollection[Document]] =
    for {
      mongoDatabaseContext <- MongoDatabaseInitializer(dbConfig).initialize
      collection <- ZIO.attempt(
        mongoDatabaseContext.mongoDatabase.getCollection(dbConfig.collection)
      )
    } yield collection

}
