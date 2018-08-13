FROM circleci/openjdk:8-jdk


USER root

RUN wget https://dl.google.com/dl/cloudsdk/channels/rapid/google-cloud-sdk.zip && unzip google-cloud-sdk.zip && rm google-cloud-sdk.zip
RUN google-cloud-sdk/install.sh --usage-reporting=true --path-update=true --bash-completion=true --rc-path=/.bashrc --additional-components app cloud-datastore-emulator
ENV PATH /google-cloud-sdk/bin:$PATH

RUN gcloud components update --quiet && gcloud components install beta --quiet

RUN mkdir -p /home/gcloud \
&& curl 'https://storage.googleapis.com/appengine-sdks/featured/appengine-java-sdk-1.9.63.zip' > /tmp/appengine.zip \
&& unzip -d /home/gcloud /tmp/appengine.zip \
&& rm /tmp/appengine.zip # \

#&& export APPENGINE_SDK_HOME=/home/gcloud/appengine-java-sdk-1.9.63
