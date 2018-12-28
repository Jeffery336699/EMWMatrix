package cc.emw.mobile.socket;

import android.os.Bundle;

import java.net.URISyntaxException;

import cc.emw.mobile.LogLongUtil;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.Const;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * @author fmc
 * @package cc.emw.mobile.socket
 * @data on 2018/5/11  8:57
 * @describe 测试 Socket 通讯 界面
 */

public class IOSocketTestActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_setting);
        try {
            LogLongUtil.e("IOSocketTestActivity"," --------onCreate-----------"+mSocket.connected());
            mSocket.on("msg",onNewMessage);
            mSocket.connect();
            LogLongUtil.e("IOSocketTestActivity"," -------mSocket connect----------"+mSocket.connected());

            //mSocket.emit("message","fmc");
        }catch (Exception e){
            LogLongUtil.e("IOSocketTestActivity"," Exception ="+e.getMessage());
        }

    }

    private Socket mSocket;
    {
        try{
            LogLongUtil.e("IOSocketTestActivity"," -------mSocket init----------");
            mSocket = IO.socket(Const.SOCKET_URL);
        }catch (URISyntaxException e){
            LogLongUtil.e("IOSocketTestActivity"," mSocket ="+e.getMessage());
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            LogLongUtil.e("IOSocketTestActivity","----data call receive  ------");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogLongUtil.e("IOSocketTestActivity"," --------onDestroy-----------");
        mSocket.disconnect();
        mSocket.off("msg", onNewMessage);
    }

}
