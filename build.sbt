name := "notion-app"
version := "0.1"
scalaVersion := "2.13.8"

lazy val zioVersion = "2.0.1"
lazy val mongoVersion = "4.7.1"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion,
  "dev.zio" %% "zio-test-sbt" % zioVersion,
  "dev.zio" %% "zio-test-junit" % zioVersion,
  "org.mongodb.scala" %% "mongo-scala-driver" % mongoVersion

)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")