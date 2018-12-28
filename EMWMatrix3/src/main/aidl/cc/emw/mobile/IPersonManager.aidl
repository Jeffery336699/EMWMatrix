// IPersonManager.aidl
package cc.emw.mobile;

//import cc.emw.mobile.UserInfo;

interface IPersonManager {

//    void putPerson(in UserInfo user);
//    Map getPersonMap();
      String getPersonStr();
      String getGroupStr();
      String getUserStr();
      void setCurChatID(int chatID);
}
