package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.mingle.headsUp.HeadsUpManager;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.ShowPhotoActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.chat.model.bean.EmojiBean;
import cc.emw.mobile.chat.view.ChatUtils;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.GroupSelectActivity;
import cc.emw.mobile.dynamic.adapter.DynamicDetailAdapter;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.dynamic.fragment.DynamicDiscussFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.UserNote.UserNoteShareTo;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.me.imagepicker.ImagePicker;
import cc.emw.mobile.me.imagepicker.bean.ImageItem;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEnum;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.record.CameraActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.DiscussFileLayout;
import cc.emw.mobile.view.DiscussImgLayout;
import cc.emw.mobile.view.DynamicEmoticonsKeyBoard;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import sj.keyboard.data.EmoticonEntity;
import sj.keyboard.interfaces.EmoticonClickListener;
import sj.keyboard.widget.EmoticonsEditText;

/**
 * 动态详情
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_dynamic_detail3)
public class DynamicDetailActivity extends BaseActivity {

    private static final String TAG = DynamicDetailActivity.class.getSimpleName();

//    @ViewInject(R.id.cm_header_btn_left) private ImageButton mHeaderBackBtn; // 顶部条返回按钮
//    @ViewInject(R.id.cm_header_tv_title) private TextView mHeaderTitleTv; // 顶部条标题
//    @ViewInject(R.id.cm_header_btn_right) private ImageButton mHeaderNoticeBtn; // 顶部条更多按钮

    @ViewInject(R.id.load_more_list_view_ptr_frame) private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
//    @ViewInject(R.id.load_more_list_view_container) private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.load_more_small_image_list_view) private ListView mListView; // 列表
    @ViewInject(R.id.ek_bar) private DynamicEmoticonsKeyBoard ekBar; //带输入框与表情的view
    private EditText mContentEt; // 内容

    private Dialog mLoadingDialog; //加载框
    private DynamicDetailAdapter mDynamicDetailAdapter; // 讨论adapter
    private ArrayList<UserNote> mDataList; // 回复列表数据
    private UserNote note; //列表传值
    private int noteID; //传值
    private int msgID; //传值，通知栏推送点击进入

    private int page = PAGE_FIRST; // 第几页
    private static final int PAGE_FIRST = 1; //第1页
    private static final int PAGE_COUNT = 10; //页数

    private DiscussImgLayout imgLayout; //展示选择的图片
    private DiscussFileLayout fileLayout; //展示选择的文件

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getIntent().putExtra("isHideKeyboard", false);
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (imgLayout != null && imgLayout.getPreSurfaceView() != null) {
            imgLayout.getPreSurfaceView().openCamera();
        }
    }

    @Override
    protected void startAnimEnd(Bundle savedInstanceState) {
        note = (UserNote) getIntent().getSerializableExtra("user_note");
        noteID = getIntent().getIntExtra("note_id", 0);
        msgID = getIntent().getIntExtra("msg_id", 0);
        if (msgID > 0) {
            Intent intent = new Intent(MainActivity.ACTION_RIGHT_REFRESH);
            intent.putExtra(MainActivity.MESSAGE_ID, msgID);
            intent.putExtra(MainActivity.MESSAGE_TYPE, 2);
            sendBroadcast(intent);//发广播清除未读数
            HeadsUpManager.getInstant(this).cancel(msgID);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(msgID);
                }
            }, 3000);
        }
        initView();

        if (note != null) {
            getRevsList();
        } else if (noteID != 0) {
            note = new UserNote();
            note.ID = noteID;
            getDetailByID();
        } else {
            AlertDialog dialog = new AlertDialog(DynamicDetailActivity.this).builder();
            dialog.setCancelable(false).setMsg(getString(R.string.dynamicdetail_info_error));
            dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollToFinishActivity();
                }
            }).show();
        }
    }

    private void initView() {
//    	mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        /*mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.dynamicdetail);
		mHeaderNoticeBtn.setImageResource(R.drawable.nav_btn_notice);
		mHeaderNoticeBtn.setVisibility(View.GONE);*/
        findViewById(R.id.cm_header_tv_right9).setVisibility(View.GONE);

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
                getRevsList();
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
//        loadMoreListViewContainer.setAutoLoadMore(true);
        initEmoticonsKeyBoardBar();
        mDataList = new ArrayList<>();
        mDynamicDetailAdapter = new DynamicDetailAdapter(this, mDataList, mContentEt);
        if (note != null) {
            mDynamicDetailAdapter.setTopData(note);
            mDynamicDetailAdapter.getVoteMap().put(note.ID, true); // 在动态列表已处理好投票逻辑，所以无需再次请求处理
        }
        mListView.setAdapter(mDynamicDetailAdapter);
