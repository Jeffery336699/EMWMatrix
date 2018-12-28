package cc.emw.mobile.me;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnShowListener;
import com.bigkoo.pickerview.view.WheelTime;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.zf.iosdialog.widget.ActionSheetDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.fragment.PersonFragments;
import cc.emw.mobile.dynamic.fragment.DynamicChildFragment;
import cc.emw.mobile.dynamic.fragment.DynamicFragment;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.me.entity.ClassSkill;
import cc.emw.mobile.me.entity.College;
import cc.emw.mobile.me.entity.JobExperience;
import cc.emw.mobile.me.fragment.MyInfoFragment;
import cc.emw.mobile.me.fragment.MyInfoFragment5;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.KeyBoardUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.CollapseView;
import cc.emw.mobile.view.SwipeBackScrollView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

@ContentView(R.layout.activity_person_info_edit)
public class PersonInfoEditActivity extends BaseActivity {

    public static final int REQUEST_CAPTURE_PHOTO = 1000;
    public static final int REQUEST_ADD_WORK_EXPRIENCE = 100;
    public static final int REQUEST_ADD_EDUCATION_BACKGROUND = 101;

    @ViewInject(R.id.swipe_scroll_person_edit)
    private SwipeBackScrollView mainScrollView;
    @ViewInject(R.id.iv_me_head)
    private CircleImageView mHeadIv;
    @ViewInject(R.id.tv_me_name)
    private TextView mTvName;
    @ViewInject(R.id.tv_me_depart_name)
    private TextView mTvDepart;
    @ViewInject(R.id.et_info_edit_mail)
    private EditText mMail;
    @ViewInject(R.id.et_info_edit_phone)
    private EditText mPhone;
    @ViewInject(R.id.tv_info_edit_depart_name)
    private TextView mDepartName;
    @ViewInject(R.id.et_info_edit_name)
    private EditText mName;
    @ViewInject(R.id.tv_info_edit_age)
    private TextView mAge;
    @ViewInject(R.id.ll_me_age)
    private CollapseView mCvAgeLayout;
    @ViewInject(R.id.rb_man)
    private RadioButton mRbMan;
    @ViewInject(R.id.rb_women)
    private RadioButton mRbWomen;
    @ViewInject(R.id.et_class_skill)
    private EditText mEtClassSkill;
    @ViewInject(R.id.ll_my_college_contain)
    private LinearLayout mLlCollegeContain;
    @ViewInject(R.id.ll_my_experience_contain)
    private LinearLayout mLlExperienceContain;
    @ViewInject(R.id.tv_add_college)
    private TextView mTvAddCollege;

