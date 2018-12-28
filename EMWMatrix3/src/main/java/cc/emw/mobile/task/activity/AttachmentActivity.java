package cc.emw.mobile.task.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.task.adapter.AttachmentAdapter;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.task.view.ItaskShareAttachmentView;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by chengyong.liu on 2016/6/24.
 * 共享文件操作类
 * 放弃引用本类   已经由RelationFileActivity替换本类    sunnydu
 */
@ContentView(R.layout.activity_attachment_2)
public class AttachmentActivity extends BaseActivity implements ITaskModifyView, ItaskShareAttachmentView {
    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.load_more_list_view_container)
    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.lv_task_attach)
    private ListView mListView;
    @ViewInject(R.id.ll_task_blank)
    private LinearLayout mBlankLayout;// 空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout;// 无网络
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_bar)
    private LinearLayout cm_header_bar;
    @ViewInject(R.id.cm_header_tv_right9)
    private IconTextView mTvRight9;
    @ViewInject(R.id.iv_task_attachement_add)
    private IconTextView mBtnAdd;
    @ViewInject(R.id.et_contact_search)
    private EditText mEtSearch;//搜索框
    public static final int SHARE_ATTACHMENT = 1;// 附件分享请求码
    public static final int ADD_ATTACHMENT = 10;// 添加附件的请求码
    public static final int DELFILE = 11;
    public static final int SHAREFILE = 12;
    public static final int ADDFILE = 13;
    public static final int GETFILE = 14;


    public static final String ATTACHMENT_BROADCAST = "cc.emw.mobile.attachment_broadcast";
    public static final String ATTACHMENT_NEW_OR_MODIFY = "attachment_new_or_modify";
    public static final int attachment_new = 21;//新建任务传来的标识
    public static final int attachment_modify = 22;//标记任务传来的标识

    private ArrayList<ApiEntity.Files> mSearchList = new ArrayList<>();//搜索列表
    private ArrayList<ApiEntity.Files> mFiles;//用于记录页面从服务器获取的附件列表对象
    private ArrayList<ApiEntity.Files> mTempFiles = new ArrayList<ApiEntity.Files>();

    private ApiEntity.UserInfo mCurrentUser;//当前用户
    private ApiEntity.Files mFile;// 用于记录需要移除的file文件
    private ApiEntity.UserFenPai mUserFenPai;

    private AttachmentAdapter mFileListAdapter;
    private AttachmentBroadcastReceive mReceive;
    private TaskPresenter mTaskPresenter;
    private Dialog mLoadingDialog; // 加载框

    private int mRequsetType;
    private int mOperateFile;// 用于标记对附件操作的类型
    private String mLastFiles;// 记录每次修改之前的附件信息
    private boolean limit = false;//负责人权限 当前用户属于负责人，具有权限 为true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
        initReceiver();//初始化广播  用于接收广播
        loadData();//加载必要的数据
        initView();//初始化视图
        initPtrFrameLayout();//初始化下拉刷新
        initListener();//初始化监听器   设置listview、搜索功能的监听
    }
    /**
     * 注册广播用于接收
     */
    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ATTACHMENT_BROADCAST);
        mReceive = new AttachmentBroadcastReceive();
        registerReceiver(mReceive, intentFilter);
    }
    /**
     * 加载数据
     */
    private void loadData() {
        mCurrentUser = EMWApplication.personMap.get(PrefsUtil.readUserInfo().ID);
        mUserFenPai = (ApiEntity.UserFenPai) getIntent().getSerializableExtra(TaskDetailActivity.TASK_DETAIL);
        mRequsetType = getIntent().getIntExtra(ATTACHMENT_NEW_OR_MODIFY, -1);//标志请求类型
        mTaskPresenter = new TaskPresenter(this);
        //操作任务的权限
        limit = loadPawer();
    }
    /**
     * 初始化视图
     */
    private void initView() {
        //判断任务状态，如果非进行状态则隐藏确定按钮
        if (mRequsetType == -1) {
            mTvRight9.setVisibility(View.GONE);
        }
        //判断是否显示文件添加按钮
        if (!limit && mRequsetType == -1||mUserFenPai.State == TaskConstant.TaskState.FINISHED) {
            mBtnAdd.setVisibility(View.GONE);
        }
        //设置title
        cm_header_bar.setBackgroundResource(R.drawable.trans_bg);
        mHeaderTitleTv.setText("任务附件");
        //listView
        mFileListAdapter = new AttachmentAdapter(this, limit);
        mListView.setAdapter(mFileListAdapter);
    }
    /**
     * 启动下拉刷新
     */
    private void initPtrFrameLayout() {
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mOperateFile = GETFILE;
                mTaskPresenter.getFileListByIds(mUserFenPai.Files);
            }
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,   View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
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
        //下拉刷新
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ApiEntity.Files file = mFileListAdapter.getDataList().get(position);
                String localPath = EMWApplication.filePath + file.Url;
                FileUtil.openFile(AttachmentActivity.this, localPath);
            }
        });
        /**增加搜索功能  */
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mFileListAdapter.setDataList(mFiles);
                mFileListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchList.clear();
                StringBuilder sb = new StringBuilder();
                for (int i = 0, size = mFiles.size(); i < size; i++) {
                    ApiEntity.Files file = mFiles.get(i);
                    if (file.Name != null) {
                        CharacterParser characterParser = CharacterParser.getInstance();
                        String selling = characterParser.getSelling(file.Name.toLowerCase());
                        sb.delete(0, sb.length());
                        for (int j = 0; j < file.Name.length(); j++) {
                            String substring = file.Name.substring(j, j + 1);
                            substring = characterParser.convert(substring);
                            substring = substring.substring(0, 1);
                            sb.append(substring);
                        }
                        if (file.Name.contains(s) || selling.contains(s.toString().toLowerCase()) || sb.toString().contains(s.toString().toLowerCase())) {
                            mSearchList.add(mFiles.get(i));
                        }
                    }
                }
                refreshView(mSearchList);
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mPtrFrameLayout.setEnabled(true);
                } else {
                    mPtrFrameLayout.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Event({R.id.cm_header_btn_left9,R.id.iv_task_attachement_add,R.id.cm_header_tv_right9,R.id.finish,R.id.ll_network_tips})
    private void onclick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9://取消按钮
                onBackPressed();
                break;
            case R.id.iv_task_attachement_add://添加附件按钮
                Intent intent = new Intent(AttachmentActivity.this, FileSelectActivity.class);
                if (mUserFenPai.Files != null && mUserFenPai.Files.equals("")) {
                    mUserFenPai.Files = "[]";
                }
                intent.putExtra(FileSelectActivity.EXTRA_SELECT_IDS,mUserFenPai.Files);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivityForResult(intent, ADD_ATTACHMENT);
                break;
            case R.id.cm_header_tv_right9://提交按钮
                //回传数据
                Intent result = new Intent();
                ArrayList<ApiEntity.Files> dataList = (ArrayList<ApiEntity.Files>) mFileListAdapter.getDataList();
                result.putExtra("attachment_result", dataList);
                setResult(Activity.RESULT_OK, result);
                finish();
                break;
            case R.id.ll_network_tips://重新加载按钮
                mPtrFrameLayout.autoRefresh();
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 返回数据，将其添加到列表中
        switch (requestCode) {
            case ADD_ATTACHMENT:
                if (resultCode == Activity.RESULT_OK) {
                    mOperateFile = ADDFILE;
                    // 获取返回成功数据
                    ArrayList<ApiEntity.Files> list = (ArrayList<ApiEntity.Files>) data .getSerializableExtra("select_list");
                    mTempFiles.clear();
                    mTempFiles.addAll(list);
                    mLastFiles = mUserFenPai.Files;// 记录修改之前的文件信息
                    mUserFenPai.Files = TaskUtils.getRepositoryArray(list);
                    if (mRequsetType == -1) {
                        mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
                        mLoadingDialog.show();
                        // 发起修改任务
                        mTaskPresenter.modifyTask(mUserFenPai);
                    } else {
                        //新建或者修改任务界面进来
                        mPtrFrameLayout.autoRefresh();
                    }
                }
                break;
            case SHARE_ATTACHMENT:
                if (resultCode == Activity.RESULT_OK) {
                    mOperateFile = SHAREFILE;
                    int fileID = mFileListAdapter.getFileID();
                    ArrayList<ApiEntity.UserInfo> userRets = (ArrayList<ApiEntity.UserInfo>) data .getSerializableExtra("select_list");
                    // 用户集合
                    ArrayList<ApiEntity.UserFilePower> upList = new ArrayList<ApiEntity.UserFilePower>();
                    for (ApiEntity.UserInfo UserInfo : userRets) {
                        ApiEntity.UserFilePower upData = new ApiEntity.UserFilePower();
                        upData.ID = UserInfo.ID;
                        upData.Power = 1;
                        upData.Type = 1;
                        upList.add(upData);
                    }
                    mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
                    mLoadingDialog.show();
                    mTaskPresenter.shareAttachment(upList, fileID);
                }
                break;
            default:
                break;
        }
    }
    class AttachmentBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ATTACHMENT_BROADCAST.equals(intent.getAction())) {
                // 接收广播
                mUserFenPai = (ApiEntity.UserFenPai) intent .getSerializableExtra(TaskConstant.SEND_USERFENPAI);
                mPtrFrameLayout.autoRefresh();
            }
        }
    }
    /***********************************************生命周期方法*******************************************************************/
    @Override
    public void onDestroy() {
        if (mRequsetType == -1) {
            if (mReceive != null)
                unregisterReceiver(mReceive);
        }
        super.onDestroy();
    }
    /***********************************************调用底层接口请求网络*******************************************************************/
    @Override
    public void modifyTask(String s) {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        if ("1".equals(s)) {
            switch (mOperateFile) {
                case DELFILE:
                    mFileListAdapter.getDataList().remove(mFile);
                    mFileListAdapter.notifyDataSetChanged();
                    ToastUtil.showToast(this, getString(R.string.deletefile_success), R.drawable.tishi_ico_gougou);
                    break;
                case ADDFILE:
                    if (mFileListAdapter.getLastPosition() != -1) {
                        mFileListAdapter.setLastPosition();
                    }
                    mFileListAdapter.setDataList(mTempFiles);
                    mFileListAdapter.notifyDataSetChanged();
                    ToastUtil.showToast(this, getString(R.string.task_attachment_add_success),R.drawable.tishi_ico_gougou);
                    break;
                default:
                    break;
            }
//            发送广播
            Intent intent = new Intent();
            intent.setAction(ATTACHMENT_BROADCAST);
            intent.putExtra(TaskConstant.SEND_USERFENPAI, mUserFenPai);
            sendBroadcast(intent);
        } else {
            switch (mOperateFile) {
                case DELFILE:
                    mUserFenPai.Files = mLastFiles;
                    ToastUtil.showToast(this, getString(R.string.deletefile_error));
                    break;
                case ADDFILE:
                    // 还原请求前的附件信息
                    mUserFenPai.Files = mLastFiles;
                    ToastUtil.showToast(this, getString(R.string.task_attachment_add_error));
                    break;
                default:
                    break;
            }
        }
        // 置空标记
        mOperateFile = 0;
    }
    @Override
    public void shareAttachmentSuccess(String s) {
        // 任务分享成功的回调
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        if ("1".equals(s)) {
            ToastUtil.showToast(this, getString(R.string.file_share_success),R.drawable.tishi_ico_gougou);
        } else {
            ToastUtil.showToast(this, getString(R.string.file_share_error));
        }
    }
    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        mPtrFrameLayout.refreshComplete();
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();

        switch (mOperateFile) {
            case DELFILE:
                mUserFenPai.Files = mLastFiles;
                ToastUtil.showToast(this, getString(R.string.deletefile_error));
                break;
            case ADDFILE:
                mUserFenPai.Files = mLastFiles;
                ToastUtil.showToast(this, getString(R.string.task_attachment_add_error));
                break;
            case SHAREFILE:
                ToastUtil.showToast(this, getString(R.string.file_share_error));
                break;
            case GETFILE:
                if (ex instanceof ConnectException) {
                    mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
                }
                break;
            default:
                break;
        }
        // 置空标记
        mOperateFile = 0;
    }
    @Override
    public void getFileList(List<ApiEntity.Files> files) {
        mFiles = (ArrayList) files;
        refreshView(files);
    }
    @Override
    public void createTask(String respInfo) {
    }


