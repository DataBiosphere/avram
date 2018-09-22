#!/usr/bin/env bash

# Right now this is 1 line because all we want to is update the db, but there might be other deploy steps
mvn liquibase:update -e