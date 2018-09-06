FROM groovy:2.4-jdk

MAINTAINER 'Devaprasadh.Xavier <devatherock@gmail.com>'

ENV PLUGIN_TARGET_BRANCH master

ADD SyncGitRepos.groovy /scripts/SyncGitRepos.groovy
ADD entry-point.sh /scripts/entry-point.sh
LABEL com.circleci.preserve-entrypoint=true

ENTRYPOINT groovy /scripts/SyncGitRepos.groovy -tr "$PLUGIN_TARGET_REPO" -tb "$PLUGIN_TARGET_BRANCH"