/*****************************************附件业务逻辑方法***************************************************/
    /**
     * 修改任务附件属性
     */
    public void deleteFile(ApiEntity.Files file, ArrayList<ApiEntity.Files> list) {
        mLastFiles = mUserFenPai.Files;
        mUserFenPai.Files = TaskUtils.getRepositoryArray(list);
        mOperateFile = DELFILE;
        mFile = file;
        mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
        mLoadingDialog.show();
        mTaskPresenter.modifyTask(mUserFenPai);
    }
    /**
     * 刷新视图
     * @param files
     */
    private void refreshView(List<ApiEntity.Files> files) {
        mNetworkTipsLayout.setVisibility(View.GONE);
        if (files != null && files.size() == 0) {
            mBlankLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mBlankLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
        // 设置数据
        mFileListAdapter.setRequestType(mRequsetType);
        mFileListAdapter.setDataList(files);
        mFileListAdapter.notifyDataSetChanged();
    }

    public ListView getmListView() {
        return mListView;
    }
    public LinearLayout getmBlankLayout() {
        return mBlankLayout;
    }
    @Override
    public void completeFresh() {
        mPtrFrameLayout.refreshComplete();
    }
    /**
     * 判断当前用户是否有权限操作任务
     */
    private boolean loadPawer() {
        boolean pawer;
        //设置负责人
        String mainUser = mUserFenPai.MainUser;
        List<ApiEntity.UserInfo> mainUsers = TaskUtils.getUsers(mainUser);
        //执行人
        String moreUser = mUserFenPai.MoreUser;
        List<ApiEntity.UserInfo> moreUsers = TaskUtils.getUsers((moreUser));
        if (mainUsers.contains(mCurrentUser) || mCurrentUser.ID == mUserFenPai.Creator || moreUsers.contains(mCurrentUser)) {
            pawer = true;
        } else {
            pawer = false;
        }
        return pawer;
    }
}




