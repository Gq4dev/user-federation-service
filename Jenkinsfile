pipeline {
  agent any
  tools {
    maven 'maven-3.9'   // el nombre que configuraste en Global Tool Configuration
  }
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }
    stage('Build & Test') {
      steps {
        sh 'mvn -v'
        sh 'mvn clean verify -DskipTests'
      }
    }
  }
  post {
    always {
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/**', fingerprint: true, allowEmptyArchive: true
    }
  }
}
