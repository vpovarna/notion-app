package io.notion.repository

import io.notion.domain.Note
import io.notion.repository.statuses.{Created, ReasonUnknown}
import io.notion.utils.{DBOperation, _}
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write
import org.mongodb.scala
import org.mongodb.scala.bson.Document
import org.mongodb.scala.model.Filters._
import zio.ZIO

final case class NoteRepository(collection: scala.MongoCollection[Document]) {
  implicit val formats: DefaultFormats.type = DefaultFormats

  def addNote(
      note: Note
  ): ZIO[Any, Throwable, DBOperation] = for {
    insertResult <- ZIO.fromFuture { implicit ec =>
      collection
        .insertOne(Document(write(note)))
        .toFuture
    }
    creationStatus <- insertResult.fold(
      insertResult.wasAcknowledged(),
      Created(s"Note: $note has been created successfully!"),
      ReasonUnknown("Unable to create note")
    )
  } yield creationStatus

  def getNoteById(id: Int): ZIO[Any, Throwable, Option[Note]] = for {
    document <- ZIO.fromFuture { implicit ec =>
      collection
        .find(equal("id", id))
        .first()
        .toFuture()
    }
    note <- ZIO.attempt(parseDocumentToNote(document))
  } yield note

  private def parseDocumentToNote(document: Document): Option[Note] =
    Option(document) match {
      case Some(doc) => Some(buildNote(doc))
      case None => None
    }

  private def buildNote(doc: Document): Note = {
    Note(
      id = doc("id").asInt32().getValue,
      title = doc("title").asString.getValue,
      body = doc("body").asString.getValue,
      createdAt = doc("createdAt").asInt64().getValue
    )
  }
}
