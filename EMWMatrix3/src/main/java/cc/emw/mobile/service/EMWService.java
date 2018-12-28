package cc.emw.mobile.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class EMWService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}
	public class MyBinder extends Binder {
		public EMWService getService() {
			return EMWService.this;
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		/*MyNotifiUtil.Instance(getApplicationContext());
		IntentFilter filter=new IntentFilter();
		filter.addAction(BroadCastConst.ZKBR_Service_Stop);
		registerReceiver(receiver, filter);
		
		SystemMsgUtil.startListener();*/
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		
//		SystemMsgUtil.stopListener();
		super.onDestroy();
	}
	
	
	/**
	 * 接受Activity发的广播
	 */
	private BroadcastReceiver receiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			/*if(BroadCastConst.ZKBR_Service_Stop.equals(intent.getAction())){
				MyNotifiUtil.clear();
				stopSelf();
			}*/
		}
	}; 
}
