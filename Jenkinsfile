pipeline {
  agent {
    docker {
      image 'maven:3.9.9-eclipse-temurin-21'
      args '-v $HOME/.m2:/root/.m2'
    }
  }
  options { timestamps(); ansiColor('xterm') }
  stages {
    stage('Checkout') { steps { checkout scm } }
    stage('Build & Test') {
      steps {
        sh 'mvn -v'
        sh 'mvn clean verify -DskipTests'
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
          archiveArtifacts artifacts: 'target/**', fingerprint: true, allowEmptyArchive: true
        }
      }
    }
  }
}
