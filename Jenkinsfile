pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t shubhadashingane/task-pipeline:latest .'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker stop my-java-app || true
                    docker rm my-java-app || true

                    docker run -d \
                      --name my-java-app \
                      -p 8081:8081 \
                      my-java-app:latest
                '''
            }
        }
    }
}