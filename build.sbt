name := "Hosptial"

version := "0.1"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "org.slf4j" % "slf4j-nop" % "2.0.5",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
  "mysql" % "mysql-connector-java" % "8.0.33"
)
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.9.0-M2"
