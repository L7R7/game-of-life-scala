organization := "com.bosch.sh.dojo"

name := "simple-sbt-project"

version := "0.1.0"

scalaVersion := "2.12.3"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

resolvers ++= Seq(
  Resolver.typesafeRepo("releases")
)

libraryDependencies ++= Seq(
)
