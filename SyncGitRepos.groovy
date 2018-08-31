import groovy.transform.Field

import java.util.logging.Logger
import java.util.regex.Pattern

System.setProperty('java.util.logging.SimpleFormatter.format', '%5$s%n')
Logger logger = Logger.getLogger('SyncGitRepos.log')
@Field Pattern whitespacePattern = Pattern.compile('\\s')
@Field int commitLimit = 100 // Number of commits to read

def cli = new CliBuilder(usage: 'groovy SyncGitRepos.groovy [options]')
cli.tr(longOpt: 'target_repo', args: 1, argName: 'target_repo', 'Target git repository')
cli.tb(longOpt: 'target_branch', args: 1, argName: 'target_branch', 'Target repository branch')

def options = cli.parse(args)
if(!options.tr) {
    cli.usage()
    System.exit(1)
}

// Add the target repository as another remote
String tempRemote = 'upstream'
"git remote add ${tempRemote} ${options.tr}".execute()

// Fetch all remote branches/tags
logger.info('git fetch --all'.execute().text.trim())

// Get source repository commits
def sourceCommits = getCommits()
logger.info({"Source commits: ${sourceCommits}".toString()})

// Switch to target branch
String targetBranch = options.tb ?: 'master'
logger.info("git checkout ${tempRemote}/${targetBranch}".execute().text.trim())

// Get target repository commits
def targetCommits = getCommits()
logger.info({"Target commits: ${targetCommits}".toString()})

// Find commits not in target
def diffCommits = sourceCommits.findAll { !targetCommits.contains(it) }
logger.info({"Commits not in target: ${diffCommits}".toString()})

if(diffCommits) {
    // Apply each missing to target
    // Reverse the list as the older commits have to be applied first
    diffCommits.reverse().each {
        logger.info("git cherry-pick ${it}".execute().text)
    }
}
else {
    logger.info({"Both repositories are in sync".toString()})
}

// Push the changes to target remote
logger.info("git push ${tempRemote} ${targetBranch}".execute().text.trim())

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

