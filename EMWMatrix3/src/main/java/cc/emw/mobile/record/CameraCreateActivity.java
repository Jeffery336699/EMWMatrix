package cc.emw.mobile.record;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.UserNote.UserNoteFile;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.NoteRole;
import cc.emw.mobile.net.ApiEntity.UploadResult;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteAddTypes;
import cc.emw.mobile.net.ApiEnum.UserNoteSendTypes;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.record.jcvideoplayer_lib.JCUserAction;
import cc.emw.mobile.record.jcvideoplayer_lib.JCUserActionStandard;
import cc.emw.mobile.record.jcvideoplayer_lib.JCVideoPlayer;
import cc.emw.mobile.record.jcvideoplayer_lib.JCVideoPlayerStandard;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.IconTextView;

/**
 * 动态·新建分享(图片/视频)
 *
 * @author shaobo.zhuang
 */
public class CameraCreateActivity extends BaseActivity implements OnClickListener {

    private IconTextView mHeaderCancelBtn;
    private TextView mHeaderSendTv; // 顶部条发布
    private EditText mContentEt; // 内容
    private ImageView mImgFlowLayout; // 图片Layout
    private JCVideoPlayerStandard mJcVideoPlayerStandard;
    private TextView mSelectTv; // 分享范围
    private LinearLayout mSelectLayout; // 分享人员Layout
    private FlowLayout mSelectFlowLayout; // 分享人员Layout

    private static final int CHOSE_PERSON_CODE = 6; //选择人员requestCode

    private DisplayImageOptions options;
    private Dialog mLoadingDialog; //加载框
    private ArrayList<UserNoteFile> imgList, videoList; // 图片、视频列表数据
    private ArrayList<UserInfo> selectList; // 分享人员列表数据
    private int addType = 0; //当前选择的类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_create);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.color.gray_1)
//		.showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
//		.showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        initView();
        initData();
    }

    private void initView() {
        mHeaderSendTv = (TextView) findViewById(R.id.cm_header_tv_right9);
        mHeaderCancelBtn = (IconTextView) findViewById(R.id.cm_header_btn_left9);
        mContentEt = (EditText) findViewById(R.id.cm_input_et_content);
        mImgFlowLayout = (ImageView) findViewById(R.id.im_shareimg_item);
        mJcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.jc_video);
        mSelectTv = (TextView) findViewById(R.id.cm_select_tv_name);
        mSelectLayout = (LinearLayout) findViewById(R.id.cm_select_ll_select);
        mSelectFlowLayout = (FlowLayout) findViewById(R.id.cm_select_fl_select);
        mContentEt.setHint(R.string.content_hint);
        imgList = new ArrayList<UserNoteFile>();
        videoList = new ArrayList<UserNoteFile>();
        mHeaderSendTv.setOnClickListener(this);
        mHeaderCancelBtn.setOnClickListener(this);
        mSelectLayout.setOnClickListener(this);
    }

    private void initData() {
        if (getIntent().hasExtra("jpegName")) {
            String vPath = getIntent().getStringExtra("jpegName");
            String filePath = getIntent().getStringExtra("filePath");
            mImgFlowLayout.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(vPath, new ImageViewAware(mImgFlowLayout), options,
                    new ImageSize(DisplayUtil.dip2px(this, DisplayUtil.getDisplayWidth(this)), DisplayUtil.dip2px(this, 200)), null, null);
            addType = UserNoteAddTypes.Image;
        } else if (getIntent().hasExtra("video_url")) {
            String vPath = getIntent().getStringExtra("video_url");
            addType = UserNoteAddTypes.Video;
            mJcVideoPlayerStandard.setVisibility(View.VISIBLE);
            mJcVideoPlayerStandard.setUp(vPath
                    , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(vPath);
                Bitmap bitmap = mmr.getFrameAtTime();
                if (bitmap != null)
                    mJcVideoPlayerStandard.thumbImageView.setImageBitmap(bitmap);
                mmr.release();
            } catch (Exception e) {
                e.printStackTrace();
            }

            JCVideoPlayer.setJcUserAction(new MyUserActionStandard());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                HelpUtil.hideSoftInput(this, mContentEt);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                String content = mContentEt.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(this, R.string.empty_content_tips);
                } else {
                    if (getIntent().hasExtra("jpegName")) {
                        String filePath = getIntent().getStringExtra("filePath");
                        uploadFile(filePath, content);
                    } else if (getIntent().hasExtra("video_url")) {
                        String vPath = getIntent().getStringExtra("video_url");
                        uploadFile(vPath, content);
                    }
                }
                break;
            case R.id.cm_select_ll_select:
                Intent intent = new Intent(this, ContactSelectActivity.class);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
                intent.putExtra("select_list", selectList);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivityForResult(intent, CHOSE_PERSON_CODE);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mContentEt.clearFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CHOSE_PERSON_CODE: //选择的人员
                    mSelectFlowLayout.removeAllViews();
                    selectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < selectList.size(); i++) {
                        if (i < 3) {
                            UserInfo user = selectList.get(i);
                            if (i != 0) {
                                builder.append("、");
                            }
                            builder.append(user.Name);
                        } else {
                            builder.append("等" + selectList.size() + "人");
                            break;
                        }
                    }
                    mSelectTv.setText(builder);
                    mSelectTv.setHint(selectList.size() > 0 ? "" : "公开");
