package cc.emw.mobile.chat.persenter.impl;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.base.BasePresenter;
import cc.emw.mobile.chat.model.IChatNetModel;
import cc.emw.mobile.chat.model.bean.ChatMsgBean;
import cc.emw.mobile.chat.model.bean.LocationBean;
import cc.emw.mobile.chat.model.bean.Videos;
import cc.emw.mobile.chat.model.impl.ChatNetModelImpl;
import cc.emw.mobile.chat.persenter.IChatNetPersenter;
import cc.emw.mobile.chat.ui.IChatView;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.ApiEntity;
import io.socket.client.Socket;

/**
 * Created by sunny.du on 2017/5/9.
 * <p>
 * MVP persenter 层提供外面接口
 */

public class ChatNetPersenterImpl<T> extends BasePresenter<IChatView> implements IChatNetPersenter {
    private int mMainUserId, mGroupId, mType, mSenderId, mUnreadNum;//用户ID，群组ID   聊天类型(个人/群组)，接收者ID，
    private IChatView mView;
    private IChatNetModel mChatNetModel;

    public ChatNetPersenterImpl(IChatView view) {
        mView = view;
    }

    @Override
    public void obtainUserInfo(int mainUserId, int groupId, int type, int senderId, int unreadNum, Socket mSocket) {
        this.mMainUserId = mainUserId;
        this.mGroupId = groupId;
        this.mType = type;
        this.mSenderId = senderId;
        this.mUnreadNum = unreadNum;
        mChatNetModel = new ChatNetModelImpl(mMainUserId, mGroupId, mType, mSenderId, mUnreadNum, mSocket);
    }


    @Override
    public void uploadAudiosPersenter(String path, final int time) {
        mChatNetModel.uploadAudios(path, time, new IChatNetModel.OnObtainDataListener() {
            @Override
            public void refreshListView(List<ChatMsgBean> dataList, boolean isR) {
                mView.modifyMsgSateView(dataList, isR);
            }

            @Override
            public void sendBroadCastsListener(String broadCastName, String action, int id, String broadCastAction) {
                mView.sendBroadCast(broadCastName, action, id, broadCastAction);
            }
        });
    }

    @Override
    public void uploadImagePersenter(String path) {
        mChatNetModel.uploadImage(path, new IChatNetModel.OnObtainDataListener() {
            @Override
            public void refreshListView(List<ChatMsgBean> dataList, boolean isR) {
                mView.modifyMsgSateView(dataList, isR);
            }

            @Override
            public void sendBroadCastsListener(String broadCastName, String action, int id, String broadCastAction) {
                mView.sendBroadCast(broadCastName, action, id, broadCastAction);
            }
        });
    }

    @Override
    public GroupInfo getGroupInfo() {
        return mChatNetModel.getGroupInfo();
    }

    @Override
    public void editTextParser(String messageContent) {
        mChatNetModel.editTextParserModel(messageContent, new IChatNetModel.OnParserLestener() {
            @Override
            public void setData(List<UserInfo> parserList) {
                mView.groupUserSearchVisible(parserList);

            }

            @Override
            public void setNullData() {
                mView.groupUserSearchGone();
            }
        });
    }

    @Override
    public void initDatas() {
        mChatNetModel.getGroupMember();
        mChatNetModel.getGroupsByID(new IChatNetModel.OnGetGroupsByIDListener() {
            @Override
            public void showDialog() {
                mView.showDialogError();
            }
        });
        mChatNetModel.showIdInfo();
    }

    @Override
    public void clearParserListPersenter() {
        mChatNetModel.clearParserList(new IChatNetModel.OnParserLestener() {
            @Override
            public void setData(List<UserInfo> parserList) {
                mView.groupUserSearchVisible(parserList);
            }

            @Override
            public void setNullData() {
                mView.groupUserSearchGone();
            }
        });
    }

    @Override
    public void initIdInfoPersenter() {
        mChatNetModel.initDBPaging();
        this.refreshMessagePersenter();//加载第一次数据
    }

    @Override
    public void refreshMessagePersenter() {
        mChatNetModel.refreshMessage(new IChatNetModel.onRefreshMessageListener() {
            @Override
            public void showRefreshListener() {
                mView.showRefreshView();
            }

            @Override
            public void refreshListViewListener(List<ChatMsgBean> dataList, int pageIndex, int pageSize) {
                mView.refreshListView(dataList, pageIndex, pageSize);
            }
        });
    }

