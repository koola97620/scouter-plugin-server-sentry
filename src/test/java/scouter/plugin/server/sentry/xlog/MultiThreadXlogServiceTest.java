package scouter.plugin.server.sentry.xlog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import scouter.lang.pack.XLogPack;
import scouter.plugin.server.sentry.performance.ConfigurationConstants;
import scouter.plugin.server.sentry.performance.MonitoringGroupConfigure;
import scouter.plugin.server.sentry.send.MessageSender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MultiThreadXlogServiceTest {
    private MonitoringGroupConfigure groupConf = mock(MonitoringGroupConfigure.class);
    private MessageSender sender = mock(MessageSender.class);
    private XlogService xlogService = new XlogService(groupConf, sender);


    @DisplayName("장애 판단시 메시지 전송")
    @Test
    void send_message() throws Exception {
        int errorCountThreshold = 5;
        int errorTimeRange = 3;

        when(groupConf.getBoolean(ConfigurationConstants.ALERT_ENABLED, "tomcat", false)).thenReturn(true);
        when(groupConf.getInt(ConfigurationConstants.ERROR_COUNT, null, 0)).thenReturn(errorCountThreshold);
        when(groupConf.getInt(ConfigurationConstants.ERROR_TIME_RANGE, null, 0)).thenReturn(errorTimeRange);
        long standardTime = System.currentTimeMillis();

        insertData(standardTime, errorTimeRange);

        XLogPack xlogPack = new XLogPack();
        xlogPack.endTime = standardTime + 10000;
        xlogPack.error = 1;
        xlogService.process(xlogPack, "tomcat", "tomcat7");

        ArgumentCaptor<RuntimeException> captor = ArgumentCaptor.forClass(RuntimeException.class);
        then(sender).should().send(captor.capture());
        RuntimeException result = captor.getValue();

        assertThat(result.getMessage()).isEqualTo("[tomcat3: 3,tomcat6: 3,tomcat2: 2,tomcat5: 2,tomcat1: 1,tomcat7: 1,tomcat4: 1]");
        assertThat(xlogService.getXlogErrorWindow().size()).isEqualTo(0);
    }

    @DisplayName("장애 기준 못 미치면 전송하지 않는다")
    @Test
    void not_over_threshold_not_send_message() throws Exception {
        int errorCountThreshold = 20;
        int errorTimeRange = 3;

        when(groupConf.getBoolean(ConfigurationConstants.ALERT_ENABLED, "tomcat", false)).thenReturn(true);
        when(groupConf.getInt(ConfigurationConstants.ERROR_COUNT, null, 0)).thenReturn(errorCountThreshold);
        when(groupConf.getInt(ConfigurationConstants.ERROR_TIME_RANGE, null, 0)).thenReturn(errorTimeRange);
        long standardTime = System.currentTimeMillis();

        insertData(standardTime, errorTimeRange);

        XLogPack xlogPack = new XLogPack();
        xlogPack.endTime = standardTime + 10000;
        xlogPack.error = 1;
        xlogService.process(xlogPack, "tomcat", "tomcat7");

        then(sender).shouldHaveNoInteractions();
    }

    @DisplayName("메시지 전송 후 일정시간동안 에러 로그 수집 안한다")
    @Test
    void after_send_not_collect_error_in_time() throws Exception {
        int errorCountThreshold = 5;
        int errorTimeRange = 3;
        when(groupConf.getBoolean(ConfigurationConstants.ALERT_ENABLED, "tomcat", false)).thenReturn(true);
        when(groupConf.getInt(ConfigurationConstants.ERROR_COUNT, null, 0)).thenReturn(errorCountThreshold);
        when(groupConf.getInt(ConfigurationConstants.ERROR_TIME_RANGE, null, 0)).thenReturn(errorTimeRange);
        long standardTime = System.currentTimeMillis();

        insertData(standardTime, errorTimeRange);

        XLogPack xlogPack = new XLogPack();
        xlogPack.endTime = standardTime + 10000;
        xlogPack.error = 1;
        xlogService.process(xlogPack, "tomcat", "tomcat7");

        ArgumentCaptor<RuntimeException> captor = ArgumentCaptor.forClass(RuntimeException.class);
        then(sender).should().send(captor.capture());
        RuntimeException result = captor.getValue();

        assertThat(result.getMessage()).isEqualTo("[tomcat3: 3,tomcat6: 3,tomcat2: 2,tomcat5: 2,tomcat1: 1,tomcat7: 1,tomcat4: 1]");

        for (int i = 1; i < 30; i++) {
            XLogPack log = new XLogPack();
            log.endTime = standardTime + 10000 + 1000 * i;
            log.error = 1;
            xlogService.process(log, "tomcat", "tomcat9");
            assertThat(xlogService.getXlogErrorWindow().size()).isZero();
        }

        XLogPack log = new XLogPack();
        log.endTime = standardTime + 10000 + 1000 * 30;
        log.error = 1;
        xlogService.process(log, "tomcat", "tomcat9");
        assertThat(xlogService.getXlogErrorWindow().size()).isEqualTo(1);

    }

    @AfterEach
    void tearDown() {
        XlogErrorWindow xlogErrorWindow = xlogService.getXlogErrorWindow();
        xlogErrorWindow.initQueue();
    }

    private void insertData(long standardTime, int errorTimeRange) {
        XlogErrorWindow xlogErrorWindow = xlogService.getXlogErrorWindow();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(() -> {
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 2500, "tomcat1"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 3500, "tomcat2"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 4500, "tomcat1"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 7000, "tomcat1"), errorTimeRange);
        });
        executorService.execute(() -> {
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 2000, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 4000, "tomcat2"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 5000, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 6000, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 6700, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 7000, "tomcat2"), errorTimeRange);

        });
        executorService.execute(() -> {
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 1000, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 3000, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 4000, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 5000, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 8000, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 8000, "tomcat2"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 8000, "tomcat6"), errorTimeRange);
        });
        executorService.execute(() -> {
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 1000, "tomcat4"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 2000, "tomcat4"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 3000, "tomcat4"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 5000, "tomcat4"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 10000, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 9000, "tomcat4"), errorTimeRange);
        });
        executorService.execute(() -> {
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 2000, "tomcat1"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 3000, "tomcat4"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 4000, "tomcat6"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 5000, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 6000, "tomcat4"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 9000, "tomcat3"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 9000, "tomcat5"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 10000, "tomcat5"), errorTimeRange);
        });
        executorService.execute(() -> {
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 7000, "tomcat6"), errorTimeRange);
            xlogErrorWindow.registFailure(new XlogStub(standardTime + 9000, "tomcat6"), errorTimeRange);
        });
    }

}
