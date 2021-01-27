import io.github.devatherock.util.ProcessUtil
import spock.lang.Specification
import spock.lang.Unroll

import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Logger

/**
 * Test class for SyncGitRepos.groovy
 */
class SyncGitReposSpec extends Specification {
    Handler handler = Mock()
    String currentBranch = System.getenv('CURRENT_BRANCH')
    String testBranch = 'unit-test'

    void setup() {
        System.setProperty('java.util.logging.SimpleFormatter.format', '%5$s%n')
        Logger.getLogger('').addHandler(handler)
        ProcessUtil.executeCommand("git checkout -b ${testBranch} a31b0b76d827e40563bebea5be783ddf1051a3bc")
        ProcessUtil.executeCommand("git push origin ${testBranch}")
        ProcessUtil.executeCommand("git checkout ${currentBranch}")
        ProcessUtil.executeCommand("git branch -D ${testBranch}")
    }

    void cleanup() {
        ProcessUtil.executeCommand("git push origin --delete ${testBranch}")
        ProcessUtil.executeCommand("git checkout ${currentBranch}")
        ProcessUtil.executeCommand("git branch -D ${testBranch}")
    }

    @Unroll
    void 'test git sync - start commit specified, test flag: #testFlag'() {
        given:
        def args = [
                '-sc', 'adf847c597651e89a20bb7bb7b2ab58f3463521f',
                '-tb', 'unit-test',
                '-n', 'devatherock',
                '-e', 'devatherock@gmail.com'
        ]
        if (testFlag) {
            args.add('--test')
        }

        when:
        SyncGitRepos.main(args as String[])

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry pick adf847c597651e89a20bb7bb7b2ab58f3463521f - exit code: 0')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry pick 24b33ddacbdbc9f654a2218c3ffbc97813658725 - exit code: 0')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry pick bd9972b6340fa312aaa1beb71e3e81fc2f4d341e - exit code: 0')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry pick 975ca93d1784646efed4351d22649fa053d80b1e - exit code: 0')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry pick 2038cf5cef1bb214c0235152d43a296faaab8b06 - exit code: 0')
        })

        where:
        testFlag << [true, false]
    }

    void 'test git sync - start commit specified, debug enabled'() {
        when:
        SyncGitRepos.main(['-sc', 'adf847c597651e89a20bb7bb7b2ab58f3463521f',
                           '-tb', 'unit-test',
                           '--debug', 'true',
                           '-n', 'devatherock',
                           '-e', 'devatherock@gmail.com'] as String[])

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Start commit: adf847c597651e89a20bb7bb7b2ab58f3463521f')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains("Target branch: ${testBranch}")
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry picking adf847c597651e89a20bb7bb7b2ab58f3463521f')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry pick adf847c597651e89a20bb7bb7b2ab58f3463521f - exit code: 0')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry picking 24b33ddacbdbc9f654a2218c3ffbc97813658725')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry pick 24b33ddacbdbc9f654a2218c3ffbc97813658725 - exit code: 0')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry picking bd9972b6340fa312aaa1beb71e3e81fc2f4d341e')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry pick bd9972b6340fa312aaa1beb71e3e81fc2f4d341e - exit code: 0')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry picking 975ca93d1784646efed4351d22649fa053d80b1e')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry pick 975ca93d1784646efed4351d22649fa053d80b1e - exit code: 0')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry picking 2038cf5cef1bb214c0235152d43a296faaab8b06')
        })

        then:
        1 * handler.publish({ LogRecord logRecord ->
            logRecord.message.contains('Cherry pick 2038cf5cef1bb214c0235152d43a296faaab8b06 - exit code: 0')
        })
    }
}
