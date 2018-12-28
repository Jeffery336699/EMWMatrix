package cc.emw.mobile.service;

import java.io.File;

import org.apache.http.HttpStatus;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import cc.emw.mobile.R;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.ToastUtil;
import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.SimpleDListener;

/**
 * 执行下载的Service
 *
 * @author AigeStudio 2015-05-18
 */
public class DLService extends Service {

	private Handler mHandler;
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	if (mHandler == null) {
    		mHandler = new Handler() {
    			@Override
    			public void handleMessage(Message msg) {
    				ToastUtil.showToast(getApplicationContext(), "您要下载的资源可能已被删除！",-1);
    			}
    		};
    	}
    	if (intent != null) {
	        String url = intent.getStringExtra("url");
	        String path = intent.getStringExtra("path");
	        String name = intent.getStringExtra("name");
	        final int id = intent.getIntExtra("id", -1);
	        final NotificationManager nm = (NotificationManager) getSystemService(Context
	                .NOTIFICATION_SERVICE);
	        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
	                .setSmallIcon(FileUtil.getResIconId("".equals(name)?url:name));
	        DLManager.getInstance(this).dlStart(url, path, name, new SimpleDListener() {
	            @Override
	            public void onStart(String fileName, String realUrl, int fileLength) {
	                super.onStart(fileName, realUrl, fileLength);
	                builder.setContentTitle(fileName);
	            }
	
	            @Override
	            public void onProgress(int progress) {
	                builder.setProgress(100, progress, false);
	                nm.notify(id, builder.build());
	            }
	
	            @Override
	            public void onFinish(File file) {
	//                nm.cancel(id);
	            	Intent intent = new Intent(Intent.ACTION_VIEW); // 使用Intent打开文件
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					String type = FileUtil.getMIMEType(file); // 获取文件file的MIME类型
					intent.setDataAndType(Uri.fromFile(file), type);                
	            	PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);   //使用PendingIntent
	            	builder.setContentIntent(pendingIntent);
	            	builder.setAutoCancel(true).setProgress(0, 0, false).setContentText("下载完成，点击打开");
	            	nm.notify(id, builder.build());
	            }
	            
	            @Override
	            public void onError(int status, String error) {
	            	if (status == HttpStatus.SC_NOT_FOUND || status == HttpStatus.SC_SWITCHING_PROTOCOLS) {
	                	if (mHandler != null) 
	                		mHandler.sendEmptyMessage(1);
	            	}
	            }
	        });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
