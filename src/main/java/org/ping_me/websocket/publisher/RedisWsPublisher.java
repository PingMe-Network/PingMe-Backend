package org.ping_me.websocket.publisher;

import lombok.RequiredArgsConstructor;
import org.ping_me.websocket.WebSocketDestinations;
import org.ping_me.websocket.dto.WsBroadcastWrapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisWsPublisher {

    @Qualifier("redisWsSyncTemplate")
    private final RedisTemplate<String, Object> redisWsSyncTemplate;

    public void publish(String destination, Object payload) {
        redisWsSyncTemplate.convertAndSend(
                WebSocketDestinations.REDIS_SYNC_CHANNEL,
                new WsBroadcastWrapper(destination, payload)
        );
    }
}
