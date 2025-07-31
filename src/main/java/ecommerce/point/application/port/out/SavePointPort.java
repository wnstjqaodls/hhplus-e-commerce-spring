package ecommerce.point.application.port.out;

import ecommerce.point.domain.Point;

public interface SavePointPort {
    Point savePoint(Point point);
}