//		loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
//            @Override
//            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
//            	page++;
//            	getRevsList();
//            }
//        });
    }

    private void initEmoticonsKeyBoardBar() {
        mContentEt = ekBar.getEtChat();
        ChatUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.setAdapter(ChatUtils.getCommonAdapter(this, emoticonClickListener));
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
                    ToastUtil.showToast(DynamicDetailActivity.this, R.string.empty_content_tips);
                } else {
                    reply(content);
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

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right, R.id.cm_header_btn_left9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
            case R.id.cm_header_btn_left9:
                super.onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                Intent noticeIntent = new Intent(this, TestActivity.class);
                startActivity(noticeIntent);
                break;
        }
    }

    @Event(R.id.itv_dynamicbottom_send)
    private void onSendClick(View v) {
        String content = mContentEt.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast(DynamicDetailActivity.this, R.string.empty_content_tips);
        } else {
            reply(content);
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
                case ContactSelectActivity.RADIO_SELECT: //分享给人员
                    UserInfo user = (UserInfo) data.getSerializableExtra("select_user");
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("SenderID", user.ID);
                    intent.putExtra("type", 1);
                    intent.putExtra("name", user.Name);
                    UserNoteShareTo share = new UserNoteShareTo();
                    share.NoteID = note.ID;
                    share.UserName = note.UserName;
                    share.Content = note.Content;
                    intent.putExtra("share", new Gson().toJson(share));
                    startActivity(intent);
                    break;
                case 99: //分享给圈子
                    GroupInfo group = (GroupInfo) data.getSerializableExtra(GroupSelectActivity.TargetG);
                    Intent intent2 = new Intent(this, ChatActivity.class);
                    intent2.putExtra("GroupID", group.ID);
                    intent2.putExtra("type", 2);
                    intent2.putExtra("name", group.Name);
                    UserNoteShareTo shareto = new UserNoteShareTo();
                    shareto.NoteID = note.ID;
                    shareto.UserName = note.UserName;
                    shareto.Content = note.Content;
                    intent2.putExtra("share", new Gson().toJson(shareto));
                    startActivity(intent2);
                    break;
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

        if (resultCode == Activity.RESULT_OK && requestCode == 10000) {
            String uri = data.getStringExtra("send_photo_uri");
            uploadImage(uri);
        }
        if (requestCode == 100 && resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //noinspection unchecked
            ArrayList<ImageItem> imageList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (imageList != null && imageList.size() > 0 && imageList.get(0) != null) {
                String pathStr = imageList.get(0).path;
                Intent intentImage = new Intent(this, ShowPhotoActivity.class);
                intentImage.putExtra(ShowPhotoActivity.EXTRA_INTO_FLAG, ShowPhotoActivity.FLAG_DYNAMIC_DISCUSS);
                intentImage.putExtra("photo_uri", pathStr);
                startActivityForResult(intentImage, 10000);
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            String uri = data.getStringExtra(CameraActivity.RESULT_DATA);
            Intent intentImage = new Intent(this, ShowPhotoActivity.class);
            intentImage.putExtra(ShowPhotoActivity.EXTRA_INTO_FLAG, ShowPhotoActivity.FLAG_DYNAMIC_DISCUSS);
            intentImage.putExtra("photo_uri", uri);
            startActivityForResult(intentImage, 10000);
        }
    }


    /**
     * 根据动态ID获取详情信息
     */
    private void getDetailByID() {
        API.TalkerAPI.getTalkerById(noteID, new RequestCallback<UserNote>(UserNote.class, false, true) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (!isFinishing()) {
                    if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                    AlertDialog dialog = new AlertDialog(DynamicDetailActivity.this).builder();
                    dialog.setCancelable(false).setMsg(getString(R.string.dynamicdetail_info_error));
                    dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            scrollToFinishActivity();
                        }
                    }).show();
                }
            }

            @Override
            public void onStarted() {
                /*mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
                mLoadingDialog.show();*/
            }

            @Override
            public void onParseSuccess(UserNote respInfo) {
                mPtrFrameLayout.refreshComplete();
                note = respInfo;
                mDynamicDetailAdapter.setTopData(note);
                mDynamicDetailAdapter.notifyDataSetChanged();
                getRevsList();

                if (msgID > 0 && note != null) {
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    intent.putExtra("push_note", note);
                    sendBroadcast(intent);
                }
            }
        });
    }

    /**
     * 获取回复列表
     */
    private void getRevsList() {
        API.TalkerAPI.getTalkerRevByTalkerId(note.ID, new RequestCallback<UserNote>(UserNote.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                mPtrFrameLayout.refreshComplete();
                ToastUtil.showToast(DynamicDetailActivity.this, R.string.dynamicdetail_commentlist_error);
                if (page > 0) {
                    page--;
                }
            }

            @Override
            public void onParseSuccess(List<UserNote> respList) {
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                mPtrFrameLayout.refreshComplete();
//				if (respList.size() < PAGE_COUNT)
//					loadMoreListViewContainer.loadMoreFinish(false, false);// load more
//				else
//					loadMoreListViewContainer.loadMoreFinish(false, true);
                if (page == 1) {
                    mDataList.clear();
                    mDynamicDetailAdapter.getAllData().clear();
//					note.RevCount = respList.size();
                }
                for (UserNote userNote : respList) {
                    if (userNote.PID == note.ID) {
                        mDataList.add(userNote);
                    }
                }
                mDynamicDetailAdapter.setAllData(respList);
                mDynamicDetailAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 回复
     *
     * @param content
     */
    private void reply(String content) {
        HelpUtil.hideSoftInput(DynamicDetailActivity.this, mContentEt);
        final UserNote rev = new UserNote();
        rev.ID = 0;
        rev.Content = content;
        rev.PID = note.ID;
        rev.TopId = note.ID;
        if (mContentEt != null && mContentEt.getTag() instanceof UserNote) { //是否回复对方
            UserNote subrev = (UserNote) mContentEt.getTag();
            rev.PID = subrev.ToUserId > 0 ? subrev.PID : subrev.ID;
            rev.ToUserId = subrev.UserID;

            rev.ToUserIdInfo = new ApiEntity.TalkerUserInfo(); //回复的对方信息
            rev.ToUserIdInfo.ID = subrev.UserID;
            rev.ToUserIdInfo.Name = subrev.UserIdInfo != null ? subrev.UserIdInfo.Name : "";
        }
        if (ekBar.getFuncLayout().getCurrentFuncKey() == DynamicEmoticonsKeyBoard.FUNC_TYPE_IMG) { //带图片
            rev.AddType = ApiEnum.UserNoteAddTypes.Image;
            rev.AddProperty = new Gson().toJson(imgLayout.getData());
        } else if (ekBar.getFuncLayout().getCurrentFuncKey() == DynamicEmoticonsKeyBoard.FUNC_TYPE_FILE) { //带文件
            rev.AddType = ApiEnum.UserNoteAddTypes.File;
            rev.AddProperty = new Gson().toJson(fileLayout.getData());
        }
        Log.e("test_test", new Gson().toJson(rev));
        API.TalkerAPI.SaveTalkerRev(rev, new RequestCallback<ApiEntity.APIResult>(ApiEntity.APIResult.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Logger.d(TAG, "onError:" + (ex != null ? ex.toString() : ""));
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                ToastUtil.showToast(DynamicDetailActivity.this, R.string.dynamicdetail_comment_error);
            }

            @Override
            public void onStarted() {
                if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
                    mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onSuccess(String result) {
                Logger.d(TAG, "onSuccess:" + result);
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(DynamicDetailActivity.this, R.string.dynamicdetail_comment_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    intent.putExtra("note_id", note.ID);
                    rev.ID = Integer.valueOf(result);
                    rev.UserID = PrefsUtil.readUserInfo().ID;
                    rev.UserIdInfo = HelpUtil.userInfo2TalkerUserInfo(PrefsUtil.readUserInfo());
                    rev.CreateTime = StringUtils.getCurTimeStr();
                    Gson gson = new Gson();
                    intent.putExtra("rev_note", gson.fromJson(gson.toJson(rev), ApiEntity.UserNote.class));
                    sendBroadcast(intent); //刷新Talker列表
                    mDataList.clear();
                    mDynamicDetailAdapter.getAllData().add(rev);
                    for (UserNote userNote : mDynamicDetailAdapter.getAllData()) {
                        if (userNote.PID == note.ID) { //找出第一级数据
                            mDataList.add(userNote);
                        }
                    }
                    note.RevCount++;
                    mDynamicDetailAdapter.setData(mDataList);
                    mDynamicDetailAdapter.setTopData(note);
                    mDynamicDetailAdapter.notifyDataSetChanged();

                    mContentEt.setText("");
                    mContentEt.setTag(null);
                    mContentEt.setHint(R.string.dynamicdetail_edittext_hint);

                    ekBar.reset();
                    imgLayout.clearData();
                    fileLayout.clearData();
                } else {
                    ToastUtil.showToast(DynamicDetailActivity.this, R.string.dynamicdetail_comment_error);
                }
            }
			/*@Override
            public void onParseSuccess(ApiEntity.APIResult respInfo) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (respInfo != null && respInfo.State == 1) {

			}*/
        });
    }

    private void uploadImage(String path) {
        RequestParam params = new RequestParam(Const.UPLOAD_IMAGE_URL);
        params.setMultipart(true);
        final File file = new File(path);
        params.addBodyParameter(file.getName(), file);
        x.http().post(params, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null && !isFinishing())
                    mLoadingDialog.dismiss();
                ToastUtil.showToast(DynamicDetailActivity.this, "上传图片失败！");
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<String> result) {
                // 获取服务器返回的图片的url发送消息
                if (result != null && result.size() > 0) {
                    UserNote.UserNoteFile image = new UserNote.UserNoteFile();
                    image.FileId = 0;
                    image.Url = result.get(0);
                    image.FileName = file.getName();
                    image.Length = file.length();
                    image.CreateUser = PrefsUtil.readUserInfo().ID;
                    image.CompanyCode = PrefsUtil.readUserInfo().CompanyCode;
                    imgLayout.getData().clear();
                    imgLayout.getData().add(image);
                    reply("");
                }
            }
        });
    }
}
