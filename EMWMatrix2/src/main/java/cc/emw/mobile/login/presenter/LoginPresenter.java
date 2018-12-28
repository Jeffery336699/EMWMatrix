package cc.emw.mobile.login.presenter;

import cc.emw.mobile.entity.LoginResp;
import cc.emw.mobile.entity.Version;
import cc.emw.mobile.login.model.LoginModel;
import cc.emw.mobile.login.model.LoginModelImple;
import cc.emw.mobile.login.model.OnLoginListener;
import cc.emw.mobile.login.view.LoginView;

public class LoginPresenter implements OnLoginListener {

	private LoginModel loginModel;
	private LoginView loginView;
	
	public LoginPresenter(LoginView loginView) {
		this.loginView = loginView;
		loginModel = new LoginModelImple();
	}
	
	public void checkVersion(int curVersion) {
		loginModel.doCheckVersion(curVersion, this);
	}
	
	public void login(String username, String password, String comcode) {
		loginModel.doLogin(username, password, comcode, this);
	}
	
	public void getPersonList() {
		loginModel.getPersonList(this);
	}
	
	@Override
	public void onVersionSuccess(Version ver) {
		loginView.showVersionDialog(ver);
	}
	
	@Override
	public void onSuccess(LoginResp respInfo) {
		loginView.saveLoginInfo(respInfo);
	}

	@Override
	public void onNameOrPwdError() {
		loginView.showTipDialog("用户名、密码或企业代码错误!");
	}

	@Override
	public void onFailure() {
		loginView.showTipDialog("请求失败!");
	}

	@Override
	public void onPersonSuccess() {
		loginView.navigateToIndex();
	}

}
