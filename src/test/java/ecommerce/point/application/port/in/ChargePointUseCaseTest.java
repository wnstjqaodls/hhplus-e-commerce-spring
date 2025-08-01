package ecommerce.point.application.port.in;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class ChargePointUseCaseTest {

    @Test
    @DisplayName("포인트 충전 UseCase 테스트")
    public void chargePointUseCaseTest() {
        // given
        ChargePointUseCase chargePointUseCase = new ChargePointUseCase() {
            @Override
            public long charge(Long pointId, long amount) {
                return amount; // 단순히 amount를 반환하는 더미 구현
            }
        };

        // when
        long chargedAmount = chargePointUseCase.charge(1L, 10000L);

        // then
        assertEquals(10000L, chargedAmount);

    }

    @Test
    @DisplayName("실패하는 포인트 충전 UseCase 테스트")
    public void chargePointUseCaseFailureTest() {
        // given
        ChargePointUseCase chargePointUseCase = new ChargePointUseCase() {
            @Override
            public long charge(Long pointId, long amount) {
                throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
            }
        };

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
            chargePointUseCase.charge(1L, 10000L));

    }


}
