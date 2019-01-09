package org.tensorflow.demo.env;

import android.os.SystemClock;

public class SplitTimer {
    private long lastCpuTime;
    private long lastWallTime;
    private final Logger logger;

    public SplitTimer(String str) {
        this.logger = new Logger(str);
        newSplit();
    }

    public void newSplit() {
        this.lastWallTime = SystemClock.uptimeMillis();
        this.lastCpuTime = SystemClock.currentThreadTimeMillis();
    }

    public void endSplit(String str) {
        long uptimeMillis = SystemClock.uptimeMillis();
        this.logger.m5i("%s: cpu=%dms wall=%dms", str, Long.valueOf(SystemClock.currentThreadTimeMillis() - this.lastCpuTime), Long.valueOf(uptimeMillis - this.lastWallTime));
        this.lastWallTime = uptimeMillis;
        this.lastCpuTime = r2;
    }
}