    private UserInfo userInfo;
    private Dialog mLoadingDialog;
    private Uri uri;
    private int tag;
    private TimePickerView mAgePickerView;
    private SimpleDateFormat format2;
    private WheelTime mWheelTime;
    private College mCollege;
    private List<JobExperience> mJobExperiences = new ArrayList<>(); //我的工作经历

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
        init();
//        initCollapseView();
    }

    private void init() {
        userInfo = (UserInfo) getIntent().getSerializableExtra("UserInfo");
        mLoadingDialog = createLoadingDialog("正在保存信息");
        format2 = new SimpleDateFormat(
                getString(R.string.timeformat7));
        if (PrefsUtil.readUserInfo().CompanyCode.equalsIgnoreCase("PUB")) {
            mName.setEnabled(true);
        } else {
            mName.setEnabled(false);
        }

        if (EMWApplication.personMap != null
                && EMWApplication.personMap
                .get(PrefsUtil.readUserInfo().ID) != null) {
            UserInfo simPleUser = EMWApplication.personMap.get(PrefsUtil
                    .readUserInfo().ID);
            String uri = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode,
                    simPleUser.Image);
            Picasso.with(this)
                    .load(uri)
                    .resize(DisplayUtil.dip2px(this, 80), DisplayUtil.dip2px(this, 80))
                    .centerCrop()
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.cm_img_head)
                    .error(R.drawable.cm_img_head)
                    .into(mHeadIv);
        }

        getUserInfoById(userInfo.ID);

        mHeadIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = 1;
                ActionSheetDialog dialog = new ActionSheetDialog(
                        PersonInfoEditActivity.this).builder();
                dialog.addSheetItem(getString(R.string.actionsheet_photo), null,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Intent intent = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(new File(
                                                EMWApplication.tempPath
                                                        + "tempraw.png")));
                                startActivityForResult(intent,
                                        REQUEST_CAPTURE_PHOTO);
                            }
                        });
                dialog.addSheetItem(getString(R.string.actionsheet_pick), null,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                Crop.pickImage(PersonInfoEditActivity.this);
                            }
                        });
                dialog.show();
            }
        });

        mAgePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);// 时间选择器
        mAgePickerView.setTitle("出生日期");
        mAgePickerView.setRange(1900, Calendar.getInstance().get(Calendar.YEAR) - 1);
        mAgePickerView.setCancelable(true);
        mAgePickerView.setCyclic(false);
        mAgePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                mAge.setText(format2.format(date));
            }
        });
        mAgePickerView.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                getSwipeBackLayout().setEnableGesture(true);
            }
        });

        mAgePickerView.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(Object o) {
                getSwipeBackLayout().setEnableGesture(false);
            }
        });
    }

    /**
     * TODO 嵌入列表中
     */
    private void initCollapseView() {
        mCvAgeLayout.setTagNameVis("", "年龄");
        mCvAgeLayout.setSwipeScrollView(mainScrollView);
        mCvAgeLayout.setContent(R.layout.item_time_select_layout);
        mWheelTime = new WheelTime(LayoutInflater.from(this).inflate(R.layout.item_time_select_layout, null));
        mCvAgeLayout.setView(mWheelTime.getView());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_PHOTO && resultCode == RESULT_OK) {
            Uri inputUri = Uri.fromFile(new File(EMWApplication.tempPath
                    + "tempraw.png"));
            Uri outputUri = Uri.fromFile(new File(EMWApplication.tempPath
                    + "tempcrop.png"));
            Crop.of(inputUri, outputUri).asSquare().start(this);
        } else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData()); // 开始裁剪
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data); // 裁剪完成
        } else if (requestCode == REQUEST_ADD_WORK_EXPRIENCE) {   //工作经验
            if (resultCode == RESULT_OK) {
                mJobExperiences = (List<JobExperience>) data.getSerializableExtra("experience");
                if (mJobExperiences != null) {
                    addExperiences(mJobExperiences, mLlExperienceContain);
                }
            }
        } else if (requestCode == REQUEST_ADD_EDUCATION_BACKGROUND) { //教育背景
            if (resultCode == RESULT_OK) {
                mCollege = (College) data.getSerializableExtra("college");
                if (mCollege != null) {
                    mTvAddCollege.setVisibility(View.GONE);
                    addColleges(mCollege, mLlCollegeContain);
                } else {
                    mTvAddCollege.setVisibility(View.VISIBLE);
                    mLlCollegeContain.removeAllViews();
                }
            }
        }
    }

    @Event(value = {R.id.cm_header_tv_right9, R.id.cm_header_btn_left9, R.id.ll_me_sex, R.id.ll_me_age, R.id.tv_add_work_experience,
            R.id.tv_add_degree, R.id.tv_add_work_skill})
    private void onHeadClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left9:
                KeyBoardUtil.hideSoftInput(mPhone, this);
                KeyBoardUtil.hideSoftInput(mMail, this);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                KeyBoardUtil.hideSoftInput(mPhone, this);
                KeyBoardUtil.hideSoftInput(mMail, this);
                if (uri != null) {
                    upload(uri.getPath());
                } else {
                    userInfo.Email = mMail.getText().toString();
                    userInfo.Phone = mPhone.getText().toString();
                    userInfo.Name = mName.getText().toString();
                    userInfo.Birthday = mAge.getText().toString();
                    if (mRbMan.isChecked())
                        userInfo.Sex = 0;
                    else
                        userInfo.Sex = 1;
                    modifyUserPub(userInfo);
//                    if (PrefsUtil.readUserInfo().CompanyCode.equalsIgnoreCase("PUB")) {
//                    modifyUserPub(userInfo);
//                    } else {
//                        modifyUser(userInfo);
//                    }
                }
                break;
            case R.id.ll_me_age:
                mAgePickerView.show();
                break;
            case R.id.tv_add_work_experience:
                Intent intentWorkExperience = new Intent(this, WorkExperienceActivity.class);
                intentWorkExperience.putExtra("start_anim", false);
                int[] location1 = new int[2];
                view.getLocationInWindow(location1);
                intentWorkExperience.putExtra("click_pos_y", location1[1]);
                intentWorkExperience.putExtra("UserInfo", userInfo);
                intentWorkExperience.putExtra("experience", (Serializable) mJobExperiences);
                startActivityForResult(intentWorkExperience, REQUEST_ADD_WORK_EXPRIENCE);
                break;
            case R.id.tv_add_degree:
                if (mCollege == null) {
                    Intent intentDegree = new Intent(this, EducationActivity.class);
                    intentDegree.putExtra("start_anim", false);
                    int[] location2 = new int[2];
                    view.getLocationInWindow(location2);
                    intentDegree.putExtra("click_pos_y", location2[1]);
                    intentDegree.putExtra("UserInfo", userInfo);
                    startActivityForResult(intentDegree, REQUEST_ADD_EDUCATION_BACKGROUND);
                }
                break;
            case R.id.tv_add_work_skill:
                ClassSkill classSkill = new ClassSkill();
                classSkill.skillName = mEtClassSkill.getText().toString();
                classSkill.skillPrivacy = 4;
                String strClassSkill = new Gson().toJson(classSkill);
                userInfo.ClassSkill = strClassSkill;
                modifyUserPubOther(userInfo);
                break;
        }

    }

    /**
     * 上传头像
     *
     * @param path
     */
    private void upload(String path) {
        RequestParam params = new RequestParam(Const.UPLOAD_IMAGE_URL);
        params.setMultipart(true);
        File file = new File(path);
        params.addBodyParameter(file.getName(), file);
        x.http().post(params, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(PersonInfoEditActivity.this, R.string.person_updatehead_error);
            }

            @Override
            public void onStarted() {
                if (mLoadingDialog != null) mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<String> result) {
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                String imgTargetStr = result.toString();
                int i = imgTargetStr.lastIndexOf("/");
                String imgName = imgTargetStr.substring(i + 1,
                        imgTargetStr.length() - 1);
                if (tag == 1) {
                    userInfo.Image = imgName;
                } else if (tag == 2) {
//                    userInfo.BackImage = imgName;
                    PrefsUtil.setKeyImgbgurl(imgName);
                }
                userInfo.Email = mMail.getText().toString();
                userInfo.Phone = mPhone.getText().toString();
                userInfo.Birthday = mAge.getText().toString();
                if (mRbMan.isChecked())
                    userInfo.Sex = 0;
                else
                    userInfo.Sex = 1;
//                if (PrefsUtil.readUserInfo().CompanyCode.equalsIgnoreCase("PUB")) {
                modifyUserPub(userInfo);
//                } else {
//                    upLoadImg(userInfo);
//                }
            }
        });
    }

    /**
     * 修改用户基本资料
     *
     * @param ui
     */
    private void modifyUserPub(final UserInfo ui) {
        API.UserPubAPI.ModifyUserById(ui,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        if (mLoadingDialog != null) mLoadingDialog.dismiss();
                        ToastUtil.showToast(PersonInfoEditActivity.this, R.string.person_updatehead_error);
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                            ToastUtil.showToast(PersonInfoEditActivity.this, R.string.person_updatehead_success, R.drawable.tishi_ico_gougou);
                            String uriHead = String.format(Const.DOWN_ICON_URL,
                                    PrefsUtil.readUserInfo().CompanyCode,
                                    userInfo.Image);

                            Picasso.with(PersonInfoEditActivity.this)
                                    .load(uriHead)
                                    .resize(DisplayUtil.dip2px(PersonInfoEditActivity.this, 60), DisplayUtil.dip2px(PersonInfoEditActivity.this, 60))
                                    .centerCrop()
                                    .config(Bitmap.Config.RGB_565)
                                    .placeholder(R.drawable.cm_img_head)
                                    .error(R.drawable.cm_img_head)
                                    .into(mHeadIv);

                            EMWApplication.personMap.put(userInfo.ID, userInfo);
                            if (EMWApplication.personSortList != null)
                                for (int i = 0; i < EMWApplication.personSortList.size(); i++) {
                                    if (EMWApplication.personSortList.get(i) != null) {
                                        if (EMWApplication.personSortList.get(i).ID == PrefsUtil.readUserInfo().ID) {
                                            EMWApplication.personSortList.get(i).Image = userInfo.Image;
                                        }
                                    }
                                }
                            PrefsUtil.readUserInfo().Name = mName.getText().toString();
                            PrefsUtil.readUserInfo().Email = userInfo.Email;
                            PrefsUtil.readUserInfo().Phone = userInfo.Phone;
                            PrefsUtil.readUserInfo().Image = userInfo.Image;
                            PrefsUtil.readUserInfo().BackImage = userInfo.BackImage;
                            PrefsUtil.readUserInfo().Birthday = mAge.getText().toString();
                            if (mRbMan.isChecked())
                                PrefsUtil.readUserInfo().Sex = 0;
                            else
                                PrefsUtil.readUserInfo().Sex = 1;
                            PrefsUtil.setKeyImgbgurl(PrefsUtil.getKeyImgbgurl());
                            PrefsUtil.saveUserInfo(PrefsUtil.readUserInfo());
                            PrefsUtil.cleanUserInfo();
                            Intent intent = new Intent();
                            intent.setAction(PersonFragments.ACTION_REFRESH_CONTACT_LIST);
                            sendBroadcast(intent);
                            intent = new Intent();
                            intent.setAction(DynamicChildFragment.ACTION_REFRESH_DYNAMIC_LIST);
                            sendBroadcast(intent);
                            intent = new Intent(MyInfoFragment5.ACTION_REFRESH_ME_LIST);
                            sendBroadcast(intent);
                            finish();
                        } else {
                            ToastUtil.showToast(PersonInfoEditActivity.this, R.string.person_updatehead_error);
                        }
                    }
                });
    }

    /**
     * 修改信息
     *
     * @param ui
     */
    private void modifyUserPubOther(final UserInfo ui) {
        API.UserPubAPI.ModifyUserOtherById(ui, 2,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        Toast.makeText(PersonInfoEditActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                            Toast.makeText(PersonInfoEditActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PersonInfoEditActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void modifyUser(final UserInfo ui) {
        API.UserAPI.ModifyUserById(ui,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        if (mLoadingDialog != null) mLoadingDialog.dismiss();
                        ToastUtil.showToast(PersonInfoEditActivity.this, R.string.person_updatehead_error);
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                            ToastUtil.showToast(PersonInfoEditActivity.this, R.string.person_updatehead_success, R.drawable.tishi_ico_gougou);
                            String uriHead = String.format(Const.DOWN_ICON_URL,
                                    PrefsUtil.readUserInfo().CompanyCode,
                                    userInfo.Image);

                            Picasso.with(PersonInfoEditActivity.this)
                                    .load(uriHead)
                                    .resize(DisplayUtil.dip2px(PersonInfoEditActivity.this, 60), DisplayUtil.dip2px(PersonInfoEditActivity.this, 60))
                                    .centerCrop()
                                    .config(Bitmap.Config.RGB_565)
                                    .placeholder(R.drawable.cm_img_head)
                                    .error(R.drawable.cm_img_head)
                                    .into(mHeadIv);

                            EMWApplication.personMap.put(userInfo.ID, userInfo);
                            PrefsUtil.readUserInfo().Name = mName.getText().toString();
                            PrefsUtil.readUserInfo().Email = userInfo.Email;
                            PrefsUtil.readUserInfo().Phone = userInfo.Phone;
                            PrefsUtil.readUserInfo().Image = userInfo.Image;
                            PrefsUtil.readUserInfo().BackImage = userInfo.BackImage;
                            PrefsUtil.setKeyImgbgurl(PrefsUtil.getKeyImgbgurl());
                            PrefsUtil.saveUserInfo(PrefsUtil.readUserInfo());
                            PrefsUtil.cleanUserInfo();
                            Intent intent = new Intent();
                            intent.setAction(PersonFragments.ACTION_REFRESH_CONTACT_LIST);
                            sendBroadcast(intent);
                            intent.setAction(DynamicFragment.ACTION_REFRESH_HOME_LIST);
                            sendBroadcast(intent);
                            intent = new Intent(MyInfoFragment.ACTION_REFRESH_ME_LIST);
                            sendBroadcast(intent);
                            finish();
                        } else {
                            ToastUtil.showToast(PersonInfoEditActivity.this, R.string.person_updatehead_error);
                        }
                    }
                });
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), Calendar.getInstance().getTimeInMillis() + ".png"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            uri = Crop.getOutput(result);
            if (tag == 1) {
                mHeadIv.setImageURI(uri);
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtil.showToast(this, Crop.getError(result).getMessage());
        }
    }

    /**
     * 根据 id 获取用户信息
     *
     * @param id
     */
    private void getUserInfoById(int id) {
        API.UserPubAPI.GetUserInfoByID(id, new RequestCallback<UserInfo>(UserInfo.class) {

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(PersonInfoEditActivity.this, "获取用户信息失败,请稍候重试");
            }

            @Override
            public void onParseSuccess(UserInfo userInfo) {
                if (userInfo != null) {
                    try {
                        mName.setText(userInfo.Name);
                        mTvName.setText(userInfo.Name);
                        mMail.setText(userInfo.Email);
                        mPhone.setText(userInfo.Phone);
                        mDepartName.setText(TextUtils.isEmpty(userInfo.DeptName) ? "暂无部门" : userInfo.DeptName);
                        mTvDepart.setText(TextUtils.isEmpty(userInfo.DeptName) ? "暂无部门" : userInfo.DeptName);
                        mRbMan.setChecked(userInfo.Sex == 0 ? true : false);
                        mRbWomen.setChecked(userInfo.Sex == 1 ? true : false);
                        mAge.setText(userInfo.Birthday.split(" ")[0]);
                        Gson gson = new Gson();
                        if (!TextUtils.isEmpty(userInfo.ClassSkill)) {  //该用户的职业技能
                            String resultStr = StringUtils.replaceBlank(userInfo.ClassSkill);
                            ClassSkill classSkill = gson.fromJson(resultStr, ClassSkill.class);
                            if (TextUtils.isEmpty(classSkill.skillName)) {
                                mEtClassSkill.setHint("请输入职业技能");
                            } else {
                                mEtClassSkill.setText(classSkill.skillName);
                            }
                        }
                        if (!TextUtils.isEmpty(userInfo.JobExperience)) {   //该用户的工作经验
                            mJobExperiences.clear();
                            String resultStr = StringUtils.replaceBlank(userInfo.JobExperience);
                            if (resultStr.startsWith("{")) {
                                JobExperience jobExperience = gson.fromJson(resultStr, JobExperience.class);
                                mJobExperiences.add(jobExperience);
                            } else if (resultStr.startsWith("[")) {
                                if (!resultStr.equals("[]") && !resultStr.equals("[null]")) {
                                    JsonArray array = new JsonParser().parse(resultStr).getAsJsonArray();
                                    for (final JsonElement elem : array) {
                                        mJobExperiences.add(gson.fromJson(elem, JobExperience.class));
                                    }
                                }
                            }
                            if (mJobExperiences.size() != 0) {
                                addExperiences(mJobExperiences, mLlExperienceContain);
                            }
                        }
                        //教育背景
                        if (!TextUtils.isEmpty(userInfo.College)) {
                            mTvAddCollege.setVisibility(View.GONE);
                            String resultStr = StringUtils.replaceBlank(userInfo.College);
                            mCollege = gson.fromJson(resultStr, College.class);
                            addColleges(mCollege, mLlCollegeContain);
                        } else {
                            mTvAddCollege.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 添加工作经历
     *
     * @param mDataList
     * @param parent
     */

    public void addExperiences(final List<JobExperience> mDataList, ViewGroup parent) {
        parent.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < mDataList.size(); i++) {
            final int index = i;
            final JobExperience jobExperience = mDataList.get(i);
            View view = inflater.inflate(R.layout.item_user_experience, null);
            TextView mTitle = (TextView) view.findViewById(R.id.tv_experience_name);
            mTitle.setText(jobExperience.userCompany);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.getDisplayWidth(this), DisplayUtil.dip2px(this, 54));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userInfo != null) {
                        Intent intentWorkExperience = new Intent(PersonInfoEditActivity.this, WorkExperienceActivity.class);
                        intentWorkExperience.putExtra("start_anim", false);
                        int[] location1 = new int[2];
                        v.getLocationInWindow(location1);
                        intentWorkExperience.putExtra("click_pos_y", location1[1]);
                        intentWorkExperience.putExtra("UserInfo", userInfo);
                        intentWorkExperience.putExtra("experience", (Serializable) mJobExperiences);
                        intentWorkExperience.putExtra("index", index);
                        startActivityForResult(intentWorkExperience, REQUEST_ADD_WORK_EXPRIENCE);
                    }
                }
            });
            parent.addView(view, params);
        }
    }

    /**
     * 添加教育背景
     *
     * @param college
     * @param parent
     */
    public void addColleges(final College college, ViewGroup parent) {
        parent.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item_user_experience, null);
        TextView mTitle = (TextView) view.findViewById(R.id.tv_experience_name);
        mTitle.setText(college.universityName);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.getDisplayWidth(this), DisplayUtil.dip2px(this, 54));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfo != null) {
                    Intent intentDegree = new Intent(PersonInfoEditActivity.this, EducationActivity.class);
                    intentDegree.putExtra("start_anim", false);
                    int[] location2 = new int[2];
                    v.getLocationInWindow(location2);
                    intentDegree.putExtra("click_pos_y", location2[1]);
                    intentDegree.putExtra("UserInfo", userInfo);
                    intentDegree.putExtra("mCollege", college);
                    startActivityForResult(intentDegree, REQUEST_ADD_EDUCATION_BACKGROUND);
                }
            }
        });
        parent.addView(view, params);
    }
}
