package cc.emw.mobile.socket.action;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.githang.androidcrash.util.AppManager;
import com.zf.iosdialog.widget.AlertDialog;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.socket.presenter.SocketListenerImp;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * @author fmc
 * @package cc.emw.mobile.socket.action
 * @data on 2018/5/21  18:54
 * @describe TODO
 */

public class BackSocketService extends Service implements SocketServiceInter {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private SocketListenerImp socketListenerImp;
    private Socket socket;

    private final static String TAG = "BackSocketService";
   private Handler myHandle = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           showHostOnlineAlert();
       }
   };
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"saveLoginInfo----------startService");
        EMWApplication application = (EMWApplication) this.getApplication();
        socket = application.getAppIOSocket();
        if (!socket.connected()){
            socket.connect();
        }
        socket.on(EMWApplication.SOCKET_EVENT_MSG,listener);
    }


    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            myHandle.sendEmptyMessage(0);
        }
    };
    private void showHostOnlineAlert(){
        Activity activity = AppManager.currentActivity();
        if((activity.getClass()).equals(ChatActivity.class)){
            return;
        }

        AlertDialog dialog = new AlertDialog(activity).builder();
        dialog.setTitle("日程提醒");
        dialog.setMsg("日志消息");
        dialog.setNegativeButton(getString(R.string.look_details), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog.setPositiveButton(getString(R.string.i_know), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).setCancelable(false).show();
    }

    @Override
    public void doActionService() {

    }

    @Override
    public void doDialogShow() {

    }

    @Override
    public void onDestroy() {
        if(socket != null){
            socket.on(EMWApplication.SOCKET_EVENT_MSG,listener);
        }
        super.onDestroy();
    }
}
