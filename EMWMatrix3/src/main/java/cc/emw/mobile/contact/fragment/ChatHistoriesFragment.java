package cc.emw.mobile.contact.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.melnykov.fab.FloatingActionButton;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.chat.model.bean.HistoryMessage;
import cc.emw.mobile.contact.ChatSelectActivity;
import cc.emw.mobile.contact.ContactFrament;
import cc.emw.mobile.contact.adapter.ChatHistoriesAdapter;
import cc.emw.mobile.contact.view.WrapContentLinearLayoutManager;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.CacheUtils;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.SwipeBackRecyclerView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import io.rong.callkit.CallSelectContactActivity;
import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * 会话列表
 *
 * @author tao.zhou
 */
@ContentView(R.layout.fragment_chat_histories)
public class ChatHistoriesFragment extends BaseFragment {

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @ViewInject(R.id.rlv_chat_history)
    private SwipeBackRecyclerView mAutoLoadRecyclerView;
    //    private AutoLoadRecyclerView mAutoLoadRecyclerView;
    @ViewInject(R.id.kongbai)
    private RelativeLayout mBlankLayout;

    public static final String ACTION_REFRESH_CHAT_HISTORY = "refresh_chat_lists";
    public static final String ACTION_CHAT_IS_BOTTOM = "chat_is_scroll_bottom";
    private List<HistoryMessage> mDatas = new ArrayList<>();    //所有消息的集合
    private List<UserInfo> mFollowLists = new ArrayList<>();    //我关注的人员集合
    private List<HistoryMessage> mSearchList = new ArrayList<>();   //搜索数据集合
    private ChatHistoriesAdapter chatHistoriesAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private MyBroadCastReceiver mReceiver;
    private FloatingActionButton mTvAddChat;
    private EditText mSearchEt; // 搜索框
    private boolean flag = true;
    private CharacterParser characterParser = CharacterParser.getInstance();

