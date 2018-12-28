package cc.emw.mobile.task.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.chat.base.NoDoubleClickListener;
import cc.emw.mobile.chat.model.bean.EmojiBean;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.TaskReply;
import cc.emw.mobile.task.adapter.CommentAdapter;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskCommentView;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.DiscussEmoticonsKeyBoard;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;

/**
 * Created by chengyong.liu on 2016/6/23.
 *
 * 讨论页面
 */
@ContentView(R.layout.activity_comment_3)
public class CommentActivity extends BaseActivity implements ITaskCommentView{
    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条左菜单按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.load_more_list_view_container)
    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.lv_task_comment_reply)
    private ListView mLvReply; // 任务详情评论列表
    @ViewInject(R.id.et_task_comment_reply)
    private EditText mEtReply;// 文本输入框
    @ViewInject(R.id.ll_task_blank)
    private LinearLayout mBlankLayout;// 空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout;// 无网络

    private static final int SAVE_REPLY = 1;// 保存评论
    private static final int DEL_REPLY_ = 2;// 删除评论
    private static final int GET_REPLY = 3;// 获取评论

    private ApiEntity.UserFenPai mUserFenPai;

    private TaskPresenter mTaskPresenter;
    private CommentAdapter mCommentAdapter;

    private int mReplyType = SAVE_REPLY;// 默认是添加评论
    private Dialog mLoadingDialog; // 加载框

    public static DiscussEmoticonsKeyBoard ekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        initData();
        initView();
        initPtrFrameLayout();
    }
    private void initData() {
        mTaskPresenter = new TaskPresenter(this);
        mUserFenPai = (ApiEntity.UserFenPai) getIntent().getSerializableExtra(TaskDetailActivity.TASK_DETAIL);
    }
    private void initView() {
        //title设置
//        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("讨论");
        ((IconTextView)findViewById(R.id.cm_header_btn_left9)).setIconText("eb68");
        findViewById(R.id.cm_header_tv_right9).setVisibility(View.GONE);
        //adapter
//        mCommentAdapter = new CommentAdapter(this, mEtReply);

        EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
            @Override
            public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
                if (isDelBtn) {
                    ChatUtils.delClick(ekBar.getEtChat());
                } else {
                    if (o == null) {
                        return;
                    }
                    if (actionType == 2) {
                        if (o instanceof EmoticonEntity) {
//						OnSendImage(((EmoticonEntity) o).getIconUri());
                        }
                    } else {
                        String content = null;
                        if (o instanceof EmojiBean) {
                            content = ((EmojiBean) o).emoji;
                        } else if (o instanceof EmoticonEntity) {
                            content = ((EmoticonEntity) o).getContent();
                        }
                        if (TextUtils.isEmpty(content)) {
                            return;
                        }
                        int index = ekBar.getEtChat().getSelectionStart();
                        Editable editable = ekBar.getEtChat().getText();
                        editable.insert(index, content);
                    }
                }
            }
        };
        ekBar = (DiscussEmoticonsKeyBoard) findViewById(R.id.root_view);
        ekBar.getRootView().setVisibility(View.VISIBLE);
        mKeyBoradView = ekBar.getRootView();
        ChatUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.setAdapter(ChatUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.getBtnSend().setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                String content = ekBar.getEtChat().getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(CommentActivity.this, R.string.empty_content_tips);
                } else {
                    String createTime = getCreateTime();
                    int creator = PrefsUtil.readUserInfo().ID;//获取当前用户
                    String reply = content;//输入框内容
                    TaskReply addReply=sendMsg(createTime,creator,reply);
                    if(addReply != null) {
                        mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
                        mLoadingDialog.show();
                        // 发送给服务器
                        mReplyType = SAVE_REPLY;
                        mTaskPresenter.saveTaskReply(addReply);
                    }
                }
            }
        });
        mCommentAdapter = new CommentAdapter(this, ekBar.getEtChat());
    }
    //加载更多设置
    private void initPtrFrameLayout() {
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                // 获取评论数据列表 根据任务ID
                mReplyType = GET_REPLY;
                mTaskPresenter .getTaskReply(mUserFenPai.ID);
            }
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mLvReply, header);
            }
        });
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
        header.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(false);
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    @Event({R.id.cm_header_btn_left9, R.id.cm_header_btn_right, R.id.ll_network_tips,R.id.btn_task_comment_send})
    private void click(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9://返回
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.cm_header_btn_right://查看侧表框     隐藏了
                Intent noticeIntent = new Intent(this, TestActivity.class);
                startActivity(noticeIntent);
                break;
            case R.id.ll_network_tips://网络加载错误  点击重试
                mPtrFrameLayout.autoRefresh();
                break;
            case R.id.btn_task_comment_send://添加评论
                String createTime = getCreateTime();
                int creator = PrefsUtil.readUserInfo().ID;//获取当前用户
                String reply = mEtReply.getText().toString().trim();//输入框内容
                TaskReply addReply=sendMsg(createTime,creator,reply);
                if(addReply != null) {
                    mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
                    mLoadingDialog.show();
                    // 发送给服务器
                    mReplyType = SAVE_REPLY;
                    mTaskPresenter.saveTaskReply(addReply);
                }
                break;
        }
    }
