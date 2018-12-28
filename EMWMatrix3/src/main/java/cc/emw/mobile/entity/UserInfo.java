package cc.emw.mobile.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import cc.emw.mobile.net.ApiEntity;

/**
 * 
 * @author shaobo.zhuang
 *
 */
public class UserInfo extends ApiEntity.UserInfo {
	
	private String sortLetters; //名称的首字母
	public boolean isFromPhone;	//是否为通讯录的好友
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

}