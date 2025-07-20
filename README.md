[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/SxatSNMX)

# 학생 관리 시스템 (Student Management System)

Spring Boot와 MariaDB를 기반으로 한 학생 관리 웹 애플리케이션입니다.

## 📋 프로젝트 소개

이 프로젝트는 학생 정보를 효율적으로 관리하기 위한 웹 애플리케이션입니다. 학생 등록, 조회, 수정, 삭제 기능을 제공하며, 검색 및 페이징 기능을 포함하고 있습니다.

## 🛠 기술 스택

- **Backend**
  - Java 17
  - Spring Boot 3.2.5
  - Spring Data JPA
  - Spring Validation
  
- **Database**
  - MariaDB (최신 버전)
  
- **Frontend**
  - Thymeleaf 템플릿 엔진
  - Bootstrap 5.3.0
  - jQuery 3.6.4
  
- **API Documentation**
  - Swagger/OpenAPI 3.0

- **Infrastructure**
  - Docker & Docker Compose
  - Maven

## 🚀 실행 방법

### 사전 요구사항
- Java 17 이상
- Docker & Docker Compose
- Maven (또는 Maven Wrapper 사용)

### 1. MariaDB 실행 (Docker Compose)

```bash
# MariaDB 컨테이너 시작
docker-compose up -d

# 컨테이너 상태 확인
docker-compose ps
```

### 2. Spring Boot 애플리케이션 실행

```bash
# Maven Wrapper 사용
./mvnw spring-boot:run

# 또는 Maven 직접 사용
mvn spring-boot:run
```

애플리케이션은 `http://localhost:8080`에서 실행됩니다.

## 📑 주요 기능

- **학생 관리**
  - 학생 등록 (이름, 이메일)
  - 학생 목록 조회 (페이징 지원)
  - 학생 상세 정보 조회
  - 학생 정보 수정
  - 학생 삭제

- **검색 기능**
  - 이름으로 검색
  - 이메일로 검색
  - 검색 결과 하이라이팅

- **UI/UX**
  - 반응형 웹 디자인 (Bootstrap)
  - 클라이언트 사이드 유효성 검증
  - 삭제 확인 다이얼로그

## 🔗 API 엔드포인트

### Web UI 엔드포인트
- `GET /students` - 학생 목록 페이지
- `GET /students/new` - 학생 등록 폼
- `POST /students` - 학생 등록 처리
- `GET /students/{id}` - 학생 상세 페이지
- `GET /students/edit/{id}` - 학생 수정 폼
- `POST /students/edit/{id}` - 학생 정보 수정 처리
- `POST /students/delete/{id}` - 학생 삭제 처리

### REST API 엔드포인트
- `GET /api/students` - 학생 목록 조회 (JSON)
- `GET /api/students/{id}` - 학생 상세 조회 (JSON)
- `POST /api/students` - 학생 등록 (JSON)
- `PUT /api/students/{id}` - 학생 수정 (JSON)
- `DELETE /api/students/{id}` - 학생 삭제 (JSON)
- `GET /api/students/email/{email}` - 이메일로 학생 조회 (JSON)

### Swagger UI
API 문서는 `http://localhost:8080/swagger-ui.html`에서 확인할 수 있습니다.

## 📸 스크린샷

(스크린샷 위치)
- 학생 목록 페이지
- 학생 등록 폼
- 학생 상세 정보
- 검색 결과

## 🗄 데이터베이스 설정

### MariaDB 연결 정보
- Host: localhost
- Port: 3306
- Database: school
- Username: root
- Password: 1234

### Docker Compose 사용법

```bash
# 컨테이너 시작
docker-compose up -d

# 로그 확인
docker-compose logs -f mariadb

# 컨테이너 중지
docker-compose down

# 컨테이너 및 볼륨 삭제
docker-compose down -v
```

## 🧪 테스트 데이터

애플리케이션 시작 시 `src/main/resources/data.sql` 파일의 초기 데이터가 자동으로 로드됩니다.

## 📝 개발 참고사항

- Thymeleaf 캐시는 개발 모드에서 비활성화되어 있습니다.
- JPA DDL-auto는 `update`로 설정되어 있어 스키마가 자동으로 생성/업데이트됩니다.
- 모든 날짜/시간은 시스템 기본 시간대를 사용합니다.

## 🐛 문제 해결

### MariaDB 연결 오류
1. Docker 컨테이너가 실행 중인지 확인: `docker-compose ps`
2. 포트 3306이 다른 프로세스에서 사용 중인지 확인
3. application.properties의 데이터베이스 연결 정보 확인

### 애플리케이션 시작 오류
1. Java 버전 확인: `java -version` (Java 17 이상 필요)
2. Maven 의존성 재설치: `./mvnw clean install`
3. 로그에서 상세 오류 메시지 확인

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 등록해주세요.

<img width="1461" height="990" alt="image" src="https://github.com/user-attachments/assets/88421af6-4c2f-4421-a7b7-23cb7d707902" /><img width="1461" height="990" alt="image" src="https://github.com/user-attachments/assets/71044944-55bb-485d-883a-0dae265a3296" />

