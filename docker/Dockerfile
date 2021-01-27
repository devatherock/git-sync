FROM openjdk:8-jre-alpine

LABEL maintainer="devatherock@gmail.com>"

# Install git - Set the user as root for apk
USER root
RUN apk --update add git openssh

ADD SyncGitRepos.jar /scripts/SyncGitRepos.jar
ADD docker/entry-point.sh /scripts/entry-point.sh

ENTRYPOINT sh /scripts/entry-point.sh