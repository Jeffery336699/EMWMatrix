package cc.emw.mobile.chat.model;

import com.farsunset.cim.client.model.Message;

import java.util.List;

import cc.emw.mobile.chat.model.bean.ChatMsgBean;


/**
 * Created by sunny.du on 2017/5/5.
 */

public interface IChatMsgDBModel {
    /**********************************************
     * 封装：构造消息实体并且插入数据库
     */
    ChatMsgBean addMsgToDB(Message msg, int isSendFlag, boolean isAddListFalg);

    /*******************************************************************************************
     * 个人聊天
     * 查询全部数据
     */
    List<ChatMsgBean> queryUserAll(int mainUserID, int ReceiverID, int index);

    /**
     * 计算分页
     */
    List<ChatMsgBean> queryUserAll(int mainUserID, int ReceiverID);

    /**
     * 群体聊天聊天
     * 查询全部数据
     */
    List<ChatMsgBean> queryGroupAll(int mainUserID, int GroupID, int index);

    /**
     * 计算分页
     */
    List<ChatMsgBean> queryGroupAll(int mainUserID, int GroupID);

    /**
     * 更新数据
     */
    void updateMsgItem(ChatMsgBean msgBean);

    /**
     * 删除数据   无业务引用 暂时保留
     */
    void deleteMsgItem(ChatMsgBean chatMsgBean);

    /**********************************************
     * 用于记录新添加的消息列表  用于刷新消息新增加的数据信息  防止信息重复加载
     */
    List<ChatMsgBean> getAddTrueMsgList();

    /**
     * 设置清空维护数据库内存缓存的列表
     * @param list   空列表
     */
    void setAddTrueMsgList(List<ChatMsgBean> list);

}
