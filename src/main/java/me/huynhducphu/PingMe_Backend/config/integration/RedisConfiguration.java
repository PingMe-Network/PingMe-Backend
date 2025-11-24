package me.huynhducphu.PingMe_Backend.config.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.huynhducphu.PingMe_Backend.model.common.DeviceMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin 8/16/2025
 **/
@Configuration
public class RedisConfiguration {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.password}")
    private String redisPassword;

    // =====================================================================
    // Kết nối tới Redis
    //    - Định nghĩa RedisConnectionFactory với host, port, password
    //    - Bean này dùng chung cho RedisTemplate & Spring Cache
    // =====================================================================
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration serverConfig =
                new RedisStandaloneConfiguration(redisHost, redisPort);

        if (!redisPassword.isBlank()) {
            serverConfig.setPassword(RedisPassword.of(redisPassword));
        }

        return new LettuceConnectionFactory(serverConfig);
    }


    // =====================================================================
    // Đăng ký Object Mapper
    // =====================================================================
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return om;
    }

    // =========================================================
    // RedisTemplate cho DeviceMeta (refresh token)
    // =========================================================
    @Bean
    public RedisTemplate<String, DeviceMeta> redisSessionMetaTemplate(
            RedisConnectionFactory cf,
            ObjectMapper om
    ) {
        var keySer = new StringRedisSerializer();
        var valSer = new Jackson2JsonRedisSerializer<>(om, DeviceMeta.class);

        RedisTemplate<String, DeviceMeta> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(cf);
        tpl.setKeySerializer(keySer);
        tpl.setHashKeySerializer(keySer);
        tpl.setValueSerializer(valSer);
        tpl.setHashValueSerializer(valSer);
        return tpl;
    }

    // =========================================================
    // 4. RedisTemplate cho message cache
    //    key:   String  (vd: chat:room:1:messages)
    //    value: String  (JSON của MessageResponse)
    // =========================================================
    @Bean(name = "redisMessageStringTemplate")
    public RedisTemplate<String, String> redisMessageStringTemplate(
            RedisConnectionFactory cf
    ) {
        RedisTemplate<String, String> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(cf);

        var stringSer = new StringRedisSerializer();
        tpl.setKeySerializer(stringSer);
        tpl.setHashKeySerializer(stringSer);
        tpl.setValueSerializer(stringSer);
        tpl.setHashValueSerializer(stringSer);

        tpl.afterPropertiesSet();
        return tpl;
    }

    @Bean(name = "redisDtoTemplate")
    public RedisTemplate<String, Object> redisDtoTemplate(RedisConnectionFactory cf, ObjectMapper om) {
        RedisTemplate<String, Object> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(cf);

        var keySer = new StringRedisSerializer();
        var valSer = new GenericJackson2JsonRedisSerializer(om);

        tpl.setKeySerializer(keySer);
        tpl.setHashKeySerializer(keySer);
        tpl.setValueSerializer(valSer);
        tpl.setHashValueSerializer(valSer);

        tpl.afterPropertiesSet();
        return tpl;
    }


    // =====================================================================
    // Cấu hình Spring Cache với Redis
    //    - Thiết lập thời gian sống mặc định cho cache (TTL)
    //    - Chỉ áp dụng cho các cache dùng annotation (@Cacheable, @CacheEvict...)
    // =====================================================================
    @Bean
    public RedisCacheConfiguration cacheConfiguration(ObjectMapper om) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer(om))
                )
                .entryTtl(Duration.ofMinutes(15))
                .disableCachingNullValues();
    }

    // =====================================================================
    // Khởi tạo CacheManager sử dụng Redis
    //    - Quản lý cache thông qua Spring Cache (annotation)
    //    - Tự động áp dụng các cấu hình phía trên cho toàn bộ cache
    // =====================================================================
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory, RedisCacheConfiguration baseCfg) {
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put("role_permissions", baseCfg.entryTtl(Duration.ofHours(2)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(baseCfg)
                .withInitialCacheConfigurations(configs)
                .transactionAware()
                .build();
    }
}
