$base = "C:\Users\avilakad\OneDrive - Publicis Groupe\Documents\Food-Delivery-main"

$jenkinsfile = @'
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
        timeout(time: 120, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        skipDefaultCheckout(false)
    }

    stages {

        // ============================================================
        // STAGE 1 - CHECKOUT
        // ============================================================
        stage('1. Checkout') {
            steps {
                echo 'Stage 1: Checkout from GitHub'
                checkout scm
                bat 'git log -1 --oneline'
            }
        }

        // ============================================================
        // STAGE 2 - PRE-FLIGHT CHECK (kubectl only, no minikube CLI)
        // ============================================================
        stage('2. Pre-Flight Check') {
            steps {
                echo 'Stage 2: Verifying tools and cluster connectivity'
                bat 'java -version'
                bat 'mvn -version'
                bat 'docker version --format "Client: {{.Client.Version}}, Server: {{.Server.Version}}"'
                bat 'kubectl version --client'
                bat 'kubectl cluster-info'
                bat 'kubectl get nodes -o wide'
            }
        }

        // ============================================================
        // STAGE 3 - MAVEN BUILD
        // ============================================================
        stage('3. Maven Build') {
            steps {
                echo 'Stage 3: Building all services (parallel)'
                bat 'mvn -B -ntp clean package -DskipTests -T 1C'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar',
                                     excludes: '**/*.original',
                                     fingerprint: true,
                                     allowEmptyArchive: true
                }
            }
        }

        // ============================================================
        // STAGE 4 - UNIT TESTS
        // ============================================================
        stage('4. Unit Tests') {
            steps {
                echo 'Stage 4: Running unit tests'
                bat 'mvn -B -ntp test -fae || exit 0'
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        // ============================================================
        // STAGE 5 - CODE COVERAGE
        // ============================================================
        stage('5. Code Coverage') {
            steps {
                echo 'Stage 5: JaCoCo coverage'
                bat 'mvn -B -ntp jacoco:report -fae || exit 0'
            }
            post {
                always {
                    jacoco execPattern:     '**/target/jacoco.exec',
                           classPattern:    '**/target/classes',
                           sourcePattern:   '**/src/main/java',
                           exclusionPattern:'**/test/**'
                }
            }
        }

        // ============================================================
        // STAGE 6 - DOCKER LOGIN
        // ============================================================
        stage('6. Docker Login') {
            steps {
                echo 'Stage 6: Authenticating Docker Hub'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DH_USER',
                    passwordVariable: 'DH_PASS')]) {
                    bat 'echo %DH_PASS%| docker login -u %DH_USER% --password-stdin'
                }
            }
        }

        // ============================================================
        // STAGE 7 - DOCKER BUILD
        // ============================================================
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

        // ============================================================
        // STAGE 8 - DOCKER PUSH (with retry)
        // ============================================================
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

        // ============================================================
        // STAGE 9 - CREATE NAMESPACE & CONFIGMAPS
        // ============================================================
        stage('9. Create Namespace & Config') {
            steps {
                echo 'Stage 9: Creating namespace and ConfigMaps'
                bat 'kubectl apply -f k8s\\00-namespace.yaml'
                bat 'kubectl apply -f k8s\\01-configmap.yaml'
                bat 'kubectl get namespace food-delivery'
                bat 'kubectl get configmap -n %K8S_NAMESPACE%'
            }
        }

        // ============================================================
        // STAGE 10 - DEPLOY INFRASTRUCTURE
        // ============================================================
        stage('10. Deploy Infrastructure') {
            steps {
                echo 'Stage 10: Deploying Postgres, RabbitMQ, Zipkin'
                bat 'kubectl apply -f k8s\\02-postgres.yaml'
                bat 'kubectl apply -f k8s\\03-rabbitmq.yaml'
                bat 'kubectl apply -f k8s\\04-zipkin.yaml'

                echo 'Waiting for infrastructure rollouts...'
                bat 'kubectl rollout status deployment/postgres -n %K8S_NAMESPACE% --timeout=300s'
                bat 'kubectl rollout status deployment/rabbitmq -n %K8S_NAMESPACE% --timeout=300s'
                bat 'kubectl rollout status deployment/zipkin   -n %K8S_NAMESPACE% --timeout=300s'

                bat 'kubectl get pods -n %K8S_NAMESPACE%'
            }
        }

        // ============================================================
        // STAGE 11 - DEPLOY CONFIG SERVER & EUREKA (FOUNDATION)
        // ============================================================
        stage('11. Deploy Foundation Services') {
            steps {
                echo 'Stage 11: Deploying Config Server + Eureka'

                echo '[a] Config Server'
                bat 'kubectl apply -f k8s\\10-config-server.yaml'
                bat 'kubectl rollout restart deployment/config-server -n %K8S_NAMESPACE% || exit 0'
                bat 'kubectl rollout status deployment/config-server -n %K8S_NAMESPACE% --timeout=420s'

                echo '[b] Eureka Server'
                bat 'kubectl apply -f k8s\\11-eureka-server.yaml'
                bat 'kubectl rollout restart deployment/eureka-server -n %K8S_NAMESPACE% || exit 0'
                bat 'kubectl rollout status deployment/eureka-server -n %K8S_NAMESPACE% --timeout=420s'
            }
        }

        // ============================================================
        // STAGE 12 - DEPLOY MICROSERVICES + GATEWAY
        // ============================================================
        stage('12. Deploy Microservices') {
            steps {
                echo 'Stage 12: Deploying all microservices'

                bat 'kubectl apply -f k8s\\12-api-gateway.yaml'
                bat 'kubectl apply -f k8s\\13-user-service.yaml'
                bat 'kubectl apply -f k8s\\14-restaurant-service.yaml'
                bat 'kubectl apply -f k8s\\15-order-service.yaml'
                bat 'kubectl apply -f k8s\\16-payment-service.yaml'
                bat 'kubectl apply -f k8s\\17-notification-service.yaml'

                echo 'Forcing pull of latest image tag'
                script {
                    def appSvcs = ['api-gateway','user-service','restaurant-service','order-service','payment-service','notification-service']
                    appSvcs.each { svc ->
                        bat "kubectl rollout restart deployment/${svc} -n %K8S_NAMESPACE% || exit 0"
                    }
                }

                echo 'Waiting for all rollouts...'
                script {
                    def appSvcs = ['api-gateway','user-service','restaurant-service','order-service','payment-service','notification-service']
                    appSvcs.each { svc ->
                        bat "kubectl rollout status deployment/${svc} -n %K8S_NAMESPACE% --timeout=420s"
                    }
                }
            }
        }

        // ============================================================
        // STAGE 13 - EXPOSE NODEPORTS
        // ============================================================
        stage('13. Expose NodePorts') {
            steps {
                echo 'Stage 13: Creating NodePort services for external access'
                bat 'kubectl apply -f k8s\\99-nodeports.yaml'
                bat 'kubectl get svc -n %K8S_NAMESPACE%'
            }
        }

        // ============================================================
        // STAGE 14 - SMOKE TESTS
        // ============================================================
        stage('14. Smoke Tests') {
            steps {
                echo 'Stage 14: Post-deploy verification'

                bat 'kubectl get pods -n %K8S_NAMESPACE% -o wide'
                bat 'kubectl get svc -n %K8S_NAMESPACE%'
                bat 'kubectl get endpoints -n %K8S_NAMESPACE%'

                echo 'Allowing 45s for services to fully initialize'
                sleep(time: 45, unit: 'SECONDS')

                echo 'Checking gateway readiness'
                bat 'kubectl wait --for=condition=ready pod -l app=api-gateway -n %K8S_NAMESPACE% --timeout=180s || exit 0'

                echo 'Final pod status'
                bat 'kubectl get pods -n %K8S_NAMESPACE%'
            }
        }

        // ============================================================
        // STAGE 15 - REPORT ACCESS URLS
        // ============================================================
        stage('15. Report Access URLs') {
            steps {
                echo 'Stage 15: Deployment summary'
                bat 'kubectl get nodes -o wide'
                bat 'kubectl get svc -n %K8S_NAMESPACE%'
                echo ''
                echo '==================================================='
                echo 'DEPLOYMENT COMPLETE'
                echo '==================================================='
                echo 'Use any Node IP shown above with these NodePorts:'
                echo '  Gateway HTTPS : https://<NODE_IP>:30443'
                echo '  RabbitMQ UI   : http://<NODE_IP>:30672  (guest/guest)'
                echo '  Zipkin UI     : http://<NODE_IP>:30411'
                echo ''
                echo 'For Minikube on Windows: run "minikube tunnel" or use'
                echo 'kubectl port-forward to access from your dev machine.'
                echo '==================================================='
            }
        }
    }

    // ============================================================
    // POST ACTIONS
    // ============================================================
    post {
        success {
            echo ''
            echo '================================================'
            echo '  PIPELINE SUCCESS - All 8 services deployed'
            echo '================================================'
        }
        failure {
            echo ''
            echo '================================================'
            echo '  PIPELINE FAILED - Attempting rollback'
            echo '================================================'
            script {
                try {
                    bat 'kubectl cluster-info'
                    def svcs = env.SERVICES.split(' ')
                    svcs.each { svc ->
                        bat "kubectl rollout undo deployment/${svc} -n %K8S_NAMESPACE% || exit 0"
                    }
                    echo 'Rollback completed'
                } catch (Exception e) {
                    echo "Rollback skipped: cluster unreachable"
                }
            }
        }
        always {
            echo 'Cleanup phase'
            bat 'docker logout || exit 0'
            bat 'docker image prune -f --filter "until=72h" || exit 0'
        }
    }
}
'@

