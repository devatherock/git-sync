@Grab(group = 'commons-cli', module ='commons-cli', version = '1.4')
@Grab(group = 'org.codehaus.groovy', module = 'groovy-cli-commons', version = '2.5.7')

import groovy.transform.Field
import groovy.cli.commons.CliBuilder

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
cli.sc(longOpt: 'start_commit', args: 1, argName: 'start_commit', 'First commit to pick from the source branch')
cli.t(longOpt: 'token', args: 1, argName: 'token', 'Github access token')
cli.n(longOpt: 'username', args: 1, argName: 'username', 'Username of last committer')
cli.e(longOpt: 'email', args: 1, argName: 'email', 'Email address of last committer')
cli._(longOpt: 'debug', args: 1, argName: 'debug', 'Flag to turn on debug logging')
cli._(longOpt: 'test', args: 0, argName: 'test', 'Flag to test changes without committing to git')

def options = cli.parse(args)

// Debug log flag
@Field boolean debug
debug = options.debug ? Boolean.parseBoolean(options.debug) : false

// Indicates if the remote is a HTTP endpoint
boolean isHttp = false

// Add the target repository as another remote
String targetRemote = null
String targetRemoteUrl = options.tr ?: null
if(targetRemoteUrl) {
    targetRemote = 'upstream'
    isHttp = targetRemoteUrl.startsWith('http')

    if(!isHttp) {
        "git remote add ${targetRemote} ${targetRemoteUrl}".execute()
    }
}
else {
    targetRemote = 'origin'
    targetRemoteUrl = 'git remote get-url --push origin'.execute().text
    isHttp = targetRemoteUrl.startsWith('http')
}

if(isHttp) {
    // If remote is http, git token has to be supplied
    if(!options.t) {
        logger.severe("Http remote '${targetRemoteUrl}' used but git token not supplied")
        System.exit(1)
    }
    else {
        int urlIndex = targetRemoteUrl.indexOf('://')
        String httpScheme = targetRemoteUrl.substring(0, urlIndex)
        String urlSansScheme = targetRemoteUrl.substring(urlIndex + 3)
        debugLog({ "Http scheme: ${httpScheme}".toString() })
        debugLog({ "URL without scheme: ${urlSansScheme}".toString() })

        targetRemote = 'upstream'
        logger.info(executeCommand(
                "git remote add ${targetRemote} ${httpScheme}://${options.t}:${options.t}@${urlSansScheme}")[0])
    }
}

// Configure git username if supplied
if(options.n) {
    debugLog( { "User name: ${options.n}".toString() } )
    executeCommand("git config user.name ${options.n}")
}

// Configure git user email if supplied
if(options.e) {
    debugLog( { "User email: ${options.e}".toString() } )
    executeCommand("git config user.email ${options.e}")
}

// All remotes
debugLog({'git remote -v'.execute().text.trim()})

// Fetch all remote branches/tags
logger.info(executeCommand('git fetch --all')[0].trim())

// Get source repository commits
String startingCommit = null
def sourceCommits = getCommits(startingCommit)
if(options.sc) {
    startingCommit = options.sc
    sourceCommits = getCommits(startingCommit)
}
else {
    // Get the commit from the penultimate release/tag
    String allTags = "git for-each-ref refs/tags --sort=-taggerdate --format=%(objectname) --count=2".execute().text
    debugLog({ "All tags: ${allTags}".toString() })

    if(allTags) {
        String[] allTagsArray = allTags.trim().split(System.lineSeparator())
        if(allTagsArray.length > 1) {
            String previousTagCommit = allTagsArray[allTagsArray.length - 1]
            debugLog({ "Previous tag commit: ${previousTagCommit}".toString() })

            sourceCommits = getCommits(previousTagCommit)
            sourceCommits.remove(0) // Remove first commit as it would have already been merged
            startingCommit = sourceCommits[0]
        }
    }
}
debugLog({ "Start commit: ${startingCommit}".toString() })
debugLog({ "Source commits: ${sourceCommits}".toString() })

// Switch to target branch
String targetBranch = options.tb ?: 'master'
debugLog({ "Target branch: ${targetBranch}".toString() })

debugLog({"git branch".execute().text.trim()})
logger.info(executeCommand("git checkout -b ${targetBranch} ${targetRemote}/${targetBranch}")[0].trim())
debugLog({"git branch".execute().text.trim()})

// Apply the commits
sourceCommits.each { commit ->
    debugLog({"Cherry picking ${commit}".toString()})
    def cherryPickOutput = executeCommand("git cherry-pick ${commit}")
    logger.info({"Cherry pick ${commit} - exit code: ${cherryPickOutput[1]} ${cherryPickOutput[0]}".toString()})
}

// Push the changes to target remote
if(!options.test) {
    logger.info(executeCommand("git push ${targetRemote} ${targetBranch}")[0].trim())
}


/**
 * Gets the list of commits from the current remote/branch
 *
 * @param startingCommit
 * @return list of commits
 */
def getCommits(String startingCommit) {
    def commits = []
	String commitText = "git log --pretty=oneline -n ${commitLimit} --topo-order".execute().text.trim()

    // If any commits are present
    if(commitText) {
        String[] allCommits = commitText.split(System.lineSeparator())

        int index = allCommits.length - 1
        String commitLine = null
        String commit = null
        boolean addCommit = startingCommit ? false : true // If starting commit is not specified, add all commits

        while(index > -1) {
            commitLine = allCommits[index]
            commit = whitespacePattern.split(commitLine)[0]

            if(addCommit) {
                commits.add(commit)
            }
            else if(commit == startingCommit) {
                addCommit = true
                commits.add(commit)
            }
            index--
        }
    }
    commits
}

/**
 * Executes a command and returns the response
 *
 * @param command
 * @return command output
 */
List executeCommand(String command) {
    Process process = command.execute()
    StringBuilder out = new StringBuilder()
    StringBuilder err = new StringBuilder()
    process.consumeProcessOutput( out, err )
    process.waitFor()

    return [ "${out}${System.properties['line.separator']}${err}", process.exitValue() ]
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