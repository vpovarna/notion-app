package io.mynote.console

import io.mynote.config.NotionAppConfig
import io.mynote.repository.mongo.MongoDatabaseInitializer
import zio._

object Program {

  def run(): ZIO[Any, Throwable, Unit] = for {
    dbConfig <- NotionAppConfig.make()
    mongoDatabaseContext <- MongoDatabaseInitializer(dbConfig).initialize
    _ <- CollectionActions(mongoDatabaseContext).actionsTrigger()
    //     collection <- MongoCollectionLive(mongoDatabaseContext).getMongoCollection
    //    _ <- noteActionTrigger(collection).repeat(Schedule.forever)
  } yield ()

}
