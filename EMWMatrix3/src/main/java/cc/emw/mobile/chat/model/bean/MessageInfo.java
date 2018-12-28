package cc.emw.mobile.chat.model.bean;

import cc.emw.mobile.net.ApiEntity;

public class MessageInfo extends ApiEntity.Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	public int getGroupID() {
		return GroupID;
	}

	public void setGroupID(int groupID) {
		GroupID = groupID;
	}

	public MessageInfo() {
	}

	public MessageInfo(int iD, String content, int userID, int senderID,
                       int receiverID, int type, String companyCode, String createTime,
                       int GroupID) {
		ID = iD;
		Content = content;
		UserID = userID;
		SenderID = senderID;
		ReceiverID = receiverID;
		Type = type;
		CompanyCode = companyCode;
		CreateTime = createTime;
		this.GroupID = GroupID;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
	}

	public int getSenderID() {
		return SenderID;
	}

	public void setSenderID(int senderID) {
		SenderID = senderID;
	}

	public int getReceiverID() {
		return ReceiverID;
	}

	public void setReceiverID(int receiverID) {
		ReceiverID = receiverID;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public String getCompanyCode() {
		return CompanyCode;
	}

	public void setCompanyCode(String companyCode) {
		CompanyCode = companyCode;
	}

	public String getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}

}
