#!/usr/bin/env bash
set -e
set -x

VAULT_TOKEN=$1
GIT_BRANCH=$2
TARGET_ENV=$3

set +x
if [ -z "$TARGET_ENV" ]; then
    echo "TARGET_ENV argument not supplied; inferring from GIT_BRANCH '$GIT_BRANCH'."

    if [ "$GIT_BRANCH" == "develop" ]; then
        TARGET_ENV="dev"
    elif [ "$GIT_BRANCH" == "alpha" ]; then
        TARGET_ENV="alpha"
    elif [ "$GIT_BRANCH" == "staging" ]; then
        TARGET_ENV="staging"
    elif [ "$GIT_BRANCH" == "master" ]; then
        TARGET_ENV="prod"
    else
        echo "Git branch '$GIT_BRANCH' is not configured to automatically deploy to a target environment"
        exit 1
    fi
fi

if [[ "$TARGET_ENV" =~ ^(dev|alpha|staging|prod)$ ]]; then
    ENVIRONMENT=${TARGET_ENV}
else
    echo "Unknown environment: $TARGET_ENV - must be one of [dev, alpha, staging, prod]"
    exit 1
fi

echo "Deploying branch '${GIT_BRANCH}' to ${ENVIRONMENT}"
set -x

GOOGLE_PROJECT=broad-avram-$ENVIRONMENT

docker pull ansingh7115/avram

# pull the credentials for the service account
docker run --rm -e VAULT_TOKEN=$VAULT_TOKEN broadinstitute/dsde-toolbox vault read --format=json "secret/dsde/avram/$ENVIRONMENT/deploy-account.json" | jq .data > deploy_account.json

# deploy endpoints
docker run \
    -v $PWD:/app \
    ansingh7115/avram /bin/bash -c \
    "cd /app; gcloud auth activate-service-account --key-file=deploy_account.json && gcloud endpoints services deploy /app/openapi.yaml --project=$GOOGLE_PROJECT"

docker run -v $PWD:/app \
    -e GOOGLE_PROJ=$GOOGLE_PROJECT \
    -e SERVICE_VERSION=1 \
    -e INPUT_PATH=/app \
    -e OUT_PATH=/app/src/main/resources \
    -e VAULT_TOKEN=$VAULT_TOKEN \
    -e ENVIRONMENT=$ENVIRONMENT \
    broadinstitute/dsde-toolbox render-templates.sh

# build app engine app
docker run \
    -v $PWD:/app \
    ansingh7115/avram /bin/bash -c \
    "cd /app; APPENGINE_SDK_HOME=/home/gcloud/appengine-java-sdk-1.9.67 sbt package"

# deploy app engine app
docker run \
    -v $PWD:/app \
    ansingh7115/avram /bin/bash -c \
    "/home/gcloud/appengine-java-sdk-1.9.67/bin/appcfg.sh --service_account_json_key_file=/app/deploy_account.json update /app/target/webapp"