pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "raccoon304/jobfinder"
        SERVER_IP = "98.84.141.91"
        APP_DIR = "/home/ubuntu/jobfinder"
    }

    tools {
        jdk 'jdk17'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Gradle') {
            steps {
                dir('finalProject') {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                dir('finalProject') {
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub_info',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh '''
                            echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                            docker build -t $DOCKER_IMAGE:latest .
                            docker push $DOCKER_IMAGE:latest
                        '''
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sshagent(['SERVER_SSH_KEY']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ubuntu@$SERVER_IP '
                            mkdir -p $APP_DIR
                            cd $APP_DIR
                            docker compose pull
                            docker compose up -d --force-recreate
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo "Compose deployment completed successfully."
        }
        failure {
            echo "Deployment failed."
        }
    }
}