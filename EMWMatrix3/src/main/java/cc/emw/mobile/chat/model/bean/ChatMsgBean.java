package cc.emw.mobile.chat.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by sunny.du on 2017/3/3.
 * GreenDao实体类    自动生成数据库操作类和方法
 * 首次运行代码时需要build模块，greendao脚本会自动生成库代码
 */
@Entity
public class ChatMsgBean {
    /**
     * 数据库主键  自增长   按照协议 必须是long类型
     */
    @Id(autoincrement = true)
    private Long dbID;
    /**
     * 消息ID
     */
    private int ID;
    /**
     * 消息内容
     */
    private String Content;
    /**
     * 用户ID
     */
    private int UserID;
    /**
     * 发送者ID
     */
    private int SenderID;
    /**
     * 接收者ID
     */
    private int ReceiverID;
    /**
     * 消息类型
     */
    private int Type; //MessageType
    /**
     * 企业代码  emw
     */
    private String CompanyCode;
    /**
     * 消息发送时间
     */
    private String CreateTime;
    /**
     * 消息发送时间long值 用于判断消息顺序
     */
    private Long CreateTimeLong;
    /**
     * 群组ID
     */
    private int GroupID;
    /**
     * 判断消息是否有发送成功    0为成功  1为 失败
     * 用于控制页面消息状态展示
     */
    private int isSendFlag;
    /**
     * 判断群组消息是否是被标注过的重要消息   当等于1的时候则为重要消息，否则是普通消息
     */
    @Transient
    private int state;
    /**
     * 判断消息状态，0为正常，1为重要消息，2为收藏消息
     */
    private int Options;


    @Generated(hash = 257522003)
    public ChatMsgBean(Long dbID, int ID, String Content, int UserID, int SenderID, int ReceiverID, int Type, String CompanyCode, String CreateTime, Long CreateTimeLong, int GroupID,
            int isSendFlag, int Options) {
        this.dbID = dbID;
        this.ID = ID;
        this.Content = Content;
        this.UserID = UserID;
        this.SenderID = SenderID;
        this.ReceiverID = ReceiverID;
        this.Type = Type;
        this.CompanyCode = CompanyCode;
        this.CreateTime = CreateTime;
        this.CreateTimeLong = CreateTimeLong;
        this.GroupID = GroupID;
        this.isSendFlag = isSendFlag;
        this.Options = Options;
    }

    @Generated(hash = 251455779)
    public ChatMsgBean() {
    }


    @Override
    public String toString() {
        return "{ID+" + ID + "},{Content=" + Content + "},{Type=" + Type + "},{CreateTime=" + CreateTime + "},{CreateTimeLong=" + CreateTimeLong + "},{isSendFlag" + isSendFlag + "}.";
    }

    public Long getDbID() {
        return this.dbID;
    }

    public void setDbID(Long dbID) {
        this.dbID = dbID;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getContent() {
        return this.Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public int getUserID() {
        return this.UserID;
    }

    public void setUserID(int UserID) {
        this.UserID = UserID;
    }

    public int getSenderID() {
        return this.SenderID;
    }

    public void setSenderID(int SenderID) {
        this.SenderID = SenderID;
    }

    public int getReceiverID() {
        return this.ReceiverID;
    }

    public void setReceiverID(int ReceiverID) {
        this.ReceiverID = ReceiverID;
    }

    public int getType() {
        return this.Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public String getCompanyCode() {
        return this.CompanyCode;
    }

    public void setCompanyCode(String CompanyCode) {
        this.CompanyCode = CompanyCode;
    }

    public String getCreateTime() {
        return this.CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public Long getCreateTimeLong() {
        return this.CreateTimeLong;
    }

    public void setCreateTimeLong(Long CreateTimeLong) {
        this.CreateTimeLong = CreateTimeLong;
    }

    public int getGroupID() {
        return this.GroupID;
    }

    public void setGroupID(int GroupID) {
        this.GroupID = GroupID;
    }

    public int getIsSendFlag() {
        return isSendFlag;
    }

    public void setIsSendFlag(int isSendFlag) {
        this.isSendFlag = isSendFlag;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getOptions() {
        return Options;
    }

    public void setOptions(int options) {
        Options = options;
    }
}
