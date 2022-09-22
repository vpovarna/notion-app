package io.mynote.console

import io.mynote.console.NoteActions.noteActionTrigger
import io.mynote.repository.mongo.{MongoCollectionLive, MongoDatabaseContext}
import zio.{Console, Schedule, ZIO}

case class CollectionActions(mongoDatabaseContext: MongoDatabaseContext) {

  def actionsTrigger(): ZIO[Any, Throwable, Unit] = for {
    number <- collectionMenu()
    inputAction <- ZIO
      .attempt(number.trim.toInt)
      .orElseFail(throw new IllegalArgumentException("Invalid selection"))
    _ <- ZIO.when(inputAction == 1)(getAllCollections)
    _ <- ZIO.when(inputAction == 2)(useCollection())
    _ <- ZIO.when(inputAction > 3)(Console.printLine("Invalid selection"))
    _ <- Console.printLine(delimiterLine)
  } yield ()

  private def collectionMenu(): ZIO[Any, Throwable, String] = for {
    _ <- Console.printLine(
      """Provide the number corresponding to the desired action:
      | 1. Get all collections
      | 2. Use collection
     """.stripMargin
    )
    action <- Console.readLine("Your selection: ") <* Console.printLine("")
  } yield action

  private def getAllCollections: ZIO[Any, Throwable, Unit] = for {
    collections <- fetchAllCollections
    _ <- Console.printLine(delimiterLine) *> Console.printLine(
      "The existing collections are: "
    ) *> ZIO.foreach(collections)(collection =>
      Console.printLine(s"\t $collection")
    )
  } yield ()

  private def fetchAllCollections: ZIO[Any, Throwable, List[String]] = for {
    observableCollections <- MongoCollectionLive(mongoDatabaseContext).getAll()
    collectionNameList <- ZIO.fromFuture(implicit ec =>
      {
        observableCollections
          .foldLeft(List.empty[String]) { (list, collection) =>
            collection +: list
          }
      }.head()
    )
  } yield collectionNameList

  private def useCollection(): ZIO[Any, Throwable, Unit] = for {
    collectionName <- Console.readLine("Collection name: ")
    dbCollections <- getAllCollections

    // TODO: validate if the collection is allready part of the DB
    collection <- MongoCollectionLive(mongoDatabaseContext).getMongoCollection(
      collectionName
    )
    _ <- noteActionTrigger(collection).repeat(Schedule.forever)
  } yield ()

}
