package ecommerce.order.application.port.out;

import ecommerce.point.domain.Point;

public interface LoadPointPort {
    Point loadPoint(Long userId);
}
