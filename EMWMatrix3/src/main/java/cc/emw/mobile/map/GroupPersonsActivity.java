package cc.emw.mobile.map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.view.ScrollableViewHelper;
import cc.emw.mobile.chat.view.SlidingUpPanelLayout;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * 群组附近的人页面
 */

public class GroupPersonsActivity extends BaseActivity implements OnMapClickListener,
        LocationSource, OnMarkerClickListener, OnInfoWindowClickListener,
        AMapLocationListener, InfoWindowAdapter, OnRouteSearchListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private MapView mapView;
    private SlidingUpPanelLayout mLayout;
    private ListView mListView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private ArrayList<Locations> datas;
    private DisplayImageOptions options;
    private GeocodeSearch geocoderSearch;
    private TextView tittle;
    private TextView persons;
    private LatLng startLatlng;
    private List<ApiEntity.UserInfo> mGroupList;
    private UiSettings mUiSettings;//定义一个UiSettings对象

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_groupperson);
        mapView = (MapView) findViewById(R.id.group_map);
        mapView.onCreate(bundle);
        setSwipeBackEnable(false);
        init();
        initView();
    }


    private void init() {

        if (aMap == null) {
            aMap = mapView.getMap();
        }
        setUpMap();
        registerListener();
        mGroupList = (List<ApiEntity.UserInfo>) getIntent().getSerializableExtra("userList");
    }


    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setScaleControlsEnabled(true);// 设定地图的比例尺
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }


    //注册监听
    private void registerListener() {
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        geocoderSearch = new GeocodeSearch(this);
    }


    //设置人员标记点
    private void setfromandtoMarker() {
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(null))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.people_default)));
    }


    private void initView() {
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类
        mUiSettings.setScaleControlsEnabled(true);//显示比例尺控件
        mListView = (ListView) findViewById(R.id.list);
        tittle = (TextView) findViewById(R.id.title);
        persons = (TextView) findViewById(R.id.persons);
        mListView.setOnItemClickListener(this);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setScrollableViewHelper(new ScrollableViewHelper());
        mLayout.setDragView(R.id.dragView);
        TextView back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head)
                .showImageForEmptyUri(R.drawable.cm_img_head)
                .showImageOnFail(R.drawable.cm_img_head)
                .cacheInMemory(true).cacheOnDisk(true).build();

        LinearLayout bottom = (LinearLayout) findViewById(R.id.bottom);
        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setAnchorPoint(0.5f);
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        });

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel,
                                            SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
    }


    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View infoContent = getLayoutInflater().inflate(
                R.layout.custom_info_contents, null);
        render(marker, infoContent);
        return infoContent;
    }

    private void render(Marker marker, View view) {
        TextView name = (TextView) view.findViewById(R.id.name);
        CircleImageView head = (CircleImageView) view.findViewById(R.id.head);
        int position = (int) marker.getObject();
        name.setText(datas.get(position).getName());
        String uri = String.format(Const.DOWN_ICON_URL,
                PrefsUtil.readUserInfo().CompanyCode, datas.get(position).getImage());
        ImageLoader.getInstance().displayImage(uri, head, options);
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapClick(LatLng arg0) {

    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint, final TextView textView) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {

            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                textView.setText(regeocodeResult.getRegeocodeAddress().getRoads().get(0).getName() + "附近");
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
    }

    //定位成功后的回调
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        startLatlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        getPersonalList();
        tittle.setText("地址:" + aMapLocation.getCity() + aMapLocation.getDistrict() + aMapLocation.getStreet());
        aMap.addMarker(new MarkerOptions()
                .position(
                        AMapUtil.convertToLatLng(new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude()))).title("").autoOverturnInfoWindow(false)
                .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(1))).draggable(false));
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                AMapUtil.convertToLatLng(new LatLonPoint(aMapLocation.getLatitude(),
                        aMapLocation.getLongitude())), 17f));
