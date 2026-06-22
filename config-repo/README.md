# Configuration Repository

This repository contains centralized configuration files for all microservices in the Food Delivery platform.

## Structure

```
config-repo/
├── application.yml              # Common properties for ALL services
├── application-dev.yml          # Development environment profile
├── application-prod.yml         # Production environment profile
├── eureka-server.yml            # Eureka Server specific config
├── api-gateway.yml              # API Gateway routes and CORS
├── user-service.yml             # User Service (PostgreSQL)
├── restaurant-service.yml       # Restaurant Service (PostgreSQL)
├── order-service.yml            # Order Service (PostgreSQL)
├── payment-service.yml          # Payment Service (PostgreSQL)
└── notification-service.yml     # Notification Service (MongoDB)
```

## Configuration Hierarchy

1. **application.yml** - Loaded by ALL services (common config)
2. **application-{profile}.yml** - Profile-specific common config
3. **{service-name}.yml** - Service-specific config
4. **{service-name}-{profile}.yml** - Service + profile specific config

## Usage

### Initialize Repository

```bash
cd ~/config-repo
git init
git add .
git commit -m "Initial commit: Add centralized configuration"
git branch -M main
```

### Update Configuration

```bash
# Edit configuration file
vim user-service.yml

# Commit changes
git add user-service.yml
git commit -m "Update user-service database configuration"
```

### Refresh Services (Without Restart)

```bash
# Trigger refresh on specific service
curl -X POST http://localhost:8081/actuator/refresh
```

## Configuration Files

### application.yml
Common configuration shared by all services:
- Eureka client settings
- Actuator endpoints
- Logging configuration
- JPA common settings

### Service-Specific Files
Each service has its own configuration file containing:
- Server port
- Database connection (PostgreSQL or MongoDB)
- Service-specific properties
- JPA/Hibernate settings

### Profile Files
- **dev**: Development environment (verbose logging, all actuator endpoints)
- **prod**: Production environment (minimal logging, limited actuator endpoints)

## Best Practices

1. **Never commit secrets**: Use environment variables or external secret management
2. **Use placeholders**: `${VARIABLE_NAME:default-value}`
3. **Profile-specific overrides**: Keep common config in base files
4. **Commit messages**: Describe what changed and why
5. **Test before commit**: Verify configuration syntax

## Environment Variables

Sensitive values should be externalized:

```yaml
spring:
  datasource:
    password: ${DB_PASSWORD:postgres}

jwt:
  secret: ${JWT_SECRET:change-me-in-production}
```

## Moving to Remote Repository

### GitHub Setup

```bash
# Create repository on GitHub: food-delivery-config

# Add remote
git remote add origin https://github.com/your-username/food-delivery-config.git

# Push to GitHub
git push -u origin main
```

### Update Config Server

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-username/food-delivery-config.git
          username: ${GIT_USERNAME}
          password: ${GIT_TOKEN}
```

## Troubleshooting

### Config Server Cannot Find Repository

```bash
# Verify Git repository is initialized
cd ~/config-repo
git status

# If not initialized:
git init
git add .
git commit -m "Initial commit"
```

### Configuration Not Loading

1. Check file naming: `{spring.application.name}.yml`
2. Verify Git commit: `git log`
3. Check Config Server logs
4. Test endpoint: `http://localhost:8888/{service-name}/default`

## Security Notes

⚠️ **Important:**
- Never commit database passwords
- Never commit API keys or secrets
- Use Spring Cloud Config encryption for sensitive data
- Rotate credentials regularly

## License

This configuration repository is part of the Food Delivery Microservices Platform.