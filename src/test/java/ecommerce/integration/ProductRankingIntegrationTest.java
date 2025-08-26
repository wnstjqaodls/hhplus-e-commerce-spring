package ecommerce.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("상품 랭킹 시스템 통합 테스트")
public class ProductRankingIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ProductRankingIntegrationTest.class);

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private ZSetOperations<String, Object> zSetOps;

    @BeforeEach
    void setUp() {
        zSetOps = redisTemplate.opsForZSet();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("Redis SortedSet을 활용한 상품 주문 랭킹 기본 테스트")
    void testProductOrderRankingBasic() {
        // ========================================
        // 1. Redis 키 생성 
        // ========================================
        // 오늘 날짜 기반 일일 랭킹 키 생성 (예: "product:ranking:order:daily:20240824")
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd"); 
        String todayString = currentTime.format(formatter);
        String rankingKey = "product:ranking:order:daily:" + todayString;
        
        log.info("=== Redis SortedSet 기본 랭킹 테스트 시작 ===");
        log.info("사용할 Redis 키: {}", rankingKey);
        
        // ========================================
        // 2. 상품별 주문 데이터 Redis에 저장
        // ========================================
        // Redis의 incrementScore 명령어를 사용하여 상품별 주문 수를 저장
        // SortedSet 구조: key="랭킹키", member="상품ID", score="주문수"
        
        // 상품1: 10건의 주문
        String product1 = "product:1";
        double orderCount1 = 10.0;
        zSetOps.incrementScore(rankingKey, product1, orderCount1);
        log.info("{}에 {}건 주문 데이터 추가", product1, orderCount1);
        
        // 상품2: 25건의 주문 (가장 많은 주문)
        String product2 = "product:2";
        double orderCount2 = 25.0;
        zSetOps.incrementScore(rankingKey, product2, orderCount2);
        log.info("{}에 {}건 주문 데이터 추가", product2, orderCount2);
        
        // 상품3: 15건의 주문
        String product3 = "product:3";
        double orderCount3 = 15.0;
        zSetOps.incrementScore(rankingKey, product3, orderCount3);
        log.info("{}에 {}건 주문 데이터 추가", product3, orderCount3);
        
        // 상품4: 5건의 주문 (가장 적은 주문)
        String product4 = "product:4";
        double orderCount4 = 5.0;
        zSetOps.incrementScore(rankingKey, product4, orderCount4);
        log.info("{}에 {}건 주문 데이터 추가", product4, orderCount4);

        // ========================================
        // 3. Top 3 상품 조회 (점수 높은 순)
        // ========================================
        // reverseRangeWithScores: 점수 내림차순으로 데이터 조회
        // 매개변수: (키, 시작인덱스, 끝인덱스) - 0-based index
        // 0, 2 = 1위부터 3위까지 조회
        Set<ZSetOperations.TypedTuple<Object>> top3Products = zSetOps.reverseRangeWithScores(rankingKey, 0, 2);
        
        log.info("--- Top 3 상품 조회 결과 ---");
        int rank = 1;
        for (ZSetOperations.TypedTuple<Object> product : top3Products) {
            log.info("{}위: {} - {}건 주문", rank++, product.getValue(), product.getScore().intValue());
        }

        // ========================================
        // 4. 랭킹 결과 검증
        // ========================================
        // Set을 List로 변환하여 순서 보장 (Redis SortedSet은 순서를 유지함)
        List<ZSetOperations.TypedTuple<Object>> rankingList = top3Products.stream().toList();
        
        // 정확히 3개의 상품이 조회되어야 함
        assertThat(rankingList).hasSize(3);
        log.info("검증: Top 3 상품 수량 확인 완료 ({}개)", rankingList.size());
        
        // 1위 검증: product:2 (25건) - 가장 높은 주문 수
        ZSetOperations.TypedTuple<Object> firstPlace = rankingList.get(0);
        assertThat(firstPlace.getValue()).isEqualTo(product2);
        assertThat(firstPlace.getScore()).isEqualTo(25.0);
        log.info("1위 검증 완료: {} - {}건", firstPlace.getValue(), firstPlace.getScore());
        
        // 2위 검증: product:3 (15건) - 두 번째로 높은 주문 수  
        ZSetOperations.TypedTuple<Object> secondPlace = rankingList.get(1);
        assertThat(secondPlace.getValue()).isEqualTo(product3);
        assertThat(secondPlace.getScore()).isEqualTo(15.0);
        log.info("2위 검증 완료: {} - {}건", secondPlace.getValue(), secondPlace.getScore());
        
        // 3위 검증: product:1 (10건) - 세 번째로 높은 주문 수
        ZSetOperations.TypedTuple<Object> thirdPlace = rankingList.get(2);
        assertThat(thirdPlace.getValue()).isEqualTo(product1);
        assertThat(thirdPlace.getScore()).isEqualTo(10.0);
        log.info("3위 검증 완료: {} - {}건", thirdPlace.getValue(), thirdPlace.getScore());
        
        log.info("=== Redis SortedSet 기본 랭킹 테스트 완료 ===");
    }

    @Test
    @DisplayName("특정 상품의 랭킹 순위 조회 테스트")
    void testGetProductRank() {
        // Given
        String rankingKey = "product:ranking:order:daily:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        zSetOps.incrementScore(rankingKey, "product:1", 30);
        zSetOps.incrementScore(rankingKey, "product:2", 50);
        zSetOps.incrementScore(rankingKey, "product:3", 20);
        zSetOps.incrementScore(rankingKey, "product:4", 40);

        // When - 특정 상품의 순위 조회
        Long product1Rank = zSetOps.reverseRank(rankingKey, "product:1");  // 3위 (30건)
        Long product2Rank = zSetOps.reverseRank(rankingKey, "product:2");  // 1위 (50건)
        Double product1Score = zSetOps.score(rankingKey, "product:1");

        // Then
        assertThat(product1Rank).isEqualTo(2);  // 0-based index이므로 3위는 2
        assertThat(product2Rank).isEqualTo(0);  // 1위는 0
        assertThat(product1Score).isEqualTo(30.0);
    }

    @Test
    @DisplayName("동시 주문 발생 시 랭킹 원자성 테스트")
    void testConcurrentOrderRankingUpdate() throws InterruptedException {
        // Given
        String rankingKey = "product:ranking:order:daily:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String productId = "product:1";
        int threadCount = 100;
        int ordersPerThread = 1;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // When - 동시에 주문 처리 (Redis 원자성 활용)
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < ordersPerThread; j++) {
                    zSetOps.incrementScore(rankingKey, productId, 1);
                }
            });
        }

        executorService.shutdown();
        boolean terminated = executorService.awaitTermination(30, TimeUnit.SECONDS);
        assertThat(terminated).isTrue();

        // Then - 정확한 주문 수 누적 확인
        Double totalOrders = zSetOps.score(rankingKey, productId);
        assertThat(totalOrders).isEqualTo(100.0);  // 100개 스레드 * 1건 = 100건
    }

    @Test
    @DisplayName("일간/주간 랭킹 TTL 설정 테스트")
    void testRankingWithTTL() {
        // Given
        String dailyRankingKey = "product:ranking:order:daily:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String weeklyRankingKey = "product:ranking:order:weekly:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-'W'ww"));

        // When - 랭킹 데이터 추가 및 TTL 설정
        zSetOps.incrementScore(dailyRankingKey, "product:1", 10);
        zSetOps.incrementScore(weeklyRankingKey, "product:1", 10);

        // TTL 설정 (일간: 1일, 주간: 7일)
        redisTemplate.expire(dailyRankingKey, 86400, TimeUnit.SECONDS);    // 1일
        redisTemplate.expire(weeklyRankingKey, 604800, TimeUnit.SECONDS);  // 7일

        // Then - TTL 확인
        Long dailyTtl = redisTemplate.getExpire(dailyRankingKey, TimeUnit.SECONDS);
        Long weeklyTtl = redisTemplate.getExpire(weeklyRankingKey, TimeUnit.SECONDS);

        assertThat(dailyTtl).isGreaterThan(0).isLessThanOrEqualTo(86400);
        assertThat(weeklyTtl).isGreaterThan(0).isLessThanOrEqualTo(604800);
    }

    @Test
    @DisplayName("분산락을 활용한 랭킹 업데이트 테스트")
    void testRankingUpdateWithDistributedLock() throws InterruptedException {
        // Given
        String rankingKey = "product:ranking:order:daily:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String productId = "product:1";
        String lockKey = "lock:ranking:" + productId;
        int threadCount = 50;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // When - 분산락을 사용한 랭킹 업데이트
        for (int i = 0; i < threadCount; i++) {
            int orderCount = i + 1;  // 각 스레드별로 다른 주문 수
            
            executorService.submit(() -> {
                RLock lock = redissonClient.getLock(lockKey);
                try {
                    if (lock.tryLock(1, 3, TimeUnit.SECONDS)) {
                        // 현재 점수 조회 후 업데이트 (복합 연산)
                        Double currentScore = zSetOps.score(rankingKey, productId);
                        double newScore = (currentScore != null ? currentScore : 0) + orderCount;
                        zSetOps.add(rankingKey, productId, newScore);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            });
        }

        executorService.shutdown();
        boolean terminated = executorService.awaitTermination(30, TimeUnit.SECONDS);
        assertThat(terminated).isTrue();

        // Then - 정확한 누적 점수 확인 (1+2+3+...+50 = 1275)
        Double finalScore = zSetOps.score(rankingKey, productId);
        assertThat(finalScore).isEqualTo(1275.0);
    }

    @Test
    @DisplayName("실시간 랭킹 조회 성능 테스트")
    void testRealTimeRankingPerformance() {
        // Given - 대량의 상품 랭킹 데이터
        String rankingKey = "product:ranking:order:daily:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        for (int i = 1; i <= 1000; i++) {
            zSetOps.add(rankingKey, "product:" + i, Math.random() * 1000);
        }

        // When & Then - Top 10 조회 성능 측정
        long startTime = System.currentTimeMillis();
        
        Set<ZSetOperations.TypedTuple<Object>> top10 = zSetOps.reverseRangeWithScores(rankingKey, 0, 9);
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // 성능 검증 (100ms 이내로 완화)
        assertThat(executionTime).isLessThan(100);
        assertThat(top10).hasSize(10);
        
        // 점수 내림차순 정렬 확인
        double previousScore = Double.MAX_VALUE;
        for (ZSetOperations.TypedTuple<Object> tuple : top10) {
            assertThat(tuple.getScore()).isLessThanOrEqualTo(previousScore);
            previousScore = tuple.getScore();
        }
    }

    @Test
    @DisplayName("주문 완료 시 랭킹 자동 업데이트 시나리오 테스트")
    void testOrderCompletionRankingUpdate() {
        // ========================================
        // 1. 테스트용 Redis 키 생성
        // ========================================
        // 오늘 날짜를 기반으로 일일 상품 랭킹 키를 생성
        // 예: "product:ranking:order:daily:20240824"
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayString = today.format(dateFormatter);
        String rankingKey = "product:ranking:order:daily:" + todayString;
        
        log.info("=== 테스트 시작: 주문 완료 시 랭킹 자동 업데이트 ===");
        log.info("사용할 Redis 랭킹 키: {}", rankingKey);

        // ========================================
        // 2. Given - 초기 상품 주문 데이터 설정
        // ========================================
        // Redis SortedSet에 여러 상품의 초기 주문 수량을 설정
        log.info("--- 초기 주문 데이터 설정 중 ---");
        
        // 상품 100번: 5건의 주문
        String product100 = "product:100";
        double initialOrder100 = 5.0;
        zSetOps.incrementScore(rankingKey, product100, initialOrder100);
        log.info("상품 {}에 {}건 주문 추가 (초기 설정)", product100, initialOrder100);
        
        // 상품 101번: 8건의 주문 (현재 1위)
        String product101 = "product:101";
        double initialOrder101 = 8.0;
        zSetOps.incrementScore(rankingKey, product101, initialOrder101);
        log.info("상품 {}에 {}건 주문 추가 (초기 설정)", product101, initialOrder101);
        
        // 상품 102번: 3건의 주문
        String product102 = "product:102";
        double initialOrder102 = 3.0;
        zSetOps.incrementScore(rankingKey, product102, initialOrder102);
        log.info("상품 {}에 {}건 주문 추가 (초기 설정)", product102, initialOrder102);

        // 초기 랭킹 상태 출력
        log.info("--- 초기 랭킹 상태 ---");
        printCurrentRanking(rankingKey);

        // ========================================
        // 3. When - 새로운 주문이 완료되는 시나리오
        // ========================================
        log.info("--- 새로운 주문 처리 중 ---");
        
        // 상품 100번에 추가로 3건의 주문이 들어옴 (총 8건이 됨)
        double additionalOrder100 = 3.0;
        zSetOps.incrementScore(rankingKey, product100, additionalOrder100);
        double totalOrder100 = initialOrder100 + additionalOrder100; // 5 + 3 = 8
        log.info("상품 {}에 추가로 {}건 주문 (누적 총 {}건)", product100, additionalOrder100, totalOrder100);
        
        // 상품 103번에 첫 주문 1건이 들어옴
        String product103 = "product:103";
        double firstOrder103 = 1.0;
        zSetOps.incrementScore(rankingKey, product103, firstOrder103);
        log.info("신규 상품 {}에 첫 주문 {}건", product103, firstOrder103);

        // 주문 처리 후 랭킹 상태 출력
        log.info("--- 주문 처리 후 랭킹 상태 ---");
        printCurrentRanking(rankingKey);
        
        // ========================================
        // 4. Then - 업데이트된 랭킹 검증
        // ========================================
        // Redis SortedSet에서 점수 높은 순으로 모든 데이터 가져오기
        Set<ZSetOperations.TypedTuple<Object>> updatedRanking = zSetOps.reverseRangeWithScores(rankingKey, 0, -1);
        List<ZSetOperations.TypedTuple<Object>> rankingList = updatedRanking.stream().toList();

        // 총 4개의 상품이 랭킹에 있어야 함
        assertThat(rankingList).hasSize(4);
        log.info("검증: 총 상품 수 = {}", rankingList.size());
        
        // ========================================
        // 5. Redis SortedSet 동점 처리 규칙 검증
        // ========================================
        // Redis SortedSet에서 점수가 같을 때는 사전순(lexicographical) 정렬됨
        // product:100 (8점)과 product:101 (8점)이 동점이므로:
        // - "product:100" < "product:101" (사전순)
        // - 하지만 ZREVRANGE는 점수 내림차순 + 사전순 오름차순이므로
        // - 높은 점수가 먼저 오고, 같은 점수에서는 사전순으로 뒤에 있는 것이 먼저 옴
        // - 즉, product:101이 product:100보다 앞에 옴 (사전순으로 뒤에 있기 때문)
        
        log.info("--- 예상 최종 랭킹 ---");
        log.info("1위: {} ({}점) - 초기 8점", product101, initialOrder101);
        log.info("2위: {} ({}점) - 5점 + 3점 추가", product100, totalOrder100);
        log.info("3위: {} ({}점) - 초기 3점", product102, initialOrder102);
        log.info("4위: {} ({}점) - 신규 진입", product103, firstOrder103);
        
        // 1위 검증: product:101 (8건)
        ZSetOperations.TypedTuple<Object> rank1 = rankingList.get(0);
        assertThat(rank1.getValue()).isEqualTo(product101);
        assertThat(rank1.getScore()).isEqualTo(8.0);
        log.info("1위 검증 완료: {} - {}점", rank1.getValue(), rank1.getScore());
        
        // 2위 검증: product:100 (8건, 동점이지만 사전순으로 뒤)
        ZSetOperations.TypedTuple<Object> rank2 = rankingList.get(1);
        assertThat(rank2.getValue()).isEqualTo(product100);
        assertThat(rank2.getScore()).isEqualTo(8.0);
        log.info("2위 검증 완료: {} - {}점", rank2.getValue(), rank2.getScore());
        
        // 3위 검증: product:102 (3건)
        ZSetOperations.TypedTuple<Object> rank3 = rankingList.get(2);
        assertThat(rank3.getValue()).isEqualTo(product102);
        assertThat(rank3.getScore()).isEqualTo(3.0);
        log.info("3위 검증 완료: {} - {}점", rank3.getValue(), rank3.getScore());
        
        // 4위 검증: product:103 (1건)
        ZSetOperations.TypedTuple<Object> rank4 = rankingList.get(3);
        assertThat(rank4.getValue()).isEqualTo(product103);
        assertThat(rank4.getScore()).isEqualTo(1.0);
        log.info("4위 검증 완료: {} - {}점", rank4.getValue(), rank4.getScore());
        
        log.info("=== 테스트 완료: 주문 완료 시 랭킹 자동 업데이트 ===");
    }

    @Test
    @DisplayName("로깅을 통한 랭킹 변경 사항 확인 테스트")
    void testRankingChangesWithDetailedLogging() {
        String rankingKey = "product:ranking:order:daily:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 초기 상태 (빈 랭킹)
        printCurrentRanking(rankingKey);
        
        // 1단계: 첫 번째 주문들
        zSetOps.incrementScore(rankingKey, "product:100", 5);
        zSetOps.incrementScore(rankingKey, "product:101", 8);
        zSetOps.incrementScore(rankingKey, "product:102", 3);
        log.info("주문 처리: product:100(+5), product:101(+8), product:102(+3)");
        printCurrentRanking(rankingKey);
        
        // 2단계: 추가 주문으로 순위 변경
        zSetOps.incrementScore(rankingKey, "product:100", 10); // 총 15점으로 1위 등극
        zSetOps.incrementScore(rankingKey, "product:103", 12); // 신규 상품 진입
        log.info("주문 처리: product:100(+10, 총15), product:103(+12, 신규)");
        printCurrentRanking(rankingKey);
        
        // 3단계: 대량 주문으로 극적인 변화
        zSetOps.incrementScore(rankingKey, "product:102", 25); // 총 28점으로 1위 도약
        log.info("주문 처리: product:102(+25, 총28) - 3위에서 1위로 급상승!");
        printCurrentRanking(rankingKey);
        
        // 최종 검증
        Set<ZSetOperations.TypedTuple<Object>> finalRanking = zSetOps.reverseRangeWithScores(rankingKey, 0, -1);
        assertThat(finalRanking).hasSize(4);
        
        List<ZSetOperations.TypedTuple<Object>> rankingList = finalRanking.stream().toList();
        assertThat(rankingList.get(0).getValue()).isEqualTo("product:102"); // 1위
        assertThat(rankingList.get(0).getScore()).isEqualTo(28.0);
        
    }
    
    @Test
    @DisplayName("수동 확인을 위한 일시정지 테스트")
    void testWithPauseForManualVerification() throws InterruptedException {
        String rankingKey = "product:ranking:order:daily:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        log.info("Redis 컨테이너 포트: {}", redis.getFirstMappedPort());
        log.info("랭킹 키: {}", rankingKey);
        
        // 테스트 데이터 생성
        zSetOps.incrementScore(rankingKey, "product:1", 10);
        zSetOps.incrementScore(rankingKey, "product:2", 25);
        zSetOps.incrementScore(rankingKey, "product:3", 15);
        zSetOps.incrementScore(rankingKey, "product:4", 5);
        
        printCurrentRanking(rankingKey);
        
        log.info("1. Docker 컨테이너 접속:");
        log.info("   docker exec -it $(docker ps -q --filter ancestor=redis:7-alpine) redis-cli");
        log.info("2. 전체 랭킹 조회:");
        log.info("   ZREVRANGE {} 0 -1 WITHSCORES", rankingKey);
        log.info("3. Top 3 랭킹 조회:");
        log.info("   ZREVRANGE {} 0 2 WITHSCORES", rankingKey);
        log.info("4. 특정 상품 점수 조회:");
        log.info("   ZSCORE {} product:2", rankingKey);
        log.info("5. 특정 상품 순위 조회:");
        log.info("   ZREVRANK {} product:2", rankingKey);
        
        // 수동 확인을 위한 대기 (실제 사용 시에는 주석 해제)
        // Thread.sleep(30000);
        
        // 추가 변경사항 적용
        zSetOps.incrementScore(rankingKey, "product:1", 20); // 총 30점으로 1위 등극
        log.info("product:1에 20점 추가 (총 30점)");
        
        printCurrentRanking(rankingKey);
        
    }
    
    @Test
    @DisplayName("랭킹 데이터 JSON 덤프 테스트")
    void testRankingDataDump() throws Exception {
        String rankingKey = "product:ranking:order:daily:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        

        // 다양한 시나리오의 테스트 데이터 생성
        zSetOps.incrementScore(rankingKey, "product:스마트폰", 150);
        zSetOps.incrementScore(rankingKey, "product:노트북", 89);
        zSetOps.incrementScore(rankingKey, "product:태블릿", 134);
        zSetOps.incrementScore(rankingKey, "product:이어폰", 267);
        zSetOps.incrementScore(rankingKey, "product:스마트워치", 95);
        
        // 현재 랭킹 출력
        printCurrentRanking(rankingKey);
        
        // 랭킹 데이터를 JSON으로 덤프
        Set<ZSetOperations.TypedTuple<Object>> finalRanking = zSetOps.reverseRangeWithScores(rankingKey, 0, -1);
        
        List<Map<String, Object>> rankingData = finalRanking.stream()
                .map(tuple -> {
                    int rank = finalRanking.stream().toList().indexOf(tuple) + 1;
                    return Map.<String, Object>of(
                            "rank", rank,
                            "product", tuple.getValue().toString(),
                            "score", tuple.getScore(),
                            "timestamp", LocalDateTime.now().toString()
                    );
                })
                .collect(Collectors.toList());
        
        Map<String, Object> dumpData = Map.of(
                "testName", "랭킹 데이터 덤프 테스트",
                "rankingKey", rankingKey,
                "totalProducts", rankingData.size(),
                "timestamp", LocalDateTime.now().toString(),
                "ranking", rankingData
        );
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dumpData);
        
        String filename = "ranking_result_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json";
        Files.write(Paths.get(filename), json.getBytes());
        
        log.info("JSON 내용 미리보기:");
        log.info(json);
        
        // 검증
        assertThat(rankingData).hasSize(5);
        assertThat(rankingData.get(0).get("product")).isEqualTo("product:이어폰"); // 1위 (267점)
        assertThat(rankingData.get(0).get("score")).isEqualTo(267.0);
        
    }
    
    @Test
    @DisplayName("실시간 주문 시뮬레이션 및 랭킹 변화 추적")
    void testRealTimeOrderSimulation() throws InterruptedException {
        String rankingKey = "product:ranking:order:daily:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        

        // 시뮬레이션 시나리오: 5개 상품의 실시간 주문 경쟁
        String[] products = {"product:iPhone", "product:Galaxy", "product:Pixel", "product:OnePlus", "product:Xiaomi"};
        int[] orderCounts = {0, 0, 0, 0, 0};
        
        printCurrentRanking(rankingKey);
        
        // 10초간 랜덤 주문 시뮬레이션
        for (int second = 1; second <= 10; second++) {

            // 랜덤하게 1-3개 상품에 주문 발생
            int orderEvents = (int) (Math.random() * 3) + 1;
            
            for (int i = 0; i < orderEvents; i++) {
                int productIndex = (int) (Math.random() * products.length);
                int orderAmount = (int) (Math.random() * 5) + 1; // 1-5개 주문
                
                orderCounts[productIndex] += orderAmount;
                zSetOps.incrementScore(rankingKey, products[productIndex], orderAmount);
                
                        //products[productIndex], orderAmount, orderCounts[productIndex];)
            }
            
            // 매 3초마다 현재 랭킹 출력
            if (second % 3 == 0) {
                log.info("--- {}초차 중간 랭킹 ---", second);
                printCurrentRanking(rankingKey);
            }
            
            Thread.sleep(100); // 실제로는 빠르게 진행
        }
        
        printCurrentRanking(rankingKey);
        
        // 가장 많이 주문된 상품 검증
        Set<ZSetOperations.TypedTuple<Object>> finalRanking = zSetOps.reverseRangeWithScores(rankingKey, 0, 0);
        ZSetOperations.TypedTuple<Object> winner = finalRanking.iterator().next();
        
        log.info("가장많이 주문된 상품: {} ({}건 주문)", winner.getValue(), winner.getScore());
        
        assertThat(finalRanking).hasSize(1);
        assertThat(winner.getScore()).isGreaterThan(0);
    }

    // 헬퍼 메서드들
    private void printCurrentRanking(String rankingKey) {
        Set<ZSetOperations.TypedTuple<Object>> ranking = zSetOps.reverseRangeWithScores(rankingKey, 0, -1);
        
        if (ranking.isEmpty()) {
            return;
        }
        
        log.info("현재 랭킹 (총 {}개 상품):", ranking.size());
        int rank = 1;
        for (ZSetOperations.TypedTuple<Object> tuple : ranking) {
            String medal = getRankMedal(rank);
            log.info("  {} {}위: {} - {}점", medal, rank++, tuple.getValue(), tuple.getScore().intValue());
        }
    }
    
    private String getRankMedal(int rank) {
        return switch (rank) {
            case 1 -> "🥇";
            case 2 -> "🥈"; 
            case 3 -> "🥉";
            default -> "📍";
        };
    }
}
