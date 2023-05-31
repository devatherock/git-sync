#!/bin/sh

additional_gradle_args=$1

mkdir git-sync-test
cp -r * git-sync-test
cp -r .git git-sync-test
cd git-sync-test
export CURRENT_BRANCH=$(git symbolic-ref --short HEAD)
git remote set-url origin "https://git-sync-token:$GIT_TOKEN@github.com/devatherock/git-sync.git"
./gradlew test --tests '*SyncGitReposDockerSpec*' -x jacocoTestCoverageVerification ${additional_gradle_args}
exit_code=$?
cd ..
rm -rf git-sync-test
git remote set-url origin git@github.com:devatherock/git-sync.git
exit $exit_code