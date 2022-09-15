package io.notion.repository

sealed abstract class DbError(val msg: String)

case class Invalid(override val msg: String) extends DbError(msg)
case class NotFound(override val msg: String) extends DbError(msg)
case class ReasonUnknown(override val msg: String) extends DbError(msg)
