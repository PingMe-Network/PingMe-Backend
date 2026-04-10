# WebSocket Package Guide

Tài liệu này mô tả cách tổ chức package `org.ping_me.websocket` sau khi refactor.

## Mục tiêu

- Tách rõ phần `config`, `auth`, `presence`, `controller`, `publisher`, `sync`
- Tránh việc class inbound STOMP bị đặt chung với outbound publisher
- Giữ nguyên behavior hiện tại của hệ thống WebSocket

## Cấu trúc package

### `org.ping_me.websocket.config`

- `WebSocketConfiguration`
- Chịu trách nhiệm đăng ký endpoint `/core-service/ws`
- Cấu hình simple broker, heartbeat và inbound interceptor

### `org.ping_me.websocket.auth`

- `StompAuthInterceptor`
- `UserSocketPrincipal`
- Chịu trách nhiệm xác thực JWT lúc `CONNECT`
- Kiểm tra quyền subscribe vào room lúc `SUBSCRIBE`

### `org.ping_me.websocket.presence`

- `UserPresenceListener`
- Lắng nghe `SessionConnectedEvent` và `SessionDisconnectEvent`
- Cập nhật trạng thái `ONLINE/OFFLINE`
- Gửi `/user/{id}/queue/status` cho bạn bè của user đó

### `org.ping_me.websocket.controller`

- `ChatTypingController`
- Đây là inbound STOMP handler
- Nhận `@MessageMapping("/rooms/{roomId}/typing")`
- Chuẩn hóa typing signal rồi publish qua Redis sync channel

### `org.ping_me.websocket.publisher`

- `RedisWsPublisher`
- `ChatMessageEventPublisher`
- `ChatRoomEventPublisher`
- `FriendshipEventPublisher`
- Đây là outbound publisher layer
- `RedisWsPublisher` là helper dùng chung để bọc payload và publish qua Redis
- `ChatMessageEventPublisher` xử lý event mức message
- `ChatRoomEventPublisher` xử lý event mức room/member
- Lắng nghe domain event sau transaction commit
- Đóng gói payload WebSocket rồi đẩy qua Redis channel `pingme-ws-sync`

### `org.ping_me.websocket`

- `WebSocketDestinations`
- Chứa hằng số channel Redis, queue suffix và helper build destination
- Mục tiêu là giảm string hard-code rải rác trong code

### `org.ping_me.websocket.sync`

- `RedisWsReceiver`
- Nhận message từ Redis Pub/Sub
- Deserialize `WsBroadcastWrapper`
- Forward sang `SimpMessagingTemplate`

## DTO liên quan

DTO WebSocket hiện nằm ngay dưới namespace websocket:

- `org.ping_me.websocket.dto`
- `org.ping_me.websocket.dto.chat.*`
- `org.ping_me.websocket.dto.friendship.*`
- `org.ping_me.websocket.dto.user_status.*`

Lợi ích:

- Toàn bộ code WebSocket nằm chung một namespace
- Dễ tìm payload khi lần theo luồng runtime
- Không còn tách rời giữa runtime package và DTO package

## Luồng chính

### 1. Client kết nối WebSocket

1. Client connect vào `/core-service/ws`
2. `StompAuthInterceptor` đọc header `Authorization`
3. JWT hợp lệ thì tạo `UserSocketPrincipal`
4. Khi client subscribe room, interceptor kiểm tra user có thuộc room đó hay không

### 2. Client gửi typing signal

1. Client gửi vào `/app/rooms/{roomId}/typing`
2. `ChatTypingController` nhận signal
3. Controller build `MessageTypingEventPayload`
4. Payload được bọc trong `WsBroadcastWrapper`
5. Dữ liệu được publish lên Redis channel `pingme-ws-sync`
6. `RedisWsReceiver` nhận lại và forward ra `/topic/rooms/{roomId}/typing`

### 3. Backend phát sự kiện chat/friendship

1. Service domain phát event nội bộ sau khi xử lý business
2. `ChatMessageEventPublisher`, `ChatRoomEventPublisher` hoặc `FriendshipEventPublisher` bắt event sau `AFTER_COMMIT`
3. Publisher build payload WebSocket tương ứng
4. Publisher gọi `RedisWsPublisher`
5. `RedisWsPublisher` đẩy payload qua Redis sync
6. `RedisWsReceiver` forward payload ra destination cuối cùng

### 4. Presence online/offline

1. Khi session WS connect/disconnect, Spring phát event
2. `UserPresenceListener` cập nhật trạng thái user
3. Listener gửi payload trạng thái đến queue status của bạn bè

## Quy ước đặt lớp

- `Controller`: nhận inbound STOMP message từ client
- `Publisher`: phát outbound event từ backend ra client
- `Listener`: nghe event từ Spring runtime hoặc lifecycle
- `Sync`: bridge giữa Redis Pub/Sub và broker nội bộ
- `Auth`: xác thực và phân quyền cho kết nối/subscription
- `Destinations`: giữ string và helper tạo destination

## Điểm cần nhớ khi mở rộng

- Nếu client gửi message vào server qua STOMP: thêm ở `websocket.controller`
- Nếu backend phát event ra client sau business event: thêm ở `websocket.publisher`
- Nếu là connect/disconnect/presence: thêm ở `websocket.presence`
- Nếu là auth/subscription guard: thêm ở `websocket.auth`
- Nếu cần thêm queue/topic/channel mới: thêm trước ở `WebSocketDestinations`
- Không đặt inbound STOMP handler vào package `publisher`
