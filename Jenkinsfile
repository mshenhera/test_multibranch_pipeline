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
                userRemoteConfigs: [[url: "${repoName}"]]
            ])

            gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
            // short SHA, possibly better for chat notifications, etc.
            shortCommit = gitCommit.take(7)
            gitCommitAuthor = sh(returnStdout: true, script: 'git show --format="%aN" ${gitCommit} | head -1').trim()

            majorVersion = sh(returnStdout: true, script: 'grep -v "^#" RELEASE.txt').trim()

            currentBuild.displayName = "${majorVersion}.${BUILD_NUMBER}+${shortCommit}"

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
