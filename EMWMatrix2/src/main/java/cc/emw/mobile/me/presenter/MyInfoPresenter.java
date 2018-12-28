package cc.emw.mobile.me.presenter;

import java.util.List;

import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.me.model.MyInfoModelImple;
import cc.emw.mobile.me.view.MyInfoView;

/**
 * @author zrjt
 * @version 2016-3-12 下午4:51:27
 */
public class MyInfoPresenter implements MyInfoListener {

	private MyInfoView myInfoView;
	private MyInfoModelImple modelImple;

	public MyInfoPresenter(MyInfoView myInfoView) {
		this.myInfoView = myInfoView;
		modelImple = new MyInfoModelImple();
	}

	// 获取我的信息各类数量
	public void getCount() {
		modelImple.getMyInfoConut(this);
	}

	public void getFollow() {
		modelImple.getFollowList(this);
	}

	/**
	 * 我发的信息
	 * 
	 * @param type
	 * @param uid
	 */
	public void getMyReleaseInfo(int type, int id, int pages, int size) {
		modelImple.getMyReleaseInfo(type, id, pages, size, this);
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onSuccess(List<UserInfo> simpleUsers) {
		myInfoView.showFollowList(simpleUsers);
	}

	@Override
	public void onFailure(String tips) {
		myInfoView.showFailureInfo(tips);
	}

	@Override
	public void onFinish() {
		myInfoView.finishRefresh();
	}

	@Override
	public void onMyInfoCountSuccess(List<Integer> str) {
		myInfoView.getMyInfoCount(str);
	}

	@Override
	public void onMyReleaseInfoSuccess(List<UserNote> userNotes) {
		myInfoView.getMyReleaseInfoList(userNotes);
	}

}
