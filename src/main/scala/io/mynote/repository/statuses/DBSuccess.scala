package io.mynote.repository.statuses

sealed abstract class DbSuccess(val msg: String)

case class Created(override val msg: String) extends DbSuccess(msg)
case class Updated(override val msg: String) extends DbSuccess(msg)
case class Deleted(override val msg: String) extends DbSuccess(msg)
