DOCKER_TAG=latest

clean:
	./gradlew clean
test:
	sh unit-tests.sh	
functional-test:
	sh functional-tests.sh
jar-build:
	docker run --rm \
	-v $(CURDIR):/work \
	-w=/work \
	-e PARAMETER_SCRIPT_PATH=SyncGitRepos.groovy \
	-e PARAMETER_OUTPUT_FILE=SyncGitRepos.jar \
	devatherock/scriptjar:1.0.0
docker-build:
	docker build -t devatherock/git-sync:$(DOCKER_TAG) -f docker/Dockerfile .