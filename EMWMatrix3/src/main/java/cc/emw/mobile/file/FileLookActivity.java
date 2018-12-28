package cc.emw.mobile.file;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;


import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.TestActivity;
import cc.emw.mobile.contact.GroupSelectActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.file.adapter.FileLookAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Dept;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserFilePower;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.ApiEnum.UserFilePowerType;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 分享查看
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.activity_file_look3)
public class FileLookActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderNoticeBtn; //

    @ViewInject(R.id.tv_filelook_all)
    private TextView mAllPowerTv; //所有人的权限

    @ViewInject(R.id.lv_filedetail_downlog)
    private ExListView mPowerLv; //已有的权限人员/群组/部门

    private ArrayList<UserFilePower> mPowerList; // 提交分享人员列表数据
    private ArrayList<UserInfo> mSelectList; // 分享人员列表数据
    private FileLookAdapter mFileLookAdapter; //选择的人员Adapter

    private Dialog mLoadingDialog; //加载框
    private Files noteFile; //传值
    private UserFilePower userFilePower; //存放列表点击值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        noteFile = (Files) getIntent().getSerializableExtra("file_info");

        initView();

        if (noteFile != null) {
            getFilePower(noteFile.ID);
        }

        try {
            findViewById(R.id.cm_header_tv_right9).setVisibility(View.GONE);
        } catch (Exception e) {

        }
    }

    private void initView() {
        /*mHeaderBackBtn.setVisibility(View.GONE);
        mHeaderNoticeBtn.setImageResource(R.drawable.nav_btn_notice);
        mHeaderNoticeBtn.setVisibility(View.GONE);*/
        mHeaderTitleTv.setText("分享查看");
        ((IconTextView)findViewById(R.id.cm_header_btn_left9)).setIconText("eb68");

        mPowerList = new ArrayList<UserFilePower>();
        mSelectList = new ArrayList<UserInfo>();
        mFileLookAdapter = new FileLookAdapter(this, mPowerList);
        mPowerLv.setAdapter(mFileLookAdapter);
        mPowerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userFilePower = (UserFilePower) view.getTag(R.id.tag_second);
                Intent intent = new Intent(FileLookActivity.this, SharePowerActivity.class);
                intent.putExtra("select_position", Integer.toBinaryString(userFilePower.Power));
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivityForResult(intent, 13);
            }
        });
    }

    /*@Override
    public void onBackPressed() {
        finish();
    }*/

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

    @Event({R.id.ll_filelook_addperson, R.id.ll_filelook_all})
    private void onRelationClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.ll_filelook_addperson: //添加分享对象
                intent = new Intent(this, SharePersonActivity.class);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivityForResult(intent, 12);
                break;
            case R.id.ll_filelook_all: //所有人
                intent = new Intent(this, SharePowerActivity.class);
                intent.putExtra("select_position", mAllPowerTv.getTag().toString());
                intent.putExtra("start_anim", false);
                int[] location2 = new int[2];
                v.getLocationOnScreen(location2);
                intent.putExtra("click_pos_y", location2[1]);
                startActivityForResult(intent, 11);
                break;
        }
    }

    /**
     * 获取权限
     * @param fid
     */
    private void getFilePower(int fid) {
        API.UserData.LoadFilePower(fid, new RequestCallback<UserFilePower>(UserFilePower.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
//				ToastUtil.showToast(FileDetailActivity.this, R.string.file_share_error);
            }

            /*@Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }*/
            @Override
            public void onParseSuccess(List<UserFilePower> respList) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0) {
                    String power = Integer.toBinaryString(respList.get(0).Power); //十进制转二进制
                    mAllPowerTv.setText(HelpUtil.getFilePowerNumber(power));
                    mAllPowerTv.setTag(power);
                }
                mPowerList.clear();
                mPowerList.addAll(respList);
                mFileLookAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 修改权限
     * @param upList
     * @param fileID
     */
    private void doFilePower(List<UserFilePower> upList, int fileID) {
        API.UserData.DoFilePower(upList, fileID, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(FileLookActivity.this, "修改失败");
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    ToastUtil.showToast(FileLookActivity.this, "修改成功", R.drawable.tishi_ico_gougou);
                    getFilePower(noteFile.ID);
                } else {
                    ToastUtil.showToast(FileLookActivity.this, "修改失败");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 11: //所有人
                    String power = data.getStringExtra("file_power");
                    mAllPowerTv.setText(HelpUtil.getFilePowerText(power));
                    mAllPowerTv.setTag(power);
                    ArrayList<UserFilePower> powerList = new ArrayList<>();
                    UserFilePower filePower = mPowerList.get(0);
                    filePower.Power = Integer.valueOf(power, 2); //二进制转十进制
                    powerList.add(filePower);
                    doFilePower(powerList, noteFile.ID);
                    break;
                case 13: //选择某人处理
                    String power2 = data.getStringExtra("file_power");
                    ArrayList<UserFilePower> powerList2 = new ArrayList<>();
                    userFilePower.Power = Integer.valueOf(power2, 2); //二进制转十进制
                    powerList2.add(userFilePower);
                    doFilePower(powerList2, noteFile.ID);
                    break;
                case 12: //添加分享对象
                    //选择的人员列表
                    mSelectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
                    if (mSelectList != null && mSelectList.size() > 0) {
                        ArrayList<UserFilePower> upList = new ArrayList<>();
                        for (UserInfo simpleUser : mSelectList) {
                            UserFilePower filePower3 = new UserFilePower();
                            filePower3.Name = simpleUser.Name;
                            filePower3.ID = simpleUser.ID;
                            filePower3.Power = UserFilePowerType.View;
                            filePower3.Type = NoteRoleTypes.User;
                            upList.add(filePower3);
                        }
                        doFilePower(upList, noteFile.ID);
                    }
                    //选择的群组
                    GroupInfo groupInfo = (GroupInfo) data.getSerializableExtra(GroupSelectActivity.TargetG);
                    if (groupInfo != null) {
                        ArrayList<UserFilePower> upList = new ArrayList<>();
                        UserFilePower filePower3 = new UserFilePower();
                        filePower3.Name = groupInfo.Name;
                        filePower3.ID = groupInfo.ID;
                        filePower3.Power = UserFilePowerType.View;
                        filePower3.Type = NoteRoleTypes.Group;
                        upList.add(filePower3);
                        doFilePower(upList, noteFile.ID);
                    }
                    //选择的部门
                    Dept dept = (Dept) data.getSerializableExtra("select_dept");
                    if (dept != null) {
                        ArrayList<UserFilePower> upList = new ArrayList<>();
                        UserFilePower filePower3 = new UserFilePower();
                        filePower3.Name = dept.Name;
                        filePower3.ID = dept.ID;
                        filePower3.Power = UserFilePowerType.View;
                        filePower3.Type = NoteRoleTypes.Dept;
                        upList.add(filePower3);
                        doFilePower(upList, noteFile.ID);
                    }
                    break;
            }
        }
    }


}
