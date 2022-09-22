package io.mynote.console

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
    _ <- ZIO.when(inputAction == 3)(addCollection())
    _ <- ZIO.when(inputAction > 3)(Console.printLine("Invalid selection"))
    _ <- Console.printLine(delimiterLine)
  } yield ()

  private def collectionMenu(): ZIO[Any, Throwable, String] = for {
    _ <- Console.printLine(delimiterLine)
    _ <- Console.printLine(
      """Provide the number corresponding to the desired action:
      |
      |  1. Get all collections
      |  2. Use collection
      |  3. Add a new collection
      |
      |To stop the application at any time press CTRL+C
     """.stripMargin
    )
    action <- Console.readLine("Your selection: ") <* Console.printLine("")
  } yield action

  private def getAllCollections: ZIO[Any, Throwable, Unit] = for {
    collections <- fetchAllCollections
    _ <- Console.printLine(delimiterLine)
    _ <- Console.printLine("The existing collections are: ")
    _ <-
      ZIO.foreach(collections)(collection =>
        Console.printLine(s"\t $collection")
      ) *>
        actionsTrigger().repeat(Schedule.forever)
  } yield ()

  private def useCollection(): ZIO[Any, Throwable, Unit] = for {
    collectionName <- Console.readLine("Collection name: ")
    dbCollections <- fetchAllCollections
    _ <-
      if (dbCollections.contains(collectionName))
        getMongoCollection(collectionName)
      else
        Console.printLine("") *>
          Console.printLine("Provided collection doesn't exist!") *>
          actionsTrigger().repeat(Schedule.forever)
  } yield ()

  private def addCollection(): ZIO[Any, Throwable, Unit] = for {
    collectionName <- Console.readLine("Collection name: ")
    _ <- getMongoCollection(collectionName)
  } yield ()

  private def getMongoCollection(
      collectionName: String
  ): ZIO[Any, Throwable, Unit] = for {
    mongoCollection <- MongoCollectionLive(mongoDatabaseContext)
      .getMongoCollection(collectionName)
    _ <- NoteActions(mongoDatabaseContext).noteActionTrigger(mongoCollection).repeat(Schedule.forever)
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
}
