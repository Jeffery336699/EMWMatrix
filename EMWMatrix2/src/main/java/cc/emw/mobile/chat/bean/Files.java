package cc.emw.mobile.chat.bean;

public class Files {

	private String Url;// 文件路径
	public String Name;// 文件名
	public long Length;// 文件大小
	private int ID;// 文件ID

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public long getLength() {
		return Length;
	}

	public void setLength(long length) {
		Length = length;
	}

}