    @Override
    public void getGroupsByIDPersenter() {
        mChatNetModel.getGroupsByID(new IChatNetModel.OnGetGroupsByIDListener() {
            @Override
            public void showDialog() {
                mView.showDialogError();
            }
        });
    }

    @Override
    public void getGroupMemberPersenter() {
        mChatNetModel.getGroupMember();
    }

    @Override
    public void saveGroupInfoPersenter() {
        mChatNetModel.saveGroupInfo(new IChatNetModel.OnSaveGroupInfoListener() {
            @Override
            public void showToast(String msg) {
                mView.showToast(msg);
            }

            @Override
            public void sendBroadCast(String action) {
                mView.sendBroadCast4saveGroupInfo(action);
            }
        });
    }

    @Override
    public void uploadVideoPersenter(String path, int time) {
        mChatNetModel.uploadVideo(path, time, new IChatNetModel.OnUploadVideoListener() {
            @Override
            public void initVideoPlayer(Videos videos) {
                mView.initVideoPlayer(videos);
            }

            @Override
            public void refreshListView(List<ChatMsgBean> dataList, boolean isR) {
                mView.modifyMsgSateView(dataList, isR);
            }

            @Override
            public void sendBroadCastsListener(String broadCastName, String action, int id, String broadCastAction) {
                mView.sendBroadCast(broadCastName, action, id, broadCastAction);
            }
        });
    }

    @Override
    public void setTitleNameTextViewPersenter() {
        mChatNetModel.getTitleName(new IChatNetModel.OnTotleNameListener() {
            @Override
            public void getTitleName(String name) {
                mView.setTitleNameTextView(name);
            }
        });
    }

    @Override
    public void sendOtherMessagePersenter(String content, int type) {
        mChatNetModel.sendOtherMessage(content, type, new IChatNetModel.OnObtainDataListener() {
            @Override
            public void refreshListView(List<ChatMsgBean> dataList, boolean isR) {
                mView.modifyMsgSateView(dataList, isR);
            }

            @Override
            public void sendBroadCastsListener(String broadCastName, String action, int id, String broadCastAction) {
                mView.sendBroadCast(broadCastName, action, id, broadCastAction);
            }
        });
    }

    @Override
    public void uploadMapImagePersenter(LocationBean vPath) {
        mChatNetModel.uploadMapImage(vPath, new IChatNetModel.OnObtainDataListener() {
            @Override
            public void refreshListView(List<ChatMsgBean> dataList, boolean isR) {
                mView.modifyMsgSateView(dataList, isR);
            }

            @Override
            public void sendBroadCastsListener(String broadCastName, String action, int id, String broadCastAction) {
                mView.sendBroadCast(broadCastName, action, id, broadCastAction);
            }
        });
    }

    @Override
    public void updateImportanceMessagePersenter(String msgId, int state) {
        mChatNetModel.updateImportanceMessage(msgId, state, new IChatNetModel.OnStaticListener() {
            @Override
            public void showToast(String msg) {
                mView.showToast(msg);
            }
        });
    }

    @Override
    public void updateImportanceMessagePersenter(String msgId, int state, ChatActivity.UpdateAdapter updateAdapter) {
        mChatNetModel.updateImportanceMessage(msgId, state, new IChatNetModel.OnStaticListener() {
            @Override
            public void showToast(String msg) {
                mView.showToast(msg);
            }
        }, updateAdapter);
    }

    /***
     * TODO   临时策略，所有的M层写完重新优化本接口方法
     * @return
     */
    @Override
    public List<ChatMsgBean> getMessageList() {
        return mChatNetModel.getMessageDateList();
    }

    @Override
    public ChatMsgBean removeMessageListItem(int pos) {
        ChatMsgBean chatMsgBean = mChatNetModel.removeMessageDateListItem(pos);
        return chatMsgBean;
    }

    @Override
    public void addMessageListItem(ChatMsgBean chatMsgBean) {
        mChatNetModel.addMessageDateListItem(chatMsgBean);
    }

    @Override
    public void setMessageDateListItem(int dataListIndex, ChatMsgBean chatMsgBean) {
        mChatNetModel.setMessageDateListItem(dataListIndex, chatMsgBean);
    }

