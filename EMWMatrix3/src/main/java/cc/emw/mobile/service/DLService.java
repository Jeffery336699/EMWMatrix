package cc.emw.mobile.service;

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
import android.util.Log;

import org.apache.http.HttpStatus;

import java.io.File;

import cc.emw.mobile.file.FileDetailActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
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
					switch (msg.what) {
						case 1:
							ToastUtil.showToast(getApplicationContext(), "您要下载的资源可能已被删除！",-1);
							break;
						case 2:
							API.UserData.SaveDownLoadRecord(msg.arg1, new RequestCallback<String>() {
								@Override
								public void onSuccess(String result) {
									sendBroadcast(new Intent(FileDetailActivity.ACTION_SAVE_RECORD_SUCCESS));
								}
								@Override
								public void onError(Throwable throwable, boolean b) {}
							});
							break;
					}
    			}
    		};
    	}
    	if (intent != null) {
	        final String url = intent.getStringExtra("url");
	        String path = intent.getStringExtra("path");
	        String name = intent.getStringExtra("name");
			final int fid = intent.getIntExtra("fid", 0);
			final boolean isShow = intent.getBooleanExtra("is_show", true);
	        final int id = intent.getIntExtra("id", -1);
	        final NotificationManager nm = (NotificationManager) getSystemService(Context
	                .NOTIFICATION_SERVICE);
	        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
	                .setSmallIcon(FileUtil.getResIconId("".equals(name)?url:name));
			final int[] length = new int[1];
	        DLManager.getInstance(this).dlStart(url, path, name, new SimpleDListener() {
	            @Override
	            public void onStart(String fileName, String realUrl, int fileLength) {
	                super.onStart(fileName, realUrl, fileLength);
					if (isShow) {
						builder.setContentTitle(fileName);
					}
					length[0] = fileLength;
	            }
	
	            @Override
	            public void onProgress(int progress) {
					if (isShow) {
						builder.setProgress(length[0], progress, false);
						nm.notify(id, builder.build());
					}
					Intent downIntent = new Intent(FileDetailActivity.ACTION_REFRESH_FILE_DOWN);
					downIntent.putExtra("url", url);
					downIntent.putExtra("progress", progress);
					downIntent.putExtra("length", length[0]);
					downIntent.putExtra("fid", fid);
					sendBroadcast(downIntent);
	            }
	
	            @Override
	            public void onFinish(File file) {
	//                nm.cancel(id);
					if (isShow) {
						Intent intent = new Intent(Intent.ACTION_VIEW); // 使用Intent打开文件
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						String type = FileUtil.getMIMEType(file); // 获取文件file的MIME类型
						intent.setDataAndType(Uri.fromFile(file), type);
						PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);   //使用PendingIntent
						builder.setContentIntent(pendingIntent);
						builder.setAutoCancel(true).setProgress(0, 0, false).setContentText("下载完成，点击打开");
						nm.notify(id, builder.build());
					}
					if (fid > 0 && mHandler != null) {
						Message msg = Message.obtain();
						msg.what = 2;
						msg.arg1 = fid;
						mHandler.sendMessage(msg);
					}
				}
	            
	            @Override
	            public void onError(int status, String error) {
					Log.d("px","文件下载---error"+error+status);
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
