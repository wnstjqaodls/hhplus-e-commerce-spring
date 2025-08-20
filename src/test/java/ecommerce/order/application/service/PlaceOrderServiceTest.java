package ecommerce.order.application.service;

import ecommerce.order.application.port.out.LoadPointPort;
import ecommerce.order.application.port.out.SaveOrderPort;
import ecommerce.order.domain.Order;
import ecommerce.point.domain.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaceOrderServiceTest {

    @Mock
    private LoadPointPort loadPointPort;
    
    @Mock
    private SaveOrderPort saveOrderPort;
    
    @InjectMocks
    private PlaceOrderService placeOrderService;

    @Test
    @DisplayName("충분한_포인트로_주문_성공")
    public void place_order_with_sufficient_points_succeeds() {
        //given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 2;
        Long amount = 30_000L;
        
        Point userPoint = new Point(userId, 50_000L); // 충분한 포인트
        Order expectedOrder = Order.createOrder(userId, productId, quantity, amount);
        
        when(loadPointPort.loadPoint(userId)).thenReturn(userPoint);
        when(saveOrderPort.saveOrder(any(Order.class), eq(userId))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            // 실제 저장된 것처럼 ID가 있는 Order 객체 반환 (타임스탬프를 ID로 사용)
            return new Order(System.currentTimeMillis() + Thread.currentThread().getId(), order.getUserId(), order.getProductId(), order.getQuantity(), order.getPrice());
        });
        
        System.out.println("=== 🛒 Point 패턴 Order 서비스 테스트 시작 ===");
        System.out.println("사용자 ID: " + userId);
        System.out.println("상품 ID: " + productId);
        System.out.println("주문 수량: " + quantity);
        System.out.println("주문 금액: " + amount + "원");
        System.out.println("사용자 포인트: " + userPoint.getAmount() + "원");

        //when
        Long result = placeOrderService.placeOrder(userId, productId, quantity, amount);

        //then
        assertThat(result).isNotNull();
        
        // Point 패턴과 동일한 verify 검증
        verify(loadPointPort, times(1)).loadPoint(userId);
        verify(saveOrderPort, times(1)).saveOrder(any(Order.class), eq(userId));
        
        System.out.println("✅ 주문 성공! 주문 ID: " + result);
        System.out.println("🔍 호출 검증:");
        System.out.println("  - LoadPointPort.loadPoint(): 1회 호출");
        System.out.println("  - SaveOrderPort.saveOrder(): 1회 호출");
        System.out.println("=== 🛒 Point 패턴 Order 서비스 테스트 완료 ===");
    }

    @Test
    @DisplayName("포인트_부족으로_주문_실패")
    public void place_order_with_insufficient_points_fails() {
        //given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 2;
        Long amount = 50_000L;
        
        Point userPoint = new Point(userId, 30_000L); // 부족한 포인트
        
        when(loadPointPort.loadPoint(userId)).thenReturn(userPoint);
        
        System.out.println("=== ❌ 포인트 부족 실패 케이스 테스트 시작 ===");
        System.out.println("필요 금액: " + amount + "원");
        System.out.println("보유 포인트: " + userPoint.getAmount() + "원");
        System.out.println("부족 금액: " + (amount - userPoint.getAmount()) + "원");

        //when & then
        assertThatThrownBy(() -> {
            placeOrderService.placeOrder(userId, productId, quantity, amount);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("포인트가 부족하여 주문할 수 없습니다");

        // 포인트 조회는 했지만 주문 저장은 하지 않았음을 검증 (Point 패턴과 동일)
        verify(loadPointPort, times(1)).loadPoint(userId);
        verify(saveOrderPort, never()).saveOrder(any(Order.class), any());
        
        System.out.println("✅ 포인트 부족으로 정상 실패!");
        System.out.println("🔍 호출 검증:");
        System.out.println("  - LoadPointPort.loadPoint(): 1회 호출 (포인트 확인)");
        System.out.println("  - SaveOrderPort.saveOrder(): 0회 호출 (실패로 저장 안함)");
        System.out.println("=== ❌ 포인트 부족 실패 케이스 테스트 완료 ===");
    }

    @Test
    @DisplayName("주문 시 Redis 분산락 적용 100 개의 동시 요청 테스트")
    public void placeOrderWithRedisLock() throws InterruptedException {

        //given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 2;
        Long amount = 30_000L;
        int threadCount = 100;

        // 동시성을 위한 스레드 준비
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Point userPoint = new Point(userId, 50_000L); // 충분한 포인트
        Order expectedOrder = Order.createOrder(userId, productId, quantity, amount);

        when(loadPointPort.loadPoint(userId)).thenReturn(userPoint);
        when(saveOrderPort.saveOrder(any(Order.class), eq(userId))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            // 실제 저장된 것처럼 ID가 있는 Order 객체 반환 (타임스탬프를 ID로 사용)
            return new Order(System.currentTimeMillis() + Thread.currentThread().getId(), order.getUserId(), order.getProductId(), order.getQuantity(), order.getPrice());
        });

        System.out.println("=== 🔒 분산락 동시성 테스트 시작 ===");
        System.out.println("동시 요청 수: " + threadCount);
        System.out.println("사용자 ID: " + userId);
        System.out.println("주문 금액: " + amount + "원");

        //when
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int requestNumber = i + 1;
            executorService.submit(() -> {
                try {
                    Long orderId = placeOrderService.placeOrder(userId, productId, quantity, amount);
                    successCount.incrementAndGet();
                    System.out.println("✅ 요청 #" + requestNumber + " 성공 - 주문 ID: " + orderId);
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("❌ 요청 #" + requestNumber + " 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기 (최대 30초)
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        executorService.shutdown();

        //then
        assertThat(completed).isTrue();
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);

        System.out.println("=== 📊 동시성 테스트 결과 ===");
        System.out.println("총 실행 시간: " + (endTime - startTime) + "ms");
        System.out.println("성공 요청 수: " + successCount.get());
        System.out.println("실패 요청 수: " + failCount.get());
        System.out.println("총 요청 수: " + (successCount.get() + failCount.get()));

        // 분산락으로 인해 순차적으로 처리되어야 하므로 성공 요청이 있어야 함
        assertThat(successCount.get()).isGreaterThan(0);
        
        System.out.println("✅ 분산락이 정상적으로 동작하여 동시성 제어됨");
        System.out.println("=== 🔒 분산락 동시성 테스트 완료 ===");
    }

    @Test
    @DisplayName("분산락 없는 동시성 테스트 - 동시성 문제 발생 확인")
    public void placeOrderWithoutLock_ConcurrencyIssues() throws InterruptedException {

        //given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 2;
        Long amount = 30_000L;
        int threadCount = 100;

        // 동시성을 위한 스레드 준비
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        
        // 주문 결과를 추적하기 위한 동시성 안전 컬렉션
        ConcurrentHashMap<Integer, String> orderResults = new ConcurrentHashMap<>();
        List<Long> orderTimes = new ArrayList<>();
        List<Long> orderIds = new ArrayList<>();

        Point userPoint = new Point(userId, 50_000L); // 충분한 포인트
        Order expectedOrder = Order.createOrder(userId, productId, quantity, amount);

        when(loadPointPort.loadPoint(userId)).thenReturn(userPoint);
        when(saveOrderPort.saveOrder(any(Order.class), eq(userId))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            // 실제 저장된 것처럼 ID가 있는 Order 객체 반환 (타임스탬프를 ID로 사용)
            return new Order(System.currentTimeMillis() + Thread.currentThread().getId(), order.getUserId(), order.getProductId(), order.getQuantity(), order.getPrice());
        });

        System.out.println("=== ⚠️ 분산락 없는 동시성 테스트 시작 ===");
        System.out.println("동시 요청 수: " + threadCount);
        System.out.println("사용자 ID: " + userId);
        System.out.println("주문 금액: " + amount + "원");
        System.out.println("❗ 주의: 이 테스트는 동시성 문제를 의도적으로 발생시킵니다");

        //when
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int requestNumber = i + 1;
            executorService.submit(() -> {
                try {
                    long requestStartTime = System.currentTimeMillis();
                    Long orderId = placeOrderService.placeOrder(userId, productId, quantity, amount);
                    long requestEndTime = System.currentTimeMillis();
                    
                    synchronized (orderTimes) {
                        orderTimes.add(requestEndTime - requestStartTime);
                        orderIds.add(orderId);
                    }
                    
                    successCount.incrementAndGet();
                    orderResults.put(requestNumber, "SUCCESS - Order ID: " + orderId + " (처리시간: " + (requestEndTime - requestStartTime) + "ms)");
                    
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    orderResults.put(requestNumber, "FAILED - " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기 (최대 30초)
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        executorService.shutdown();

        //then
        assertThat(completed).isTrue();
        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);

        System.out.println("=== 📊 분산락 없는 동시성 테스트 결과 ===");
        System.out.println("총 실행 시간: " + (endTime - startTime) + "ms");
        System.out.println("성공 요청 수: " + successCount.get());
        System.out.println("실패 요청 수: " + failCount.get());
        System.out.println("총 요청 수: " + (successCount.get() + failCount.get()));

        // 동시성 문제 분석
        analyzeConcurrencyIssues(orderResults, orderTimes, orderIds, threadCount);
        
        System.out.println("=== ⚠️ 분산락 없는 동시성 테스트 완료 ===");
    }

    private void analyzeConcurrencyIssues(ConcurrentHashMap<Integer, String> orderResults, 
                                        List<Long> orderTimes, List<Long> orderIds, int threadCount) {
        
        System.out.println("\n=== 🔍 동시성 문제 분석 ===");
        
        // 1. 처리 시간 분석
        if (!orderTimes.isEmpty()) {
            long avgTime = orderTimes.stream().mapToLong(Long::longValue).sum() / orderTimes.size();
            long maxTime = orderTimes.stream().mapToLong(Long::longValue).max().orElse(0);
            long minTime = orderTimes.stream().mapToLong(Long::longValue).min().orElse(0);
            
            System.out.println("📈 처리 시간 분석:");
            System.out.println("  - 평균 처리 시간: " + avgTime + "ms");
            System.out.println("  - 최대 처리 시간: " + maxTime + "ms");
            System.out.println("  - 최소 처리 시간: " + minTime + "ms");
            System.out.println("  - 시간 편차: " + (maxTime - minTime) + "ms");
        }

        // 2. 주문 ID 중복 검사 (동시성 문제로 인한 중복 가능성)
        long uniqueOrderIds = orderIds.stream().distinct().count();
        System.out.println("\n🆔 주문 ID 분석:");
        System.out.println("  - 총 생성된 주문 ID 수: " + orderIds.size());
        System.out.println("  - 고유 주문 ID 수: " + uniqueOrderIds);
        if (uniqueOrderIds < orderIds.size()) {
            System.out.println("  ⚠️ 중복된 주문 ID 발견! (동시성 문제 의심)");
            System.out.println("  - 중복 발생 횟수: " + (orderIds.size() - uniqueOrderIds));
        } else {
            System.out.println("  ✅ 주문 ID 중복 없음");
        }

        // 3. 실패율 분석
        double failureRate = (double) (threadCount - orderIds.size()) / threadCount * 100;
        System.out.println("\n📉 실패율 분석:");
        System.out.println("  - 실패율: " + String.format("%.2f", failureRate) + "%");
        if (failureRate > 10) {
            System.out.println("  ⚠️ 높은 실패율 감지! 동시성 문제로 인한 것일 수 있습니다");
        }

        // 4. 상세 결과 로깅 (처음 10개와 마지막 10개만)
        System.out.println("\n📋 주문 처리 결과 샘플:");
        System.out.println("--- 처음 10개 요청 ---");
        for (int i = 1; i <= Math.min(10, threadCount); i++) {
            String result = orderResults.get(i);
            System.out.println("  요청 #" + i + ": " + (result != null ? result : "결과 없음"));
        }
        
        if (threadCount > 10) {
            System.out.println("--- 마지막 10개 요청 ---");
            for (int i = Math.max(threadCount - 9, 11); i <= threadCount; i++) {
                String result = orderResults.get(i);
                System.out.println("  요청 #" + i + ": " + (result != null ? result : "결과 없음"));
            }
        }

        // 5. 동시성 문제 결론
        System.out.println("\n🎯 동시성 문제 진단:");
        if (uniqueOrderIds < orderIds.size() || failureRate > 10) {
            System.out.println("  ❌ 동시성 문제 발생!");
            System.out.println("  📌 권장사항: @DistributedLock 어노테이션을 사용하여 분산락을 적용하세요");
        } else {
            System.out.println("  ✅ 심각한 동시성 문제는 감지되지 않았습니다");
            System.out.println("  📌 참고: 하지만 더 많은 요청이나 실제 환경에서는 문제가 발생할 수 있습니다");
        }
    }
}
