package cc.emw.mobile.file;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.GroupSelectActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.file.adapter.FileShareAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserFilePower;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEnum.NoteRoleTypes;
import cc.emw.mobile.net.ApiEnum.UserFilePowerType;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.FileUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 权限
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_share3)
public class FileShareActivity extends BaseActivity {
	
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderOkTv; //

	@ViewInject(R.id.iv_fileshare_icon)
	private ImageView mIconIv; //文件图标
	@ViewInject(R.id.tv_fileshare_name)
	private TextView mNameTv; //文件名称
	@ViewInject(R.id.tv_fileshare_size)
	private TextView mSizeTv; //文件大小
	@ViewInject(R.id.tv_fileshare_time)
	private TextView mUploadTimeTv; //上传时间

	@ViewInject(R.id.tv_fileshare_addpower)
	private TextView mAddPowerTv; //权限

	@ViewInject(R.id.lv_fileshare_power)
	private ExListView mPowerLv; //

	private ArrayList<UserFilePower> mPowerList; // 提交分享人员列表数据
	private ArrayList<UserInfo> mSelectList; // 分享人员列表数据
	private FileShareAdapter mFileShareAdapter; //选择的人员Adapter
	
	private Dialog mLoadingDialog; //加载框
	private Files noteFile; //传值
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        noteFile = (Files) getIntent().getSerializableExtra("file_info");
        
        initView();
        
    }
    
    private void initView() {
        /*mHeaderBackBtn.setVisibility(View.GONE);
		mHeaderOkTv.setText(R.string.finish);
		mHeaderOkTv.setVisibility(View.VISIBLE);*/
		mHeaderTitleTv.setText("权限设置");
		((IconTextView)findViewById(R.id.cm_header_btn_left9)).setIconText("eb68");
		((TextView)findViewById(R.id.cm_header_tv_right9)).setText("确认");

		mIconIv.setImageResource(FileUtil.getResIconId(noteFile.Name));
        mNameTv.setText(noteFile.Name);
        mSizeTv.setText(FileUtil.getReadableFileSize(noteFile.Length));
        mUploadTimeTv.setText(noteFile.UpdateTime);

        mPowerList = new ArrayList<UserFilePower>();
        mSelectList = new ArrayList<UserInfo>();
		mFileShareAdapter = new FileShareAdapter(this, mPowerList);
		mPowerLv.setAdapter(mFileShareAdapter);
    }

	/*@Override
	public void onBackPressed() {
		finish();
	}*/

	@Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left9:
				onBackPressed();
				break;
			case R.id.cm_header_tv_right9:
				if (mPowerList.size() == 0 || TextUtils.isEmpty(mAddPowerTv.getText())) {
					ToastUtil.showToast(this, "请选择！");
				} else {
					for (UserFilePower filePower : mPowerList) {
						filePower.Power = Integer.valueOf(mAddPowerTv.getTag().toString(), 2); //二进制转十进制
					}
					doFilePower(mPowerList, noteFile.ID);
				}
				break;
    	}
    }
    @Event({R.id.ll_fileshare_addpower, R.id.ll_fileshare_addperson})
    private void onRelationClick(View v) {
    	Intent intent = null;
    	switch (v.getId()) {
			case R.id.ll_fileshare_addpower: //选择权限
				intent = new Intent(this, SharePowerActivity.class);
				intent.putExtra("select_position", mAddPowerTv.getTag().toString());
				intent.putExtra("start_anim", false);
				int[] location = new int[2];
				v.getLocationOnScreen(location);
				intent.putExtra("click_pos_y", location[1]);
				startActivityForResult(intent, 11);
				break;
			case R.id.ll_fileshare_addperson: //选择人员/群组/部门
				intent = new Intent(this, SharePersonActivity.class);
				intent.putExtra("start_anim", false);
				int[] location2 = new int[2];
				v.getLocationOnScreen(location2);
				intent.putExtra("click_pos_y", location2[1]);
				startActivityForResult(intent, 12);
				break;
    	}
    }

	private void doFilePower(List<UserFilePower> upList, int fileID) {
		API.UserData.DoFilePower(upList, fileID, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileShareActivity.this, "修改失败");
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
					ToastUtil.showToast(FileShareActivity.this, "修改成功", R.drawable.tishi_ico_gougou);
					finish();
				} else {
					ToastUtil.showToast(FileShareActivity.this, "修改失败");
				}
			}
		});
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		switch (requestCode) {
				case 11: //选择的权限
					String power = data.getStringExtra("file_power");
					mAddPowerTv.setText(HelpUtil.getFilePowerText(power));
					mAddPowerTv.setTag(power);
					break;
    			case 12:
					//选择的人员
    				mSelectList = (ArrayList<UserInfo>) data.getSerializableExtra("select_list");
    				ArrayList<UserFilePower> upList = new ArrayList<UserFilePower>();
					if (mSelectList != null && mSelectList.size() > 0) {
						for (UserInfo simpleUser : mSelectList) {
							UserFilePower filePower = new UserFilePower();
							filePower.Name = simpleUser.Name;
							filePower.ID = simpleUser.ID;
							filePower.Power = UserFilePowerType.View;
							filePower.Type = NoteRoleTypes.User;
							upList.add(filePower);
						}
					}
					//选择的群组
					GroupInfo groupInfo = (GroupInfo) data.getSerializableExtra(GroupSelectActivity.TargetG);
					if (groupInfo != null) {
						UserFilePower filePower3 = new UserFilePower();
						filePower3.Name = groupInfo.Name;
						filePower3.ID = groupInfo.ID;
						filePower3.Power = UserFilePowerType.View;
						filePower3.Type = NoteRoleTypes.Group;
						upList.add(filePower3);
					}
					//选择的部门
					ApiEntity.Dept dept = (ApiEntity.Dept) data.getSerializableExtra("select_dept");
					if (dept != null) {
						UserFilePower filePower3 = new UserFilePower();
						filePower3.Name = dept.Name;
						filePower3.ID = dept.ID;
						filePower3.Power = UserFilePowerType.View;
						filePower3.Type = NoteRoleTypes.Dept;
						upList.add(filePower3);
					}

					mPowerList.addAll(upList);
					mFileShareAdapter.notifyDataSetChanged();
    				break;
    		}
		}
	}
   

}
