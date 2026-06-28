

pipeline {
  agent any
 stages {
  /* GET STALE BRANCHES STARTS*/
  stage('Get STALE Branches') {
    steps {
      script {
      withCredentials([gitUsernamePassword(credentialsId: 'githubapp_STAF', gitToolName: 'git')]) {
           echo "Get STALE branches"
     }
      }
    }
  }
  /* GET STALE BRANCHES ENDS*/
  }

}