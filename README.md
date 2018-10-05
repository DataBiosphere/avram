# Avram

Avram is a metadata manager that allows analysts to create and manage [meta]data entries that follow 
flexible, user-defined models.

## Running tests
From avram root:
```
$ docker/run-postgres.sh start
$ APPENGINE_SDK_HOME=/Applications/appengine-java-sdk-1.9.65/ sbt clean compile test
$ docker/run-postgres.sh stop
```

## Setting up local dev env

1. Download the [appengine java sdk](https://cloud.google.com/appengine/docs/standard/java/download) and follow the instructions for setting your path.
2. Setup the path to the appengine sdk by doing one of the following:
	- set the environment variable: `APPENGINE_SDK_HOME`
	- create the file appengine.properties in the avram directory and set the variable `sdkHome`
3. run `docker run -v $PWD:/app -e GOOGLE_PROJ=broad-avram-dev -eSERVICE_VERSION=br1 -e INPUT_PATH=/app -e OUT_PATH=/app/src/main/resources -e VAULT_TOKEN=$(cat ~/.vault-token) -e ENVIRONMENT=dev broadinstitute/dsde-toolbox render-templates.sh`
4. run `sbt`, then in sbt run `appengineDevServer`
5. [test an endpoint locally](http://localhost:8080/avram/v1/ping)


## On the dev environment
Avram is in its own google project: broad-avram-dev.

To deploy to dev run the deploy.sh script

To execute one of the endpoints on the dev environment use [openapi](https://endpointsportal.broad-avram-dev.cloud.goog/)

