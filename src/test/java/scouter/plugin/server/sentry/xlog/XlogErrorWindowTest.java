package scouter.plugin.server.sentry.xlog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class XlogErrorWindowTest {

    private long beforeTime;
    private XlogErrorWindow xlogErrorWindow;
    private XlogErrorWindowHelper helper;

    @BeforeEach
    void setUp() {
        xlogErrorWindow = new XlogErrorWindow();
        helper = new XlogErrorWindowHelper(xlogErrorWindow);
        beforeTime = System.currentTimeMillis();
        helper.registFailure(new XlogStub(beforeTime));
        helper.registFailure(new XlogStub(beforeTime + 1000)); //2000
        helper.registFailure(new XlogStub(beforeTime + 1000)); //2000
        helper.registFailure(new XlogStub(beforeTime + 1000)); //2000
        helper.registFailure(new XlogStub(beforeTime + 1000)); //2000
        helper.registFailure(new XlogStub(beforeTime + 2000)); //3000
        helper.registFailure(new XlogStub(beforeTime + 2000)); //3000
        helper.registFailure(new XlogStub(beforeTime + 2000)); //3000
        helper.registFailure(new XlogStub(beforeTime + 3000)); //4000
        helper.registFailure(new XlogStub(beforeTime + 3000)); //4000
        helper.registFailure(new XlogStub(beforeTime + 3000)); //4000
    }

    @AfterEach
    void tearDown() {
        xlogErrorWindow.initQueue();
    }

    @Test
    void delete_prev_error() {
        int countTimeRange = 3;
        long current = beforeTime + 5000;  // 6000

        //xlogErrorWindow.registFailure(new XlogStub(current), countTimeRange);
        helper.registFailure(new XlogStub(current), countTimeRange);

        assertThat(helper.size()).isEqualTo(7);
    }

    @Test
    void over_threshold_then_true() {
        int countTimeRange = 3;
        int countThreshold = 5;

        long current = beforeTime + 5000;  // 6000
        helper.registFailure(new XlogStub(current), countTimeRange);
        boolean result = helper.isFailure(countThreshold);

        assertThat(result).isTrue();
    }

    @Test
    void not_over_threshold_then_true() {
        int countTimeRange = 3;
        int countThreshold = 10;

        long current = beforeTime + 5000;  // 6000
        helper.registFailure(new XlogStub(current), countTimeRange);
        boolean result = helper.isFailure(countThreshold);

        assertThat(result).isFalse();
    }
}
