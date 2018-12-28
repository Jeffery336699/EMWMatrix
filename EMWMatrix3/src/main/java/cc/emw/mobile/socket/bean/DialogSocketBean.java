package cc.emw.mobile.socket.bean;

import cc.emw.mobile.socket.notification.DialogSocket;

/**
 * @author fmc
 * @package cc.emw.mobile.socket.bean
 * @data on 2018/5/24  13:38
 * @describe TODO
 */

public class DialogSocketBean {
    private String Msg;
    private Object object;
    private String reStr;

    public DialogSocketBean(String msg){
        this.Msg = msg;
    }
    public DialogSocketBean(){

    }
    public void setMsg(String msg) {
        Msg = msg;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setReStr(String reStr) {
        this.reStr = reStr;
    }

    public Object getObject() {
        return object;
    }

    public String getReStr() {
        return reStr;
    }

    public String getMsg() {
        return Msg;
    }
}
