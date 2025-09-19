#!/usr/bin/env bash
set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
IMAGE_NAME="${IMAGE_NAME:-homepage}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
DOCKERFILE="${DOCKERFILE:-Dockerfile.alpine}"
PLATFORM="${PLATFORM:-linux/amd64}"

echo -e "${GREEN}Building GraalVM native image for homepage...${NC}"

# Function to print usage
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo "Options:"
    echo "  -s, --scratch    Use scratch image (smallest, no shell)"
    echo "  -a, --alpine     Use Alpine image (small, with shell) [default]"
    echo "  -l, --local      Build native image locally (requires GraalVM)"
    echo "  -p, --push       Push image to registry"
    echo "  -t, --tag TAG    Docker image tag (default: latest)"
    echo "  -h, --help       Show this help message"
    exit 0
}

# Parse arguments
BUILD_LOCAL=false
PUSH_IMAGE=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -s|--scratch)
            DOCKERFILE="Dockerfile"
            echo -e "${YELLOW}Using scratch base image${NC}"
            shift
            ;;
        -a|--alpine)
            DOCKERFILE="Dockerfile.alpine"
            echo -e "${YELLOW}Using Alpine base image${NC}"
            shift
            ;;
        -l|--local)
            BUILD_LOCAL=true
            shift
            ;;
        -p|--push)
            PUSH_IMAGE=true
            shift
            ;;
        -t|--tag)
            IMAGE_TAG="$2"
            shift 2
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            usage
            ;;
    esac
done

if [ "$BUILD_LOCAL" = true ]; then
    echo -e "${GREEN}Building native image locally...${NC}"

    # Check if GraalVM is installed
    if ! command -v native-image &> /dev/null; then
        echo -e "${RED}native-image not found. Please install GraalVM.${NC}"
        exit 1
    fi

    # Build with SBT
    sbt assembly

    # Create native image
    sbt nativeImage
    cp target/native-image/homepage ./homepage

    echo -e "${GREEN}Native image built: ./homepage${NC}"
    echo -e "${YELLOW}Size: $(du -h homepage | cut -f1)${NC}"
else
    echo -e "${GREEN}Building Docker image...${NC}"

    # Build Docker image
    docker build \
        --platform "$PLATFORM" \
        -f "$DOCKERFILE" \
        -t "${IMAGE_NAME}:${IMAGE_TAG}" \
        .

    echo -e "${GREEN}Docker image built: ${IMAGE_NAME}:${IMAGE_TAG}${NC}"
    echo -e "${YELLOW}Image size: $(docker images ${IMAGE_NAME}:${IMAGE_TAG} --format "{{.Size}}")${NC}"

    if [ "$PUSH_IMAGE" = true ]; then
        echo -e "${GREEN}Pushing image to registry...${NC}"
        docker push "${IMAGE_NAME}:${IMAGE_TAG}"
        echo -e "${GREEN}Image pushed successfully${NC}"
    fi
fi

echo -e "${GREEN}Build complete!${NC}"

# Show how to run
if [ "$BUILD_LOCAL" = false ]; then
    echo -e "\n${YELLOW}To run the container:${NC}"
    echo "  docker run -p 8080:8080 ${IMAGE_NAME}:${IMAGE_TAG}"
else
    echo -e "\n${YELLOW}To run the native binary:${NC}"
    echo "  ./homepage"
fi
