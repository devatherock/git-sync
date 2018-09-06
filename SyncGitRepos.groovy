import groovy.transform.Field

import java.util.logging.Logger
import java.util.regex.Pattern
import java.util.function.Supplier

System.setProperty('java.util.logging.SimpleFormatter.format', '%5$s%n')
@Field Logger logger = Logger.getLogger('SyncGitRepos.log')
@Field Pattern whitespacePattern = Pattern.compile('\\s')
@Field int commitLimit = 100 // Number of commits to read

def cli = new CliBuilder(usage: 'groovy SyncGitRepos.groovy [options]')
cli.tr(longOpt: 'target_repo', args: 1, argName: 'target_repo', 'Target git repository')
cli.tb(longOpt: 'target_branch', args: 1, argName: 'target_branch', 'Target repository branch')
cli._(longOpt: 'debug', args: 1, argName: 'debug', 'Flag to turn on debug logging')

def options = cli.parse(args)
if(!options.tr) {
    cli.usage()
    System.exit(1)
}

// Debug log flag
@Field boolean debug
debug = Boolean.parseBoolean(options.debug)

// Add the target repository as another remote
String tempRemote = 'upstream'
"git remote add ${tempRemote} ${options.tr}".execute()

// All remotes
debugLog({'git remote -v'.execute().text.trim()})

// Fetch all remote branches/tags
logger.info(executeCommand('git fetch --all').trim())

// Get source repository commits
def sourceCommits = getCommits()
debugLog({ "Source commits: ${sourceCommits}".toString() })

// Switch to target branch
String targetBranch = options.tb ?: 'master'
debugLog({ "Target branch: ${targetBranch}".toString() })
logger.info(executeCommand("git checkout ${tempRemote}/${targetBranch}").trim())
debugLog({"git branch".execute().text.trim()})

// Get target repository commits
def targetCommits = getCommits()
debugLog({"Target commits: ${targetCommits}".toString()})

// Find commits not in target
def diffCommits = sourceCommits.findAll { !targetCommits.contains(it) }
logger.info({"Commits not in target: ${diffCommits}".toString()})

if(diffCommits) {
    // Apply each missing to target
    // Reverse the list as the older commits have to be applied first
    diffCommits.reverse().each {
        logger.info("git cherry-pick ${it}".execute().text)
    }

    // Push the changes to target remote
    logger.info(executeCommand("git push ${tempRemote} ${targetBranch}").trim())
}
else {
    logger.info({"Both repositories are in sync".toString()})
}

/**
 * Gets the list of commits from the current remote/branch
 *
 * @return list of commits
 */
def getCommits() {
    def commits = []
	String commitText = "git log --pretty=oneline -n ${commitLimit}".execute().text.trim()

    // If any commits are present
    if(commitText) {
        commitText.split(System.lineSeparator()).each { commit ->
            commits.push(whitespacePattern.split(commit)[0])
        }
    }
    commits
}

/**
 * Executes a command and returns the response
 *
 * @param command
 * @return
 */
String executeCommand(String command) {
	Process process = command.execute()
    StringBuilder out = new StringBuilder()
    StringBuilder err = new StringBuilder()
	process.consumeProcessOutput( out, err )
	process.waitFor()

    return "${out}${System.properties['line.separator']}${err}"
}

/**
 * Log the provided message if debug is enabled
 *
 * @param message
 */
void debugLog(Supplier message) {
    if(debug) {
        logger.info(message)
    }
}