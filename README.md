
# Noticeboard

## 프로젝트 개요
공지사항 관리 시스템으로, 공지사항의 등록, 수정, 삭제, 조회 API를 제공합니다.  
기술 스택으로는 Java, Spring Boot, Hibernate, MySQL(local 환경 H2)을 사용합니다.

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

## 파일 업로드 경로 설정

Mac 환경에서 파일 업로드 경로를 설정하려면 `application-prod.yml`과 `application-local.yml`에 다음과 같은 설정을 추가합니다:

```yaml
file:
  upload:
    base-path: ${user.home}/uploads
```

윈도우 환경에서는 파일 경로를 다음과 같이 설정합니다:

```yaml
file:
  upload:
    base-path: C:/Users/${user.name}/uploads
```


## 로컬 환경 (H2)

### 2. 애플리케이션 실행 (로컬 환경)
로컬 개발 및 테스트 환경에서는 H2 인메모리 데이터베이스를 사용합니다.  
JDK 17로 빌드되고 실행됩니다. Project Structure에서 Project, Modules에서 17을 설정합니다.

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 3. H2 콘솔 접속 (로컬 환경)
- H2 Console URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

### 4. Swagger UI (로컬 환경)
API 문서를 Swagger UI를 통해 확인할 수 있습니다.  
Swagger UI는 애플리케이션 실행 후 다음 URL에서 접근할 수 있습니다.

- Swagger UI URL: `http://localhost:8080/swagger-ui.html`

### Swagger 사용 시 Spring Security 인증 (로컬 환경)
Swagger UI를 통해 API를 테스트하려면 Spring Security를 통해 로그인해야 합니다.

- Username: `user`
- Password: `password`

## 프로덕션 환경 (MySQL)

### 2. Docker Compose 실행 (프로덕션 환경)
프로덕션 환경에서는 Docker를 이용한 MySQL 데이터베이스를 사용합니다.

```bash
docker-compose up -d
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### 3. MySQL 접속 정보 (프로덕션 환경)
- URL: `jdbc:mysql://localhost:3306/noticeboard`
- Username: `admin`
- Password: `pass!@#$`

### 4. Docker MySQL 컨테이너를 사용하는 방법 (프로덕션 환경)
1. 컨테이너 ID 확인: `docker ps`
2. 도커 컨테이너 접속: `docker exec -it <container_id_or_name> mysql -u admin -p`
    - `<container_id_or_name>`를 실제 컨테이너 ID 또는 이름으로 대체합니다. 비밀번호로 `pass!@#$`을 입력합니다.
3. 데이터베이스 선택: `USE noticeboard;`
4. 테이블 목록 확인: `SHOW TABLES;`
5. 테이블 조회: `SELECT * FROM notice;`
6. 첨부파일 경로 테이블 조회: `notice_attachment_paths`

### 5. Swagger UI (프로덕션 환경)
API 문서를 Swagger UI를 통해 확인할 수 있습니다.  
Swagger UI는 애플리케이션 실행 후 다음 URL에서 접근할 수 있습니다.

- Swagger UI URL: `http://localhost:8080/swagger-ui.html`

### Swagger 사용 시 Spring Security 인증 (프로덕션 환경)
Swagger UI를 통해 API를 테스트하려면 Spring Security를 통해 로그인해야 합니다.

- Username: `user`
- Password: `password`

---

## 핵심 문제해결 전략

### 1. 데이터베이스 설정 및 관리
- **문제**: 로컬 개발 환경과 프로덕션 환경에서 데이터베이스 설정이 다릅니다.
- **전략**: 개발 및 테스트 환경에서는 H2 인메모리 데이터베이스를 사용하고, 프로덕션 환경에서는 MySQL을 사용합니다. 환경에 따라 다른 설정을 적용하기 위해 Spring Profiles을 사용합니다.

### 2. 데이터 무결성 보장
- **문제**: 잘못된 데이터 입력으로 인한 시스템 오류 발생 가능성.
- **전략**: 공지사항 생성 및 업데이트 시 필수 필드 검증과 비즈니스 로직 검증을 추가합니다.

### 3. 트랜잭션 관리
- **문제**: 데이터베이스 트랜잭션 처리 중 오류 발생 시 데이터 무결성 문제.
- **전략**: Spring의 `@Transactional` 애노테이션을 사용하여 서비스 메서드에서 트랜잭션 관리를 자동화합니다.

### 4. 보안
- **문제**: API 접근 및 데이터 조작에 대한 보안 취약점.
- **전략**: Spring Security를 사용하여 인증 및 권한 관리를 구현합니다.

### 5. 캐시 관리
- **문제**: 반복적인 데이터베이스 조회로 인한 성능 저하.
- **전략**: Ehcache를 사용하여 자주 조회되는 데이터에 대해 캐싱을 적용합니다.

### 6. 제약사항 처리
- **문제**: 잘못된 파일 업로드로 인한 오류 발생 가능성.
- **전략**: 파일 업로드 시 빈 파일이나 유효하지 않은 확장자를 가진 파일을 처리하는 로직을 추가하여 예외를 발생시킵니다.
- **문제**: 존재하지 않는 공지사항에 대한 수정 및 삭제 요청 처리.
- **전략**: 존재하지 않는 공지사항에 대한 요청 시 적절한 예외를 발생시키고 로그를 남깁니다.
