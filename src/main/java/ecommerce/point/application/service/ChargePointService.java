package ecommerce.point.application.service;

import ecommerce.config.CacheConfig;
import ecommerce.config.DistributedLock;
import ecommerce.point.application.port.in.ChargePointUseCase;
import ecommerce.point.application.port.out.LoadPointPort;
import ecommerce.point.application.port.out.SavePointPort;
import ecommerce.point.domain.Point;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ChargePointService implements ChargePointUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChargePointService.class);

    private final LoadPointPort loadPointPort;
    private final SavePointPort savePointPort;

    public ChargePointService(LoadPointPort loadPointPort, SavePointPort savePointPort) {
        this.loadPointPort = loadPointPort;
        this.savePointPort = savePointPort;
    }

    /**
     * 포인트 충전 - 분산락과 캐시 무효화 적용
     * 분산락 설정: 1초 대기, 2초 보유 (최적화됨)
     * 캐시 무효화: 포인트 충전 후 해당 사용자 잔액 캐시 삭제
     */
    @Transactional
    @Override
    @DistributedLock(key = "'user:point:' + #userId", waitTime = 1, leaseTime = 2)
    @CacheEvict(value = CacheConfig.CacheNames.USER_BALANCE, key = "#userId")
    public long charge(Long userId, long amount) {
        log.info("ChargePointService.charge() 호출됨 - 분산락 적용 (최적화됨). userId: {}, amount: {}", userId, amount);

        Point point = new Point(); // 초기 잔액은 0으로 설정 , id 는 자동채번
        log.info("Point 도메인 객체의 charge() 메서드 호출 전 - 분산락 내에서 실행");
        point.charge(amount);
        log.info("Point 도메인 객체의 charge() 메서드 호출 후. 새 잔액 (예상): {}", point.calculateBalance());

        Point savedPoint = savePointPort.savePoint(point, userId);
        log.info("Point 애그리거트 저장 완료 - 분산락으로 동시성 제어됨, 캐시 무효화됨. 저장된 Point ID: {}, 충전 금액: {}", savedPoint.getId(), savedPoint.calculateBalance());

        return savedPoint.calculateBalance();
    }
}
