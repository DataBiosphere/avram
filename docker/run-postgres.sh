#!/usr/bin/env bash

# The CloudSQL console simply states "PostgreSQL 9.6" so we may not match the minor version number
POSTGRES_VERSION=9.6
start() {
    echo "attempting to remove old $CONTAINER container..."
    docker rm -f $CONTAINER || echo "docker rm failed. nothing to rm."

    # start up PostgreSQL
    echo "starting up postgres container..."
    docker run --name $CONTAINER -e POSTGRES_USER=avram -e POSTGRES_PASSWORD=test -e POSTGRES_DB=testdb -d -p 5432:5432 circleci/postgres:$POSTGRES_VERSION-alpine-ram
}

stop() {
    echo "Stopping docker $CONTAINER container..."
    docker stop $CONTAINER || echo "$CONTAINER stop failed. container already stopped."
    docker rm -v $CONTAINER || echo "$CONTAINER rm -v failed.  container already destroyed."
}

CONTAINER=postgres-avram
COMMAND=$1

if [ ${#@} == 0 ]; then
    echo "Usage: $0 stop|start"
    exit 1
fi

if [ $COMMAND = "start" ]; then
    start
elif [ $COMMAND = "stop" ]; then
    stop
else
    exit 1
fi
