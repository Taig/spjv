val circeVersion = "0.12.0-RC1"
val jsonSchemaValidatorVersion = "2.2.10"
val http4sVersion = "0.21.0-M3"
val slf4jSimpleVersion = "1.7.28"
val scalatestVersion = "3.0.8"

name := "spjv"

libraryDependencies ++=
  "com.github.java-json-tools" % "json-schema-validator" % jsonSchemaValidatorVersion ::
    "io.circe" %% "circe-core" % circeVersion ::
    "io.circe" %% "circe-generic" % circeVersion ::
    "io.circe" %% "circe-parser" % circeVersion ::
    "org.http4s" %% "http4s-circe" % http4sVersion ::
    "org.http4s" %% "http4s-dsl" % http4sVersion ::
    "org.http4s" %% "http4s-blaze-server" % http4sVersion ::
    "org.slf4j" % "slf4j-simple" % slf4jSimpleVersion ::
    "org.scalatest" %% "scalatest" % scalatestVersion % "test" ::
    Nil

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"
test in assembly := {}
