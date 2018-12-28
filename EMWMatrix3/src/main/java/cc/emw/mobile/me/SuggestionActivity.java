package cc.emw.mobile.me;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.me.adapter.ImageGridAdapter;
import cc.emw.mobile.me.imagepicker.ImagePicker;
import cc.emw.mobile.me.imagepicker.bean.ImageItem;
import cc.emw.mobile.me.imagepicker.ui.ImageGridActivity;
import cc.emw.mobile.me.imagepicker.view.CropImageView;
import cc.emw.mobile.me.view.PicassoImageLoader;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.UserFeedBack;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.util.statusbar.Eyes;
import cc.emw.mobile.view.MyGridView;
import cc.emw.mobile.view.SegmentedGroup;

/**
 * 我·关于》意见反馈
 *
 * @author zrjt
 */
@ContentView(R.layout.activity_about_suggestion)
public class SuggestionActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeaderSubmitTv;
    @ViewInject(R.id.segmented_plan_type)
    private SegmentedGroup mRadioGroup; // 类型RadioGroup

    @ViewInject(R.id.et_suggestion_content)
    private EditText mContentEt;
    @ViewInject(R.id.et_suggestion_contact)
    private EditText mContactEt;
    @ViewInject(R.id.me_suggest_image_giv)
    private MyGridView mGridView;

    private Dialog mLoadingDialog; // 加载框
    private static final int REQUEST_PICK_PHOTO = 100;
    private ImageGridAdapter mAdaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Eyes.setStatusBarLightMode(this, Color.WHITE);
        super.onCreate(savedInstanceState);
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.suggestion);
        mHeaderSubmitTv.setVisibility(View.VISIBLE);
        mHeaderSubmitTv.setText(R.string.submit);
        mAdaper = new ImageGridAdapter(getApplication());
        mGridView.setAdapter(mAdaper);
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setMultiMode(true);
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(6);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素\
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right, R.id.ll_me_add_suggest_image})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
                String content = mContentEt.getText().toString().trim();
                String contact = mContactEt.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    submitSuggest(content, contact);
                } else {
                    ToastUtil.showToast(SuggestionActivity.this, R.string.suggestion_empty_content);
                }
                break;
            case R.id.ll_me_add_suggest_image:
//                Intent intent = new Intent("com.ns.mutiphotochoser.sample.action.CHOSE_PHOTOS");
//                //指定图片选择数
//                intent.putExtra(Constant.EXTRA_PHOTO_LIMIT, 6);
//                startActivityForResult(intent, REQUEST_PICK_PHOTO);
                Intent intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_PICK_PHOTO);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                mAdaper.swapDatas(images);
            }
        }
//        switch (requestCode) {
//            case REQUEST_PICK_PHOTO:
//                ArrayList<String> images = data.getStringArrayListExtra(Constant.EXTRA_PHOTO_PATHS);
//                mAdaper.swapDatas(images);
//                break;
//        }
    }

    private void upLoadSuggestImg(String path) {
        RequestParam params = new RequestParam(Const.UPLOAD_IMAGE_URL);
        params.setMultipart(true);
        File file = new File(path);
        params.addBodyParameter(file.getName(), file);
        x.http().post(params, new RequestCallback<String>(String.class) {

            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }
        });
    }

    /**
     * 提交意见反馈
     */
    private void submitSuggest(String content, String contact) {
        UserFeedBack ufb = new UserFeedBack();
        StringBuilder str = new StringBuilder(content);
        if (!TextUtils.isEmpty(contact)) {
            str.append("/").append(contact);
        }
        ufb.Content = str.toString();
        ufb.FeedType = 1;
        API.UserAPI.AddFeedBack(ufb, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                Toast.makeText(SuggestionActivity.this, ex.toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(SuggestionActivity.this, R.string.suggestion_submit_success, R.drawable.tishi_ico_gougou);
                finish();
            }
        });
    }
}
