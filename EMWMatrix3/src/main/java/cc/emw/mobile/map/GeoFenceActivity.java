package cc.emw.mobile.map;

/**
 * Created by xiang.peng on 2016/8/10.
 * 新建地理围栏
 */

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.map.activity.SearchAddressActivity;
import cc.emw.mobile.chat.map.bean.SearchAddressInfo;
import cc.emw.mobile.map.route.adapter.MapAddressAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.socket.bean.NotificationChannelBean;
import cc.emw.mobile.socket.notification.NotificationSocket;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;

import static cc.emw.mobile.map.AMapUtil.convertToLatLng;

public class GeoFenceActivity extends BaseActivity implements AMapLocationListener, LocationSource, View.OnClickListener, AMap.OnMapClickListener, AdapterView.OnItemClickListener {
    final String TAG = GeoFenceActivity.class.getSimpleName();
    public static final String USER_RAIL = "user_rail";
    final int REQ_GEO_FENCE = 0x13;
    final String ACTION_GEO_FENCE = "geo fence action";
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private IntentFilter intentFilter;
    private Vibrator vibrator;
    private LatLng centerLatLng;
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener onLocationChangedListener;
    private RelativeLayout content;
    private TextView size;
    private ImageView find;
    private EditText edit;

    private TextView mTvDelete;//删除文本

    private int mRadius = 100;//记录围栏选择半径 默认100米
    private ApiEntity.UserRail userData;
    private ApiEntity.UserRail mUserRail;//获取单个围栏数据
    private GeocodeSearch mSearch;
    private TextView back;
    private TextView complete;
    private SeekBar seekBar;
    private GeocodeAddress address;
    private LatLng latLngCircle;
    private Dialog mLoadingDialog;
    private String loactionCity;    //定位的城市

