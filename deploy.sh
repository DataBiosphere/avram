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
elif [ "$GIT_BRANCH" == "gawb3639" ]; then
	ENVIRONMENT="dev"
else
	echo "Unknown Git branch $GIT_BRANCH"
	#exit 1
fi

GOOGLE_PROJECT=broad-avram-$ENVIRONMENT
SERVICE_VERSION=version1

# pull the credentials for the service account 
docker run --rm -e VAULT_TOKEN=$VAULT_TOKEN broadinstitute/dsde-toolbox vault read --format=json "secret/dsde/avram/$ENVIRONMENT/deploy-account.json" | jq .data > deploy_account.json

# Auth with deploy service account
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