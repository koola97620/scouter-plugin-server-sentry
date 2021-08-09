package scouter.plugin.server.sentry.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scouter.lang.counters.CounterConstants;
import scouter.lang.pack.PerfCounterPack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PerformanceCollectorTest {

    private MonitoringGroupConfigure groupConf = mock(MonitoringGroupConfigure.class);
    private ObjectInfo objectInfo = mock(ObjectInfo.class);
    private PerfCounterPack pack;
    private PerformanceCollector performanceCollector;

    @BeforeEach
    void setUp() {
        pack = new PerfCounterPack();
        performanceCollector = new PerformanceCollector(groupConf, pack, objectInfo);
    }

    @DisplayName("GC 수치가 GC 임계점보다 높으면 true 반환")
    @Test
    void collect_gcTime() {
        when(groupConf.getLong(ConfigurationConstants.GC_TIME_THRESHOLD,objectInfo.getObjType(),0)).thenReturn(500L);
        pack.data.put(CounterConstants.JAVA_GC_TIME, 1000L);
        PerformanceInfo performanceInfo = performanceCollector.collect();
        assertThat(performanceInfo.getGcTimeInfo().getGcTime()).isEqualTo(1000L);
        assertThat(performanceInfo.getGcTimeInfo().getGcThreshold()).isEqualTo(500L);
        assertThat(performanceInfo.getGcTimeInfo().isOverThreshold()).isTrue();
    }

    @DisplayName("GC 임계점이 0이면 false 반환한다")
    @Test
    void gc_threshold_0_then_false() {
        when(groupConf.getLong(ConfigurationConstants.GC_TIME_THRESHOLD,objectInfo.getObjType(),0)).thenReturn(0L);
        pack.data.put(CounterConstants.JAVA_GC_TIME, 1000L);
        PerformanceInfo performanceInfo = performanceCollector.collect();
        assertThat(performanceInfo.getGcTimeInfo().getGcTime()).isEqualTo(1000L);
        assertThat(performanceInfo.getGcTimeInfo().getGcThreshold()).isEqualTo(0L);
        assertThat(performanceInfo.getGcTimeInfo().isOverThreshold()).isFalse();
    }

    @DisplayName("서비스 수가 임계점 보다 많으면 true 반환한다")
    @Test
    void collect_activeService() {
        when(groupConf.getInt(ConfigurationConstants.ACTIVE_SERVICE_THRESHOLD,objectInfo.getObjType(),0)).thenReturn(10);
        pack.data.put(CounterConstants.WAS_ACTIVE_SERVICE, 20);
        PerformanceInfo performanceInfo = performanceCollector.collect();
        assertThat(performanceInfo.getActiveServiceInfo().getActiveService()).isEqualTo(20);
        assertThat(performanceInfo.getActiveServiceInfo().getActiveServiceThreshold()).isEqualTo(10);
        assertThat(performanceInfo.getActiveServiceInfo().isOverThreshold()).isTrue();
    }

    @DisplayName("서비스 임계점이 0이면 false 반환한다")
    @Test
    void as_threshold_0_then_false() {
        when(groupConf.getInt(ConfigurationConstants.ACTIVE_SERVICE_THRESHOLD,objectInfo.getObjType(),0)).thenReturn(0);
        pack.data.put(CounterConstants.WAS_ACTIVE_SERVICE, 20);
        PerformanceInfo performanceInfo = performanceCollector.collect();
        assertThat(performanceInfo.getActiveServiceInfo().getActiveService()).isEqualTo(20);
        assertThat(performanceInfo.getActiveServiceInfo().getActiveServiceThreshold()).isEqualTo(0);
        assertThat(performanceInfo.getActiveServiceInfo().isOverThreshold()).isFalse();
    }

}