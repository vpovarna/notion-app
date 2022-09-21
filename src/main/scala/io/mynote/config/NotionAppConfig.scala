package io.mynote.config

import io.mynote.repository.DBConfig
import zio._

object NotionAppConfig {

  def make(): ZIO[Any, SecurityException, DBConfig] = for {
    endpoint <- System.envOrElse("MONGO_ENDPOINT", "localhost")
    dbPort <- System.envOrElse("MONGO_PORT", "27017").map(_.toInt)
    dbName <- System.envOrElse("MONGO_DB_NAME", "notesdb")
    collection <- System.envOrElse("MONGO_COLLECTION", "notesdb")
  } yield DBConfig(endpoint, dbPort, dbName, collection)

}
