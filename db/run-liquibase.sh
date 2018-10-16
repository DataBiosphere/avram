#!/usr/bin/env bash

# Right now this is just running liquibase but we'll want to put our liquibase.properties template rendering in here later.
mvn liquibase:update -e