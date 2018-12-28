package cc.emw.mobile.contact.view;

import java.util.List;

import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;

public interface ContactView {
	// 取消进度条
	void disProgressDialog();

	// 刷新完成
	void refreshComplete();

	// 显示进度条
	void showProgressDialog();

	// 显示跟随列表
	void showFollowResult(String result);

	// 提示对话框
	void showTipDialog(String tips);

	// 显示用户信息
	void showUserInfo(List<UserInfo> simpleUsers);

	// 显示群组列表
	void showGroupInfo(List<GroupInfo> groupInfos);
}
