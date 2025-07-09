[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/SxatSNMX)

# Handong International Media 학회 백엔드

## 프로젝트 소개

한동대학교 국제미디어 학회를 위한 백엔드 API 서버입니다. 회원 관리, 프로젝트 관리, 이벤트 관리 기능을 제공하며, RESTful API 형태로 구현되어 있습니다.

## 기술 스택

- **Framework**: Spring Boot 3.2.5
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: H2 (In-Memory Database)
- **ORM**: Spring Data JPA
- **기타**: Lombok

## 실행 방법

### 사전 요구사항
- Java 17 이상
- Maven 3.6 이상

### 실행 명령어

```bash
# 프로젝트 빌드
mvn clean install

# 애플리케이션 실행
mvn spring-boot:run
```

애플리케이션은 기본적으로 `http://localhost:8080`에서 실행됩니다.

## API 엔드포인트 목록

### Member API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/members` | 새 회원 생성 |
| GET | `/api/members` | 모든 회원 조회 |
| GET | `/api/members/{id}` | ID로 회원 조회 |
| GET | `/api/members/email/{email}` | 이메일로 회원 조회 |
| GET | `/api/members/active` | 활성 회원 조회 |
| GET | `/api/members/role/{role}` | 역할별 회원 조회 |
| GET | `/api/members/department/{department}` | 부서별 회원 조회 |
| PUT | `/api/members/{id}` | 회원 정보 수정 |
| DELETE | `/api/members/{id}` | 회원 삭제 |

### Project API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/projects` | 새 프로젝트 생성 |
| GET | `/api/projects` | 모든 프로젝트 조회 |
| GET | `/api/projects/{id}` | ID로 프로젝트 조회 |
| GET | `/api/projects/status/{status}` | 상태별 프로젝트 조회 |
| GET | `/api/projects/date-range` | 날짜 범위로 프로젝트 조회 |
| GET | `/api/projects/member/{memberId}` | 특정 회원의 프로젝트 조회 |
| GET | `/api/projects/search?keyword={keyword}` | 제목으로 프로젝트 검색 |
| PUT | `/api/projects/{id}` | 프로젝트 정보 수정 |
| POST | `/api/projects/{projectId}/members/{memberId}` | 프로젝트에 회원 추가 |
| DELETE | `/api/projects/{projectId}/members/{memberId}` | 프로젝트에서 회원 제거 |
| DELETE | `/api/projects/{id}` | 프로젝트 삭제 |

### Event API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/events` | 새 이벤트 생성 |
| GET | `/api/events` | 모든 이벤트 조회 |
| GET | `/api/events/{id}` | ID로 이벤트 조회 |
| GET | `/api/events/status/{status}` | 상태별 이벤트 조회 |
| GET | `/api/events/type/{type}` | 유형별 이벤트 조회 |
| GET | `/api/events/date-range` | 날짜 범위로 이벤트 조회 |
| GET | `/api/events/organizer/{organizerId}` | 주최자별 이벤트 조회 |
| GET | `/api/events/participant/{memberId}` | 참가자별 이벤트 조회 |
| GET | `/api/events/upcoming` | 예정된 이벤트 조회 |
| PUT | `/api/events/{id}` | 이벤트 정보 수정 |
| PUT | `/api/events/{eventId}/organizer/{organizerId}` | 이벤트 주최자 설정 |
| POST | `/api/events/{eventId}/participants/{memberId}` | 이벤트 참가자 추가 |
| DELETE | `/api/events/{eventId}/participants/{memberId}` | 이벤트 참가자 제거 |
| DELETE | `/api/events/{id}` | 이벤트 삭제 |

## H2 콘솔 접속 방법

H2 데이터베이스 콘솔은 애플리케이션 실행 후 웹 브라우저를 통해 접속할 수 있습니다.

### 접속 정보
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (비워두기)

### 주요 테이블
- `MEMBERS`: 회원 정보
- `PROJECTS`: 프로젝트 정보
- `EVENTS`: 이벤트 정보
- `PROJECT_MEMBERS`: 프로젝트-회원 연결 테이블
- `EVENT_PARTICIPANTS`: 이벤트-참가자 연결 테이블

## 데이터 모델

### Member Entity
- 회원 기본 정보 (이름, 이메일, 학번, 학과 등)
- 역할 (PRESIDENT, VICE_PRESIDENT, SECRETARY, TREASURER, MEMBER)
- 활동 상태

### Project Entity
- 프로젝트 정보 (제목, 설명, 기간 등)
- 상태 (PLANNED, IN_PROGRESS, COMPLETED, ON_HOLD, CANCELLED)
- 참여 회원 목록 (다대다 관계)

### Event Entity
- 이벤트 정보 (제목, 설명, 일시, 장소 등)
- 유형 (WORKSHOP, SEMINAR, CONFERENCE, SOCIAL, MEETING, COMPETITION)
- 상태 (UPCOMING, ONGOING, COMPLETED, CANCELLED)
- 주최자 및 참가자 정보

## Docker 실행

### Docker 빌드 및 실행

#### 쉘 스크립트 사용 (권장)
```bash
# Docker 이미지 빌드
./build.sh

# Docker 컨테이너 실행 (백그라운드)
./run.sh -d

# 컨테이너 상태 확인
./run.sh --status

# 로그 확인
./run.sh --logs

# 컨테이너 중지
./run.sh --stop
```

#### 직접 Docker 명령어 사용
```bash
# Docker 이미지 빌드
docker build -t international-media-api .

# Docker 컨테이너 실행
docker run -p 8080:8080 international-media-api
```

### Docker Compose 실행

```bash
# Docker Compose로 실행 (H2 데이터베이스 포함)
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 특정 서비스 로그 확인
docker-compose logs -f app
docker-compose logs -f h2-database

# 종료
docker-compose down

# 볼륨까지 삭제 (데이터 초기화)
docker-compose down -v
```

#### 서비스 접속 정보
- **Spring Boot App**: `http://localhost:8080`
- **H2 Console (앱 내)**: `http://localhost:8080/h2-console`
- **H2 Console (독립)**: `http://localhost:8082`
- **H2 TCP Server**: `localhost:9092`

## API 테스트

### HTTP 클라이언트 테스트
`src/test/resources/api-test.http` 파일에 모든 API 엔드포인트에 대한 테스트 예제가 포함되어 있습니다. IntelliJ IDEA의 HTTP Client 또는 VS Code의 REST Client 확장을 사용하여 테스트할 수 있습니다.

### Postman 테스트
`International_Media_API.postman_collection.json` 파일을 Postman에서 Import하여 모든 API 엔드포인트를 테스트할 수 있습니다.
