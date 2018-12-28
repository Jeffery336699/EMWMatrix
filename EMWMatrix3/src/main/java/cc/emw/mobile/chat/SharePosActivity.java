package cc.emw.mobile.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.farsunset.cim.client.android.CIMEventListener;
import com.farsunset.cim.client.android.CIMListenerManager;
import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.map.activity.ShareLocationActivity;
import cc.emw.mobile.chat.model.bean.LocationBean;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.map.Locations;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.SectorProgressView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

@ContentView(R.layout.activity_share_pos)
public class SharePosActivity extends BaseActivity implements AMapLocationListener, LocationSource, CIMEventListener {

    @ViewInject(R.id.map_view)
    private MapView mapView;
    @ViewInject(R.id.ll_share_ing_pos)
    private LinearLayout mLayoutSendPosIng;
    @ViewInject(R.id.tv_share_pos_ing_tag)
    private TextView mSharePosIngTv;
    @ViewInject(R.id.btn_send_pos)
    private ImageButton mBtnSendPos;
    @ViewInject(R.id.tv_end_time)
    private TextView mEndTime;
    @ViewInject(R.id.spv)
    private SectorProgressView mSectorProgressView;

    private AMap aMap;
    private AMapLocationClientOption mLocationOption = null; //定位参数
    private AMapLocationClient mlocationClient;
    private AMapLocation mAMapLocation;
    private int type;   //聊天的类型(个人群组)
    private int SenderID;
    private List<Marker> markerList = new ArrayList<>();

    private boolean isSharePosIng;
    private Handler mHandler = new Handler() {  //实时共享位置
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 555:
                    countDown++;
                    if (countDown == 3600) {
                        countDown = 0;
                        currentTime = 0;
                        mLayoutSendPosIng.setTag(0);
                        isSharePosIng = false;
                        mLayoutSendPosIng.setBackgroundColor(getResources().getColor(R.color.blue));
                        mSharePosIngTv.setTextColor(Color.parseColor("#FFFFFF"));
                        mSharePosIngTv.setText("共享实时位置");
                        deactivate();

                        //倒计时
                        mSectorProgressView.setVisibility(View.GONE);
                        if (timer2 != null) {
                            timer2.cancel();
                            timer2.purge();
                            timer2 = null;
                        }
                        mSectorProgressView.setPercent(0);
                    }
                    mSectorProgressView.setPercent(countDown / 36);
                    break;
            }
        }
    };

    // 发送者定位时间倒计时
    private Timer timer2;
    private TimerTask task2;
    private int countDown = 0;
    private long currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.NO_ID);
        CIMListenerManager.registerMessageListener(this, this);
        type = getIntent().getIntExtra("type", 1);
        SenderID = getIntent().getIntExtra("SenderId", 0);
        initAMap(savedInstanceState);
    }

    /**
     * 高德地图(实时共享位置)
     *
     * @param savedInstanceState
     */
    private void initAMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        //show my location
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);
        mLayoutSendPosIng.setTag(0);
        mBtnSendPos.setTag(0);
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
        mLocationOption.setInterval(10000);
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
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getLongitude() != 0.0 && aMapLocation.getLatitude() != 0.0
                && aMapLocation.getAddress() != null && !TextUtils.isEmpty(aMapLocation.getAddress())) {
            // 停止定位
            mAMapLocation = aMapLocation;

            if (!isSharePosIng) {
                convertViewToBitmap(PrefsUtil.readUserInfo().ID);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), 13f));
                deactivate();
            }

            boolean hasExist = false;
