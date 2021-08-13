package scouter.plugin.server.sentry.xlog;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class SampleBucketTest {

    // 다른 아이디어
    // @Test
    void name() throws InterruptedException {
        ErrorCount[] cnts = new ErrorCount[6];

        for (int i = 0; i < 100; i++) {
            long timestamp = System.currentTimeMillis();
            LocalTime ldt = new Timestamp(timestamp).toLocalDateTime().toLocalTime().withNano(0);
            ldt = ldt.minusSeconds(ldt.getSecond() % 10); // 초 1의자리를 뺌 13:20:23 -> 13:20:20
            int id = ldt.getSecond() / 10; // 초 10의자리
            long ten = timestamp / 10000; // 초 10,20,30
            if (cnts[id] == null) {
                cnts[id] = new ErrorCount(ldt, ten, 1);
            } else {
                if (cnts[id].time == ten) {
                    cnts[id].count = cnts[id].count + 1;
                    if (cnts[id].count == 5) {
                        System.out.println("------> ALERT!");
                    }
                } else {
                    cnts[id] = new ErrorCount(ldt, ten, 1);
                }
            }
            System.out.println(Arrays.toString(cnts));
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(4) + 1);
        }
    }

    public static class ErrorCount {
        private LocalTime ldt;
        private long time; // 10초 단위 버림
        private int count;

        public ErrorCount(LocalTime ldt, long time, int count) {
            this.ldt = ldt;
            this.time = time;
            this.count = count;
        }

        @Override
        public String toString() {
            return "ErrCt{" +
                    "ldt=" + ldt +
                    ", time=" + time +
                    ", count=" + count +
                    '}';
        }
    }
}