//        CameraUpdateFactory.newCameraPosition(new CameraPosition(
//                new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()),//新的中心点坐标
//                18, //新的缩放级别
//                30, //俯仰角0°~45°（垂直与地图时为0）
//                0  ////偏航角 0~360° (正北方为0)
//        ));
        deactivate();//停止定位
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 开启定位
            mlocationClient.startLocation();
        }
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        String location = datas.get(position).getAxis();
        String lat = location.substring(0, location.indexOf(","));
        String log = location.substring(location.indexOf(",") + 1,
                location.length());
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(lat), Double.valueOf(log)), 19), 1000, null);
        List<Marker> markers = aMap.getMapScreenMarkers();
        Log.d("zzzz", "size" + markers.size() + "");
        for (int i = 0; i < markers.size(); i++) {
            if ((markers.get(i).getPosition().latitude + "," + markers.get(i).getPosition().longitude).equals(location)) {
                markers.get(i).showInfoWindow();
                break;
            }
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        deactivate();
    }

    private void getPersonalList() {

        API.UserAPI.GetUserAxisList(null, new RequestCallback<Locations>(Locations.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onParseSuccess(List<Locations> respList) {
                datas = new ArrayList<>();
                int count = 0;
                for (Locations locationss : respList) {
                    if (locationss.getAxis().length() == 20) {
                        String[] data = locationss.getAxis().split(",");
                        String lat = data[0];
                        String log = data[1];
                        //设置数据 根据设置的附件的距离刷选数据 默认为1公里
                        double distances = AMapUtils.calculateLineDistance(startLatlng, new LatLng(Double.valueOf(lat), Double
                                .valueOf(log)));
                        if (mGroupList != null && mGroupList.size() > 0) {
                            for (int i = 0; i < mGroupList.size(); i++) {
                                if (mGroupList.get(i).ID == locationss.getID()) {
                                    if (locationss.getAxis() != null && distances < 1000) {
                                        datas.add(locationss);
                                        aMap.addMarker(new MarkerOptions()
                                                .position(
                                                        AMapUtil.convertToLatLng(new LatLonPoint(
                                                                Double.valueOf(lat), Double
                                                                .valueOf(log)))).title(locationss.getName()).autoOverturnInfoWindow(false)
                                                .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(2))).draggable(false)).setObject(count);
                                        count++;
                                    }
                                }
                            }

                        } else if (locationss.getAxis() != null && distances < 1000) {
                            datas.add(locationss);
                            aMap.addMarker(new MarkerOptions()
                                    .position(
                                            AMapUtil.convertToLatLng(new LatLonPoint(
                                                    Double.valueOf(log), Double
                                                    .valueOf(lat)))).title(locationss.getName())
                                    .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(2))).draggable(true)).setObject(count);
                            count++;
                        }
                    }
                }
                if (datas.size() > 0) {
                    mListView.setAdapter(new MyAdapter());
                    persons.setText("附近发现" + datas.size() + "人");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size() == 0 ? 0 : datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position) == null ? null : datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(GroupPersonsActivity.this).inflate(R.layout.listitem_location_group, null);
                holder.imageView = (CircleImageView) convertView.findViewById(R.id.head);
                holder.textView = (TextView) convertView.findViewById(R.id.name);
                holder.loction = (TextView) convertView.findViewById(R.id.location);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(datas.get(position).getName());

            String[] data = datas.get(position).getAxis().split(",");
            String lat = data[0];
            String log = data[1];
            getAddress(new LatLonPoint(Double.valueOf(lat), Double
                    .valueOf(log)), holder.loction);


            String uri = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode, datas.get(position).getImage());
            ImageLoader.getInstance().displayImage(uri, holder.imageView, options);
            return convertView;
        }
    }

    class ViewHolder {
        TextView textView, loction;
        CircleImageView imageView;
    }

    public Bitmap convertViewToBitmap(int id) {

        ImageView view = new ImageView(this);
        if (id == 1) {
            view.setBackgroundResource(R.drawable.map_location);
        } else {
            view.setBackgroundResource(R.drawable.map_people);
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}

