package io.github.devatherock.gitsync.docker

import io.github.devatherock.util.ProcessUtil

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Test class to test the built docker images
 */
class SyncGitReposDockerSpec extends Specification {
    @Shared
    def config = [
            'drone'   : [
                    'envPrefix'  : 'PLUGIN_',
                    'emailVar'   : 'DRONE_COMMIT_AUTHOR_EMAIL',
                    'usernameVar': 'DRONE_COMMIT_AUTHOR'
            ],
            'vela'    : [
                    'envPrefix'  : 'PARAMETER_',
                    'emailVar'   : 'BUILD_AUTHOR_EMAIL',
                    'usernameVar': 'BUILD_AUTHOR'
            ],
            'circleci': [
                    'envPrefix'  : 'PLUGIN_',
                    'emailVar'   : 'CIRCLE_USERNAME',
                    'usernameVar': 'CIRCLE_USERNAME'
            ]
    ]

    @Shared
    String dockerImage = 'devatherock/git-sync:latest'

    String currentBranch = System.getenv('CURRENT_BRANCH')
    String testBranch = 'func-test'

    void setupSpec() {
        System.setProperty('java.util.logging.SimpleFormatter.format', '%5$s%n')

        if (!Boolean.getBoolean('skip.pull')) {
            ProcessUtil.executeCommand("docker pull ${dockerImage}")
        }
    }

    void setup() {
        ProcessUtil.executeCommand("git checkout -b ${testBranch} a31b0b76d827e40563bebea5be783ddf1051a3bc")
        ProcessUtil.executeCommand("git push origin ${testBranch}")
        ProcessUtil.executeCommand("git checkout -f ${currentBranch}")
        ProcessUtil.executeCommand("git branch -D ${testBranch}")
    }

    void cleanup() {
        ProcessUtil.executeCommand("git push origin --delete ${testBranch}")
        ProcessUtil.executeCommand("git checkout -f ${currentBranch}")
        ProcessUtil.executeCommand("git branch -D ${testBranch}")
        ProcessUtil.executeCommand('git remote rm upstream')
    }

    @Unroll
    void 'test git sync - start commit specified, ci: #ci, debug: #debugEnabled'() {
        when:
        def output = ProcessUtil.executeCommand(['docker', 'run', '--rm',
                                                 '-v', "${System.properties['user.dir']}:/git-sync",
                                                 '-w=/git-sync',
                                                 '-e', "${config[ci].envPrefix}DEBUG=${debugEnabled}",
                                                 '-e', "${config[ci].envPrefix}START_COMMIT=adf847c597651e89a20bb7bb7b2ab58f3463521f",
                                                 '-e', "${config[ci].envPrefix}TARGET_BRANCH=${testBranch}",
                                                 '-e', "${config[ci].envPrefix}TARGET_REPO=https://github.com/devatherock/git-sync.git",
                                                 '-e', "GIT_SYNC_TOKEN=${System.getenv('GIT_TOKEN')}",
                                                 '-e', "${config[ci].emailVar}=devatherock@gmail.com",
                                                 '-e', "${config[ci].usernameVar}=devatherock",
                                                 dockerImage])

        then:
        output[0] == 0
        output[1].contains('Start commit: adf847c597651e89a20bb7bb7b2ab58f3463521f') == debugEnabled
        output[1].contains("Target branch: ${testBranch}") == debugEnabled
        output[1].contains("Cherry picking adf847c597651e89a20bb7bb7b2ab58f3463521f") == debugEnabled
        output[1].contains('Cherry pick adf847c597651e89a20bb7bb7b2ab58f3463521f - exit code: 0')
        output[1].contains("Cherry picking 24b33ddacbdbc9f654a2218c3ffbc97813658725") == debugEnabled
        output[1].contains('Cherry pick 24b33ddacbdbc9f654a2218c3ffbc97813658725 - exit code: 0')
        output[1].contains("Cherry picking bd9972b6340fa312aaa1beb71e3e81fc2f4d341e") == debugEnabled
        output[1].contains('Cherry pick bd9972b6340fa312aaa1beb71e3e81fc2f4d341e - exit code: 0')
        output[1].contains("Cherry picking 975ca93d1784646efed4351d22649fa053d80b1e") == debugEnabled
        output[1].contains('Cherry pick 975ca93d1784646efed4351d22649fa053d80b1e - exit code: 0')
        output[1].contains("Cherry picking 2038cf5cef1bb214c0235152d43a296faaab8b06") == debugEnabled
        output[1].contains('Cherry pick 2038cf5cef1bb214c0235152d43a296faaab8b06 - exit code: 0')

        where:
        ci << [
                'drone', 'vela', 'circleci',
                'drone', 'vela', 'circleci'
        ]
        debugEnabled << [
                false, false, false,
                true, true, true
        ]
    }
}