//            for (int i = 0; i < aMap.getMapScreenMarkers().size(); i++) {
//                Marker m = aMap.getMapScreenMarkers().get(i);
            for (int j = 0; j < markerList.size(); j++) {
                Marker m = markerList.get(j);
                if (m.getObject() != null && Integer.valueOf(m.getObject().toString()) == PrefsUtil.readUserInfo().ID) {
                    LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    m.setPosition(latLng);
                    aMap.invalidate();
                    hasExist = true;
                    break;
                }
            }
            if (!hasExist) {
                Marker marker = aMap.addMarker(new MarkerOptions()
                        .position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(isSharePosIng ? PrefsUtil.readUserInfo().ID : 0))).draggable(true));
                marker.setObject(isSharePosIng ? PrefsUtil.readUserInfo().ID : 0);
                markerList.add(marker);
            }

            if (isSharePosIng) {
                String lola = aMapLocation.getLatitude() + ","
                        + aMapLocation.getLongitude();
                sendMyPos(lola);
                List<Integer> userIds = new ArrayList<>();
                userIds.add(SenderID);
                userIds.add(PrefsUtil.readUserInfo().ID);
                getOthersPos(userIds);
            }
            if (SenderID != 0) {
                convertViewToBitmap(SenderID);
                List<Integer> userIds = new ArrayList<>();
                userIds.add(SenderID);
                getOthersPos(userIds);
            }
        }
    }

    @Event(value = {R.id.ll_share_ing_pos, R.id.btn_send_pos, R.id.ic_tv_back, R.id.out_side_view})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.ll_share_ing_pos:
                if (mLayoutSendPosIng.getTag().equals(0)) {
                    mLayoutSendPosIng.setTag(1);
                    mBtnSendPos.setTag(1);
                    isSharePosIng = true;
                    mEndTime.setVisibility(View.GONE);
                    mLayoutSendPosIng.setBackgroundColor(getResources().getColor(R.color.white));
                    mSharePosIngTv.setTextColor(Color.parseColor("#E50909"));
                    mSharePosIngTv.setText("停止共享");
                    if (aMap.getMapScreenMarkers().size() > 0 && markerList.size() > 0) {
                        aMap.getMapScreenMarkers().get(0).remove();
                        markerList.remove(0);
                    }
                    Marker marker = aMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(PrefsUtil.readUserInfo().ID))).draggable(true));
                    marker.setObject(PrefsUtil.readUserInfo().ID);
                    markerList.add(marker);
                    sendLocation();
                    initLocation();

                    //倒计时
                    mSectorProgressView.setVisibility(View.VISIBLE);
                    timer2 = new Timer();
                    task2 = new TimerTask() {
                        public void run() {
                            mHandler.sendEmptyMessage(555);
                        }
                    };
                    timer2.schedule(task2, 0, 1000);
                } else if (mLayoutSendPosIng.getTag().equals(1)) {
                    mLayoutSendPosIng.setTag(0);
                    mBtnSendPos.setTag(0);
                    currentTime = 0;
                    isSharePosIng = false;
                    mEndTime.setVisibility(View.VISIBLE);
                    mLayoutSendPosIng.setBackgroundColor(getResources().getColor(R.color.blue));
                    mSharePosIngTv.setTextColor(Color.parseColor("#FFFFFF"));
                    mSharePosIngTv.setText("共享实时位置");
                    deactivate();
                    sendBroadcast(new Intent(ChatContent.SEND_UNSHARE_POS_MESSAGE));

                    //倒计时
                    mSectorProgressView.setVisibility(View.GONE);
                    if (timer2 != null) {
                        timer2.cancel();
                        timer2.purge();
                        timer2 = null;
                    }
                    mSectorProgressView.setPercent(0);
                }
                break;
            case R.id.btn_send_pos:
                if (mBtnSendPos.getTag().equals(1)) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude()), 13f));
                } else if (mBtnSendPos.getTag().equals(0)) {
                    Intent intent = new Intent(this, ShareLocationActivity.class);
                    intent.putExtra("start_anim", false);
                    startActivity(intent);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    }, 1000);
                }
                break;
            case R.id.ic_tv_back:
            case R.id.out_side_view:
                onBackPressed();
                break;
        }
    }

    private void sendLocation() {
        if (mAMapLocation != null) {
            final LocationBean lb = new LocationBean();
            lb.addressName = mAMapLocation.getAddress();
            lb.title = mAMapLocation.getPoiName();
            lb.longitude = mAMapLocation.getLongitude();
            lb.latitude = mAMapLocation.getLatitude();
            if (isSharePosIng) {
                lb.isShareLocation = true;
            } else {
                lb.isShareLocation = false;
            }

            aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
                @Override
                public void onMapScreenShot(Bitmap bitmap) {
                    Log.d("zrjtsss", "---------->in");
                    long time = System.currentTimeMillis();
                    writeImage(bitmap, time);
                    lb.url = EMWApplication.tempPath + time + ".png";
//                    mPersenter.uploadMapImagePersenter(lb);
                    Intent intent = new Intent(ChatContent.SEND_SHRRE_POS_MESSAGE);
                    intent.putExtra("lb", lb);
                    sendBroadcast(intent);
                    bitmap.recycle();
                }

                /*@Override
                public void onMapScreenShot(Bitmap bitmap, int i) {
                }*/
            });
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

    //将view转换为bitmap
    public Bitmap convertViewToBitmap(int id) {
        if (id == 0) {
            ImageView view = new ImageView(this);
            view.setBackgroundResource(R.drawable.my_location);
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();
            return bitmap;
        } else {
            final View view = LayoutInflater.from(this).inflate(R.layout.item_user_map_head, null);
            CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.civ_map_head);
            if (EMWApplication.personMap != null && EMWApplication.personMap.get(id) != null) {
                UserInfo userInfo = EMWApplication.personMap.get(id);
                String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode,
                        userInfo.Image);
                Picasso.with(this)
                        .load(uri)
                        .resize(DisplayUtil.dip2px(this, 30), DisplayUtil.dip2px(this, 30))
                        .centerCrop()
                        .placeholder(R.drawable.cm_img_head)
                        .error(R.drawable.cm_img_head)
                        .config(Bitmap.Config.ALPHA_8)
                        .into(circleImageView);
            }
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, DisplayUtil.dip2px(this, 30), DisplayUtil.dip2px(this, 30));
            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();
            return bitmap;
        }
    }

    /**
     * 更新我的位置
     */
    private void sendMyPos(final String lola) {
        API.UserAPI.ModifyUserAxisById(lola, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
//                    Toast.makeText(SharePosActivity.this, "更新位置成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 获取对方的位置
     */
    private void getOthersPos(final List<Integer> userIds) {
        API.UserAPI.GetUserAxisList(userIds, new RequestCallback<Locations>(Locations.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(SharePosActivity.this, "获取位置失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onParseSuccess(List<Locations> respList) {
                for (int i = 0; i < respList.size(); i++) {
                    Locations locations = respList.get(i);
                    if (locations.getAxis() != null && !TextUtils.isEmpty(locations.getAxis())
                            && !locations.getAxis().equals("0.0,0.0")) {
                        String[] data = locations.getAxis().split(",");
                        if (data.length == 2) {
                            String lat = data[0];
                            String log = data[1];
                            if (locations.getAxis() != null) {
                                boolean hasExist = false;
//                                for (int j = 0; j < aMap.getMapScreenMarkers().size(); j++) {
//                                    Marker m = aMap.getMapScreenMarkers().get(j);
                                for (int j = 0; j < markerList.size(); j++) {
                                    Marker m = markerList.get(j);
                                    if (m.getObject() != null && Integer.valueOf(m.getObject().toString()) == respList.get(i).getID()) {
                                        LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(log));
                                        m.setPosition(latLng);
                                        aMap.invalidate();
                                        hasExist = true;
                                        break;
                                    }
                                }
                                if (!hasExist) {
                                    MarkerOptions markers = new MarkerOptions()
                                            .position(new LatLng(
                                                    Double.valueOf(lat), Double
                                                    .valueOf(log))).title(locations.getName())
                                            .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap(locations.getID()))).draggable(false);

                                    Marker marker = aMap.addMarker(markers);
                                    marker.setObject(locations.getID());
                                    markerList.add(marker);
                                }
                            }
                        }
                    }
                }
            }
        });
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

    @Override
    public void onBackPressed() {
        if (isSharePosIng) {
            new AlertDialog(this).builder().setMsg("退出该页面,将停止共享位置功能\n确认退出?")
                    .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text)).setCancelable(false)
                    .setPositiveButton(getString(R.string.confirm),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mLayoutSendPosIng.setTag(0);
                                    currentTime = 0;
                                    isSharePosIng = false;
                                    mEndTime.setVisibility(View.VISIBLE);
                                    mLayoutSendPosIng.setBackgroundColor(getResources().getColor(R.color.blue));
                                    mSharePosIngTv.setTextColor(Color.parseColor("#FFFFFF"));
                                    mSharePosIngTv.setText("共享实时位置");
                                    deactivate();
                                    //倒计时
                                    mSectorProgressView.setVisibility(View.GONE);
                                    if (timer2 != null) {
                                        timer2.cancel();
                                        timer2.purge();
                                        timer2 = null;
                                    }
                                    onBackPressed();
                                    sendBroadcast(new Intent(ChatContent.SEND_UNSHARE_POS_MESSAGE));
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
        } else {
            finish();
        }
    }

    @Override
    public void onMessageReceived(String message) {
        try {
            Message msg = new Gson().fromJson(message, Message.class);
            if (msg.getSenderID() == EMWApplication.currentChatUid) {
                switch (msg.getBusType()) {
                    case 0:     //聊天消息
                        if (type == 1) {
                            if (msg.getType() == 41) {   //进入
                                List<Integer> mUsers = new ArrayList<>();
                                mUsers.add(msg.getSenderID());
                                mUsers.add(PrefsUtil.readUserInfo().ID);
                                getOthersPos(mUsers);
                            } else if (msg.getType() == 42) {
                                for (int i = 0; i < markerList.size(); i++) {
                                    if (Integer.valueOf(markerList.get(i).getObject().toString()) == msg.getSenderID()) {
                                        markerList.get(i).remove();
                                        aMap.invalidate();
                                        break;
                                    }
                                }
                                List<Integer> mUsers = new ArrayList<>();
                                mUsers.add(PrefsUtil.readUserInfo().ID);
                                getOthersPos(mUsers);
                            }
                        } else if (type == 2) {
                        }
                        break;
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onReplyReceived(String replybody) {
    }

    @Override
    public void onNetworkChanged(NetworkInfo networkinfo) {
    }

    @Override
    public void onConnectionStatus(boolean isConnected) {
    }

    @Override
    public void onCIMConnectionSucceed() {
    }

    @Override
    public void onCIMConnectionClosed() {
    }
}
