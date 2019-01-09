package org.tensorflow.contrib.android;

public class RunStats implements AutoCloseable {
    private static byte[] fullTraceRunOptions = new byte[]{(byte) 8, (byte) 3};
    private long nativeHandle = allocate();

    private static native void add(long j, byte[] bArr);

    private static native long allocate();

    private static native void delete(long j);

    private static native String summary(long j);

    public static byte[] runOptions() {
        return fullTraceRunOptions;
    }

    public void close() {
        if (this.nativeHandle != 0) {
            delete(this.nativeHandle);
        }
        this.nativeHandle = 0;
    }

    public synchronized void add(byte[] bArr) {
        add(this.nativeHandle, bArr);
    }

    public synchronized String summary() {
        return summary(this.nativeHandle);
    }
}
