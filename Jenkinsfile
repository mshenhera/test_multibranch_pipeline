def repoName    = "git@github.com:mshenhera/test_multibranch_pipeline.git"
def fileVersion = "VERSION.txt"

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

            gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
            // short SHA, possibly better for chat notifications, etc.
            shortCommit = gitCommit.take(7)
            gitCommitAuthor = sh(returnStdout: true, script: 'git show --format="%aN" ${gitCommit} | head -1').trim()

            majorVersion = sh(returnStdout: true, script: 'grep -v "^#" RELEASE.txt').trim()

            tagName = sh(returnStdout: true, script: 'git describe --tags 2>/dev/null || :').trim()

            // If tag name is empty or match something like 1.5.3-3-gb9c22bb
            // We don't have tag there
            if (tagName == '' || !tagName.matches(/^.*-\d+-g[a-zA-Z0-9]{7}$/)) {
              if (env.BRANCH_NAME == 'master') {
                  // Do not add branch name in version for master branch
                  currentBuild.displayName = "${majorVersion}.${BUILD_NUMBER}+${shortCommit}"
              } else {
                  // Add branch name to version for non master branch
                  def branchNameNormaized = env.BRANCH_NAME.replaceAll("/\W/", ".")
                  currentBuild.displayName = "${majorVersion}.${BUILD_NUMBER}+${branchNameNormaized}.${shortCommit}"
              }
            // Add tag name for tagged commit
            } else {
              def tagNameNormaized = tagName.replaceAll("/\W/", ".")
              currentBuild.displayName = "${majorVersion}.${BUILD_NUMBER}+${tagName}.${shortCommit}"
            }

            // currentBuild.displayName = "${majorVersion}.${BUILD_NUMBER}+${shortCommit}"

            writeFile file: "${fileVersion}", text: "${currentBuild.displayName}"
            sh '''
                env
                echo "======================"
                echo "git describe --tags"
                echo "======================"
                git describe --tags
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