    class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_REFRESH_CHAT_HISTORY)) {
                getChatList();
            } else if (intent.getAction().equals(ACTION_CHAT_IS_BOTTOM)) {
                if (intent.hasExtra("enable")) {
                    mAutoLoadRecyclerView.setEnableGesture(false);
                } else {
                    mAutoLoadRecyclerView.setEnableGesture(true);
                    //mAutoLoadRecyclerView.updateSwipeBackState();
                }
            } else if (intent.getAction().equals(ContactFrament.ACTION_CONTACT_SEARCH)) {
                int currentItem = intent.getIntExtra("currentItem", -1);
                String s = intent.getStringExtra("keyword");
                if (currentItem == 0 && !TextUtils.isEmpty(s)) {
                    mSearchList.clear();
                    StringBuilder sb = new StringBuilder();
                    String name = "";
                    for (int i = 0; i < EMWApplication.mChatHistory.size();
                         i++) {
                        HistoryMessage historyMessage = EMWApplication.mChatHistory.get(i);
                        if (historyMessage.getType() == 2) {
                            GroupInfo info = EMWApplication.groupMap.get(historyMessage
                                    .getMessage().getGroupID());
                            if (info != null)
                                name = info.Name;
                        } else if (EMWApplication.personMap != null && EMWApplication.personMap.size() > 0) {
                            ApiEntity.UserInfo user = EMWApplication.personMap.get(historyMessage.getReceiverID());
                            if (user != null)
                                name = user.Name;
                        }
                        name = name.replaceAll(" ", "");
                        if (name != null) {
                            String selling = characterParser.getSelling(name.toLowerCase());
                            sb.delete(0, sb.length());
                            for (int j = 0; j < name.length(); j++) {
                                String substring = name.substring(j, j + 1);
                                substring = characterParser.convert(substring);
                                if (substring != null && substring.length() >= 1) {
                                    substring = substring.substring(0, 1);
                                    sb.append(substring);
                                }
                            }
                            if (name.contains(s.toString().trim()) || selling.contains(s.toString().trim().toLowerCase())
                                    || sb.toString().contains(s.toString().trim().toLowerCase())) {
                                if (EMWApplication.mChatHistory.get(i).getMessage() != null)
                                    mSearchList.add(EMWApplication.mChatHistory.get(i));
                            }
                        }
                    }
                    mDatas.clear();
                    mDatas.addAll(mSearchList);
                    chatHistoriesAdapter.notifyDataSetChanged();
                }
            } else if (intent.getAction().equals(ContactFrament.ACTION_CONTACT_SEARCH_BACK)) {
                int currentItem = intent.getIntExtra("currentItem", -1);
                String s = intent.getStringExtra("keyword");
                if (currentItem == 0 && TextUtils.isEmpty(s)) {
                    mDatas.clear();
                    mDatas.addAll(EMWApplication.mChatHistory);
                    chatHistoriesAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 10) {
                flag = false;
                getChatList();
                getFollowList(true);
            }
            if (flag) {
                flag = false;
                mPtrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrameLayout.autoRefresh(true);
                    }
                }, 100);
                mBlankLayout.setVisibility(View.VISIBLE);
                mAutoLoadRecyclerView.setVisibility(View.GONE);
            }
            switch (msg.what) {
                case 5:
                    if (msg.obj != null) {
                        mDatas.clear();
                        mDatas.addAll((Collection<? extends HistoryMessage>) msg.obj);
                        chatHistoriesAdapter.setMyFollowingList(mFollowLists);
                        chatHistoriesAdapter.notifyDataSetChanged();
                        EMWApplication.mChatHistory.clear();
                        EMWApplication.mChatHistory.addAll(mDatas);
                    }
                    if (mDatas.size() > 2 || mFollowLists.size() > 0) {
                        mAutoLoadRecyclerView.setVisibility(View.VISIBLE);
                        mBlankLayout.setVisibility(View.GONE);
                    } else {
                        mBlankLayout.setVisibility(View.VISIBLE);
                        mAutoLoadRecyclerView.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    public ChatHistoriesFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPtrFrameLayout();    //初始化下拉刷新
        init();
        mSearchEt = (EditText) getActivity().findViewById(R.id.et_search_keywords);
        mTvAddChat = (FloatingActionButton) view.findViewById(R.id.ic_tv_add_chat);
        mTvAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_chat_history, null);
                final PopupWindow window = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupView.findViewById(R.id.tv_history_video).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), CallSelectContactActivity.class);
                        intent.putExtra("has_oneself", false);
                        intent.putExtra("start_anim", false);
                        startActivityForResult(intent, 1110);
                        window.dismiss();
                    }
                });
                popupView.findViewById(R.id.tv_history_audio).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), CallSelectContactActivity.class);
                        intent.putExtra("has_oneself", false);
                        intent.putExtra("start_anim", false);
                        startActivityForResult(intent, 1100);
                        window.dismiss();
                    }
                });
                popupView.findViewById(R.id.tv_history_chat).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ChatSelectActivity.class);
                        intent.putExtra("start_anim", false);
                        startActivity(intent);
                        window.dismiss();
                    }
                });
                window.setOutsideTouchable(true);
                window.setFocusable(true);
