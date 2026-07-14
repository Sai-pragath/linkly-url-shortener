pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = credentials('dockerhub-credentials') // Needs to be configured in Jenkins
        DOCKER_IMAGE_NAME = 'saipragath/url-shortener' // User needs to update this
        IMAGE_TAG = "${BUILD_NUMBER}"
        KUBECONFIG = '/etc/rancher/k3s/k3s.yaml'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${DOCKER_IMAGE_NAME}:${IMAGE_TAG} -t ${DOCKER_IMAGE_NAME}:latest .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh 'echo ${DOCKER_HUB_CREDENTIALS_PSW} | docker login -u ${DOCKER_HUB_CREDENTIALS_USR} --password-stdin'
                sh 'docker push ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}'
                sh 'docker push ${DOCKER_IMAGE_NAME}:latest'
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                // First apply MySQL
                sh 'kubectl apply -f devops/k8s/mysql-deployment.yaml'
                
                // Then apply App (dynamically setting the image)
                sh 'sed "s|\\${DOCKER_IMAGE}|${DOCKER_IMAGE_NAME}:${IMAGE_TAG}|g" devops/k8s/app-deployment.yaml | kubectl apply -f -'
                
                // Lastly, apply the ServiceMonitor so Prometheus scrapes the app
                sh 'kubectl apply -f devops/k8s/service-monitor.yaml'
            }
        }
    }
}
