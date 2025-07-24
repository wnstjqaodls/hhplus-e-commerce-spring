package ecommerce.point.application.port.out;

import ecommerce.point.domain.Activity;

public interface SavePointPort {
    Activity saveActivity(Activity activity);
}
