package cc.emw.mobile.login.model;

import io.socket.client.Socket;

/**
 * 
 * @author shaobo.zhuang
 *
 */
public interface LoginModel {
	
	void doCheckVersion(int curVersion, OnLoginListener listener);
	void doLogin(Socket mSocket, String username, String password, String comcode, OnLoginListener listener);
	void cancelLogin();
	void getPersonList(OnLoginListener listener);
}
