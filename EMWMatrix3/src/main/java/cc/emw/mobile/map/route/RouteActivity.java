package cc.emw.mobile.map.route;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.view.ScrollableViewHelper;
import cc.emw.mobile.chat.view.SlidingUpPanelLayout;
import cc.emw.mobile.map.AMapUtil;
import cc.emw.mobile.map.Locations;
import cc.emw.mobile.map.WalkRouteDetailActivity;
import cc.emw.mobile.map.overlay.DrivingRouteOverlay;
import cc.emw.mobile.map.overlay.WalkRouteOverlay;
import cc.emw.mobile.map.route.activity.DriveRouteDetailActivity;
import cc.emw.mobile.map.route.adapter.BusResultListAdapter;
import cc.emw.mobile.map.route.adapter.RideSegmentListAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.StringUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

public class RouteActivity extends BaseActivity implements OnMapClickListener,
        LocationSource, OnMarkerClickListener, OnInfoWindowClickListener,
        AMapLocationListener, InfoWindowAdapter, OnRouteSearchListener, AdapterView.OnItemClickListener, GeocodeSearch.OnGeocodeSearchListener {

    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private WalkRouteResult mWalkRouteResult;
    private RideRouteResult mRideRouteResult;
    private BusRouteResult mBusRouteResult;
    private LatLonPoint mStartPoint;// 起点， "113.994548,22.539596"
    private LatLonPoint mEndPoint;// 模拟位置，具体位置请求服务器返回数据。= new LatLonPoint(22.526504, 113.962358)
    private final int ROUTE_TYPE_BUS = 1;
    private final int ROUTE_TYPE_DRIVE = 2;
    private final int ROUTE_TYPE_WALK = 3;
    private final int ROUTE_TYPE_CROSSTOWN = 4;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private String url;
    private LinearLayout mBusResultLayout;
    private LinearLayout mBottomLayout;
    private TextView mRotueTimeDes, mRouteDetailDes;
    private ListView mBusResultList;
    private Dialog progDialog = null;// 搜索时进度条
    private int id;
    private ListView mListView;
    private ArrayList<Locations> datas = new ArrayList<>();
    private DisplayImageOptions options;
    private SlidingUpPanelLayout mLayout;
    private String currentCity;
    private ImageView mBus;
    private ImageView mDrive;
    private ImageView mWalk;
    private GeocodeSearch geocoderSearch;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.route_activitys);
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(bundle);// 此方法必须重写
        init();
        initView();
        getPersonalList();
        getIntents();
    }

    private void getIntents() {
        if (getIntent().hasExtra("Axis")) {
            String axis = getIntent().getStringExtra("Axis");
            if (!"".equals(axis)) {
                String lat = axis.substring(0, axis.indexOf(","));
                String log = axis.substring(axis.indexOf(",") + 1,
                        axis.length());
                mEndPoint = new LatLonPoint(Double.valueOf(lat), Double.valueOf(log));
            }
        }
        if (getIntent().hasExtra("ID")) {
            id = getIntent().getIntExtra("ID", -1);
        }
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        setUpMap();
        registerListener();

        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mBusResultLayout = (LinearLayout) findViewById(R.id.bus_result);
        mRotueTimeDes = (TextView) findViewById(R.id.firstline);
        mRouteDetailDes = (TextView) findViewById(R.id.secondline);
        mBusResultList = (ListView) findViewById(R.id.bus_result_list);
        mDrive = (ImageView) findViewById(R.id.route_drive);
        mBus = (ImageView) findViewById(R.id.route_bus);
        mWalk = (ImageView) findViewById(R.id.route_walk);
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(RouteActivity.this);
        aMap.setOnMarkerClickListener(RouteActivity.this);
        aMap.setOnInfoWindowClickListener(RouteActivity.this);
        aMap.setInfoWindowAdapter(RouteActivity.this);
    }

    @Override
    public View getInfoContents(Marker arg0) {
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
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(head), options,
                new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        mEndPoint = AMapUtil.convertToLatLonPoint(arg0.getPosition());
//        aMap.addMarker(new MarkerOptions()
//                .position(AMapUtil.convertToLatLng(mEndPoint))
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end))
//                .draggable(true));
        url = "http://m.amap.com/?from=" + mStartPoint
                + "(from)&to=" + arg0.getPosition().latitude + "," + arg0.getPosition().longitude + "(to)";// 分享路线 url
        return false;
    }

    @Override
    public void onMapClick(LatLng arg0) {

    }

    public void onBusClick(View view) {
        searchRouteResult(ROUTE_TYPE_BUS, RouteSearch.BusDefault);
        mapView.setVisibility(View.GONE);
        mBusResultLayout.setVisibility(View.VISIBLE);
    }

    public void onDriveClick(View view) {
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
        mapView.setVisibility(View.VISIBLE);
        mBusResultLayout.setVisibility(View.GONE);
    }

    public void onWalkClick(View view) {
        searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
        mapView.setVisibility(View.VISIBLE);
        mBusResultLayout.setVisibility(View.GONE);
    }

    public void onCrosstownBusClick(View view) {
        searchRouteResult(ROUTE_TYPE_CROSSTOWN, RouteSearch.BusDefault);
//        mDrive.setImageResource(R.drawable.route_drive_normal);
//        mBus.setImageResource(R.drawable.route_bus_normal);
//        mWalk.setImageResource(R.drawable.route_walk_normal);
        mapView.setVisibility(View.GONE);
        mBusResultLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
//        ToastUtil.showToast(this, "开始搜索路径规划方案 mStartPoint=" + mStartPoint + "mEndPoint=" + mEndPoint);
        if (mStartPoint == null) {
            ToastUtil.showToast(mContext, getString(R.string.location_ing));
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.showToast(mContext, getString(R.string.no_destination));
            startActivity(new Intent(this, RouteActivity.class));
            RouteActivity.this.finish();
        }
        showProgressDialog();

        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
            String mCurrentCityName = currentCity;
            if (mlocationClient != null && mlocationClient.getLastKnownLocation() != null) {
                mCurrentCityName = mlocationClient.getLastKnownLocation().getCityCode();
            }
            BusRouteQuery query = new BusRouteQuery(fromAndTo, mode,
                    mCurrentCityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            WalkRouteQuery query = new WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        } else if (routeType == ROUTE_TYPE_CROSSTOWN) {
            RouteSearch.FromAndTo fromAndTo_bus = new RouteSearch.FromAndTo(
                    mStartPoint, mEndPoint);
            BusRouteQuery query = new BusRouteQuery(fromAndTo_bus, mode,
                    currentCity, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            query.setCityd(currentCity);
            mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {
        dissmissProgressDialog();
        mBottomLayout.setVisibility(View.GONE);
//        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mBusRouteResult = result;
                    BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(this, mBusRouteResult);
                    mBusResultList.setAdapter(mBusResultListAdapter);
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.showToast(mContext, R.string.no_result);
                }
            } else {
                mapView.setVisibility(View.VISIBLE);
                mBusResultLayout.setVisibility(View.GONE);
                ToastUtil.showToast(mContext, R.string.no_result);
            }
        } else {
            mapView.setVisibility(View.VISIBLE);
            mBusResultLayout.setVisibility(View.GONE);
            ToastUtil.showToast(mContext, R.string.no_result);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
        dissmissProgressDialog();
        mBottomLayout.setVisibility(View.GONE);
//        aMap.clear();// 清理地图上的所有覆盖物
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (rideRouteResult != null && rideRouteResult.getPaths() != null) {
                if (rideRouteResult.getPaths().size() > 0) {
                    mRideRouteResult = rideRouteResult;
                    RideSegmentListAdapter mBusResultListAdapter = new RideSegmentListAdapter(this, mRideRouteResult.getPaths().get(0).getSteps());
                    mBusResultList.setAdapter(mBusResultListAdapter);
                } else if (rideRouteResult != null && rideRouteResult.getPaths() == null) {
                    ToastUtil.showToast(mContext, R.string.no_result);
                }
            } else {
                mapView.setVisibility(View.VISIBLE);
                mBusResultLayout.setVisibility(View.GONE);
                ToastUtil.showToast(mContext, R.string.no_result);
            }
        } else {
            mapView.setVisibility(View.VISIBLE);
            mBusResultLayout.setVisibility(View.GONE);
            ToastUtil.showToast(mContext, R.string.no_result);
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
//        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
//                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
//                            this, aMap, drivePath,
//                            mDriveRouteResult.getStartPos(),
//                            mDriveRouteResult.getTargetPos());
//                    drivingRouteOverlay.removeFromMap();
//                    drivingRouteOverlay.addToMap();
//                    drivingRouteOverlay.zoomToSpan();
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            mContext, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "("
                            + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    mRouteDetailDes.setText("打车约" + taxiCost + "元");
                    mBottomLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Logger.d("route", "drive_url:" + url);
                            Intent intent = new Intent(mContext,
                                    DriveRouteDetailActivity.class);
//                            intent.putExtra("drive_path", drivePath);
//                            intent.putExtra("url", url);
//                            intent.putExtra("drive_result", mDriveRouteResult);
                            Bundle args = new Bundle();
                            args.putParcelable("drive_path", drivePath);
                            args.putParcelable("drive_result", mDriveRouteResult);
                            args.putString("url", url);
                            intent.putExtras(args);
//                            for (int i = 0; i < drivePath.getSteps().size(); i++) {
//                                Log.d("zzzz", drivePath.getSteps().get(i).getInstruction());
//                            }
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Log.d("zzzz", e.getMessage());
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        dissmissProgressDialog();
//        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
//                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
//                            this, aMap, walkPath,
//                            mWalkRouteResult.getStartPos(),
//                            mWalkRouteResult.getTargetPos());
//                    walkRouteOverlay.removeFromMap();
//                    walkRouteOverlay.addToMap();
//                    walkRouteOverlay.zoomToSpan();
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "("
                            + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.GONE);
                    mBottomLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Logger.d("route", "walk_url:" + url);
                            Intent intent = new Intent(mContext,
                                    WalkRouteDetailActivity.class);
                            intent.putExtra("walk_path", walkPath);
                            intent.putExtra("walk_result", mWalkRouteResult);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }
                    });
                }
            }
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {

        if (progDialog == null)
            progDialog = createLoadingDialog(getString(R.string.searching));
        progDialog.show();
    }

    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
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

    private void setUpMap() {

        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setScaleControlsEnabled(true);// 设定地图的比例尺
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    // 激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 开启定位
            mlocationClient.startLocation();
        }
    }

    // 停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    // 定位成功后回调
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            currentCity = amapLocation.getCity();
            mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            mStartPoint = new LatLonPoint(amapLocation.getLatitude(),
                    amapLocation.getLongitude());
