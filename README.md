# PingMe Core Service

Backend trung tâm của hệ sinh thái PingMe, phụ trách authentication, chat realtime, friendship, reels, AI assistant,
signaling và các tích hợp hạ tầng như Redis, S3, Mail Service, Weather API.

## Overview

`PingMe-Core-Service` cung cấp các nghiệp vụ cốt lõi cho nền tảng PingMe, bao gồm:

- Authentication và quản lý session
- Chat realtime với WebSocket STOMP
- Friendship và social interactions
- Reels và media workflows
- AI chatbox, speech-to-text và utility integrations

## Kiến trúc kỹ thuật

| Thành phần                               | Vai trò                                             |
|------------------------------------------|-----------------------------------------------------|
| Spring Boot 4                            | Nền tảng backend chính                              |
| Spring Security + OAuth2 Resource Server | Bảo vệ API, xử lý JWT                               |
| MariaDB + Spring Data JPA                | Dữ liệu quan hệ, nghiệp vụ chính                    |
| MongoDB + Spring Data MongoDB            | Dữ liệu chat/AI linh hoạt                           |
| Redis                                    | Cache, token/session support                        |
| WebSocket STOMP                          | Realtime chat, presence, signaling                  |
| OpenFeign + Resilience4j                 | Gọi service ngoài có timeout/rate limit             |
| AWS S3                                   | Lưu trữ file/media                                  |
| Spring AI + Groq/OpenAI                  | Tính năng AI chat và chuyển giọng nói thành văn bản |

## Công nghệ sử dụng

- Java 21
- Spring Boot `4.0.3`
- Spring Cloud OpenFeign `2025.1.0`
- Spring AI `2.0.0-M2`
- MariaDB
- MongoDB
- Redis
- AWS SDK S3
- Maven
- Jib

## Yêu cầu môi trường

- JDK `21`
- Maven `3.9+`
- MariaDB
- MongoDB
- Redis
- Tài khoản AWS hoặc credentials hợp lệ nếu cần upload file lên S3

## Cấu hình môi trường

Ứng dụng đọc cấu hình từ `src/main/resources/application.properties` và biến môi trường.

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

CLOUDFLARE_SECRET_KEY=...

APP_REELS_MAX_VIDEO_SIZE=20MB
APP_REELS_FOLDER=reels
APP_MESSAGES_CACHE_ENABLED=true
APP_INTERNAL_SECRET=...
```

## Chạy local nhanh

### 1. Build source

```bash
./mvnw -DskipTests package
```

### 2. Chạy với profile `dev`

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. Hoặc chạy file jar

```bash
java -jar target/pingme-core-service-1.0.0.jar
```

Mặc định service chạy tại:

- App: `http://localhost:8080`
- Health check: `GET /actuator/health`

## Docker image với Jib

```bash
./mvnw -DskipTests clean compile jib:build \
  -Djib.to.auth.username=YOUR_DOCKER_USERNAME \
  -Djib.to.auth.password=YOUR_DOCKER_PASSWORD
```

Image mặc định được đẩy theo cấu hình Maven:

```text
{YOUR_DOCKER_USERNAME}/pingme-core-service
```

## Gợi ý quy trình local

1. Khởi động MariaDB, MongoDB và Redis.
2. Cấu hình đầy đủ biến môi trường bắt buộc.
3. Chạy service với profile `dev`.
4. Kiểm tra `GET /actuator/health`.
5. Kết nối frontend/mobile app hoặc WebSocket client để test flow chat realtime.

## Cấu trúc nghiệp vụ nổi bật trong mã nguồn

```text
src/main/java/org/ping_me
├── controller      # API endpoints
├── service         # Business logic
├── repository      # JPA + Mongo repositories
├── config          # Security, WebSocket, S3, Swagger
├── publisher       # Realtime event publishing
├── dto             # Request/response models
└── utils           # Mapper, crypto, helper
```

## Observability và vận hành

- Port mặc định: `8080`
- Actuator exposure: `health`, `info`
- Virtual threads được bật
- Redis cache được bật
- WebSocket heartbeat đã cấu hình sẵn
- Có rate limit bằng Resilience4j cho một số flow chat
