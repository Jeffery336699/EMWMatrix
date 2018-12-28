package cc.emw.mobile.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.farsunset.cim.client.android.CIMPushManager;
import com.githang.androidcrash.AndroidCrash;
import com.githang.androidcrash.reporter.httpreporter.CrashHttpReporter;
import com.githang.androidcrash.reporter.mailreporter.CrashEmailReporter;
import com.githang.androidcrash.util.AppManager;

import cc.emw.mobile.R;
import cc.emw.mobile.login.WelcomeActivity;
import io.rong.imkit.RongIM;

public class ErrorlogManager {
	
	
	/**
     * 使用EMAIL发送日志
     */
    public static void initEmailReporter(Context context) {
        CrashEmailReporter reporter = new CrashEmailReporter(context) {
            /**
             * 重写此方法，可以弹出自定义的崩溃提示对话框，而不使用系统的崩溃处理。
             * 
             * @param thread
             * @param ex
             */
            @Override
            public void closeApp(Thread thread, Throwable ex) {
                final Activity activity = AppManager.currentActivity();
                /*// 自定义弹出对话框
                new AlertDialog.Builder(activity).setMessage("程序发生异常，现在退出")
                        .setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppManager.AppExit(activity);
                            }
                        }).create().show();*/
                new com.zf.iosdialog.widget.AlertDialog(activity).builder().setMsg("程序发生异常！")
                        .setPositiveButton("重启", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(activity
                                        .getApplicationContext(),
                                        WelcomeActivity.class);
                                PendingIntent restartIntent = PendingIntent.getActivity(
                                        activity.getApplicationContext(), 0,
                                        intent,
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                AlarmManager mgr = (AlarmManager) activity
                                        .getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC,
                                        System.currentTimeMillis() + 0,
                                        restartIntent); // 0毫秒钟后重启应用
                                RongIM.getInstance().logout();
                                CIMPushManager.destory(activity);
                                AppManager.AppExit(activity);// 退出程序
                            }
                        }).setNegativeButton("退出", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RongIM.getInstance().logout();
                        CIMPushManager.destory(activity);
                        AppManager.AppExit(activity);// 退出程序
                    }
                }).show();
                Log.d("MyApplication", "thead:" + Thread.currentThread().getName());
            }
        };
        reporter.setReceiver("tao.zhou@zkbr.cc");// your email
        reporter.setSender("shaobo.zhuang@zkbr.cc");// send email
        reporter.setSendPassword("Welcome2zkbr!");// send email pwd
        reporter.setSMTPHost("mail.zkbr.cc"); // smtp host
        reporter.setPort("2525");// ssl--994/465，非ssl--25 // port,by ssl or not
        AndroidCrash.getInstance().setCrashReporter(reporter).init(context);
    }

    /**
     * 使用HTTP发送日志
     */
    public static void initHttpReporter(Context context) {
        CrashHttpReporter reporter = new CrashHttpReporter(context) {
            /**
             * 重写此方法，可以弹出自定义的崩溃提示对话框，而不使用系统的崩溃处理。
             * 
             * @param thread
             * @param ex
             */
            @Override
            public void closeApp(Thread thread, Throwable ex) {
                final Activity activity = AppManager.currentActivity();
                Toast.makeText(activity, "发生异常，正在退出", Toast.LENGTH_SHORT).show();
                // 自定义弹出对话框
                new AlertDialog.Builder(activity).setMessage("程序发生异常，现在退出")
                        .setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppManager.AppExit(activity);
                            }
                        }).create().show();
                Log.d("MyApplication", "thead:" + Thread.currentThread().getName());
            }
        };
        reporter.setUrl("接收你请求的API").setFileParam("fileName").setToParam("to").setTo("你的接收邮箱")
                .setTitleParam("subject").setBodyParam("message");
        reporter.setCallback(new CrashHttpReporter.HttpReportCallback() {
            @Override
            public boolean isSuccess(int i, String s) {
                return s.endsWith("ok");
            }
        });
        AndroidCrash.getInstance().setCrashReporter(reporter).init(context);
    }
}
