package cc.emw.mobile.chat;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.model.bean.LocationBean;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

@ContentView(R.layout.activity_pos_info)
public class PosInfoActivity extends BaseActivity {

    @ViewInject(R.id.map_view)
    private TextureMapView mapView;
    @ViewInject(R.id.tv_address_title)
    private TextView mAddressTitle;
    @ViewInject(R.id.tv_address_name)
    private TextView mAddressName;

    private AMap aMap;
    private UiSettings uiSettings;
    private LocationBean mLocationBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        mLocationBean = getIntent().getParcelableExtra("lb");
        initMap();
        if (mLocationBean != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocationBean.latitude, mLocationBean.longitude), 13));
            aMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromBitmap(convertViewToBitmap()))
                    .position(new LatLng(mLocationBean.latitude, mLocationBean.longitude)));
            mAddressTitle.setText(mLocationBean.title);
            mAddressName.setText(mLocationBean.addressName);
        }
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

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();

            //地图ui界面设置
            uiSettings = aMap.getUiSettings();

            //地图比例尺的开启
            uiSettings.setScaleControlsEnabled(true);

            //关闭地图缩放按钮 就是那个加号 和减号
            uiSettings.setZoomControlsEnabled(true);
        }
    }


    @Event(value = {R.id.ic_tv_back, R.id.out_side_view})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.ic_tv_back:
            case R.id.out_side_view:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
