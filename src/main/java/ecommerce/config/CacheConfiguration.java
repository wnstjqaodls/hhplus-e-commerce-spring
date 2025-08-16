package ecommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 기반 캐시 설정
 * - 조회 성능 개선을 위한 캐시 전략 적용
 * - TTL 설정으로 데이터 일관성 관리
 * - 캐시 키 전략 및 직렬화 설정
 */
@Slf4j
@Configuration
@EnableCaching  // 스프링 캐시 기능 활성화
@RequiredArgsConstructor
public class CacheConfiguration {

    @Value("${spring.data.redis.host}")
    private String redisHost;
    
    @Value("${spring.data.redis.port}")
    private int redisPort;

    /**
     * Redis 연결 팩토리 설정
     * - Lettuce 클라이언트 사용 (Netty 기반, 비동기 지원)
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("Redis 연결 설정 시작 - Host: {}, Port: {}", redisHost, redisPort);
        
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisHost, redisPort);
        factory.setValidateConnection(true);  // 연결 검증 활성화
        
        log.info("Redis 연결 설정 완료");
        return factory;
    }

    /**
     * RedisTemplate 설정 - 직접 Redis 조작용
     * - Key: String, Value: JSON 직렬화
     * - Hash 구조 지원
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("RedisTemplate 설정 시작");
        
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Key는 String, Value는 JSON으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        // 설정 적용
        template.afterPropertiesSet();
        
        log.info("RedisTemplate 설정 완료");
        return template;
    }

    /**
     * 캐시 매니저 설정 - @Cacheable 등에서 사용
     * - 도메인별 TTL 설정
     * - null 값 캐싱 방지
     * - LocalDateTime 등 시간 타입 지원
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        log.info(" CacheManager 설정 시작");

        // ObjectMapper 설정 (LocalDateTime 등 처리)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        // Redis 캐시 기본 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))  // 기본 TTL 10분
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)))
                .disableCachingNullValues();  // null 값은 캐싱하지 않음

        // 도메인별 TTL 설정
        RedisCacheConfiguration productConfig = defaultConfig.entryTtl(Duration.ofMinutes(30));  // 상품: 30분
        RedisCacheConfiguration pointConfig = defaultConfig.entryTtl(Duration.ofMinutes(5));     // 포인트: 5분
        RedisCacheConfiguration orderConfig = defaultConfig.entryTtl(Duration.ofMinutes(15));    // 주문: 15분

        log.info(" 도메인별 TTL 설정 - 상품: 30분, 포인트: 5분, 주문: 15분, 기본: 10분");
        
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration(CacheNames.PRODUCT, productConfig)
                .withCacheConfiguration(CacheNames.PRODUCT_LIST, productConfig)
                .withCacheConfiguration(CacheNames.USER_POINT, pointConfig)
                .withCacheConfiguration(CacheNames.ORDER_HISTORY, orderConfig)
                .build();
    }
}
