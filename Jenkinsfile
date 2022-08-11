// Import the shared library
@Library('acv-jenkins-lib')
import com.acvJenkins.*;

// Instantiate classes from shared infrastructure libraries
notify = new Notify(this)
ecr = new ECR(this)

pipeline {
  options {
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
  }

  agent {
    kubernetes {
      defaultContainer 'docker'
      yaml new GlobalVars().extraLargeRunnerPod()
    }
  }

  environment {
    SERVICE_NAME = "java-spring-template"
    NEWRELIC_KEY = credentials('newrelic_push_token')

    // Fill in slack channel an uncomment slackSend in notifyBuild function below
    // BUILDS_SLACK_CHANNEL = ""

    // NewRelic App ID is not generated until first deploy. After new service is deployed for the first time,
    // uncomment this line with the new App ID and redeploy
    // NEWRELIC_APP_ID_LATEST = ""
    // NEWRELIC_APP_ID_STAGING = ""
    // NEWRELIC_APP_ID_PROD = ""
  }

  stages {

    // Gradle build, lint and test without creating image for not main branches
    stage('Build, Lint, Unit Test') {
      when {
        not {
          branch 'main'
        }
      }
      steps {
        sh 'make jenkins-build'
      }
    }

    // Gradle build, lint, test, and package image
    stage('Build, Lint, Unit Test, Package') {
      when {
        branch 'main'
      }
      steps {
        sh 'make jenkins-package'
      }
    }

    stage('Publish Coverage Report') {
      steps {
        publishCoverage adapters: [jacocoAdapter('build/reports/jacoco/test/jacocoTestReport.xml')]
      }
    }

    stage('Push Image to ECR') {
      when {
        branch 'main'
      }
      steps {
        sh 'make push'
      }
    }

    stage('Deploy (Latest)') {
      when {
        branch 'main'
      }
      environment {
        KUBECONFIG = credentials('carwash-0-2-0-uat-kubeconfig')
      }
      stages {
        stage('Helm Deploy (Latest)') {
          steps {
            sh 'helm3 repo update'
            //replace_helm_latest
          }
        }
      }
      post {
        success {
          script {
            echo 'success'
            // notify.send('pass', 'non-prod')
            // Uncomment below once NEWRELIC_APP_ID_LATEST is created after first deploy
            // newRelicTag("${NEWRELIC_APP_ID_LATEST}")
          }
        }
        failure {
          script {
            echo 'fail'
            // notify.send('fail', 'non-prod')
          }
        }
      }
    }
    stage('Manual Deploy Check') {
        when {
            branch 'main'
        }
        steps {
            timeout(time: 45, unit: "MINUTES") {
                input "Are you sure you want to deploy to Staging and Production?"
            }
        }
    }
    stage('Deploy (Staging)') {
      when {
        branch 'main'
      }
      environment {
        KUBECONFIG = credentials('carwash-2-2-4-staging-kubeconfig')
      }
      stages {
        stage('Helm Deploy (Staging)') {
          steps {
            sh 'helm3 repo update'
            //replace_helm_staging
          }
        }
      }
      post {
        success {
          script {
            echo 'success'
            // notify.send('pass', 'staging')
            // Uncomment below once NEWRELIC_APP_ID_STAGING is created after first deploy
            // newRelicTag("${NEWRELIC_APP_ID_STAGING}")
          }
        }
        failure {
          script {
            echo 'fail'
            // notify.send('fail', 'staging')
          }
        }
      }
    }
    stage('Deploy (Prod)') {
      when {
        branch 'main'
      }
      environment {
        KUBECONFIG = credentials('carwash-0-2-0-prod-kubeconfig')
      }
      stages {
        stage('Helm Deploy (Prod)') {
          steps {
            sh 'helm3 repo update'
            //replace_helm_prod
          }
        }
      }
      post {
        success {
          script {
            echo 'success'
            // notify.send('pass', 'prod')
            // Uncomment below once NEWRELIC_APP_ID_PROD is created after first deploy
            // newRelicTag("${NEWRELIC_APP_ID_PROD}")
          }
        }
        failure {
          script {
            echo 'fail'
            // notify.send('fail', 'prod')
          }
        }
      }
    }
  }

  post {
    always {
      notifySlack(currentBuild.result)
    }
  }
}

def newRelicTag(String newRelicAppId) {
  def createNewRelicDeployCommand = "python3 /bin/create_newrelic_deployment.py -k '${env.NEWRELIC_KEY}' -a '${newRelicAppId}' -r '${env.GIT_COMMIT}' -d 'Release from ${env.BRANCH_NAME} branch with tag ${env.GIT_COMMIT}'"
  sh(script: createNewRelicDeployCommand)
}

def notifySlack(String buildStatus = 'STARTED') {
  buildStatus =  buildStatus ?: 'SUCCESS'
  def colorCode = '#EF2929' // red
  def author = sh(returnStdout: true, script: 'git log -n 1 --format="%an" | awk \'{print $0;}\'').trim()
  def summary = "*${env.SERVICE_NAME}* ${buildStatus}\n<${env.BUILD_URL}|${env.BRANCH_NAME}> `${env.GIT_COMMIT}`\nAuthor: `${author}`"
  if (buildStatus == 'STARTED') {
    return
  } else if (buildStatus == 'SUCCESS') {
    colorCode = '#00A988' // green
  } else if (buildStatus == 'ABORTED') {
    colorCode = '#808080' // gray
  } else {
    colorCode = '#EF2929' // red
  }
// Uncomment once BUILDS_SLACK_CHANNEL is defined
//  slackSend (channel: "${env.BUILDS_SLACK_CHANNEL}", color: colorCode, message: summary)
}
