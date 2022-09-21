name := "notion-app"
version := "0.1"
scalaVersion := "2.13.8"

lazy val zioVersion = "2.0.2"
lazy val mongoVersion = "4.6.0"
lazy val zioJsonVersion = "0.1.5"
lazy val json4sVersion = "3.6.9"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion,
  "dev.zio" %% "zio-test-sbt" % zioVersion,
  "dev.zio" %% "zio-test-junit" % zioVersion,
  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "org.mongodb.scala" %% "mongo-scala-driver" % mongoVersion
)

assembly / assemblyJarName := s"$name-fatjar-$version.jar"

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
