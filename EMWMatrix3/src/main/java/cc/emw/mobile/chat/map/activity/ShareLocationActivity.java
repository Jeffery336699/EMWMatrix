package cc.emw.mobile.chat.map.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.map.adapter.AddressAdapter;
import cc.emw.mobile.chat.map.bean.SearchAddressInfo;
import cc.emw.mobile.chat.map.utils.ToastUtil;
import cc.emw.mobile.chat.model.bean.LocationBean;


public class ShareLocationActivity extends BaseActivity implements AMapLocationListener, LocationSource, GeocodeSearch.OnGeocodeSearchListener,
        AMap.OnMapClickListener, AMap.OnCameraChangeListener, PoiSearch.OnPoiSearchListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private String addressName;
    private GeocodeSearch geocoderSearch;
    private TextureMapView mapView;
    private ListView listView;
    private AMap aMap;
    private Marker locationMarker;
    private LatLng mFinalChoosePosition;
    //    private ImageView centerImage;
    private Animation centerAnimation;
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private String city;
    private PoiResult poiResult; // poi返回的结果
    private List<PoiItem> poiItems;// poi数据
    private ArrayList<SearchAddressInfo> mData = new ArrayList<>();
    public SearchAddressInfo mAddressInfoFirst;
    private boolean isHandDrag = true;
    private boolean isFirstLoad = true;
    private boolean isBackFromSearch = false;
    private AddressAdapter addressAdapter;
    private UiSettings uiSettings;
    private ImageButton locationButton;
    private LinearLayout search;
    private TextView send;
    private TextView back;
    private static final int SEARCH_ADDDRESS = 1;
    private AMapLocation mAMapLocation;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption = null; //定位参数
    private String intoTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Eyes.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.activity_share_location2);

        intoTag = getIntent().getStringExtra("tag");

        mapView = (TextureMapView) findViewById(R.id.mapview);
        listView = (ListView) findViewById(R.id.listview);
