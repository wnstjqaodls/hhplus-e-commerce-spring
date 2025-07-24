package ecommerce.point.application.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChargePointServiceTest {

    @InjectMocks // 실제 ChargePointService 객체를 생성하고 주입
    ChargePointService chargePointService;

    @Test
    @DisplayName("포인트 충전에 성공한다.")
    void chargePointSuccess() {
        // given
        long pointId = 1L;
        long amount = 10000L;
        ChargePointService chargePointService = this.chargePointService;

        // when
        chargePointService.charge(pointId, amount);

        // then
        verify(chargePointService).charge(pointId, amount);
    }
}
