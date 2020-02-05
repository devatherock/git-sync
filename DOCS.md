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
### On push

```yaml
git-sync:
  when:
    branch: master                                            # Source branch
    event: push
  image: devatherock/git-sync:1.0.3
  target_repo: "git@bitbucket.org:xyz/my-target-repo.git"
  target_branch: master
  start_commit: 29186cd
  debug: false
```

### On tag

```yaml
git-sync:
  when:
    ref: refs/tags/v*
    event: tag
  image: devatherock/git-sync:1.0.3
  target_branch: prod
  secrets: [ git_sync_token ]
```