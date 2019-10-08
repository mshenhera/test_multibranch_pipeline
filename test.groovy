import utilities.GithubMultibranch
 
 
def multiPipeline = new GithubMultibranch(
    description: 'Just try make world better',
    name: 'Github-Test',
    displayName: 'Github-Test',
    repositoryOwner: "mshenhera",
    repositoryName: "test_multibranch_pipeline",
    credentials: 'github-mykola',
    includeBranches: 'development staging master',
    excludeBranches: ''
).build(this)
