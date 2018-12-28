package cc.emw.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.google.gson.Gson;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.IPersonManager;
import cc.emw.mobile.util.PrefsUtil;


public class PersonManagerService extends Service {
    private String personStr, groupStr, userStr;
    private Binder binder = new IPersonManager.Stub() {

        @Override
        public String getPersonStr() throws RemoteException {
            return personStr;
        }

        @Override
        public String getGroupStr() throws RemoteException {
            return groupStr;
        }

        @Override
        public String getUserStr() throws RemoteException {
            return userStr;
        }

        @Override
        public void setCurChatID(int chatID) throws RemoteException {
            EMWApplication.currentChatUid = chatID;
        }
    };
    /*private ConcurrentHashMap<Integer, UserInfo> personMap = new ConcurrentHashMap<>();

    private Binder binder = new IPersonManager.Stub() {
        @Override
        public void putPerson(UserInfo user) throws RemoteException {
            personMap.put(user.ID, user);
        }

        @Override
        public Map getPersonMap() throws RemoteException {
            return personMap;
        }
    };*/

    @Override
    public void onCreate() {
        super.onCreate();
        personStr = EMWApplication.personAIDLStr;
        groupStr = EMWApplication.groupAIDLStr;
        userStr = new Gson().toJson(PrefsUtil.readUserInfo()); //解决多进程切换账号，进聊天还是以之前用户信息显示
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