//                    ((TextView) findViewById(R.id.tv_share_select)).setText(selectList.size() > 0 ? "部分好友" : "公开");
                    break;
            }
        }
    }

    //添加视频
    private void addVideo(final Files file) {
        if (file != null) {
            videoList.clear();
            file.Url = "/" + file.CompanyCode + "/UserFile/" + file.Url;
            videoList.add(HelpUtil.files2UserNoteFile(file));
        }
    }

    private void uploadFile(String path, final String content) {
        RequestParam params = new RequestParam(Const.BASE_URL + "/UploadFile");
        params.addQueryStringParameter("path", "");
        params.addQueryStringParameter("n", "");
        params.addQueryStringParameter("save", "1");
        params.setMultipart(true);
        File file = new File(path);
        params.addBodyParameter("file", file);
        Callback.Cancelable cancelable = x.http().post(params, new RequestCallback<UploadResult>(UploadResult.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(CameraCreateActivity.this, R.string.filelist_upload_error);
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips4);
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<UploadResult> respList) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0 && respList.get(0).FileInfo != null) {
//                    ToastUtil.showToast(CameraCreateActivity.this, R.string.filelist_upload_success, R.drawable.tishi_ico_gougou);
                    if (addType == UserNoteAddTypes.Video) {
                        addVideo(respList.get(0).FileInfo);
                    } else {
                        imgList.clear();
                        imgList.add(HelpUtil.files2UserNoteFile(respList.get(0).FileInfo));
                    }
                    send(content);
                } else {
                    ToastUtil.showToast(CameraCreateActivity.this, R.string.filelist_upload_error);
                }
            }
        });
    }

    /**
     * 发布
     *
     * @param content
     */
    private void send(String content) {
        UserNote un = new UserNote();
        un.Type = UserNoteAddTypes.Normal;
        un.AddType = addType;
        un.Content = content;
        un.UserID = PrefsUtil.readUserInfo().ID;
        un.Roles = new ArrayList<NoteRole>();
        switch (addType) {
            case UserNoteAddTypes.Image:
                un.AddProperty = new Gson().toJson(imgList);
                break;
            case UserNoteAddTypes.Video:
                un.AddProperty = new Gson().toJson(videoList);
                break;
        }
        if (imgList.size() == 0 && videoList.size() == 0 && addType != UserNoteAddTypes.Link && addType != UserNoteAddTypes.Vote) {
            un.AddType = 0;
        }
        ArrayList<NoteRole> nrList = new ArrayList<NoteRole>();
        if (selectList != null && selectList.size() > 0) {
            un.SendType = UserNoteSendTypes.Private;  //0 公共 1 私有
            for (int i = 0, size = selectList.size(); i < size; i++) {
                NoteRole role = new NoteRole();
                role.ID = selectList.get(i).ID;
                role.Name = selectList.get(i).Name;
                role.Image = selectList.get(i).Image;
                role.Type = NoteRoleTypes.User;
                nrList.add(role);
            }
            un.Roles = nrList;
        } else {
            un.SendType = UserNoteSendTypes.Public;
        }

        API.TalkerAPI.SaveTalker(un, new RequestCallback<ApiEntity.APIResult>(ApiEntity.APIResult.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                if (mLoadingDialog != null) mLoadingDialog.dismiss();
//                ToastUtil.showToast(CameraCreateActivity.this, R.string.publish_error);
            }

            @Override
            public void onStarted() {
//                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
//                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
//                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(CameraCreateActivity.this, R.string.publish_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    sendBroadcast(intent); // 刷新首页列表
                    finish();
                } else {
                    ToastUtil.showToast(CameraCreateActivity.this, R.string.publish_error);
                }
            }
        });
    }

    class MyUserActionStandard implements JCUserActionStandard {

        @Override
        public void onEvent(int type, String url, int screen, Object... objects) {
            switch (type) {
                case JCUserAction.ON_CLICK_START_ICON:
                    Log.i("USER_EVENT", "ON_CLICK_START_ICON" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_CLICK_START_ERROR:
                    Log.i("USER_EVENT", "ON_CLICK_START_ERROR" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_CLICK_START_AUTO_COMPLETE:
                    Log.i("USER_EVENT", "ON_CLICK_START_AUTO_COMPLETE" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_CLICK_PAUSE:
                    Log.i("USER_EVENT", "ON_CLICK_PAUSE" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_CLICK_RESUME:
                    Log.i("USER_EVENT", "ON_CLICK_RESUME" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_SEEK_POSITION:
                    Log.i("USER_EVENT", "ON_SEEK_POSITION" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_AUTO_COMPLETE:
                    Log.i("USER_EVENT", "ON_AUTO_COMPLETE" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_ENTER_FULLSCREEN:
                    Log.i("USER_EVENT", "ON_ENTER_FULLSCREEN" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_QUIT_FULLSCREEN:
                    Log.i("USER_EVENT", "ON_QUIT_FULLSCREEN" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_ENTER_TINYSCREEN:
                    Log.i("USER_EVENT", "ON_ENTER_TINYSCREEN" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_QUIT_TINYSCREEN:
                    Log.i("USER_EVENT", "ON_QUIT_TINYSCREEN" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_TOUCH_SCREEN_SEEK_VOLUME:
                    Log.i("USER_EVENT", "ON_TOUCH_SCREEN_SEEK_VOLUME" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_TOUCH_SCREEN_SEEK_POSITION:
                    Log.i("USER_EVENT", "ON_TOUCH_SCREEN_SEEK_POSITION" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;

                case JCUserActionStandard.ON_CLICK_START_THUMB:
                    Log.i("USER_EVENT", "ON_CLICK_START_THUMB" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserActionStandard.ON_CLICK_BLANK:
                    Log.i("USER_EVENT", "ON_CLICK_BLANK" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                default:
                    Log.i("USER_EVENT", "unknow");
                    break;
            }
        }
    }

    /**
     * 加载对话框
     *
     * @param resId 字符串资源ID
     * @return
     */
    public Dialog createLoadingDialog(int resId) {
        return createLoadingDialog(getString(resId));
    }

    /**
     * 加载对话框
     *
     * @param msg 提示信息
     * @return
     */
    public Dialog createLoadingDialog(String msg) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        TextView tipTextView = (TextView) v.findViewById(R.id.loading_tv_tip);
        tipTextView.setText(msg);
        Dialog loadingDialog = new Dialog(this, R.style.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }
}
