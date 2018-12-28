/**
 * probject:cim-android-sdk
 *
 * @version 2.0.0
 * @author 3979434@qq.com
 */
package com.farsunset.cim.client.model;

import java.io.Serializable;

/**
 * 消息对象
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private String CreateTime;
    private String CompanyCode;
    private String Content;
    private int ID;
    private int ReceiverID;
    private int SenderID;
    private int Type;
    private int UserID;
    private int GroupID;
    private int BusType; //BusTypes
    private int IsNew;
    private boolean isShow = false;
    private boolean isSend;
    public int state;
    public int Options;

    public Message() {
    }

    public Message(int iD, String content, int userID, int senderID,
                   int receiverID, int type, String companyCode, String createTime,int state,int Options) {
        ID = iD;
        Content = content;
        UserID = userID;
        SenderID = senderID;
        ReceiverID = receiverID;
        Type = type;
        CompanyCode = companyCode;
        CreateTime = createTime;
        this.state=state;
        this.Options=Options;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public int getIsNew() {
        return IsNew;
    }

    public void setIsNew(int isNew) {
        IsNew = isNew;
    }

    public String getCompanyCode() {
        return CompanyCode;
    }

    public void setCompanyCode(String companyCode) {
        CompanyCode = companyCode;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public int getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(int receiverID) {
        ReceiverID = receiverID;
    }

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }

    public int getBusType() {
        return BusType;
    }

    public void setBusType(int busType) {
        BusType = busType;
    }

    public int getSenderID() {
        return SenderID;
    }

    public void setSenderID(int senderID) {
        SenderID = senderID;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
   public boolean isSend(){return isSend;}

    public void setSend(boolean send) {
        isSend = send;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getOptions() {
        return Options;
    }

    public void setOptions(int Options) {
        this.Options = Options;
    }
}
