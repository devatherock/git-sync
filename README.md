[![CircleCI](https://circleci.com/gh/devatherock/git-sync.svg?style=svg)](https://circleci.com/gh/devatherock/git-sync)
[![Docker Pulls - drone](https://img.shields.io/docker/pulls/devatherock/drone-git-sync.svg)](https://hub.docker.com/r/devatherock/drone-git-sync/)
[![Docker Pulls - vela](https://img.shields.io/docker/pulls/devatherock/vela-git-sync.svg)](https://hub.docker.com/r/devatherock/vela-git-sync/)
[![Docker Image Size](https://img.shields.io/docker/image-size/devatherock/vela-git-sync.svg?sort=date)](https://hub.docker.com/r/devatherock/vela-git-sync/)
[![Docker Image Layers](https://img.shields.io/microbadger/layers/devatherock/vela-git-sync.svg)](https://microbadger.com/images/devatherock/vela-git-sync)
# git-sync
drone.io/CircleCI/vela plugin to sync the contents of a git repository with another

# Usage
## CircleCI
### On push

```yaml
version: 2
jobs:
  sync:
    docker:
      - image: devatherock/git-sync:1.0.6
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
            - "ssh key fingerprint"                                                             # Fingerprint of SSH key with write access to target repository
      - run: sh /scripts/entry-point.sh

workflows:
  version: 2
  git-sync:
    jobs:
      - sync:
          filters:
            branches:
              only: master                                                                    # Source branch
```

### On tag
Change `filters` section to below:

```yaml
tags:
  only: /^v[0-9\.]+$/       # Regex to match tag pattern
```

Note: To add SSH key with write access to target repository, follow these
[instructions](https://circleci.com/docs/2.0/add-ssh-key/)

## drone.io and vela
Please refer [docs](DOCS.md)
