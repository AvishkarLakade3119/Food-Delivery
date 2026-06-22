# Config Server - Centralized Configuration Management

## Overview

The Config Server provides centralized configuration management for all microservices in the Food Delivery platform. It serves configuration files from a Git repository and integrates with Eureka for service discovery.

## Features

- ✅ Git-backed configuration storage
- ✅ Centralized configuration for all microservices
- ✅ Environment-specific profiles (dev, prod)
- ✅ Dynamic configuration refresh without restart
- ✅ Integration with Eureka Service Discovery
- ✅ Health checks and monitoring via Actuator
- ✅ Docker and Kubernetes ready

## Technology Stack

- **Spring Boot:** 3.2.5
- **Spring Cloud:** 2023.0.1
- **Spring Cloud Config Server:** Centralized configuration
- **Eureka Client:** Service registration and discovery
- **Java:** 17+
- **Maven:** Build tool

## Configuration

### Application Properties

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: file://${user.home}/config-repo
          default-label: main
          clone-on-start: true
```

### Git Repository Structure

The config repository (`~/config-repo`) contains:

```
config-repo/
├── application.yml              # Common properties for all services
├── application-dev.yml          # Development profile
├── application-prod.yml         # Production profile
├── eureka-server.yml            # Eureka Server config
├── api-gateway.yml              # API Gateway config
├── user-service.yml             # User Service config
├── restaurant-service.yml       # Restaurant Service config
├── order-service.yml            # Order Service config
├── payment-service.yml          # Payment Service config
└── notification-service.yml     # Notification Service config
```

## Building and Running

### Local Development

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR
java -jar target/config-server-1.0.0.jar
```

### Docker

```bash
# Build Docker image
docker build -t config-server:latest .

# Run container
docker run -p 8888:8888 \
  -v ~/config-repo:/root/config-repo \
  config-server:latest
```

### Kubernetes

```bash
# Apply deployment
kubectl apply -f k8s/config-server-deployment.yaml

# Check status
kubectl get pods -l app=config-server -n food-delivery
```

## Endpoints

### Health Check
```
GET http://localhost:8888/actuator/health
```

### Fetch Configuration
```
GET http://localhost:8888/{application}/{profile}
GET http://localhost:8888/{application}/default

Examples:
GET http://localhost:8888/user-service/default
GET http://localhost:8888/user-service/dev
GET http://localhost:8888/api-gateway/prod
```

### Configuration Properties
```
GET http://localhost:8888/actuator/configprops
```

## Testing

```bash
# Run tests
mvn test

# Run with coverage
mvn verify
```

## Monitoring

Config Server exposes the following actuator endpoints:

- `/actuator/health` - Health status
- `/actuator/info` - Application info
- `/actuator/env` - Environment properties
- `/actuator/configprops` - Configuration properties

## Dependencies

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|----------|
| `SERVER_PORT` | Server port | 8888 |
| `EUREKA_SERVER_URL` | Eureka server URL | http://localhost:8761/eureka/ |
| `CONFIG_GIT_URI` | Git repository URI | file://${user.home}/config-repo |
| `SPRING_PROFILES_ACTIVE` | Active profile | default |

## Troubleshooting

### Config Server Cannot Find Git Repository

Ensure the Git repository is initialized:
```bash
cd ~/config-repo
git init
git add .
git commit -m "Initial commit"
```

### Services Cannot Connect to Config Server

Verify Config Server is running:
```bash
curl http://localhost:8888/actuator/health
```

Check service configuration:
```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
```

## License

This project is part of the Food Delivery Microservices Platform.