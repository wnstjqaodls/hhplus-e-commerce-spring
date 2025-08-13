package ecommerce.order.application.service;

import ecommerce.order.application.port.in.PayOrderUseCase;
import ecommerce.order.application.port.out.LoadOrderPort;
import ecommerce.order.application.port.out.SaveOrderPort;
import ecommerce.point.application.port.out.LoadPointPort;
import ecommerce.point.application.port.out.SavePointPort;

public class PayOrderService implements PayOrderUseCase {

    private final LoadPointPort loadPointPort;
    private final LoadOrderPort loadOrderPort;
    private final SavePointPort savePointPort;
    private final SaveOrderPort saveOrderPort;

    public PayOrderService(LoadPointPort loadPointPort, LoadOrderPort loadOrderPort,
                           SavePointPort savePointPort, SaveOrderPort saveOrderPort) {
        this.loadPointPort = loadPointPort;
        this.loadOrderPort = loadOrderPort;
        this.savePointPort = savePointPort;
        this.saveOrderPort = saveOrderPort;
    }

    @Override
    public Long payOrder (Long userId, Long orderId) {
        return 0L;
    }
}
