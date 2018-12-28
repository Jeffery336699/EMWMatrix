package cc.emw.mobile.me.view;

import java.util.List;

import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;

/**
 * @author zrjt
 * @version 2016-3-12 下午4:42:53
 */
public interface MyInfoView {

	void showFailureInfo(String tips);

	void showFollowList(List<UserInfo> simpleUsers);//获取我关注的人员列表s

	void finishRefresh();//刷新完成
	
	void getMyInfoCount(List<Integer> counts);//获取我的信息各类数量
	
	void getMyReleaseInfoList(List<UserNote> userNotes); //获取所有UserNote的信息
}
