package cc.emw.mobile.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.WalkPath;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.net.ApiEntity.UserInfo;

public class WalkRouteDetailActivity extends BaseActivity implements
        OnClickListener {
    private WalkPath mWalkPath;
    private TextView mTitleWalkRoute;
    private ListView mWalkSegmentList;
    private WalkSegmentListAdapter mWalkSegmentListAdapter;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        getIntentData();
        TextView mTitle = (TextView) findViewById(R.id.title_center);
        findViewById(R.id.but_share).setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
        mTitle.setText(R.string.WalkRouteDetail);
        mTitleWalkRoute = (TextView) findViewById(R.id.firstline);
        String dur = AMapUtil.getFriendlyTime((int) mWalkPath.getDuration());
        String dis = AMapUtil.getFriendlyLength((int) mWalkPath.getDistance());
        mTitleWalkRoute.setText(dur + "(" + dis + ")");
        mWalkSegmentList = (ListView) findViewById(R.id.bus_segment_list);
        mWalkSegmentListAdapter = new WalkSegmentListAdapter(
                this.getApplicationContext(), mWalkPath.getSteps());
        mWalkSegmentList.setAdapter(mWalkSegmentListAdapter);

    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mWalkPath = intent.getParcelableExtra("walk_path");
        url = intent.getStringExtra("url");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 分享路线
            case R.id.but_share:
                Intent intent = new Intent(this, ContactSelectActivity.class);
                intent.putExtra(ContactSelectActivity.EXTRA_SELECT_TYPE, ContactSelectActivity.RADIO_SELECT);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                startActivityForResult(intent, 10);
                break;
            case R.id.title_back:
                finish();
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
