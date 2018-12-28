package cc.emw.mobile.login.model;

import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.Version;

public interface OnLoginListener {
	
	void onVersionSuccess(Version ver);
	void onPersonSuccess();
	void onSuccess(LoginResp respInfo);
	void onNameOrPwdError();
	void onFailure();
}
