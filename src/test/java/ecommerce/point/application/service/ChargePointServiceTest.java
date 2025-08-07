package ecommerce.point.application.service;

import ecommerce.point.application.port.out.LoadPointPort;
import ecommerce.point.application.port.out.SavePointPort;
import ecommerce.point.domain.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChargePointServiceTest {

    @InjectMocks
    ChargePointService chargePointService;

    @Mock
    LoadPointPort loadPointPort;

    @Mock
    SavePointPort savePointPort;

    @Test
    @DisplayName("포인트 충전에 성공한다.")
    void chargePointSuccess() {
        // given
        Long userId = 1L;
        long amount = 10000L;
        Point savedPoint = new Point(userId, amount);

        // Mock 설정
        when(savePointPort.savePoint(any(Point.class), anyLong()))
            .thenReturn(savedPoint);

        // when
        long result = chargePointService.charge(userId, amount);

        // then
        assertThat(result).isEqualTo(amount);
    }
}