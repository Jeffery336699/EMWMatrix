package cc.emw.mobile.chat.model;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.model.bean.ChatMsgBean;
import cc.emw.mobile.chat.model.bean.LocationBean;
import cc.emw.mobile.chat.model.bean.Videos;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.ApiEntity;

/**
 * Created by sunny.du on 2017/5/8.
 *
 * MVP Model层接口聊天联网信息
 */

public interface IChatNetModel {
    /**
     * 获取当前消息的列表
     *
     * @return 返回当前消息列表
     */
    List<ChatMsgBean> getMessageDateList();

    /**
     * 上传图片
     *
     * @param path     本地图片路径
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void uploadImage(String path, OnObtainDataListener listener);

    /**
     * 上传语音
     *
     * @param path     本地图片路径
     * @param time     语音时间  S
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void uploadAudios(String path, int time, OnObtainDataListener listener);

    /**
     * 发送消息的接口
     *
     * @param type          1个人聊天 2群组聊天
     * @param message       消息实体
     * @param chatMsgBean   本地数据库支持的实体
     * @param dataListIndex 要插入的消息目前在消息列表中的引索位置     便于后续消息状态更新
     * @param isR           是否刷新
     * @param listener      回调View层的接口  详情看回调接口描述
     */
    void sendMessage(int type, ApiEntity.Message message, ChatMsgBean chatMsgBean, int dataListIndex, boolean isR, OnObtainDataListener listener);

    /**
     * 上传视频文件
     *
     * @param path     本地视频路径
     * @param time     视频时间 S
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void uploadVideo(String path, int time, OnUploadVideoListener listener);

    /**
     * 根据群组id获取群组人员
     */
    void getGroupMember();

    /**
     * 获取当前GroupInfo实体类
     *
     * @return 返回相应的GroupInfo实体
     */
    GroupInfo getGroupInfo();

    /**
     * 当群组聊天时，  首字符是@的时候 则调用本接口解析匹配合适的群成员 添加到要显示的检索列表中
     *
     * @param messageContent 当前输入的文字
     * @param lestener       回调View层的接口  详情看回调接口描述
     */
    void editTextParserModel(String messageContent, OnParserLestener lestener);

    /**
     * 跟据群id获取群
     *
     * @param lestener 回调View层的接口  详情看回调接口描述
     */
    void getGroupsByID(OnGetGroupsByIDListener lestener);

    /**
     * 初始化群组ID和聊天对象的ID
     */
    void showIdInfo();

    /**
     * 本地数据库与网络数据进行比对，同步更新本地数据库的消息
     *
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void initMessageList(onRefreshMessageListener listener);

    /**
     * 下拉刷新数据接口
     *
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void refreshMessage(onRefreshMessageListener listener);

    /**
     * 人员变动提交后台
     *
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void saveGroupInfo(OnSaveGroupInfoListener listener);

    /**
     * 设置聊天头部名字
     *
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void getTitleName(OnTotleNameListener listener);

    /**
     * 当初始化消息时获取getIntent(); 如果包含指定字段则调用sendMessage接口发消息，详情请看具体实现
     *
     * @param sendmsg     消息内容
     * @param MessageType 聊天类型  1个人 2群组
     * @param listener    回调View层的接口  详情看回调接口描述
     */
    void sendOtherMessage(String sendmsg, int MessageType, OnObtainDataListener listener);

    /**
     * 上传地理位置相关的图片信息
     *
     * @param vPath    LocationBean地里位置实体
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void uploadMapImage(LocationBean vPath, OnObtainDataListener listener);

    /**
     * 当群组聊天时调用
     * 当第一个字符是@时并且弹出人员选择列表后   为了刷新列表数据   则调用本接口清理上一次的数据列表
     *
     * @param lestener 回调View层的接口  详情看回调接口描述
     */
    void clearParserList(OnParserLestener lestener);

    /**
     * 更新重要消息状态  (删除重要消息)
     *
     * @param msgId    消息ID
     * @param state    消息状态
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void updateImportanceMessage(String msgId, int state, OnStaticListener listener);

    /**
     * 更新重要消息状态  (删除重要消息)
     *
     * @param msgId    消息ID
     * @param state    消息状态
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void updateImportanceMessage(String msgId, int state, OnStaticListener listener,ChatActivity.UpdateAdapter updateAdapter);

    /**
     * 从当前消息列表中删除一个指定的消息并且返回
     *
     * @param i 需要删除的消息引索
     * @return 返回被删除的消息实体
     */
    ChatMsgBean removeMessageDateListItem(int i);

