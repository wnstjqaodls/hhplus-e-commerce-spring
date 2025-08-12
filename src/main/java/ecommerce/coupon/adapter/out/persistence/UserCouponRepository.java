package ecommerce.coupon.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCouponJpaEntity, Long> {
}


