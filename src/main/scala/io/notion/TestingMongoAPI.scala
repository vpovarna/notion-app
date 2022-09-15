package io.notion

import io.notion.utils.Helpers.GenericObservable
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}

object TestingMongoAPI extends App {

  val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017")
  val database: MongoDatabase = mongoClient.getDatabase("notesdb")
  val collection: MongoCollection[Document] = database.getCollection("notesdb")

  val doc: Document = Document(
    "_id" -> 4,
    "name" -> "MongoDB",
    "type" -> "database",
    "count" -> 1,
    "info" -> Document("f" -> 303, "y" -> 102)
  )

  collection.insertOne(doc).printResults()
  println("File names:")

  collection.find().results().foreach(file => println(s" - ${file}"))

}
