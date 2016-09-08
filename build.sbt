// Project name (artifact name in Maven)
name := "popbill-sdk-java"

// orgnization name (e.g., the package name of the project)
organization := "com.getprobe"

val snapshot = "-SNAPSHOT"
version := s"1.0$snapshot"

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

libraryDependencies ++= Seq(
  "kr.co.linkhub" % "linkhub-auth" % "1.0.2",
  "com.google.code.gson" % "gson" % "2.3",
  "junit" % "junit" % "4.7" % "test"
)