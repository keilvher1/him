#!/bin/bash

# International Media API - Docker Run Script
# This script runs the Docker container for the Spring Boot application

set -e  # Exit on any error

# Configuration
APP_NAME="international-media-api"
VERSION="latest"
CONTAINER_NAME="international-media-container"
HOST_PORT="8080"
CONTAINER_PORT="8080"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to check if image exists
check_image() {
    if ! docker image inspect "${APP_NAME}:${VERSION}" > /dev/null 2>&1; then
        print_error "Docker image ${APP_NAME}:${VERSION} not found."
        print_status "Please build the image first: ./build.sh"
        exit 1
    fi
}

# Function to check if port is available
check_port() {
    if lsof -Pi :${HOST_PORT} -sTCP:LISTEN -t > /dev/null 2>&1; then
        print_error "Port ${HOST_PORT} is already in use."
        print_status "Please stop the service using port ${HOST_PORT} or use a different port with -p option."
        exit 1
    fi
}

# Function to stop and remove existing container
cleanup_container() {
    if docker ps -a --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
        print_status "Stopping and removing existing container: ${CONTAINER_NAME}"
        docker stop "${CONTAINER_NAME}" > /dev/null 2>&1 || true
        docker rm "${CONTAINER_NAME}" > /dev/null 2>&1 || true
        print_success "Existing container removed"
    fi
}

# Function to run the container
run_container() {
    print_status "Running container: ${CONTAINER_NAME}"
    print_status "Image: ${APP_NAME}:${VERSION}"
    print_status "Port mapping: ${HOST_PORT}:${CONTAINER_PORT}"
    
    # Build docker run command
    DOCKER_CMD="docker run"
    
    if [ "$DETACHED" = true ]; then
        DOCKER_CMD="$DOCKER_CMD -d"
    else
        DOCKER_CMD="$DOCKER_CMD -it"
    fi
    
    DOCKER_CMD="$DOCKER_CMD --name ${CONTAINER_NAME}"
    DOCKER_CMD="$DOCKER_CMD -p ${HOST_PORT}:${CONTAINER_PORT}"
    
    # Add environment variables
    if [ ! -z "$SPRING_PROFILES_ACTIVE" ]; then
        DOCKER_CMD="$DOCKER_CMD -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}"
    fi
    
    if [ ! -z "$JAVA_OPTS" ]; then
        DOCKER_CMD="$DOCKER_CMD -e JAVA_OPTS='${JAVA_OPTS}'"
    fi
    
    # Add restart policy
    if [ "$RESTART_POLICY" != "no" ]; then
        DOCKER_CMD="$DOCKER_CMD --restart ${RESTART_POLICY}"
    fi
    
    # Add the image
    DOCKER_CMD="$DOCKER_CMD ${APP_NAME}:${VERSION}"
    
    # Execute the command
    print_status "Executing: $DOCKER_CMD"
    eval $DOCKER_CMD
    
    if [ $? -eq 0 ]; then
        print_success "Container started successfully"
        if [ "$DETACHED" = true ]; then
            print_status "Container is running in detached mode"
            print_status "View logs: docker logs -f ${CONTAINER_NAME}"
        fi
    else
        print_error "Failed to start container"
        exit 1
    fi
}

# Function to show container status
show_status() {
    print_status "Container status:"
    docker ps --filter "name=${CONTAINER_NAME}" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
}

# Function to wait for application to be ready
wait_for_ready() {
    if [ "$DETACHED" = true ]; then
        print_status "Waiting for application to be ready..."
        
        local max_attempts=30
        local attempt=1
        
        while [ $attempt -le $max_attempts ]; do
            if curl -f http://localhost:${HOST_PORT}/api/members > /dev/null 2>&1; then
                print_success "Application is ready!"
                print_status "API is available at: http://localhost:${HOST_PORT}/api"
                print_status "H2 Console: http://localhost:${HOST_PORT}/h2-console"
                return 0
            fi
            
            echo -n "."
            sleep 2
            attempt=$((attempt + 1))
        done
        
        print_warning "Application may still be starting up. Check logs: docker logs ${CONTAINER_NAME}"
    fi
}

