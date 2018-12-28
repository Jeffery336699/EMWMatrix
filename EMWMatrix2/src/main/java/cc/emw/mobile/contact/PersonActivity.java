package cc.emw.mobile.contact;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.activity.ChatActivity;
import cc.emw.mobile.contact.fragment.PersonFragment;
import cc.emw.mobile.contact.presenter.ContactPresenter;
import cc.emw.mobile.contact.view.ContactView;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.main.fragment.talker.DynamicFragment;
import cc.emw.mobile.map.RouteActivity;
import cc.emw.mobile.me.ConcernActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.UserMark;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.PopMenu.Item;
import cc.emw.mobile.view.PopMenu.OnItemSelectedListener;
import cc.emw.mobile.view.RightMenu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;
import com.zf.iosdialog.widget.ActionSheetDialog;
import com.zf.iosdialog.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.zf.iosdialog.widget.AlertDialog;

/**
 * 个人详情界面
 *
 * @author zrjt
 */
@ContentView(R.layout.activity_person)
public class PersonActivity extends BaseActivity implements ContactView {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn;
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn;

    @ViewInject(R.id.my_head_image)
    private ImageView mHeadImage;
    @ViewInject(R.id.portrait)
    private TextView mUserName;
    @ViewInject(R.id.department_name)
    private TextView mDepartment; // 部门
    @ViewInject(R.id.user_phone)
    private TextView mUserPhone;
    @ViewInject(R.id.user_mail)
    private TextView mUserMail;
    @ViewInject(R.id.work_rank)
    private TextView workRank; // 职称
    @ViewInject(R.id.contact_to_chat_online)
    private Button btnSend;
    @ViewInject(R.id.bei_zhu_name)
    private TextView bzTv;
    @ViewInject(R.id.location)
    private TextView locationTv;
    // @ViewInject(R.id.user_company)
    // private TextView mUserCompany;
    // @ViewInject(R.id.user_post)
    // private TextView mUserPost;
    // @ViewInject(R.id.user_job_number)
    private TextView mJobNumber;
    @ViewInject(R.id.contact_add_attention)
    private Button btnDoFollow; // 关注按钮
    private TextView followtext;
    private DisplayImageOptions options;
    private boolean flag = false;
    private ContactPresenter cPresenter; // mvp p处理层
    private UserInfo sUser; // 传递来的用户对象
    private Intent intent = null;
    private static final int REQUEST_CAPTURE_PHOTO = 1000;
    private String mUploadImg = ""; // 上传头像名称
    private boolean IsFromChat;
    private boolean isReqFollow;
    private Dialog mLoadingDialog; // 加载框
    private RightMenu mMenu;
    public static final int REQUESTCODE = 1;
    public static final String ACTION_REFRESH_CONTACT_LIST = "cc.emw.mobile.refresh_follow_list"; // 刷新的action
    public static final String ACTION_REFRESH_BZ = "cc.emw.mobile.refresh_bz";
    private List<UserMark> userMarks;
    private UserMark userMarkz; // 用户备注对象

    // 人员相关数目
    @ViewInject(R.id.relation_project_num)
    private TextView projectNum;
    @ViewInject(R.id.relation_task_num)
    private TextView taskNum;
    @ViewInject(R.id.relation_calendar_num)
    private TextView calendarNum;
    @ViewInject(R.id.relation_file_num)
    private TextView fileNum;

