FROM amazoncorretto:8u372-alpine3.14-jre

LABEL maintainer="devatherock@gmail.com>"

# Set the user as root for apk
USER root
RUN apk --update add openssh

# Install git. Pinned git version to get around the safe.directory check
RUN apk add git=2.26.3-r1 --repository=http://dl-cdn.alpinelinux.org/alpine/v3.12/main

COPY SyncGitRepos.jar /scripts/
COPY docker/entry-point.sh /scripts/

ENTRYPOINT sh /scripts/entry-point.sh