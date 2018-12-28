package cc.emw.mobile.entity;

import cc.emw.mobile.net.ApiEntity;

/**
 * 
 * @author shaobo.zhuang
 *
 */
public class UserInfo extends ApiEntity.UserInfo {
	
	public boolean isTag;
	
	private String sortLetters; //名称的首字母
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}