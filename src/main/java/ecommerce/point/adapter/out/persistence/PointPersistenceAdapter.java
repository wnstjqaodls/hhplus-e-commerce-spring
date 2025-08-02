package ecommerce.point.adapter.out.persistence;

import ecommerce.point.application.port.out.LoadPointPort;
import ecommerce.point.application.port.out.SavePointPort;
import ecommerce.point.domain.Point;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class PointPersistenceAdapter implements LoadPointPort, SavePointPort {

    private final PointRepository pointRepository;

    public PointPersistenceAdapter(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Point loadPoint(Long userId) {
        PointJpaEntity pointJpaEntity = pointRepository.findByUserId(userId).orElse(null);

        if (pointJpaEntity == null) {
            return null;
        }

        return new Point(pointJpaEntity.getId(), pointJpaEntity.getAmount());
    }

    @Override
    @Transactional
    public Point savePoint(Point point, Long userId) {
        PointJpaEntity pointJpaEntity;

        if (point.getId() == null) {
            pointJpaEntity = new PointJpaEntity();
            pointJpaEntity.setUserId(userId);
            pointJpaEntity.setLastChargedAt(LocalDateTime.now());
        } else {
            pointJpaEntity = pointRepository.findById(point.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Point with ID " + point.getId() + " not found"));
        }

        pointJpaEntity.setAmount(point.calculateBalance());


        PointJpaEntity savedPointJpaEntity = pointRepository.save(pointJpaEntity);

        return new Point(savedPointJpaEntity.getId(),
                savedPointJpaEntity.getAmount());
    }
} 
