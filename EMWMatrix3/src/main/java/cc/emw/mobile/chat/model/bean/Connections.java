package cc.emw.mobile.chat.model.bean;

public class Connections {
	private int ClientCount;
	private boolean Connected;
	private String HostName;
	private int ID;
	private int MaxConnectedCount;
	private int Port;
	private String Protol; //ws ; wss

	public String getProtol() {
		return Protol;
	}

	public int getClientCount() {
		return ClientCount;
	}

	public void setClientCount(int clientCount) {
		ClientCount = clientCount;
	}

	public boolean isConnected() {
		return Connected;
	}

	public void setConnected(boolean connected) {
		Connected = connected;
	}

	public String getHostName() {
		return HostName;
	}

	public void setHostName(String hostName) {
		HostName = hostName;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getMaxConnectedCount() {
		return MaxConnectedCount;
	}

	public void setMaxConnectedCount(int maxConnectedCount) {
		MaxConnectedCount = maxConnectedCount;
	}

	public int getPort() {
		return Port;
	}

	public void setPort(int port) {
		Port = port;
	}

}
