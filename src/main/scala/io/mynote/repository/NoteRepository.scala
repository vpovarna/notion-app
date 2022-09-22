package io.mynote.repository

import scala.util.Try

import io.mynote.domain.Note
import io.mynote.repository.statuses.{Created, Deleted, InvalidId, ReasonUnknown}
import io.mynote.utils.{DBOperation, _}
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write
import org.mongodb.scala
import org.mongodb.scala.bson.Document
import org.mongodb.scala.model.Filters._
import zio.ZIO

trait NoteRepository {
  def addNote(note: Note): ZIO[Any, Throwable, DBOperation]
  def getNoteById(id: Int): ZIO[Any, Throwable, Option[Note]]
  def deleteNote(id: Int): ZIO[Any, Throwable, DBOperation]
  def getAll(): ZIO[Any, Throwable, List[Note]]
}

final case class MongoNoteRepositoryLive(collection: scala.MongoCollection[Document])
    extends NoteRepository {
  implicit val formats: DefaultFormats.type = DefaultFormats

  override def addNote(
      note: Note
  ): ZIO[Any, Throwable, DBOperation] = for {
    insertResult <- ZIO.fromFuture { implicit ec =>
      collection
        .insertOne(Document(write(note)))
        .toFuture
    }
    creationStatus <- insertResult.fold(
      insertResult.wasAcknowledged(),
      Created(s"Note with id: ${note.id} has been created successfully!"),
      ReasonUnknown("Unable to create note")
    )
  } yield creationStatus

  override def getNoteById(id: Int): ZIO[Any, Throwable, Option[Note]] = for {
    document <- ZIO.fromFuture { implicit ec =>
      collection
        .find(equal("id", id))
        .first()
        .toFuture()
    }
    note <- ZIO.attempt(parseDocumentToNote(document))
  } yield note

  override def deleteNote(id: Int): ZIO[Any, Throwable, DBOperation] = for {
    deleteResult <- ZIO.fromFuture { implicit ec =>
      collection
        .deleteOne(equal("id", id))
        .toFuture()
    }
    deleteStatus <- deleteResult.fold(
      deleteResult.getDeletedCount == 1,
      Deleted(s"Note with id: $id, deleted successfully!"),
      InvalidId(s"Could not delete Note. Note with id: $id does not exist")
    )
  } yield deleteStatus

  override def getAll(): ZIO[Any, Throwable, List[Note]] = for {
    documents <- ZIO.fromFuture { implicit ec =>
      collection
        .find()
        .toFuture()
    }
    notes <- ZIO.attempt(documents.flatMap(parseDocumentToNote).toList)
  } yield notes

  private def parseDocumentToNote(doc: Document): Option[Note] = {
    Try(
      Note(
        id = doc("id").asInt32().getValue,
        title = doc("title").asString.getValue,
        body = doc("body").asString.getValue,
        createdAt = doc("createdAt").asInt64().getValue
      )
    ).toOption
  }

}