//                setfromandtoMarker();
            if (!getIntent().getStringExtra("Axis").equals("")) {
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(mEndPoint), 17));
                List<Marker> markers = aMap.getMapScreenMarkers();
                Log.d("zzzz", "size" + markers.size() + "");
                for (int i = 0; i < markers.size(); i++) {
                    if ((markers.get(i).getPosition().latitude + "," + markers.get(i).getPosition().longitude).
                            equals(mEndPoint.getLatitude() + "," + mEndPoint.getLongitude())) {
                        markers.get(i).showInfoWindow();
                        break;
                    }
                }
            } else if (getIntent().hasExtra("ID")) {
                for (Locations location : datas) {
                    if (location.getID() == id) {
                        String temp = location.getAxis();
                        String lat = temp.substring(0, temp.indexOf(","));
                        String log = temp.substring(temp.indexOf(",") + 1,
                                temp.length());
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(lat), Double
                                .valueOf(log)), 17), 200, null);
                        mEndPoint = new LatLonPoint(
                                Double.valueOf(lat), Double
                                .valueOf(log));
                        break;
                    }
                }
                if (mEndPoint == null) {
                    ToastUtil.showToast(RouteActivity.this, "暂无此人位置信息。");
//                    finish();
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(mStartPoint.getLatitude()), Double
                            .valueOf(mStartPoint.getLongitude())), 17), 1000, null);