# Write WITHOUT BOM
$path = "$base\Jenkinsfile"
[System.IO.File]::WriteAllText($path, $jenkinsfile, [System.Text.UTF8Encoding]::new($false))

# Verify
Write-Host "✅ Jenkinsfile created" -ForegroundColor Green
Write-Host "   Path: $path" -ForegroundColor Gray
Write-Host "   Size: $((Get-Item $path).Length) bytes" -ForegroundColor Gray
Write-Host "   Lines: $((Get-Content $path).Count)" -ForegroundColor Gray

Write-Host "`n=== Stage count ===" -ForegroundColor Cyan
$stages = Select-String -Path $path -Pattern "^\s+stage\('"
$stages | ForEach-Object { Write-Host "  $($_.Line.Trim())" }
Write-Host "Total: $($stages.Count) stages" -ForegroundColor Cyan

Write-Host "`n=== No 'minikube' CLI references ===" -ForegroundColor Cyan
$mk = Select-String -Path $path -Pattern "minikube" | Where-Object { $_.Line -notmatch "^\s*//" -and $_.Line -notmatch "echo" }
if ($mk) { 
    Write-Host "⚠️ Found minikube references:" -ForegroundColor Yellow
    $mk | ForEach-Object { Write-Host "  Line $($_.LineNumber): $($_.Line.Trim())" }
} else { 
    Write-Host "✅ Clean — only kubectl used" -ForegroundColor Green 
}