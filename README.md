# Avram

Avram is a metadata manager that allows analysts to create and manage [meta]data entries that follow 
flexible, user-defined models.

## Setup

To install git-secrets
```
$ brew install git-secrets
```
To ensure git hooks are run
```
$ cp -r hooks/ .git/hooks/
$ chmod 755 .git/hooks/apply-git-secrets.sh
```


## Running tests
From avram root:
```
$ docker/run-postgres.sh start
$ sbt clean compile test
$ docker/run-postgres.sh stop
```