FROM circleci/openjdk:8-jdk


USER root


RUN mkdir -p /home/gcloud \
&& curl 'https://storage.googleapis.com/appengine-sdks/featured/appengine-java-sdk-1.9.63.zip' > /tmp/appengine.zip \
&& unzip -d /home/gcloud /tmp/appengine.zip \
&& rm /tmp/appengine.zip \
&& export APPENGINE_SDK_HOME=/home/gcloud/appengine-java-sdk-1.9.63
