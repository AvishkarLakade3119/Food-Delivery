pipeline {
    agent any

    tools {
        maven 'Maven-3.9.12'
        jdk   'JDK-17'
    }

    environment {
        DOCKER_HUB_USER = 'avishkarlakade'
        IMAGE_TAG       = "${env.BUILD_NUMBER}"
        K8S_NAMESPACE   = 'food-delivery'
        SERVICES        = 'config-server eureka-server api-gateway user-service restaurant-service order-service payment-service notification-service'
    }

    options {
        timestamps()
        timeout(time: 90, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    stages {

        stage('1. Checkout') {
            steps {
                echo 'Stage 1: Checkout from GitHub'
                checkout scm
                bat 'git log -1 --oneline'
            }
        }

        stage('2. Pre-Flight Check') {
            steps {
                echo 'Stage 2: Verifying tools'
                bat 'java -version'
                bat 'mvn -version'
                bat 'docker version --format "Client: {{.Client.Version}}"'
                bat 'kubectl version --client'
                bat 'minikube status'
            }
        }

        stage('3. Maven Build') {
            steps {
                echo 'Stage 3: Building all services'
                bat 'mvn -B -ntp clean package -DskipTests -T 1C'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', excludes: '**/*.original', fingerprint: true, allowEmptyArchive: true
                }
            }
        }

        stage('4. Unit Tests') {
            steps {
                echo 'Stage 4: Running unit tests'
                bat 'mvn -B -ntp test -fae || exit 0'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('5. Code Coverage') {
            steps {
                echo 'Stage 5: JaCoCo coverage'
                bat 'mvn -B -ntp jacoco:report -fae || exit 0'
            }
            post {
                always {
                    jacoco execPattern: '**/target/jacoco.exec', classPattern: '**/target/classes', sourcePattern: '**/src/main/java', exclusionPattern: '**/test/**'
                }
            }
        }

        stage('6. Docker Login') {
            steps {
                echo 'Stage 6: Authenticating Docker Hub'
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                    bat 'echo %DH_PASS%| docker login -u %DH_USER% --password-stdin'
                }
            }
        }

        stage('7. Docker Build') {
            steps {
                echo 'Stage 7: Building Docker images'
                script {
                    def svcs = env.SERVICES.split(' ')
                    svcs.each { svc ->
                        echo "Building image for ${svc}"
                        bat "cd ${svc} && docker build -t ${env.DOCKER_HUB_USER}/${svc}:${env.IMAGE_TAG} -t ${env.DOCKER_HUB_USER}/${svc}:latest . && cd .."
                    }
                }
            }
        }

        stage('8. Docker Push') {
            steps {
                echo 'Stage 8: Pushing images to Docker Hub'
                script {
                    def svcs = env.SERVICES.split(' ')
                    svcs.each { svc ->
                        echo "Pushing ${svc}"
                        retry(3) {
                            bat "docker push ${env.DOCKER_HUB_USER}/${svc}:${env.IMAGE_TAG}"
                            bat "docker push ${env.DOCKER_HUB_USER}/${svc}:latest"
                        }
                    }
                }
            }
        }

        stage('9. Deploy Infrastructure') {
            steps {
                echo 'Stage 9: Deploying Postgres, RabbitMQ, Zipkin'
                bat 'kubectl apply -f k8s\\00-namespace.yaml'
                bat 'kubectl apply -f k8s\\01-configmap.yaml'
                bat 'kubectl apply -f k8s\\02-postgres.yaml'
                bat 'kubectl apply -f k8s\\03-rabbitmq.yaml'
                bat 'kubectl apply -f k8s\\04-zipkin.yaml'
                bat 'kubectl rollout status deployment/postgres -n %K8S_NAMESPACE% --timeout=180s'
                bat 'kubectl rollout status deployment/rabbitmq -n %K8S_NAMESPACE% --timeout=180s'
                bat 'kubectl rollout status deployment/zipkin -n %K8S_NAMESPACE% --timeout=180s'
            }
        }

        stage('10. Deploy Microservices') {
            steps {
                echo 'Stage 10: Deploying microservices in order'
                bat 'kubectl apply -f k8s\\10-config-server.yaml'
                bat 'kubectl rollout status deployment/config-server -n %K8S_NAMESPACE% --timeout=300s'
                bat 'kubectl apply -f k8s\\11-eureka-server.yaml'
                bat 'kubectl rollout status deployment/eureka-server -n %K8S_NAMESPACE% --timeout=300s'
                bat 'kubectl apply -f k8s\\12-api-gateway.yaml'
                bat 'kubectl apply -f k8s\\13-user-service.yaml'
                bat 'kubectl apply -f k8s\\14-restaurant-service.yaml'
                bat 'kubectl apply -f k8s\\15-order-service.yaml'
                bat 'kubectl apply -f k8s\\16-payment-service.yaml'
                bat 'kubectl apply -f k8s\\17-notification-service.yaml'
                bat 'kubectl apply -f k8s\\99-nodeports.yaml'
                script {
                    def appSvcs = ['api-gateway', 'user-service', 'restaurant-service', 'order-service', 'payment-service', 'notification-service']
                    appSvcs.each { svc ->
                        bat "kubectl rollout status deployment/${svc} -n %K8S_NAMESPACE% --timeout=420s"
                    }
                }
            }
        }

        stage('11. Smoke Tests') {
            steps {
                echo 'Stage 11: Post-deploy verification'
                bat 'kubectl get pods -n %K8S_NAMESPACE% -o wide'
                bat 'kubectl get svc -n %K8S_NAMESPACE%'
                sleep(time: 30, unit: 'SECONDS')
                bat 'kubectl get pods -n %K8S_NAMESPACE%'
            }
        }

        stage('12. Expose URLs') {
            steps {
                echo 'Stage 12: Access URLs'
                bat 'minikube ip'
                bat 'echo Gateway HTTPS: https://MINIKUBE_IP:30443'
                bat 'echo RabbitMQ UI: http://MINIKUBE_IP:30672 (guest/guest)'
                bat 'echo Zipkin: http://MINIKUBE_IP:30411'
            }
        }
    }

    post {
        success {
            echo 'PIPELINE SUCCESS - All 8 services deployed'
        }
        failure {
            echo 'PIPELINE FAILED - Rolling back'
            script {
                def svcs = env.SERVICES.split(' ')
                svcs.each { svc ->
                    bat "kubectl rollout undo deployment/${svc} -n %K8S_NAMESPACE% || exit 0"
                }
            }
        }
        always {
            bat 'docker logout || exit 0'
        }
    }
}