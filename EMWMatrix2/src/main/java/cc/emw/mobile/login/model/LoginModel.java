package cc.emw.mobile.login.model;

/**
 * 
 * @author shaobo.zhuang
 *
 */
public interface LoginModel {
	
	void doCheckVersion(int curVersion, OnLoginListener listener);
	void doLogin(String username, String password, String comcode, OnLoginListener listener);
	void getPersonList(OnLoginListener listener);
}
