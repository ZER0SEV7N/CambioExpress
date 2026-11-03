// RN-3: pipeline declarativo que ejecutaria el job de Jenkins en el modelo OCD
pipeline {
  agent any

  options {
    timeout(time: 20, unit: 'MINUTES')
  }

  stages {
    stage('Compilar') {
      steps {
        sh 'mvn -B -ntp clean compile'
      }
    }

    stage('Pruebas y cobertura') {
      steps {
        sh 'mvn -B -ntp verify'
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'target/*-reports/*.xml'
        }
      }
    }

    stage('Publicar artefacto') {
      steps {
        archiveArtifacts artifacts: 'target/cambioexpress-*.jar', fingerprint: true
      }
    }

    stage('Desplegar a cert') {
      when {
        branch 'master'
      }
      steps {
        withCredentials([string(credentialsId: 'cambioexpress-deploy-token', variable: 'DEPLOY_TOKEN')]) {
          sh 'bash devops/desplegar.sh cert target/cambioexpress-*.jar'
        }
      }
    }
  }
}
