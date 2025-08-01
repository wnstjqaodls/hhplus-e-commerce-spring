package ecommerce.point.adapter.in.web;

import ecommerce.point.application.port.in.ChargePointUseCase;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestAttributes;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class ChargePointControllerTest {

    private ChargePointUseCase chargePointUseCase;
    private RequestAttributes requestAttributes;

    @Test
    @DisplayName("만료된_사용자의_요청_대해_포인트충전_요청에_실패한다")
    public void theExpired_Request_pointCharging_(){
        // given
        chargePointUseCase = new ChargePointUseCase();
        Long userId = 1L;
        Long points = 1000L;

        //when
        // 만료된 사용자의 요청 검증 메서드
        // 예시로, 만료된 사용자의 요청을 검증하는 로직을 추가합니다.
        boolean isExpired = requestAttributes.getSessionId() ; // 예시로 만료된 사용자로 설정
        if (isExpired) {
            throw new IllegalArgumentException("만료된 사용자의 요청입니다.");
        }


        chargePointUseCase.charge(userId, points);

        //then




    }


}
