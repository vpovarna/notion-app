package io.notion.program

import io.notion.repository.mongo.{DatabaseInitializer, MongoDatabaseInitializer}
import io.notion.repository.{DBConfig, DataSource, DataSourceLive}
import zio._

final case class NotesService() {

  def start(): ZIO[DataSource with DatabaseInitializer, Throwable, Unit] = for {
    _ <- Console.printLine("Starting DB!")
    dbEndpoint <- System.envOrElse("MONGO_ENDPOINT", "localhost")
    dbPort <- System.envOrElse("MONGO_PORT", "27018").map(_.toInt)
    dbName <- System.envOrElse("MONGO_DB_NAME", "notesdb")
    _ <- ZIO.service[DatabaseInitializer].flatMap(_.initializer(DBConfig(dbEndpoint, dbPort, dbName)))
    _ <- Console.printLine(s"DB connection successfully!")
  } yield ()
}

object NotesService {
  lazy val live: ZLayer[Any, Nothing, NotesService] = ZLayer.fromFunction(NotesService.apply _)
}

object Program {

  lazy val dataSourceLayer: ZLayer[Any, Nothing, DataSource] = DataSourceLive.live
  lazy val mongoDatabaseLayer: ZLayer[DataSource, Nothing, MongoDatabaseInitializer] = MongoDatabaseInitializer.live
  lazy val serverLayer: ZLayer[Any, Nothing, NotesService] = NotesService.live

  lazy val run: ZIO[Any, Throwable, Unit] =
    ZIO.serviceWithZIO[NotesService](_.start)
        .provide(dataSourceLayer,
          mongoDatabaseLayer,
          serverLayer)
}
