#!/bin/bash

# International Media API - Docker Build Script
# This script builds the Docker image for the Spring Boot application

set -e  # Exit on any error

# Configuration
APP_NAME="international-media-api"
VERSION="latest"
DOCKER_REGISTRY=""  # Add your registry URL if needed

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

# Function to clean up old images
cleanup_images() {
    print_status "Cleaning up old images..."
    
    # Remove dangling images
    if docker images -f "dangling=true" -q | grep -q .; then
        docker rmi $(docker images -f "dangling=true" -q) 2>/dev/null || true
        print_success "Removed dangling images"
    else
        print_status "No dangling images to remove"
    fi
    
    # Remove old versions of the app image (keep latest)
    OLD_IMAGES=$(docker images "${APP_NAME}" --format "table {{.Repository}}:{{.Tag}}" | tail -n +2 | head -n -1)
    if [ ! -z "$OLD_IMAGES" ]; then
        echo "$OLD_IMAGES" | xargs -r docker rmi 2>/dev/null || true
        print_success "Removed old app images"
    fi
}

# Function to build the Docker image
build_image() {
    print_status "Building Docker image: ${APP_NAME}:${VERSION}"
    
    # Build the image
    docker build -t "${APP_NAME}:${VERSION}" . \
        --build-arg BUILD_DATE=$(date -u +'%Y-%m-%dT%H:%M:%SZ') \
        --build-arg VCS_REF=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
    
    if [ $? -eq 0 ]; then
        print_success "Docker image built successfully: ${APP_NAME}:${VERSION}"
    else
        print_error "Failed to build Docker image"
        exit 1
    fi
}

# Function to tag image for registry
tag_image() {
    if [ ! -z "$DOCKER_REGISTRY" ]; then
        FULL_IMAGE_NAME="${DOCKER_REGISTRY}/${APP_NAME}:${VERSION}"
        print_status "Tagging image for registry: ${FULL_IMAGE_NAME}"
        docker tag "${APP_NAME}:${VERSION}" "${FULL_IMAGE_NAME}"
        print_success "Image tagged: ${FULL_IMAGE_NAME}"
    fi
}

# Function to show image info
show_image_info() {
    print_status "Image information:"
    docker images "${APP_NAME}" --format "table {{.Repository}}\t{{.Tag}}\t{{.ID}}\t{{.CreatedAt}}\t{{.Size}}"
}

# Function to run basic image tests
test_image() {
    print_status "Running basic image tests..."
    
    # Test if image can start
    CONTAINER_ID=$(docker run -d -p 8081:8080 "${APP_NAME}:${VERSION}")
    
    if [ $? -eq 0 ]; then
        print_success "Container started successfully"
        
        # Wait for application to start
        print_status "Waiting for application to start..."
        sleep 30
        
        # Test health endpoint
        if curl -f http://localhost:8081/api/members > /dev/null 2>&1; then
            print_success "Health check passed"
        else
            print_warning "Health check failed - application might need more time to start"
        fi
        
        # Cleanup test container
        docker stop "$CONTAINER_ID" > /dev/null 2>&1
        docker rm "$CONTAINER_ID" > /dev/null 2>&1
        print_status "Test container cleaned up"
    else
        print_error "Failed to start test container"
        exit 1
    fi
}

# Main execution
main() {
    echo "======================================="
    echo "International Media API - Docker Build"
    echo "======================================="
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -v|--version)
                VERSION="$2"
                shift 2
                ;;
            -r|--registry)
                DOCKER_REGISTRY="$2"
                shift 2
                ;;
            --no-cache)
                BUILD_ARGS="--no-cache"
                shift
                ;;
            --no-test)
                SKIP_TEST=true
                shift
                ;;
            --no-cleanup)
                SKIP_CLEANUP=true
                shift
                ;;
            -h|--help)
                echo "Usage: $0 [options]"
                echo "Options:"
                echo "  -v, --version VERSION    Set image version (default: latest)"
                echo "  -r, --registry REGISTRY  Set Docker registry URL"
                echo "  --no-cache              Build without using cache"
                echo "  --no-test               Skip image testing"
                echo "  --no-cleanup            Skip cleanup of old images"
                echo "  -h, --help              Show this help message"
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                exit 1
                ;;
        esac
    done
    
    # Check prerequisites
    check_docker
    
    # Build process
    if [ "$SKIP_CLEANUP" != true ]; then
        cleanup_images
    fi
    
    build_image
    tag_image
    show_image_info
    
    if [ "$SKIP_TEST" != true ]; then
        test_image
    fi
    
    print_success "Build process completed successfully!"
    echo ""
    echo "Next steps:"
    echo "1. Run the application: ./run.sh"
    echo "2. Or use Docker Compose: docker-compose up -d"
    
    if [ ! -z "$DOCKER_REGISTRY" ]; then
        echo "3. Push to registry: docker push ${DOCKER_REGISTRY}/${APP_NAME}:${VERSION}"
    fi
}

# Run main function
main "$@"