    int currentPage = 0;
    PoiSearch poiSearch;
    PoiSearch.Query query;
    private MapAddressAdapter addressAdapter;
    private ListView listView;
    private PoiResult poiResult; // poi返回的结果
    private List<PoiItem> poiItems;// poi数据
    private ArrayList<SearchAddressInfo> mData = new ArrayList<>();
    public SearchAddressInfo mAddressInfoFirst;
    private boolean isHandDrag = true;
    private TextView mapAddress;
    private String city;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_geofenc);
        mapView = (MapView) findViewById(R.id.geo_map);
        mapView.onCreate(bundle);
        setSwipeBackEnable(false);
        mLoadingDialog = createLoadingDialog("正在加载．．．");
        initData();
        //show my location
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        initView();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mLocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationOption);
        //        applyPermission();
        //处理进出地理围栏事件
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GEO_FENCE);
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);//初始化
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
        aMap.setOnMapClickListener(this);
        //点击地理位置返回关键点的地址名称
        aMap.setOnPOIClickListener(new AMap.OnPOIClickListener() {
            @Override
            public void onPOIClick(Poi poi) {
                /*if (poi.getName() != null) {
                    edit.setText(poi.getName());
                    edit.setSelection(poi.getName().length());
                    mapAddress.setText(poi.getName());
                    GeocodeQuery query = new GeocodeQuery(poi.getName(), loactionCity);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
                    mSearch.getFromLocationNameAsyn(query);
                }*/
            }
        });
        listView = (ListView) findViewById(R.id.listview);
        addressAdapter = new MapAddressAdapter(this, mData);
        listView.setAdapter(addressAdapter);
        listView.setOnItemClickListener(this);
        findViewById(R.id.seach).setOnClickListener(this);

        //listView.setOnItemClickListener(this);
    }

    private void initData() {
        /**获取其他界面跳转过来的围栏数据
         *1、新建界面传来空数据 null
         *2、修改界面传来围栏实体 mUserRail
         */
        mUserRail = (ApiEntity.UserRail) getIntent().getSerializableExtra(USER_RAIL);
    }

    private List<NotificationChannelBean> beanList = new ArrayList<NotificationChannelBean>();
    private NotificationSocket notificationSocket = new NotificationSocket();
    BroadcastReceiver
            broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // 接收广播
            if (intent.getAction().equals(ACTION_GEO_FENCE)) {
                Bundle bundle = intent.getExtras();
                // 根据广播的event来确定是在区域内还是在区域外
                int status = bundle.getInt("event");
                String geoFenceId = bundle.getString("fenceId");
                if (status == 1) {
                    //通知提醒 已经入地理围栏
                    Toast.makeText(GeoFenceActivity.this, "进入地理围栏~", Toast.LENGTH_LONG).show();

                    NotificationManager nm = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(
                            context).setSmallIcon(R.drawable.ic_launcher)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setContentTitle("位置信息").setContentText("你已进入地理围栏~");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    }
                    nm.notify(1, builder.build());
                    vibrator.vibrate(3000);
                } else if (status == 2) {
                    // 离开围栏区域 通知提醒
                    Toast.makeText(GeoFenceActivity.this, "离开地理围栏~", Toast.LENGTH_LONG).show();

                    NotificationManager nm = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(
                            context).setSmallIcon(R.drawable.ic_launcher)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setContentTitle("位置信息").setContentText("你已离开地理围栏~");
                    nm.notify(2, builder.build());
                    vibrator.vibrate(3000);
                }
            }
        }
    };

    private void initView() {
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GeoFenceActivity.this, HistoryGeoFenceActivity.class);
                startActivity(intent);
            }
        });
        size = (TextView) findViewById(R.id.map_size);
        back = (TextView) findViewById(R.id.back);
        complete = (TextView) findViewById(R.id.complete);
        edit = (EditText) findViewById(R.id.edit);
        content = (RelativeLayout) findViewById(R.id.content);
        mTvDelete = (TextView) findViewById(R.id.tv_delete_map);
        find = (ImageView) findViewById(R.id.find);
        mapAddress = (TextView) findViewById(R.id.address);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(1);//默认进度为1 100m
        //        mTvDelete.setVisibility(mUserRail == null ? View.GONE : View.VISIBLE);//根据新建还是修改界面 是否展示删除按钮
        initClick();
        /**回显数据*/
        if (mUserRail != null) {
            //修改界面跳转进来
            edit.setText(mUserRail.Address);
            mapAddress.setText(mUserRail.Address);
            seekBar.setProgress(mUserRail.Radius / 100);
            //是否初始化围栏坐标在地图上的展示 TODO ？
            String axts = mUserRail.Axts;
            if (axts != null) {
                String[] strs = axts.split(",");
                if (strs.length > 1) {
                    LatLng latLng = new LatLng(Double.valueOf(strs[0]), Double.valueOf(strs[1]));
                    addCircle(latLng, mUserRail.Radius);
                }
            }
            GeocodeQuery query = new GeocodeQuery(edit.getText().toString(), loactionCity);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
            mSearch.getFromLocationNameAsyn(query);
        }
    }

    private void initClick() {
        mTvDelete.setOnClickListener(this);
        find.setOnClickListener(this);
        back.setOnClickListener(this);
        complete.setOnClickListener(this);
        mSearch = new GeocodeSearch(GeoFenceActivity.this);
        mSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                //i=100
                //                Log.d(TAG, "onGeocodeSearched-->" + i + "" + geocodeResult.toString());
                if (geocodeResult == null) {
                    ToastUtil.showToast(GeoFenceActivity.this, "抱歉,该位置不存在!");
                    edit.setText(address.getFormatAddress());
                    mapAddress.setText(address.getFormatAddress());
                } else {
                    if (geocodeResult.getGeocodeAddressList().size() > 0) {
                        address = geocodeResult.getGeocodeAddressList().get(0);
                    } else {
                        ToastUtil.showToast(GeoFenceActivity.this, "抱歉,该位置无效");
                        edit.setText(address.getFormatAddress());
                        mapAddress.setText(address.getFormatAddress());
                    }
                }
                String lola = convertToLatLng(address.getLatLonPoint()).latitude + ","
                        + convertToLatLng(address.getLatLonPoint()).longitude;
                mRadius = seekBar.getProgress() * 100;
                Log.d(TAG, mRadius + "...");
                if (mUserRail != null) {
                    mUserRail.Axts = lola;
                    mUserRail.Address = address.getFormatAddress();
                    mUserRail.Radius = mRadius;
                    city = address.getCity();
                    if (null == loactionCity) {
                        loactionCity = address.getCity();
                    }
                } else {
                    userData = new ApiEntity.UserRail();
                    userData.Address = address.getFormatAddress();
                    userData.CreateTime = "";
                    userData.ID = 0;
                    userData.Type = 0;
                    userData.Creator = 0;
                    userData.Radius = mRadius;
                    userData.Axts = lola;
                }
                //                100 * 16.5f / mRadius
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        convertToLatLng(address.getLatLonPoint()), 16.5f));
                aMap.addMarker(new MarkerOptions()
                        .position(convertToLatLng(address.getLatLonPoint())).title(address.getFormatAddress())
                        .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(1))).draggable(true));

                Intent intent = new Intent(ACTION_GEO_FENCE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(GeoFenceActivity.this, REQ_GEO_FENCE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //500:是围栏半径;-1：是超时时间（单位：ms，-1代表永不超时）
                AMapLocationClient client = new AMapLocationClient(GeoFenceActivity.this);
                client.addGeoFenceAlert("fenceId", convertToLatLng(address.getLatLonPoint()).latitude,
                        convertToLatLng(address.getLatLonPoint()).longitude, 100, -1, pendingIntent);
                latLngCircle = convertToLatLng(address.getLatLonPoint());
                addCircle(latLngCircle, mRadius);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //              mRadius = (100 * progress == 0 ? 100 : 100 * progress);
                if (progress < 1) {
                    //让seekbar永远有数据。
                    progress = 1;
                    seekBar.setProgress(1);
                }
                if (latLngCircle != null)
                    addCircle(latLngCircle, 100 * progress);
                else
                    addCircle(centerLatLng, 100 * progress);
                size.setText(100 * progress == 0 ? 100 + "m" : 100 * progress + "m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //隐藏软键盘
                HelpUtil.hideSoftInput(GeoFenceActivity.this, edit);
                //执行搜索代码
                GeocodeQuery query = new GeocodeQuery(edit.getText().toString(), loactionCity);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
                mSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
                //根据经纬度确定地点位置
                //              search.getFromLocation(new RegeocodeQuery(new LatLonPoint(AMapUtil.convertToLatLonPoint(new LatLng()))))
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onLocationChanged(AMapLocation loc) {

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                convertToLatLng(new LatLonPoint(loc.getLatitude(),
                        loc.getLongitude())), 16.5f));
        aMap.addMarker(new MarkerOptions()
                .position(
                        convertToLatLng(new LatLonPoint(loc.getLatitude(),
                                loc.getLongitude()))).title(PrefsUtil.readUserInfo().Name)
                .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(1))).draggable(true));

        if (loc != null && loc.getErrorCode() == 0) {
            //设置地理围栏
            edit.setText(loc.getAddress());
            mapAddress.setText(loc.getPoiName());
            loactionCity = loc.getCityCode();
            Log.d("zrjt", loactionCity);
            GeocodeQuery query = new GeocodeQuery(edit.getText().toString(), loactionCity);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
            mSearch.getFromLocationNameAsyn(query);
            if (centerLatLng == null) {
                centerLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());

                Intent intent = new Intent(ACTION_GEO_FENCE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQ_GEO_FENCE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //500:是围栏半径;-1：是超时时间（单位：ms，-1代表永不超时）
                mLocationClient.addGeoFenceAlert("fenceId", centerLatLng.latitude, centerLatLng.longitude, 100, 5000, pendingIntent);
                addCircle(centerLatLng, 100);
            } else {
                double latitude = loc.getLatitude();
                double longitude = loc.getLongitude();
                Log.d(TAG, "当前经纬度: " + latitude + "," + longitude);
                LatLng endLatlng = new LatLng(loc.getLatitude(), loc.getLongitude());

                // 计算量坐标点距离
                double distances = AMapUtils.calculateLineDistance(centerLatLng, endLatlng);
                Toast.makeText(GeoFenceActivity.this, "当前距离中心点：" + ((int) distances), Toast.LENGTH_LONG).show();
                if (onLocationChangedListener != null) {
                    onLocationChangedListener.onLocationChanged(loc);
                }
            }
            //查询周边
            doSearchQuery(loc.getCity(), loc.getLatitude(), loc.getLongitude());
        }
        //停止定位
        deactivate();
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String city, double latitude, double longitude) {
        String mType = "汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";
        query = new PoiSearch.Query("", "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(30);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(onPoiSearchListener);
        //以当前定位的经纬度为准搜索周围5000米范围
        // 设置搜索区域为以lp点为圆心，其周围5000米范围
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 10000, true));//
        poiSearch.searchPOIAsyn();// 异步搜索
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        cc.emw.mobile.chat.map.utils.ToastUtil.show(this, infomation);
    }


    PoiSearch.OnPoiSearchListener onPoiSearchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult result, int rCode) {
            if (rCode == 1000) {
                if (result != null && result.getQuery() != null) {// 搜索poi的结果
                    if (result.getQuery().equals(query)) {// 是否是同一条
                        poiResult = result;
                        poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始

                        List<SuggestionCity> suggestionCities = poiResult
                                .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                        //搜索到数据
                        if (poiItems != null && poiItems.size() > 0) {

                            mData.clear();

                            //先将 逆地理编码过的当前地址 也就是条目中第一个地址 放到集合中
                            if (mAddressInfoFirst != null && !TextUtils.isEmpty(mAddressInfoFirst.addressName))
                                mData.add(mAddressInfoFirst);

                            SearchAddressInfo addressInfo = null;

                            for (PoiItem poiItem : poiItems) {

                                addressInfo = new SearchAddressInfo(poiItem.getTitle(), poiItem.getSnippet(), false, poiItem.getLatLonPoint());

                                mData.add(addressInfo);
                            }
                            if (isHandDrag) {
                                mData.get(0).isChoose = true;
                            }
                            addressAdapter.notifyDataSetChanged();

                        } else if (suggestionCities != null
                                && suggestionCities.size() > 0) {
                            showSuggestCity(suggestionCities);
                        } else {
                            cc.emw.mobile.chat.map.utils.ToastUtil.show(GeoFenceActivity.this,
                                    "对不起，没有搜索到相关数据");
                        }
                    }
                } else {
                    Toast.makeText(GeoFenceActivity.this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
                }
            }
            /*if (rCode == 1000) {
                if (result != null && result.getQuery() != null) {// 搜索poi的结果
                    ToastUtil.showToast(GeoFenceActivity.this, ""+result.getPois().size());
                    *//*if (result.getQuery().equals(poiQuery)) {// 是否是同一条
                        lv_list.onLoadComplete();
                        List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                        List<PoiBean> tem = new ArrayList<>();
                        if (poiItems != null && poiItems.size() > 0) {
                            for (int i = 0; i < poiItems.size(); i++) {
                                PoiItem poiItem = poiItems.get(i);
                                PoiBean bean = new PoiBean();
                                bean.setTitleName(poiItem.getTitle());
                                bean.setCityName(poiItem.getCityName());
                                bean.setAd(poiItem.getAdName());
                                bean.setSnippet(poiItem.getSnippet());
                                bean.setPoint(poiItem.getLatLonPoint());
                                tem.add(bean);
                            }
                            poiData.addAll(tem);
                            mAdapter.notifyDataSetChanged();
                        }
                    }*//*
                }
            }*/
        }


        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };


    /**
     * 调用该方法后会在地图上展示围栏效果。
     * 目前调用一次添加一次围栏圈
     *
     * @param latLng
     * @param radius
     */

    public void addCircle(LatLng latLng, int radius) {
        float mapIndex = 16.5f;
        if (radius < 500) {
            mapIndex = 16.5f;
        } else if (radius < 1000) {
            mapIndex = 15.5f;
        } else if (radius < 2000) {
            mapIndex = 14.5f;
        } else if (radius < 3000) {
            mapIndex = 14f;
        } else if (radius < 4000) {
            mapIndex = 13.5f;
        } else if (radius < 5000) {
            mapIndex = 13f;
        } else if (radius < 6000) {
            mapIndex = 12.5f;
        } else if (radius < 7000) {
            mapIndex = 12f;
        } else if (radius < 8000) {
            mapIndex = 11.5f;
        } else {
            mapIndex = 11f;
        }
        aMap.moveCamera(CameraUpdateFactory.zoomTo(mapIndex));
        /*aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                convertToLatLng(address.getLatLonPoint()), mapIndex));*/
        /*aMap.addMarker(new MarkerOptions()
                .position(convertToLatLng(address.getLatLonPoint())).title(address.getFormatAddress())
                .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(1))).draggable(true));*/
        aMap.clear();
        //        ToastUtil.showToast(this, "画圈圈");
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeWidth(3);
        circleOptions.strokeColor(getResources().getColor(R.color.map_wai));
        circleOptions.fillColor(getResources().getColor(R.color.map_nei));
        aMap.addCircle(circleOptions);
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
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        // 设置定位监听
        mLocationClient.setLocationListener(this);
        // 设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置时间间隔
        //mLocationOption.setInterval(long   time);
        // 设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 开启定位
        if (mUserRail == null)
            mLocationClient.startLocation();
    }

    //关闭定位
    @Override
    public void deactivate() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find:
                GeocodeQuery query = new GeocodeQuery(edit.getText().toString(), loactionCity);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
                mSearch.getFromLocationNameAsyn(query);
                //                GeocodeSearch search = new GeocodeSearch(this);
                //                search.getFromLocationNameAsyn(query);// 设置同步地理编码请求
                //                search.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                //                    @Override
                //                    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                //
                //                    }
                //
                //                    @Override
                //                    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                //
                //                        userData = new ApiEntity.UserRail();
                //                        GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                //                        userData.Address = address.getFormatAddress();
                //                        userData.CreateTime = "";
                //                        userData.ID = 0;
                //                        userData.Type = 0;
                //                        userData.Creator = 0;
                ////                        userData.Radius;
                //                        String lola = AMapUtil.convertToLatLng(address.getLatLonPoint()).latitude + ","
                //                                + AMapUtil.convertToLatLng(address.getLatLonPoint()).longitude;
                //                        userData.Axts = lola;
                //                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                //                                AMapUtil.convertToLatLng(address.getLatLonPoint()), 16.5f));
                //                        aMap.addMarker(new MarkerOptions()
                //                                .position(AMapUtil.convertToLatLng(address.getLatLonPoint())).title(address.getFormatAddress())
                //                                .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(1))).draggable(true));
                //
                //                        Intent intent = new Intent(ACTION_GEO_FENCE);
                //                        PendingIntent pendingIntent = PendingIntent.getBroadcast(GeoFenceActivity.this, REQ_GEO_FENCE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //                        //500:是围栏半径;-1：是超时时间（单位：ms，-1代表永不超时）
                //                        AMapLocationClient client = new AMapLocationClient(GeoFenceActivity.this);
                //                        client.addGeoFenceAlert("fenceId", AMapUtil.convertToLatLng(address.getLatLonPoint()).latitude, AMapUtil.convertToLatLng(address.getLatLonPoint()).longitude, 100, 5000, pendingIntent);
                //                        addCircle(AMapUtil.convertToLatLng(address.getLatLonPoint()), 100);
                //                    }
                //                });
                break;
            case R.id.back:
                finish();
                break;
            case R.id.complete:
                if (TextUtils.isEmpty(edit.getText())) {
                    ToastUtil.showToast(this, "请输入围栏地址!");
                    return;
                } else if (mUserRail == null && userData != null && TextUtils.isEmpty(userData.Axts)) {
                    ToastUtil.showToast(this, "请点击按钮定位位置!");
                    return;
                }
                if (mUserRail != null) {
                    //修改界面,在点击完成的时候 重新查询下地址对应的坐标值
                    GeocodeQuery queryLocation = new GeocodeQuery(edit.getText().toString().trim(), loactionCity);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
                    mSearch.getFromLocationNameAsyn(queryLocation);
                    /**这里有可能会数据不准确 因为查询坐标貌似需要时间 因此注意 测试下*/
                    modifyUserRail(mUserRail);
                } else {
                    //新建界面
                    doUserRail(userData);
                }
                break;
            case R.id.tv_delete_map:
                //删除当前的围栏
                delUserRail(mUserRail);
                break;
            case R.id.seach:
                Intent intent = new Intent(this, SearchAddressActivity.class);
                if (null == centerLatLng) {
                    intent.putExtra("position", latLngCircle);
                } else {
                    intent.putExtra("position", centerLatLng);
                }
                if (null == loactionCity) {
                    intent.putExtra("city", city);
                } else {
                    intent.putExtra("city", loactionCity);
                }
                intent.putExtra("start_anim", false);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            SearchAddressInfo info = (SearchAddressInfo) data.getParcelableExtra("position");
            mAddressInfoFirst = info; // 上一个页面传过来的 位置信息
            info.isChoose = true;
            isHandDrag = false;
            edit.setText(info.addressName);
            mapAddress.setText(info.title);
            String myCity = "";
            if (null == loactionCity) {
                myCity = city;
            } else {
                myCity = loactionCity;
            }
            GeocodeQuery query = new GeocodeQuery(info.addressName + info.title, myCity);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
            mSearch.getFromLocationNameAsyn(query);
            //移动地图
            //aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(info.latLonPoint.getLatitude(), info.latLonPoint.getLongitude()), 13));
            //查询周边
            doSearchQuery(loactionCity, info.latLonPoint.getLatitude(), info.latLonPoint.getLongitude());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mData.get(position).latLonPoint != null) {
            /*mFinalChoosePosition = convertToLatLng(mData.get(position).latLonPoint);
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).isChoose = false;
            }
            mData.get(position).isChoose = true;

            isHandDrag = false;

            // 点击之后，改变了地图中心位置， onCameraChangeFinish 也会调用
            // 只要地图发生改变，就会调用 onCameraChangeFinish ，不是说非要手动拖动屏幕才会调用该方法
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mFinalChoosePosition.latitude, mFinalChoosePosition.longitude), 13));

            for (int i = 0; i < aMap.getMapScreenMarkers().size(); i++) {
                aMap.getMapScreenMarkers().get(i).destroy();
            }
            aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mFinalChoosePosition.latitude, mFinalChoosePosition.longitude)).draggable(false));*/
            edit.setText(mData.get(position).addressName);
            mapAddress.setText(mData.get(position).title);
            GeocodeQuery query = new GeocodeQuery(mData.get(position).addressName + mData.get(position).title, loactionCity);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
            mSearch.getFromLocationNameAsyn(query);
        }
    }

    //将view转换为bitmap
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
    public void onMapClick(LatLng latLng) {
    }

    /**
     * 操作围栏数据
     * 添加修改用户围栏
     */
    public void doUserRail(final ApiEntity.UserRail ur) {
        API.UserPubAPI.DoUserRail(ur, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
            }

            @Override
            public void onSuccess(String result) {
                mLoadingDialog.dismiss();
                Log.d("zrjt", "上传围栏成功返回数据-->" + result);
                if (Integer.parseInt(result) > 0) {
                    ToastUtil.showToast(GeoFenceActivity.this, "上传围栏成功");
                    edit.setText("");
                    mapAddress.setText("");
                    int RailId = Integer.valueOf(result);
                    Intent intent = new Intent();
                    intent.putExtra("UserRailId", RailId);
                    intent.putExtra("UserRail", userData);
                    ur.Axts = null;
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    ToastUtil.showToast(GeoFenceActivity.this, "上传围栏失败");
                }
            }
        });
    }

    /**
     * 修改用户围栏数据
     */
    public void modifyUserRail(final ApiEntity.UserRail ur) {
        API.UserPubAPI.DoUserRail(ur, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onSuccess(String result) {
                Log.d("zrjt", "修改围栏成功返回数据-->" + result);
                if (Integer.parseInt(result) > 0) {
                    ToastUtil.showToast(GeoFenceActivity.this, "修改围栏成功");
                    edit.setText("");
                    mapAddress.setText("");
                    Intent intent = new Intent();
                    intent.putExtra("UserRail", mUserRail);
                    intent.putExtra("UserRailId", Integer.parseInt(result));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    public void delUserRail(ApiEntity.UserRail ur) {
        API.UserPubAPI.DelUserRail(ur.ID, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "修改围栏成功返回数据-->" + result);
                if (Integer.parseInt(result) > 0) {
                    ToastUtil.showToast(GeoFenceActivity.this, "删除围栏成功");
                    edit.setText("");
                    mapAddress.setText("");
                }
            }
        });
    }

}