//package cc.emw.mobile.task.activity;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import org.xutils.view.annotation.ContentView;
//import org.xutils.view.annotation.Event;
//import org.xutils.view.annotation.ViewInject;
//
//import java.net.ConnectException;
//import java.util.ArrayList;
//import java.util.List;
//
//import cc.emw.mobile.EMWApplication;
//import cc.emw.mobile.R;
//import cc.emw.mobile.base.BaseActivity;
//import cc.emw.mobile.file.FileSelectActivity;
//import cc.emw.mobile.net.ApiEntity;
//import cc.emw.mobile.task.adapter.AttachmentAdapter;
//import cc.emw.mobile.task.constant.TaskConstant;
//import cc.emw.mobile.task.presenter.TaskPresenter;
//import cc.emw.mobile.task.view.ITaskModifyView;
//import cc.emw.mobile.task.view.ItaskShareAttachmentView;
//import cc.emw.mobile.util.CharacterParser;
//import cc.emw.mobile.util.DisplayUtil;
//import cc.emw.mobile.util.FileUtil;
//import cc.emw.mobile.util.PrefsUtil;
//import cc.emw.mobile.util.TaskUtils;
//import cc.emw.mobile.util.ToastUtil;
//import cc.emw.mobile.view.IconTextView;
//import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
//import in.srain.cube.views.ptr.PtrDefaultHandler;
//import in.srain.cube.views.ptr.PtrFrameLayout;
//import in.srain.cube.views.ptr.PtrHandler;
//import in.srain.cube.views.ptr.header.MaterialHeader;
//
///**
// * Created by chengyong.liu on 2016/6/24.
// */
//@ContentView(R.layout.activity_attachment_2)
//public class AttachmentActivity extends BaseActivity implements ITaskModifyView, ItaskShareAttachmentView {
//    public static final String ATTACHMENT_BROADCAST = "cc.emw.mobile.attachment_broadcast";
//    public static final String ATTACHMENT_NEW_OR_MODIFY = "attachment_new_or_modify";
//    public static final int attachment_new = 21;//新建任务传来的标识
//    public static final int attachment_modify = 22;//标记任务传来的标识
//    private static final String TAG = "AttachmentActivity";
//    private int mRequsetType;
//    @ViewInject(R.id.load_more_list_view_ptr_frame)
//    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
//    @ViewInject(R.id.load_more_list_view_container)
//    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
//    @ViewInject(R.id.lv_task_attach)
//    private ListView mListView;
//
//    @ViewInject(R.id.ll_task_blank)
//    private LinearLayout mBlankLayout;// 空视图
//    @ViewInject(R.id.ll_network_tips)
//    private LinearLayout mNetworkTipsLayout;// 无网络
//    @ViewInject(R.id.cm_header_tv_title)
//    private TextView mHeaderTitleTv; // 顶部条标题
//    @ViewInject(R.id.cm_header_bar)
//    private LinearLayout cm_header_bar;
//    @ViewInject(R.id.cm_header_tv_right9)
//    private IconTextView mTvRight9;
//    @ViewInject(R.id.iv_task_attachement_add)
//    private IconTextView mBtnAdd;
//    @ViewInject(R.id.et_contact_search)
//    private EditText mEtSearch;//搜索框
//
//    private ArrayList<ApiEntity.Files> mSearchList = new ArrayList<>();//搜索列表
//    private ArrayList<ApiEntity.Files> mFiles;//用于记录页面从服务器获取的附件列表对象
//
//    private AttachmentAdapter mFileListAdapter;
//    private ApiEntity.UserFenPai mUserFenPai;
//    private AttachmentBroadcastReceive mReceive;
//    private TaskPresenter mTaskPresenter;
//
//    public static final int SHARE_ATTACHMENT = 1;// 附件分享请求码
//    public static final int ADD_ATTACHMENT = 10;// 添加附件的请求码
//
//    public static final int DELFILE = 11;
//    public static final int SHAREFILE = 12;
//    public static final int ADDFILE = 13;
//    public static final int GETFILE = 14;
//
//    private int mOperateFile;// 用于标记对附件操作的类型
//    private ApiEntity.Files mFile;// 用于记录需要移除的file文件
//    private ArrayList<ApiEntity.Files> mTempFiles = new ArrayList<ApiEntity.Files>();
//    private Dialog mLoadingDialog; // 加载框
//    private String mLastFiles;// 记录每次修改之前的附件信息
//    private boolean limit = false;//负责人权限 当前用户属于负责人，具有权限 为true
//    private ApiEntity.UserInfo mCurrentUser;//当前用户
//
//    // private UserFenPai mTempUserFenpai = new UserFenPai();
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mCurrentUser = EMWApplication.personMap
//                .get(PrefsUtil.readUserInfo().ID);
//        mUserFenPai = (ApiEntity.UserFenPai) getIntent()
//                .getSerializableExtra(TaskDetailActivity.TASK_DETAIL);
//        mRequsetType = getIntent().getIntExtra(ATTACHMENT_NEW_OR_MODIFY, -1);//标志请求类型
//        mTaskPresenter = new TaskPresenter(this);
//        initView();
//        if (mRequsetType != -1) {
//            //新建或者修改任务界面跳转进来的
//            //mHeaderRightTv.setVisibility(View.VISIBLE);
//            //mHeaderRightTv.setText("完成");
//        } else {
//            //任务详情跳转
////            cm_header_bar2.setVisibility(View.INVISIBLE);
//            mTvRight9.setVisibility(View.GONE);
//        }
//        // 注册广播用于接收
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ATTACHMENT_BROADCAST);
//        mReceive = new AttachmentBroadcastReceive();
//        registerReceiver(mReceive, intentFilter);
//
//        mPtrFrameLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPtrFrameLayout.autoRefresh(false);
//            }
//        }, 100);
//    }
//
//    private void initView() {
//        //设置负责人
//        String mainUser = mUserFenPai.MainUser;
//        List<ApiEntity.UserInfo> mainUsers = TaskUtils.getUsers(mainUser);
//        String moreUser = mUserFenPai.MoreUser;
//        List<ApiEntity.UserInfo> moreUsers = TaskUtils.getUsers((moreUser));
//        if (mainUsers.contains(mCurrentUser) || mCurrentUser.ID == mUserFenPai.Creator
//                || moreUsers.contains(mCurrentUser)) {
//            limit = true;
//        } else {
//            limit = false;
//        }
//        //展示是否显示添加按钮
//        if (!limit && mRequsetType == -1) {
//            mBtnAdd.setVisibility(View.GONE);
//        }
//        cm_header_bar.setBackgroundResource(R.drawable.trans_bg);
//        mHeaderTitleTv.setText("任务附件");
//        mFileListAdapter = new AttachmentAdapter(this, limit);
//        mListView.setAdapter(mFileListAdapter);
//        mPtrFrameLayout.setPinContent(false);
//        mPtrFrameLayout.setLoadingMinTime(1000);
//        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
//            @Override
//            public void onRefreshBegin(PtrFrameLayout frame) {
//                mOperateFile = GETFILE;
////                String ids = getIds(mUserFenPai.Files);
////                Log.d(TAG,"muserfenpai.."+mUserFenPai.Files+"...."+ids+"..");
//                mTaskPresenter.getFileListByIds(mUserFenPai.Files);
//            }
//
//            @Override
//            public boolean checkCanDoRefresh(PtrFrameLayout frame,
//                                             View content, View header) {
//                // here check list view, not content.
//                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
//                        mListView, header);
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
//
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                ApiEntity.Files file = mFileListAdapter.getDataList().get(position);
////                String name = file.ID + FileUtil.getExtension(file.Name);
////                FileUtil.openFile(AttachmentActivity.this, EMWApplication.filePath + name);
//                String localPath = EMWApplication.filePath + file.Url;
//                FileUtil.openFile(AttachmentActivity.this, localPath);
//            }
//        });
//        /**增加搜索功能  */
//        mEtSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                mFileListAdapter.setDataList(mFiles);
//                mFileListAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mSearchList.clear();
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0, size = mFiles.size(); i < size; i++) {
//                    ApiEntity.Files file = mFiles.get(i);
//                    if (file.Name != null) {
//                        CharacterParser characterParser = CharacterParser.getInstance();
//                        String selling = characterParser.getSelling(file.Name.toLowerCase());
//                        sb.delete(0, sb.length());
//                        for (int j = 0; j < file.Name.length(); j++) {
//                            String substring = file.Name.substring(j, j + 1);
//                            substring = characterParser.convert(substring);
//                            substring = substring.substring(0, 1);
//                            sb.append(substring);
//                        }
//                        if (file.Name.contains(s) || selling.contains(s.toString().toLowerCase()) || sb.toString().contains(s.toString().toLowerCase())) {
//                            mSearchList.add(mFiles.get(i));
//                        }
//                    }
//                }
//                refreshView(mSearchList);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (TextUtils.isEmpty(s)) {
//                    mPtrFrameLayout.setEnabled(true);
//                } else {
//                    mPtrFrameLayout.setEnabled(false);
//                }
//            }
//        });
//    }
//
//    @Event({R.id.cm_header_btn_left9,
//            R.id.iv_task_attachement_add,
//            R.id.cm_header_tv_right9,
//            R.id.finish,
//            R.id.ll_network_tips})
//    private void onclick(View v) {
//        switch (v.getId()) {
//            case R.id.cm_header_btn_left9:
//                finish();
//                break;
//            case R.id.iv_task_attachement_add:
////                if (mRequsetType == -1 && !limit) {
////                    //任务详情跳转过来
////                    ToastUtil.showToast(AttachmentActivity.this, "你没有添加附件的权限!");
////                    return;
////                }
//                //跳转到附件选择界面
//                Intent intent = new Intent(AttachmentActivity.this,
//                        FileSelectActivity.class);
//                if (mUserFenPai.Files != null && mUserFenPai.Files.equals("")) {
//                    mUserFenPai.Files = "[]";
//                }
//                intent.putExtra(FileSelectActivity.EXTRA_SELECT_IDS,
//                        mUserFenPai.Files);
//                startActivityForResult(intent, ADD_ATTACHMENT);
//                break;
//            case R.id.cm_header_tv_right9:
//                //回传数据
//                Intent result = new Intent();
//                ArrayList<ApiEntity.Files> dataList = (ArrayList<ApiEntity.Files>) mFileListAdapter.getDataList();
//                result.putExtra("attachment_result", dataList);
//                setResult(Activity.RESULT_OK, result);
//                finish();
//                break;
////            case R.id.finish:
////                finish();
////                break;
//            case R.id.ll_network_tips:
//                mPtrFrameLayout.autoRefresh();
//                break;
//        }
//    }
//
//    private String getIds(String files) {
//        if (files != null && !files.equals("") && !files.equals("[]")) {
//            return files.substring(1, files.length() - 1);
//        } else {
//            return "";
//        }
//    }
//
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // 返回数据，将其添加到列表中
//        switch (requestCode) {
//            case ADD_ATTACHMENT:
//                if (resultCode == Activity.RESULT_OK) {
//                    mOperateFile = ADDFILE;
//                    // 获取返回成功数据
//                    ArrayList<ApiEntity.Files> list = (ArrayList<ApiEntity.Files>) data
//                            .getSerializableExtra("select_list");
//                    mTempFiles.clear();
//                    mTempFiles.addAll(list);
//                    mLastFiles = mUserFenPai.Files;// 记录修改之前的文件信息
//                    mUserFenPai.Files = TaskUtils.getRepositoryArray(list);
//                    if (mRequsetType == -1) {
//                        mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
//                        mLoadingDialog.show();
//                        // 发起修改任务
//                        mTaskPresenter.modifyTask(mUserFenPai);
//                    } else {
//                        //新建或者修改任务界面进来
//                        mPtrFrameLayout.autoRefresh();
//                    }
//                }
//                break;
//
//            case SHARE_ATTACHMENT:
//                if (resultCode == Activity.RESULT_OK) {
//                    mOperateFile = SHAREFILE;
//                    int fileID = mFileListAdapter.getFileID();
//                    ArrayList<ApiEntity.UserInfo> userRets = (ArrayList<ApiEntity.UserInfo>) data
//                            .getSerializableExtra("select_list");
//                    // 用户集合
//                    ArrayList<ApiEntity.UserFilePower> upList = new ArrayList<ApiEntity.UserFilePower>();
//                    for (ApiEntity.UserInfo UserInfo : userRets) {
//                        ApiEntity.UserFilePower upData = new ApiEntity.UserFilePower();
//                        upData.ID = UserInfo.ID;
//                        upData.Power = 1;
//                        upData.Type = 1;
//                        upList.add(upData);
//                    }
//                    mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
//                    mLoadingDialog.show();
//                    mTaskPresenter.shareAttachment(upList, fileID);
//                }
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    class AttachmentBroadcastReceive extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (ATTACHMENT_BROADCAST.equals(intent.getAction())) {
//                // 接收广播
//                mUserFenPai = (ApiEntity.UserFenPai) intent
//                        .getSerializableExtra(TaskConstant.SEND_USERFENPAI);
//                mPtrFrameLayout.autoRefresh();
//            }
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        if (mRequsetType == -1) {
//            if (mReceive != null)
//                unregisterReceiver(mReceive);
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    public void createTask(String respInfo) {
//
//    }
//
//    @Override
//    public void modifyTask(String s) {
//        if (mLoadingDialog != null)
//            mLoadingDialog.dismiss();
//        if ("1".equals(s)) {
//            switch (mOperateFile) {
//                case DELFILE:
//                    mFileListAdapter.getDataList().remove(mFile);
//                    mFileListAdapter.notifyDataSetChanged();
//                    ToastUtil.showToast(this, getString(R.string.deletefile_success),
//                            R.drawable.tishi_ico_gougou);
//                    break;
//                case ADDFILE:
//                    if (mFileListAdapter.getLastPosition() != -1) {
//                        mFileListAdapter.setLastPosition();
//                    }
//                    mFileListAdapter.setDataList(mTempFiles);
//                    mFileListAdapter.notifyDataSetChanged();
//                    ToastUtil.showToast(this, getString(R.string.task_attachment_add_success),
//                            R.drawable.tishi_ico_gougou);
//                    break;
//                default:
//                    break;
//            }
////            发送广播
//            Intent intent = new Intent();
//            intent.setAction(ATTACHMENT_BROADCAST);
//            intent.putExtra(TaskConstant.SEND_USERFENPAI, mUserFenPai);
//            sendBroadcast(intent);
//        } else {
//
//            switch (mOperateFile) {
//                case DELFILE:
//                    mUserFenPai.Files = mLastFiles;
//                    ToastUtil.showToast(this, getString(R.string.deletefile_error));
//                    break;
//                case ADDFILE:
//                    // 还原请求前的附件信息
//                    mUserFenPai.Files = mLastFiles;
//                    ToastUtil.showToast(this, getString(R.string.task_attachment_add_error));
//                    break;
//                default:
//                    break;
//            }
//        }
//        // 置空标记
//        mOperateFile = 0;
//    }
//
//    /**
//     * 修改任务附件属性
//     */
//    public void deleteFile(ApiEntity.Files file, ArrayList<ApiEntity.Files> list) {
//        mLastFiles = mUserFenPai.Files;
//        mUserFenPai.Files = TaskUtils.getRepositoryArray(list);
//        mOperateFile = DELFILE;
//        mFile = file;
//        mLoadingDialog = createLoadingDialog(getString(R.string.loading_dialog_tips3));
//        mLoadingDialog.show();
//        mTaskPresenter.modifyTask(mUserFenPai);
//    }
//
//    @Override
//    public void shareAttachmentSuccess(String s) {
//        // 任务分享成功的回调
//        if (mLoadingDialog != null)
//            mLoadingDialog.dismiss();
//        if ("1".equals(s)) {
//            ToastUtil.showToast(this, getString(R.string.file_share_success),
//                    R.drawable.tishi_ico_gougou);
//        } else {
//            ToastUtil.showToast(this, getString(R.string.file_share_error));
//        }
//    }
//
//    @Override
//    public void getFileList(List<ApiEntity.Files> files) {
//        // StringBuilder sb = new StringBuilder();
//        // for (Files files2 : files) {
//        // Log.d(TAG, "获取的数据。。。"+files2.Name+files2.ID);
//        // sb.append(files2.Name+":"+files2.ID+",");
//        // }
//        // Toast.makeText(getActivity(), sb.toString(), 0).show();
//        mFiles = (ArrayList) files;
//        refreshView(files);
//    }
//
//    /**
//     * 刷新视图
//     *
//     * @param files
//     */
//    private void refreshView(List<ApiEntity.Files> files) {
//        mNetworkTipsLayout.setVisibility(View.GONE);
//        if (files != null && files.size() == 0) {
//            mBlankLayout.setVisibility(View.VISIBLE);
//            mListView.setVisibility(View.GONE);
//        } else {
//            mBlankLayout.setVisibility(View.GONE);
//            mListView.setVisibility(View.VISIBLE);
//        }
//        // 设置数据
//        mFileListAdapter.setRequestType(mRequsetType);
//        mFileListAdapter.setDataList(files);
//        mFileListAdapter.notifyDataSetChanged();
//    }
//
//    public ListView getmListView() {
//        return mListView;
//    }
//
//    public LinearLayout getmBlankLayout() {
//        return mBlankLayout;
//    }
//
//
//    @Override
//    public void completeFresh() {
//        mPtrFrameLayout.refreshComplete();
//    }
//
//    @Override
//    public void onError(Throwable ex, boolean isOnCallback) {
//        mPtrFrameLayout.refreshComplete();
//        if (mLoadingDialog != null)
//            mLoadingDialog.dismiss();
//
//        switch (mOperateFile) {
//            case DELFILE:
//                mUserFenPai.Files = mLastFiles;
//                ToastUtil.showToast(this, getString(R.string.deletefile_error));
//                break;
//            case ADDFILE:
//                mUserFenPai.Files = mLastFiles;
//                ToastUtil.showToast(this, getString(R.string.task_attachment_add_error));
//                break;
//            case SHAREFILE:
//                ToastUtil.showToast(this, getString(R.string.file_share_error));
//                break;
//            case GETFILE:
//                if (ex instanceof ConnectException) {
//                    mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
//                }
//                break;
//            default:
//                break;
//        }
//        // 置空标记
//        mOperateFile = 0;
//    }
//
//}
