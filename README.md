[![CircleCI](https://circleci.com/gh/devatherock/git-sync.svg?style=svg)](https://circleci.com/gh/devatherock/git-sync)
[![Version](https://img.shields.io/docker/v/devatherock/git-sync?sort=semver)](https://hub.docker.com/r/devatherock/git-sync/)
[![Coverage Status](https://coveralls.io/repos/github/devatherock/git-sync/badge.svg?branch=master)](https://coveralls.io/github/devatherock/git-sync?branch=master)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/c09d2da01eba4895b7d4709880e5c548)](https://www.codacy.com/gh/devatherock/git-sync/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=devatherock/git-sync&amp;utm_campaign=Badge_Grade)
[![Docker Pulls](https://img.shields.io/docker/pulls/devatherock/vela-git-sync.svg)](https://hub.docker.com/r/devatherock/git-sync/)
[![Docker Image Size](https://img.shields.io/docker/image-size/devatherock/git-sync.svg?sort=date)](https://hub.docker.com/r/devatherock/git-sync/)
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
  devatherock/git-sync:1.1.0
```

### CI
#### Config
The following parameters/secrets can be set to configure the plugin.

**Parameters**
* **target_repo** - Git URI of target repository. If not specified, same as the source repo
* **target_branch** - Branch to sync to, in target repository. Optional, defaults to master
* **start_commit** - Commit sha of the first commit to sync. All commits after that will be synced. If not specified,
  commit sha of the penultimate tag will be used. And if no tags are present, all commits up to the 100th will be synced
* **debug** - Flag to enable debug logs. Optional, by default, debug logs are disabled

**Secrets**

The following secret values can be set to configure the plugin.

* **GIT_SYNC_TOKEN** - Github API token with push access to the repository. Required if HTTP URI of target repository
  is used

#### drone.io

```yaml
git-sync:
  when:
    ref: refs/tags/v*
    event: tag
  image: devatherock/git-sync:1.1.0
  target_branch: prod
  secrets: [ git_sync_token ]
```

#### vela

```yaml
steps:
  - name: git-sync
    ruleset:
      tag: refs/tags/v*
      event: tag
    image: devatherock/git-sync:1.1.0
    secrets: [ git_sync_token ]
    parameters:
      target_branch: prod
```

#### CircleCI
To add SSH key with write access to target repository, follow these [instructions](https://circleci.com/docs/2.0/add-ssh-key/)

```yaml
version: 2
jobs:
  sync:
    docker:
      - image: devatherock/git-sync:1.1.0
    working_directory: ~/my-source-repo
    environment:
      PLUGIN_TARGET_REPO: "git@bitbucket.org:xyz/my-target-repo.git"                        # Git URI of target repository. If not specified, same as the source repo
      PLUGIN_TARGET_BRANCH: master                                                          # Branch to sync to in target repository. Optional, defaults to master
      PLUGIN_START_COMMIT: 29186cd                                                          # Commit sha of the first commit to sync. All commits after that will be synced. If not specified, commit sha of the penultimate tag will be used. And if no tags are present, all commits up to the 100th will be synced
      PLUGIN_DEBUG: false                                                                   # Flag to enable debug logs. Optional, by default, debug logs are disabled
      GIT_SYNC_TOKEN: xyz                                                                   # Github API token with push access to the repository. Required if HTTP URI of target repository is used
    steps:
      - checkout
      - add_ssh_keys:
          fingerprints:
            - "ssh key fingerprint"                                                         # Fingerprint of SSH key with write access to target repository
      - run: sh /scripts/entry-point.sh
```


## Tests
To test the latest plugin image, run the below command
```shell
sh functional-tests.sh
```
