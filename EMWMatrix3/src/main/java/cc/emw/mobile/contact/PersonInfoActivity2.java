package cc.emw.mobile.contact;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.me.entity.ClassSkill;
import cc.emw.mobile.me.entity.JobExperience;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;

@ContentView(R.layout.activity_person_info2)
public class PersonInfoActivity2 extends BaseActivity {

    public static final String PERSON_ID = "person_id";
    private int userId;

    @ViewInject(R.id.civ_user_img)
    private CircleImageView mImgHead;
    @ViewInject(R.id.tv_user_name)
    private TextView mUserName;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.tv_add_friend)
    private TextView mAddFriend;
    @ViewInject(R.id.tv_user_experience)
    private TextView mTvExperience;
    @ViewInject(R.id.tv_user_skill)
    private TextView mTvSkill;

    private List<JobExperience> mJobExperiences = new ArrayList<>();
    private DisplayImageOptions options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Eyes.setStatusBarLightMode(this, Color.WHITE);
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.trans_bg) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.trans_bg) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.trans_bg) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("详细资料");
        mHeaderTitleTv.setTextColor(Color.parseColor("#FF202020"));
        mHeaderTitleTv.setTextSize(18);
        initData();
    }

    private void initData() {
        userId = getIntent().getIntExtra(PERSON_ID, 0);
        if (userId != 0) {
            getUserInfoById(userId);
        } else {
            AlertDialog dialog = new AlertDialog(PersonInfoActivity2.this).builder();
            dialog.setCancelable(false).setMsg("无法获取该用户信息");
            dialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollToFinishActivity();
                }
            }).show();
        }
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.tv_add_friend})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                finish();
                break;
            case R.id.tv_add_friend:
                Intent intent = new Intent(this, AddFriendGroupSelectActivity.class);
                intent.putExtra(AddFriendGroupSelectActivity.USER_ID, userId);
                startActivity(intent);
                finish();
//                addFriend();
                break;
        }
    }

    /**
     * 添加好友
     */
    private void addFriend() {
        mAddFriend.setText("等待验证");
        API.UserPubAPI.AddPubConApply(userId, new RequestCallback<String>(String.class) {

            @Override
            public void onSuccess(String result) {
                if (result != null && Integer.valueOf(result) > 0) {
                    Toast.makeText(PersonInfoActivity2.this, "好友申请已发出,请耐心等待", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PersonInfoActivity2.this, "添加失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(PersonInfoActivity2.this, "添加失败", Toast.LENGTH_SHORT).show();
            }
        });
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
                ToastUtil.showToast(PersonInfoActivity2.this, "获取用户信息失败,请稍候重试");
            }

            @Override
            public void onParseSuccess(UserInfo userInfo) {
                if (userInfo != null) {
                    try {
                        mAddFriend.setVisibility(userInfo.IsFollow && userInfo.ID == userId ? View.GONE : View.VISIBLE);
                        mUserName.setText(TextUtils.isEmpty(userInfo.Name) ? "未知" : userInfo.Name);
                        mImgHead.setTvBg(EMWApplication.getIconColor(userInfo.ID), userInfo.Name, 40);
                        String uri = String.format(Const.DOWN_ICON_URL,
                                userInfo.CompanyCode, userInfo.Image);
                        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(mImgHead), options,
                                new ImageSize(DisplayUtil.dip2px(PersonInfoActivity2.this, 40), DisplayUtil.dip2px(PersonInfoActivity2.this, 40)), null, null);
                        Gson gson = new Gson();
                        if (!TextUtils.isEmpty(userInfo.ClassSkill)) {  //该用户的职业技能
                            String resultStr = StringUtils.replaceBlank(userInfo.ClassSkill);
                            ClassSkill classSkill = gson.fromJson(resultStr, ClassSkill.class);
                            if (TextUtils.isEmpty(classSkill.skillName)) {
                                mTvSkill.setText("无");
                            } else {
                                mTvSkill.setText(classSkill.skillName);
                            }
                        } else {
                            mTvSkill.setText("无");
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
                                mTvExperience.setText(mJobExperiences.get(0).userCompany);
                            } else {
                                mTvExperience.setText("无");
                            }
                        } else {
                            mTvExperience.setText("无");
                        }
//                        //教育背景
//                        if (!TextUtils.isEmpty(userInfo.College)) {
//                            mTvAddCollege.setVisibility(View.GONE);
//                            String resultStr = StringUtils.replaceBlank(userInfo.College);
//                            mCollege = gson.fromJson(resultStr, College.class);
//                            addColleges(mCollege, mLlCollegeContain);
//                        } else {
//                            mTvAddCollege.setVisibility(View.VISIBLE);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
