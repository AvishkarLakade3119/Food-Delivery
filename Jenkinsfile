pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
  }

  triggers {
    githubPush()
  }

  environment {
    DOCKERHUB_USER    = "avishkarlakade"
    DOCKERHUB_CRED_ID = "DockerHubCred"
    GITHUB_CRED_ID    = "GitHubCred"
    NAMESPACE  = "food-delivery"
    REPO_URL   = "https://github.com/AvishkarLakade3119/Food-Delivery.git"
    BRANCH     = "main"
    SERVICES   = "eureka-server api-gateway user-service restaurant-service order-service payment-service notification-service"
    KUBECONFIG = "/var/lib/jenkins/.kube/config"
  }

  stages {

    stage("Checkout") {
      steps {
        checkout([
          $class: 'GitSCM',
          branches: [[name: "*/${BRANCH}"]],
          userRemoteConfigs: [[
            url: "${REPO_URL}",
            credentialsId: "${GITHUB_CRED_ID}"
          ]]
        ])
      }
    }

    stage("Compute Image Tag") {
      steps {
        script {
          env.GIT_SHA   = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
          env.IMAGE_TAG = "${env.BUILD_NUMBER}-${env.GIT_SHA}"
          echo "Using IMAGE_TAG = ${env.IMAGE_TAG}"
        }
      }
    }

    stage("Build JARs with Maven") {
      steps {
        sh '''#!/bin/bash
          set -euo pipefail
          for svc in ${SERVICES}; do
            echo "=============================="
            echo "Building service: $svc"
            echo "=============================="
            mvn -f $svc/pom.xml -DskipTests clean package
          done
        '''
      }
    }

    stage("Docker Login") {
      steps {
        withCredentials([usernamePassword(
          credentialsId: "${DOCKERHUB_CRED_ID}",
          usernameVariable: 'DH_USER',
          passwordVariable: 'DH_PASS'
        )]) {
          sh '''#!/bin/bash
            set -euo pipefail
            echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
          '''
        }
      }
    }

    stage("Build and Push Images") {
      steps {
        sh '''#!/bin/bash
          set -euo pipefail
          for svc in ${SERVICES}; do
            echo "=============================="
            echo "Docker build and push: $svc"
            echo "=============================="
            docker build \
              -t ${DOCKERHUB_USER}/${svc}:${IMAGE_TAG} \
              -t ${DOCKERHUB_USER}/${svc}:latest \
              ./${svc}
            docker push ${DOCKERHUB_USER}/${svc}:${IMAGE_TAG}
            docker push ${DOCKERHUB_USER}/${svc}:latest
          done
        '''
      }
    }

    stage("Kubernetes Deploy") {
      steps {
        sh '''#!/bin/bash
          set -euo pipefail
          export KUBECONFIG=${KUBECONFIG}
          if [ ! -f "${KUBECONFIG}" ]; then
            echo "ERROR: kubeconfig not found at ${KUBECONFIG}"
            exit 1
          fi
          echo "Applying all k8s manifests..."
          kubectl apply -f k8s/namespace.yaml || true
          kubectl apply -n ${NAMESPACE} -f k8s/ || true

          echo "Waiting for database pods to be ready first..."
          for db in userdb restaurantdb orderdb paymentdb notificationdb; do
            echo "Waiting for $db..."
            kubectl -n ${NAMESPACE} rollout status deployment/${db} --timeout=300s || true
          done
          echo "All databases ready."

          kubectl get pods -n ${NAMESPACE}
          kubectl get svc  -n ${NAMESPACE}
        '''
      }
    }

    stage("Kubernetes Update Images") {
      steps {
        sh '''#!/bin/bash
          set -euo pipefail
          export KUBECONFIG=${KUBECONFIG}
          echo "Updating deployments to new image tag: ${IMAGE_TAG}"
          for svc in ${SERVICES}; do
            echo "Updating $svc..."
            kubectl -n ${NAMESPACE} set image deployment/${svc} ${svc}=${DOCKERHUB_USER}/${svc}:${IMAGE_TAG} || true
          done
        '''
      }
    }

    stage("Rollout Verify") {
      steps {
        sh '''#!/bin/bash
          set -euo pipefail
          export KUBECONFIG=${KUBECONFIG}
          for svc in ${SERVICES}; do
            echo "Waiting for $svc rollout..."
            kubectl -n ${NAMESPACE} rollout status deployment/${svc} --timeout=600s
          done
          echo "SUCCESS: All services rolled out."
          kubectl -n ${NAMESPACE} get pods -o wide
          kubectl -n ${NAMESPACE} get svc
        '''
      }
    }

    stage("Expose Services") {
      steps {
        sh '''#!/bin/bash
          set +e
          export KUBECONFIG=/var/lib/jenkins/.kube/config

          echo "Waiting 30s for pods to fully stabilize..."
          sleep 30

          pkill -f "kubectl port-forward.*food-delivery" || true
          sleep 3
          for port in 8761 8081 8082 8083 8084 8085 8086; do
            fuser -k ${port}/tcp 2>/dev/null || true
          done
          sleep 2

          LOG_DIR=/var/lib/jenkins/pf-logs
          mkdir -p ${LOG_DIR}
          rm -f ${LOG_DIR}/*.log

          JENKINS_NODE_COOKIE=dontKillMe nohup kubectl port-forward -n food-delivery svc/eureka-server 8761:8761 --address=0.0.0.0 > ${LOG_DIR}/eureka.log 2>&1 &
          JENKINS_NODE_COOKIE=dontKillMe nohup kubectl port-forward -n food-delivery svc/api-gateway 8081:8081 --address=0.0.0.0 > ${LOG_DIR}/gateway.log 2>&1 &
          JENKINS_NODE_COOKIE=dontKillMe nohup kubectl port-forward -n food-delivery svc/user-service 8082:8082 --address=0.0.0.0 > ${LOG_DIR}/user.log 2>&1 &
          JENKINS_NODE_COOKIE=dontKillMe nohup kubectl port-forward -n food-delivery svc/restaurant-service 8083:8083 --address=0.0.0.0 > ${LOG_DIR}/restaurant.log 2>&1 &
          JENKINS_NODE_COOKIE=dontKillMe nohup kubectl port-forward -n food-delivery svc/order-service 8084:8084 --address=0.0.0.0 > ${LOG_DIR}/order.log 2>&1 &
          JENKINS_NODE_COOKIE=dontKillMe nohup kubectl port-forward -n food-delivery svc/payment-service 8085:8085 --address=0.0.0.0 > ${LOG_DIR}/payment.log 2>&1 &
          JENKINS_NODE_COOKIE=dontKillMe nohup kubectl port-forward -n food-delivery svc/notification-service 8086:8086 --address=0.0.0.0 > ${LOG_DIR}/notification.log 2>&1 &

          sleep 10

          PF_COUNT=$(ps aux | grep "kubectl port-forward.*food-delivery" | grep -v grep | wc -l)
          echo "Active port-forwards: ${PF_COUNT} / 7"

          if [ "$PF_COUNT" -lt 7 ]; then
            echo "Retrying failed port-forwards..."
            sleep 5
            for entry in eureka-server:8761 api-gateway:8081 user-service:8082 restaurant-service:8083 order-service:8084 payment-service:8085 notification-service:8086; do
              svc=$(echo $entry | cut -d: -f1)
              port=$(echo $entry | cut -d: -f2)
              if ! ps aux | grep "kubectl port-forward.*svc/${svc}" | grep -v grep > /dev/null 2>&1; then
                echo "Restarting port-forward for ${svc}..."
                fuser -k ${port}/tcp 2>/dev/null || true
                sleep 1
                JENKINS_NODE_COOKIE=dontKillMe nohup kubectl port-forward -n food-delivery svc/${svc} ${port}:${port} --address=0.0.0.0 > ${LOG_DIR}/${svc}-retry.log 2>&1 &
              fi
            done
            sleep 5
          fi

          FINAL_COUNT=$(ps aux | grep "kubectl port-forward.*food-delivery" | grep -v grep | wc -l)
          echo "Final active port-forwards: ${FINAL_COUNT} / 7"

          echo ""
          echo "======================================================"
          echo "  DEPLOYMENT COMPLETE - ACCESS URLS"
          echo "======================================================"
          echo "  Eureka Dashboard   : http://localhost:8761"
          echo "  API Gateway        : http://localhost:8081"
          echo "  User Service       : http://localhost:8081/user-service/api/users"
          echo "  Restaurant Service : http://localhost:8081/restaurant-service/api/restaurants"
          echo "  Order Service      : http://localhost:8081/order-service/api/orders"
          echo "  Payment Service    : http://localhost:8081/payment-service/api/payments"
          echo "  Notification Svc   : http://localhost:8081/notification-service/api/notifications"
          echo "======================================================"
        '''
      }
    }
  }

  post {
    failure {
      sh '''#!/bin/bash
        set +e
        export KUBECONFIG=/var/lib/jenkins/.kube/config
        echo "---- DEBUG: Pods ----"
        kubectl -n food-delivery get pods -o wide || true
        echo "---- DEBUG: Events ----"
        kubectl -n food-delivery get events --sort-by=.metadata.creationTimestamp | tail -n 50 || true
        echo "---- DEBUG: Logs api-gateway ----"
        kubectl -n food-delivery logs deploy/api-gateway --tail=200 || true
      '''
    }

    always {
      sh 'docker logout || true'
    }
  }
}
