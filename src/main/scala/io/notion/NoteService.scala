package io.notion

import io.notion.domain.Note
import io.notion.repository.NoteRepositoryLive
import io.notion.utils._
import org.mongodb.scala
import org.mongodb.scala.bson.Document
import zio._

trait NoteService {
  def addNote(note: Note): ZIO[Any, Throwable, Unit]
  def getNoteByID(id: Int): ZIO[Any, Throwable, Note]
  def deleteNote(id: Int): ZIO[Any, Throwable, Unit]
}

final case class NoteServiceLive(collection: scala.MongoCollection[Document])
    extends NoteService {

  override def addNote(note: Note): ZIO[Any, Throwable, Unit] = for {
    creationStatus <- NoteRepositoryLive(collection).addNote(note)
    _ <- creationStatus.printResult()
  } yield ()

  override def getNoteByID(noteId: Int): ZIO[Any, Throwable, Note] = for {
    someNote <- NoteRepositoryLive(collection).getNoteById(noteId)
    note <- someNote match {
      case Some(value) => ZIO.succeed(value)
      case None => ZIO.fail(throw new IllegalArgumentException(s"Unable tot fetch note with id: $noteId from repository"))
    }
  } yield note

  override def deleteNote(id: RuntimeFlags): ZIO[Any, Throwable, Unit] = for {
    deleteStatus <- NoteRepositoryLive(collection).deleteNote(id)
    _ <- deleteStatus.printResult()
  } yield ()

}
