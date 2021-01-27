[![CircleCI](https://circleci.com/gh/devatherock/git-sync.svg?style=svg)](https://circleci.com/gh/devatherock/git-sync)
[![Version](https://img.shields.io/docker/v/devatherock/vela-git-sync?sort=semver)](https://hub.docker.com/r/devatherock/vela-git-sync/)
[![Coverage Status](https://coveralls.io/repos/github/devatherock/git-sync/badge.svg?branch=master)](https://coveralls.io/github/devatherock/git-sync?branch=master)
[![Docker Pulls](https://img.shields.io/docker/pulls/devatherock/vela-git-sync.svg)](https://hub.docker.com/r/devatherock/vela-git-sync/)
[![Docker Image Size](https://img.shields.io/docker/image-size/devatherock/vela-git-sync.svg?sort=date)](https://hub.docker.com/r/devatherock/vela-git-sync/)
[![Docker Image Layers](https://img.shields.io/microbadger/layers/devatherock/vela-git-sync.svg)](https://microbadger.com/images/devatherock/vela-git-sync)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
# git-sync
CI plugin to sync the contents of a git repository with another

## Usage
### Docker

```shell
docker run --rm \
  -e PLUGIN_DEBUG=true \
  -e PLUGIN_START_COMMIT=29186cd \
  -e PLUGIN_TARGET_BRANCH=test \
  -v path/to/repo:/repo \
  -w=/repo \
  devatherock/drone-git-sync:latest
```

### CI
Please refer [docs](DOCS.md)

## Tests
To test the latest plugin images, run the below command
```shell
sh functional-tests.sh
```
