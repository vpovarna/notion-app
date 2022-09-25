package io.mynote.repository.mongo

import io.mynote.repository.DBConfig
import org.mongodb.scala.MongoClient
import zio._

final case class MongoDatabaseInitializer(dbConfig: DBConfig) {

  def initialize: ZIO[Any, Throwable, Ref[MongoDatabaseContext]] = for {
    mongoContext <- buildMongoContext()
    ref <- Ref.make(mongoContext)
  } yield ref

  private def buildMongoContext() = for {
    _ <- Console.printLine(
      s"Attempting to establish connection to MongoDB host ${dbConfig.hostname}, on port: ${dbConfig.port} with db ${dbConfig.dbName}"
    )
    mongoClient <- ZIO.attempt(
      MongoClient(s"mongodb://${dbConfig.hostname}:${dbConfig.port}")
    )
    mongoDatabase <- ZIO.attempt(
      mongoClient.getDatabase(dbConfig.dbName)
    ) <* Console.printLine("Established connection with database successfully!")
  } yield MongoDatabaseContext(mongoDatabase)

}