//        centerImage = (ImageView) findViewById(R.id.center_image);
        locationButton = (ImageButton) findViewById(R.id.position_btn);
        search = (LinearLayout) findViewById(R.id.seach);
        send = (TextView) findViewById(R.id.send);
        back = (TextView) findViewById(R.id.base_back);

        search.setOnClickListener(this);
        send.setOnClickListener(this);
        back.setOnClickListener(this);
        findViewById(R.id.ll_out_side).setOnClickListener(this);

        locationButton.setOnClickListener(this);

        mapView.onCreate(savedInstanceState);

        centerAnimation = AnimationUtils.loadAnimation(this, R.anim.center_anim);
        for (int i = 0; i < 10; i++) {
            SearchAddressInfo searchAddressInfo = new SearchAddressInfo("", "", false, null);
            mData.add(searchAddressInfo);
        }
        addressAdapter = new AddressAdapter(this, mData);
        listView.setAdapter(addressAdapter);

        listView.setOnItemClickListener(this);

        initMap();

        initLocation();
    }

    private void initLocation() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置返回地址信息，默认为true
        mLocationOption.setNeedAddress(true);
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    private void initMap() {

        if (aMap == null) {
            aMap = mapView.getMap();

            //地图ui界面设置
            uiSettings = aMap.getUiSettings();

            //地图比例尺的开启
            uiSettings.setScaleControlsEnabled(true);

            //关闭地图缩放按钮 就是那个加号 和减号
            uiSettings.setZoomControlsEnabled(true);

            aMap.setOnMapClickListener(this);

            //对amap添加移动地图事件监听器
            aMap.setOnCameraChangeListener(this);
        }
        setMap();
    }

    //将view转换为bitmap
    public Bitmap convertViewToBitmap() {
        ImageView view = new ImageView(this);
        view.setBackgroundResource(R.drawable.my_location);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    private void setMap() {

        geocoderSearch = new GeocodeSearch(getApplicationContext());

        //设置逆地理编码监听
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 根据经纬度得到地址
     */
    public void getAddressFromLonLat(final LatLng latLonPoint) {
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(convertToLatLonPoint(latLonPoint), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 逆地理编码查询回调
     *
     * @param result
     * @param i
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int i) {

        if (i == 1000) {//转换成功
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                //拿到详细地址
                addressName = result.getRegeocodeAddress().getRoads().get(0).getName(); // 逆转地里编码不是每次都可以得到对应地图上的opi

                //条目中第一个地址 也就是当前你所在的地址
                mAddressInfoFirst = new SearchAddressInfo(addressName, result.getRegeocodeAddress().getFormatAddress(), false, convertToLatLonPoint(mFinalChoosePosition));

                //其实也是可以在这就能拿到附近的兴趣点的
                send.setVisibility(View.VISIBLE);

            } else {
                ToastUtil.show(this, "没有搜到");
            }
        } else {
//            ToastUtil.showerror(this, i);
        }

    }

    /**
     * 地理编码查询回调
     *
     * @param geocodeResult
     * @param i
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    /**
     * 移动地图时调用
     *
     * @param cameraPosition
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        for (int i = 0; i < aMap.getMapScreenMarkers().size(); i++) {
            aMap.getMapScreenMarkers().get(i).destroy();
        }
    }

    /**
     * 地图移动结束后调用
     *
     * @param cameraPosition
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

        //每次移动结束后地图中心的经纬度
        mFinalChoosePosition = cameraPosition.target;

//        centerImage.startAnimation(centerAnimation);


        if (isHandDrag || isFirstLoad) {//手动去拖动地图
            // 开始进行poi搜索
            getAddressFromLonLat(cameraPosition.target);
            doSearchQueryByPosition();
        } else if (isBackFromSearch) {
            //搜索地址返回后 拿到选择的位置信息继续搜索附近的兴趣点
            isBackFromSearch = false;
            doSearchQueryByPosition();
        } else {
            addressAdapter.notifyDataSetChanged();
        }
//        for (int i = 0; i < aMap.getMapScreenMarkers().size(); i++) {
//            aMap.getMapScreenMarkers().get(i).destroy();
//        }
        aMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap()))
                .position(new LatLng(mFinalChoosePosition.latitude, mFinalChoosePosition.longitude)).draggable(false));
        isHandDrag = true;
        isFirstLoad = false;
    }

    /**
     * 开始进行poi搜索
     * 通过经纬度获取附近的poi信息
     * <p>
     * 1、keyword 传 ""
     * 2、poiSearch.setBound(new PoiSearch.SearchBound(lpTemp, 5000, true)); 根据
     */
    protected void doSearchQueryByPosition() {

        currentPage = 0;
        query = new PoiSearch.Query("", "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        LatLonPoint llPoint = convertToLatLonPoint(mFinalChoosePosition);

        if (llPoint != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);  // 实现  onPoiSearched  和  onPoiItemSearched
            poiSearch.setBound(new PoiSearch.SearchBound(llPoint, 5000, true));//
            // 设置搜索区域为以lpTemp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {

        if (rcode == 1000) {
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
                        ToastUtil.show(ShareLocationActivity.this,
                                "对不起，没有搜索到相关数据");
                    }
                }
            } else {
                Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mData.get(position).latLonPoint != null) {

            mFinalChoosePosition = convertToLatLng(mData.get(position).latLonPoint);
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
                    .position(new LatLng(mFinalChoosePosition.latitude, mFinalChoosePosition.longitude)).draggable(false));
        }
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
        ToastUtil.show(this, infomation);
    }

    @Override
    public void onClick(View v) {
        if (v == locationButton) {
            //回到当前位置
            if (mAMapLocation != null) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude()), 13));
            } else {
                initLocation();
            }
        } else if (v == back) {
            finish();
        } else if (v == search) {
            Intent intent = new Intent(this, SearchAddressActivity.class);
            intent.putExtra("position", mFinalChoosePosition);
            intent.putExtra("city", city);
            intent.putExtra("start_anim", false);
            startActivityForResult(intent, SEARCH_ADDDRESS);
            isBackFromSearch = false;

        } else if (v == send) {
            try {
                if (!TextUtils.isEmpty(intoTag) && intoTag.equals("dynamic")) {
                    SearchAddressInfo addressInfo = null;
                    for (SearchAddressInfo info : mData) {
                        if (info.isChoose) {
                            addressInfo = info;
                        }
                    }
                    if (addressInfo != null) {
                        Intent intent = new Intent();
                        intent.putExtra("addressInfo", addressInfo);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        ToastUtil.show(this, "请选择地址");
                    }
                } else {
                    sendLocaton();
                }
            } catch (Exception e) {
            }
        } else if (v.getId() == R.id.ll_out_side) {
            finish();
        }
    }

    /**
     * 组装地图截图和其他View截图，需要注意的是目前提供的方法限定为MapView与其他View在同一个ViewGroup下
     *
     * @param bitmap        地图截图回调返回的结果
     * @param viewContainer MapView和其他要截图的View所在的父容器ViewGroup
     * @param mapView       MapView控件
     * @param views         其他想要在截图中显示的控件
     */
    public void getMapAndViewScreenShot(Bitmap bitmap, ViewGroup viewContainer, MapView mapView, View... views) {
        int width = viewContainer.getWidth();
        int height = viewContainer.getHeight();
        final Bitmap screenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(screenBitmap);
        canvas.drawBitmap(bitmap, mapView.getLeft(), mapView.getTop(), null);
        for (View view : views) {
            view.setDrawingCacheEnabled(true);
            canvas.drawBitmap(view.getDrawingCache(), view.getLeft(), view.getTop(), null);
        }

        screenBitmap.recycle();
    }

    private void sendLocaton() {
        SearchAddressInfo addressInfo = null;
        for (SearchAddressInfo info : mData) {
            if (info.isChoose) {
                addressInfo = info;
            }
        }
        if (addressInfo != null) {
            final LocationBean lb = new LocationBean();
            lb.addressName = addressInfo.addressName;
            lb.isChoose = addressInfo.isChoose;
            lb.title = addressInfo.title;
            lb.longitude = addressInfo.latLonPoint.getLongitude();
            lb.latitude = addressInfo.latLonPoint.getLatitude();

            aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
                @Override
                public void onMapScreenShot(Bitmap bitmap) {
                    long time = System.currentTimeMillis();
                    writeImage(bitmap, time);
                    lb.url = EMWApplication.tempPath + time + ".png";
                    Intent intent = new Intent();
                    intent.putExtra("lb", lb);
                    intent.setAction(ChatContent.SEND_SHRRE_POS_MESSAGE);
                    sendBroadcast(intent);
                    bitmap.recycle();
                    finish();
                }

                @Override
                public void onMapScreenShot(Bitmap bitmap, int i) {
                }
            });
        } else {
            ToastUtil.show(this, "请选择地址");
        }
    }

    public void writeImage(Bitmap bitmap, long timeName) {
        OutputStream outputStream = null;
        try {
            File dir = new File(EMWApplication.tempPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(EMWApplication.tempPath, timeName + ".png");
            boolean falg = file.createNewFile();
            if (falg) {
                outputStream = new FileOutputStream(file);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                }
            } else {
                file.delete();
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    bitmap.recycle();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH_ADDDRESS && resultCode == RESULT_OK) {
            SearchAddressInfo info = (SearchAddressInfo) data.getParcelableExtra("position");
            mAddressInfoFirst = info; // 上一个页面传过来的 位置信息
            info.isChoose = true;
            isBackFromSearch = true;
            isHandDrag = false;
            //移动地图
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(info.latLonPoint.getLatitude(), info.latLonPoint.getLongitude()), 13));
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            deactivate();
            mAMapLocation = aMapLocation;
            city = mAMapLocation.getCityCode();
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude()), 13));

            locationMarker = aMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap()))
                    .position(new LatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude())));

            //拿到地图中心的经纬度
            mFinalChoosePosition = locationMarker.getPosition();
        } else {
            Toast.makeText(this, "定位失败,请重试!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
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

    @Override
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
}
