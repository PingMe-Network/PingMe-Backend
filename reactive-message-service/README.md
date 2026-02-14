# PingMe Message Reactive Service

Service này chỉ tách riêng phần **reactive read/write message** cho chat bằng **Spring WebFlux + Reactive MongoDB**.

## Scope hiện tại
- `POST /messages`: gửi message (idempotent theo `roomId + senderId(from JWT) + clientMsgId`).
- `GET /messages/history`: load lịch sử theo `roomId`, hỗ trợ `beforeId` và `size`.
- `DELETE /messages/{id}/recall`: thu hồi message.

## Security
- Service này dùng Resource Server để **verify JWT hợp lệ**.
- Không quản lý login/refresh token.
- `senderId` được lấy từ claim `id` trong JWT.

## WebSocket responsibility
- Service này **không quản lý WebSocket/STOMP**.
- WebSocket tiếp tục để ở Core Service như kiến trúc hiện tại.

## Chạy local
```bash
cd reactive-message-service
mvn spring-boot:run
```

## ENV
- `SPRING_DATA_MONGODB_URI` (default: `mongodb://localhost:27017/pingme_chat`)
- `JWT_JWK_SET_URI` (default: `http://localhost:8080/oauth2/jwks`)
