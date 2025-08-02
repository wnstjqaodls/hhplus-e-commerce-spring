package ecommerce.point.application.service;

import ecommerce.point.application.port.in.ChargePointUseCase;
import ecommerce.point.application.port.out.LoadPointPort;
import ecommerce.point.application.port.out.SavePointPort;
import ecommerce.point.domain.Point;
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

    @Override
    public long charge(Long userId, long amount) {
        log.info("ChargePointService.charge() 호출됨. pointId: {}, amount: {}", userId, amount);

        Point point = loadPointPort.loadPoint(userId);
        if (point == null) {
            log.info("새로운 Point 생성. pointId: {}", userId);
            point = new Point(userId);
        } else {
            log.info("기존 Point 로드됨. pointId: {}, 현재 잔액: {}", point.getId(), point.calculateBalance());
        }

        log.info("Point 도메인 객체의 charge() 메서드 호출 전");
        point.charge(amount);
        log.info("Point 도메인 객체의 charge() 메서드 호출 후. 새 잔액 (예상): {}", point.calculateBalance());

        Point savedPoint = savePointPort.savePoint(point);
        log.info("Point 애그리거트 저장 완료. 저장된 Point ID: {}, 최종 잔액: {}", savedPoint.getId(), savedPoint.calculateBalance());

        return savedPoint.calculateBalance();
    }
}