# Function to show usage information
show_usage() {
    echo "Usage: $0 [options]"
    echo ""
    echo "Options:"
    echo "  -d, --detached              Run container in detached mode"
    echo "  -p, --port PORT             Host port to bind to (default: 8080)"
    echo "  -v, --version VERSION       Image version to run (default: latest)"
    echo "  -n, --name NAME             Container name (default: international-media-container)"
    echo "  -e, --env KEY=VALUE         Set environment variable"
    echo "  --profile PROFILE           Set Spring profile (default: default)"
    echo "  --java-opts OPTS            Set Java options"
    echo "  --restart POLICY            Restart policy (no|always|on-failure|unless-stopped)"
    echo "  --compose                   Use Docker Compose instead"
    echo "  --stop                      Stop running container"
    echo "  --logs                      Show container logs"
    echo "  --status                    Show container status"
    echo "  -h, --help                  Show this help message"
}

# Function to stop container
stop_container() {
    if docker ps --filter "name=${CONTAINER_NAME}" --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
        print_status "Stopping container: ${CONTAINER_NAME}"
        docker stop "${CONTAINER_NAME}"
        print_success "Container stopped"
    else
        print_warning "Container ${CONTAINER_NAME} is not running"
    fi
}

# Function to show logs
show_logs() {
    if docker ps -a --filter "name=${CONTAINER_NAME}" --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
        print_status "Showing logs for container: ${CONTAINER_NAME}"
        docker logs -f "${CONTAINER_NAME}"
    else
        print_error "Container ${CONTAINER_NAME} does not exist"
    fi
}

# Function to run with Docker Compose
run_with_compose() {
    print_status "Using Docker Compose to run the application..."
    
    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml not found"
        exit 1
    fi
    
    # Stop existing services
    docker-compose down 2>/dev/null || true
    
    # Start services
    docker-compose up -d
    
    if [ $? -eq 0 ]; then
        print_success "Services started with Docker Compose"
        print_status "Application: http://localhost:8080"
        print_status "H2 Console (App): http://localhost:8080/h2-console"
        print_status "H2 Console (Standalone): http://localhost:8082"
        print_status "View logs: docker-compose logs -f"
        print_status "Stop services: docker-compose down"
    else
        print_error "Failed to start services with Docker Compose"
        exit 1
    fi
}

# Main execution
main() {
    echo "====================================="
    echo "International Media API - Docker Run"
    echo "====================================="
    
    # Default values
    DETACHED=false
    RESTART_POLICY="unless-stopped"
    SPRING_PROFILES_ACTIVE=""
    JAVA_OPTS=""
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -d|--detached)
                DETACHED=true
                shift
                ;;
            -p|--port)
                HOST_PORT="$2"
                shift 2
                ;;
            -v|--version)
                VERSION="$2"
                shift 2
                ;;
            -n|--name)
                CONTAINER_NAME="$2"
                shift 2
                ;;
            -e|--env)
                ENV_VAR="$2"
                DOCKER_CMD="$DOCKER_CMD -e $ENV_VAR"
                shift 2
                ;;
            --profile)
                SPRING_PROFILES_ACTIVE="$2"
                shift 2
                ;;
            --java-opts)
                JAVA_OPTS="$2"
                shift 2
                ;;
            --restart)
                RESTART_POLICY="$2"
                shift 2
                ;;
            --compose)
                run_with_compose
                exit 0
                ;;
            --stop)
                stop_container
                exit 0
                ;;
            --logs)
                show_logs
                exit 0
                ;;
            --status)
                show_status
                exit 0
                ;;
            -h|--help)
                show_usage
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
    done
    
    # Check prerequisites
    check_docker
    check_image
    check_port
    
    # Run the container
    cleanup_container
    run_container
    
    if [ "$DETACHED" = true ]; then
        show_status
        wait_for_ready
        
        echo ""
        echo "Container is running successfully!"
        echo "• Application: http://localhost:${HOST_PORT}"
        echo "• H2 Console: http://localhost:${HOST_PORT}/h2-console"
        echo "• API Docs: http://localhost:${HOST_PORT}/api"
        echo ""
        echo "Useful commands:"
        echo "• View logs: docker logs -f ${CONTAINER_NAME}"
        echo "• Stop container: docker stop ${CONTAINER_NAME}"
        echo "• Or use: ./run.sh --stop"
    fi
}

# Run main function
main "$@"