package cc.emw.mobile.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.map.utils.MapUtils;
import cc.emw.mobile.chat.model.bean.MapBean;
import cc.emw.mobile.util.ToastUtil;

/**
 * Created by sunny.du on 2017/7/5.
 */
@ContentView(R.layout.activity_chat_selector_poi)
public class ChatPOISelectorActivity extends BaseActivity {
    @ViewInject(R.id.rl_chat_selector_poi_i)
    private RelativeLayout mRlChatSelectPoiI;
    @ViewInject(R.id.rl_chat_selector_poi_ii)
    private RelativeLayout mRlChatSelectPoiII;
    @ViewInject(R.id.rl_chat_selector_poi_iii)
    private RelativeLayout mRlChatSelectPoiIII;
    @ViewInject(R.id.rl_chat_selector_poi_iv)
    private RelativeLayout mRlChatSelectPoiIV;
    @ViewInject(R.id.iv_chat_back_button)
    private ImageView mIvChatBoackButton;
    private double longitude;
    private double latitude;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initEvent();
    }

    private void initEvent() {
        mRlChatSelectPoiI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivitys("美食");
            }
        });
        mRlChatSelectPoiII.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivitys("电影");
            }
        });
        mRlChatSelectPoiIII.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivitys("娱乐");
            }
        });
        mRlChatSelectPoiIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(ChatPOISelectorActivity.this, ChatDynamicCreateActivity.class);
                startActivity(startIntent);
                finish();
            }
        });
        mIvChatBoackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void startActivitys(String findCondition) {
        if(mMapBean !=null) {
            Intent intent = new Intent(ChatPOISelectorActivity.this, ChatPOIActivity.class);
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            intent.putExtra("cityCode", city);
            intent.putExtra("start_anim", false);
            intent.putExtra("find_condition", findCondition);
            startActivity(intent);
        }else{
            ToastUtil.showToast(this,"正在定位中，请稍后尝试");
        }
    }

    private void initData() {
        //获取定位信息
        MapUtils mapUtils = new MapUtils();
        mapUtils.getLonLat(this, new MyLonLatListener());
//        longitude=mMapBean.longitude;
//        latitude=mMapBean.latitude;
//        city= mMapBean.cityCode;
//        longitude = getIntent().getDoubleExtra("longitude", 0);
//        latitude = getIntent().getDoubleExtra("latitude", 0);
//        city = getIntent().getStringExtra("cityCode");
    }
    private MapBean  mapBean;
    private MapBean mMapBean;
    class MyLonLatListener implements MapUtils.LonLatListener {

        @Override
        public void getLonLat(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    mapBean = new MapBean();
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    mapBean.latitude = amapLocation.getLatitude();//获取纬度
                    mapBean.longitude = amapLocation.getLongitude();//获取经度
                    mapBean.cityCode = amapLocation.getCityCode();
                    ChatPOISelectorActivity.this.mMapBean = mapBean;
                    latitude=amapLocation.getLatitude();
                    longitude=amapLocation.getLongitude();
                    city= amapLocation.getCityCode();
                    amapLocation.getAccuracy();//获取精度信息
                } else {
                    Toast.makeText(ChatPOISelectorActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
