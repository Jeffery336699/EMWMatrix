package cc.emw.mobile.chat.bean;

public class HistoryMessage {
	private SendMessage Message;

	private int ReceiverID;
	private int Type;
	private int UserID;

	public SendMessage getMessage() {
		return Message;
	}

	public void setMessage(SendMessage message) {
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
