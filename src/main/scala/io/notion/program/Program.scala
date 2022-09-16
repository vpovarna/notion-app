package io.notion.program

import java.io.IOException
import java.time.temporal.ChronoUnit

import io.notion.config.NotionAppConfig
import io.notion.domain.Note
import io.notion.repository.{DataSource, NoteRepository}
import zio.Random._
import zio._

object Program {

  def createNote: ZIO[Any, IOException, Note] = for {
    noteTitle <- Console.readLine("Note Title: ")
    noteText <- Console.readLine("Note Text: ")
    id <- nextIntBounded(Integer.MAX_VALUE)
    currentTime <- Clock.currentTime(ChronoUnit.MILLIS)
  } yield Note(id, noteTitle, noteText, currentTime)

  def run(): ZIO[Any, Any, Unit] = for {
    dbConfig <- NotionAppConfig.make
    collection <- DataSource(dbConfig).getMongoCollection
    note <- createNote
    creationStatus <- NoteRepository(collection).addNote(note) <* Console.printLine("Note creation status:info")
    _ <- Console.printLine(creationStatus)
    _ <- Console.printLine(s"Fetching note with id: ${note.id} from DB")
    document <- NoteRepository(collection).getNoteById(note.id)
    _ <- Console.printLine(document.get)
  } yield ()

}
