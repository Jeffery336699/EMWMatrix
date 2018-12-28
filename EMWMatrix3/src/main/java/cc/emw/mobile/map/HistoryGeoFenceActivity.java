package cc.emw.mobile.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;



public class HistoryGeoFenceActivity extends BaseActivity implements AMapLocationListener, LocationSource, View.OnClickListener {
    private static final String TAG = "HistoryGeoFenceActivity";
    public static final String HISTORY_USERRAIL = "history_userrail";
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener onLocationChangedListener;
    private RelativeLayout content;
    private ListView mListView;
//    private List<String> data = new ArrayList<String>();

    private ApiEntity.UserRail mUserRail;//围栏信息
    private MyAdapter mAdapter;
    /**
     * 围栏管理列表数据
     */
    private ArrayList<ApiEntity.UserRailManage> mDataList;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_history_geofenc);
        mapView = (MapView) findViewById(R.id.geo_map);
        mapView.onCreate(bundle);
        setSwipeBackEnable(false);

        initView();

        //show my location
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
        initIntent();
    }

    private void initIntent() {
        mUserRail = (ApiEntity.UserRail) getIntent().getSerializableExtra(HISTORY_USERRAIL);
        getUserRailManager(mUserRail.ID);//根据围栏ID获取围栏管理记录信息
    }

    private void initView() {
        TextView back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        content = (RelativeLayout) findViewById(R.id.content);
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
//        TaskUtils.setListViewHeightBasedOnChildren(mListView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onLocationChanged(AMapLocation loc) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }

    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }


    class MyAdapter extends BaseAdapter {
        private ArrayList<ApiEntity.UserRailManage> mDatas = new ArrayList<>();

        public void setDataList(ArrayList<ApiEntity.UserRailManage> datas) {
            mDatas.clear();
            if (datas != null) {
                mDatas.addAll(datas);
            }
        }

        @Override
        public int getCount() {
            return mDatas == null ? 0 : mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(HistoryGeoFenceActivity.this).inflate(R.layout.listitem_history_geofec, null);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.type = (TextView) convertView.findViewById(R.id.type);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ApiEntity.UserRailManage urm = mDatas.get(position);
            holder.date.setText(urm.CreateTime);
            holder.type.setText(urm.Type);
            holder.time.setText(urm.CreateTime);
            return convertView;
        }
    }

    class ViewHolder {
        TextView date, type, time;
    }

    /**
     * 获取用户围栏ID,获取围栏管理信息数据
     *
     * @return
     */
    private void getUserRailManager(int userRailId) {
        API.UserAPI.GetRailManageByRid(userRailId, new RequestCallback<ApiEntity.UserRailManage>(ApiEntity.UserRailManage.class) {
            @Override
            public void onCancelled(CancelledException arg0) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onParseSuccess(List<ApiEntity.UserRailManage> userRailManage) {
                mDataList = (ArrayList<ApiEntity.UserRailManage>) userRailManage;
                mAdapter.setDataList(mDataList);
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, mDataList.size() + "");
            }
        });
    }
}