package cc.emw.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.contact.bean.UserInfo;
import cc.emw.mobile.contact.bean.UserInfoManager;
import cc.emw.mobile.util.PrefsUtil;

/**
 * Created by tao.zhou on 2017/5/11.
 */

public class AIDLService extends Service {

    private List<UserInfo> mUserInfos = new ArrayList<>();

    private Map<Integer,UserInfo> personMap2 = new HashMap();

    private final UserInfoManager.Stub mUserInfoManager = new UserInfoManager.Stub() {
        @Override
        public List<UserInfo> getUserInfos() throws RemoteException {
            synchronized (this) {
                if (mUserInfos != null) {
                    return mUserInfos;
                }
                return new ArrayList<>();
            }
        }

        @Override
        public void addUserInfo(UserInfo userInfo) throws RemoteException {

        }

        @Override
        public void putUserInfo(int userId, UserInfo userInfo) throws RemoteException {

        }

        @Override
        public Map getUserMap() throws RemoteException {
            synchronized (this) {
                if (personMap2 != null) {
                    return personMap2;
                }
                return new HashMap();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mUserInfoManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        cc.emw.mobile.entity.UserInfo userInfos = EMWApplication.personMap.get(PrefsUtil.readUserInfo().ID);
//        userInfo.Name = userInfos.Name;
//        mUserInfos.add(userInfo);
        int key = 0;
        for(int i = 0; i < EMWApplication.personMap.size(); i++) {
            key = EMWApplication.personMap.keyAt(i);
            // get the object by the key.
            cc.emw.mobile.entity.UserInfo userInfos = EMWApplication.personMap.get(key);
            UserInfo userInfo = new UserInfo();
            userInfo.Name = userInfos.Name;
            userInfo.ID = userInfos.ID;
            personMap2.put(key,userInfo);
        }
    }

}
