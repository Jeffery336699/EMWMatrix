package cc.emw.mobile.contact;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity.UserMark;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.ToastUtil;

/**
 * @author zrjt
 */
@ContentView(R.layout.activity_beizhumin)
public class BzmActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn;
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv;
	@ViewInject(R.id.cm_header_tv_right)
	private TextView tvSubmit;
	@ViewInject(R.id.cm_input_et_content)
	private EditText editContent;
	private int userId;
	private Dialog mLoadingDialog; // 加载框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
		mHeaderBackBtn.setVisibility(View.VISIBLE);
		mHeaderTitleTv.setText(R.string.bzm);
		tvSubmit.setVisibility(View.VISIBLE);
		tvSubmit.setText(R.string.submit);
		userId = getIntent().getIntExtra("userId", 0);
	}

	@Event(value = { R.id.cm_header_btn_left, R.id.cm_header_tv_right })
	private void Onhlick(View v) {
		switch (v.getId()) {
		case R.id.cm_header_btn_left:
			onBackPressed();
			break;
		case R.id.cm_header_tv_right:
			submitSuggest();
			break;
		default:
			break;
		}
	}

	/**
	 * 提交用户备注
	 */
	private void submitSuggest() {
		UserMark um = new UserMark();
		um.UserId = userId;
		um.Name = editContent.getText().toString();
		API.UserAPI.DoUserMark(um, new RequestCallback<String>(String.class) {
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				ToastUtil.showToast(BzmActivity.this, R.string.bzm_submit_error);
			}
			@Override
			public void onStarted() {
				mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
				mLoadingDialog.show();
			}
			@Override
			public void onSuccess(String result) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
					ToastUtil.showToast(BzmActivity.this, R.string.bzm_submit_success, R.drawable.tishi_ico_gougou);
					Intent intent = new Intent(BzmActivity.this,
							PersonMoreInfoActivity.class);
					intent.putExtra("bzName", editContent.getText().toString());
					if (editContent.getText().toString().equals("")){
						intent.putExtra("bzName", "添加");
					}
					intent.putExtra("userId", userId);
					startActivity(intent);
					intent = new Intent();
					intent.setAction(PersonActivity.ACTION_REFRESH_BZ);
					sendBroadcast(intent);
					finish();
				} else {
					ToastUtil.showToast(BzmActivity.this, R.string.bzm_submit_error);
				}
			}
		});
	}
}
