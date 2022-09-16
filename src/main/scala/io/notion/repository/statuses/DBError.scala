package io.notion.repository.statuses

sealed abstract class DbError(val msg: String)

case class InvalidId(override val msg: String) extends DbError(msg)
case class NotFound(override val msg: String) extends DbError(msg)
case class ReasonUnknown(override val msg: String) extends DbError(msg)
