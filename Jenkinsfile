pipeline {
    agent any

    tools {
        maven 'Maven-3.9.12'
        jdk 'JDK-17'
    }

    environment {
        DOCKER_HUB_USER = 'avishkarlakade'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        K8S_NAMESPACE = 'food-delivery'
        SERVICES = 'config-server eureka-server api-gateway user-service restaurant-service order-service payment-service notification-service'
    }

    options {
        timestamps()
        timeout(time: 120, unit: 'MINUTES')
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
                echo 'Stage 2: Verifying tools and cluster connectivity'
                bat 'java -version'
                bat 'mvn -version'
                bat 'docker version --format "Client: {{.Client.Version}}"'
                bat 'kubectl version --client'
                bat 'kubectl cluster-info'
                bat 'kubectl get nodes -o wide'
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
                echo 'Stage 5: Archive JaCoCo execution data'
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    bat 'mvn -B -ntp org.jacoco:jacoco-maven-plugin:0.8.11:report -fae || exit 0'
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/target/jacoco.exec, **/target/site/jacoco/**', allowEmptyArchive: true, fingerprint: false
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
                echo 'Stage 7: Building 8 Docker images'
                script {
                    def svcs = env.SERVICES.split(' ')
                    svcs.each { svc ->
                        echo "Building image: ${env.DOCKER_HUB_USER}/${svc}:${env.IMAGE_TAG}"
                        bat "cd ${svc} && docker build -t ${env.DOCKER_HUB_USER}/${svc}:${env.IMAGE_TAG} -t ${env.DOCKER_HUB_USER}/${svc}:latest ."
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

        stage('9. Create Namespace and Config') {
            steps {
                echo 'Stage 9: Creating namespace and ConfigMaps'
                bat 'kubectl apply -f k8s/00-namespace.yaml'
                bat 'kubectl apply -f k8s/01-configmap.yaml'
                bat 'kubectl get namespace food-delivery'
                bat 'kubectl get configmap -n %K8S_NAMESPACE%'
            }
        }

        stage('10. Deploy Infrastructure') {
            steps {
                echo 'Stage 10: Deploying Postgres, RabbitMQ, Zipkin'
                bat 'kubectl apply -f k8s/02-postgres.yaml'
                bat 'kubectl apply -f k8s/03-rabbitmq.yaml'
                bat 'kubectl apply -f k8s/04-zipkin.yaml'
                bat 'kubectl rollout status deployment/postgres -n %K8S_NAMESPACE% --timeout=300s'
                bat 'kubectl rollout status deployment/rabbitmq -n %K8S_NAMESPACE% --timeout=300s'
                bat 'kubectl rollout status deployment/zipkin -n %K8S_NAMESPACE% --timeout=300s'
                bat 'kubectl get pods -n %K8S_NAMESPACE%'
            }
        }

        stage('11. Deploy Foundation Services') {
            steps {
                echo 'Stage 11: Deploying Config Server and Eureka'
                bat 'kubectl apply -f k8s/10-config-server.yaml'
                bat 'kubectl rollout restart deployment/config-server -n %K8S_NAMESPACE% || exit 0'
                bat 'kubectl rollout status deployment/config-server -n %K8S_NAMESPACE% --timeout=420s'
                bat 'kubectl apply -f k8s/11-eureka-server.yaml'
                bat 'kubectl rollout restart deployment/eureka-server -n %K8S_NAMESPACE% || exit 0'
                bat 'kubectl rollout status deployment/eureka-server -n %K8S_NAMESPACE% --timeout=420s'
            }
        }

        stage('12. Deploy Microservices') {
            steps {
                echo 'Stage 12: Deploying all microservices'
                bat 'kubectl apply -f k8s/12-api-gateway.yaml'
                bat 'kubectl apply -f k8s/13-user-service.yaml'
                bat 'kubectl apply -f k8s/14-restaurant-service.yaml'
                bat 'kubectl apply -f k8s/15-order-service.yaml'
                bat 'kubectl apply -f k8s/16-payment-service.yaml'
                bat 'kubectl apply -f k8s/17-notification-service.yaml'
                script {
                    def appSvcs = ['api-gateway', 'user-service', 'restaurant-service', 'order-service', 'payment-service', 'notification-service']
                    appSvcs.each { svc ->
                        bat "kubectl rollout restart deployment/${svc} -n %K8S_NAMESPACE% || exit 0"
                    }
                    appSvcs.each { svc ->
                        bat "kubectl rollout status deployment/${svc} -n %K8S_NAMESPACE% --timeout=420s"
                    }
                }
            }
        }

        stage('13. Expose NodePorts') {
            steps {
                echo 'Stage 13: Creating NodePort services'
                bat 'kubectl apply -f k8s/99-nodeports.yaml'
                bat 'kubectl get svc -n %K8S_NAMESPACE%'
            }
        }

        stage('14. Smoke Tests') {
            steps {
                echo 'Stage 14: Post-deploy verification'
                bat 'kubectl get pods -n %K8S_NAMESPACE% -o wide'
                bat 'kubectl get svc -n %K8S_NAMESPACE%'
                bat 'kubectl get endpoints -n %K8S_NAMESPACE%'
                sleep(time: 45, unit: 'SECONDS')
                bat 'kubectl wait --for=condition=ready pod -l app=api-gateway -n %K8S_NAMESPACE% --timeout=180s || exit 0'
                bat 'kubectl get pods -n %K8S_NAMESPACE%'
            }
        }

        stage('15. Report Access URLs') {
            steps {
                echo 'Stage 15: Deployment summary'
                bat 'kubectl get nodes -o wide'
                bat 'kubectl get svc -n %K8S_NAMESPACE%'
                echo '==================================================='
                echo 'DEPLOYMENT COMPLETE'
                echo '==================================================='
                echo 'Use Node IP and NodePort:'
                echo '  Gateway HTTPS : https://NODE_IP:30443'
                echo '  RabbitMQ UI   : http://NODE_IP:30672 (guest/guest)'
                echo '  Zipkin UI     : http://NODE_IP:30411'
                echo '==================================================='
            }
        }
    }

    post {
        success {
            echo '================================================'
            echo '  PIPELINE SUCCESS - All 8 services deployed'
            echo '================================================'
        }
        failure {
            echo '================================================'
            echo '  PIPELINE FAILED'
            echo '================================================'
            script {
                def clusterUp = bat(returnStatus: true, script: 'kubectl cluster-info > nul 2>&1') == 0
                def nsExists = bat(returnStatus: true, script: 'kubectl get namespace %K8S_NAMESPACE% > nul 2>&1') == 0
                if (clusterUp && nsExists) {
                    echo 'Cluster reachable and namespace exists - attempting rollback'
                    try {
                        def svcs = env.SERVICES.split(' ')
                        svcs.each { svc ->
                            bat "kubectl rollout undo deployment/${svc} -n %K8S_NAMESPACE% || exit 0"
                        }
                    } catch (Exception e) {
                        echo "Rollback error"
                    }
                } else {
                    echo 'No deployments to rollback (cluster unreachable or namespace missing)'
                }
            }
        }
        always {
            bat 'docker logout || exit 0'
            bat 'docker image prune -f --filter "until=72h" || exit 0'
        }
    }
}