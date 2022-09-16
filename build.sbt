name := "notion-app"
version := "0.1"
scalaVersion := "2.13.8"

lazy val zioVersion = "2.0.2"
lazy val mongoVersion = "4.6.0"
lazy val zioJsonVersion = "0.1.5"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion,
  "dev.zio" %% "zio-test-sbt" % zioVersion,
  "dev.zio" %% "zio-test-junit" % zioVersion,
  "org.mongodb.scala" %% "mongo-scala-driver" % mongoVersion,
  "org.json4s" %% "json4s-jackson" % "3.6.9"
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")