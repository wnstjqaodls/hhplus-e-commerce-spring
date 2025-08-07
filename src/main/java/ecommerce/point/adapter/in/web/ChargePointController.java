package ecommerce.point.adapter.in.web;

import ecommerce.point.application.port.in.ChargePointUseCase;
import ecommerce.point.application.port.in.GetPointBalanceUseCase;
import ecommerce.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/points")
public class ChargePointController {

    private static final Logger log = LoggerFactory.getLogger(ChargePointController.class);

    private final ChargePointUseCase chargePointUseCase;
    private final GetPointBalanceUseCase getPointBalanceUseCase;

    public ChargePointController(ChargePointUseCase chargePointUseCase, GetPointBalanceUseCase getPointBalanceUseCase) {
        this.chargePointUseCase = chargePointUseCase;
        this.getPointBalanceUseCase = getPointBalanceUseCase;
    }

    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<PointReponseDto>> chargePoint(@RequestBody PointRequestDto requestDto) {
        log.info("POST /points/charge 요청 수신: userId={}, amount={}", requestDto.getUserId(), requestDto.getAmount());

        try {
            long newBalance = chargePointUseCase.charge(requestDto.getUserId(), requestDto.getAmount());

            PointReponseDto responseDto = new PointReponseDto(
                newBalance,
                requestDto.getUserId(),
                java.time.LocalDateTime.now().toString()
            );

            log.info("포인트 충전 성공: userId={}, newBalance={}", requestDto.getUserId(), newBalance);
            return ResponseEntity.ok(ApiResponse.success(responseDto));

        } catch (IllegalArgumentException e) {
            log.warn("포인트 충전 실패 (잘못된 요청): {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("포인트 충전 중 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버 오류 발생"));
        }
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<ApiResponse<PointReponseDto>> getPointBalance(@PathVariable Long userId) {
        log.info("GET /points/balance/{} 요청 수신", userId);

        try {
            long balance = getPointBalanceUseCase.getBalance(userId);

            PointReponseDto responseDto = new PointReponseDto(
                balance,
                userId,
                java.time.LocalDateTime.now().toString()
            );

            log.info("포인트 조회 성공: userId={}, balance={}", userId, balance);
            return ResponseEntity.ok(ApiResponse.success(responseDto));

        } catch (Exception e) {
            log.error("포인트 조회 중 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버 오류 발생"));
        }
    }
}
