//package org.ping_me.consumer;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
///**
// * @author : user664dntp
// * @mailto : phatdang19052004@gmail.com
// * @created : 5/04/2026, Sunday
// **/
//
//@Component
//@Slf4j
//public class TestKafkaConsumer {
//    @KafkaListener(topics = "${spring.kafka.topic.user-chat-dev}",
//            groupId = "ping-me-audit-group",
//            containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void consumeTest(String message) {
//        log.info("======================================================");
//        log.info("ĐÃ BẮT ĐƯỢC TIN NHẮN TỪ KAFKA!");
//        log.info("Nội dung: {}", message);
//        log.info("======================================================");
//    }
//}
