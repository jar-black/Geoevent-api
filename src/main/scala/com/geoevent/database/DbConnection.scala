package com.geoevent.database

import cats.effect.IO
import doobie.Transactor
import org.flywaydb.core.Flyway

object DbConnection extends DbConnection

trait DbConnection {
  def flywayMigration(): Unit = {
    val flywayMigration: Flyway = Flyway.configure().dataSource("jdbc:postgresql://localhost:5432/geoeventdb", "geoevent", "geoevent").load()
    flywayMigration.migrate()
  }
  val transactor: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/geoeventdb",
    "geoevent",
    "geoevent"
  )
}
