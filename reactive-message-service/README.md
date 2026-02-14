# PingMe Message Reactive Service

Service này tách riêng phần đọc/ghi message theo hướng **Spring WebFlux + Reactive MongoDB**.

## Scope hiện tại
- `POST /messages`: gửi message (idempotent theo `roomId + senderId + clientMsgId`).
- `GET /messages/history`: load lịch sử theo `roomId`, hỗ trợ `beforeId` và `size`.
- `DELETE /messages/{id}/recall`: thu hồi message.

## Chạy local
```bash
cd reactive-message-service
mvn spring-boot:run
```

## ENV
- `SPRING_DATA_MONGODB_URI` (default: `mongodb://localhost:27017/pingme_chat`)

## Ghi chú kiến trúc
- Reactive stack không dùng JPA/Hibernate ORM truyền thống.
- Dùng `ReactiveMongoRepository` để mapping document + truy vấn non-blocking.
