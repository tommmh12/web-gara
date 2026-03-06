# Web-Gara Deployment Guide

## Overview

This guide covers deploying the Web-Gara garage management system using Docker Compose.

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- At least 2GB free RAM
- Port 8080 and 27017 available

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/tommmh12/web-gara.git
cd web-gara
```

### 2. Configure Environment Variables

```bash
cp .env.example .env
# Edit .env with your actual values
nano .env  # or use your preferred editor
```

**Required Configuration**:
- `JWT_SECRET`: Generate using `openssl rand -base64 64`
- `MAIL_USERNAME`: Your email for sending notifications
- `MAIL_PASSWORD`: App-specific password for your email

### 3. Start the Application

```bash
docker-compose up -d
```

This will:
- Build the Spring Boot application (takes 3-5 minutes on first run)
- Start MongoDB 8.0
- Initialize the database with required collections and indexes
- Start the application on port 8080

### 4. Verify Deployment

Check that all services are running:
```bash
docker-compose ps
```

Expected output:
```
NAME                IMAGE           STATUS
web-gara-app        web-gara-app    Up
web-gara-mongo      mongo:8.0       Up (healthy)
```

### 5. Access the Application

- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## Architecture

### Services

#### Application Service (`web-gara-app`)
- **Container Name**: `web-gara-app`
- **Port**: 8080
- **Image**: Built from Dockerfile (multi-stage build)
- **Base Image**: Eclipse Temurin 25 JRE
- **Restart Policy**: `unless-stopped`

#### MongoDB Service (`web-gara-mongo`)
- **Container Name**: `web-gara-mongo`
- **Port**: 27017
- **Image**: mongo:8.0
- **Data Volume**: `mongo_data` (persistent storage)
- **Health Check**: Runs every 10s with 5s timeout
- **Initialization**: Executes `docker/mongo-init.js` on first start

### Network

- **Network Name**: `webgara-network`
- **Driver**: bridge
- **Isolation**: Services communicate internally via service names

### Volumes

- `mongo_data`: Persists MongoDB data across container restarts

## Configuration

### Environment Variables

All environment variables can be configured in `.env` file:

| Variable | Default | Description |
|----------|---------|-------------|
| `JWT_SECRET` | (required) | 256-bit secret key for JWT signing |
| `JWT_ACCESS_TOKEN_EXPIRATION` | 900000 | Access token lifetime (15 min) |
| `JWT_REFRESH_TOKEN_EXPIRATION` | 604800000 | Refresh token lifetime (7 days) |
| `MAIL_HOST` | smtp.gmail.com | SMTP server hostname |
| `MAIL_PORT` | 587 | SMTP server port |
| `MAIL_USERNAME` | (required) | Email account username |
| `MAIL_PASSWORD` | (required) | Email account password |

### Application Properties

Additional configuration in `src/main/resources/application.properties`:

- MongoDB URI: `spring.data.mongodb.uri`
- Server port: `server.port`
- File upload limits: `spring.servlet.multipart.max-file-size`
- Logging levels: `logging.level.*`

## Common Operations

### View Logs

```bash
# All services
docker-compose logs -f

# Application only
docker-compose logs -f app

# MongoDB only
docker-compose logs -f mongo
```

### Restart Services

```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart app
```

### Stop Services

```bash
# Stop without removing containers
docker-compose stop

# Stop and remove containers (preserves volumes)
docker-compose down

# Stop, remove containers, and delete volumes (CAUTION: data loss)
docker-compose down -v
```

### Rebuild Application

After code changes:
```bash
docker-compose build app
docker-compose up -d app
```

### Access MongoDB Shell

```bash
docker exec -it web-gara-mongo mongosh webgara
```

### Backup MongoDB Data

```bash
docker exec web-gara-mongo mongodump --db webgara --out /tmp/backup
docker cp web-gara-mongo:/tmp/backup ./mongodb-backup
```

### Restore MongoDB Data

```bash
docker cp ./mongodb-backup web-gara-mongo:/tmp/backup
docker exec web-gara-mongo mongorestore --db webgara /tmp/backup/webgara
```

## Monitoring

### Health Checks

The application exposes actuator endpoints:

```bash
# Basic health status
curl http://localhost:8080/actuator/health