/****************************************调用底层代码请求网络处理数据的回调方法******************************************/
    @Override
    public void completeFresh() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void showReply(List<TaskReply> replyList) {
        mNetworkTipsLayout.setVisibility(View.GONE);
        if (replyList.size() == 0) {
            mBlankLayout.setVisibility(View.VISIBLE);
            mLvReply.setVisibility(View.GONE);
        } else {
            mBlankLayout.setVisibility(View.GONE);
            mLvReply.setVisibility(View.VISIBLE);
        }
        mCommentAdapter.setData(replyList);
        mCommentAdapter.notifyDataSetChanged();
        mLvReply.setAdapter(mCommentAdapter);
        // 置空类型
        mReplyType = -1;
    }
    @Override
    public void saveReply(ApiEntity.APIResult respInfo) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        // 评论成功返回的是评论值的ID
        // 将是否是子评论置为初始值 false
        PrefsUtil.setChildReply(false);
        if (respInfo != null && respInfo.State == 1) {
            ToastUtil.showToast(CommentActivity.this, getString(R.string.dynamicdetail_comment_success),
                    R.drawable.tishi_ico_gougou);
            mPtrFrameLayout.autoRefresh(false);
            // 清空EditText内容
            mEtReply.setText("");
            mEtReply.setHint(getString(R.string.dynamicdetail_edittext_hint));
            ekBar.getEtChat().setText("");
        } else {
            ToastUtil.showToast(CommentActivity.this, getString(R.string.dynamicdetail_comment_error));
        }
        // 置空类型
        mReplyType = -1;
    }
    @Override
    public void delReply(String s) {
        if ("1".equals(s)) {
            ToastUtil.showToast(this, "删除评论成功!", R.drawable.tishi_ico_gougou);
            mPtrFrameLayout.autoRefresh();
        } else {
            ToastUtil.showToast(CommentActivity.this, "删除评论失败！");
        }
        // 置空类型
        mReplyType = -1;
    }
    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        mPtrFrameLayout.refreshComplete();
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        switch (mReplyType) {
            case SAVE_REPLY:
                ToastUtil.showToast(CommentActivity.this, getString(R.string.dynamicdetail_comment_error));
                break;
            case DEL_REPLY_:
                ToastUtil.showToast(CommentActivity.this, "删除评论失败！");
                break;
            case GET_REPLY:
                if (ex instanceof ConnectException) {
                    mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
                }
                break;
            default:
                break;
        }
        // 置空类型
        mReplyType = -1;
    }
    @Override
    public void onBackPressed() {
        if (ekBar.getEtChat().getHint().toString().startsWith("回复")) {
            PrefsUtil.setChildReply(false);
            ekBar.getEtChat().setHint(R.string.dynamicdetail_edittext_hint);
        } else if (mEtReply.getHint().toString().startsWith("回复")) {
            PrefsUtil.setChildReply(false);
            mEtReply.setHint(R.string.dynamicdetail_edittext_hint);
        } else {
            super.onBackPressed();
        }
    }
