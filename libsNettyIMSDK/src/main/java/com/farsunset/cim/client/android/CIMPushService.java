
package com.farsunset.cim.client.android;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


/**
 * 与服务端连接服务
 *
 * @author 3979434
 */
public class CIMPushService extends Service {

    protected final static int DEF_CIM_PORT = 28888;
    CIMConnectorManager manager;

    /**
     * 定时唤醒的时间间隔，5分钟
     */
    private final static int ALARM_INTERVAL = 5 * 60 * 1000;
    private final static int WAKE_REQUEST_CODE = 6666;

    private final static int GRAY_SERVICE_ID = -1111;

    @Override
    public void onCreate() {
        manager = CIMConnectorManager.getManager(this.getApplicationContext());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, CIMPushInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }

        //发送唤醒广播来促使挂掉的UI进程重新启动起来
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent();
        alarmIntent.setAction(WakeReceiver.GRAY_WAKE_ACTION);
        PendingIntent operation = PendingIntent.getBroadcast(this, WAKE_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_INTERVAL, operation);


        String action;
        if (intent == null) {
            intent = new Intent(CIMPushManager.ACTION_CONNECTION);
            String host = CIMCacheTools.getString(this, CIMCacheTools.KEY_CIM_SERVIER_HOST);
            int port = CIMCacheTools.getInt(this, CIMCacheTools.KEY_CIM_SERVIER_PORT);
            intent.putExtra(CIMCacheTools.KEY_CIM_SERVIER_HOST, host);
            intent.putExtra(CIMCacheTools.KEY_CIM_SERVIER_PORT, port);
        }
        action = intent.getStringExtra(CIMPushManager.SERVICE_ACTION);

        if (CIMPushManager.ACTION_CONNECTION.equals(action)) {
            String host = intent.getStringExtra(CIMCacheTools.KEY_CIM_SERVIER_HOST);
            int port = intent.getIntExtra(CIMCacheTools.KEY_CIM_SERVIER_PORT, DEF_CIM_PORT);
            manager.connect(host, port);
        }

        if (CIMPushManager.ACTION_SENDREQUEST.equals(action)) {
            manager.send(intent.getStringExtra(CIMPushManager.KEY_SEND_BODY));
        }

        if (CIMPushManager.ACTION_DISCONNECTION.equals(action)) {
            manager.closeSession();
        }

        if (CIMPushManager.ACTION_DESTORY.equals(action)) {
            manager.destroy();
            this.stopSelf();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        if (CIMPushManager.ACTION_CONNECTION_STATUS.equals(action)) {
            manager.deliverIsConnected();
        }

        if (CIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action)) {

            if (!manager.isConnected()) {
                Log.d(CIMPushService.class.getSimpleName(), "cimpush isConnected() = false ");
                String host = intent.getStringExtra(CIMCacheTools.KEY_CIM_SERVIER_HOST);
                int port = intent.getIntExtra(CIMCacheTools.KEY_CIM_SERVIER_PORT, DEF_CIM_PORT);
                manager.connect(host, port);
            } else {
                Log.d(CIMPushService.class.getSimpleName(), "isConnected() = true ");
            }
        }


        return Service.START_REDELIVER_INTENT;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class CIMPushInnerService extends Service {

        @Override
        public void onCreate() {
//            Log.i(TAG, "InnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
//            Log.i(TAG, "InnerService -> onStartCommand");
            startForeground(GRAY_SERVICE_ID, new Notification());
            //stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            // TODO: Return the communication channel to the service.
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
//            Log.i(TAG, "InnerService -> onDestroy");
            super.onDestroy();
        }
    }
}
