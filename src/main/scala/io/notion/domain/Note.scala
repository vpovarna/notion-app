package io.notion.domain

final case class Note(
    id: Long,
    title: String,
    body: String,
    createdAt: Long
)
