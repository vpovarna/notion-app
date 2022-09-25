package io.mynote.console

import io.mynote.config.NotionAppConfig
import io.mynote.repository.mongo.MongoDatabaseInitializer
import zio._

object Program {

  def run(): ZIO[Any, Throwable, Unit] = for {
    dbConfig <- NotionAppConfig.make()
    mongoDatabaseContextRef <- MongoDatabaseInitializer(dbConfig).initialize
    mongoDatabaseContext <- mongoDatabaseContextRef.get
    _ <- CollectionActions(mongoDatabaseContext).actionsTrigger()
  } yield ()

}
