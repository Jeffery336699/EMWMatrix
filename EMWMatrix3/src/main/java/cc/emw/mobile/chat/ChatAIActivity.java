package cc.emw.mobile.chat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.adapter.ChatAIMainAdapter;
import cc.emw.mobile.chat.view.EmoticonsEditText;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.KeyBoardUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by sunny.du on 2017/1/9.
 * 智能聊天机器人
 */
public class ChatAIActivity extends BaseActivity {
    private PtrFrameLayout mAILoadMoreListViewPtrFrame;
    private RecyclerView myRecyclerView;
    private EmoticonsEditText mEditText;
    private RelativeLayout mRlFinsh;
    private IconTextView mItvAiChatSendMsg;
    private ChatAIMainAdapter adapter;
    private List<Message> msgList;
    private LinearLayoutManager linearLayoutManager;
    private int pageIndex;
    private int pageSize;
    /***///智能聊天信息的实体类。后续确定替换
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);
        initData();
        initView();
        initEvent();
        initPtrFrameLayout();
        getAllMsg();
    }

    private void initView() {
        mAILoadMoreListViewPtrFrame = (PtrFrameLayout) findViewById(R.id.load_ai_more_list_view_ptr_frame);
        myRecyclerView = (RecyclerView) findViewById(R.id.chat_ai_lv_message);
        mEditText = (EmoticonsEditText) findViewById(R.id.eet_ai_chat_add_text);
        mRlFinsh = (RelativeLayout) findViewById(R.id.rl_finsh);
        mItvAiChatSendMsg = (IconTextView) findViewById(R.id.itv_ai_chat_send_msg);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setAdapter(adapter);
        MoveToPosition(linearLayoutManager,myRecyclerView,msgList.size()-1);
    }

    private void initEvent() {
        mItvAiChatSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendBtnClick(mEditText.getText().toString());
                mEditText.setText("");
            }
        });

        mRlFinsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardUtil.closeKeyboard(ChatAIActivity.this);
                onBackPressed();
            }
        });
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEditText.setFocusable(true);
                mEditText.setFocusableInTouchMode(true);//EditText与软键盘建立连接关系
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
                    }
                }, 100);
                return false;
            }
        });
        myRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        KeyBoardUtil.closeKeyboard(ChatAIActivity.this);
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        KeyBoardUtil.closeKeyboard(ChatAIActivity.this);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        KeyBoardUtil.closeKeyboard(ChatAIActivity.this);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /**
     * 发送消息中转方法
     *
     * @param msg
     */
    private void onSendBtnClick(String msg) {
        if (!TextUtils.isEmpty(msg.trim())) {
            Message message = new Message(0, msg, PrefsUtil.readUserInfo().ID, PrefsUtil.readUserInfo().ID, 0, 6, PrefsUtil.readUserInfo().CompanyCode, null,0,0);
            msgList.add(message);
            adapter.setData(msgList);
            adapter.notifyDataSetChanged();
            MoveToPosition(linearLayoutManager,myRecyclerView,msgList.size()-1);
            getAIMsg(msg);
        }
    }



    private void initData() {
        msgList = new ArrayList<>();
        adapter = new ChatAIMainAdapter(this, msgList);
        pageSize=20;
        pageIndex=0;
    }

    private void initPtrFrameLayout() {
        mAILoadMoreListViewPtrFrame = (PtrFrameLayout) findViewById(R.id.load_ai_more_list_view_ptr_frame);
        mAILoadMoreListViewPtrFrame.setPinContent(false);
        mAILoadMoreListViewPtrFrame.setLoadingMinTime(1000);
        mAILoadMoreListViewPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean check = PtrDefaultHandler.checkContentCanBePulledDown(frame, myRecyclerView, header);
                if (check) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    params.topMargin = DisplayUtil.dip2px(ChatAIActivity.this, 44);
                    mAILoadMoreListViewPtrFrame.setLayoutParams(params);
                } else {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    params.topMargin = DisplayUtil.dip2px(ChatAIActivity.this, 0);
                    mAILoadMoreListViewPtrFrame.setLayoutParams(params);
                }
                return check;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageIndex++;
                getAllMsg();
            }
        });
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
        header.setPtrFrameLayout(mAILoadMoreListViewPtrFrame);
        mAILoadMoreListViewPtrFrame.setLoadingMinTime(1000);
        mAILoadMoreListViewPtrFrame.setDurationToCloseHeader(1500);
        mAILoadMoreListViewPtrFrame.setHeaderView(header);
        mAILoadMoreListViewPtrFrame.addPtrUIHandler(header);
        mAILoadMoreListViewPtrFrame.autoRefresh();
    }


    private void getAIMsg(String msg) {
        if (HelpUtil.isNetworkAvailable(ChatAIActivity.this)) {
            API.Robot.GetResult(msg, new RequestCallback<String>(String.class) {
                @Override
                public void onSuccess(String message) {
                    Message msgBean = new Gson().fromJson(message, Message.class);
                    msgList.add(msgBean);
                    adapter.setData(msgList);
                    adapter.notifyDataSetChanged();
                    MoveToPosition(linearLayoutManager,myRecyclerView,msgList.size()-1);
                }

                @Override
                public void onParseSuccess(List<String> respList) {

                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                }
            });
        } else {
            ToastUtil.showToast(ChatAIActivity.this, R.string.tip_network_error, R.drawable.ico_del_red);
        }
    }

    /**
     * 获取所有消息列表
     */
    private void getAllMsg() {
        if (HelpUtil.isNetworkAvailable(ChatAIActivity.this)) {
            API.Robot.GetRecords(pageIndex, pageSize, new RequestCallback<Message>(Message.class) {
                @Override
                public void onParseSuccess(List<Message> respList) {
                    if (mAILoadMoreListViewPtrFrame != null) {
                        mAILoadMoreListViewPtrFrame.refreshComplete();
                    }
                    if (respList.size() != 0) {
                        msgList.addAll(0, respList);
                        adapter.setData(msgList);
                        adapter.notifyDataSetChanged();
                        MoveToPosition(linearLayoutManager,myRecyclerView,msgList.size()-1);
                    }
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    if (mAILoadMoreListViewPtrFrame != null) {
                        mAILoadMoreListViewPtrFrame.refreshComplete();
                        pageIndex--;
                    }
                }
            });
        }else{
            ToastUtil.showToast(ChatAIActivity.this, R.string.tip_network_error, R.drawable.ico_del_red);
        }
    }


    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager   设置RecyclerView对应的manager
     * @param mRecyclerView  当前的RecyclerView
     * @param n  要跳转的位置
     */
    private  void MoveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {
        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(n);
        }

    }
}


//    private RelativeLayout mRlAIChat;
//    private RelativeLayout mRlAIListView;
//    private RelativeLayout mRlAIUploader;
//    private RelativeLayout mRlAIHeaderBar;
//    private TextView mTvAIHeaderTvTitle;
//    private RelativeLayout mRLAIChatShow;
//    private UserInfo user;
//    private Message msg;
//    private boolean keybordStateFlag = false;//用于标记键盘的打开或者关闭  true打开   false关闭
//        mRlAIChat = (RelativeLayout) findViewById(R.id.rl_ai_chat);
//        mRlAIListView = (RelativeLayout) findViewById(R.id.rl_ai_list_view);
//        mRlAIUploader = (RelativeLayout) findViewById(R.id.rl_ai_uploader);
//        mRlAIHeaderBar = (RelativeLayout) findViewById(R.id.rl_ai_header_bar);
//        mTvAIHeaderTvTitle = (TextView) findViewById(R.id.tv_ai_header_tv_title);
//        mRLAIChatShow = (RelativeLayout) findViewById(R.id.rl_ai_chat_show);