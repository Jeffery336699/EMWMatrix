package cc.emw.mobile.base;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.githang.androidcrash.util.AppManager;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.x;

import cc.emw.mobile.R;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Activity基类
 * 
 * @author shaobo.zhuang
 * 
 */
public class DialogActivity extends FragmentActivity {

	private boolean isBackTip; //是否退出提示
	private boolean isStartAnim = true; //是否跳转动画
	private boolean isForceKilled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.addActivity(this);
		x.view().inject(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.finishActivity(this);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		isForceKilled = true;
		Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}

	@Override
	public void onBackPressed() {
		if (isBackTip) {
			showDialogTip();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		if (isStartAnim) {
			overridePendingTransition(R.anim.popup_show, R.anim.activity_out);
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		if (isStartAnim) {
			overridePendingTransition(R.anim.popup_show, R.anim.activity_out);
		}
	}

	/**
	 * 加载对话框
	 * 
	 * @param resId
	 *            字符串资源ID
	 * @return
	 */
	public Dialog createLoadingDialog(int resId) {
		return createLoadingDialog(getString(resId));
	}

	/**
	 * 加载对话框
	 * 
	 * @param msg
	 *            提示信息
	 * @return
	 */
	public Dialog createLoadingDialog(String msg) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.loading_dialog, null);
		TextView tipTextView = (TextView) v.findViewById(R.id.loading_tv_tip);
		tipTextView.setText(msg);
		Dialog loadingDialog = new Dialog(this, R.style.loading_dialog);
		loadingDialog.setCancelable(true);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		return loadingDialog;
	}

	public void setBackTip(boolean isBackTip) {
		this.isBackTip = isBackTip;
	}
	public void showDialogTip() {
		new AlertDialog(this).builder().setMsg("退出此次编辑？")
		.setPositiveButton("退出", new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		}).setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		}).show();
	}

	public void setStartAnim(boolean isStartAnim) {
		this.isStartAnim = isStartAnim;
	}
}
