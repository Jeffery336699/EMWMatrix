package cc.emw.mobile.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zf.iosdialog.widget.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.adapter.ChatPhotoAndCameraAdapter;
import cc.emw.mobile.chat.utils.PhotoUtil;
import cc.emw.mobile.chat.view.PreSurfaceView;
import cc.emw.mobile.dynamic.DynamicDiscussActivity;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.file.FileSelectActivity2;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;

/**
 * 评论带图片
 */
public class DiscussImgLayout extends RelativeLayout {

    protected View view;
    protected FlowLayout mImgFlowLayout;
    protected Context context;

    private ArrayList<UserNote.UserNoteFile> imgList; //图片列表数据

    private RecyclerView mRvShowPhoto;
    private ChatPhotoAndCameraAdapter photoAndCameraAdapter;

    public DiscussImgLayout(Context context) {
        this(context, null);
    }

    public DiscussImgLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        imgList = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_discuss_image, this);
        /*mImgFlowLayout = (FlowLayout) view.findViewById(R.id.fl_shareimg_item);
        Button addBtn = (Button) view.findViewById(R.id.btn_shareimg_add);
        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imgIntent = new Intent(context, FileSelectActivity2.class);
                imgIntent.putExtra(FileSelectActivity2.EXTRA_SELECT_TYPE, FileSelectActivity2.MULTI_SELECT);
                imgIntent.putExtra(FileSelectActivity2.EXTRA_FILE_TYPE, 2);
                imgIntent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                imgIntent.putExtra("click_pos_y", location[1]);
                ((Activity)context).startActivityForResult(imgIntent, DynamicDiscussActivity.CHOSE_IMG_CODE);
            }
        });*/
        mRvShowPhoto = (RecyclerView) view.findViewById(R.id.rv_show_photo);
        mRvShowPhoto.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvShowPhoto.setLayoutManager(linearLayoutManager);
        photoAndCameraAdapter = new ChatPhotoAndCameraAdapter(context, 1);
        mRvShowPhoto.setAdapter(photoAndCameraAdapter);
        showPhotoWindow();
    }

    public void setData(ArrayList<ApiEntity.Files> imgList) {
        if (imgList != null && imgList.size() > 0) {
            for (int i = 0; i < imgList.size(); i++) {
                UserNote.UserNoteFile file = HelpUtil.files2UserNoteFile(imgList.get(i));
                addImgItem(file);
            }
        }
    }

    public ArrayList<UserNote.UserNoteFile> getData() {
        return imgList;
    }

    public void clearData() {
        if (imgList != null) {
            imgList.clear();
        }
        if (mImgFlowLayout != null) {
            mImgFlowLayout.removeAllViews();
        }
    }

    /**
     * 显示选择的图片
     * @param file
     */
    private void addImgItem(final UserNote.UserNoteFile file) {
        final View childView = LayoutInflater.from(context).inflate(R.layout.view_discuss_image_item, null);
        ImageView iconIv = (ImageView) childView.findViewById(R.id.iv_shareimg_icon);
//    	ImageView delBtn = (ImageView) childView.findViewById(R.id.iv_shareimg_del);
        IconTextView delTv = (IconTextView) childView.findViewById(R.id.tv_shareimg_del);

        String url = HelpUtil.getFileURL(file);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.color.gray_1)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
        ImageLoader.getInstance().displayImage(url, iconIv, options);

        delTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog(context).builder().setMsg(context.getString(R.string.deleteimage_tips))
                        .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                        .setPositiveButton(context.getString(R.string.ok), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mImgFlowLayout.removeView(childView);
                                imgList.remove(file);
                            }
                        })
                        .setNegativeButton(context.getString(R.string.cancel), new OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        }).show();
            }
        });
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        params.rightMargin = DisplayUtil.dip2px(context, 10);
        mImgFlowLayout.addView(childView, params);
        imgList.add(file);
    }

    /**
     * 相册展示界面
     */
    private void showPhotoWindow() {
        new Thread() {
            public void run() {
                final List<String> listimage = PhotoUtil.getAllPhoto(context.getContentResolver());
                new Handler(context.getMainLooper()).post(new Runnable() {
                    public void run() {
                        photoAndCameraAdapter.setData(listimage);
                        photoAndCameraAdapter.notifyDataSetChanged();
                        mRvShowPhoto.smoothScrollToPosition(1);
                    }
                });
            }
        }.start();
    }

    public PreSurfaceView getPreSurfaceView() {
        return photoAndCameraAdapter.getPreSurfaceView();
    }
}
