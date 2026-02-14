package me.huynhducphu.pingme_message_reactive.repository;

import me.huynhducphu.pingme_message_reactive.model.MessageDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReactiveMessageRepository extends ReactiveMongoRepository<MessageDocument, String> {

    Mono<MessageDocument> findByRoomIdAndSenderIdAndClientMsgId(Long roomId, Long senderId, UUID clientMsgId);

    Flux<MessageDocument> findByRoomIdOrderByIdDesc(Long roomId, Pageable pageable);

    Flux<MessageDocument> findByRoomIdAndIdLessThanOrderByIdDesc(Long roomId, String beforeId, Pageable pageable);
}
