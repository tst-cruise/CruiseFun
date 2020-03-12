resolvers += "Sonatype OSS Snapshots" at
    "https://oss.sonatype.org/content/repositories/snapshots"

organization := "com.tst"

name := "CruiseFun"

version := "0.1"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "org.scalacheck" %% "scalacheck" % "1.13.5" % Test
)