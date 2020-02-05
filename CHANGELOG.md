# Changelog
## 1.0.0
### 2018-08-31
- Initial version with script to sync two repositories and a `Dockerfile` for packaging
- Added circle-ci config for publishing to docker

### 2018-09-05
- Added an entry-point file
- Added flag to enable/disable debug logs

### 2018-09-08
- Updated readme with usage instructions

### 2018-09-28
- Used `alpine` image and installed `git` and `openssh` using `apk`

### 2018-09-29
- Added `License.txt`

### 2018-10-04
- Added PR check
- Upgraded docker image version to `17.12.1-ce`

## 1.0.1
### 2020-02-04
- Added logic to accept a starting commit sha as input. If it is not specified, the commit sha of the penultimate tag
will be used as the starting commit
- Packaged the script into a jar
- Declared `commons-cli` dependency explicitly for `script-jar` to include

## 1.0.2
### 2020-02-05
- Added support for HTTP git URI
- Published separate docker images for drone and circle ci