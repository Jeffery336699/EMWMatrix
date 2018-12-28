package com.githang.androidcrash;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.githang.androidcrash.log.CrashCatcher;
import com.githang.androidcrash.reporter.AbstractCrashHandler;

/**
 * User: Geek_Soledad(msdx.android@qq.com)
 * Date: 2014-11-03
 * Time: 21:37
 * FIXME
 */
public class AndroidCrash {
    private static final AndroidCrash instance = new AndroidCrash();

    private AbstractCrashHandler mReporter;

    private String mLogPath;
    private String mLogName;

    private AndroidCrash(){}

    public static AndroidCrash getInstance() {
        return instance;
    }

    /**
     * 设置报告处理。
     * @param reporter
     * @return
     */
    public AndroidCrash setCrashReporter(AbstractCrashHandler reporter) {
        mReporter = reporter;
        return this;
    }

    /**
     * 设置日志存放路径
     * @param path
     * @return
     */
    public AndroidCrash setLogFilePath(String path) {
    	mLogPath = path;
        return this;
    }
    
    /**
     * 设置日志文件名。
     * @param name
     * @return
     */
    public AndroidCrash setLogFileName(String name) {
        mLogName = name;
        return this;
    }

    public void init(Context mContext) {
        if (mLogName == null) {
            mLogName = "AndroidCrash.log";
        }
        File logFile = getLogFile(mContext, mLogName);
        CrashCatcher.getInstance().init(logFile, mReporter, mReporter.buildBody(mContext));
        Thread.setDefaultUncaughtExceptionHandler(CrashCatcher.getInstance());
        Log.d("AndroidCrash", "init success: " + Thread.getDefaultUncaughtExceptionHandler().getClass());
    }

    private File getLogFile(Context context, String name) {
    	File path = null;
    	if (mLogPath != null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		path = new File(mLogPath);
    	} else {
    		path = context.getFilesDir();
    	}
    	
    	return new File(path, name);
    }

}