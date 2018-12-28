package cc.emw.mobile.socket.bean;

/**
 * @author fmc
 * @package cc.emw.mobile.socket
 * @data on 2018/5/24  10:13
 * @describe TODO
 */

public class NotificationChannelBean {
    private String channelId;
    private String channelName;
    private String ContentText = "";
    private String ContentTitle = "";
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setContentText(String contentText) {
        ContentText = contentText;
    }

    public void setContentTitle(String contentTitle) {
        ContentTitle = contentTitle;
    }

    public String getContentTitle() {
        return ContentTitle;
    }

    public String getContentText() {
        return ContentText;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }
}
