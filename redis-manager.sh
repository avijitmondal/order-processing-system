#!/bin/bash

################################################################################
# Redis Manager Script
# Manages Redis container for Order Processing System
################################################################################

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
CONTAINER_NAME="redis"
IMAGE="redis:7-alpine"
PORT="6379"

################################################################################
# Functions
################################################################################

# Print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
}

# Start Redis container
start_redis() {
    check_docker
    
    if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
            print_info "Redis container is already running"
            return
        else
            print_info "Starting existing Redis container..."
            docker start ${CONTAINER_NAME}
            print_success "Redis container started"
            return
        fi
    fi
    
    print_info "Creating and starting new Redis container..."
    docker run -d \
        --name ${CONTAINER_NAME} \
        -p ${PORT}:6379 \
        -v redis-data:/data \
        ${IMAGE} \
        redis-server --appendonly yes
    
    # Wait for Redis to be ready
    sleep 2
    
    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        print_success "Redis container created and started successfully"
        print_info "Redis is available at localhost:${PORT}"
    else
        print_error "Failed to start Redis container"
        exit 1
    fi
}

# Stop Redis container
stop_redis() {
    check_docker
    
    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        print_info "Stopping Redis container..."
        docker stop ${CONTAINER_NAME}
        print_success "Redis container stopped"
    else
        print_info "Redis container is not running"
    fi
}

# Check Redis status
check_status() {
    check_docker
    
    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        print_success "Redis container is running"
        echo ""
        echo "Container Details:"
        docker ps --filter "name=${CONTAINER_NAME}" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
        echo ""
        
        # Try to ping Redis
        if docker exec ${CONTAINER_NAME} redis-cli ping &> /dev/null; then
            print_success "Redis is responding to PING"
            
            # Get Redis info
            echo ""
            echo "Redis Info:"
            docker exec ${CONTAINER_NAME} redis-cli INFO server | grep -E "redis_version|uptime_in_seconds|tcp_port"
            
            # Get database stats
            echo ""
            echo "Database Stats:"
            docker exec ${CONTAINER_NAME} redis-cli INFO stats | grep -E "total_connections_received|total_commands_processed|keyspace"
            
            # Get keyspace info
            echo ""
            echo "Keyspace:"
            docker exec ${CONTAINER_NAME} redis-cli INFO keyspace
        else
            print_error "Redis is not responding"
        fi
    else
        print_error "Redis container is not running"
        
        if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
            print_info "Container exists but is stopped. Use './redis-manager.sh start' to start it."
        else
            print_info "Container does not exist. Use './redis-manager.sh start' to create it."
        fi
    fi
}

# View Redis logs
view_logs() {
    check_docker
    
    if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        print_info "Showing Redis logs (Ctrl+C to exit)..."
        docker logs -f ${CONTAINER_NAME}
    else
        print_error "Redis container does not exist"
        exit 1
    fi
}

# Open Redis CLI
open_cli() {
    check_docker
    
    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        print_info "Opening Redis CLI (type 'exit' or press Ctrl+D to quit)..."
        docker exec -it ${CONTAINER_NAME} redis-cli
    else
        print_error "Redis container is not running. Start it first with './redis-manager.sh start'"
        exit 1
    fi
}

# Flush all data
flush_data() {
    check_docker
    
    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        echo -e "${YELLOW}⚠ WARNING: This will delete ALL data in Redis!${NC}"
        read -p "Are you sure you want to continue? (yes/no): " confirm
        
        if [ "$confirm" = "yes" ]; then
            print_info "Flushing all Redis data..."
            docker exec ${CONTAINER_NAME} redis-cli FLUSHALL
            print_success "All data has been deleted"
        else
            print_info "Operation cancelled"
        fi
    else
        print_error "Redis container is not running"
        exit 1
    fi
}

# Monitor Redis commands
monitor_redis() {
    check_docker
    
    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        print_info "Monitoring Redis commands (Ctrl+C to exit)..."
        docker exec -it ${CONTAINER_NAME} redis-cli MONITOR
    else
        print_error "Redis container is not running. Start it first with './redis-manager.sh start'"
        exit 1
    fi
}

# Show usage
show_usage() {
    cat << EOF
Redis Manager for Order Processing System

Usage: $0 [command]

Commands:
    start       Start Redis container
    stop        Stop Redis container
    status      Check Redis status and display info
    logs        View Redis logs (follow mode)
    cli         Open interactive Redis CLI
    monitor     Monitor Redis commands in real-time
    flush       Flush all Redis data (with confirmation)
    help        Show this help message

Examples:
    $0 start        # Start Redis
    $0 status       # Check if Redis is running
    $0 cli          # Open Redis CLI

Redis Configuration:
    Container: ${CONTAINER_NAME}
    Image: ${IMAGE}
    Port: ${PORT}

EOF
}

################################################################################
# Main
################################################################################

case "${1:-}" in
    start)
        start_redis
        ;;
    stop)
        stop_redis
        ;;
    status)
        check_status
        ;;
    logs)
        view_logs
        ;;
    cli)
        open_cli
        ;;
    flush)
        flush_data
        ;;
    monitor)
        monitor_redis
        ;;
    help|--help|-h)
        show_usage
        ;;
    *)
        if [ -n "${1:-}" ]; then
            print_error "Unknown command: $1"
            echo ""
        fi
        show_usage
        exit 1
        ;;
esac

exit 0
