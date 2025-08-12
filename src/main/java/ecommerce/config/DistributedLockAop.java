package ecommerce.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * @DistributedLock 선언 시 수행되는 Aop Class
* */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "MY_LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        RLock rLock = redissonClient.getLock(key); // (1) 락이름으로 인스턴스가져옴

        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit()); // (2) 정의된 watTime 까지 획득시도
            if (!available) {
                return false;
            }
            return aopForTransaction.proceed(joinPoint); // (3) distributedLock 어노테이션이 선언된 메서드를 별도의 트랜잭션으로 실행
        } catch (InterruptedException e){
            throw new InterruptedException(e.getMessage());
        } finally {
            try {
                rLock.unlock(); // (4)
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock {} {}",
                    kv("serviceName", method.getName()),
                    kv("key", key)
                );
            }
        }

    }

}
