#!/usr/bin/env bash
set -e
set -x

VAULT_TOKEN=$1
GIT_BRANCH=$2

#need to get the environment from the branch name
#if [ "$GIT_BRANCH" == "develop" ]; then
#	ENVIRONMENT="dev"
#elif [ "$GIT_BRANCH" == "alpha" ]; then
#        ENVIRONMENT="alpha"
#elif [ "$GIT_BRANCH" == "staging" ]; then
#	ENVIRONMENT="staging"
#elif [ "$GIT_BRANCH" == "master" ]; then
#	ENVIRONMENT="prod"
#else
#	echo "Unknown Git branch $GIT_BRANCH"
#	#exit 1
#fi



ENVIRONMENT="dev"

GOOGLE_PROJECT=broad-avram-$ENVIRONMENT

# pull the credentials for the service account

docker run --rm -e VAULT_TOKEN=$VAULT_TOKEN broadinstitute/dsde-toolbox vault read --format=json "secret/dsde/avram/$ENVIRONMENT/deploy-account.json" | jq .data > deploy_account.json


gcloud auth activate-service-account --key-file=deploy_account.json

# get appenginge sdk
curl 'https://storage.googleapis.com/appengine-sdks/featured/appengine-java-sdk-1.9.64.zip' > /tmp/appengine.zip
unzip /tmp/appengine.zip
export APPENGINE_SDK_HOME=$PWD/appengine-java-sdk-1.9.64

# deploy endpoints
gcloud endpoints services deploy $PWD/openapi.yaml --project=$GOOGLE_PROJECT

# deploy backend
sbt package
$PWD/appengine-java-sdk-1.9.64/bin/appcfg.sh --service_account_json_key_file=deploy_account.json update $PWD/target/webapp


##render the endpoints json and then deploy it
#docker run -v $PWD/startup.sh:/app/startup.sh \
#    -v $PWD/output:/output \
#    -v $PWD/deploy_account.json:/deploy_account.json \
#    -e GOOGLE_PROJECT=$GOOGLE_PROJECT \
#    ansingh7115/avram /bin/bash -c \
#    "gcloud auth activate-service-account --key-file=deploy_account.json; sbt appengineDeploy"
#
##SERVICE_VERSION in app.yaml needs to match this
##SERVICE_VERSION=`gcloud endpoints services describe $GOOGLE_PROJECT.appspot.com --format=json --project $GOOGLE_PROJECT | jq .serviceConfig.id` #todo: gcloud returns different response when calling as a service account and google doesn't know why
##SERVICE_VERSION=`date +%Y-%m-%d`r0 #todo: until google fixes the above gcloud command, we will use this. it will work a max of once per day (because it only uses r0 for each date)
#SERVICE_VERSION=ver1
#
##render config.ini and app.yaml for environment with SERVICE_VERSION and GOOGLE_PROJECT
#docker run -v $PWD:/app \
#  -e GOOGLE_PROJ=$GOOGLE_PROJECT \
#  -e SERVICE_VERSION=$SERVICE_VERSION \
#  -e INPUT_PATH=/app \
#  -e OUT_PATH=/app \
#  -e VAULT_TOKEN=$VAULT_TOKEN \
#  -e ENVIRONMENT=$ENVIRONMENT \
#  broadinstitute/dsde-toolbox render-templates.sh
#
##deploy the app to the specified project
#docker run -v $PWD/startup.sh:/app/startup.sh \
#    -v $PWD/app.yaml:/app/app.yaml \
#    -v $PWD/config.ini:/app/config.ini \
#    -v $PWD/deploy_account.json:/deploy_account.json \
#    -e GOOGLE_PROJECT=$GOOGLE_PROJECT \
#    ansingh7115/avram /bin/bash -c \
#    "gcloud auth activate-service-account --key-file=deploy_account.json; gcloud -q app deploy app.yaml --project=$GOOGLE_PROJECT"