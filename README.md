# avram

Entity Service



## Running tests
From avram root:
```
$ docker/run-postgres.sh start
$ sbt clean compile test
$ docker/run-postgres.sh stop
```