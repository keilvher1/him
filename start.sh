#!/bin/bash

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}학생 관리 시스템 시작 스크립트${NC}"
echo "=================================="

# Docker 설치 확인
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker가 설치되어 있지 않습니다. Docker를 먼저 설치해주세요.${NC}"
    exit 1
fi

# Docker Compose 설치 확인
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}Docker Compose가 설치되어 있지 않습니다. Docker Compose를 먼저 설치해주세요.${NC}"
    exit 1
fi

# MariaDB 컨테이너 시작
echo -e "${YELLOW}1. MariaDB 컨테이너 시작 중...${NC}"
docker-compose up -d

# MariaDB가 준비될 때까지 대기
echo -e "${YELLOW}2. MariaDB가 준비될 때까지 대기 중...${NC}"
sleep 10

# 컨테이너 상태 확인
echo -e "${YELLOW}3. 컨테이너 상태 확인${NC}"
docker-compose ps

# Spring Boot 애플리케이션 시작
echo -e "${YELLOW}4. Spring Boot 애플리케이션 시작 중...${NC}"
./mvnw spring-boot:run &

# 애플리케이션이 시작될 때까지 대기
echo -e "${YELLOW}5. 애플리케이션이 시작될 때까지 대기 중...${NC}"
sleep 20

# 성공 메시지
echo -e "${GREEN}=================================="
echo -e "✅ 시스템이 성공적으로 시작되었습니다!"
echo -e "=================================="
echo -e "웹 UI: http://localhost:8080/students"
echo -e "Swagger UI: http://localhost:8080/swagger-ui.html"
echo -e "=================================="
echo -e "${NC}"

# 종료 방법 안내
echo -e "${YELLOW}종료하려면:${NC}"
echo "1. Ctrl+C를 눌러 Spring Boot 종료"
echo "2. docker-compose down 명령으로 MariaDB 종료"