package ecommerce.point.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<PointJpaEntity, Long> {
    // JpaRepository를 상속받아 기본적인 CRUD 메서드들이 자동으로 제공됩니다.
    Optional<PointJpaEntity> findByUserId (Long userId);
} 
