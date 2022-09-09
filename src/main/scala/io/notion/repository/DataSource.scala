package io.notion.repository

import zio._

trait DataSource {
  def setCtx(context: DatabaseContext): UIO[Unit]
  def getCtx: UIO[DatabaseContext]
}

final case class DataSourceLive(ref: Ref[DatabaseContext]) extends DataSource {
  override def setCtx(context: DatabaseContext): UIO[Unit] = ref.set(context)
  override def getCtx: UIO[DatabaseContext] = ref.get
}

object DataSourceLive {
  def live: ZLayer[Any, Nothing, DataSource] = ZLayer.scoped {
    for {
      ref <- Ref.make[DatabaseContext](DatabaseContext.initialize)
    } yield DataSourceLive(ref)
  }

}