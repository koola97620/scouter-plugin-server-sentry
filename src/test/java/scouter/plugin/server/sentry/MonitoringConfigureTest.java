package scouter.plugin.server.sentry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import scouter.server.Configure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MonitoringConfigureTest {

    @DisplayName("sentryDsn 조회")
    @Test
    void get_sentry_dsn() {
        String objType = "testObj";
        Configure configure = mock(Configure.class);
        MonitoringConfigure monitoringConfigure = new MonitoringConfigure(configure);

        assertThat(monitoringConfigure.getValue("ext_plugin_sentry_dsn", objType, "https://822e0@o3724.ingest.sentry.io/1547877"))
                .isEqualTo("https://822e0@o3724.ingest.sentry.io/1547877");
    }

}