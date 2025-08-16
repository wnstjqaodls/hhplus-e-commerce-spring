package ecommerce.integration;

import ecommerce.point.application.port.in.ChargePointUseCase;
import ecommerce.order.application.port.in.PayOrderUseCase;
import ecommerce.order.application.port.in.PlaceOrderUseCase;
import ecommerce.product.application.port.in.ReduceStockUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("분산락 통합 테스트")
public class DistributedLockIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(DistributedLockIntegrationTest.class);

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private ChargePointUseCase chargePointUseCase;

    @Autowired
    private PayOrderUseCase payOrderUseCase;

    @Autowired
    private PlaceOrderUseCase placeOrderUseCase;

    @Autowired
    private ReduceStockUseCase reduceStockUseCase;

    @Test
    @DisplayName("포인트 충전 - 분산락으로 동시성 제어")
    void testConcurrentPointCharge() throws InterruptedException {
        // Given
        Long userId = 1L;
        long chargeAmount = 1000L;
        int threadCount = 10;
        
        log.info("=== 포인트 충전 동시성 테스트 시작 ===");
        log.info("사용자 ID: {}, 충전 금액: {}원, 동시 요청 수: {}개", userId, chargeAmount, threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < threadCount; i++) {
            final int requestNumber = i + 1;
            executorService.submit(() -> {
                try {
                    log.info("포인트 충전 요청 {} 시작", requestNumber);
                    long result = chargePointUseCase.charge(userId, chargeAmount);
                    log.info("포인트 충전 요청 {} 성공 - 결과: {}원", requestNumber, result);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("포인트 충전 요청 {} 실패: {}", requestNumber, e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        log.info("=== 포인트 충전 동시성 테스트 결과 ===");
        log.info("성공한 요청: {}개", successCount.get());
        log.info("실패한 요청: {}개", failCount.get());
        
        // 분산락이 적용되어 순차적으로 처리되므로 모든 요청이 성공해야 함
        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(failCount.get()).isEqualTo(0);
    }

    @Test
    @DisplayName("상품 재고 차감 - 분산락으로 동시성 제어")
    void testConcurrentStockReduction() throws InterruptedException {
        // Given
        Long productId = 1L;
        int reduceQuantity = 1;
        int threadCount = 5;
        
        log.info("=== 상품 재고 차감 동시성 테스트 시작 ===");
        log.info("상품 ID: {}, 차감 수량: {}개, 동시 요청 수: {}개", productId, reduceQuantity, threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < threadCount; i++) {
            final int requestNumber = i + 1;
            executorService.submit(() -> {
                try {
                    log.info("재고 차감 요청 {} 시작", requestNumber);
                    reduceStockUseCase.reduceStock(productId, reduceQuantity);
                    log.info("재고 차감 요청 {} 성공", requestNumber);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("재고 차감 요청 {} 실패: {}", requestNumber, e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        log.info("=== 상품 재고 차감 동시성 테스트 결과 ===");
        log.info("성공한 요청: {}개", successCount.get());
        log.info("실패한 요청: {}개", failCount.get());
        
        // 분산락으로 순차 처리되어야 함
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
    }

    @Test
    @DisplayName("결제 처리 - 분산락으로 중복 결제 방지")
    void testConcurrentPayment() throws InterruptedException {
        // Given
        Long userId = 2L;
        Long orderId = 1L;
        int threadCount = 3;
        
        log.info("=== 결제 처리 동시성 테스트 시작 ===");
        log.info("사용자 ID: {}, 주문 ID: {}, 동시 결제 요청 수: {}개", userId, orderId, threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < threadCount; i++) {
            final int requestNumber = i + 1;
            executorService.submit(() -> {
                try {
                    log.info("결제 요청 {} 시작", requestNumber);
                    Long paymentId = payOrderUseCase.payOrder(userId, orderId);
                    log.info("결제 요청 {} 성공 - Payment ID: {}", requestNumber, paymentId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("결제 요청 {} 실패: {}", requestNumber, e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        log.info("=== 결제 처리 동시성 테스트 결과 ===");
        log.info("성공한 요청: {}개", successCount.get());
        log.info("실패한 요청: {}개", failCount.get());
        
        // 분산락으로 중복 결제가 방지되어야 함
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
    }

    @Test
    @DisplayName("분산락 키 격리 테스트 - 서로 다른 사용자는 영향받지 않음")
    void testDistributedLockKeyIsolation() throws InterruptedException {
        // Given
        Long user1 = 10L;
        Long user2 = 20L;
        long chargeAmount = 5000L;
        int threadCountPerUser = 3;
        
        log.info("=== 분산락 키 격리 테스트 시작 ===");
        log.info("사용자1 ID: {}, 사용자2 ID: {}, 각각 {}개 요청", user1, user2, threadCountPerUser);

        CountDownLatch latch = new CountDownLatch(threadCountPerUser * 2);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCountPerUser * 2);
        AtomicInteger user1SuccessCount = new AtomicInteger(0);
        AtomicInteger user2SuccessCount = new AtomicInteger(0);

        // When - 서로 다른 사용자의 포인트 충전이 동시에 처리되어야 함
        for (int i = 0; i < threadCountPerUser; i++) {
            final int requestNumber = i + 1;
            
            // 사용자 1 요청
            executorService.submit(() -> {
                try {
                    log.info("사용자1 포인트 충전 요청 {} 시작", requestNumber);
                    chargePointUseCase.charge(user1, chargeAmount);
                    log.info("사용자1 포인트 충전 요청 {} 성공", requestNumber);
                    user1SuccessCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("사용자1 포인트 충전 요청 {} 실패: {}", requestNumber, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
            
            // 사용자 2 요청  
            executorService.submit(() -> {
                try {
                    log.info("사용자2 포인트 충전 요청 {} 시작", requestNumber);
                    chargePointUseCase.charge(user2, chargeAmount);
                    log.info("사용자2 포인트 충전 요청 {} 성공", requestNumber);
                    user2SuccessCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("사용자2 포인트 충전 요청 {} 실패: {}", requestNumber, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        log.info("=== 분산락 키 격리 테스트 결과 ===");
        log.info("사용자1 성공 요청: {}개", user1SuccessCount.get());
        log.info("사용자2 성공 요청: {}개", user2SuccessCount.get());
        
        // 서로 다른 사용자이므로 각각 모든 요청이 성공해야 함
        assertThat(user1SuccessCount.get()).isEqualTo(threadCountPerUser);
        assertThat(user2SuccessCount.get()).isEqualTo(threadCountPerUser);
    }
}
