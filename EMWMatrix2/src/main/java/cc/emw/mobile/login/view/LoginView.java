package cc.emw.mobile.login.view;

import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.Version;

public interface LoginView {
	void saveLoginInfo(LoginResp respInfo);
	void navigateToIndex();
	void showTipDialog(String tip);
	void showVersionDialog(Version ver);
}
