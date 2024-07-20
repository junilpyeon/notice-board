
# Noticeboard

## 프로젝트 개요
공지사항 관리 시스템으로, 공지사항의 등록, 수정, 삭제, 조회 API를 제공합니다.  
기술 스택으로는 Java, Spring Boot, Hibernate, MySQL(local환경 H2)을 사용합니다.

## 기능 및 API
### 공지사항 API
- **등록 (POST /api/notices)**
- **수정 (PUT /api/notices/{id})**
- **삭제 (DELETE /api/notices/{id})**
- **조회 (GET /api/notices/{id})**
- **목록 조회 (GET /api/notices)**

## 설치 및 실행 방법

### 1. 클론 및 빌드
```bash
git clone https://github.com/junilpyeon/notice-board.git
cd noticeboard
./gradlew build
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. 데이터베이스 설정
- **H2**: 개발 및 테스트용 메모리 데이터베이스
- **MySQL**: Docker를 이용한 MySQL 데이터베이스

### 4. Docker Compose 실행
```bash
docker-compose up -d
```

#### H2 콘솔 접속 (local)
- H2 Console URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

#### MySQL 접속 정보 (prod)
- URL: `jdbc:mysql://localhost:3306/noticeboard`
- Username: `admin`
- Password: `pass!@#$`

Docker MySQL 컨테이너를 사용하는 방법
실행 중인 컨테이너 목록 확인:
터미널에서 실행 중인 Docker 컨테이너 목록을 확인합니다:

1. 컨테이너 ID 확인 : docker ps
2. 도커 컨테이너 접속 : docker exec -it <container_id_or_name> mysql -u admin -p
<container_id_or_name>를 실제 컨테이너 ID 또는 이름으로 대체합니다. 비밀번호로 pass!@#$을 입력합니다.
3. 데이터베이스 선택 : USE noticeboard;
4. 테이블 목록 확인 : SHOW TABLES;
5. 테이블 조회 : SELECT * FROM notice;

### 5. Swagger UI
API 문서를 Swagger UI를 통해 확인할 수 있습니다.  
Swagger UI는 애플리케이션 실행 후 다음 URL에서 접근할 수 있습니다.

- Swagger UI URL: \`http://localhost:8080/swagger-ui.html\`

#### Swagger 사용 시 Spring Security 인증
Swagger UI를 통해 API를 테스트하려면 Spring Security를 통해 로그인해야 합니다.

- Username: \`user\`
- Password: \`password\`

---

## 문제해결 전략
- 

