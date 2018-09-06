FROM groovy:2.4-jdk

MAINTAINER 'Devaprasadh.Xavier <devatherock@gmail.com>'

ENV PLUGIN_TARGET_BRANCH master
ENV PLUGIN_DEBUG false

ADD SyncGitRepos.groovy /scripts/SyncGitRepos.groovy
ADD entry-point.sh /scripts/entry-point.sh

ENTRYPOINT sh /scripts/entry-point.sh