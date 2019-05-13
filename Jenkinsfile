def repoName    = "git@github.com:mshenhera/test_multibranch_pipeline.git"
def fileVersion = "VERSION.txt"

// Python Local version identifiers supports only:
// ASCII letters ([a-zA-Z]), ASCII digits ([0-9]), periods (.)
// https://www.python.org/dev/peps/pep-0440/#local-version-identifiers
def normalizeVersion(version) {
    return version.replaceAll(/[\W_]/, ".")
}

def generateVersion() {
    gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
    // short SHA, possibly better for chat notifications, etc.
    shortCommit = gitCommit.take(7)
    gitCommitAuthor = sh(returnStdout: true, script: 'git show --format="%aN" ${gitCommit} | head -1').trim()

    majorVersion = sh(returnStdout: true, script: 'grep -v "^#" RELEASE.txt').trim()

    tagName = sh(returnStdout: true, script: 'git describe --tags 2>/dev/null || :').trim()

    // If tag name is empty or match something like 1.5.3-3-gb9c22bb
    // We don't have tag there
    if (tagName == '' || tagName.matches(/^.*-\d+-g[a-zA-Z0-9]{7}$/)) {
      if (env.BRANCH_NAME == 'master') {
          // Do not add branch name in version for master branch
          return "${majorVersion}.${BUILD_NUMBER}+${shortCommit}"
      } else {
          // Add branch name to version for non master branch
          def branchNameNormaized = normalizeVersion env.BRANCH_NAME
          return "${majorVersion}.${BUILD_NUMBER}+${branchNameNormaized}.${shortCommit}"
      }
    // Add tag name for tagged commit
    } else {
    def tagNameNormaized = normalizeVersion tagName
      return "${majorVersion}.${BUILD_NUMBER}+${tagName}.${shortCommit}"
    }
}

node {
    // Job is triggered by
    // Scan Multibranch Pipeline Triggers
    properties([])

    try {
        stage ('Get Source') {
            checkout([
                $class: 'GitSCM',
                branches: [[name: env.BRANCH_NAME]],
                doGenerateSubmoduleConfigurations: false,
                extensions: [[$class: 'CleanBeforeCheckout']],
                submoduleCfg: [],
                userRemoteConfigs: [[url: "${repoName}", credentialsId: 'github_mykola_key']]
            ])

            currentBuild.displayName = generateVersion()

            writeFile file: "${fileVersion}", text: "${currentBuild.displayName}"
            sh '''
                env
                echo "======================"
                echo "git describe --tags"
                echo "======================"
                git describe --tags || echo "No tags"
                echo "======================"
            '''
        }

        stage ('Cat Version') {
            sh '''
                cat VERSION.txt
            '''
        }
    } catch (caughtError) {
        currentBuild.result = "FAILURE"
        throw caughtError
    } finally {
    }
}
