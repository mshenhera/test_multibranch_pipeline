package utilities



import javaposse.jobdsl.dsl.DslFactory
 
class GithubMultibranch {
 
    String name
    String description
    String displayName
    String repositoryOwner
    String repositoryName
    String credentials
    String includeBranches
    String excludeBranches
 
 
    void build(DslFactory dslFactory) {
        def job = dslFactory.multibranchPipelineJob(name) {
            description(description)
            displayName(displayName)
            branchSources {
                github {
                    scanCredentialsId(credentials)
                    repoOwner(repositoryOwner)
                    repository(repositoryName)
                    includes(includeBranches)
                    excludes(excludeBranches)
                }
            }
            factory {
                workflowBranchProjectFactory {
                    scriptPath('Jenkinsfile')
                }
            }
            orphanedItemStrategy {
                discardOldItems {
                    numToKeep(15)
                }
            }
        }
    }
}
