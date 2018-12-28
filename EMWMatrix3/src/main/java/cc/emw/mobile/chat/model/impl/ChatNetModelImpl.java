package cc.emw.mobile.chat.model.impl;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.farsunset.cim.client.model.Message;
import com.githang.androidcrash.util.AppManager;
import com.google.gson.Gson;

import org.xutils.x;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.model.IChatMsgDBModel;
import cc.emw.mobile.chat.model.IChatNetModel;
import cc.emw.mobile.chat.model.bean.Audios;
import cc.emw.mobile.chat.model.bean.ChatMsgBean;
import cc.emw.mobile.chat.model.bean.Files;
import cc.emw.mobile.chat.model.bean.LocationBean;
import cc.emw.mobile.chat.model.bean.MessageInfo;
import cc.emw.mobile.chat.model.bean.Videos;
import cc.emw.mobile.chat.utils.DateUtil;
import cc.emw.mobile.contact.fragment.ChatHistoriesFragment;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.PrefsUtil;
import io.socket.client.IO;
import io.socket.client.Socket;


/**
 * Created by sunny.du on 2017/5/9.
 * Chat模块请求网络的model层实现 聊天消息请求到服务器中
 */
public class ChatNetModelImpl implements IChatNetModel {
    private List<ChatMsgBean> mDataList;
    private int mMainUserId;
    private int mGroupId;
    private GroupInfo mGroupInfo;
    private int mType;
    private int mSenderId;
    private int mUnreadNum;
    private IChatMsgDBModel mChatMsgDao;
    private List<UserInfo> noteRoles;
    private List<Integer> userids;
    private List<UserInfo> sUsers;
    private List<String> mGroupUserPinyin;
    private List<String> mGroupUserPinyin2;
    private List<UserInfo> parserList;
    private Socket mSocket;

    //  private int normal_mes = 4, audio_mes = 8, image_mes = 7, video_mes = 9;// 4 普通消息 7图片消息 6附件消息 8音频信息,10分享 信息
    public ChatNetModelImpl(int mainUserId, int groupId, int type, int senderId, int unreadNum, Socket mSocket) {
        mDataList = new LinkedList<>();
        noteRoles = new ArrayList<>();
        userids = new ArrayList<>();
        sUsers = new ArrayList<>();
        parserList = new ArrayList<>();
        mGroupUserPinyin2 = new ArrayList<>();
        mGroupUserPinyin = new ArrayList<>();
        mChatMsgDao = new ChatMsgDBModelImpl();
        mPageSize = 50;
        this.mMainUserId = mainUserId;
        this.mGroupId = groupId;
        this.mType = type;
        this.mSenderId = senderId;
        this.mUnreadNum = unreadNum;
        this.mSocket = mSocket;
    }