# Detailed health (requires authentication)
curl -H "Authorization: Bearer <token>" http://localhost:8080/actuator/health
```

### MongoDB Health

MongoDB health is automatically monitored by Docker:
```bash
docker inspect --format='{{json .State.Health}}' web-gara-mongo
```

## Security Considerations

### Production Deployment

**CRITICAL**: Before deploying to production:

1. **Change JWT Secret**: Generate a secure 256-bit random key
   ```bash
   openssl rand -base64 64
   ```

2. **Use HTTPS**: Set up reverse proxy (nginx/Caddy) with SSL/TLS

3. **Restrict MongoDB Access**:
   - Don't expose port 27017 publicly
   - Use authentication (add to docker-compose.yml):
     ```yaml
     environment:
       MONGO_INITDB_ROOT_USERNAME: admin
       MONGO_INITDB_ROOT_PASSWORD: secure_password
     ```

4. **Firewall Configuration**:
   - Only allow ports 80 (HTTP), 443 (HTTPS)
   - Block direct access to 8080, 27017

5. **Environment Variables**:
   - Never commit `.env` file
   - Use secrets management (Docker Secrets, Vault)

6. **Update Application Properties**:
   ```properties
   spring.profiles.active=production
   logging.level.root=WARN
   logging.level.com.webgara=INFO
   ```

### Network Security

The default configuration uses a bridge network for service isolation. For production:

```yaml
networks:
  webgara-network:
    driver: bridge
    internal: true  # Prevent external access
```

## Troubleshooting

### Application Won't Start

**Check logs**:
```bash
docker-compose logs app
```

**Common issues**:
- MongoDB not ready: Wait 30s after first start
- Port 8080 in use: Change `SERVER_PORT` env var
- Missing JWT_SECRET: Check `.env` file

### MongoDB Connection Failed

**Verify MongoDB is healthy**:
```bash
docker-compose ps mongo
```

**Check connection string**:
```bash
docker exec web-gara-app env | grep MONGODB
```

### Build Failures

**Clear Docker cache**:
```bash
docker-compose build --no-cache app
```

**Check Java version**:
```bash
docker run --rm eclipse-temurin:25-jdk-noble java -version
```

### Out of Memory

**Increase Docker memory limit** (Docker Desktop):
- Settings → Resources → Memory → Increase to 4GB

**Set JVM memory limits** in docker-compose.yml:
```yaml
app:
  environment:
    JAVA_OPTS: "-Xmx1g -Xms512m"
  entrypoint: ["sh", "-c", "java $$JAVA_OPTS -jar app.jar"]
```

## Scaling

### Horizontal Scaling

To run multiple application instances:

```yaml
app:
  deploy:
    replicas: 3
```

Add load balancer (nginx):
```yaml
nginx:
  image: nginx:alpine
  ports:
    - "80:80"
  volumes:
    - ./nginx.conf:/etc/nginx/nginx.conf:ro
  depends_on:
    - app
```

### Vertical Scaling

Increase container resources:
```yaml
app:
  deploy:
    resources:
      limits:
        cpus: '2'
        memory: 2G
```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Build and Deploy

on:
  push:
    branches: [master]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Build and push
        run: |
          docker-compose build
          docker-compose push
      - name: Deploy
        run: |
          docker-compose up -d
```

## Support

- **Issues**: https://github.com/tommmh12/web-gara/issues
- **Documentation**: See `implementation-plan.md` for API specifications
- **API Reference**: http://localhost:8080/swagger-ui.html

## License

MIT License - See LICENSE file for details
