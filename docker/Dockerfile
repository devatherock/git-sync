FROM amazoncorretto:8u372-alpine3.14-jre

LABEL maintainer="devatherock@gmail.com>"

# Install git - Set the user as root for apk
USER root
RUN apk --update add git openssh

COPY SyncGitRepos.jar /scripts/
COPY docker/entry-point.sh /scripts/

ENTRYPOINT sh /scripts/entry-point.sh