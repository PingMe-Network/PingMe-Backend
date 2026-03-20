# PingMe Core Service

`PingMe Core Service` là backend chính của hệ thống PingMe, xây dựng bằng Spring Boot.
Service cung cấp:
- REST API
- WebSocket (STOMP) cho realtime
- Lưu trữ dữ liệu với MariaDB + MongoDB
- Cache với Redis
- Tích hợp S3, AI và các dịch vụ ngoài qua OpenFeign

## Công nghệ sử dụng
- Java 21, Spring Boot 4
- Spring Web MVC, Validation, Security (OAuth2 Resource Server)
- Spring Data JPA (MariaDB), Spring Data MongoDB
- Spring Cache + Redis
- WebSocket (STOMP)
- OpenFeign, Resilience4j
- Spring AI (OpenAI), Groq AI
- AWS SDK (S3)

## Yêu cầu môi trường
- JDK 21
- Maven 3.9+
- MariaDB
- MongoDB
- Redis
- (Tuỳ chọn) AWS credentials để upload file lên S3

## Cấu hình
Ứng dụng đọc cấu hình từ `application.properties` và biến môi trường.

Biến môi trường tối thiểu:
```env
SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/pingme
SPRING_DATASOURCE_USERNAME=pingme
SPRING_DATASOURCE_PASSWORD=secret

SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/pingme

REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

JWT_SECRET=change-me
MESSAGES_AES_KEY=change-me
CORS_ALLOWED_ORIGINS=http://localhost:3000

AWS_ACCESS_KEY=...
AWS_SECERT_KEY=...
AWS_REGION=ap-southeast-1
AWS_S3_BUCKET_NAME=...
AWS_S3_DOMAIN=...

WEATHER_API_BASE_URL=https://api.openweathermap.org/data/2.5/weather
WEATHER_API_KEY=...

SPRING_AI_OPENAI_API_KEY=...
SPRING_AI_OPENAI_CHAT_MODEL=...
GROQ_AI_API_KEY=...
GROQ_AI_API_URL=...

MAIL_SERVICE_URL=http://localhost:8081
MAIL_DEFAULT_OTP=000000

APP_REELS_MAX_VIDEO_SIZE=20MB
APP_REELS_FOLDER=reels
APP_MESSAGES_CACHE_ENABLED=true
APP_INTERNAL_SECRET=...
```

## Chạy local
Chạy với profile `dev`:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Port mặc định: `8080`

Kiểm tra health:
```http
GET /actuator/health
```

## Build
```bash
./mvnw -DskipTests package
```

Chạy file jar:
```bash
java -jar target/pingme-core-service-1.0.0.jar
```

## Build/PUSH Docker image với Jib
```bash
./mvnw -DskipTests clean compile jib:build \
  -Djib.to.auth.username=YOUR_DOCKER_USERNAME \
  -Djib.to.auth.password=YOUR_DOCKER_PASSWORD
```

## CI/CD
GitHub Actions build và đẩy Docker image bằng Jib, sau đó deploy lên AWS Elastic Beanstalk.
Xem chi tiết tại `.github/workflows/deploy.yml`.

