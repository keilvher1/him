# Render 배포 가이드

## 개요

이 가이드는 International Media API를 Render 플랫폼의 무료 플랜으로 배포하는 방법을 설명합니다.

## Render 플랫폼 특징

### 무료 플랜 제한사항
- **메모리**: 최대 512MB
- **CPU**: 공유 CPU
- **디스크**: 1GB
- **대역폭**: 100GB/월
- **빌드 시간**: 500 빌드 분/월
- **슬립 모드**: 15분 비활성 후 자동 슬립 (웜업 시간 필요)

### 장점
- 무료 HTTPS 인증서
- 자동 배포 (Git 연동)
- 컨테이너 기반 배포
- 글로벌 CDN

## 배포 준비

### 1. 필요 파일 확인

프로젝트에 다음 파일들이 있는지 확인하세요:

```
├── render.yaml                    # Render 배포 설정
├── Dockerfile                     # Docker 이미지 빌드
├── src/main/resources/
│   ├── application.properties     # 기본 설정
│   └── application-production.properties  # 프로덕션 설정
└── src/main/java/.../config/
    └── CorsConfig.java           # CORS 설정
```

### 2. GitHub Repository 설정

1. GitHub에 프로젝트 push
2. Repository가 public이어야 함 (무료 플랜 제한)

```bash
git add .
git commit -m "Add Render deployment configuration"
git push origin main
```

## Render 배포 단계

### 1. Render 계정 생성

1. [Render.com](https://render.com) 방문
2. GitHub 계정으로 로그인
3. 무료 계정 생성

### 2. 서비스 생성

#### 방법 1: Blueprint 사용 (추천)

1. Render 대시보드에서 **"New"** → **"Blueprint"** 선택
2. GitHub repository 연결
3. `render.yaml` 파일 자동 감지
4. **"Apply"** 클릭

#### 방법 2: 수동 생성

1. **"New"** → **"Web Service"** 선택
2. GitHub repository 연결
3. 다음 설정 입력:
   - **Name**: `international-media-api`
   - **Runtime**: `Docker`
   - **Plan**: `Free`
   - **Region**: `Oregon (US West)`
   - **Branch**: `main`
   - **Dockerfile Path**: `./Dockerfile`

### 3. 환경 변수 설정

Render 대시보드에서 다음 환경 변수 설정:

```
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx450m -Xms200m -XX:+UseG1GC -XX:+UseContainerSupport
PORT=8080
SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=
SPRING_H2_CONSOLE_ENABLED=true
SPRING_H2_CONSOLE_PATH=/h2-console
SPRING_H2_CONSOLE_SETTINGS_WEB_ALLOW_OTHERS=true
```

### 4. 배포 시작

1. **"Deploy"** 버튼 클릭
2. 빌드 로그 확인
3. 배포 완료 대기 (5-10분 소요)

## 배포 후 확인

### 1. 서비스 상태 확인

배포된 서비스 URL: `https://international-media-api.onrender.com`

### 2. API 엔드포인트 테스트

```bash
# 기본 API 테스트
curl https://international-media-api.onrender.com/api/members

# 헬스 체크
curl https://international-media-api.onrender.com/actuator/health
```

### 3. H2 콘솔 접근

URL: `https://international-media-api.onrender.com/h2-console`

접속 정보:
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (비워둠)

## 주요 설정 설명

### render.yaml 구성

```yaml
services:
  - type: web
    name: international-media-api
    runtime: docker
    plan: free                    # 무료 플랜
    region: oregon               # 무료 지역
    branch: main
    dockerfilePath: ./Dockerfile
    healthCheckPath: "/api/members"  # 헬스 체크 경로
    autoDeploy: true            # 자동 배포 활성화
```

### 메모리 최적화

```yaml
env:
  - key: JAVA_OPTS
    value: "-Xmx450m -Xms200m -XX:+UseG1GC -XX:+UseContainerSupport"
```

- **-Xmx450m**: 최대 힙 메모리 450MB (512MB 제한 고려)
- **-Xms200m**: 초기 힙 메모리 200MB
- **-XX:+UseG1GC**: G1 가비지 컬렉터 사용
- **-XX:+UseContainerSupport**: 컨테이너 환경 최적화

### 데이터베이스 설정

무료 플랜에서는 영구 데이터베이스를 사용할 수 없으므로 H2 인메모리 데이터베이스를 사용합니다.

```properties
spring.datasource.url=jdbc:h2:mem:proddb
spring.jpa.hibernate.ddl-auto=create-drop
```

## 문제 해결

### 1. 빌드 실패

**문제**: Docker 빌드 실패
**해결**: Dockerfile 확인 및 메모리 제한 고려

```dockerfile
# Dockerfile에서 메모리 제한 설정
ENV JAVA_OPTS="-Xmx450m -Xms200m -XX:+UseG1GC"
```

### 2. 애플리케이션 시작 실패

**문제**: 메모리 부족으로 시작 실패
**해결**: JVM 옵션 조정

```yaml
env:
  - key: JAVA_OPTS
    value: "-Xmx400m -Xms150m -XX:+UseG1GC -XX:+UseContainerSupport"
```

### 3. 슬립 모드 문제

**문제**: 15분 후 자동 슬립
**해결**: 
- 무료 플랜의 제한사항
- 첫 요청 시 웜업 시간 필요 (30초-2분)
- 상용 서비스에서는 유료 플랜 고려

### 4. CORS 에러

**문제**: 프론트엔드에서 API 호출 시 CORS 에러
**해결**: CorsConfig 클래스에서 허용 도메인 추가

```java
@Value("${cors.allowed-origins:*}")
private String allowedOrigins;
```

## 모니터링 및 로그

### 1. 로그 확인

Render 대시보드에서:
1. 서비스 선택
2. **"Logs"** 탭 클릭
3. 실시간 로그 확인

### 2. 메트릭 확인

- CPU 사용량
- 메모리 사용량
- 네트워크 트래픽
- 응답 시간

## 업데이트 및 재배포

### 자동 배포

`main` 브랜치에 push하면 자동으로 재배포됩니다.

```bash
git add .
git commit -m "Update application"
git push origin main
```

### 수동 배포

Render 대시보드에서 **"Manual Deploy"** 버튼 클릭

## 유료 플랜 고려사항

무료 플랜의 제한사항을 벗어나려면 유료 플랜 고려:

- **Starter Plan ($7/월)**: 슬립 모드 없음, 더 많은 리소스
- **Standard Plan ($25/월)**: 더 큰 메모리, 전용 CPU
- **Database Plans**: 영구 PostgreSQL 데이터베이스

## 보안 고려사항

1. **환경 변수**: 민감한 정보는 환경 변수로 관리
2. **HTTPS**: 자동으로 제공되는 HTTPS 사용
3. **H2 콘솔**: 프로덕션에서는 비활성화 권장

## 결론

Render 플랫폼은 Spring Boot 애플리케이션을 쉽게 배포할 수 있는 좋은 플랫폼입니다. 무료 플랜으로 프로토타입이나 개발 환경에서 충분히 활용할 수 있으며, 필요에 따라 유료 플랜으로 업그레이드할 수 있습니다.