// UserInfoManager.aidl
package cc.emw.mobile.contact.bean;
import cc.emw.mobile.contact.bean.UserInfo;
// Declare any non-default types here with import statements

interface UserInfoManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     List<UserInfo> getUserInfos();

     Map getUserMap();

     void putUserInfo(int userId,in UserInfo userInfo);

     void addUserInfo(in UserInfo userInfo);
}
