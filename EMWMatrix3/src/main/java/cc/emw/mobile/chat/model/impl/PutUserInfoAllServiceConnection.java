package cc.emw.mobile.chat.model.impl;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.IPersonManager;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.util.PrefsUtil;

/**
 * Created by sunny.du on 2017/6/3.
 * 初始化Chat专用进程的数据
 */
public class PutUserInfoAllServiceConnection implements ServiceConnection {
    private  static final PutUserInfoAllServiceConnection mConnection=new PutUserInfoAllServiceConnection();
    private static IPersonManager manager ;
    public static final IPersonManager getManager(){
                return manager;
    }
    public static final PutUserInfoAllServiceConnection getConnection(){
        return mConnection;
    }
    private  PutUserInfoAllServiceConnection(){
    }
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        manager = IPersonManager.Stub.asInterface(service);
        new Thread() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<UserInfo>>() {
                    }.getType();
                    List<UserInfo> userList = gson.fromJson(manager.getPersonStr(), type);
                    for (UserInfo user : userList) {
                        EMWApplication.personMap.put(user.ID, user);
                    }

                    Type type2 = new TypeToken<List<GroupInfo>>() {
                    }.getType();
                    List<GroupInfo> groupList = gson.fromJson(manager.getGroupStr(), type2);
                    for (GroupInfo group : groupList) {
                        EMWApplication.groupMap.put(group.ID, group);
                    }

                    PrefsUtil.saveUserInfo(gson.fromJson(manager.getUserStr(), UserInfo.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
