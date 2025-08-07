package ecommerce.point.adapter.in.web;

import ecommerce.point.application.port.in.ChargePointUseCase;
import ecommerce.point.application.port.out.LoadPointPort;
import ecommerce.point.application.port.out.SavePointPort;
import ecommerce.point.application.service.ChargePointService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@WebMvcTest(ChargePointController.class)
public class ChargePointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private LoadPointPort loadPointPort;

    @Mock
    private SavePointPort savePointPort;

    @MockitoBean
    private ChargePointUseCase chargePointUseCase;

    @Test
    @DisplayName("충전 한도초과금액에_대해_포인트충전_요청에_실패한다")
    public void exceed_maxLimitAmount_pointCharging_() throws Exception {
        // given
        ChargePointUseCase chargePointUseCase = new ChargePointService(loadPointPort,savePointPort);

        Long userId = 1L;
        Long points = 1000L;
        String requestBody = String.format("""
            { 
               "userId" : %d,
               "amount" : %d
                }
            """, userId, points);

        //when
        mockMvc.perform(post("/points/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())  // 400 상태 코드
            .andExpect(jsonPath("$.success").value(false));


        assertThatThrownBy(()-> {
            chargePointUseCase.charge(userId, points);
        })
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("충전 한도를 초과했습니다.");


    }


}
