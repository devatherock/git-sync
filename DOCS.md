## Config
The following parameters/secrets can be set to configure the plugin.

### Parameters
* **target_repo** - Git URI of target repository. If not specified, same as the source repo
* **target_branch** - Branch to sync to, in target repository. Optional, defaults to master
* **start_commit** - Commit sha of the first commit to sync. All commits after that will be synced. If not specified,
commit sha of the penultimate tag will be used. And if no tags are present, all commits up to the 100th will be synced
* **debug** - Flag to enable debug logs. Optional, by default, debug logs are disabled

### Secrets

The following secret values can be set to configure the plugin.

* **GIT_SYNC_TOKEN** - Github API token with push access to the repository. Required if HTTP URI of target repository
is used

## Examples
### drone.io

```yaml
git-sync:
  when:
    ref: refs/tags/v*
    event: tag
  image: devatherock/drone-git-sync:latest
  target_branch: prod
  secrets: [ git_sync_token ]
```

### vela

```yaml
steps:
  - name: git-sync
    ruleset:
      tag: refs/tags/v*
      event: tag
    image: devatherock/vela-git-sync:latest
    secrets: [ git_sync_token ]
    parameters:
      target_branch: prod
```

### CircleCI
To add SSH key with write access to target repository, follow these [instructions](https://circleci.com/docs/2.0/add-ssh-key/)

```yaml
version: 2
jobs:
  sync:
    docker:
      - image: devatherock/git-sync:latest
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
```