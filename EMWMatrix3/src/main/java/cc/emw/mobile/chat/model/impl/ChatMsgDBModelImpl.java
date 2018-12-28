package cc.emw.mobile.chat.model.impl;


import android.util.Log;

import com.farsunset.cim.client.model.Message;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.chat.model.IChatMsgDBModel;
import cc.emw.mobile.chat.model.bean.ChatMsgBean;
import cc.emw.mobile.chat.model.bean.ChatMsgBeanDao;
import cc.emw.mobile.chat.utils.DateUtil;

/**
 * Created by sunny.du on 2017/3/3.
 * chat消息数据库操作dao
 * 增/删/改/查  本地数据库
 */
public class ChatMsgDBModelImpl implements IChatMsgDBModel {
    private static ChatMsgBeanDao session;
    /**********************************************
     * 用于记录新添加的消息列表  用于刷新消息新增加的数据信息  防止信息重复加载
     */
    private List<ChatMsgBean> mAddTrueMsgList;

    @Override
    public List<ChatMsgBean> getAddTrueMsgList() {
        return mAddTrueMsgList;
    }

    @Override
    public void setAddTrueMsgList(List<ChatMsgBean> list) {
        this.mAddTrueMsgList = list;
    }

    /**********************************************
     * 构造函数
     */
    public ChatMsgDBModelImpl() {
        session = EMWApplication.getDaoInstant().getChatMsgBeanDao();
        mAddTrueMsgList = new ArrayList<>();
    }

    /**********************************************
     * 封装：构造消息实体并且插入数据库
     */
    @Override
    public ChatMsgBean addMsgToDB(Message msg, int isSendFlag, boolean isAddListFalg) {
        ChatMsgBean chatMsgBean = new ChatMsgBean();
        chatMsgBean.setID(msg.getID());
        chatMsgBean.setContent(msg.getContent());
        chatMsgBean.setUserID(msg.getUserID());
        chatMsgBean.setSenderID(msg.getSenderID());
        chatMsgBean.setReceiverID(msg.getReceiverID());
        chatMsgBean.setType(msg.getType());
        chatMsgBean.setCompanyCode(msg.getCompanyCode());
        chatMsgBean.setCreateTime(msg.getCreateTime());
        if (msg.getType() == 36 || msg.getType() == 37) {
            long createTime = DateUtil.getDate("yyyy-MM-dd HH:mm:ss", msg.getCreateTime()).getTime();
            chatMsgBean.setCreateTimeLong(createTime);
        } else {
            chatMsgBean.setCreateTimeLong(DateUtil.getDate("yyyy-MM-dd HH:mm:ss", msg.getCreateTime()).getTime());
        }
        chatMsgBean.setGroupID(msg.getGroupID());
        chatMsgBean.setIsSendFlag(isSendFlag);
        chatMsgBean.setOptions(msg.getOptions());
        this.insertChatMsg(chatMsgBean, isAddListFalg);
        return chatMsgBean;
    }

    /*******************************************************************************************/
    /**
     * 个人聊天
     * 查询全部数据
     * test:103922
     */
    @Override
    public List<ChatMsgBean> queryUserAll(int mainUserID, int ReceiverID, int index) {
        return session.queryBuilder()
                .where(ChatMsgBeanDao.Properties.UserID.eq(mainUserID), ChatMsgBeanDao.Properties.GroupID.eq(0))
                .whereOr(ChatMsgBeanDao.Properties.SenderID.eq(ReceiverID)
                        , ChatMsgBeanDao.Properties.ReceiverID.eq(ReceiverID))
                .orderAsc(ChatMsgBeanDao.Properties.CreateTimeLong)
                .offset(index * 50).limit(50).list();
    }

    //      分页实现前备份
    @Override
    public List<ChatMsgBean> queryUserAll(int mainUserID, int ReceiverID) {
        return session.queryBuilder()
                .where(ChatMsgBeanDao.Properties.UserID.eq(mainUserID), ChatMsgBeanDao.Properties.GroupID.eq(0))
                .whereOr(ChatMsgBeanDao.Properties.SenderID.eq(ReceiverID), ChatMsgBeanDao.Properties.ReceiverID.eq(ReceiverID))
                .orderAsc(ChatMsgBeanDao.Properties.CreateTimeLong).list();
    }

    /**
     * 群体聊天聊天
     * 查询全部数据
     */
    @Override
    public List<ChatMsgBean> queryGroupAll(int mainUserID, int GroupID, int index) {
        Log.e("ChatMsgDBModelImpl","----------queryGroupAll-----------------");
        return session.queryBuilder()
                .where(ChatMsgBeanDao.Properties.UserID.eq(mainUserID), ChatMsgBeanDao.Properties.GroupID.eq(GroupID))
//                .where(ChatMsgBeanDao.Properties.GroupID.eq(GroupID)) //修改
                .orderAsc(ChatMsgBeanDao.Properties.CreateTimeLong)
                .offset(index * 50).limit(50).list();
    }

    //      分页实现前备份
    @Override
    public List<ChatMsgBean> queryGroupAll(int mainUserID, int GroupID) {
        return session.queryBuilder()
                .where(ChatMsgBeanDao.Properties.UserID.eq(mainUserID), ChatMsgBeanDao.Properties.GroupID.eq(GroupID))
//                .where(ChatMsgBeanDao.Properties.GroupID.eq(GroupID)) //后期修改
                .orderAsc(ChatMsgBeanDao.Properties.CreateTimeLong).list();
    }

    /**
     * 更新数据
     */
    @Override
    public void updateMsgItem(ChatMsgBean msgBean) {
        if (msgBean != null) session.update(msgBean);
    }


    /**
     * 删除数据   无业务引用 暂时保留
     */
    @Override
    public void deleteMsgItem(ChatMsgBean chatMsgBean) {
        try {
            List<ChatMsgBean> chatMsgBeanList = session.queryBuilder().where(ChatMsgBeanDao.Properties.ID.eq(chatMsgBean.getID())
                    ,ChatMsgBeanDao.Properties.CreateTimeLong.eq(chatMsgBean.getCreateTimeLong())).list();
            for (ChatMsgBean chatMsgBeans : chatMsgBeanList) {
                if (chatMsgBeans != null) {
                    session.delete(chatMsgBeans);
                }
            }
        } catch (Exception x) {
            Log.d("sunny----->", "=x" + x);
        }
    }

    /**********************************************
     *是否插入重复数据的查询  返回！null则代表数据库有相同数据了 放弃插入操作
     */
    private ChatMsgBean queryMsgsById(long createTime, String Content) {
        return session.queryBuilder().where(ChatMsgBeanDao.Properties.CreateTimeLong.eq(createTime), ChatMsgBeanDao.Properties.Content.eq(Content)).unique();
    }

    /**********************************************
     * 插入数据
     */
    private void insertChatMsg(ChatMsgBean msgBean, boolean isAddListFalg) {
        if (msgBean != null) {
            ChatMsgBean msgs = queryMsgsById(msgBean.getCreateTimeLong(), msgBean.getContent());
            if (msgs == null) {
                session.insertOrReplace(msgBean);
                if (isAddListFalg) {//判断是否需要加入缓存
                    mAddTrueMsgList.add(msgBean);
                }
            }
        }

    }
}
