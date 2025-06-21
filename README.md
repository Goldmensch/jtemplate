# Using this template

This template contains:
- multimodule gradle project
- build/test workflow with gradle
- release and deploy workflow with
  - wiki support via mkdocs (gh-pages)
  - javadocs (gh-pages)
  - GitHub release creation

## First steps
You first have to run the workflow "setup repository" manually, you can do that in the
actions tab of the GitHub page. This workflow requires several inputs, which will then used
to prepare the repository.

After a successful run, this readme will be replaced.

Then set the gh-pages setting to "from branch" and choose "gh-pages".


## VARIABLES
This template has several variables, that will be replaced by the setup-repo task.

- PROJECT_NAME -> the project's name
- PROJECT_DESC -> the project's main modules (lib) description
- PROJECT_LOWER_NAME -> the project's name but in lower case, for use in java code

- JAVA_VERSION -> the project java version to use


- LICENSE_NAME -> the project's license name
- LICENSE_URL -> the url to the used license


- AUTHOR_NAME -> the authors name (only one author)

- MVN_GROUP -> the projects maven group, used for publishing
- MVN_ARTIFACT -> the projects maven artifact, used for publishing

- REPO_OWNER -> the repos owner
- REPO_NAME -> the repos name with the author, eg. Goldmensch/fluava
- REPO_WO_OWNER_NAME -> the repos name without the owner
- REPO_URL -> the url to the online code repository, without https://, eg. github.com/godmensch/jtemplate