# Changelog

## [Unreleased]
### Added
- Functional tests to CI pipeline

### Changed
- fix: Fixed a few Codacy code violations

### Removed
- [#7](https://github.com/devatherock/git-sync/issues/20): Publishing of `drone-git-sync` and `vela-git-sync` images

## [1.1.0] - 2021-01-27
### Changed
- test([#9](https://github.com/devatherock/git-sync/issues/9)): Added unit tests
- feat([#5](https://github.com/devatherock/git-sync/issues/5)): Used one dockerfile to simplify build and release

## [1.0.7] - 2021-01-16
### Changed
- [#7](https://github.com/devatherock/git-sync/issues/7): Changed the order in which commits are applied.
This prevents commits from different tracks from getting mixed together

## [1.0.6] - 2020-06-11
### Changed
- A log statement which was causing runtime exception during `GString` to `String` conversion

## [1.0.5] - 2020-05-26
### Changed
- Fixed the bug due to which last commit was skipped

## [1.0.4] - 2020-04-12
### Changed
- [Issue 3](https://github.com/devatherock/git-sync/issues/3) - Made the plugin
compatible with vela

## [1.0.3] - 2020-02-05
### Changed
- Configured git user email to be the email address from drone

## [1.0.2] - 2020-02-05
### Added
- Support for HTTP git URI

### Changed
- Published separate docker images for drone and circle ci

## [1.0.1] - 2020-02-04
### Added
- Logic to accept a starting commit sha as input. If it is not specified, the commit sha of the penultimate tag
will be used as the starting commit

### Changed
- Packaged the script into a jar
- Declared `commons-cli` dependency explicitly for `script-jar` to include

## [1.0.0] - 2018-10-04
### Added
- Initial version with script to sync two repositories and a `Dockerfile` for packaging
- Circle-ci config for publishing to docker
- An entry-point file
- A flag to enable/disable debug logs
- A `License.txt` file
- PR check

### Changed
- Updated readme with usage instructions
- Used `alpine` image and installed `git` and `openssh` using `apk`
- Upgraded docker image version to `17.12.1-ce`
