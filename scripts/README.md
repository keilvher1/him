# Docker Scripts Guide

## Overview

This directory contains shell scripts to simplify Docker operations for the International Media API.

## Scripts

### build.sh
Builds the Docker image for the Spring Boot application.

#### Usage
```bash
./build.sh [options]
```

#### Options
- `-v, --version VERSION`: Set image version (default: latest)
- `-r, --registry REGISTRY`: Set Docker registry URL
- `--no-cache`: Build without using cache
- `--no-test`: Skip image testing
- `--no-cleanup`: Skip cleanup of old images
- `-h, --help`: Show help message

#### Examples
```bash
# Basic build
./build.sh

# Build with specific version
./build.sh -v v1.0.0

# Build without cache
./build.sh --no-cache

# Build for registry
./build.sh -r docker.io/myuser -v v1.0.0
```

### run.sh
Runs the Docker container for the Spring Boot application.

#### Usage
```bash
./run.sh [options]
```

#### Options
- `-d, --detached`: Run container in detached mode
- `-p, --port PORT`: Host port to bind to (default: 8080)
- `-v, --version VERSION`: Image version to run (default: latest)
- `-n, --name NAME`: Container name (default: international-media-container)
- `-e, --env KEY=VALUE`: Set environment variable
- `--profile PROFILE`: Set Spring profile
- `--java-opts OPTS`: Set Java options
- `--restart POLICY`: Restart policy (no|always|on-failure|unless-stopped)
- `--compose`: Use Docker Compose instead
- `--stop`: Stop running container
- `--logs`: Show container logs
- `--status`: Show container status
- `-h, --help`: Show help message

#### Examples
```bash
# Basic run (interactive)
./run.sh

# Run in detached mode
./run.sh -d

# Run on different port
./run.sh -p 8081

# Run with Docker profile
./run.sh --profile docker

# Run with custom Java options
./run.sh --java-opts "-Xmx1g -Xms512m"

# Use Docker Compose
./run.sh --compose

# Stop container
./run.sh --stop

# View logs
./run.sh --logs

# Check status
./run.sh --status
```

## Quick Start

1. **Build the image:**
   ```bash
   ./build.sh
   ```

2. **Run the container:**
   ```bash
   ./run.sh -d
   ```

3. **Check if it's running:**
   ```bash
   ./run.sh --status
   ```

4. **View logs:**
   ```bash
   ./run.sh --logs
   ```

5. **Stop the container:**
   ```bash
   ./run.sh --stop
   ```

## Docker Compose Alternative

For a complete setup with H2 database:

```bash
./run.sh --compose
```

This will start both the application and H2 database services.

## Troubleshooting

### Port Already in Use
If you get a port conflict:
```bash
./run.sh -p 8081  # Use different port
```

### Container Already Exists
The scripts automatically clean up existing containers with the same name.

### Image Not Found
If the image doesn't exist:
```bash
./build.sh  # Build the image first
```

### Application Not Ready
Wait for the application to fully start:
```bash
./run.sh --logs  # Check startup logs
```

## Environment Variables

Common environment variables you can set:

- `SPRING_PROFILES_ACTIVE`: Spring profile (default, docker, prod)
- `JAVA_OPTS`: JVM options
- `SPRING_DATASOURCE_URL`: Database URL
- `SPRING_H2_CONSOLE_ENABLED`: Enable H2 console

Example:
```bash
./run.sh -e SPRING_PROFILES_ACTIVE=docker -e JAVA_OPTS="-Xmx1g"
```