//                    startActivity(new Intent(RouteActivity.this, RouteActivity.class));
//                    RouteActivity.this.finish();
                } else {
                    url = "http://m.amap.com/?from=" + mStartPoint + "(from)&to="
                            + mEndPoint.getLatitude() + "," + mEndPoint.getLongitude() + "(to)";// 分享路线 url
                }
            } else {
                ToastUtil.showToast(RouteActivity.this, "暂无此人位置信息。");
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(amapLocation.getLatitude(),
                        amapLocation.getLongitude()), 17), 200, null);
            }
            // 停止定位
            deactivate();

//            // 向服务器返回当前用户的经纬度
//            String lola = amapLocation.getLongitude() + ","
//                    + amapLocation.getLatitude();
//            API.UserAPI.ModifyUserAxisById(lola, new RequestCallback<String>(String.class) {
//                @Override
//                public void onError(Throwable throwable, boolean b) {
////                        cc.emw.mobile.util.ToastUtil.showToast(mContext, "cuowudaima!");
//                }
//
//                @Override
//                public void onSuccess(String result) {
//                    super.onSuccess(result);
////                        cc.emw.mobile.util.ToastUtil.showToast(mContext, "上传位置成功！result="+result);
//                }
//            });
        }
    }

    private void initView() {
        setSwipeBackEnable(false);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head)
                .showImageForEmptyUri(R.drawable.cm_img_head)
                .showImageOnFail(R.drawable.cm_img_head)
                .cacheInMemory(true).cacheOnDisk(true).build();
        IconTextView headerBackBtn = (IconTextView) findViewById(R.id.cm_header_btn_left); // 顶部条返回按钮
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setScrollableViewHelper(new ScrollableViewHelper());
        mLayout.setDragView(R.id.dragView);
        LinearLayout bottom = (LinearLayout) findViewById(R.id.bottom);
        bottom.setOnClickListener(new OnClickListener() {
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
        mLayout.setFadeOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mListView = (ListView) findViewById(R.id.list);
        myAdapter = new MyAdapter(datas);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(this);
        TextView headerTitleTv = (TextView) findViewById(R.id.cm_header_tv_title); // 顶部条标题
        headerTitleTv.setText(R.string.route);
        headerBackBtn.setVisibility(View.VISIBLE);
        headerBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mContext = this.

                getApplicationContext();
    }

    private void getPersonalList() {
        API.UserAPI.GetUserAxisList(null, new RequestCallback<Locations>(Locations.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(RouteActivity.this, "获取人员列表失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onParseSuccess(List<Locations> respList) {
                datas.clear();
                int count = 0;
                for (Locations locationss : respList) {
                    if (locationss.getAxis() != null && !TextUtils.isEmpty(locationss.getAxis())
                            && !locationss.getAxis().equals("0.0,0.0")) {
                        String[] data = locationss.getAxis().split(",");
                        if (data.length == 2) {
                            String lat = data[0];
                            String log = data[1];
                            LatLonPoint latLonPoint = new LatLonPoint(Double.valueOf(lat), Double.valueOf(log));
                            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
                            geocoderSearch.getFromLocationAsyn(query);
                            if (locationss.getAxis() != null) {
                                datas.add(locationss);
                                aMap.addMarker(new MarkerOptions()
                                        .position(
                                                AMapUtil.convertToLatLng(new LatLonPoint(
                                                        Double.valueOf(lat), Double
                                                        .valueOf(log)))).title(locationss.getName()).infoWindowEnable(false)
                                        .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(locationss.getID()))).draggable(false)).setObject(count);
                                count++;
                            }
                        }
                    }
                }
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        String location = datas.get(position).getAxis();
        String lat = location.substring(0, location.indexOf(","));
        String log = location.substring(location.indexOf(",") + 1,
                location.length());
        mEndPoint = new LatLonPoint(Double.valueOf(lat), Double.valueOf(log));
        url = "http://m.amap.com/?from=" + mStartPoint
                + "(from)&to=" + mEndPoint + "(to)";// 分享路线 url
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(lat), Double.valueOf(log)), 19), 200, null);
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
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        Log.d("zzzz", rCode + "");
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getRoads().get(0).getName()
                        + "附近";
                for (int i = 0; i < datas.size(); i++) {
                    String[] data = datas.get(i).getAxis().split(",");
                    if(data.length == 2) {
                        Double lat = Double.valueOf(data[0]);
                        Double log = Double.valueOf(data[1]);
                        if (result.getRegeocodeQuery().getPoint().getLatitude() == lat &&
                                result.getRegeocodeQuery().getPoint().getLongitude() == log) {
                            datas.get(i).address = addressName;
                        }
                    }
                }
                myAdapter.notifyDataSetChanged();
                Log.d("zzzz", addressName);
            } else {
                ToastUtil.showToast(RouteActivity.this, R.string.no_result);
            }
        } else {
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    class MyAdapter extends BaseAdapter {

        private List<Locations> datas;

        public MyAdapter(List<Locations> datas) {
            this.datas = datas;
        }

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
                convertView = LayoutInflater.from(RouteActivity.this).inflate(R.layout.listitem_location_person, null);
                holder.imageView = (CircleImageView) convertView.findViewById(R.id.personnel_iv_head);
                holder.textView = (TextView) convertView.findViewById(R.id.personnel_tv_name);
                holder.positionInfo = (TextView) convertView.findViewById(R.id.tv_person_position);
                holder.postionTime = (TextView) convertView.findViewById(R.id.tv_person_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (datas.get(position).address != null && !TextUtils.isEmpty(datas.get(position).address)) {
                holder.positionInfo.setText(datas.get(position).address);
                if (datas.get(position).AxisTime != null && !TextUtils.isEmpty(datas.get(position).AxisTime)) {
                    String time = datas.get(position).AxisTime.replaceAll("/", "-");
                    holder.postionTime.setText(StringUtils.friendly_time(time));
                }
            } else
                holder.positionInfo.setText("未知位置...");
            holder.textView.setText(datas.get(position).getName());
            String uri = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode, datas.get(position).getImage());
            ImageLoader.getInstance().displayImage(uri, holder.imageView, options);
            return convertView;
        }
    }

    class ViewHolder {
        TextView textView, positionInfo, postionTime;
        CircleImageView imageView;
    }

    //view 转bitmap

    public Bitmap convertViewToBitmap(int id) {

        ImageView view = new ImageView(this);
        if (id == PrefsUtil.readUserInfo().ID) {
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
}
