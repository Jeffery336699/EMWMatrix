package cc.emw.mobile.chat.model.bean;

/**
 * Created by sunny.du on 2017/2/8.
 * 机器人返回消息实体
 */
public class ChatAIBean {
    /*****
     * 普通消息
     */
    int code;//返回标记码
    String text;//文本信息
    String url;//URL信息

    /******
     * 日程消息
     */

    public  ChatAIBean(){

    }
    public  ChatAIBean(int code,String text, String url){
        this.code=code;
        this.text=text;
        this.url=url;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }
}
