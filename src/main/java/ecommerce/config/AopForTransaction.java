package ecommerce.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * AOP 에서 트랜잭션 분리를 위한 클래스
 */
@Component
public class AopForTransaction {


    // 격리수준을 requires_new 로 설정하여 트랜잭션 유무에 관계없이 별도의 트랜잭션으로 동작하게 설정
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed (final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
