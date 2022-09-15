package io.notion.domain

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

final case class Note(
    id: Option[Long] = None,
    title: String,
    body: String,
    createdAt: Option[Long] = None
)

object Note {
  implicit val decoder: JsonDecoder[Note] = DeriveJsonDecoder.gen[Note]
  implicit val encoder: JsonEncoder[Note] = DeriveJsonEncoder.gen[Note]
}
