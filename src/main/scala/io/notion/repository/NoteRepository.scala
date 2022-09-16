package io.notion.repository

import io.notion.domain.Note
import io.notion.repository.status.{Created, ReasonUnknown}
import io.notion.utils.DbOperation
import org.json4s.jackson.Serialization.write
import org.mongodb.scala
import org.mongodb.scala.bson.Document
import zio.ZIO
import io.notion.utils._
import org.json4s.DefaultFormats

final case class NoteRepository(collection: scala.MongoCollection[Document]) {
  implicit val formats: DefaultFormats.type = DefaultFormats

  def addNote(
      note: Note
  ): ZIO[Any, Throwable, DbOperation] = for {
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

}
