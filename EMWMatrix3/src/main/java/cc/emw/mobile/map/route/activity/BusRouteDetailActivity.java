package cc.emw.mobile.map.route.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;

import cc.emw.mobile.R;
import cc.emw.mobile.contact.ContactSelectActivity;
import cc.emw.mobile.map.AMapUtil;
import cc.emw.mobile.map.overlay.BusRouteOverlay;
import cc.emw.mobile.map.route.adapter.BusSegmentListAdapter;
import cc.emw.mobile.view.IconTextView;

public class BusRouteDetailActivity extends Activity implements OnMapLoadedListener,
        OnMapClickListener, InfoWindowAdapter, OnInfoWindowClickListener, OnMarkerClickListener, View.OnClickListener {
    private AMap aMap;
    private MapView mapView;
    private BusPath mBuspath;
    private BusRouteResult mBusRouteResult;
    private TextView mTitle, mTitleBusRoute, mDesBusRoute;
    private ListView mBusSegmentList;
    private BusSegmentListAdapter mBusSegmentListAdapter;
    private IconTextView mBusMap;
    private LinearLayout mBuspathview;
    private BusRouteOverlay mBusrouteOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        getIntentData();
        init();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            mBuspath = intent.getParcelableExtra("bus_path");
            mBusRouteResult = intent.getParcelableExtra("bus_result");
        }
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        registerListener();
        findViewById(R.id.but_share).setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.title_center);
        mTitle.setText("公交路线详情");
        mTitleBusRoute = (TextView) findViewById(R.id.firstline);
        mDesBusRoute = (TextView) findViewById(R.id.secondline);
        String dur = AMapUtil.getFriendlyTime((int) mBuspath.getDuration());
        String dis = AMapUtil.getFriendlyLength((int) mBuspath.getDistance());
        mTitleBusRoute.setText(dur + "(" + dis + ")");
        int taxiCost = (int) mBusRouteResult.getTaxiCost();
        mDesBusRoute.setText("打车约" + taxiCost + "元");
        mDesBusRoute.setVisibility(View.VISIBLE);
        mBusMap = (IconTextView) findViewById(R.id.title_map);
        mBusMap.setVisibility(View.VISIBLE);
        mBusMap.setOnClickListener(this);
        mBuspathview = (LinearLayout) findViewById(R.id.bus_path);
        configureListView();
    }

    private void registerListener() {
        aMap.setOnMapLoadedListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
    }

    private void configureListView() {
        mBusSegmentList = (ListView) findViewById(R.id.bus_segment_list);
        mBusSegmentListAdapter = new BusSegmentListAdapter(
                this.getApplicationContext(), mBuspath.getSteps());
        mBusSegmentList.setAdapter(mBusSegmentListAdapter);
    }

    @Override
    public void onMapLoaded() {
        if (mBusrouteOverlay != null) {
            mBusrouteOverlay.addToMap();
            mBusrouteOverlay.zoomToSpan();
        }
    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_map:
                mBuspathview.setVisibility(View.GONE);
                mBusMap.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                aMap.clear();// 清理地图上的所有覆盖物
                mBusrouteOverlay = new BusRouteOverlay(this, aMap,
                        mBuspath, mBusRouteResult.getStartPos(),
                        mBusRouteResult.getTargetPos());
                mBusrouteOverlay.removeFromMap();
                break;
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
                this.finish();
                break;
        }
    }
}
