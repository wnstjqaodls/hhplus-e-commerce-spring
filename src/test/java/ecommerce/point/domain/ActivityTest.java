package ecommerce.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityTest {

    @Test
    @DisplayName("Activity 생성 시 포인트 ID와 금액이 설정된다")
    void createActivity() {
        // given
        Long pointId = 1L;
        long amount = 1000L;

        // when
        Activity activity = new Activity(pointId, amount);

        // then
        assertThat(activity.getPointId()).isEqualTo(pointId);
        assertThat(activity.getAmount()).isEqualTo(amount);
        assertThat(activity.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("같은 ID를 가진 Activity는 동등하다")
    void equalsByIdOnly() {
        // given
        Activity activity1 = new Activity(1L, 1L, 1000L, LocalDateTime.now());
        Activity activity2 = new Activity(1L, 2L, 2000L, LocalDateTime.now().plusDays(1));

        // then
        assertThat(activity1).isEqualTo(activity2);
    }
}
