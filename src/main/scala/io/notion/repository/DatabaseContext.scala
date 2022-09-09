package io.notion.repository

import org.mongodb.scala.MongoDatabase

final case class DatabaseContext(mongoDatabase: Option[MongoDatabase])

object DatabaseContext {
  def initialize: DatabaseContext = new DatabaseContext(None)
  def apply(mongoDatabase: MongoDatabase): DatabaseContext = new DatabaseContext(Some(mongoDatabase))
}