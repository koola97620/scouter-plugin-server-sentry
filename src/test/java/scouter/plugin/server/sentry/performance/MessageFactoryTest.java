package scouter.plugin.server.sentry.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MessageFactoryTest {

    @DisplayName("장애 메트릭이 존재하지 않으면 빈값을 반환한다.")
    @Test
    void no_occur_failure() {
        PerformanceInfo performanceInfo = new PerformanceInfo.builder()
                .objectInfo(
                        new ObjectInfo("objName", 1234, "objType", "objFamily")
                )
                .gcTimeInfo(
                        new GcTimeInfo(1000L, 500L)
                )
                .tpsInfo(
                        new TpsInfo(3.5f, 1.0f)
                )
                .activeServiceInfo(
                        new ActiveServiceInfo(50, 20)
                )
                .build();

        Optional<Message> message = MessageFactory.createMessage(performanceInfo);
        assertThat(message.isPresent()).isFalse();
    }

    @DisplayName("GC 시간이 GC임계점보다 크면 GC 예외 클래스 반환한다.")
    @Test
    void occur_gc_failure() {
        PerformanceInfo performanceInfo = new PerformanceInfo.builder()
                .objectInfo(
                        new ObjectInfo("objName", 1234, "objType", "objFamily")
                )
                .gcTimeInfo(
                        new GcTimeInfo(300L, 500L)
                )
                .tpsInfo(
                        new TpsInfo(3.5f, 1.0f)
                )
                .activeServiceInfo(
                        new ActiveServiceInfo(50, 20)
                )
                .build();

        Optional<Message> message = MessageFactory.createMessage(performanceInfo);
        assertThat(message.get().throwException()).isInstanceOf(GcTimeOverThreshold.class);
    }

    @DisplayName("TPS가 TPS 임계점보다 크면 TPS 예외 클래스 반환한다.")
    @Test
    void occur_tps_failure() {
        PerformanceInfo performanceInfo = new PerformanceInfo.builder()
                .objectInfo(
                        new ObjectInfo("objName", 1234, "objType", "objFamily")
                )
                .gcTimeInfo(
                        new GcTimeInfo(700L, 500L)
                )
                .tpsInfo(
                        new TpsInfo(3.5f, 8.0f)
                )
                .activeServiceInfo(
                        new ActiveServiceInfo(50, 20)
                )
                .build();

        Optional<Message> message = MessageFactory.createMessage(performanceInfo);
        assertThat(message.get().throwException()).isInstanceOf(TpsOverThreshold.class);
    }

    @DisplayName("활성서비스 수가 임계점보다 크면 활성서비스 예외 클래스 반환한다.")
    @Test
    void occur_active_service_failure() {
        PerformanceInfo performanceInfo = new PerformanceInfo.builder()
                .objectInfo(
                        new ObjectInfo("objName", 1234, "objType", "objFamily")
                )
                .gcTimeInfo(
                        new GcTimeInfo(700L, 500L)
                )
                .tpsInfo(
                        new TpsInfo(3.5f, 3.0f)
                )
                .activeServiceInfo(
                        new ActiveServiceInfo(50, 80)
                )
                .build();

        Optional<Message> message = MessageFactory.createMessage(performanceInfo);
        assertThat(message.get().throwException()).isInstanceOf(ActiveServiceOverThreshold.class);
    }

    @DisplayName("장애 메트릭이 두개면 MultipleOverThreshold 예외 클래스 반환한다.")
    @Test
    void two_failure_occur() {
        PerformanceInfo performanceInfo = new PerformanceInfo.builder()
                .objectInfo(
                        new ObjectInfo("objName", 1234, "objType", "objFamily")
                )
                .gcTimeInfo(
                        new GcTimeInfo(700L, 500L)
                )
                .tpsInfo(
                        new TpsInfo(3.5f, 4.0f)
                )
                .activeServiceInfo(
                        new ActiveServiceInfo(50, 80)
                )
                .build();

        Optional<Message> message = MessageFactory.createMessage(performanceInfo);
        assertThat(message.get().throwException()).isInstanceOf(MultipleOverThreshold.class);
    }

    @DisplayName("장애 메트릭이 세개면 MultipleOverThreshold 예외 클래스 반환한다.")
    @Test
    void three_failure_occur() {
        PerformanceInfo performanceInfo = new PerformanceInfo.builder()
                .objectInfo(
                        new ObjectInfo("objName", 1234, "objType", "objFamily")
                )
                .gcTimeInfo(
                        new GcTimeInfo(700L, 1000L)
                )
                .tpsInfo(
                        new TpsInfo(3.5f, 4.0f)
                )
                .activeServiceInfo(
                        new ActiveServiceInfo(50, 80)
                )
                .build();

        Optional<Message> message = MessageFactory.createMessage(performanceInfo);
        assertThat(message.get().throwException()).isInstanceOf(MultipleOverThreshold.class);
    }

}