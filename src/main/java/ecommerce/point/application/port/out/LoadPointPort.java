package ecommerce.point.application.port.out;

import ecommerce.point.domain.ActivityWindow;

public interface LoadPointPort {
    ActivityWindow loadActivityWindow(Long pointId);


}
