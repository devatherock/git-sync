FROM openjdk:8-jre-alpine

MAINTAINER 'Devaprasadh.Xavier <devatherock@gmail.com>'

ENV PLUGIN_TARGET_BRANCH master
ENV PLUGIN_DEBUG false

ADD SyncGitRepos.jar /scripts/SyncGitRepos.jar
ADD docker/circle-entry-point.sh /scripts/entry-point.sh

# Install git - Set the user as root for apk
USER root
RUN apk --update add git openssh

ENTRYPOINT sh /scripts/entry-point.sh