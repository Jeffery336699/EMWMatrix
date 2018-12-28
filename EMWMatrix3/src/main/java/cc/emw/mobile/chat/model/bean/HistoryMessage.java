package cc.emw.mobile.chat.model.bean;

import cc.emw.mobile.entity.Receiver;

public class HistoryMessage {
    private MessageInfo Message;
    private int ReceiverID;
    private int Type;
    private int UserID;
    public Receiver Receiver;
    private int UnReadCount;

    public int getUnReadCount() {
        return UnReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        UnReadCount = unReadCount;
    }

    public MessageInfo getMessage() {
        return Message;
    }

    public void setMessage(MessageInfo message) {
        Message = message;
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

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

}
