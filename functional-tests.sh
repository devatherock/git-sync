#!/bin/sh

mkdir git-sync-test
cp -r * git-sync-test
cp -r .git git-sync-test
cd git-sync-test
export CURRENT_BRANCH=$(git symbolic-ref --short HEAD)
git remote set-url origin "https://git-sync-token:$GIT_TOKEN@github.com/devatherock/git-sync.git"
./gradlew test --tests '*SyncGitReposDockerSpec*' -x jacocoTestCoverageVerification
exit_code=$?
cd ..
rm -rf git-sync-test
exit $exit_code