    /**
     * 给当前消息列表添加消息
     *
     * @param chatMsgBean 需要添加的消息实体
     */
    void addMessageDateListItem(ChatMsgBean chatMsgBean);

    /**
     * 根据引索值给指定消息实体做替换
     *
     * @param dataListIndex 消息引索
     * @param chatMsgBean   需要替换的消息实体
     */
    void setMessageDateListItem(int dataListIndex, ChatMsgBean chatMsgBean);

    /**
     * 群主管理员转让接口
     *
     * @param gid      群组ID
     * @param actor    指定成员ID
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void outChatterGroupByCreator(final int gid, int actor, ChatterGroupByCreatorListener listener);

    /**
     * 退出群组的接口
     *
     * @param gid      群组ID
     * @param userid   当前用户的UserId
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void delGroupRoles(final int gid, int userid, ChatterGroupByCreatorListener listener);

    /**
     * 解散群组的接口
     *
     * @param gid      群组ID
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void delGroup(int gid, ChatterGroupByCreatorListener listener);

    /**
     * 移除新消息状态
     *
     * @param isSendBroad 是否发广播
     * @param listener    回调View层的接口  详情看回调接口描述
     */
    void removeNewMessageBySenderID(final boolean isSendBroad, OnStaticBroadCaseListener listener);

    /**
     * 上传文件
     *
     * @param paths    需要上传的文件本地路径列表
     * @param listener 回调View层的接口  详情看回调接口描述
     */
    void upLoadFiles(ArrayList<String> paths, final OnObtainDataListener listener);

    /**
     * 初始化本地数据库的相关页数的信息
     */
    void initDBPaging();

    /***********************************************************************************************************************************************
     *回调接口
     */
    /*
     *公共回调接口
     */
    interface OnStaticListener {
        //弹文字Toast
        void showToast(String msg);
    }

    /*
     * 公共回调接口
     */
    interface OnStaticListenerPro extends OnStaticListener {
        //弹带有图片的文字Toast
        void showToast(String msg, int id);
    }

    /*
     * 公共回调接口
     */
    interface OnStaticFinishListenr {
        //关闭当前Activity
        void finish();
    }
    /*
    * 公共回调接口
    */
    interface OnStaticBroadCaseListener {
        //发送广播
        void sendBroadCast(String action);
    }
    /*
     * 公共回调接口
     */
    interface OnStaticShowDialogListener {
        //提交相关状态下展示加载状态的控件
        void showDialog();
    }
    /*
     * 公共回调接口
     */
    interface OnStaticDismiss {
        //隐藏加载状态的控件
        void dismissDialog();
    }
    /*
     * 组合回调接口
     */
    interface ChatterGroupByCreatorListener extends OnStaticListener, OnStaticListenerPro, OnStaticShowDialogListener, OnStaticDismiss, OnStaticFinishListenr, OnStaticBroadCaseListener {
    }
    /*
     * 组合回调接口
     */
    interface OnSaveGroupInfoListener extends OnStaticListener, OnStaticBroadCaseListener {
    }
    /*
     * 公共回调接口
     */
    interface OnObtainDataListener {
        //刷新消息列表展示
        void refreshListView(List<ChatMsgBean> dataList, boolean isRefresh);
        //发送广播
        void sendBroadCastsListener(String broadCastName, String action, int id, String broadCastAction);
    }
    /*
     * 公共回调接口
     */
    interface onRefreshMessageListener {
        //下拉刷新状态展示
        void showRefreshListener();
        //刷新当前消息列表
        void refreshListViewListener(List<ChatMsgBean> dataList, int pageIndex, int pageSize);
    }
    /*
     * 公共回调接口
     */
    interface OnUploadVideoListener extends OnObtainDataListener {
        //初始化视频播放的视频操作
        void initVideoPlayer(Videos videos);
    }
    /*
     * 公共回调接口
     */
    interface OnTotleNameListener {
        //设置头部名称
        void getTitleName(String name);
    }

    /*
     * 当群组聊天 第一个字符时@的时候  检索功能的相关回调
     */
    interface OnParserLestener {
        //设置需要展示的人员列表
        void setData(List<UserInfo> parserList);
        //清空列表
        void setNullData();
    }
    /*
     * 组合接口
     */
    interface OnGetGroupsByIDListener extends OnStaticShowDialogListener {
    }
}
