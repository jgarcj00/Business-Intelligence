package org.tensorflow.demo.env;

import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public final class Logger {
    private static final int DEFAULT_MIN_LOG_LEVEL = 3;
    private static final String DEFAULT_TAG = "tensorflow";
    private static final Set<String> IGNORED_CLASS_NAMES = new HashSet(DEFAULT_MIN_LOG_LEVEL);
    private final String messagePrefix;
    private int minLogLevel;
    private final String tag;

    static {
        IGNORED_CLASS_NAMES.add("dalvik.system.VMStack");
        IGNORED_CLASS_NAMES.add("java.lang.Thread");
        IGNORED_CLASS_NAMES.add(Logger.class.getCanonicalName());
    }

    public Logger(Class<?> cls) {
        this(cls.getSimpleName());
    }

    public Logger(String str) {
        this(DEFAULT_TAG, str);
    }

    public Logger(String str, String str2) {
        this.minLogLevel = DEFAULT_MIN_LOG_LEVEL;
        this.tag = str;
        if (str2 == null) {
            str2 = getCallerSimpleName();
        }
        if (str2.length() > null) {
            str = new StringBuilder();
            str.append(str2);
            str.append(": ");
            str2 = str.toString();
        }
        this.messagePrefix = str2;
    }

    public Logger() {
        this(DEFAULT_TAG, null);
    }

    public Logger(int i) {
        this(DEFAULT_TAG, null);
        this.minLogLevel = i;
    }

    public void setMinLogLevel(int i) {
        this.minLogLevel = i;
    }

    public boolean isLoggable(int i) {
        if (i < this.minLogLevel) {
            if (Log.isLoggable(this.tag, i) == 0) {
                return false;
            }
        }
        return true;
    }

    private static String getCallerSimpleName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int i = 0;
        int length = stackTrace.length;
        while (i < length) {
            String className = stackTrace[i].getClassName();
            if (IGNORED_CLASS_NAMES.contains(className)) {
                i++;
            } else {
                String[] split = className.split("\\.");
                return split[split.length - 1];
            }
        }
        return Logger.class.getSimpleName();
    }

    private String toMessage(String str, Object... objArr) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.messagePrefix);
        if (objArr.length > 0) {
            str = String.format(str, objArr);
        }
        stringBuilder.append(str);
        return stringBuilder.toString();
    }

    /* renamed from: v */
    public void m7v(String str, Object... objArr) {
        if (isLoggable(2)) {
            Log.v(this.tag, toMessage(str, objArr));
        }
    }

    /* renamed from: v */
    public void m8v(Throwable th, String str, Object... objArr) {
        if (isLoggable(2)) {
            Log.v(this.tag, toMessage(str, objArr), th);
        }
    }

    /* renamed from: d */
    public void m1d(String str, Object... objArr) {
        if (isLoggable(DEFAULT_MIN_LOG_LEVEL)) {
            Log.d(this.tag, toMessage(str, objArr));
        }
    }

    /* renamed from: d */
    public void m2d(Throwable th, String str, Object... objArr) {
        if (isLoggable(DEFAULT_MIN_LOG_LEVEL)) {
            Log.d(this.tag, toMessage(str, objArr), th);
        }
    }

    /* renamed from: i */
    public void m5i(String str, Object... objArr) {
        if (isLoggable(4)) {
            Log.i(this.tag, toMessage(str, objArr));
        }
    }

    /* renamed from: i */
    public void m6i(Throwable th, String str, Object... objArr) {
        if (isLoggable(4)) {
            Log.i(this.tag, toMessage(str, objArr), th);
        }
    }

    /* renamed from: w */
    public void m9w(String str, Object... objArr) {
        if (isLoggable(5)) {
            Log.w(this.tag, toMessage(str, objArr));
        }
    }

    /* renamed from: w */
    public void m10w(Throwable th, String str, Object... objArr) {
        if (isLoggable(5)) {
            Log.w(this.tag, toMessage(str, objArr), th);
        }
    }

    /* renamed from: e */
    public void m3e(String str, Object... objArr) {
        if (isLoggable(6)) {
            Log.e(this.tag, toMessage(str, objArr));
        }
    }

    /* renamed from: e */
    public void m4e(Throwable th, String str, Object... objArr) {
        if (isLoggable(6)) {
            Log.e(this.tag, toMessage(str, objArr), th);
        }
    }
}
