# MyNoteApp
Console note tacking application using ZIO

[![Build Status](https://travis-ci.org/knoldus/scalajobz.png?branch=master)](https://travis-ci.org/MyNoteApp) for Travis CI passing

### Running the application locally
Start MongoDB Container
```
$ cd docker
$ docker compose up
```

Mongo Shell
```
$ mongosh "mongodb://localhost:27017"
```

On Mac you can install `mongosh` using: `brew install mongosh`

Building the application:
```
$ sbt clean assembly
```

Running the application:
```
$ java -jar target/scala-2.13/MyNoteApp-assembly-0.1.jar
```

