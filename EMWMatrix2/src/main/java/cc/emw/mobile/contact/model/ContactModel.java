package cc.emw.mobile.contact.model;

import cc.emw.mobile.entity.UserInfo;

/**
 * 
 * @author shaobo.zhuang
 * 
 */
public interface ContactModel {

	void getPersonList(String keyword, OnContactListener listener);

	void getFollowList(OnContactListener listener);

	void doFollow(final UserInfo mUser, String tips,
			OnContactListener listener);

	void addFollow(final UserInfo mUser, OnContactListener listener);

	void delFollow(final UserInfo mUser, OnContactListener listener);
	
	void getGroupList(OnContactListener listener);
}
