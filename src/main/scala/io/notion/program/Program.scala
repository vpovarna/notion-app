package io.notion.program

import java.io.IOException
import java.time.temporal.ChronoUnit

import io.notion.config.NotionAppConfig
import io.notion.domain.Note
import io.notion.repository.DataSource
import io.notion.service.NoteServiceLive
import org.mongodb.scala
import org.mongodb.scala.bson.Document
import zio.Random._
import zio._
import io.notion.utils._

object Program {
  private val delimiterLength = 80
  private val delimiterChar = "-"
  private val delimiterLine = delimiterChar * delimiterLength

  def run(): ZIO[Any, Throwable, Unit] = for {
    dbConfig <- NotionAppConfig.make()
    collection <- DataSource(dbConfig).getMongoCollection
    number <- buildApplicationMenu()
    inputAction <- ZIO.attempt(number.trim.toInt).orElseFail(throw new IllegalArgumentException("Invalid input"))
    _ <- ZIO.when(inputAction == 1)(addNote(collection))
    _ <- ZIO.when(inputAction == 2)(deleteNote(collection))
    _ <- ZIO.when(inputAction == 3)(getNoteByID(collection))
    _ <- ZIO.when(inputAction == 4)(getAllNotes(collection))
    _ <- ZIO.when(inputAction > 4)(Console.printLine("Invalid selection"))
    _ <- Console.printLine(delimiterLine)
  } yield ()

  private def buildApplicationMenu(): ZIO[Any, IOException, String] = for {
    _ <- Console.printLine(delimiterLine)
    _ <- Console.printLine(
      """Provide the number corresponding to the desired action:
        | 1. Add new note
        | 2. Delete a note
        | 3. Get note by id
        | 4. Get all notes
       """.stripMargin
    )
    action <- Console.readLine("Your selection: ") <* Console.printLine("")
  } yield action

  private def createNote: ZIO[Any, IOException, Note] = for {
    noteTitle <- Console.readLine("Note Title: ")
    noteText <- Console.readLine("Note Text: ") <* Console.printLine("")
    id <- nextIntBounded(Integer.MAX_VALUE)
    currentTime <- Clock.currentTime(ChronoUnit.MILLIS)
  } yield Note(id, noteTitle, noteText, currentTime)

  private def addNote(
      collection: scala.MongoCollection[Document]
  ): ZIO[Any, Throwable, Unit] = for {
    note <- createNote
    _ <- NoteServiceLive(collection).addNote(note)
  } yield ()

  private def getNoteByID(
      collection: scala.MongoCollection[Document]
  ): ZIO[Any, Throwable, Unit] = for {
    noteId <- Console.readLine("Provide note id: ") <* Console.printLine("")
    note <- NoteServiceLive(collection).getNoteByID(noteId.toInt)
    _ <- printNote(note)
  } yield ()

  private def getAllNotes(collection: scala.MongoCollection[Document]) = for {
    notes <- NoteServiceLive(collection).getAll()
    _ <- ZIO.foreach(notes)(printNote)
  } yield ()

  private def printNote(note: Note): ZIO[Any, IOException, Unit] = for {
    _ <- Console.printLine(delimiterLine) <* Console.printLine(s"Title: ${note.title}")
    _ <- Console.printLine(s"Body: ${note.body}")
    _ <- Console.printLine(s"CreatedAt: ${note.createdAt}")
  } yield ()

  private def deleteNote(
      collection: scala.MongoCollection[Document]
  ): ZIO[Any, Throwable, Unit] = for {
    noteId <- Console.readLine("Provide note id: ") <* Console.printLine("")
    _ <- NoteServiceLive(collection).deleteNote(noteId.toInt)
  } yield ()

}