/************************************生命周期方法*******************************************/
    @Override
    protected void onPause() {
        super.onPause();
        HelpUtil.hideSoftInput(CommentActivity.this, mEtReply);
    }
    @Override
    protected void onDestroy() {
        PrefsUtil.setChildReply(false);//重置评论初始状态
        HelpUtil.hideSoftInput(CommentActivity.this, mEtReply);
        super.onDestroy();
    }
/************************************业务逻辑方法*******************************************/
    /**
     *返回相应格式的时间
     */
    private String getCreateTime() {
        Date date = new Date();
        String pattern = getString(R.string.timeformat2);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        String createTime = simpleDateFormat.format(date);
        return createTime;
    }
    /**
     * 删除任务评论
     */
    public void delTaskReply(TaskReply reply) {
        mTaskPresenter.deleteTaskReply(reply.ID, mUserFenPai.ID);
    }
    /**
     * 处理评论对象
     */
    private TaskReply sendMsg(String createTime,int creator,String msg) {
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(CommentActivity.this, getString(R.string.empty_content_tips), Toast.LENGTH_SHORT).show();
            return null;
        }
        HelpUtil.hideSoftInput(CommentActivity.this, mEtReply);
        // 创建评论实体
        TaskReply addReply = new TaskReply();
        // 判断是对任务进行评论 还是通过点击回复进行子评论创建
        if (PrefsUtil.isChildReply() && mCommentAdapter.getChildReply() != null) {
            TaskReply childReply = mCommentAdapter.getChildReply();
            childReply.TaskId = mUserFenPai.ID;
            childReply.Content = msg;
            childReply.CreateTime = createTime;
            childReply.Creator = creator;
            addReply = childReply;
        } else {
            // 如果不是子评论 添加评论对象
            addReply.TaskId = mUserFenPai.ID;
            addReply.ID = 0;
            addReply.CreateTime = createTime;
            addReply.Creator = creator;
            addReply.Content = msg;
            addReply.ParentID = 0;
        }
        addReply.RType = 1;
        return addReply;
    }
}