    private MyBroadcastReceiver receiver;

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_REFRESH_BZ) {
                getUserBz();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips4);
        sUser = (UserInfo) getIntent().getSerializableExtra("simple_user");
        if (getIntent().hasExtra("IsFromChat")) {
            IsFromChat = getIntent().getBooleanExtra("IsFromChat", false);
        }
        if (IsFromChat) {
            btnSend.setVisibility(View.GONE);
        } else {
            btnSend.setVisibility(View.VISIBLE);
        }
        if (sUser.ID == PrefsUtil.readUserInfo().ID) {
            sUser.IsFollow = true;
        }
        init();
        cPresenter = new ContactPresenter(this);
        getNumOfRelation();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
        String uri = String.format(Const.DOWN_ICON_URL,
                PrefsUtil.readUserInfo().CompanyCode, sUser.Image);

        ImageLoader.getInstance().displayImage(uri, mHeadImage, options);

        if (sUser.ID == PrefsUtil.readUserInfo().ID) {
            String url = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode,
                    PrefsUtil.readUserInfo().Image);
            ImageLoader.getInstance().displayImage(url, mHeadImage, options);
            mUserName.setText(sUser.Name);
            workRank.setText(PrefsUtil.readUserInfo().Job); // 职称
            if (EMWApplication.personMap != null && EMWApplication.personMap.get(sUser.ID) != null) {
                mDepartment.setText(EMWApplication.personMap.get(sUser.ID).DeptName);
            } else {
                mDepartment.setText(PrefsUtil.readUserInfo().DeptName);
            }
            // mUserCompany.setText(PrefsUtil.readUserInfo().getCompanyCode());
            mUserPhone.setText(PrefsUtil.readUserInfo().Phone);
            mUserMail.setText(PrefsUtil.readUserInfo().Email);
            btnDoFollow.setVisibility(View.GONE);

        } else {
            if (EMWApplication.personMap != null) {
                if (EMWApplication.personMap.get(sUser.ID) != null && EMWApplication.personMap.get(sUser.ID).DeptName != null) {
                    mDepartment
                            .setText(EMWApplication.personMap.get(sUser.ID).DeptName);
                }
            }
            mUserName.setText(sUser.Name);
            workRank.setText(sUser.Job); // 职称
            mUserPhone.setText(sUser.Phone);
            mUserMail.setText(sUser.Email); // 邮箱
        }

        if (sUser.ID == PrefsUtil.readUserInfo().ID) { // 当前用户才可以修改头像
            mHeadImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionSheetDialog dialog = new ActionSheetDialog(
                            PersonActivity.this).builder();
                    dialog.addSheetItem(getString(R.string.actionsheet_photo), null,
                            new OnSheetItemClickListener() {
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
                            new OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    Crop.pickImage(PersonActivity.this);
                                }
                            });
                    dialog.show();
                }
            });
        }

        getUserBz();

        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH_BZ);
        registerReceiver(receiver, filter);

        // 实例化SelectPicPopupWindow
        // menuWindow = new SelectPicPopupWindow(PersonActivity.this,
        // itemsOnClick, sUser.getID());
        // getUserInfo();
        // getCompany();

    }

    private void init() {
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderMoreBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(R.string.person);
        isFollowTip(sUser);
        mMenu = new RightMenu(this);
        mMenu.addItem(R.string.person_more_info, 1);
        if (PrefsUtil.readUserInfo().ID == sUser.ID) {
            mMenu.addItem("编辑详情", 2);
        }
        mMenu.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void selected(View view, Item item, int position) {
                Intent intent;
                switch (item.id) {
                    case 1:
                        intent = new Intent(PersonActivity.this,
                                PersonMoreInfoActivity.class);
                        intent.putExtra("sUser", sUser);
                        if (userMarkz != null) {
                            intent.putExtra("userMark", userMarkz.Name);
                        }
                        startActivityForResult(intent, REQUESTCODE);
                        break;
                    case 2:
                        intent = new Intent(PersonActivity.this, ModifyUserInfoActivity.class);
                        intent.putExtra("userInfo", sUser);
                        startActivityForResult(intent, 44);
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mMenu.showAsDropDown(mHeaderMoreBtn);
//			}
//		}, 50);
//
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mMenu.dismiss();
//			}
//		}, 100);
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
        } else if (resultCode == RESULT_OK && requestCode == REQUESTCODE) {
            sUser = (UserInfo) data.getSerializableExtra("sUser");
            isFollowTip(sUser);
        } else if (resultCode == RESULT_OK && requestCode == 44) {
            UserInfo userInfo = (UserInfo) data.getSerializableExtra("modifyData");
            mUserPhone.setText(userInfo.Phone);
            mUserMail.setText(userInfo.Email);
        }
    }

    private void isFollowTip(UserInfo sUser) {
        if (sUser.IsFollow) {
            btnDoFollow.setVisibility(View.GONE);
        } else {
            btnDoFollow.setVisibility(View.VISIBLE);
        }
    }

    @Event(value = {R.id.contact_add_attention, R.id.cm_header_btn_left, R.id.location,
            R.id.cm_header_btn_right, R.id.contact_to_chat_online,
            R.id.task_relation, R.id.work_project_relation,
            R.id.calendar_relation, R.id.file_relation})
    private void onFollowClick(View view) {
        Intent relation = null;
        switch (view.getId()) {
            case R.id.location:
                Intent intents = new Intent(this, RouteActivity.class);
                intents.putExtra("ID", sUser.ID);
                startActivity(intents);
                break;
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_btn_right:
                mMenu.showAsDropDown(view);
                break;
            case R.id.contact_add_attention:

                // String msg = sUser.isIsFollow() ? "确认取消关注？" : "确认关注？";
                String msg = getString(R.string.follow_tips);
                new AlertDialog(PersonActivity.this).builder().setMsg(msg)
                        .setPositiveButton(getString(R.string.ok), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // if (sUser.isIsFollow() == true) {
                                // cPresenter.delFollow(sUser);
                                // } else {
                                // cPresenter.addFollow(sUser);
                                // }
                                cPresenter.addFollow(sUser);
                            }
                        }).setNegativeButton(getString(R.string.cancel), new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                break;
            case R.id.contact_to_chat_online:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("SenderID", sUser.ID);
                intent.putExtra("name", sUser.Name);
                intent.putExtra("type", 1);
                intent.putExtra("GroupID", 0);
                startActivity(intent);
                onBackPressed();
                break;
            case R.id.work_project_relation:
                relation = new Intent(this, RelationProjectActivity.class);
                break;
            case R.id.task_relation:
                relation = new Intent(this, RelationTaskActivity.class);
                break;
            case R.id.calendar_relation:
                relation = new Intent(this, RelationCalendarActivity.class);
                break;
            case R.id.file_relation:
                relation = new Intent(this, RelationFileActivity.class);
                break;
            default:
                break;
        }
        if (relation != null) {
            relation.putExtra("user_info", sUser);
            startActivity(relation);
        }
    }

    /**
     * 获取人员相关条目的数量
     */
    private void getNumOfRelation() {
        API.TalkerAPI.GetTalkCountByUserId(sUser.ID,
                new RequestCallback<Integer>(Integer.class) {
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastUtil.showToast(PersonActivity.this, R.string.person_relationcount_error);
                    }

                    @Override
                    public void onParseSuccess(List<Integer> respList) {
                        if (respList != null && respList.size() >= 4) {
                            if (respList.get(0) > 0) {
                                projectNum.setVisibility(View.VISIBLE);
                                projectNum.setText(respList.get(0) + "");
                            }
                            if (respList.get(1) > 0) {
                                taskNum.setVisibility(View.VISIBLE);
                                taskNum.setText(respList.get(1) + "");
                            }
                            if (respList.get(2) > 0) {
                                calendarNum.setVisibility(View.VISIBLE);
                                calendarNum.setText(respList.get(2) + "");
                            }
                            if (respList.get(3) > 0) {
                                fileNum.setVisibility(View.VISIBLE);
                                fileNum.setText(respList.get(3) + "");
                            }
                        } else {
                            ToastUtil.showToast(PersonActivity.this, R.string.person_relationcount_error);
                        }
                    }
                });
    }

    @Override
    public void disProgressDialog() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refreshComplete() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showProgressDialog() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showFollowResult(String responseInfo) {
        if (responseInfo != null && "1".equals(responseInfo)) {
            if (sUser.IsFollow) {
                ToastUtil.showToast(this, R.string.person_cancelfollow_success);
                sUser.IsFollow = false;
                isFollowTip(sUser);
                // mMenu.addItem("暂无更多信息", 0);
                // mMenu.notifyAll();
            } else {
                ToastUtil.showToast(this, R.string.person_follow_success);
                sUser.IsFollow = true;
                isFollowTip(sUser);
                // mMenu.addItem("更多信息", 1);
                // mMenu.notifyAll();
            }
            intent = new Intent(ConcernActivity.ACTION_REFRESH_FOLLOW_LIST);
            sendBroadcast(intent); // 刷新人员列表
        } else if (responseInfo != null && "0".equals(responseInfo)) {
            if (sUser.IsFollow) {
                ToastUtil.showToast(this, R.string.person_cancelfollow_error);
            } else {
                ToastUtil.showToast(this, R.string.person_follow_error);
            }
        } else {
            Toast.makeText(this, "超时！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showTipDialog(String tips) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showUserInfo(List<UserInfo> simpleUsers) {

    }

    @Override
    public void showGroupInfo(List<GroupInfo> groupInfos) {
        // TODO Auto-generated method stub

    }

    /**
     * 获取用户备注
     */
    private void getUserBz() {
        UserMark um = new UserMark();
        um.UserId = sUser.ID;
        API.UserAPI.GetUserMark(um, new RequestCallback<String>(String.class) {

            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                Toast.makeText(PersonActivity.this, arg0.toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onSuccess(String arg0) {
                try {
                    Gson gson = new Gson();
                    userMarks = new ArrayList<UserMark>();
                    JsonArray array = new JsonParser().parse(arg0)
                            .getAsJsonArray();
                    for (JsonElement jsonElement : array) {
                        UserMark userMark = gson.fromJson(jsonElement,
                                UserMark.class);
                        if (userMark.Creator == PrefsUtil.readUserInfo().ID) {
                            userMarkz = userMark;
                        }
                    }
                    if (userMarkz != null) {
                        bzTv.setText("备注名:" + userMarkz.Name);
                    } else {
                        bzTv.setText("备注名：");
                    }
                } catch (Exception e) {

                }

            }
        });
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
                ToastUtil.showToast(PersonActivity.this, R.string.person_updatehead_error);
            }

            @Override
            public void onStarted() {
                if (mLoadingDialog != null) mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<String> result) {
                String imgTargetStr = result.toString();
                int i = imgTargetStr.lastIndexOf("/");
                String imgName = imgTargetStr.substring(i + 1,
                        imgTargetStr.length() - 1);
                sUser.Image = imgName;
                upLoadImg(sUser);
            }
        });
    }

    private void upLoadImg(final UserInfo ui) {
        API.UserAPI.ModifyUserById(ui,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        if (mLoadingDialog != null) mLoadingDialog.dismiss();
                        ToastUtil.showToast(PersonActivity.this, R.string.person_updatehead_error);
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (mLoadingDialog != null) mLoadingDialog.dismiss();
                        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                            ToastUtil.showToast(PersonActivity.this, R.string.person_updatehead_success);
                            String uri = String.format(Const.DOWN_ICON_URL,
                                    PrefsUtil.readUserInfo().CompanyCode,
                                    sUser.Image);
                            ImageLoader.getInstance().displayImage(uri,
                                    mHeadImage, options);
                            ToastUtil.showToast(PersonActivity.this, R.string.person_updatehead_success);
                            Intent intent = new Intent();
                            intent.setAction(PersonFragment.ACTION_REFRESH_CONTACT_LIST);
                            sendBroadcast(intent);
                            intent.setAction(DynamicFragment.ACTION_REFRESH_HOME_LIST);
                            sendBroadcast(intent);
                            EMWApplication.personMap.put(ui.ID, ui);
                            PrefsUtil.readUserInfo().Image = ui.Image;
                        } else {
                            ToastUtil.showToast(PersonActivity.this, R.string.person_updatehead_error);
                        }
                    }
                });
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped.png"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            // mHeadImage.setImageURI(uri);
            upload(uri.getPath());
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtil.showToast(this, Crop.getError(result).getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
