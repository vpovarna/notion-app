package io.notion.repository

final case class DBConfig(hostname: String, port: Int, dbName: String, collection: String)
