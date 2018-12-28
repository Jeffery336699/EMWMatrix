package cc.emw.mobile.file;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.file.adapter.FileHistoryAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 历史版本
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_file_history3)
public class FileHistoryActivity extends BaseActivity implements OnItemClickListener {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderNoticeBtn; // 顶部条更多按钮

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    //	@ViewInject(R.id.load_more_list_view_container)
//	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 文件列表
    @ViewInject(R.id.ll_file_blank)
    private LinearLayout mBlankLayout; // 空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout; //无网络

    private Dialog mLoadingDialog; //加载框
    private FileHistoryAdapter mFileHistoryAdapter; // 文件历史版本adapter
    private ArrayList<Files> mDataList; // 文件列表数据

    private static final int CHOSE_FILE_CODE = 100;
    private int page = PAGE_FIRST; //第几页
    private static final int PAGE_FIRST = 1; //第1页
    private static final int PAGE_COUNT = 10; //页数
    private Files noteFile; //传值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        noteFile = (Files) getIntent().getSerializableExtra("file_info");

        initView();

        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);

        try {
            findViewById(R.id.cm_header_tv_right9).setVisibility(View.GONE);
        } catch (Exception e) {

        }
    }

    @Event(R.id.ll_network_tips)
    private void onNetworkTipsClick(View v) {
        mPtrFrameLayout.autoRefresh(false);
    }

    private void initView() {
        /*mHeaderBackBtn.setVisibility(View.GONE);
        mHeaderNoticeBtn.setImageResource(R.drawable.nav_btn_notice);
        mHeaderNoticeBtn.setVisibility(View.GONE);*/
        mHeaderTitleTv.setText("历史版本");
        ((IconTextView)findViewById(R.id.cm_header_btn_left9)).setIconText("eb68");

        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // here check list view, not content.
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = PAGE_FIRST;
                getFileVersion(noteFile.ID);
            }
        });

        // header
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

        /*loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(false);*/

        mDataList = new ArrayList<Files>();
        mFileHistoryAdapter = new FileHistoryAdapter(this, mDataList);
        mListView.setAdapter(mFileHistoryAdapter);
        mListView.setOnItemClickListener(this);
        /*loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
            	page++;
            	String keyword = mSearchEt.getText().toString().trim();
				getFilesListByPage(keyword, mType, noteFile != null ? noteFile.ID : 0);
            }
        });*/

    }

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

    @Event({R.id.cm_header_btn_left9, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                Intent noticeIntent = new Intent(this, TestActivity.class);
                startActivity(noticeIntent);
                break;
        }
    }


    /**
     * 获取文件版本列表
     *
     * @param fid
     */
    private void getFileVersion(int fid) {
        API.UserData.GetFilesVersionById(fid, new RequestCallback<Files>(Files.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrameLayout.refreshComplete();
                mBlankLayout.setVisibility(View.GONE);
                if (ex instanceof ConnectException) {
                    mNetworkTipsLayout.setVisibility(View.VISIBLE);
                } else {
                    ToastUtil.showToast(FileHistoryActivity.this, R.string.filelist_list_error);
                }
            }

            @Override
            public void onParseSuccess(List<Files> respList) {
                mPtrFrameLayout.refreshComplete();
                mNetworkTipsLayout.setVisibility(View.GONE);
                if (page == PAGE_FIRST) {
                    mDataList.clear();
                }
                if (PrefsUtil.readUserInfo().ID == noteFile.Creator) {
                    //去掉在用记录
                    Iterator<Files> it = respList.iterator();
                    while (it.hasNext()) {
                        Files file = it.next();
                        if (file.IsActive == 1) {
                            it.remove();//注意此处不能用list.remove(it.next());
                        }
                    }
                }
                mDataList.addAll(respList);
                mFileHistoryAdapter.notifyDataSetChanged();
                mBlankLayout.setVisibility(page == PAGE_FIRST && respList.size() == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * 恢复版本
     * @param file
     */
    private void recoverFile(Files file) {
        API.UserData.ReCoverFileByFileId(file, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(FileHistoryActivity.this, R.string.recoverfile_error);
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(FileHistoryActivity.this, R.string.recoverfile_success, R.drawable.tishi_ico_gougou);
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showToast(FileHistoryActivity.this, R.string.recoverfile_error);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if (v.getTag(R.id.tag_second) != null && PrefsUtil.readUserInfo().ID == noteFile.Creator) {
            final Files file = (Files) v.getTag(R.id.tag_second);
            new AlertDialog(this).builder().setMsg(getString(R.string.recoverfile_tips))
                    .setPositiveButton(getString(R.string.confirm), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recoverFile(file);
                        }
                    }).setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
    }

}
