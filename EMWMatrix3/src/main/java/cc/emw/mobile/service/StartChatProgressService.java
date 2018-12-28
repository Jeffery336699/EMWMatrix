package cc.emw.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.farsunset.cim.client.model.Message;

import java.util.List;

import cc.emw.mobile.chat.model.IChatMsgDBModel;

/**
 * Created by sunny.du on 2017/5/18.
 * 计划后续用于处理数据    暂时保留  在main界面开启服务 用于在首页初始化的时候拉活chat模块进程
 */

public class StartChatProgressService extends Service {
    private IChatMsgDBModel mChatMsgDao;
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mChatMsgDao = new ChatMsgDBModelImpl();
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void addMessage2DB(List<Message> list){
        for (Message bean:list) {
            mChatMsgDao.addMsgToDB(bean,0, false);
        }
    }
}
