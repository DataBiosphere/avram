#!/usr/bin/env bash
set -e
set -x

VAULT_TOKEN=$1
GIT_BRANCH=$2

#need to get the environment from the branch name
if [ "$GIT_BRANCH" == "develop" ]; then
	ENVIRONMENT="dev"
elif [ "$GIT_BRANCH" == "alpha" ]; then
        ENVIRONMENT="alpha"
elif [ "$GIT_BRANCH" == "staging" ]; then
	ENVIRONMENT="staging"
elif [ "$GIT_BRANCH" == "master" ]; then
	ENVIRONMENT="prod"
else
	echo "Unknown Git branch $GIT_BRANCH"
	#exit 1
fi

GOOGLE_PROJECT=broad-avram-$ENVIRONMENT
SERVICE_VERSION=version1

# pull the credentials for the service account
docker run --rm -e VAULT_TOKEN=$VAULT_TOKEN broadinstitute/dsde-toolbox vault read --format=json "secret/dsde/avram/$ENVIRONMENT/deploy-account.json" | jq .data > deploy_account.json

# deploy endpoints
docker run \
    -v $PWD:/app \
    ansingh7115/avram /bin/bash -c \
    "cd /app; gcloud auth activate-service-account --key-file=deploy_account.json && gcloud endpoints services deploy /app/openapi.yaml --project=$GOOGLE_PROJECT"

# build app engine app
docker run \
    -v $PWD:/app \
    ansingh7115/avram /bin/bash -c \
    "cd /app; APPENGINE_SDK_HOME=/home/gcloud/appengine-java-sdk-1.9.64 sbt package"

# deploy app engine app
docker run \
    -v $PWD:/app \
    ansingh7115/avram /bin/bash -c \
    "/home/gcloud/appengine-java-sdk-1.9.63/bin/appcfg.sh --service_account_json_key_file=/app/deploy_account.json update /app/target/webapp"