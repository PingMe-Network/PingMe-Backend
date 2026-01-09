package me.huynhducphu.ping_me;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableMongoAuditing
@EnableWebSocketMessageBroker
@EnableCaching
public class PingMeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PingMeBackendApplication.class, args);
    }

}
