package ecommerce.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 기반 캐시 설정
 * 성능 최적화를 위한 도메인별 캐시 전략 적용
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Redis 기반 캐시 매니저 설정
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 기본 캐시 설정
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(10)) // 기본 TTL: 10분
                .disableCachingNullValues(); // null 값 캐싱 방지

        // 도메인별 캐시 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 상품 정보 캐시 (자주 조회되지만 변경 빈도 낮음)
        cacheConfigurations.put("products",
            defaultCacheConfig.entryTtl(Duration.ofMinutes(30)));

        // 상품 목록 캐시 (변경 빈도 낮음)
        cacheConfigurations.put("product-list",
            defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));

        // 사용자 포인트 잔액 캐시 (변경 빈도 높음)
        cacheConfigurations.put("user-balance",
            defaultCacheConfig.entryTtl(Duration.ofMinutes(2)));

        // 상품 재고 캐시 (실시간성 중요)
        cacheConfigurations.put("product-stock",
            defaultCacheConfig.entryTtl(Duration.ofSeconds(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware() // 트랜잭션 지원
                .build();
    }

    /**
     * 캐시 관련 상수 정의
     */
    public static class CacheNames {
        public static final String PRODUCTS = "products";
        public static final String PRODUCT_LIST = "product-list";
        public static final String USER_BALANCE = "user-balance";
        public static final String PRODUCT_STOCK = "product-stock";
    }
}