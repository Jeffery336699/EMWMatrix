package cc.emw.mobile.util;

import android.util.Log;

/**
 * Created by jven.wu on 2016/8/23.
 * 可全局控制打印
 */
public class Logger {
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    public static int err(final String TAG, final String message) {
        return Log.e(TAG, attachThreadId(message));
    }

    public static int err(final String TAG, final String message, Throwable throwable) {
        return Log.e(TAG, attachThreadId(message), throwable);
    }

    public static int w(final String TAG, final String message) {
        return SHOW_LOGS ? Log.w(TAG, attachThreadId(message)) : 0;
    }

    public static int inf(final String TAG, final String message) {
        return SHOW_LOGS ? Log.i(TAG, attachThreadId(message)) : 0;
    }

    public static int d(final String TAG, final String message) {
        return SHOW_LOGS ? Log.d(TAG, attachThreadId(message)) : 0;
    }

    public static int v(final String TAG, final String message) {
        return SHOW_LOGS ? Log.v(TAG, attachThreadId(message)) : 0;
    }

    private static String attachThreadId(String str) {
        return "[Thread:" + Thread.currentThread().getId() + "] " + str;
    }

}