    @Override
    public void outChatterGroupByCreatorPersenter(int gid, final int actor) {
        mChatNetModel.outChatterGroupByCreator(gid, actor, new IChatNetModel.ChatterGroupByCreatorListener() {

            @Override
            public void dismissDialog() {
                mView.dismissDialog();
            }

            @Override
            public void showDialog() {
                mView.showDialog();
            }

            @Override
            public void sendBroadCast(String action) {
                mView.sendBroadCast(null, null, 0, action);
            }

            @Override
            public void finish() {
                mView.finishActivity();
            }

            @Override
            public void showToast(String msg, int id) {
                mView.showToastPro(msg, id);
            }

            @Override
            public void showToast(String msg) {
                mView.showToast(msg);
            }
        });
    }

    @Override
    public void delGroupRolesPersenter(int gid, int userid) {
        mChatNetModel.delGroupRoles(gid, userid, new IChatNetModel.ChatterGroupByCreatorListener() {

            @Override
            public void dismissDialog() {
                mView.dismissDialog();
            }

            @Override
            public void showDialog() {
                mView.showDialog();
            }

            @Override
            public void sendBroadCast(String action) {
                mView.sendBroadCast(null, null, 0, action);
            }

            @Override
            public void finish() {
                mView.finishActivity();
            }

            @Override
            public void showToast(String msg, int id) {
                mView.showToastPro(msg, id);
            }

            @Override
            public void showToast(String msg) {
                mView.showToast(msg);
            }
        });
    }

    @Override
    public void delGroupPersenter(int gid) {
        mChatNetModel.delGroup(gid, new IChatNetModel.ChatterGroupByCreatorListener() {

            @Override
            public void dismissDialog() {
                mView.dismissDialog();
            }

            @Override
            public void showDialog() {
                mView.showDialog();
            }

            @Override
            public void sendBroadCast(String action) {
                mView.sendBroadCast(null, null, 0, action);
            }

            @Override
            public void finish() {
                mView.finishActivity();
            }

            @Override
            public void showToast(String msg, int id) {
                mView.showToastPro(msg, id);
            }

            @Override
            public void showToast(String msg) {
                mView.showToast(msg);
            }
        });
    }

    @Override
    public void removeNewMessageBySenderIdPersenter(boolean isSendBroad) {
        mChatNetModel.removeNewMessageBySenderID(isSendBroad, new IChatNetModel.OnStaticBroadCaseListener() {
                    @Override
                    public void sendBroadCast(String action) {
                        mView.sendBroadCast(null, null, 0, action);
                    }
                }
        );
    }

    @Override
    public void upLoadFilesPersenter(ArrayList<String> paths) {
        mChatNetModel.upLoadFiles(paths, new IChatNetModel.OnObtainDataListener() {

            @Override
            public void refreshListView(List<ChatMsgBean> dataList, boolean isR) {
                mView.modifyMsgSateView(dataList, isR);
            }

            @Override
            public void sendBroadCastsListener(String broadCastName, String action, int id, String broadCastAction) {
                mView.sendBroadCast(broadCastName, action, id, broadCastAction);
            }
        });
    }

    @Override
    public void sendMessagePersenter(int type, ApiEntity.Message message, ChatMsgBean chatMsgBean, int dataListIndex, boolean isR) {
        mChatNetModel.sendMessage(type, message, chatMsgBean, dataListIndex, isR, new IChatNetModel.OnObtainDataListener() {

            @Override
            public void refreshListView(List<ChatMsgBean> dataList, boolean isR) {
                mView.modifyMsgSateView(dataList, isR);
            }

            @Override
            public void sendBroadCastsListener(String broadCastName, String action, int id, String broadCastAction) {
                mView.sendBroadCast(broadCastName, action, id, broadCastAction);
            }
        });
    }

    @Override
    public void initMessageListPersenter() {
        mChatNetModel.initMessageList(new IChatNetModel.onRefreshMessageListener() {
            @Override
            public void showRefreshListener() {
                mView.showRefreshView();
            }

            @Override
            public void refreshListViewListener(List<ChatMsgBean> dataList, int pageIndex, int pageSize) {
                mView.refreshListView(dataList, pageIndex, pageSize);
            }
        });
    }
}
