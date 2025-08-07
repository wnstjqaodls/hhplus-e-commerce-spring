package ecommerce.point.application.service;

import ecommerce.point.application.port.in.GetPointBalanceUseCase;
import ecommerce.point.application.port.out.LoadPointPort;
import ecommerce.point.domain.Point;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GetPointBalanceService implements GetPointBalanceUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetPointBalanceService.class);

    private final LoadPointPort loadPointPort;

    public GetPointBalanceService(LoadPointPort loadPointPort) {
        this.loadPointPort = loadPointPort;
    }

    @Override
    public long getBalance(Long userId) {
        log.info("GetPointBalanceService.getBalance() 호출됨. userId: {}", userId);

        Point point = loadPointPort.loadPoint(userId);
        
        if (point == null) {
            log.info("사용자의 포인트 정보가 없습니다. userId: {}, 기본값 0 반환", userId);
            return 0L;
        }

        long balance = point.calculateBalance();
        log.info("포인트 조회 완료. userId: {}, balance: {}", userId, balance);

        return balance;
    }
}
