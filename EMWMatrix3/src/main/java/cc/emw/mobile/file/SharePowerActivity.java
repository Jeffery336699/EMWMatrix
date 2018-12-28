package cc.emw.mobile.file;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 选择文件权限
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_power3)
public class SharePowerActivity extends BaseActivity {
	
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderOkTv; //

	@ViewInject(R.id.ll_filepower_unpower)
	private LinearLayout mUnpowerLayout; //无权限Layout
	@ViewInject(R.id.ll_filepower_look)
	private LinearLayout mLookLayout; //查看Layout
	@ViewInject(R.id.ll_filepower_edit)
	private LinearLayout mEditLayout; //编辑Layout
	@ViewInject(R.id.ll_filepower_edit)
	private LinearLayout mDownloadLayout; //下载Layout
	@ViewInject(R.id.ll_filepower_edit)
	private LinearLayout mShareLayout; //共享Layout
	@ViewInject(R.id.ll_filepower_edit)
	private LinearLayout mDeleteLayout; //删除Layout

	@ViewInject(R.id.cb_filepower_unpower)
	private CheckBox mUnpowerCb; //无权限
	@ViewInject(R.id.cb_filepower_look)
	private CheckBox mLookCb; //查看
	@ViewInject(R.id.cb_filepower_edit)
	private CheckBox mEditCb; //协助
	@ViewInject(R.id.cb_filepower_downlaod)
	private CheckBox mDownloadCb; //下载
	@ViewInject(R.id.cb_filepower_share)
	private CheckBox mShareCb; //共享
	@ViewInject(R.id.cb_filepower_delete)
	private CheckBox mDeleteCb; //删除

	private String position; //传值，默认选中某项

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
		position = getIntent().getStringExtra("select_position");
        initView();
    }
    
    private void initView() {
        /*mHeaderBackBtn.setVisibility(View.GONE);
		mHeaderOkTv.setText(R.string.finish);
		mHeaderOkTv.setVisibility(View.VISIBLE);*/
		mHeaderTitleTv.setText("分享权限");
		((IconTextView)findViewById(R.id.cm_header_btn_left9)).setIconText("eb68");
		((TextView)findViewById(R.id.cm_header_tv_right9)).setText("确认");


		if (!TextUtils.isEmpty(position) && TextUtils.isDigitsOnly(position)) {
			int power = Integer.valueOf(position);
			if (power == 0) {
				mUnpowerCb.setChecked(true);
			} else {
				char[] chars = position.toCharArray();
				for (int i = 0; i < chars.length; i++) {
					if (chars[i] == '1') {
						if (i == 0) {
							mLookCb.setChecked(true);
						} else if (i == 1) {
							mEditCb.setChecked(true);
						} else if (i == 2) {
							mDownloadCb.setChecked(true);
						} else if (i == 3) {
							mShareCb.setChecked(true);
						} else if (i == 4) {
							mDeleteCb.setChecked(true);
						}
					}
				}
			}
		}
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
				if (!mUnpowerCb.isChecked() && !mLookCb.isChecked() && !mEditCb.isChecked() && !mDownloadCb.isChecked() && !mShareCb.isChecked() && !mDeleteCb.isChecked()) {
					ToastUtil.showToast(this, "请选择！");
				} else {
					StringBuilder power = new StringBuilder();
					if (mUnpowerCb.isChecked()) {
						power.append("0");
					}
					power.append(mLookCb.isChecked() ? "1" : "0");
					power.append(mEditCb.isChecked() ? "1" : "0");
					power.append(mDownloadCb.isChecked() ? "1" : "0");
					power.append(mShareCb.isChecked() ? "1" : "0");
					power.append(mDeleteCb.isChecked() ? "1" : "0");

					setResult(Activity.RESULT_OK, new Intent().putExtra("file_power", power.toString()));
					finish();
				}
				break;
    	}
    }
    @Event({R.id.ll_filepower_unpower, R.id.ll_filepower_look, R.id.ll_filepower_edit, R.id.ll_filepower_download, R.id.ll_filepower_share, R.id.ll_filepower_delete})
    private void onItemClick(View v) {
    	switch (v.getId()) {
			case R.id.ll_filepower_unpower:
				mUnpowerCb.setChecked(!mUnpowerCb.isChecked());
				if (mUnpowerCb.isChecked()) {
					mLookCb.setChecked(false);
					mEditCb.setChecked(false);
					mDownloadCb.setChecked(false);
					mShareCb.setChecked(false);
					mDeleteCb.setChecked(false);
				}
				break;
			case R.id.ll_filepower_look:
				mLookCb.setChecked(!mLookCb.isChecked());
				if (mLookCb.isChecked()) {
					mUnpowerCb.setChecked(false);
				}
				break;
			case R.id.ll_filepower_edit:
				mEditCb.setChecked(!mEditCb.isChecked());
				if (mEditCb.isChecked()) {
					mUnpowerCb.setChecked(false);
					mLookCb.setChecked(true);
				} else {
					mDownloadCb.setChecked(false);
					mShareCb.setChecked(false);
					mDeleteCb.setChecked(false);
				}
				break;
			case R.id.ll_filepower_download:
				mDownloadCb.setChecked(!mDownloadCb.isChecked());
				if (mDownloadCb.isChecked()) {
					mUnpowerCb.setChecked(false);
					mLookCb.setChecked(true);
					mEditCb.setChecked(true);
				} else {
					mShareCb.setChecked(false);
					mDeleteCb.setChecked(false);
				}
				break;
			case R.id.ll_filepower_share:
				mShareCb.setChecked(!mShareCb.isChecked());
				if (mShareCb.isChecked()) {
					mUnpowerCb.setChecked(false);
					mLookCb.setChecked(true);
					mEditCb.setChecked(true);
					mDownloadCb.setChecked(true);
				} else {
					mDeleteCb.setChecked(false);
				}
				break;
			case R.id.ll_filepower_delete:
				mDeleteCb.setChecked(!mDeleteCb.isChecked());
				if (mDeleteCb.isChecked()) {
					mUnpowerCb.setChecked(false);
					mLookCb.setChecked(true);
					mEditCb.setChecked(true);
					mDownloadCb.setChecked(true);
					mShareCb.setChecked(true);
				} else {
					mLookCb.setChecked(false);
					mEditCb.setChecked(false);
					mDownloadCb.setChecked(false);
					mShareCb.setChecked(false);
				}
				break;
    	}
    }

}
