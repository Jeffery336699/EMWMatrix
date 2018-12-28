package cc.emw.mobile.project.bean;

/**
 * @author fmc
 * @package cc.emw.mobile.project.bean
 * @data on 2018/5/11  15:34
 * @describe 发送socket 信息发送给服务器
 */

public class JoinDataBean {

    private int SenderID;//me
    private int ReceiverID;//other
    private int GroupID;//group
    public JoinDataBean(int SenderID,int ReceiverID,int GroupID){
        this.SenderID = SenderID;
        this.ReceiverID = ReceiverID;
        this.GroupID = GroupID;
    }
}
