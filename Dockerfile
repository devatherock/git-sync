FROM groovy:2.4-alpine

MAINTAINER 'Devaprasadh.Xavier <devatherock@gmail.com>'

ENV PLUGIN_TARGET_BRANCH master
ENV PLUGIN_DEBUG false

ADD SyncGitRepos.groovy /scripts/SyncGitRepos.groovy
ADD entry-point.sh /scripts/entry-point.sh

# Install git
USER root
RUN apk --update add git openssh

ENTRYPOINT sh /scripts/entry-point.sh