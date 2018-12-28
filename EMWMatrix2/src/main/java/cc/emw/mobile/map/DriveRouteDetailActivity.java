package cc.emw.mobile.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.activity.ChatActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.net.ApiEntity.UserInfo;

import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;

public class DriveRouteDetailActivity extends BaseActivity implements
		OnClickListener {
	private DrivePath mDrivePath;
	private DriveRouteResult mDriveRouteResult;
	private TextView mTitle, mTitleDriveRoute, mDesDriveRoute;
	private ListView mDriveSegmentList;
	private DriveSegmentListAdapter mDriveSegmentListAdapter;
	private String url;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_detail);
		findViewById(R.id.but_share).setOnClickListener(this);
		getIntentData();
		init();
	}

	private void init() {
		mTitle = (TextView) findViewById(R.id.title_center);
		mTitleDriveRoute = (TextView) findViewById(R.id.firstline);
		mDesDriveRoute = (TextView) findViewById(R.id.secondline);
		mTitle.setText(R.string.DriveRoute);
		String dur = AMapUtil.getFriendlyTime((int) mDrivePath.getDuration());
		String dis = AMapUtil.getFriendlyLength((int) mDrivePath.getDistance());
		mTitleDriveRoute.setText(dur + "(" + dis + ")");
		int taxiCost = (int) mDriveRouteResult.getTaxiCost();
		mDesDriveRoute.setText("打车约" + taxiCost + "元");
		mDesDriveRoute.setVisibility(View.VISIBLE);
		configureListView();
	}

	private void configureListView() {
		mDriveSegmentList = (ListView) findViewById(R.id.bus_segment_list);
		mDriveSegmentListAdapter = new DriveSegmentListAdapter(
				this.getApplicationContext(), mDrivePath.getSteps());
		mDriveSegmentList.setAdapter(mDriveSegmentListAdapter);
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mDrivePath = intent.getParcelableExtra("drive_path");
		mDriveRouteResult = intent.getParcelableExtra("drive_result");
		url = intent.getStringExtra("url");
	}

	public void onBackClick(View view) {
		this.finish();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// 分享路线
		case R.id.but_share:
			Intent intent = new Intent(this, ContactSelectActivity.class);
			intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.RADIO_SELECT);
			startActivityForResult(intent, 10);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 10:
				UserInfo selectUser = (UserInfo) data.getSerializableExtra("select_user");
				Intent intent = new Intent(this, ChatActivity.class);
				intent.putExtra("SenderID", selectUser.ID);
				intent.putExtra("type", 1);// "type", 1
				intent.putExtra("name", selectUser.Name);
				intent.putExtra("url_mes", url);
				startActivity(intent);
				break;
			}
		}
	}
}
