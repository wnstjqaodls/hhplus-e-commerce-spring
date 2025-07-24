package ecommerce.point.application.service;

import ecommerce.point.application.port.in.ChargePointUseCase;
import ecommerce.point.application.port.out.LoadPointPort;
import ecommerce.point.application.port.out.SavePointPort;
import ecommerce.point.domain.Activity;
import ecommerce.point.domain.ActivityWindow;
import ecommerce.point.domain.Point;

public class ChargePointService implements ChargePointUseCase {

    private final LoadPointPort loadPointPort;
    private final SavePointPort savePointPort;


    public ChargePointService(ActivityWindow activityWindow, LoadPointPort loadPointPort, SavePointPort savePointPort) {
        this.loadPointPort = loadPointPort;
        this.savePointPort = savePointPort;
    }

    public long charge(Long pointId, long amount) {

        ActivityWindow activityWindow = loadPointPort.loadActivityWindow(pointId);

        // 도메인 로직 실행
        Point point = new Point(pointId);
        Activity newActivity = point.charge(activityWindow, amount);

        // 새로운 Activity를 저장
        savePointPort.saveActivity(newActivity);

        // 새로운 잔액 반환
        long newBalance = activityWindow.calculateBalance() + amount;
        return newBalance;
    }
}
