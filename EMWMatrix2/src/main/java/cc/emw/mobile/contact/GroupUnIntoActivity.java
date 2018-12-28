package cc.emw.mobile.contact;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.fragment.GroupFragment;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;

import com.zf.iosdialog.widget.AlertDialog;

/**
 * @author zrjt
 */
@ContentView(R.layout.activity_group_un_into)
public class GroupUnIntoActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderMoreBtn;
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeadRight;
	@ViewInject(R.id.btn_addin_group)
	private Button btnAdd; // 加入群主按钮
	@ViewInject(R.id.tv_group_create_person)
	private TextView tvCreate; // 创建者
	@ViewInject(R.id.tv_group_name)
	private TextView tvName;
	@ViewInject(R.id.tv_group_member)
	private TextView tvNumber;
	private GroupInfo info = new GroupInfo();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		info = (GroupInfo) getIntent().getSerializableExtra("group_info");
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		 mHeadRight.setVisibility(View.GONE);
		mHeaderTitleTv.setText(R.string.groupinto);
		tvName.setText(info.Name);
		tvNumber.setText("群组成员（" + info.Count + "）");
		if (info.Type == 0) {
			tvCreate.setText("创建人： " + info.CreateUserName + "  公开群组");
		} else if (info.Type == 1) {
			tvCreate.setText("创建人： " + info.CreateUserName + "  私有群组");
		}
	}

	@Event(value = { R.id.cm_header_btn_left, R.id.btn_addin_group })
	private void onContentClick(View view) {
		switch (view.getId()) {
		case R.id.cm_header_btn_left:
			finish();
			break;
		case R.id.btn_addin_group:
			new AlertDialog(this).builder().setMsg("确认加入群" + info.Name)
					.setNegativeButton(getString(R.string.cancel), new OnClickListener() {

						@Override
						public void onClick(View v) {
						}
					}).setPositiveButton(getString(R.string.ok), new OnClickListener() {

						@Override
						public void onClick(View v) {
							getGroupInfo(PrefsUtil.readUserInfo().ID);
						}
					}).show();
			break;
		default:
			break;
		}
	}

	private void getGroupInfo(int uid) {
		API.TalkerAPI.AddGroupUser(info.ID, uid, new RequestCallback<String>(
				String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				ToastUtil.showToast(GroupUnIntoActivity.this, R.string.groupinto_join_error);
			}
			@Override
			public void onSuccess(String result) {
				if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(GroupUnIntoActivity.this, R.string.groupinto_join_success);
					Intent intent = new Intent(GroupFragment.ACTION_REFRESH_GROUP);
					sendBroadcast(intent); // 刷新群组列表
					finish();
				} else {
					ToastUtil.showToast(GroupUnIntoActivity.this, R.string.groupinto_join_error);
				}
			}
		});
	}
}