//package cc.emw.mobile.task.activity;
//
//import android.app.Dialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.xutils.view.annotation.ContentView;
//import org.xutils.view.annotation.Event;
//import org.xutils.view.annotation.ViewInject;
//
//import java.net.ConnectException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//import cc.emw.mobile.R;
//import cc.emw.mobile.base.BaseActivity;
//import cc.emw.mobile.chat.TestActivity;
//import cc.emw.mobile.net.ApiEntity;
//import cc.emw.mobile.task.adapter.CommentAdapter;
//import cc.emw.mobile.task.presenter.TaskPresenter;
//import cc.emw.mobile.task.view.ITaskCommentView;
//import cc.emw.mobile.util.DisplayUtil;
//import cc.emw.mobile.util.HelpUtil;
//import cc.emw.mobile.util.PrefsUtil;
//import cc.emw.mobile.util.ToastUtil;
//import cc.emw.mobile.view.IconTextView;
//import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
//import in.srain.cube.views.ptr.PtrDefaultHandler;
//import in.srain.cube.views.ptr.PtrFrameLayout;
//import in.srain.cube.views.ptr.PtrHandler;
//import in.srain.cube.views.ptr.header.MaterialHeader;
//
///**
// * Created by chengyong.liu on 2016/6/23.
// */
//@ContentView(R.layout.activity_comment)
//public class CommentActivity extends BaseActivity implements ITaskCommentView, View.OnClickListener {
//    private static final String TAG = "CommentActivity";
//    @ViewInject(R.id.cm_header_btn_left)
//    private ImageButton mHeaderBackBtn; // 顶部条左菜单按钮
//    @ViewInject(R.id.cm_header_tv_title)
//    private TextView mHeaderTitleTv; // 顶部条标题
//
//
//    @ViewInject(R.id.load_more_list_view_ptr_frame)
//    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
//    @ViewInject(R.id.load_more_list_view_container)
//    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
//    @ViewInject(R.id.lv_task_comment_reply)
//    private ListView mLvReply; // 任务详情评论列表
//
//    @ViewInject(R.id.et_task_comment_reply)
//    private EditText mEtReply;// 文本输入框
//    @ViewInject(R.id.btn_task_comment_send)
//    private IconTextView mBtnSend;// 发送按钮
//
//    @ViewInject(R.id.ll_task_blank)
//    private LinearLayout mBlankLayout;// 空视图
//    @ViewInject(R.id.ll_network_tips)
//    private LinearLayout mNetworkTipsLayout;// 无网络
//
//
//    private TaskPresenter mTaskPresenter;
//    private CommentAdapter mCommentAdapter;
//
//    private Dialog mLoadingDialog; // 加载框
//
//    private static final int SAVE_REPLY = 1;// 保存评论
//    private static final int DEL_REPLY_ = 2;// 删除评论
//    private static final int GET_REPLY = 3;// 获取评论
//    private int mReplyType = SAVE_REPLY;// 默认是添加评论
//
//    private ApiEntity.UserFenPai mUserFenPai;
//
//    private int rootHeight = 0;//当前跟布局的高度。
//    LinearLayout.LayoutParams lp = new LinearLayout
//            .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        super.onCreate(savedInstanceState);
//        mTaskPresenter = new TaskPresenter(this);
//        mUserFenPai = (ApiEntity.UserFenPai) getIntent()
//                .getSerializableExtra(TaskDetailActivity.TASK_DETAIL);
//        //添加渲染完毕监听，获取view的高度
////        ViewTreeObserver observer = root.getViewTreeObserver();
////        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
////            @Override
////            public void onGlobalLayout() {
////                if (rootHeight == 0) {
////                    rootHeight = root.getMeasuredHeight();
////                }
////            }
////        });
//        initView();
//        initClick();
//    }
//
//    private void initView() {
////        root.setmOnSizeChangeListener(new ResizeLinearLayout.OnSizeChangeListener() {
////            @Override
////            public void onSizeChange(int h, int oldh) {
////
////                if (h > oldh || h == rootHeight) {
////                    //键盘隐藏的时候
////                    Log.d(TAG, "2h..." + h + "...oldh...." + oldh + "..rootheight.." + rootHeight);
////                    lp.setMargins(DisplayUtil.dip2px(CommentActivity.this, 12),
////                            DisplayUtil.dip2px(CommentActivity.this, 66),
////                            DisplayUtil.dip2px(CommentActivity.this, 12),
////                            DisplayUtil.dip2px(CommentActivity.this, 0));
////                } else {
////                    Log.d(TAG, "1h..." + h + "...oldh...." + oldh + "..rootheight.." + rootHeight);
////                    lp.setMargins(DisplayUtil.dip2px(CommentActivity.this, 12),
////                            DisplayUtil.dip2px(CommentActivity.this, 66),
////                            DisplayUtil.dip2px(CommentActivity.this, 12),
////                            DisplayUtil.dip2px(CommentActivity.this, 56));
////                }
////                Log.d(TAG, lp.bottomMargin+"..bootommargin");
////                change.setLayoutParams(lp);
////            }
////        });
////        mHeaderRightBtn.setVisibility(View.VISIBLE);
//        mHeaderBackBtn.setVisibility(View.VISIBLE);
//        mHeaderTitleTv.setVisibility(View.VISIBLE);
//        mHeaderTitleTv.setText("讨论");
//        mCommentAdapter = new CommentAdapter(this, mEtReply);
//        mPtrFrameLayout.setPinContent(false);
//        mPtrFrameLayout.setLoadingMinTime(1000);
//        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
//            @Override
//            public void onRefreshBegin(PtrFrameLayout frame) {
//                // 获取评论数据列表 根据任务ID
//                mReplyType = GET_REPLY;
//                mTaskPresenter
//                        .getTaskReply(mUserFenPai.ID);
//            }
//
//            @Override
//            public boolean checkCanDoRefresh(PtrFrameLayout frame,
//                                             View content, View header) {
//                // here check list view, not content.
//                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
//                        mLvReply, header);
//            }
//        });
//
//        // header
//        final MaterialHeader header = new MaterialHeader(this);
//        int[] colors = getResources().getIntArray(R.array.google_colors);
//        header.setColorSchemeColors(colors);
//        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
//        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
//        header.setPtrFrameLayout(mPtrFrameLayout);
//
//        mPtrFrameLayout.setLoadingMinTime(1000);
//        mPtrFrameLayout.setDurationToCloseHeader(1500);
//        mPtrFrameLayout.setHeaderView(header);
//        mPtrFrameLayout.addPtrUIHandler(header);
//
//        loadMoreListViewContainer.useDefaultFooter();
//        loadMoreListViewContainer.setAutoLoadMore(false);
//        refresh();
//    }
//
//    private void refresh() {
//        mPtrFrameLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPtrFrameLayout.autoRefresh(false);
//            }
//        }, 100);
//    }
//
//    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right, R.id.finish, R.id.ll_network_tips})
//    private void click(View v) {
//        switch (v.getId()) {
//            case R.id.cm_header_btn_left:
//                onBackPressed();
//                break;
//            case R.id.cm_header_btn_right:
//                Intent noticeIntent = new Intent(this, TestActivity.class);
//                startActivity(noticeIntent);
//                break;
//            case R.id.finish:
//                finish();
//                break;
//            case R.id.ll_network_tips:
//                mPtrFrameLayout.autoRefresh();
//                break;
//        }
//    }
//
//    private void initClick() {
//        mBtnSend.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        String createTime = getCreateTime();
//        int creator = PrefsUtil.readUserInfo().ID;
//        String reply = mEtReply.getText().toString().trim();
//        // 非空判断
//        if (TextUtils.isEmpty(reply)) {
//            Toast.makeText(CommentActivity.this, getString(R.string.empty_content_tips), Toast.LENGTH_SHORT).show();
//            return;
//        }
//        HelpUtil.hideSoftInput(CommentActivity.this, mEtReply);
//
//        // 创建评论实体
//        ApiEntity.TaskReply addReply = new ApiEntity.TaskReply();
//        // 判断是对任务进行评论 还是通过点击回复进行子评论创建
//        if (PrefsUtil.isChildReply()) {
//            // 如果是子评论
//            // 获取Adapter中评论对象，并进行赋值
//            // addReply.setTaskId(taskId);//任务ID在fragment中拿,其他字段在Adapter已经设置
//            // addReply.setContent(content)//内容在Fragment中拿,其他字段在Adapter已经设置
//            // addReply.setCreator(creator)//内容在Fragment中拿,其他字段在Adapter已经设置
//            // addReply.setCreateTime(createTime)//在fragment中设置,其他字段在Adapter已经设置
//            // addReply.setReplys(replys)//默认为空;
//            ApiEntity.TaskReply childReply = mCommentAdapter.getChildReply();
//            childReply.TaskId = mUserFenPai.ID;
//            childReply.Content = reply;
//            childReply.CreateTime = createTime;
//            childReply.Creator = creator;
//            addReply = childReply;
//        } else {
//            // 如果不是子评论
//            // 添加评论对象
//            addReply.TaskId = mUserFenPai.ID;
//            addReply.ID = 0;
//            addReply.CreateTime = createTime;
//            addReply.Creator = creator;
//            addReply.Content = reply;
//            addReply.ParentID = 0;
//        }
//        addReply.RType = 1;
//        mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
//        mLoadingDialog.show();
//        // 发送给服务器
//        mReplyType = SAVE_REPLY;
//        mTaskPresenter.saveTaskReply(addReply);
//    }
//
//    @Override
//    public void completeFresh() {
//        mPtrFrameLayout.refreshComplete();
//    }
//
//    @Override
//    public void showReply(List<ApiEntity.TaskReply> replyList) {
//        mNetworkTipsLayout.setVisibility(View.GONE);
//        if (replyList.size() == 0) {
//            mBlankLayout.setVisibility(View.VISIBLE);
//            mLvReply.setVisibility(View.GONE);
//        } else {
//            mBlankLayout.setVisibility(View.GONE);
//            mLvReply.setVisibility(View.VISIBLE);
//        }
//        mCommentAdapter.setData(replyList);
//        mCommentAdapter.notifyDataSetChanged();
//        mLvReply.setAdapter(mCommentAdapter);
//        // 置空类型
//        mReplyType = -1;
//    }
//
//    @Override
//    public void saveReply(ApiEntity.APIResult respInfo) {
//        if (mLoadingDialog != null)
//            mLoadingDialog.dismiss();
//        // 评论成功返回的是评论值的ID
//        // 将是否是子评论置为初始值 false
//        PrefsUtil.setChildReply(false);
//        if (respInfo != null && respInfo.State == 1) {
//            ToastUtil.showToast(CommentActivity.this, getString(R.string.dynamicdetail_comment_success),
//                    R.drawable.tishi_ico_gougou);
//            mPtrFrameLayout.autoRefresh(false);
//            // 清空EditText内容
//            mEtReply.setText("");
//            mEtReply.setHint(getString(R.string.dynamicdetail_edittext_hint));
//        } else {
//            ToastUtil.showToast(CommentActivity.this, getString(R.string.dynamicdetail_comment_error));
//        }
//        // 置空类型
//        mReplyType = -1;
//    }
//
//    @Override
//    public void delReply(String s) {
//        if ("1".equals(s)) {
//            ToastUtil.showToast(this, "删除评论成功!", R.drawable.tishi_ico_gougou);
//            mPtrFrameLayout.autoRefresh();
//        } else {
//            ToastUtil.showToast(CommentActivity.this, "删除评论失败！");
//        }
//        // 置空类型
//        mReplyType = -1;
//    }
//
//
//    @Override
//    public void onError(Throwable ex, boolean isOnCallback) {
//        mPtrFrameLayout.refreshComplete();
//        if (mLoadingDialog != null)
//            mLoadingDialog.dismiss();
//        switch (mReplyType) {
//            case SAVE_REPLY:
//                Log.d(TAG, "onError:" + ex.toString());
//                ToastUtil.showToast(CommentActivity.this, getString(R.string.dynamicdetail_comment_error));
//                break;
//            case DEL_REPLY_:
//                ToastUtil.showToast(CommentActivity.this, "删除评论失败！");
//                break;
//            case GET_REPLY:
//                if (ex instanceof ConnectException) {
//                    mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
//                }
//                break;
//            default:
//                break;
//        }
//        // 置空类型
//        mReplyType = -1;
//
//    }
//
//    private String getCreateTime() {
//        Date date = new Date();
////		String pattern = "yyyy/MM/dd HH:mm:ss";
//        String pattern = getString(R.string.timeformat2);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
//                Locale.CHINA);
//        String createTime = simpleDateFormat.format(date);
//        return createTime;
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (mEtReply.getHint().toString().startsWith("回复")) {
//            PrefsUtil.setChildReply(false);
//            mEtReply.setHint(R.string.dynamicdetail_edittext_hint);
//        } else {
//            scrollToFinishActivity();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        HelpUtil.hideSoftInput(CommentActivity.this, mEtReply);
//    }
//
//    @Override
//    protected void onDestroy() {
//        PrefsUtil.setChildReply(false);//重置评论初始状态
//        HelpUtil.hideSoftInput(CommentActivity.this, mEtReply);
//        super.onDestroy();
//    }
//
//    /**
//     * 删除任务评论
//     *
//     * @param reply
//     */
//    public void delTaskReply(ApiEntity.TaskReply reply) {
//        mTaskPresenter.deleteTaskReply(reply.ID, mUserFenPai.ID);
//    }
//
//}
