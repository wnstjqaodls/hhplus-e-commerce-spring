package ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 설정 - 분산 락 및 고급 Redis 기능
 * - 분산 환경에서 동시성 제어
 * - Redis 클러스터 지원
 */
@Slf4j
@Configuration
public class RedissonConfiguration {
    
    @Value("${spring.data.redis.host}")
    private String redisHost;
    
    @Value("${spring.data.redis.port}")
    private int redisPort;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    /**
     * Redisson 클라이언트 설정
     * - 분산 락, 스케줄러 등 고급 기능 지원
     */
    @Bean
    public RedissonClient redissonClient() {
        log.info("🔒 Redisson 클라이언트 설정 시작 - Host: {}, Port: {}", redisHost, redisPort);
        
        Config config = new Config();
        config.useSingleServer()
                .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort)
                .setConnectionMinimumIdleSize(2)      // 최소 유휴 연결 수
                .setConnectionPoolSize(10)            // 연결 풀 크기
                .setConnectTimeout(5000)              // 연결 타임아웃 5초
                .setIdleConnectionTimeout(10000)      // 유휴 연결 타임아웃 10초
                .setRetryAttempts(3)                 // 재시도 횟수
                .setRetryInterval(1500);              // 재시도 간격 1.5초

        RedissonClient redisson = Redisson.create(config);
        
        log.info("Redisson 클라이언트 설정 완료");
        return redisson;
    }
}
