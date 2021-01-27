export CURRENT_BRANCH=$(git symbolic-ref --short HEAD)
./gradlew test --tests '*SyncGitReposSpec*' coveralls
exit $?