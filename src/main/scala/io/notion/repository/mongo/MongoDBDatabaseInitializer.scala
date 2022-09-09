package io.notion.repository.mongo

import zio._
import io.notion.repository.{DBConfig, DataSource, DatabaseContext}
import org.mongodb.scala.MongoClient

trait DatabaseInitializer {
  def initializer(dbConfig: DBConfig): ZIO[DataSource, Throwable, Unit]
}

final case class MongoDatabaseInitializer(dataSource: DataSource) extends DatabaseInitializer {
  override def initializer(
      dbConfig: DBConfig): ZIO[DataSource, Throwable, Unit] = {
    for {
      _ <- Console.printLine(s"Attempting to establish connection to MongoDB host ${dbConfig.hostname}, on port: ${dbConfig.port} with db ${dbConfig.dbName}")
      client <- ZIO.attempt(MongoClient(s"mongodb://${dbConfig.hostname}:${dbConfig.port}"))
      dbName <- ZIO.attempt(client.getDatabase(dbConfig.dbName)) <* Console.printLine("Established connection with database successfully!")
      _ <- dataSource.setCtx(DatabaseContext(dbName))
    } yield ()
  }
}

object MongoDatabaseInitializer {
  def live: ZLayer[DataSource, Nothing, MongoDatabaseInitializer] = ZLayer.fromFunction(MongoDatabaseInitializer.apply _)
}
