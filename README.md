# Avram

Avram is a metadata manager that allows analysts to create and manage [meta]data entries that follow 
flexible, user-defined models.

## Running tests
From avram root:
```
$ docker/run-postgres.sh start
$ sbt clean compile test
$ docker/run-postgres.sh stop
```