//                window.setAnimationStyle(R.style.popup_more_anim);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupView.measure(0, 0);
                int xoff = -popupView.getMeasuredWidth() + v.getHeight();
                int yoff = -popupView.getMeasuredHeight() - v.getHeight() + DisplayUtil.dip2px(getActivity(), 10);
                window.showAsDropDown(v, xoff, yoff);
                window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        /*WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                        lp.alpha = 1f; //0.0-1.0
                        getActivity().getWindow().setAttributes(lp);
                        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));*/
                    }
                });
                /*WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 0.5f; //0.0-1.0
                getActivity().getWindow().setAttributes(lp);
                getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.cm_dialog_bg)));*/
            }
        });
        mTvAddChat.attachToRecyclerView(mAutoLoadRecyclerView);
    }

    private void init() {
        mReceiver = new MyBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_CHAT_HISTORY);
        filter.addAction(ACTION_CHAT_IS_BOTTOM);
        filter.addAction(ContactFrament.ACTION_CONTACT_SEARCH);
        filter.addAction(ContactFrament.ACTION_CONTACT_SEARCH_BACK);
        getActivity().registerReceiver(mReceiver, filter);
        chatHistoriesAdapter = new ChatHistoriesAdapter(getActivity(), mDatas);
        mAutoLoadRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAutoLoadRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置动画
        mAutoLoadRecyclerView.setAdapter(chatHistoriesAdapter);
        if (EMWApplication.mChatHistory.size() == 0) {
            readCache();
        } else {
            mDatas.clear();
            mDatas.addAll(EMWApplication.mChatHistory);
            getFollowList(true);
            if (mDatas.size() > 2 || mFollowLists.size() > 0) {
                mAutoLoadRecyclerView.setVisibility(View.VISIBLE);
                mBlankLayout.setVisibility(View.GONE);
            } else {
                mBlankLayout.setVisibility(View.VISIBLE);
                mAutoLoadRecyclerView.setVisibility(View.GONE);
            }
            chatHistoriesAdapter.setMyFollowingList(mFollowLists);
            chatHistoriesAdapter.notifyDataSetChanged();
            if (EMWApplication.onMessageReceive == 1) {
                EMWApplication.onMessageReceive = 0;
                getChatList();
                getOnlinePersons(); //获取在线人员
            }
        }

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAutoLoadRecyclerView.notifyScrollChangedListeners();
            }
        }, 1000);*/
    }

    /**
     * 读取缓存
     */
    private void readCache() {
        new Thread() {
            @Override
            public void run() {
                List<HistoryMessage> mDatas = CacheUtils.readObjectFile(PrefsUtil.readUserInfo().ID + "mDatas",
                        new TypeToken<List<HistoryMessage>>() {
                        }.getType());

                Message message1 = mHandler.obtainMessage();
                message1.what = 5;
                message1.obj = mDatas;
                if (mDatas != null && mDatas.size() > 0) {
                    message1.arg1 = 10;
                }
                mHandler.sendMessage(message1);
            }
        }.start();
    }

    private void initPtrFrameLayout() {

        final MaterialHeader header = new MaterialHeader(getActivity());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(getActivity(), 15));
        header.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setToggleMenu(true);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mAutoLoadRecyclerView, header);

            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getFollowList(false);
                getOnlinePersons(); //获取在线人员
            }
        });
    }

    /**
     * 得到我关注的联系人列表
     */
    private void getFollowList(boolean isFinish) {
        if (EMWApplication.personMap != null && EMWApplication.personMap.size() > 0) {
            mFollowLists.clear();
            for (int i = 0; i < EMWApplication.personMap.size(); i++) {
                UserInfo userInfo = EMWApplication.personMap.valueAt(i);
                if (userInfo.IsFollow == true && PrefsUtil.readUserInfo().ID != userInfo.ID) {
                    mFollowLists.add(userInfo);
                }
            }
            if (!isFinish)
                getChatList();
        }
    }

    /**
     * 获取聊天记录列表 HttpConstant.CHATHISTTROY_UR
     */
    private void getChatList() {
        API.Message.GetChatRecords(new RequestCallback<HistoryMessage>(
                HistoryMessage.class) {
            @Override
            public void onError(Throwable arg0, boolean arg1) {
                mPtrFrameLayout.refreshComplete();
                if (arg0 instanceof ConnectException) {
                    Toast.makeText(getActivity(), "网络异常,请检查网络", Toast.LENGTH_SHORT).show();
                }
//                if (arg0 instanceof ConnectException) {
//                  mNetworkTipsLayout.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onParseSuccess(final List<HistoryMessage> respList) {
                mPtrFrameLayout.refreshComplete();
                mSearchEt.setText("");
                mSearchEt.clearFocus();
                if (respList != null && respList.size() > 0) {
                    mDatas.clear();
                    EMWApplication.mChatHistory.clear();
                    List<HistoryMessage> mRecentDatas = new ArrayList<>();  //最近7天的聊天记录
                    List<HistoryMessage> mLongDatas = new ArrayList<>();    //7天前的聊天记录
                    for (HistoryMessage historyMessage : respList) {
                        if (historyMessage.getMessage() != null) {
                            if (StringUtils.isSevenDay(historyMessage.getMessage().getCreateTime())) {
                                mRecentDatas.add(historyMessage);
                            } else {
                                mLongDatas.add(historyMessage);
                            }
                        }
                    }
                    HistoryMessage message1 = new HistoryMessage();
                    message1.setType(-1);
                    mDatas.add(message1);
                    mDatas.addAll(mRecentDatas);
                    HistoryMessage message2 = new HistoryMessage();
                    message2.setType(-2);
                    mDatas.add(message2);
                    mDatas.addAll(mLongDatas);
                    EMWApplication.mChatHistory.addAll(mDatas);
                    chatHistoriesAdapter.setMyFollowingList(mFollowLists);
//                    chatHistoriesAdapter.setData(mDatas);
                    chatHistoriesAdapter.notifyDataSetChanged();
                    if (mDatas.size() > 2 || mFollowLists.size() > 0) {
                        mAutoLoadRecyclerView.setVisibility(View.VISIBLE);
                        mBlankLayout.setVisibility(View.GONE);
                    } else {
                        mBlankLayout.setVisibility(View.VISIBLE);
                        mAutoLoadRecyclerView.setVisibility(View.GONE);
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            CacheUtils.writeObjectFile(PrefsUtil.readUserInfo().ID + "mDatas", EMWApplication.mChatHistory);
                        }
                    }.start();
                }
            }
        });
    }

    /**
     * 获取在线人员列表
     */
    private void getOnlinePersons() {

        API.Message.GetOnlines(new RequestCallback<Integer>(Integer.class) {

            @Override
            public void onParseSuccess(List<Integer> respList) {
                if (respList != null) {
                    chatHistoriesAdapter.setOnLineList(respList);
                    chatHistoriesAdapter.notifyDataSetChanged();
                } else {
//                    ToastUtil.showToast(getActivity(), "获取在线人员失败");
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1100:
                case 1110:
                    ArrayList<UserInfo> selectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
                    StringBuilder disName = new StringBuilder();
                    final ArrayList<String> userIdList = new ArrayList<>();
                    userIdList.add(Integer.toString(PrefsUtil.readUserInfo().ID));
                    disName.append(PrefsUtil.readUserInfo().Name).append("、");
                    for (int i = 0, count = selectList.size(); i < count; i++) {
                        UserInfo userInfo = selectList.get(i);
                        userIdList.add(Integer.toString(userInfo.ID));
                        disName.append(userInfo.Name);
                        if (i != count - 1) {
                            disName.append("、");
                        }
                    }
                    RongIMClient.getInstance().createDiscussion(disName.toString(), userIdList, new RongIMClient.CreateDiscussionCallback() {
                        @Override
                        public void onSuccess(String s) {
                            Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_MULTIAUDIO);
                            if (requestCode == 1110) {
                                intent.setAction(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_MULTIVIDEO);
                            }
                            intent.putExtra("conversationType", Conversation.ConversationType.DISCUSSION.getName().toLowerCase());
                            intent.putExtra("targetId", s);
                            intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
                            intent.putStringArrayListExtra("invitedUsers", userIdList);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setPackage(getActivity().getPackageName());
                            getActivity().getApplicationContext().startActivity(intent);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            if (requestCode == 1110) {
                                ToastUtil.showToast(getActivity(), "创建视频聊天失败:" + errorCode.getValue());
                            } else {
                                ToastUtil.showToast(getActivity(), "创建语音聊天失败:" + errorCode.getValue());
                            }
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null)
            getActivity().unregisterReceiver(mReceiver);
    }
}
