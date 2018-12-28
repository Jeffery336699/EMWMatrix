package cc.emw.mobile.contact;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.adapter.GroupCreateAdapter;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.AnimatedColorPickerDialog;
import cc.emw.mobile.view.MyGridView;
import cc.emw.mobile.view.SwitchButton;
/*
**组建圈子并命名
 */
@ContentView(R.layout.activity_groups_create_activitys)
public class GroupsCreateActivitys extends BaseActivity {

    @ViewInject(R.id.mgv_group_create_head_img)
    private MyGridView myGridView;
    @ViewInject(R.id.et_group_create_edit_name)
    private EditText mEdit;
    @ViewInject(R.id.group_create_sb_apply)
    private SwitchButton mPrivate; // 需要申请
    @ViewInject(R.id.group_create_et_memo)
    private EditText mMemoEt; // 圈子描述
    @ViewInject(R.id.ll_calendar_color)
    private LinearLayout mLlColor; // 颜色Layout
    @ViewInject(R.id.tv_calendar_color_select)
    private TextView mTvColorSelect;    //颜色
    private static final int REQUEST_CAPTURE_PHOTO = 1010;
    private Uri uri;
    private int[] colors;   // 群组颜色集合
    private int colorWhat;
    private GroupInfo groupInfo = new GroupInfo();
    private List<String> nativePic;
    private GroupCreateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        nativePic = new ArrayList();
        nativePic.add("drawable://");
        nativePic.add("drawable://" + R.drawable.group1);
        nativePic.add("drawable://" + R.drawable.group2);
        nativePic.add("drawable://" + R.drawable.group3);
        nativePic.add("drawable://" + R.drawable.group4);
        nativePic.add("drawable://" + R.drawable.group5);
        nativePic.add("drawable://" + R.drawable.group6);
        nativePic.add("drawable://" + R.drawable.group7);
        nativePic.add("drawable://" + R.drawable.group8);
        nativePic.add("drawable://" + R.drawable.group9);
        nativePic.add("drawable://" + R.drawable.group10);
        nativePic.add("drawable://" + R.drawable.group11);
        nativePic.add("drawable://" + R.drawable.group12);
        nativePic.add("drawable://" + R.drawable.group13);
        nativePic.add("drawable://" + R.drawable.group14);
        nativePic.add("drawable://" + R.drawable.group15);
        adapter = new GroupCreateAdapter(this);
        adapter.setData(nativePic);
        myGridView.setAdapter(adapter);

        colors = new int[]{getResources().getColor(R.color.cal_color0), getResources().getColor(R.color.cal_color1),
                getResources().getColor(R.color.cal_color2), getResources().getColor(R.color.cal_color3),
                getResources().getColor(R.color.cal_color4), getResources().getColor(R.color.cal_color5),
                getResources().getColor(R.color.cal_color6)};
        Drawable left = getResources().getDrawable(R.drawable.share_circle1_nor);
        left.setBounds(0, 0, DisplayUtil.dip2px(this, 25), DisplayUtil.dip2px(this, 25));
        mTvColorSelect.setCompoundDrawables(left, null, null, null);
        mLlColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AnimatedColorPickerDialog.Builder(GroupsCreateActivitys.this).setTitle("选择一种颜色").
                        setColors(colors).setOnColorClickListener(new AnimatedColorPickerDialog.ColorClickListener() {
                    @Override
                    public void onColorClick(int color) {
                        Drawable left = null;
                        if (color == getResources().getColor(R.color.cal_color0)) {
                            left = getResources().getDrawable(R.drawable.share_circle1_nor);
                            colorWhat = 0;
                        } else if (color == getResources().getColor(R.color.cal_color1)) {
                            left = getResources().getDrawable(R.drawable.share_circle2_nor);
                            colorWhat = 1;
                        } else if (color == getResources().getColor(R.color.cal_color2)) {
                            left = getResources().getDrawable(R.drawable.share_circle3_nor);
                            colorWhat = 2;
                        } else if (color == getResources().getColor(R.color.cal_color3)) {
                            left = getResources().getDrawable(R.drawable.share_circle4_nor);
                            colorWhat = 3;
                        } else if (color == getResources().getColor(R.color.cal_color4)) {
                            left = getResources().getDrawable(R.drawable.share_circle5_nor);
                            colorWhat = 4;
                        } else if (color == getResources().getColor(R.color.cal_color5)) {
                            left = getResources().getDrawable(R.drawable.share_circle6_nor);
                            colorWhat = 5;
                        } else if (color == getResources().getColor(R.color.cal_color6)) {
                            left = getResources().getDrawable(R.drawable.share_circle7_nor);
                            colorWhat = 6;
                        }
                        if (left != null) {
                            left.setBounds(0, 0, DisplayUtil.dip2px(GroupsCreateActivitys.this, 25), DisplayUtil.dip2px(GroupsCreateActivitys.this, 25));
                            mTvColorSelect.setCompoundDrawables(left, null, null, null);
                        }
                    }
                }).create().show();
            }
        });
    }

    @Event(value = {R.id.ic_tv_group_create_title_back, R.id.group_create_ll_apply, R.id.tv_group_create_title_right})
    private void onClicks(View view) {
        switch (view.getId()) {
            case R.id.tv_group_create_title_right:
                if (!TextUtils.isEmpty(mEdit.getText().toString())) {
                    groupInfo.Name = mEdit.getText().toString();
                    groupInfo.Memo = mMemoEt.getText().toString().trim();
                    groupInfo.Type = mPrivate.isChecked() ? 1 : 0;
                    groupInfo.ID = 0;
                    groupInfo.Color = colorWhat;
                    groupInfo.BackImageIndex = adapter.getLocalBackImgIndex();
                    Intent intent = new Intent(this, PersonSelectActivity.class);
                    intent.putExtra("start_anim", false);
                    intent.putExtra("groupInfo", groupInfo);
                    if (uri != null)
                        intent.putExtra("groupImg", uri.toString());
                    startActivity(intent);
                } else {
                    ToastUtil.showToast(this, "请先为圈子命名!");
                }
                break;
            case R.id.group_create_ll_apply:
                mPrivate.toggle();
                break;
            case R.id.ic_tv_group_create_title_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_PHOTO && resultCode == RESULT_OK) {
            Uri inputUri = Uri.fromFile(new File(EMWApplication.tempPath
                    + "tempraw.png"));
            Uri outputUri = Uri.fromFile(new File(EMWApplication.tempPath
                    + "tempcrop.png"));
            Crop.of(inputUri, outputUri).asSquare().start(this);
        } else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), Calendar.getInstance().getTimeInMillis() + ".png"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            uri = Crop.getOutput(result);
            adapter.setPreImg(uri);
            adapter.notifyDataSetChanged();
//            uploadImage(uri.getPath(), uri);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
