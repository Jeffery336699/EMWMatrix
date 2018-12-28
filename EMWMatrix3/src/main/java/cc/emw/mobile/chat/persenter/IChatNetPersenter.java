package cc.emw.mobile.chat.persenter;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.model.bean.ChatMsgBean;
import cc.emw.mobile.chat.model.bean.LocationBean;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.net.ApiEntity;
import io.socket.client.Socket;

public interface IChatNetPersenter {
    /**
     * 初始化聊天对象的基本参数   并且实例化ChatNetModelImpl将基本参数传递给Model层
     * @param mMainUserId   当前用户ID
     * @param mGroupId      当前群组ID
     * @param mType         当前聊天类型  1个人  2群组
     * @param mSsenderId    聊天目标ID      1对1聊天则存储对方ID，如果群组 则存储GroupId
     * @param mUnreadNum    已经废除   暂时无用
     */
    void obtainUserInfo(int mMainUserId, int mGroupId, int mType, int mSsenderId, int mUnreadNum, Socket mSocket);

    /**
     * 上传图片接口
     * @param path   图片本地路径
     */
    void uploadImagePersenter(String path);

    /**
     * 上传语音接口
     * @param path   语音本地路径
     * @param time    语音时间
     */
    void uploadAudiosPersenter(String path, int time);

    /**
     * 初始化消息列表获取本地数据库的页数
     */
    void initIdInfoPersenter();

    /**
     * 刷新当前消息列表
     */
    void refreshMessagePersenter();

    /**
     * 根据ID获取当前群组的实体类 GroupInfo
     *   id直接从Model层取。
     */
    void getGroupsByIDPersenter();

    /**
     * 获取当前群组的人员列表
     */
    void getGroupMemberPersenter();

    /**
     * 给当前群组添加新人
     */
    void saveGroupInfoPersenter();

    /**
     * 调用getGroupMember   showIdInfo   初始化群组成员信息和聊天相关的ID
     */
    void initDatas();

    /**
     * 当群组聊天时调用
     * 当第一个字符是@时并且弹出人员选择列表后   为了刷新列表数据   则调用本接口清理上一次的数据列表
     */
    void clearParserListPersenter();

    /**
     * 获取群组基本信息
     * @return GroupInfo
     */
    GroupInfo getGroupInfo();

    /**
     * 当第一个字符是@的时候调用
     * 根据输入框输入的字符调用本接口检索匹配的@相关的人员
     * @param messageContent  聊天框输入的信息
     */
    void editTextParser(String messageContent);

    /**
     * 上传视频的接口
     * @param path   视频本地地址
     * @param time   视频时间
     */
    void uploadVideoPersenter(String path, int time);

    /**
     * 设置当前聊天对话框头部名称
     */
    void setTitleNameTextViewPersenter();

    /**
     * 当初始化消息时获取getIntent(); 如果包含指定字段则调用sendMessage接口发消息，详情请看具体实现
     * @param content 内容
     * @param type    消息类型
     */
    void sendOtherMessagePersenter(String content, int type);

    /**
     * 上传地理位置相关的图片信息
     * @param vPath   LocationBean地里位置实体
     */
    void uploadMapImagePersenter(LocationBean vPath);

    /**
     * 修改消息状态。重要消息/取消重要消息标注
     * @param msgId   消息ID
     * @param state    状态   0  为普通消息  1为重要消息
     */
    void updateImportanceMessagePersenter(String msgId, int state);

    /**
     * 修改消息状态。重要消息/取消重要消息标注
     * @param msgId   消息ID
     * @param state    状态   0  为普通消息  1为重要消息
     */
    void updateImportanceMessagePersenter(String msgId, int state, ChatActivity.UpdateAdapter updateAdapter);

    /**
     * 获取当前的消息列表List
     * @return   当前消息列表
     */
    List<ChatMsgBean> getMessageList();

    /**
     * 删除指定一条消息并且返回被删除的消息实体
     * @param pos    需要删除的key
     * @return        删除成功的消息实体
     */
    ChatMsgBean removeMessageListItem(int pos);

    /**
     * 给消息列表添加一条消息
     * @param chatMsgBean    需要添加消息的实体
     */
    void addMessageListItem(ChatMsgBean chatMsgBean);

    /**
     * 修改指定引索的消息实体
     * @param dataListIndex   引索
     * @param chatMsgBean     需要修改的消息实体
     */
    void setMessageDateListItem(int dataListIndex, ChatMsgBean chatMsgBean);

    /**
     * 转让群管理员的接口
     * @param gid   群组ID
     * @param actor  需要转让的指定成员ID
     */
    void outChatterGroupByCreatorPersenter(final int gid, int actor);

    /**
     * 退出群组的接口
     * @param gid   群组ID
     * @param userid 当前用户ID
     */
    void delGroupRolesPersenter(final int gid, int userid);

    /**
     * 解散群组的接口
     * @param gid   群组ID
     */
    void delGroupPersenter(int gid);

    /**
     * 移除新消息标记
     * @param isSendBroad  是否需要发广播更新聊天界面外的数据
     */
    void removeNewMessageBySenderIdPersenter(final boolean isSendBroad);

    /**
     * 上传文件的接口
     * @param paths   本地文件的路径列表
     */
    void upLoadFilesPersenter(ArrayList<String> paths);

    /**
     * 发送消息的接口
     * @param type   1个人聊天 2群组聊天
     * @param message 消息实体
     * @param chatMsgBean   本地数据库支持的实体
     * @param dataListIndex  要插入的消息目前在消息列表中的引索位置     便于后续消息状态更新
     * @param isR   是否刷新
     */
    void sendMessagePersenter(int type, ApiEntity.Message message, final ChatMsgBean chatMsgBean, final int dataListIndex, boolean isR);

    /**
     * 界面初始化时进行服务器消息与本地消息比对 同步本地数据库
     */
    void initMessageListPersenter();

}
