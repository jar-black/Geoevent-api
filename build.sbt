lazy val pekkoHttpVersion = "1.1.0"
lazy val pekkoVersion = "1.1.2"
lazy val doobieVersion = "1.0.0-RC1"

fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.geoevent",
      scalaVersion := "2.13.15"
    )),
    name := "Geoevent-api",
    libraryDependencies ++= Seq(
      "org.flywaydb" % "flyway-core" % "7.0.0",
      "org.scalikejdbc" %% "scalikejdbc" % "4.2.1",
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
      "ch.qos.logback" % "logback-classic" % "1.3.14",

      "org.apache.pekko" %% "pekko-http-testkit" % pekkoHttpVersion % Test,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )
