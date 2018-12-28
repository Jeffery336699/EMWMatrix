package cc.emw.mobile.me.model;

import cc.emw.mobile.me.presenter.MyInfoListener;

/**
 * @author zrjt
 * @version 2016-3-12 下午4:40:41
 */
public interface MyInfoModel {

	void showCollect();

	void showConcernInfo(String keyword, MyInfoListener listener);

	void getFollowList(MyInfoListener listener);

	void getMyInfoConut(MyInfoListener listener); // 获取我发布，我收藏、相关到我的数量

	void getMyReleaseInfo(int type, int id, int pages, int size,
			MyInfoListener listener); // 获取所有UserNote的信息
}
