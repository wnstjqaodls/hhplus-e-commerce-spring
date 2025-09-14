package ecommerce.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
* Redisson Distributed Lock Annotation
*/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    //락의 이름
    String key();

    //락의 시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    //락을 기다리는 시간 (기본값은 - 1초로 단축)
    //락 획득을 위해 watTime 만큼 대기한다
    long waitTime() default 1L;

    //락 임대시간 (기본값은 - 2초로 단축)
    long leaseTime() default 2L;

}
