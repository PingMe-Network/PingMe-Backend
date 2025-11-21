package me.huynhducphu.PingMe_Backend.service.chat.publisher;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.Message;
import me.huynhducphu.PingMe_Backend.service.chat.event.MessageCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Publisher dùng để bắn các WebSocket events theo kiểu "ngay lập tức",
 * tách biệt hoàn toàn khỏi transaction hiện tại.
 *
 * <p>Mỗi lần publish sẽ chạy trong một transaction mới (REQUIRES_NEW),
 * giúp các MESSAGE_CREATED không bị gộp/batch khi được gọi trong
 * các transaction lớn (ví dụ: add nhiều group members trong 1 lần).
 *
 * <p>Mục đích:
 * - Gửi từng event WebSocket độc lập.
 * - Tránh burst / drop messages của SimpleBroker.
 * - Đảm bảo FE nhận đủ số lượng sự kiện theo đúng số lần gọi.
 *
 * <p>Class này KHÔNG sử dụng event batching hay AFTER_COMMIT.
 * Mỗi event sẽ được gửi ra ngay lập tức thông qua transaction riêng của nó.
 */
@Component
@RequiredArgsConstructor
public class ChatEventImmediatePublisher {

    private final ApplicationEventPublisher publisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishMessageCreated(Message msg) {
        publisher.publishEvent(new MessageCreatedEvent(msg));
    }

}
