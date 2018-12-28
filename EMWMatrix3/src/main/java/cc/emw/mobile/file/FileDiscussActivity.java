package cc.emw.mobile.file;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.chat.model.bean.EmojiBean;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.dynamic.DynamicDiscussActivity;
import cc.emw.mobile.file.adapter.FileDiscussAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.APIResult;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.TaskReply;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.DiscussFileLayout;
import cc.emw.mobile.view.DiscussImgLayout;
import cc.emw.mobile.view.DynamicEmoticonsKeyBoard;
import cc.emw.mobile.view.IconTextView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.widget.EmoticonsEditText;

/**
 * 文件讨论
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_file_discuss3)
public class FileDiscussActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderNoticeBtn; //

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
//    @ViewInject(R.id.load_more_list_view_container)
//    private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.load_more_small_image_list_view)
    private ListView mListView; // 列表
    @ViewInject(R.id.ek_bar)
    private DynamicEmoticonsKeyBoard ekBar;
    private EditText mContentEt; // 内容
    @ViewInject(R.id.ll_filediscuss_blank)
    private LinearLayout mBlankLayout; // 空视图
    @ViewInject(R.id.ll_network_tips)
    private LinearLayout mNetworkTipsLayout; //无网络

    private ArrayList<TaskReply> mDataList; //列表数据
    private FileDiscussAdapter mFileDiscussAdapter; // Adapter
    private int page = PAGE_FIRST; //第几页
    private static final int PAGE_FIRST = 1; //第1页
    private static final int PAGE_COUNT = 10; //页数

    private Dialog mLoadingDialog; //加载框
    private Files noteFile; //传值
    private DiscussImgLayout imgLayout;
    private DiscussFileLayout fileLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        noteFile = (Files) getIntent().getSerializableExtra("file_info");

        initView();

        if (noteFile != null) {
            mPtrFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPtrFrameLayout.autoRefresh(false);
                }
            }, 100);
        }

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
        mHeaderTitleTv.setText("讨论");
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
//                page = PAGE_FIRST;
                getTaskReplyByFileId(noteFile.ID);
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

//        loadMoreListViewContainer.useDefaultFooter();
//        loadMoreListViewContainer.setAutoLoadMore(false);
        initEmoticonsKeyBoardBar();
        mDataList = new ArrayList<TaskReply>();
        mFileDiscussAdapter = new FileDiscussAdapter(this, mDataList, mContentEt);
        mListView.setAdapter(mFileDiscussAdapter);
//        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
//            @Override
//            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
//                page++;
//                getRevsList();
//            }
//        });
    }

    private void initEmoticonsKeyBoardBar() {
        mContentEt = ekBar.getEtChat();
        ChatUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.setAdapter(ChatUtils.getCommonAdapter(this, emoticonClickListener));
//		ekBar.addOnFuncKeyBoardListener(this);
        imgLayout = new DiscussImgLayout(this);
        ekBar.addFuncView(DynamicEmoticonsKeyBoard.FUNC_TYPE_IMG, imgLayout);
        fileLayout = new DiscussFileLayout(this);
        ekBar.addFuncView(DynamicEmoticonsKeyBoard.FUNC_TYPE_FILE, fileLayout);

        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
//				scrollToBottom();
            }
        });
        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mContentEt.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(FileDiscussActivity.this, R.string.empty_content_tips);
                } else {
                    doFileReply(content);
                }
            }
        });
    }
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

    @Event(R.id.tv_filediscuss_send)
    private void onSendClick(View v) {
        String content = mContentEt.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast(FileDiscussActivity.this, R.string.empty_content_tips);
        } else {
            doFileReply(content);
        }
    }

    @Override
    public void onBackPressed() {
        if (mContentEt.getHint().toString().startsWith("回复")) {
            mContentEt.setTag(null);
            mContentEt.setHint(R.string.dynamicdetail_edittext_hint);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DynamicDiscussActivity.CHOSE_IMG_CODE: //选择的图片
                    ArrayList<ApiEntity.Files> imgList = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
                    imgLayout.setData(imgList);
                    break;
                case DynamicDiscussActivity.CHOSE_FILE_CODE: //选择的文件
                    ArrayList<ApiEntity.Files> fileList = (ArrayList<ApiEntity.Files>) data.getSerializableExtra("select_list");
                    fileLayout.setData(fileList);
                    break;
            }
        }
    }

    /**
     * 获取评论列表
     * @param fid
     */
    private void getTaskReplyByFileId(int fid) {
        API.TalkerAPI.GetTaskReplyByTaskId(fid, 2, new RequestCallback<TaskReply>(TaskReply.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                mPtrFrameLayout.refreshComplete();
                mBlankLayout.setVisibility(View.GONE);
                if (ex instanceof ConnectException) {
                    mNetworkTipsLayout.setVisibility(View.VISIBLE);
                } else {
                    ToastUtil.showToast(FileDiscussActivity.this, R.string.dynamicdetail_commentlist_error);
                }
//                if (page > 0) {
//                    page--;
//                }
            }

            @Override
            public void onParseSuccess(List<TaskReply> respList) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                mPtrFrameLayout.refreshComplete();
                mNetworkTipsLayout.setVisibility(View.GONE);
                mBlankLayout.setVisibility(page == PAGE_FIRST && respList.size() == 0 ? View.VISIBLE : View.GONE);
                if (page == PAGE_FIRST) {
                    mDataList.clear();
                }
                mDataList.addAll(respList);
                mFileDiscussAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 回复
     * @param content
     */
    private void doFileReply(String content) {
        HelpUtil.hideSoftInput(FileDiscussActivity.this, mContentEt);
        TaskReply fileReply = new TaskReply();
        fileReply.ID = 0;
        fileReply.Content = content;
        fileReply.ParentID = 0;
        fileReply.TaskId = noteFile.ID;
        fileReply.RType = 2;
        if (mContentEt != null && mContentEt.getTag() instanceof TaskReply) {
            TaskReply subrev = (TaskReply) mContentEt.getTag();
            fileReply.ParentID = subrev.ID;
            fileReply.ToUserID = subrev.Creator;
        }
        if (ekBar.getFuncLayout().getCurrentFuncKey() == DynamicEmoticonsKeyBoard.FUNC_TYPE_IMG) {
            fileReply.ContentType = 1;
            fileReply.AddProperty = new Gson().toJson(imgLayout.getData());
        } else if (ekBar.getFuncLayout().getCurrentFuncKey() == DynamicEmoticonsKeyBoard.FUNC_TYPE_FILE) {
            fileReply.ContentType = 2;
            fileReply.AddProperty = new Gson().toJson(fileLayout.getData());
        } else {
            fileReply.ContentType = 0;
        }

        API.TalkerAPI.DoTaskReply(fileReply, new RequestCallback<APIResult>(APIResult.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(FileDiscussActivity.this, R.string.dynamicdetail_comment_error);
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(APIResult respInfo) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (respInfo != null && respInfo.State == 1) {
                    ToastUtil.showToast(FileDiscussActivity.this, R.string.dynamicdetail_comment_success, R.drawable.tishi_ico_gougou);
                    getTaskReplyByFileId(noteFile.ID);
                    mContentEt.setText("");
                    mContentEt.setTag(null);
                    mContentEt.setHint(R.string.dynamicdetail_edittext_hint);

                    ekBar.reset();
                    imgLayout.clearData();
                    fileLayout.clearData();
                } else {
                    ToastUtil.showToast(FileDiscussActivity.this, R.string.dynamicdetail_comment_error);
                }
            }

            /*@Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(FileDiscussActivity.this, R.string.dynamicdetail_comment_success, R.drawable.tishi_ico_gougou);
//                    mPtrFrameLayout.autoRefresh(false);
                    getTaskReplyByFileId(noteFile.ID);
                    mContentEt.setText("");
                    mContentEt.setTag(null);
                    mContentEt.setHint(R.string.dynamicdetail_edittext_hint);

                    ekBar.reset();
                    imgLayout.clearData();
                    fileLayout.clearData();
                } else {
                    ToastUtil.showToast(FileDiscussActivity.this, R.string.dynamicdetail_comment_error);
                }
            }*/
        });
    }


}
