package cc.emw.mobile.file;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.ToastUtil;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 新建文件夹
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_create3)
public class FileCreateActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderFinishTv; // 顶部条完成

	@ViewInject(R.id.et_filecreate_name)
	private EditText mNameEt; //文件夹名称
	@ViewInject(R.id.et_filecreate_desc)
	private EditText mDescEt; //文件夹描述

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
		/*findViewById(R.id.cm_header_bar).setBackgroundColor(Color.TRANSPARENT);
		mHeaderBackBtn.setVisibility(View.GONE);
		mHeaderFinishTv.setText(R.string.finish);
		mHeaderFinishTv.setVisibility(View.GONE);*/
		mHeaderTitleTv.setText(R.string.filelist_more_create);
		((TextView)findViewById(R.id.cm_header_tv_right9)).setText("确认");
	}

	/*@Override
	public void onBackPressed() {
		finish();
	}*/

	@Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
	private void onHeaderClick(View v) {
		switch (v.getId()) {
			case R.id.cm_header_btn_left9:
				HelpUtil.hideSoftInput(this, mNameEt);
				onBackPressed();
				break;
			case R.id.cm_header_tv_right9:
				String name = mNameEt.getText().toString();
				String desc = mDescEt.getText().toString();
				if (!TextUtils.isEmpty(name)) {
					saveFolder(noteFile != null ? noteFile.ID : 0, name, desc);
				} else {
					ToastUtil.showToast(FileCreateActivity.this, R.string.filecreate_empty_folder);
				}
				break;
		}
	}


	/**
	 * 新建文件夹
	 */
	private void saveFolder(int pid, String name, String desc) {
		Files file = new Files();
		file.ParentID = pid;
		file.Name = name;
		file.Content = desc;

		API.UserData.SaveFoldInfo(file, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(FileCreateActivity.this, R.string.filecreate_error);
			}
			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips6);
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (result != null && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(FileCreateActivity.this, R.string.filecreate_success, R.drawable.tishi_ico_gougou);
					setResult(Activity.RESULT_OK);
					finish();
				} else {
					ToastUtil.showToast(FileCreateActivity.this, R.string.filecreate_error);
				}
			}
		});
	}

}