    /**
     * model内部使用
     * 构造消息
     *
     * @param msgType 消息类型
     * @param content 消息实体
     * @return 构造消息实体
     */
    private Message createMessageBean(int msgType, String content) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat temporaryTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式yyyy-MM-dd HH:mm:ss
        Message msg = new Message();
        msg.setID(0);
        msg.setContent(content);
        msg.setUserID(PrefsUtil.readUserInfo().ID);
        msg.setSenderID(PrefsUtil.readUserInfo().ID);
        msg.setReceiverID(mSenderId);
        msg.setType(msgType);
        msg.setCompanyCode(PrefsUtil.readUserInfo().CompanyCode);
        msg.setCreateTime(temporaryTimeFormat.format(new Date()));
        msg.setGroupID(mGroupId);
        return msg;
    }

    @Override
    public GroupInfo getGroupInfo() {
        return mGroupInfo;
    }

    @Override
    public void clearParserList(OnParserLestener lestener) {
        parserList.clear();
        lestener.setNullData();
    }

    @Override
    public void editTextParserModel(String messageContent, OnParserLestener lestener) {
        try {
            parserList.clear();
            if (!("".equals(messageContent))) {
                messageContent = messageContent.substring(1, messageContent.length());
                messageContent = messageContent.trim();
                if ("".equals(messageContent)) {
                    parserList.addAll(sUsers);
                    lestener.setData(parserList);
                } else {
                    for (int i = 0; i < mGroupUserPinyin.size(); i++) {
                        if (sUsers.get(i).Name.contains(messageContent.trim()) ||
                                mGroupUserPinyin.get(i).contains(messageContent.trim().toLowerCase()) ||
                                mGroupUserPinyin2.get(i).contains(messageContent.trim().toLowerCase())) {
                            parserList.add(sUsers.get(i));
                        }
                    }
                    lestener.setData(parserList);
                }
            } else {
                parserList.clear();
                lestener.setData(parserList);
            }
        } catch (Exception x) {
        }
    }

    private void performGroupUser2Pinyin() {
        mGroupUserPinyin.clear();
        mGroupUserPinyin2.clear();
        CharacterParser characterParser = CharacterParser.getInstance();
        StringBuilder sb = new StringBuilder();
        for (cc.emw.mobile.net.ApiEntity.UserInfo user : sUsers) {
            mGroupUserPinyin.add(characterParser.getSelling(user.Name.toLowerCase()));
            sb.delete(0, sb.length());
            for (int j = 0; j < user.Name.length(); j++) {
                String substring = user.Name.substring(j, j + 1);
                substring = characterParser.convert(substring);
                if (substring != null && substring.length() >= 1) {
                    substring = substring.substring(0, 1);
                    sb.append(substring);
                }
            }
            mGroupUserPinyin2.add(sb.toString());
        }
    }
    /***********************************************************************
     *
     *初始化信息接口
     */
    /**
     * 获取群成员信息
     */
    @Override
    public void getGroupMember() {
        if (EMWApplication.groupMap != null && EMWApplication.groupMap.get(mGroupId) != null) {
            mGroupInfo = EMWApplication.groupMap.get(mGroupId);
            Log.d("Const", "---chatNetModelImpl getGroupMember------");
            API.TalkerAPI.LoadGroupUsersByGid(mGroupId, new RequestCallback<UserInfo>(UserInfo.class, true) {
                @Override
                public void onParseSuccess(List<UserInfo> respList) {
                    if (respList != null && respList.size() > 0) {
                        noteRoles.clear();
                        if (mGroupInfo.Users != null) {
                            mGroupInfo.Users.clear();
                            mGroupInfo.Users.addAll(respList);
                        }
                        noteRoles.addAll(respList);
                        sUsers = noteRoles;
                        if (sUsers.size() > 0) {
                            userids.clear();
                            for (int i = 0; i < sUsers.size(); i++) {
                                userids.add(sUsers.get(i).ID);
                            }
                        }
                        performGroupUser2Pinyin();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                }

                @Override
                public void onStarted() {
                }
            });
        }
    }

    /**
     * 跟据群id获取群
     */
    @Override
    public void getGroupsByID(final OnGetGroupsByIDListener lestener) {
        if (mType == 2 && EMWApplication.groupMap.get(mGroupId) != null && EMWApplication.groupMap.get(mGroupId).CreateUser == 0) {
            API.TalkerAPI.GetChatterGroupById(mGroupId, new RequestCallback<GroupInfo>(GroupInfo.class) {
                @Override
                public void onParseSuccess(GroupInfo respInfo) {
                    if (respInfo != null) {
                        EMWApplication.groupMap.put(respInfo.ID, respInfo);
                    }
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    lestener.showDialog();
                }
            });
        }
    }

    @Override
    public void showIdInfo() {
        if (mGroupId != 0) {
            EMWApplication.currentChatUid = mGroupId;
        } else if (mSenderId != -1) {
            EMWApplication.currentChatUid = mSenderId;
        }
    }

    boolean isAddMsgFlag = false;

    @Override
    public void initMessageList(onRefreshMessageListener listener) {
        //        initDBPaging();//初始化参数：pageCount 总页数
        //        if (pageCount >= 0) {
        //            switch (mType) {
        //                case 1:
        //                    List<ChatMsgBean> chatMsgBeenList = mChatMsgDao.queryUserAll(mMainUserId, mSenderId, pageCount);
        //                    ChatMsgBean chatMsgBeen = null;
        //                    if (chatMsgBeenList.size() != 0) {
        //                        chatMsgBeen = chatMsgBeenList.get(chatMsgBeenList.size() - 1);
        //                    }
        //                    if (chatMsgBeen != null) performData(chatMsgBeen, listener);
        //                    break;
        //                case 2:
        //                    List<ChatMsgBean> msgGrounpList = mChatMsgDao.queryGroupAll(mMainUserId, mGroupId, pageCount);
        //                    ChatMsgBean chatMsgBeens = null;
        //                    if (msgGrounpList.size() != 0) {
        //                        chatMsgBeens = msgGrounpList.get(msgGrounpList.size() - 1);
        //                    }
        //                    if (chatMsgBeens != null) performData(chatMsgBeens, listener);
        //                    break;
        //            }
        //        } else {
        pageIndex = 0;
        initDBPaging();//初始化参数：pageCount 总页数
        refreshMessage(listener);
        //        }
    }

    private void performData(final ChatMsgBean msg, final onRefreshMessageListener listener) {
        ++pageIndex;
        API.Message.GetGroupMessages(mGroupId + "", pageIndex, mPageSize, new RequestCallback<Message>(Message.class) {
            @Override
            public void onError(Throwable arg0, boolean arg1) {
                --pageIndex;
            }

            @Override
            public void onParseSuccess(List<Message> respList) {
                for (int i = 0; i < respList.size(); i++) {
                    Date greateTimeDate = DateUtil.getDate("yyyy-MM-dd HH:mm:ss", respList.get(i).getCreateTime());
                    if (greateTimeDate != null && greateTimeDate.getTime() > msg.getCreateTimeLong()) {
                        mChatMsgDao.addMsgToDB(respList.get(i), 0, false);
                        if (i == respList.size() - 1) {
                            isAddMsgFlag = true;
                        }
                    }
                }
                if (isAddMsgFlag) {
                    isAddMsgFlag = false;
                    performData(msg, listener);//递归
                } else {
                    pageIndex = 0;
                    initDBPaging();//初始化参数：pageCount 总页数
                    refreshMessage(listener);
                }
            }
        });
    }

    /**********************************************
     * 下拉刷新封装方法
     * 根据类型获取消息记录
     */
    private boolean isRefreshFlag = false;//用于控制重复刷新失效功能开关。起到原子操作的效果。
    private int mPageSize;
    private int pageIndex = 0;
    private boolean isFirstFinish;

    @Override
    public void refreshMessage(final onRefreshMessageListener listener) {
        if (!isRefreshFlag) {
            isRefreshFlag = true;
            ++pageIndex;
            switch (mType) {
                case 1://个人聊天
                    List<ChatMsgBean> msgList;
                    if (pageCount > 0) {
                        pageCount--;
                        msgList = mChatMsgDao.queryUserAll(mMainUserId, mSenderId, pageCount);
                        if (msgList.size() != 0 && msgList.size() < 10 && pageCount > 0) {//第一页小于10条则再刷新一次    保证20以上的数据
                            mDataList.addAll(0, msgList);
                            pageCount--;
                            msgList = mChatMsgDao.queryUserAll(mMainUserId, mSenderId, pageCount);
                            mDataList.addAll(0, msgList);
                            listener.showRefreshListener();
                            if (!isFirstFinish || pageIndex > 1) {
                                listener.refreshListViewListener(mDataList, pageIndex, mPageSize);
                            }
                        } else if (msgList.size() != 0) {
                            listener.showRefreshListener();
                            mDataList.addAll(0, msgList);
                            if (!isFirstFinish || pageIndex > 1) {
                                listener.refreshListViewListener(mDataList, pageIndex, mPageSize);
                            }
                        }
                        isRefreshFlag = false;
                        if (pageIndex == 1) {
                            API.Message.GetChatMessages(mSenderId, 1, mPageSize, new RequestCallback<Message>(Message.class) {
                                @Override
                                public void onError(Throwable arg0, boolean arg1) {
                                    listener.showRefreshListener();
                                }

                                @Override
                                public void onParseSuccess(List<Message> respList) {
                                    listener.showRefreshListener();
                                    if (respList.size() != 0) {
                                        isFirstFinish = true;
                                        mDataList.clear();
                                        Collections.reverse(respList);
                                        for (Message msg : respList) {
                                            mDataList.add(mChatMsgDao.addMsgToDB(msg, 0, false));
                                        }
                                        listener.refreshListViewListener(mDataList, pageIndex, mPageSize);
                                    }
                                }
                            });
                        }
                    } else {
                        API.Message.GetChatMessages(mSenderId, pageIndex, mPageSize, new RequestCallback<Message>(Message.class) {
                            @Override
                            public void onError(Throwable arg0, boolean arg1) {
                                listener.showRefreshListener();
                                --pageIndex;
                                isRefreshFlag = false;
                            }

                            @Override
                            public void onParseSuccess(List<Message> respList) {
                                listener.showRefreshListener();
                                if (respList.size() != 0) {
                                    Collections.reverse(respList);
                                    for (Message msg : respList) {
                                        mChatMsgDao.addMsgToDB(msg, 0, true);
                                    }
                                    List<ChatMsgBean> addTrueMsgList = mChatMsgDao.getAddTrueMsgList();
                                    if (addTrueMsgList.size() != 0) {
                                        mDataList.addAll(0, addTrueMsgList);
                                        listener.refreshListViewListener(mDataList, pageIndex, mPageSize);
                                        addTrueMsgList.clear();//清除刷新数据列表以备下一次从服务器刷新记录数据
                                        mChatMsgDao.setAddTrueMsgList(addTrueMsgList);//清空缓存列表
                                    }
                                } else {
                                    --pageIndex;
                                }
                                isRefreshFlag = false;
                            }
                        });
                    }
                    break;
                case 2:// 获取群组消息
                    List<ChatMsgBean> msgGrounpList;
                    if (pageCount > 0) {
                        pageCount--;
                        Log.e("ChatMsgDBModelImpl", "----------msgGrounpLis-----------------");
                        msgGrounpList = mChatMsgDao.queryGroupAll(mMainUserId, mGroupId, pageCount);
                        if (msgGrounpList.size() != 0 && msgGrounpList.size() < 10 && pageCount > 0) {
                            mDataList.addAll(0, msgGrounpList);
                            pageCount--;
                            msgGrounpList = mChatMsgDao.queryGroupAll(mMainUserId, mGroupId, pageCount);
                            mDataList.addAll(0, msgGrounpList);
                            listener.showRefreshListener();
                            if (!isFirstFinish || pageIndex > 1) {
                                listener.refreshListViewListener(mDataList, pageIndex, mPageSize);
                            }
                        } else if (msgGrounpList.size() != 0) {
                            listener.showRefreshListener();
                            mDataList.addAll(0, msgGrounpList);
                            if (!isFirstFinish || pageIndex > 1) {
                                listener.refreshListViewListener(mDataList, pageIndex, mPageSize);
                            }
                        }
                        isRefreshFlag = false;
                        if (pageIndex == 1) {
                            API.Message.GetGroupMessages(mGroupId + "", 1, mPageSize, new RequestCallback<Message>(Message.class) {
                                @Override
                                public void onError(Throwable arg0, boolean arg1) {
                                    listener.showRefreshListener();
                                }

                                @Override
                                public void onParseSuccess(List<Message> respList) {
                                    listener.showRefreshListener();
                                    if (respList.size() != 0) {
                                        isFirstFinish = true;
                                        mDataList.clear();
                                        Collections.reverse(respList);
                                        for (Message msg : respList) {
                                            mDataList.add(mChatMsgDao.addMsgToDB(msg, 0, false));
                                        }
                                        listener.refreshListViewListener(mDataList, pageIndex, mPageSize);
                                    }
                                }
                            });
                        }
                    } else {
                        API.Message.GetGroupMessages(mGroupId + "", pageIndex, mPageSize, new RequestCallback<Message>(Message.class) {
                            @Override
                            public void onError(Throwable arg0, boolean arg1) {
                                listener.showRefreshListener();
                                --pageIndex;
                                isRefreshFlag = false;
                            }

                            @Override
                            public void onParseSuccess(List<Message> respList) {
                                listener.showRefreshListener();
                                if (respList.size() != 0) {
                                    Collections.reverse(respList);
                                    for (Message msg : respList) {
                                        mChatMsgDao.addMsgToDB(msg, 0, true);
                                    }
                                    List<ChatMsgBean> addTrueMsgList = mChatMsgDao.getAddTrueMsgList();
                                    if (addTrueMsgList.size() != 0) {
                                        mDataList.addAll(0, addTrueMsgList);
                                        listener.refreshListViewListener(mDataList, pageIndex, mPageSize);
                                        addTrueMsgList.clear();//清除刷新数据列表以备下一次从服务器刷新记录数据
                                        mChatMsgDao.setAddTrueMsgList(addTrueMsgList);//清空缓存列表
                                    }
                                } else {
                                    --pageIndex;
                                }
                                isRefreshFlag = false;
                            }
                        });
                    }
                    break;
            }
        }
    }

    /**********************************************
     * 从数据库中加载所有的数据取总数计算分页的页数。
     * 20条/页
     * type:个人/群组
     */
    private int pageCount;//总页数  从0开始计数  0 为第一页

    @Override
    public void initDBPaging() {
        List<ChatMsgBean> chatMsgBeen = new LinkedList<>();
        switch (mType) {
            case 1:/***私人聊天*****/
                chatMsgBeen = mChatMsgDao.queryUserAll(mMainUserId, mSenderId);
                break;
            case 2:/***群组聊天*****/
                Log.e("ChatMsgDBModelImpl", "----------initDBPaging() queryGroupAll-----------------");
                chatMsgBeen = mChatMsgDao.queryGroupAll(mMainUserId, mGroupId);
        }
        if (chatMsgBeen != null && chatMsgBeen.size() > 0) {
            if (chatMsgBeen.size() > 100) {
                //                pageCount = (chatMsgBeen.size() - 20) / 20;//数据库储存的总共页数
                //                if (((chatMsgBeen.size() - 20) % 20) > 0) {
                //                    pageCount += 1;
                //                }
                if (chatMsgBeen.size() % 100 == 0) {
                    pageCount = chatMsgBeen.size() / 100;
                } else {
                    pageCount = (chatMsgBeen.size() / 100) + 1;
                }
            } else {
                pageCount = 1;
            }
        } else {
            pageCount = 0;
        }
    }

    /**
     * 人员变动提交后台
     */
    @Override
    public void saveGroupInfo(final OnSaveGroupInfoListener listener) {
        API.TalkerAPI.SaveChatterGroup(mGroupInfo, userids, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                listener.showToast("编辑失败,服务器异常");
            }

            @Override
            public void onSuccess(String result) {
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    listener.showToast("添加成功,等待对方同意");
                    //刷新缓存
                    EMWApplication.groupMap.put(mGroupInfo.ID, mGroupInfo);
                    listener.sendBroadCast(GroupFragment.ACTION_REFRESH_GROUP);
                    getGroupMember();
                } else {
                    listener.showToast("编辑失败,请稍候再试");
                }
            }
        });
    }


    // 上传视频
    @Override
    public void uploadVideo(final String path, final int time, final OnUploadVideoListener listener) {
        RequestParam params = new RequestParam(Const.UPLOAD_VIDEO_URL);
        params.setMultipart(true);
        File file = new File(path);
        params.setMultipart(true);
        params.addBodyParameter(file.getName(), file);
        String imagePath = path.substring(0, path.lastIndexOf(".")) + ".jpg";
        String audiosJson = "{" + "\"Length\":" + "\"" + time + "\",\"ThumbFileName\":" + "\"" + imagePath + "\"," + "\"Url\":" + "\"" + file.getAbsolutePath() + "\"" + "}";
        Message msg = createMessageBean(ApiEnum.MessageType.Video, audiosJson);
        final ChatMsgBean chatMsgBean = mChatMsgDao.addMsgToDB(msg, ChatContent.SEND_MSG_STARTED, false);
        mDataList.add(chatMsgBean);
        listener.refreshListView(mDataList, true);
        final int dataListIndex = mDataList.size() - 1;//获取插入数据的位置便于更新状态
        x.http().post(params, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable arg0, boolean arg1) {
                if (chatMsgBean.getDbID() != null && chatMsgBean.getDbID() != 0) {
                    chatMsgBean.setIsSendFlag(ChatContent.SEND_MSG_ERROR);
                    mChatMsgDao.updateMsgItem(chatMsgBean);
                    mDataList.set(dataListIndex, chatMsgBean);
                    listener.refreshListView(mDataList, true);
                }
            }

            @Override
            public void onSuccess(String arg0) {
                File oldName = new File(path);
                String fileName = arg0.substring(arg0.lastIndexOf("/") + 1).replace("\"", "");
                File newName = new File(EMWApplication.videoPath + fileName);
                if (oldName.exists()) {
                    //给文件改名
                    oldName.renameTo(newName);
                }
                Videos videos = new Videos();
                videos.setUrl(new Gson().fromJson(arg0, String.class));
                videos.setThumbFileName(fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg");
                videos.setLength(time + "");
                listener.initVideoPlayer(videos);
                if (!"".equals(arg0)) {
                    String send_msg = new Gson().toJson(videos);
                    chatMsgBean.setContent(send_msg);
                    if (chatMsgBean.getDbID() != null && chatMsgBean.getDbID() != 0) {
                        chatMsgBean.setIsSendFlag(ChatContent.SEND_MSG_SUCCESS);
                        mChatMsgDao.updateMsgItem(chatMsgBean);
                        mDataList.set(dataListIndex, chatMsgBean);
                        listener.refreshListView(mDataList, true);
                    }
                    ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID, PrefsUtil.readUserInfo().ID, mSenderId, ChatContent.VIDEO_MSG, PrefsUtil.readUserInfo().CompanyCode, null, mGroupId);
                    sendMessage(mType, message, chatMsgBean, dataListIndex, true, listener);
                }
            }
        });
    }

    @Override
    public List<ChatMsgBean> getMessageDateList() {
        return mDataList;
    }

    @Override
    public ChatMsgBean removeMessageDateListItem(int i) {
        ChatMsgBean remove = mDataList.remove(i);
        return remove;
    }

    public void setMessageDateListItem(int dataListIndex, ChatMsgBean chatMsgBean) {
        try {
            mDataList.set(dataListIndex, chatMsgBean);
        } catch (Exception x) {
            mDataList.set(dataListIndex - 1, chatMsgBean);
        }
    }

    @Override
    public void addMessageDateListItem(ChatMsgBean chatMsgBean) {
        mDataList.add(chatMsgBean);
    }

    @Override
    public void uploadImage(String path, final OnObtainDataListener listener) {
        RequestParam params = new RequestParam(Const.UPLOAD_IMAGE_URL);
        params.setMultipart(true);
        final File file = new File(path);
        params.addBodyParameter(file.getName(), file);
        Message msg = createMessageBean(ApiEnum.MessageType.Image, file.getAbsolutePath());
        final ChatMsgBean chatMsgBean = mChatMsgDao.addMsgToDB(msg, ChatContent.SEND_MSG_STARTED, false);
        mDataList.add(chatMsgBean);
        listener.refreshListView(mDataList, true);
        final int dataListIndex = mDataList.size() - 1;//获取插入数据的位置便于更新状态
        x.http().post(params, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (chatMsgBean.getDbID() != null && chatMsgBean.getDbID() != 0) {
                    chatMsgBean.setIsSendFlag(ChatContent.SEND_MSG_ERROR);
                    mChatMsgDao.updateMsgItem(chatMsgBean);
                    mDataList.set(dataListIndex, chatMsgBean);
                    listener.refreshListView(mDataList, true);
                }
            }

            @Override
            public void onParseSuccess(List<String> result) {
                // 获取服务器返回的图片的url发送消息
                if (result != null && result.size() > 0 && result.get(0).lastIndexOf("/") != -1) {
                    Files image = new Files();
                    image.Url = result.get(0).substring(result.get(0).lastIndexOf("/") + 1);
                    image.ID = 0;
                    image.Name = file.getName();
                    image.Content = "";
                    image.Length = file.length();
                    String send_msg = new Gson().toJson(image);
                    chatMsgBean.setContent(send_msg);
                    if (chatMsgBean.getDbID() != null && chatMsgBean.getDbID() != 0) {
                        chatMsgBean.setIsSendFlag(ChatContent.SEND_MSG_SUCCESS);
                        mChatMsgDao.updateMsgItem(chatMsgBean);
                        mDataList.set(dataListIndex, chatMsgBean);
                        listener.refreshListView(mDataList, true);
                    }
                    ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID, PrefsUtil.readUserInfo().ID, mSenderId, ChatContent.IMAGE_MSG, PrefsUtil.readUserInfo().CompanyCode, null, mGroupId);
                    sendMessage(mType, message, chatMsgBean, dataListIndex, true, listener);
                }
            }
        });
    }


    // 上传音频
    @Override
    public void uploadAudios(String path, final int time, final OnObtainDataListener listener) {
        RequestParam params = new RequestParam(Const.UPLOAD_AUDIO_URL);
        params.setMultipart(true);
        File file = new File(path);
        params.setMultipart(true);
        params.addBodyParameter(file.getName(), file);
        String audiosJson = "{" + "\"Length\":" + "\"" + time + "\"," + "\"Url\":" + "\"" + file.getAbsolutePath() + "\"" + "}";
        Message msg = createMessageBean(ApiEnum.MessageType.Audio, audiosJson);
        final ChatMsgBean chatMsgBean = mChatMsgDao.addMsgToDB(msg, ChatContent.SEND_MSG_STARTED, false);
        mDataList.add(chatMsgBean);
        listener.refreshListView(mDataList, true);
        final int dataListIndex = mDataList.size() - 1;//获取插入数据的位置便于更新状态
        x.http().post(params, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable arg0, boolean arg1) {
                if (chatMsgBean.getDbID() != null && chatMsgBean.getDbID() != 0) {
                    chatMsgBean.setIsSendFlag(ChatContent.SEND_MSG_ERROR);
                    mChatMsgDao.updateMsgItem(chatMsgBean);
                    mDataList.set(dataListIndex, chatMsgBean);
                    listener.refreshListView(mDataList, true);
                }
            }

            @Override
            public void onSuccess(String arg0) {
                Audios audios = new Audios();
                audios.setUrl(new Gson().fromJson(arg0, String.class));
                audios.setLength(time + "");
                if (!"".equals(arg0)) {
                    String send_msg = new Gson().toJson(audios);
                    chatMsgBean.setContent(send_msg);
                    if (chatMsgBean.getDbID() != null && chatMsgBean.getDbID() != 0) {
                        chatMsgBean.setIsSendFlag(ChatContent.SEND_MSG_SUCCESS);
                        mChatMsgDao.updateMsgItem(chatMsgBean);
                        mDataList.set(dataListIndex, chatMsgBean);
                        listener.refreshListView(mDataList, true);
                    }
                    ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID, PrefsUtil.readUserInfo().ID, mSenderId, ChatContent.AUDIO_MSG, PrefsUtil.readUserInfo().CompanyCode, null, mGroupId);
                    sendMessage(mType, message, chatMsgBean, dataListIndex, true, listener);
                }
            }
        });
    }

    /**********************************************
     * 消息状态：发送中。。。     发送失败。。。  发送成功。。。
     * 用于发送消息时对数据的重新更改数据状态
     */
    private void modifyMsgSate(int dataListIndex, ChatMsgBean chatMsgBean, int state, boolean isRefresh, OnObtainDataListener listener) {
        if (chatMsgBean.getDbID() != null && chatMsgBean.getDbID() != 0) {
            chatMsgBean.setIsSendFlag(state);
            mChatMsgDao.updateMsgItem(chatMsgBean);
            setMessageDateListItem(dataListIndex, chatMsgBean);
            listener.refreshListView(mDataList, isRefresh);
        }
    }

    /**********************************************
     * 向服务器发送消息
     * 1、普通文本消息
     * 2、多媒体消息会先调用上传服务器的相应接口，上传完成后再拼接msg bean调用本方法将多媒体消息发送服务器
     * 3、socket io 上传聊天信息
     */

    public void sendSocketChatMessage(final ApiEntity.Message message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mSocket == null) {
                        try {
                            mSocket = IO.socket(Const.SOCKET_URL);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!mSocket.connected()) {
                        mSocket.connect();
                    }
                    String messageStr = new Gson().toJson(message);
                    mSocket.send(messageStr);
                } catch (Exception ex) {

                }
            }
        }).start();
    }

    @Override
    public void sendMessage(int type, ApiEntity.Message message, final ChatMsgBean chatMsgBean, final int dataListIndex, final boolean isR, final OnObtainDataListener listener) {
        Log.d("Const", "---chatNetModelImpl getGroupMember--sendMessage-test---");
        sendSocketChatMessage(message);
        switch (type) {
            case 1:// 单人消息
                API.Message.Send(message, true, new RequestCallback<String>(String.class) {
                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        modifyMsgSate(dataListIndex, chatMsgBean, ChatContent.SEND_MSG_ERROR, true, listener);
                    }

                    @Override
                    public void onSuccess(String result) {
                        Message msg = new Gson().fromJson(result, Message.class);
                        if (msg != null) {
                            chatMsgBean.setID(msg.getID());
                            chatMsgBean.setCreateTime(msg.getCreateTime());
                            if (chatMsgBean.getContent().contains("@EMW ")) {
                                chatMsgBean.setCreateTimeLong(DateUtil.getDate("yyyy-MM-dd HH:mm:ss", msg.getCreateTime()).getTime() - 1000);
                            } else {
                                chatMsgBean.setCreateTimeLong(DateUtil.getDate("yyyy-MM-dd HH:mm:ss", msg.getCreateTime()).getTime());
                            }
                        }
                        modifyMsgSate(dataListIndex, chatMsgBean, ChatContent.SEND_MSG_SUCCESS, isR, listener);
                        listener.sendBroadCastsListener(MainActivity.ACTION_REFRESH_COUNT, "chat", mSenderId, ChatHistoriesFragment.ACTION_REFRESH_CHAT_HISTORY);
                        listener.sendBroadCastsListener(null, null, 0, ChatHistoriesFragment.ACTION_REFRESH_CHAT_HISTORY);
                        listener.sendBroadCastsListener(null, null, 0, MainActivity.ACTION_RIGHT_REFRESH);
                    }
                });

                break;
            case 2:// 群组消息
                API.Message.SendGroup(message, new RequestCallback<String>(String.class) {
                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        modifyMsgSate(dataListIndex, chatMsgBean, ChatContent.SEND_MSG_ERROR, false, listener);
                    }

                    @Override
                    public void onSuccess(String result) {//返回的是messageID
                        Message msg = new Gson().fromJson(result, Message.class);
                        if (msg != null) {
                            chatMsgBean.setID(msg.getID());
                            chatMsgBean.setCreateTime(msg.getCreateTime());
                        }
                        modifyMsgSate(dataListIndex, chatMsgBean, ChatContent.SEND_MSG_SUCCESS, isR, listener);
                        listener.sendBroadCastsListener(MainActivity.ACTION_REFRESH_COUNT, "chat", mSenderId, ChatHistoriesFragment.ACTION_REFRESH_CHAT_HISTORY);
                        listener.sendBroadCastsListener(null, null, 0, ChatHistoriesFragment.ACTION_REFRESH_CHAT_HISTORY);
                        listener.sendBroadCastsListener(null, null, 0, MainActivity.ACTION_RIGHT_REFRESH);
                    }
                });
                break;
        }
    }


    @Override
    public void getTitleName(OnTotleNameListener listener) {
        String name = "";
        switch (mType) {
            case 1:/***私人聊天*****/
                if (EMWApplication.personMap != null && EMWApplication.personMap.get(mSenderId) != null) {
                    UserInfo user = EMWApplication.personMap.get(mSenderId);
                    if (user != null) {
                        name = user.Name;
                    }
                }
                break;
            case 2:/***群组聊天*****/
                if (EMWApplication.groupMap != null && EMWApplication.groupMap.get(mGroupId) != null) {
                    name = EMWApplication.groupMap.get(mGroupId).Name;
                }
                break;
        }
        listener.getTitleName(name);
    }

    @Override
    public void sendOtherMessage(String sendmsg, int MessageType, OnObtainDataListener listener) {
        Message msg = createMessageBean(MessageType, sendmsg);
        final ChatMsgBean chatMsgBean = mChatMsgDao.addMsgToDB(msg, ChatContent.SEND_MSG_STARTED, false);
        mDataList.add(chatMsgBean);
        listener.refreshListView(mDataList, true);
        final int dataListIndex = mDataList.size() - 1;//获取插入数据的位置便于更新状态
        ApiEntity.Message message = new MessageInfo(0, sendmsg, PrefsUtil.readUserInfo().ID, PrefsUtil.readUserInfo().ID, mSenderId, MessageType, PrefsUtil.readUserInfo().CompanyCode, null, mGroupId);
        sendMessage(mType, message, chatMsgBean, dataListIndex, true, listener);
    }

    @Override
    public void uploadMapImage(final LocationBean vPath, final OnObtainDataListener listener) {
        RequestParam params = new RequestParam(Const.UPLOAD_IMAGE_URL);
        params.setMultipart(true);
        final File file = new File(vPath.url);
        params.addBodyParameter(file.getName(), file);

        String send_msg = new Gson().toJson(vPath);
        Message msg = createMessageBean(ChatContent.CHAT_LOCATION, send_msg);
        final ChatMsgBean chatMsgBean = mChatMsgDao.addMsgToDB(msg, ChatContent.SEND_MSG_STARTED, false);
        mDataList.add(chatMsgBean);
        listener.refreshListView(mDataList, true);
        final int dataListIndex = mDataList.size() - 1;//获取插入数据的位置便于更新状态

        x.http().post(params, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (chatMsgBean.getDbID() != null && chatMsgBean.getDbID() != 0) {
                    chatMsgBean.setIsSendFlag(ChatContent.SEND_MSG_ERROR);
                    mChatMsgDao.updateMsgItem(chatMsgBean);
                    mDataList.set(dataListIndex, chatMsgBean);
                    listener.refreshListView(mDataList, true);
                }
            }

            @Override
            public void onParseSuccess(List<String> result) {
                // 获取服务器返回的图片的url发送消息
                if (result != null && result.size() > 0 && result.get(0).lastIndexOf("/") != -1) {
                    vPath.url = result.get(0).substring(result.get(0).lastIndexOf("/") + 1);
                    String send_msg = new Gson().toJson(vPath);
                    chatMsgBean.setContent(send_msg);
                    int msgType = ChatContent.CHAT_LOCATION;
                    if (vPath.isShareLocation) {
                        msgType = ChatContent.CHAT_SHARE_LOCATION;
                        chatMsgBean.setType(msgType);
                    }
                    ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID, PrefsUtil.readUserInfo().ID, mSenderId,
                            msgType, PrefsUtil.readUserInfo().CompanyCode, null, mGroupId);
                    sendMessage(mType, message, chatMsgBean, mDataList.size() - 1, false, listener);//获取插入数据的位置便于更新状态);

                }
            }
        });
    }

    @Override
    public void updateImportanceMessage(String msgId, final int state, final OnStaticListener listener) {
        API.Message.UpdateOptionsByID(msgId, state, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                listener.showToast("网络异常，请稍后尝试");
            }

            @Override
            public void onSuccess(String result) {
                listener.showToast("操作成功");
            }
        });
    }

    @Override
    public void updateImportanceMessage(String msgId, final int state, final OnStaticListener listener, final ChatActivity.UpdateAdapter updateAdapter) {
        API.Message.UpdateOptionsByID(msgId, state, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                listener.showToast("网络异常，请稍后尝试");
            }

            @Override
            public void onSuccess(String result) {
                listener.showToast("操作成功");
                updateAdapter.updateAdapter(state);
            }
        });
    }

    @Override
    public void outChatterGroupByCreator(final int gid, int actor, final ChatterGroupByCreatorListener listener) {
        API.TalkerAPI.OutChatterGroupByCreator(gid, actor, new RequestCallback<String>(String.class) {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result) && result.equals("true")) {
                    // ToastUtil.showToast(ChatActivity.this, "转让管理员成功", R.drawable.tishi_ico_gougou);
                    listener.showToast("转让管理员成功", R.drawable.tishi_ico_gougou);
                    delGroupRoles(gid, PrefsUtil.readUserInfo().ID, listener);
                } else {
                    // ToastUtil.showToast(ChatActivity.this, "操作失败");
                    listener.showToast("操作失败");
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                // ToastUtil.showToast(ChatActivity.this, "操作失败,服务器异常");
                listener.showToast("操作失败,服务器异常");
            }
        });
    }

    @Override
    public void delGroupRoles(final int gid, int userid, final ChatterGroupByCreatorListener listener) {
        API.TalkerAPI.DelGroupUser(gid, userid, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                listener.showDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.dismissDialog();
                listener.showToast("", R.string.groupinto_exit_error);
            }

            @Override
            public void onSuccess(String result) {
                listener.dismissDialog();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    deletes(gid);
                    AppManager.finishActivity(ChatActivity.class);
                    listener.showToast("退出成功！", R.string.groupinto_exit_error);
                    listener.sendBroadCast(GroupFragment.ACTION_REFRESH_GROUP);
                    listener.finish();
                } else {
                    listener.showToast("退出失败！");
                }
            }
        });
    }

    /**
     * //     * 解散群
     */
    @Override
    public void delGroup(int gid, final ChatterGroupByCreatorListener listener) {
        API.TalkerAPI.DelGroup(gid, new RequestCallback<String>(String.class) {
            @Override
            public void onStarted() {
                listener.showDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.dismissDialog();
                listener.showToast("解散失败！");
            }

            @Override
            public void onSuccess(String result) {
                listener.dismissDialog();
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    deletes(mGroupInfo.ID);
                    AppManager.finishActivity(ChatActivity.class);
                    listener.showToast("解散成功！", R.drawable.tishi_ico_gougou);
                    listener.sendBroadCast(GroupFragment.ACTION_REFRESH_GROUP);
                    listener.finish();
                } else {
                    listener.showToast("解散失败！");
                }
            }
        });
    }

    public void deletes(int sendID) {
        API.Message.RemoveChatRecord(sendID, 2, new RequestCallback<String>(
                String.class) {
            @Override
            public void onError(Throwable arg0, boolean arg1) {
            }

            @Override
            public void onSuccess(String result) {
            }
        });
    }

    @Override
    public void removeNewMessageBySenderID(final boolean isSendBroad, final OnStaticBroadCaseListener listener) {
        API.Message.RemoveNewMessageBySenderID(mGroupId == 0 ? mSenderId : mGroupId, mType, new RequestCallback<String>(String.class) {
            @Override
            public void onSuccess(String result) {
                listener.sendBroadCast(ChatHistoriesFragment.ACTION_REFRESH_CHAT_HISTORY);
                listener.sendBroadCast(MainActivity.ACTION_REFRESH_COUNT);
                if (isSendBroad) {
                    listener.sendBroadCast(MainActivity.ACTION_RIGHT_REFRESH);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }
        });
    }

    @Override
    public void upLoadFiles(ArrayList<String> paths, final OnObtainDataListener listener) {
        for (String path : paths) {
            Uri uri = Uri.parse(path);
            File file = com.nononsenseapps.filepicker.Utils.getFileForUri(uri);
            RequestParam params = new RequestParam(Const.BASE_URL + "/UploadFile");
            params.addQueryStringParameter("path", "");
            params.addQueryStringParameter("n", "");
            params.addQueryStringParameter("save", "1");
            params.setMultipart(true);
            params.addBodyParameter("file", file);
            x.http().post(params, new RequestCallback<ApiEntity.UploadResult>(ApiEntity.UploadResult.class) {
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                }

                @Override
                public void onStarted() {
                }

                @Override
                public void onParseSuccess(List<ApiEntity.UploadResult> respList) {
                    if (respList != null && respList.size() > 0 && respList.get(0).FileInfo != null) {
                        Files files = new Files();
                        files.setLength(respList.get(0).FileInfo.Length);
                        files.setName(respList.get(0).FileInfo.Name);
                        files.setUrl(respList.get(0).FileInfo.Url);
                        files.setID(respList.get(0).FileInfo.ID);
                        String send_msg = new Gson().toJson(files);
                        int attach_mes = 6;
                        Message msg = createMessageBean(attach_mes, send_msg);
                        final ChatMsgBean chatMsgBean = mChatMsgDao.addMsgToDB(msg, ChatContent.SEND_MSG_STARTED, false);
                        mDataList.add(chatMsgBean);
                        listener.refreshListView(mDataList, true);
                        final int dataListIndex = mDataList.size() - 1;//获取插入数据的位置便于更新状态
                        ApiEntity.Message message = new MessageInfo(0, send_msg, PrefsUtil.readUserInfo().ID, PrefsUtil.readUserInfo().ID, mSenderId, attach_mes, PrefsUtil.readUserInfo().CompanyCode, null, mGroupId);
                        sendMessage(mType, message, chatMsgBean, dataListIndex, true, listener);
                    }
                }
            });

        }
    }
}