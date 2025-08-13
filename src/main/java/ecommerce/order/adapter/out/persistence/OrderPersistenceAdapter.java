package ecommerce.order.adapter.out.persistence;

import ecommerce.order.application.port.out.LoadOrderPort;
import ecommerce.order.application.port.out.LoadPointPort;
import ecommerce.order.application.port.out.SaveOrderPort;
import ecommerce.order.domain.Order;
import ecommerce.point.domain.Point;
import ecommerce.point.adapter.out.persistence.PointJpaEntity;
import ecommerce.point.adapter.out.persistence.PointRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class OrderPersistenceAdapter implements LoadPointPort, SaveOrderPort, LoadOrderPort {

    private final OrderRepository orderRepository;
    private final PointRepository pointRepository;

    public OrderPersistenceAdapter(OrderRepository orderRepository, PointRepository pointRepository) {
        this.orderRepository = orderRepository;
        this.pointRepository = pointRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Point loadPoint(Long userId) {
        PointJpaEntity pointJpaEntity = pointRepository.findByUserId(userId).orElse(null);

        if (pointJpaEntity == null) {
            return new Point(userId, 0L); // Point 패턴과 동일: 없으면 0원으로 생성
        }

        return new Point(pointJpaEntity.getId(), pointJpaEntity.getAmount());
    }

    @Override
    @Transactional
    public Order saveOrder(Order order, Long userId) {
        OrderJpaEntity orderJpaEntity;

        if (order.getId() == null) {
            orderJpaEntity = new OrderJpaEntity();
            orderJpaEntity.setUserId(userId);
            orderJpaEntity.setOrderTime(LocalDateTime.now());
            orderJpaEntity.setOrderStatus("CREATED"); // 기본 상태
        } else {
            orderJpaEntity = orderRepository.findById(order.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Order with ID " + order.getId() + " not found"));
        }

        // Order 도메인 → JPA Entity 매핑 (Point 패턴과 동일)
        orderJpaEntity.setQuantity(order.getQuantity());
        orderJpaEntity.setAmount(order.getPrice());
        // productId는 Order 도메인에서 가져올 수 없으므로 임시로 1L 설정
        orderJpaEntity.setProductId(1L);

        OrderJpaEntity savedOrderJpaEntity = orderRepository.save(orderJpaEntity);

        // JPA Entity → Order 도메인 매핑 (Point 패턴과 동일)
        return new Order(
            savedOrderJpaEntity.getId(),
            savedOrderJpaEntity.getUserId(),
            savedOrderJpaEntity.getProductId(),
            savedOrderJpaEntity.getQuantity(),
            savedOrderJpaEntity.getAmount());
    }

    @Override
    @Transactional(readOnly = true)
    public Order loadOrder(Long orderId) {
        OrderJpaEntity orderJpaEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. orderId: " + orderId));

        // JPA Entity → Order 도메인 매핑
        return new Order(
            orderJpaEntity.getId(),
            orderJpaEntity.getUserId(),
            orderJpaEntity.getProductId(),
            orderJpaEntity.getQuantity(),
            orderJpaEntity.getAmount());
    }
}
