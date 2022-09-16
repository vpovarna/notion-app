package io.notion.domain

final case class Note(
    id: Int,
    title: String,
    body: String,
    createdAt: Long
)
