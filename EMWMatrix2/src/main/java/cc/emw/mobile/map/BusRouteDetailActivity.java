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

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;

public class BusRouteDetailActivity extends BaseActivity implements OnClickListener {

	private BusPath mBuspath;
	private BusRouteResult mBusRouteResult;
	private TextView mTitle, mTitleBusRoute, mDesBusRoute;
	private ListView mBusSegmentList;
	private BusSegmentListAdapter mBusSegmentListAdapter;
	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_detail);
		getIntentData();
		init();
		findViewById(R.id.but_share).setOnClickListener(this);
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			mBuspath = intent.getParcelableExtra("bus_path");
			mBusRouteResult = intent.getParcelableExtra("bus_result");
			url = intent.getStringExtra("url");
		}
	}

	private void init() {
		mTitle = (TextView) findViewById(R.id.title_center);
		mTitle.setText(R.string.BusRoute);
		mTitleBusRoute = (TextView) findViewById(R.id.firstline);
		mDesBusRoute = (TextView) findViewById(R.id.secondline);
		String dur = AMapUtil.getFriendlyTime((int) mBuspath.getDuration());
		String dis = AMapUtil.getFriendlyTime((int) mBuspath.getDistance());
		mTitleBusRoute.setText(dur + "(" + dis + ")");
		int taxiCost = (int) mBusRouteResult.getTaxiCost();
		mDesBusRoute.setText(R.string.spends + taxiCost + "元");
		mDesBusRoute.setVisibility(View.VISIBLE);
		configureListView();
	}

	private void configureListView() {
		mBusSegmentList = (ListView) findViewById(R.id.bus_segment_list);
		mBusSegmentListAdapter = new BusSegmentListAdapter(
				this.getApplicationContext(), mBuspath.getSteps());
		mBusSegmentList.setAdapter(mBusSegmentListAdapter);

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
