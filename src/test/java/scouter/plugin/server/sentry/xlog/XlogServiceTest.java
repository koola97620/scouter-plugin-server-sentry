package scouter.plugin.server.sentry.xlog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import scouter.lang.pack.XLogPack;
import scouter.plugin.server.sentry.performance.ConfigurationConstants;
import scouter.plugin.server.sentry.performance.MonitoringGroupConfigure;
import scouter.plugin.server.sentry.send.MessageSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class XlogServiceTest {
    private MonitoringGroupConfigure groupConf = mock(MonitoringGroupConfigure.class);
    private MessageSender sender = mock(MessageSender.class);
    private XlogService xlogService = new XlogService(groupConf, sender);

    @DisplayName("장애 판단시 메시지 전송")
    @Test
    void occur_failure() throws Exception {
        when(groupConf.getBoolean(ConfigurationConstants.ALERT_ENABLED, "tomcat", false)).thenReturn(true);
        when(groupConf.getInt(ConfigurationConstants.ERROR_COUNT, null, 0)).thenReturn(5);
        when(groupConf.getInt(ConfigurationConstants.ERROR_TIME_RANGE, null, 0)).thenReturn(3);
        long time = System.currentTimeMillis();
        insertSample(time);

        XLogPack xlogPack = new XLogPack();
        xlogPack.endTime = time + 6000;
        xlogPack.error = 1;

        xlogService.process(xlogPack, "tomcat", "tomcat07");

        ArgumentCaptor<RuntimeException> captor = ArgumentCaptor.forClass(RuntimeException.class);
        then(sender).should().send(captor.capture());
        RuntimeException result = captor.getValue();

        assertThat(result.getMessage()).isEqualTo("[tomcat05: 4,tomcat04: 4,tomcat03: 2,tomcat07: 1]");
    }

    @DisplayName("장애 아닐시 메시지 미전송")
    @Test
    void not_occur_failure() throws Exception {
        long time = System.currentTimeMillis();
        insertSample(time);
        when(groupConf.getBoolean(ConfigurationConstants.ALERT_ENABLED, "tomcat", false)).thenReturn(true);
        when(groupConf.getInt(ConfigurationConstants.ERROR_COUNT, null, 0)).thenReturn(20);
        when(groupConf.getInt(ConfigurationConstants.ERROR_TIME_RANGE, null, 0)).thenReturn(3);

        XLogPack xlogPack = new XLogPack();
        xlogPack.endTime = time + 6000;
        xlogPack.error = 1;

        xlogService.process(xlogPack, "tomcat", "tomcat07");

        then(sender).shouldHaveNoInteractions();
    }


    @AfterEach
    void tearDown() {
        XlogErrorWindow xlogErrorWindow = xlogService.getXlogErrorWindow();
        xlogErrorWindow.initQueue();
    }

    private void insertSample(long time) {
        XlogErrorWindow xlogErrorWindow = xlogService.getXlogErrorWindow();
        xlogErrorWindow.regist(new XlogStub(time, "tomcat01"));
        xlogErrorWindow.regist(new XlogStub(time + 1000, "tomcat01"));
        xlogErrorWindow.regist(new XlogStub(time + 2000, "tomcat02"));
        xlogErrorWindow.regist(new XlogStub(time + 2100, "tomcat02"));
        xlogErrorWindow.regist(new XlogStub(time + 2200, "tomcat03"));
        xlogErrorWindow.regist(new XlogStub(time + 3000, "tomcat03"));
        xlogErrorWindow.regist(new XlogStub(time + 3100, "tomcat03"));
        xlogErrorWindow.regist(new XlogStub(time + 4000, "tomcat04"));
        xlogErrorWindow.regist(new XlogStub(time + 4100, "tomcat04"));
        xlogErrorWindow.regist(new XlogStub(time + 4200, "tomcat04"));
        xlogErrorWindow.regist(new XlogStub(time + 4300, "tomcat04"));
        xlogErrorWindow.regist(new XlogStub(time + 5100, "tomcat05"));
        xlogErrorWindow.regist(new XlogStub(time + 5200, "tomcat05"));
        xlogErrorWindow.regist(new XlogStub(time + 5300, "tomcat05"));
        xlogErrorWindow.regist(new XlogStub(time + 5300, "tomcat05"));
    }

}