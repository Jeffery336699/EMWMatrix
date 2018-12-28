package cc.emw.mobile.contact;

import cc.emw.mobile.net.RequestCallback;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.file.adapter.FileListAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.task.presenter.TaskPresenter;
import cc.emw.mobile.task.view.ITaskModifyView;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;

@ContentView(R.layout.activity_relations)
public class RelationFileActivity extends BaseActivity implements
        ITaskModifyView {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn; // 顶部条发布

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.load_more_list_view_container)
    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 我相关的列表

    @ViewInject(R.id.ll_task_blank)
    private LinearLayout mLlBlank;// 空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout;// 无网络
    @ViewInject(R.id.img_relation_blank_tag)
    private ImageView imgBlankTips;
    @ViewInject(R.id.tv_relation_blank_tag)
    private TextView tvBlankTips;


    private ArrayList<Files> mDataList; // 列表数据
    private int page = PAGE_FIRST; // 第几页，第1页为1，每下一页+1
    private static final int PAGE_FIRST = 1; // 第1页
    private static final int PAGE_COUNT = 10; // 页数
    private FileListAdapter adapter; // 文件列表的Adapter
    // private int mType; //0:所有文件；1:我的文件；2:共享给我的文件；3:作废的文件
    private UserInfo userInfo;
    private GroupInfo groupInfo;

    public static final String CALENDER_FILE = "calender_file";// 接收到日程界面的传过来的附件ID
    private String mStringFiles;
    private TaskPresenter mTaskPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataList = new ArrayList<Files>();
        mTaskPresenter = new TaskPresenter(this);
        if (getIntent().hasExtra("user_info")) {
            userInfo = (UserInfo) getIntent().getSerializableExtra("user_info");
        } else if (getIntent().hasExtra("group_info")) {
            groupInfo = (GroupInfo) getIntent().getSerializableExtra(
                    "group_info");
        } else if (getIntent().hasExtra(CALENDER_FILE)) {
            mStringFiles = StringUtils.replaceBlank(getIntent().getStringExtra(CALENDER_FILE));
            getFiles();
        }

        initView();

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    /**
     * 根据附件IDs获取附件
     */
    private void getFiles() {
        mTaskPresenter.getFileListByIds(getIds(mStringFiles));
    }

    @Override
    public void showFinish() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        mPtrFrameLayout.refreshComplete();
        if (ex instanceof ConnectException) {
            mNetworkTipsLayout.setVisibility(View.VISIBLE);// 无网络状态 展示视图
        }
    }

    @Override
    public void getFileList(List<Files> files) {
        // 空视图展示
        mNetworkTipsLayout.setVisibility(View.GONE);
        if (files.size() == 0) {
            mLlBlank.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            imgBlankTips.setImageResource(R.drawable.blank_ico_zhishiku);
            tvBlankTips.setText(getString(R.string.tv_file_relation_tips));
        } else {
            mLlBlank.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
        mDataList.clear();
        mDataList.addAll(files);
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.relationfile);

        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        adapter = new FileListAdapter(this, mDataList);
        mListView.setAdapter(adapter);

        refresh();
    }

    private void refresh() {

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,
                        mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (getIntent().hasExtra(CALENDER_FILE)) {
                    getFiles();
                } else {
                    if (userInfo != null) {
                        getFileByUserId(userInfo.ID);
                    } else if (groupInfo != null) {
                        GetFileByGroupId(groupInfo.ID);
                    }
                }
            }
        });
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                break;
        }
    }

    private void getFileByUserId(int uid) {
        API.UserData.GetFilesListByUserId(uid, new RequestCallback<Files>(
                Files.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationFileActivity.this, R.string.relationfile_list_error);
            }

            @Override
            public void onParseSuccess(List<Files> respList) {
                if (respList.size() > 0) {
                    // 有数据
                    mLlBlank.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mPtrFrameLayout.refreshComplete();
                    mDataList.clear();
                    mDataList.addAll(respList);
                    adapter.notifyDataSetChanged();
                } else {
                    mListView.setVisibility(View.GONE);
                    mLlBlank.setVisibility(View.VISIBLE);
                    imgBlankTips.setImageResource(R.drawable.blank_ico_zhishiku);
                    tvBlankTips.setText(getString(R.string.tv_file_relation_tips));
                    mPtrFrameLayout.refreshComplete();
                }
            }
        });
    }

    private void GetFileByGroupId(int gid) {
        API.TalkerAPI.GetFileListByGroupId(gid, new RequestCallback<Files>(
                Files.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(RelationFileActivity.this, R.string.relationfile_list_error);
            }

            @Override
            public void onParseSuccess(List<Files> respList) {
                if (respList.size() > 0) {
                    // 有数据
                    mLlBlank.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mPtrFrameLayout.refreshComplete();
                    mDataList.clear();
                    mDataList.addAll(respList);
                    adapter.notifyDataSetChanged();
                } else {
                    mListView.setVisibility(View.GONE);
                    mLlBlank.setVisibility(View.VISIBLE);
                    imgBlankTips.setImageResource(R.drawable.blank_ico_zhishiku);
                    tvBlankTips.setText(getString(R.string.tv_file_relation_tips));
                    mPtrFrameLayout.refreshComplete();
                }
            }
        });
    }

    /**
     * 将字符串[1，2，3]变成1，2，3
     *
     * @param files
     * @return
     */
    private String getIds(String files) {
        if (files != null && !files.equals("") && !files.equals("[]")) {
            return files.substring(1, files.length() - 1);
        } else {
            return "";
        }
    }

    @Override
    public void createTask(String s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void modifyTask(String s) {
        // TODO Auto-generated method stub

    }

}
