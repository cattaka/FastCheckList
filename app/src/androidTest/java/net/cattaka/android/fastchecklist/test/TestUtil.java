package net.cattaka.android.fastchecklist.test;

import android.os.SystemClock;

/**
 * Created by cattaka on 15/07/05.
 */
public class TestUtil {
    public static void waitForBoolean(BooleanFunc func, int timeout) {
        long time = SystemClock.elapsedRealtime();
        do {
            if (func.run()) {
                break;
            }
            SystemClock.sleep(50);
        } while (SystemClock.elapsedRealtime() - time <= time);
    }

    public interface BooleanFunc {
        public boolean run();
    }
}
