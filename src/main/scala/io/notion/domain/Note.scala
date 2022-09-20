package io.notion.domain

/**
  * @param id = Unique random generated id. Used to identify a note
  * @param title = Note title
  * @param body = Note text
  * @param createdAt = Date in milliseconds wen the note has been created
  */
final case class Note(
    id: Int,
    title: String,
    body: String,
    createdAt: Long
)
