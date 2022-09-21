package io.mynote.service

import io.mynote.domain.Note
import io.mynote.repository.NoteRepositoryLive
import io.mynote.utils._
import org.mongodb.scala
import org.mongodb.scala.bson.Document
import zio._

trait NoteService {
  def addNote(note: Note): ZIO[Any, Throwable, Unit]
  def getNoteByID(id: Int): ZIO[Any, Throwable, Note]
  def deleteNote(id: Int): ZIO[Any, Throwable, Unit]
  def getAll(): ZIO[Any, Throwable, List[Note]]
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

  override def getAll(): ZIO[Any, Throwable, List[Note]] = for {
    notes <- NoteRepositoryLive(collection).getAll()
  } yield notes
}
