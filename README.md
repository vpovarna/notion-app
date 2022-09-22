# MyNoteApp
Note tacking application using ZIO

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

