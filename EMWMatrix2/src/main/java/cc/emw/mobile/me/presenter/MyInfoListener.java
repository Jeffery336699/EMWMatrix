package cc.emw.mobile.me.presenter;

import java.util.List;

import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;

/**
 * @author zrjt
 * @version 2016-3-12 下午4:51:44
 */
public interface MyInfoListener {

	void onStart();

	void onFailure(String tips);
	
	// 取消加载框的通知
	void onFinish();
	
	// 我关注的人员信息
	void onSuccess(List<UserInfo> simpleUsers);
	
	// 我发布、收藏、关注、相关到我的数量
	void onMyInfoCountSuccess(List<Integer> counts);

	// 获取所有UserNote的信息
	void onMyReleaseInfoSuccess(List<UserNote> userNotes);

}
