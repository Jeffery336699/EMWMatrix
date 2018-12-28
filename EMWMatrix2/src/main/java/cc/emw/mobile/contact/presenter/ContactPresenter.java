package cc.emw.mobile.contact.presenter;

import java.util.List;

import cc.emw.mobile.contact.model.ContactModel;
import cc.emw.mobile.contact.model.ContactModelImple;
import cc.emw.mobile.contact.model.OnContactListener;
import cc.emw.mobile.contact.view.ContactView;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;

public class ContactPresenter implements OnContactListener {

	private ContactModel contactModel;
	private ContactView contactView;

	public ContactPresenter(ContactView contactView) {
		this.contactView = contactView;
		contactModel = new ContactModelImple();
	}

	public void getPersonList(String keyword) {
		contactModel.getPersonList(keyword, this);
	}

	public void addFollow(UserInfo sUser) {
		contactModel.addFollow(sUser, this);
	}

	public void delFollow(UserInfo sUser) {
		contactModel.delFollow(sUser, this);
	}

	public void getGroupList() {
		contactModel.getGroupList(this);
	}

	@Override
	public void onSuccess(List<UserInfo> respList) {
		contactView.showTipDialog("onSuccess");
		contactView.showUserInfo(respList);
		contactView.disProgressDialog();
		contactView.refreshComplete();
	}

	@Override
	public void onFailure() {
		contactView.showTipDialog("onfailure");
		contactView.disProgressDialog();
	}

	@Override
	public void onStart() {
		contactView.showProgressDialog();
	}

	@Override
	public void onSuccess(String result) {
		contactView.showTipDialog("onSuccess");
		contactView.disProgressDialog();
		contactView.showFollowResult(result);
	}

	@Override
	public void onGroupSuccess(List<GroupInfo> groupInfos) {
		contactView.refreshComplete();
		contactView.showGroupInfo(groupInfos);
	}

}
