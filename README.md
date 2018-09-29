[![CircleCI](https://circleci.com/gh/devaprasadh/git-sync.svg?style=svg)](https://circleci.com/gh/devaprasadh/git-sync)
[![Docker Pulls](https://img.shields.io/docker/pulls/devatherock/git-sync.svg)](https://hub.docker.com/r/devatherock/git-sync/)
# git-sync
drone.io/CircleCI plugin to sync the contents of a git repository with another

# Usage
## CircleCI
### On push

```yaml
version: 2
jobs:
  sync:
    docker:
    - image: devatherock/git-sync:1.0.0
    working_directory: ~/my-source-repo
    environment:
      PLUGIN_TARGET_REPO: "git@bitbucket.org:xyz/my-target-repo.git"                        # SSH git URI of target repository 
      PLUGIN_TARGET_BRANCH: master                                                          # Branch to sync to in target repository. Optional, defaults to master
      PLUGIN_DEBUG: false                                                                   # Flag to enable debug logs. Optional, by default, debug logs are disabled
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

## drone.io
```yaml
pre-release:
  when:
    branch: master                                            # Source branch
    event: push                                               # push, tag, etc
  image: devatherock/git-sync:1.0.0
  target_repo: "git@bitbucket.org:xyz/my-target-repo.git"     # SSH git URI of target repository 
  trget_branch: master                                        # Branch to sync to in target repository. Optional, defaults to master
  debug: false                                                # Flag to enable debug logs. Optional, by default, debug logs are disabled 
```