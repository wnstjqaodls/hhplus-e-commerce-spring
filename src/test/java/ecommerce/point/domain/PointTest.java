package ecommerce.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class PointTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp () {
    }

    @DisplayName("충전시 100만원 이상은 충전 할 수 없다.")
    @Test
    void charge_amountIsOverOrEqualChargeLimit_fail () {
        // given
        Point point = new Point(1L);
        long chargeLimit = 1_000_000L; // 100만원
        // 1_000_000L = 100만원
        ActivityWindow activityWindow = new ActivityWindow(List.of(
            new Activity(1L,1L, 1_000_000L, LocalDateTime.now())
        ));

        // when
        point.charge(activityWindow,1_000_000L);

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            point.charge(activityWindow , chargeLimit + 1);
        }, "충전 금액은 100만원을 초과할 수 없습니다.");
    }

    @DisplayName("1만원 충전시 정상적으로 충전이 성공한다.")
    @Test
    void charge_amountIsUnderChargeLimit_success () {
        // given
        Point point = new Point(1L);
        long chargeAmount = 10_000L; // 1만원
        long chargedAmount = 10_000L; // 충전 완료 금액
        ActivityWindow activityWindow = new ActivityWindow(List.of(
            new Activity(1L,1L, 10_000L, LocalDateTime.now())
        ));

        // when
        point.charge(activityWindow, chargedAmount);

        // then
        assertThat(point.getBalance(activityWindow)).isEqualTo(chargedAmount);
    }

    @DisplayName("포인트 사용금액이 잔액보다 많을 경우 예외가 발생한다.")
    @Test
    void use_amountIsOverBalance_fail () {
        // given
        Point point = new Point(1L);
        Long balance = 10_000L; // 잔액
        Long useAmount = 10_001L; // 사용 금액
        ActivityWindow activityWindow = new ActivityWindow(List.of(
            new Activity(1L,1L, 10_000L, LocalDateTime.now())
        ));

        // when
        point.charge(activityWindow, balance);
        point.use(activityWindow, useAmount);

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            point.use(activityWindow, useAmount);
        }, "사용 금액이 잔액보다 많습니다.");

    }

    @DisplayName("포인트 사용시 잔액이 차감된다.")
    @Test
    void use_amountIsUnderBalance_success () {
        // given
        Point point = new Point(1L);
        Long balance = 10_000L; // 잔액
        Long useAmount = 5_000L; // 사용 금액
        ActivityWindow activityWindow = new ActivityWindow(List.of(
            new Activity(1L,1L, 10_000L, LocalDateTime.now())
        ));

        // when
        point.charge(activityWindow, balance);
        Activity remainingBalance = point.use(activityWindow, useAmount);

        // then
        assertThat(remainingBalance.getAmount()).isEqualTo(balance - useAmount);
    }



}
