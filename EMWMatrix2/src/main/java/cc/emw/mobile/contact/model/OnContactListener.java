package cc.emw.mobile.contact.model;

import java.util.List;

import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;

public interface OnContactListener {

	void onStart();

	void onSuccess(List<UserInfo> respList);

	void onFailure();

	void onSuccess(String result);

	void onGroupSuccess(List<GroupInfo> groupInfos);
}
