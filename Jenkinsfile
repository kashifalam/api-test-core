pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_TARGET', choices: ['snapshots', 'releases'], description: 'Maven repository target')
    }

    environment {
        JAVA_HOME = tool name: 'jdk-21', type: 'jdk'
        MAVEN_HOME = tool name: 'maven-3.9', type: 'Maven'
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('Deploy to Maven Repository') {
            when {
                branch 'main'
            }
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }
    }

    post {
        success {
            echo "api-test-core published successfully."
        }
    }
}
