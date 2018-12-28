package cc.emw.mobile.file;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.contact.DeptSelectActivity;
import cc.emw.mobile.contact.GroupSelectActivity;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * 选择分享对象
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_file_person3)
public class SharePersonActivity extends BaseActivity {
	
	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_tv_right)
	private TextView mHeaderOkTv; //

	@ViewInject(R.id.itv_fileperson_all)
	private IconTextView mAllItv; //所有人

	private static final int CHOSE_PERSON_CODE = 11; //人员requestCode
	private static final int CHOSE_TEAM_CODE = 12; //团队requestCode
	private static final int CHOSE_DEPT_CODE = 13; //部门requestCode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        initView();
    }
    
    private void initView() {
        /*mHeaderBackBtn.setVisibility(View.GONE);
		mHeaderOkTv.setText(R.string.finish);
		mHeaderOkTv.setVisibility(View.VISIBLE);*/
		mHeaderTitleTv.setText("分享对象");
		((IconTextView)findViewById(R.id.cm_header_btn_left9)).setIconText("eb68");
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
				onBackPressed();
				break;
			case R.id.cm_header_tv_right9:
				if (mAllItv.getVisibility() == View.GONE) {
					ToastUtil.showToast(this, "请选择！");
				} else {
//					setResult(Activity.RESULT_OK, new Intent().putExtra("file_power", ""));
					finish();
				}
				break;
    	}
    }
    @Event({R.id.ll_fileperson_person, R.id.ll_fileperson_team, R.id.ll_fileperson_dept})
    private void onRelationClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.ll_fileperson_person: //选择人员
				intent = new Intent(this, ContactSelectActivity.class);
				intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.MULTI_SELECT);
				intent.putExtra("start_anim", false);
				int[] location = new int[2];
				v.getLocationOnScreen(location);
				intent.putExtra("click_pos_y", location[1]);
				startActivityForResult(intent, CHOSE_PERSON_CODE);
				break;
			case R.id.ll_fileperson_team: //选择团队
				intent = new Intent(this, GroupSelectActivity.class);
				intent.putExtra("start_anim", false);
				int[] location2 = new int[2];
				v.getLocationOnScreen(location2);
				intent.putExtra("click_pos_y", location2[1]);
				startActivityForResult(intent, CHOSE_TEAM_CODE);
				break;
			case R.id.ll_fileperson_dept: //选择部门
				intent = new Intent(this, DeptSelectActivity.class);
				intent.putExtra("start_anim", false);
				int[] location3 = new int[2];
				v.getLocationOnScreen(location3);
				intent.putExtra("click_pos_y", location3[1]);
				startActivityForResult(intent, CHOSE_DEPT_CODE);
				break;
    	}
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case CHOSE_PERSON_CODE:
				case CHOSE_TEAM_CODE:
				case CHOSE_DEPT_CODE:
					setResult(Activity.RESULT_OK, data);
					finish();
					break;
			}
		}
	}
}
