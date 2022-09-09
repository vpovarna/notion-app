package io.notion.program

import io.notion.repository.mongo.MongoDatabaseInitializer
import io.notion.repository.{DataSource, DataSourceLive}
import io.notion.service.NotesService
import zio._

object Program {

  lazy val dataSourceLayer: ZLayer[Any, Nothing, DataSource] = DataSourceLive.live
  lazy val mongoDatabaseLayer: ZLayer[DataSource, Nothing, MongoDatabaseInitializer] = MongoDatabaseInitializer.live
  lazy val serverLayer: ZLayer[Any, Nothing, NotesService] = NotesService.live

//  lazy val programLayer: ZLayer[Any, Nothing, DataSource with NotesService with MongoDatabaseInitializer] =
//    dataSourceLayer ++ serverLayer ++ (dataSourceLayer >>> mongoDatabaseLayer)

  lazy val run: ZIO[Any, Throwable, Unit] =
    ZIO.serviceWithZIO[NotesService](noteService => noteService.start())
        .provide(dataSourceLayer,
          mongoDatabaseLayer,
          serverLayer)
}
