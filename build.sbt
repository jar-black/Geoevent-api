lazy val pekkoHttpVersion = "1.1.0"
lazy val pekkoVersion = "1.1.2"
lazy val doobieVersion = "1.0.0-RC1"

fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.geoevent",
      scalaVersion := "2.12.20"
    )),
    name := "Geoevent-api",
    libraryDependencies ++= Seq(
      "org.flywaydb" % "flyway-core" % "8.5.13",
      "org.scalikejdbc" %% "scalikejdbc" % "4.3.2",
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.12",
      "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0",
      "org.apache.pekko" %% "pekko-http-testkit" % pekkoHttpVersion % Test,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )
