package cc.emw.mobile.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnShowListener;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.LayoutRipple2;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.dynamic.adapter.ShareImgVoteAdapter;
import cc.emw.mobile.dynamic.adapter.ShareVoteAdapter;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.entity.UserNote.UserNoteFile;
import cc.emw.mobile.entity.UserNote.UserNoteLink;
import cc.emw.mobile.entity.UserNote.UserNoteVote;
import cc.emw.mobile.entity.UserNote.UserRootVote;
import cc.emw.mobile.file.FileSelectActivity;
import cc.emw.mobile.file.FileSelectActivity2;
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
import cc.emw.mobile.record.CameraActivity;
import cc.emw.mobile.record.CameraActivity2;
import cc.emw.mobile.record.jcvideoplayer_lib.JCVideoPlayerStandard;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.FlowLayout.LayoutParams;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SegmentedGroup;
import cc.emw.mobile.view.edittag.EditVoteTag;

/**
 * 动态·新建分享(文件/图片/链接/投票/视频)
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_share3)
public class ShareActivity extends BaseActivity implements OnShowListener, OnDismissListener {

//    @ViewInject(R.id.cm_header_btn_left) private IconTextView mHeaderCancelBtn; // 顶部条取消
//    @ViewInject(R.id.cm_header_tv_title) private TextView mHeaderTitleTv; // 顶部条标题
//    @ViewInject(R.id.cm_header_tv_right) private TextView mHeaderSendTv; // 顶部条发布

    @ViewInject(R.id.cm_input_et_content)
    private EditText mContentEt; // 内容
    @ViewInject(R.id.ll_sharefile_root)
    private LinearLayout mFileRootLayout; // 文件根Layout
    @ViewInject(R.id.ll_sharefile_item)
    private LinearLayout mFileLayout; // 文件Layout
    @ViewInject(R.id.ll_shareimg_root)
    private LinearLayout mImgRootLayout; // 图片根Layout
    @ViewInject(R.id.fl_shareimg_item)
    private FlowLayout mImgFlowLayout; // 图片Layout
    @ViewInject(R.id.ll_sharelink_root)
    private LinearLayout mLinkRootLayout; // 链接根Layout
    @ViewInject(R.id.et_sharelink_url)
    private EditText mLinkUrlEt; // 链接URL
    @ViewInject(R.id.et_sharelink_desc)
    private EditText mLinkNameEt; // 链接名称
    @ViewInject(R.id.ll_sharevote_root)
    private LinearLayout mVoteRootLayout; // 投票根Layout
    @ViewInject(R.id.segmented_vote_type)
    private SegmentedGroup mRadioGroup; // 投票类型RadioGroup
    @ViewInject(R.id.lv_sharevote_item)
    private ExListView mVoteLv; // 投票列表
    @ViewInject(R.id.btn_sharevote_add)
    private ButtonFlat mVoteAddBtn; //添加投票
    @ViewInject(R.id.ll_sharevote_tag)
    private LinearLayout mVoteTagLayout; //标签Layout
    @ViewInject(R.id.edit_tag_view)
    private EditVoteTag mVoteTagEvt; //标签
    @ViewInject(R.id.ll_sharevote_enddate)
    private LayoutRipple2 mEndDateLayout; //截止时间Layout
    @ViewInject(R.id.tv_sharevote_enddate)
    private TextView mEndDateTv; //
    @ViewInject(R.id.ll_sharevideo_root)
    private LinearLayout mVideoRootLayout; // 视频根Layout
    @ViewInject(R.id.ll_sharevideo_item)
    private LinearLayout mVideoLayout; // 视频Layout
    @ViewInject(R.id.cm_select_ll_select)
    private LinearLayout mSelectRootLayout; // 选择分享人员Layout
    @ViewInject(R.id.cm_select_tv_name)
    private TextView mSelectTv; // 分享范围
    @ViewInject(R.id.cm_select_fl_select)
    private FlowLayout mSelectFlowLayout; // 分享人员Layout

    private static final int CHOSE_FILE_CODE = 1; //选择文件requestCode
    private static final int CHOSE_IMG_CODE = 2; //选择图片requestCode
    public static final int CHOSE_IMGVOTE_CODE = 3; //选择图片投票requestCode
    private static final int CHOSE_VIDEOFILE_CODE = 4; //选择视频requestCode
    private static final int CHOSE_VIDEORECORD_CODE = 5; //录制视频requestCode
    private static final int CHOSE_PERSON_CODE = 6; //选择人员requestCode

    private DisplayImageOptions options;
    private SimpleDateFormat format;
    private Dialog mLoadingDialog; //加载框
    private ArrayList<UserNoteFile> fileList, imgList, videoList; // 文件、图片、视频列表数据
    private ArrayList<UserNoteVote> voteList, imgVoteList; // 投票列表数据
    private ArrayList<UserInfo> selectList; // 分享人员列表数据
    private ShareVoteAdapter shareAdapter; // 投票adapter
    private ShareImgVoteAdapter shareImgAdapter; //图片投票adapter
    private TimePickerView mEndPopupWindow; //投票截止时间选择器
    private int addType = 0; //当前选择的类型
    private boolean isRecordVideo; //是否录制视频
    private String recordPath; //录制视频存放路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initView();
                options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.color.gray_1)
//		.showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
//		.showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
                        .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                        .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                        .build(); // 创建配置过得DisplayImageOption对象
            }
        }, 100);
    }

    private void initView() {
        /*mHeaderCancelBtn.setVisibility(View.GONE);
        mHeaderTitleTv.setText(R.string.share);
        mHeaderSendTv.setText(R.string.publish);
        mHeaderSendTv.setVisibility(View.GONE);*/
        mContentEt.setHint(R.string.content_hint);

        fileList = new ArrayList<>();
        imgList = new ArrayList<>();
        videoList = new ArrayList<>();
        voteList = new ArrayList<>();

        shareAdapter = new ShareVoteAdapter(this, voteList);
        mVoteLv.setAdapter(shareAdapter);

        imgVoteList = new ArrayList<>();
        shareImgAdapter = new ShareImgVoteAdapter(this, imgVoteList);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_vote_txt:
                        mVoteLv.setAdapter(shareAdapter);
                        mVoteAddBtn.setVisibility(View.VISIBLE);
                        mVoteTagLayout.setVisibility(View.GONE);
                        break;
                    case R.id.rb_vote_img:
                        mVoteLv.setAdapter(shareImgAdapter);
                        mVoteAddBtn.setVisibility(View.GONE);
                        mVoteTagLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        format = new SimpleDateFormat(getString(R.string.timeformat6), Locale.getDefault());
        mEndPopupWindow = new TimePickerView(this, TimePickerView.Type.ALL);// 时间选择器
        mEndPopupWindow.setTitle("截止时间");
        mEndPopupWindow.setCancelable(true);
        mEndPopupWindow.setOnShowListener(this);
        mEndPopupWindow.setOnDismissListener(this);
        mEndPopupWindow.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                if (date.getTime() < new Date().getTime()) {
                    ToastUtil.showToast(ShareActivity.this, "截止时间不能小于当前时间！");
                } else {
                    String endTime = format.format(date);
                    mEndDateTv.setText(endTime);
                }
            }
        });
        mEndDateLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEndPopupWindow.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                String content = mContentEt.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(this, R.string.empty_content_tips);
                } else {
                    if (addType == UserNoteAddTypes.Link) {
                        String linkUrl = mLinkUrlEt.getText().toString().trim();
                        String linkName = mLinkNameEt.getText().toString().trim();
                        if (TextUtils.isEmpty(linkUrl)) {
                            ToastUtil.showToast(this, R.string.share_empty_linkurl);
                            return;
                        }
                        if (TextUtils.isEmpty(linkName)) {
                            ToastUtil.showToast(this, R.string.share_empty_linkdesc);
                            return;
                        }
                    } else if (addType == UserNoteAddTypes.Vote) {
                        if (mRadioGroup.getCheckedRadioButtonId() == R.id.rb_vote_txt) { //文字
                            for (int i = 0, size = voteList.size(); i < size; i++) {
                                UserNoteVote vote = voteList.get(i);
                                if (!validate(vote, i + 1))
                                    return;
                            }
                        } else { //图片
                            for (int i = 0, size = imgVoteList.size(); i < size; i++) {
                                UserNoteVote vote = imgVoteList.get(i);
                                if (!validate2(vote, i + 1))
                                    return;
                            }
                        }
                    } else if (addType == UserNoteAddTypes.Video && isRecordVideo) {
                        uploadFile(recordPath);
                        return;
                    }
                    send(content);
                }
                break;
        }
    }

    /**
     * 切换底部页签颜色高亮
     *
     * @param id 页签id
     */
    private void switchTab(int id) {
        IconTextView tab1 = (IconTextView) findViewById(R.id.share_btn_file);
        IconTextView tab2 = (IconTextView) findViewById(R.id.share_btn_img);
        IconTextView tab3 = (IconTextView) findViewById(R.id.share_btn_link);
        IconTextView tab4 = (IconTextView) findViewById(R.id.share_btn_vote);
        IconTextView tab5 = (IconTextView) findViewById(R.id.share_btn_video);
        IconTextView tab6 = (IconTextView) findViewById(R.id.share_btn_location);
        tab1.setTextColor(getResources().getColor(tab1.getId() == id ? R.color.cm_main_text : R.color.dynamicadd_tab_text));
        tab2.setTextColor(getResources().getColor(tab2.getId() == id ? R.color.cm_main_text : R.color.dynamicadd_tab_text));
        tab3.setTextColor(getResources().getColor(tab3.getId() == id ? R.color.cm_main_text : R.color.dynamicadd_tab_text));
        tab4.setTextColor(getResources().getColor(tab4.getId() == id ? R.color.cm_main_text : R.color.dynamicadd_tab_text));
        tab5.setTextColor(getResources().getColor(tab5.getId() == id ? R.color.cm_main_text : R.color.dynamicadd_tab_text));
        tab6.setTextColor(getResources().getColor(tab6.getId() == id ? R.color.cm_main_text : R.color.dynamicadd_tab_text));
    }

    @Event({R.id.share_btn_file, R.id.share_btn_img, R.id.share_btn_link, R.id.share_btn_vote, R.id.share_btn_video, R.id.share_btn_location,
            R.id.itv_sharefile_del, R.id.itv_shareimg_del, R.id.itv_sharelink_del, R.id.itv_sharevote_del, R.id.itv_sharevideo_del})
    private void onSwitchClick(View v) {
        switchTab(v.getId());
        switch (v.getId()) {
            case R.id.share_btn_file:
                addType = UserNoteAddTypes.File;
                mFileRootLayout.setVisibility(View.VISIBLE);
                mImgRootLayout.setVisibility(View.GONE);
                mLinkRootLayout.setVisibility(View.GONE);
                mVoteRootLayout.setVisibility(View.GONE);
                mVideoRootLayout.setVisibility(View.GONE);
                break;
            case R.id.share_btn_img:
                addType = UserNoteAddTypes.Image;
                mFileRootLayout.setVisibility(View.GONE);
                mImgRootLayout.setVisibility(View.VISIBLE);
                mLinkRootLayout.setVisibility(View.GONE);
                mVoteRootLayout.setVisibility(View.GONE);
                mVideoRootLayout.setVisibility(View.GONE);
                break;
            case R.id.share_btn_link:
                addType = UserNoteAddTypes.Link;
                mFileRootLayout.setVisibility(View.GONE);
                mImgRootLayout.setVisibility(View.GONE);
                mLinkRootLayout.setVisibility(View.VISIBLE);
                mVoteRootLayout.setVisibility(View.GONE);
                mVideoRootLayout.setVisibility(View.GONE);
                break;
            case R.id.share_btn_vote:
                addType = UserNoteAddTypes.Vote;
                mFileRootLayout.setVisibility(View.GONE);
                mImgRootLayout.setVisibility(View.GONE);
                mLinkRootLayout.setVisibility(View.GONE);
                mVoteRootLayout.setVisibility(View.VISIBLE);
                mVideoRootLayout.setVisibility(View.GONE);
                break;
            case R.id.share_btn_video:
                addType = UserNoteAddTypes.Video;
                mFileRootLayout.setVisibility(View.GONE);
                mImgRootLayout.setVisibility(View.GONE);
                mLinkRootLayout.setVisibility(View.GONE);
                mVoteRootLayout.setVisibility(View.GONE);
                mVideoRootLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.itv_sharefile_del:
            case R.id.itv_shareimg_del:
            case R.id.itv_sharelink_del:
            case R.id.itv_sharevote_del:
            case R.id.itv_sharevideo_del:
                addType = 0;
                mFileRootLayout.setVisibility(View.GONE);
                mImgRootLayout.setVisibility(View.GONE);
                mLinkRootLayout.setVisibility(View.GONE);
                mVoteRootLayout.setVisibility(View.GONE);
                mVideoRootLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Event({R.id.btn_sharefile_add, R.id.btn_shareimg_add, R.id.btn_sharevote_add, R.id.btn_sharevideo_add, R.id.btn_sharevideo_record})
    private void onAddActionClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sharefile_add: //添加文件
                Intent fileIntent = new Intent(this, FileSelectActivity.class);
                fileIntent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                fileIntent.putExtra("click_pos_y", location[1]);
                startActivityForResult(fileIntent, CHOSE_FILE_CODE);
                break;
            case R.id.btn_shareimg_add: //添加图片
                Intent imgIntent = new Intent(this, FileSelectActivity2.class);
                imgIntent.putExtra(FileSelectActivity2.EXTRA_SELECT_TYPE, FileSelectActivity2.MULTI_SELECT);
                imgIntent.putExtra(FileSelectActivity2.EXTRA_FILE_TYPE, 2);
                imgIntent.putExtra("start_anim", false);
                int[] location2 = new int[2];
                v.getLocationOnScreen(location2);
                imgIntent.putExtra("click_pos_y", location2[1]);
                startActivityForResult(imgIntent, CHOSE_IMG_CODE);
                break;
            case R.id.btn_sharevote_add: //添加投票
                UserNoteVote vote = new UserNoteVote();
                voteList.add(vote);
                shareAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_sharevideo_add: //从知识库上传视频
                Intent videoFileIntent = new Intent(this, FileSelectActivity2.class);
                videoFileIntent.putExtra(FileSelectActivity2.EXTRA_SELECT_TYPE, FileSelectActivity2.RADIO_SELECT);
                videoFileIntent.putExtra(FileSelectActivity2.EXTRA_FILE_TYPE, 3);
                videoFileIntent.putExtra("start_anim", false);
                int[] location3 = new int[2];
                v.getLocationOnScreen(location3);
                videoFileIntent.putExtra("click_pos_y", location3[1]);
                startActivityForResult(videoFileIntent, CHOSE_VIDEOFILE_CODE);
                break;
            case R.id.btn_sharevideo_record: //录制视频
                Intent videoRecordIntent = new Intent(this, CameraActivity.class);
                videoRecordIntent.putExtra(CameraActivity.EXTRA_CAMERA_TYPE, CameraActivity.TYPE_RECORD);
                startActivityForResult(videoRecordIntent, CHOSE_VIDEORECORD_CODE);
                break;
        }
    }

    @Event({R.id.cm_select_ll_select, R.id.ll_share_select})
    private void onSelectClick(View v) {
        Intent intent = new Intent(this, ContactSelectActivity.class);
        intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
        intent.putExtra("select_list", selectList);
        intent.putExtra("has_oneself", false);
        intent.putExtra("start_anim", false);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        intent.putExtra("click_pos_y", location[1]);
        startActivityForResult(intent, CHOSE_PERSON_CODE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mContentEt.clearFocus(); //防止跳转其他页面后回到该页面滚动到mContentEt焦点位置
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CHOSE_FILE_CODE: //选择的文件
                    ArrayList<Files> fileList = (ArrayList<Files>) data.getSerializableExtra("select_list");
                    for (int i = 0; i < fileList.size(); i++) {
                        UserNoteFile file = HelpUtil.files2UserNoteFile(fileList.get(i));
                        addFileItem(file);
                    }
                    break;
                case CHOSE_IMG_CODE: //选择的图片
                    ArrayList<Files> imgList = (ArrayList<Files>) data.getSerializableExtra("select_list");
                    for (int i = 0; i < imgList.size(); i++) {
                        UserNoteFile file = HelpUtil.files2UserNoteFile(imgList.get(i));
                        addImgItem(file);
                    }
                    break;
                case CHOSE_IMGVOTE_CODE: //选择投票的图片
                    Files img = (Files) data.getSerializableExtra("select_file");
                    int position = data.getIntExtra(FileSelectActivity2.EXTRA_POSITION, 0);
                    UserNoteVote unv = imgVoteList.get(position);
                    unv.Url = img.Url;
                    unv.CompanyCode = img.CompanyCode;
                    imgVoteList.set(position, unv);
                    shareImgAdapter.notifyDataSetChanged();
                    break;
                case CHOSE_VIDEOFILE_CODE: //选择的视频
                    isRecordVideo = false;
                    Files file = (Files) data.getSerializableExtra("select_file");
                    addFilesVideo(file);
                    break;
                case CHOSE_VIDEORECORD_CODE: //录制的视频
                    isRecordVideo = true;
                    recordPath = data.getStringExtra(CameraActivity.RESULT_DATA);
                    addRecordVideo(recordPath);
                    break;
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

                    ((TextView) findViewById(R.id.tv_share_select)).setText(selectList.size() > 0 ? "部分好友" : "公开");
                    break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra("video_url")) {
            String vPath = intent.getStringExtra("video_url");
            uploadFile(vPath);
        }
    }

    /**
     * 显示选择的文件
     *
     * @param file
     */
    private void addFileItem(final UserNoteFile file) {
        final View childView = LayoutInflater.from(this).inflate(R.layout.share_tab_file_item, null);
        IconTextView delTv = (IconTextView) childView.findViewById(R.id.tv_sharefile_del);
        ImageView iconIv = (ImageView) childView.findViewById(R.id.iv_sharefile_icon);
        TextView nameTv = (TextView) childView.findViewById(R.id.tv_sharefile_name);
        TextView timeTv = (TextView) childView.findViewById(R.id.tv_sharefile_time);
        TextView sizeTv = (TextView) childView.findViewById(R.id.tv_sharefile_size);
        TextView projectTv = (TextView) childView.findViewById(R.id.tv_sharefile_project);

        delTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog(ShareActivity.this).builder()
                        .setMsg(getString(R.string.deletefile_tips))
                        .setPositiveButton(getString(R.string.ok), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFileLayout.removeView(childView);
                                fileList.remove(file);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();

            }
        });
        iconIv.setImageResource(FileUtil.getResIconId(file.FileName));
        nameTv.setText(file.FileName);
        timeTv.setText("");
        sizeTv.setText(FileUtil.getReadableFileSize(file.Length));
        mFileLayout.addView(childView);
        fileList.add(file);
    }

    /**
     * 显示选择的图片
     *
     * @param file
     */
    private void addImgItem(final UserNoteFile file) {
        final View childView = LayoutInflater.from(this).inflate(R.layout.share_tab_image_item, null);
        ImageView iconIv = (ImageView) childView.findViewById(R.id.iv_shareimg_icon);
        IconTextView delTv = (IconTextView) childView.findViewById(R.id.tv_shareimg_del);

        String url = HelpUtil.getFileURL(file);
        ImageLoader.getInstance().displayImage(url, new ImageViewAware(iconIv), options, new ImageSize(DisplayUtil.dip2px(this, 40), DisplayUtil.dip2px(this, 40)), null, null);

        delTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog(ShareActivity.this).builder()
                        .setMsg(getString(R.string.deleteimage_tips))
                        .setPositiveButton(getString(R.string.ok), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mImgFlowLayout.removeView(childView);
                                imgList.remove(file);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
            }
        });
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.rightMargin = DisplayUtil.dip2px(this, 10);
        mImgFlowLayout.addView(childView, params);
        imgList.add(file);
    }

    /**
     * 显示录制的视频
     *
     * @param localPath
     */
    private void addRecordVideo(String localPath) {
        mVideoLayout.removeAllViews();
        JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) LayoutInflater.from(this).inflate(R.layout.dynamic_item_video2, null, false);
        jcVideoPlayerStandard.setUp(localPath, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(localPath);
            Bitmap bitmap = mmr.getFrameAtTime();
            if (bitmap != null)
                jcVideoPlayerStandard.thumbImageView.setImageBitmap(bitmap);
            mmr.release();
            mVideoLayout.addView(jcVideoPlayerStandard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示选择的知识库文件
     *
     * @param file
     */
    private void addFilesVideo(final Files file) {
        if (file != null) {
            mVideoLayout.removeAllViews();
            videoList.clear();
            videoList.add(HelpUtil.files2UserNoteFile(file));
            JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) LayoutInflater.from(this).inflate(R.layout.dynamic_item_video2, null, false);
            String companyCode;
            if (TextUtils.isEmpty(file.CompanyCode)) {
                companyCode = PrefsUtil.readUserInfo().CompanyCode;
            } else {
                companyCode = file.CompanyCode;
            }
            final String fileUrl;
            if (file.Url.startsWith("/") || file.Url.startsWith("\\")) {
                fileUrl = Const.BASE_URL + "/" + "Resource" + file.Url.replaceAll("\\\\", "/");
            } else {
                fileUrl = Const.BASE_URL + "/" + "Resource/" + companyCode + "/UserFile/" + file.Url;
            }
            jcVideoPlayerStandard.setUp(fileUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(fileUrl, new HashMap<String, String>());//获取网络视频
                Bitmap bitmap = mmr.getFrameAtTime();
                if (bitmap != null)
                    jcVideoPlayerStandard.thumbImageView.setImageBitmap(bitmap);
                mmr.release();
                mVideoLayout.addView(jcVideoPlayerStandard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传录制的视频文件
     *
     * @param path
     */
    private void uploadFile(final String path) {
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
                ToastUtil.showToast(ShareActivity.this, R.string.filelist_upload_error);
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
//                    ToastUtil.showToast(ShareActivity.this, R.string.filelist_upload_success, R.drawable.tishi_ico_gougou);
//                    addRecordVideo(respList.get(0).FileInfo, path);
                    Files file = respList.get(0).FileInfo;
                    if (file != null) {
                        videoList.clear();
                        file.Url = "/" + file.CompanyCode + "/UserFile/" + file.Url;
                        videoList.add(HelpUtil.files2UserNoteFile(file));
                        send(mContentEt.getText().toString());
                    }
                } else {
                    ToastUtil.showToast(ShareActivity.this, R.string.filelist_upload_error);
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
        un.Roles = new ArrayList<>();
        switch (addType) {
            case UserNoteAddTypes.Image:
                un.AddProperty = new Gson().toJson(imgList);
                break;
            case UserNoteAddTypes.File:
                un.AddProperty = new Gson().toJson(fileList);
                break;
            case UserNoteAddTypes.Link:
                UserNoteLink link = new UserNoteLink();
                link.addr = mLinkUrlEt.getText().toString().trim();
                link.desc = mLinkNameEt.getText().toString().trim();
                un.AddProperty = new Gson().toJson(link);
                break;
            case UserNoteAddTypes.Vote:
                ArrayList<UserRootVote> rootVoteList = new ArrayList<UserRootVote>();
                UserRootVote rootVote = new UserRootVote();
                if (mRadioGroup.getCheckedRadioButtonId() == R.id.rb_vote_txt) { //文字
                    rootVote.Content = voteList;
                } else {
                    imgVoteList.get(0).Tag = mVoteTagEvt.getTagStr();
                    rootVote.Content = imgVoteList;
                }
                rootVote.Type = 1;
                rootVote.EndTime = mEndDateTv.getText().toString();
                rootVoteList.add(rootVote);
                un.AddProperty = new Gson().toJson(rootVoteList);
                break;
            case UserNoteAddTypes.Video:
                un.AddProperty = new Gson().toJson(videoList);
                break;
        }
        if (imgList.size() == 0 && fileList.size() == 0 && videoList.size() == 0 && addType != UserNoteAddTypes.Link && addType != UserNoteAddTypes.Vote) {
            un.AddType = 0;
        }
        ArrayList<NoteRole> nrList = new ArrayList<>();
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
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(ShareActivity.this, R.string.publish_error);
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            /*@Override
            public void onParseSuccess(ApiEntity.APIResult respInfo) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (respInfo != null && respInfo.State == 1) {
                    ToastUtil.showToast(ShareActivity.this, R.string.publish_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    sendBroadcast(intent); // 刷新首页列表
                    finish();
                } else {
                    ToastUtil.showToast(ShareActivity.this, R.string.publish_error);
                }
            }*/
            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(ShareActivity.this, R.string.publish_success, R.drawable.tishi_ico_gougou);
                    Intent intent = new Intent(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                    sendBroadcast(intent); // 刷新首页列表
                    finish();
                } else {
                    ToastUtil.showToast(ShareActivity.this, R.string.publish_error);
                }
            }
        });
    }

    /**
     * 非空验证
     *
     * @param vote
     * @param position
     * @return
     */
    private boolean validate(UserNoteVote vote, int position) {
        boolean isSuccess = false;
        String tip = "";
        if (TextUtils.isEmpty(vote.Text)) {
            tip = getString(R.string.share_empty_votename);
        } else {
            isSuccess = true;
        }
        if (!isSuccess) {
            ToastUtil.showToast(this, "【选项" + position + "】" + tip);
        }

        return isSuccess;
    }

    private boolean validate2(UserNoteVote vote, int position) {
        boolean isSuccess = false;
        String tip = "";
        if (TextUtils.isEmpty(vote.Url)) {
            tip = "请选择图片！";
        } else if (TextUtils.isEmpty(vote.Text)) {
            tip = getString(R.string.share_empty_votename);
        } else {
            isSuccess = true;
        }
        if (!isSuccess) {
            ToastUtil.showToast(this, "【选项" + position + "】" + tip);
        }

        return isSuccess;
    }

    //===OnShowListener ↓=== 防止滑动到最底端，弹出选择器后无法滚动选择问题
    @Override
    public void onShow(Object o) {
        getSwipeBackLayout().setEnableGesture(false);
    }

    @Override
    public void onDismiss(Object o) {
        getSwipeBackLayout().setEnableGesture(true);
    }
    //===OnDismissListener ↑===
}
