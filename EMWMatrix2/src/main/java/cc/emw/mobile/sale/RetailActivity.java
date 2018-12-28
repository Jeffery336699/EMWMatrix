package cc.emw.mobile.sale;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.CookieStore;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.SaleInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestTestParam;
import cc.emw.mobile.sale.adapter.RetailAdapter;
import cc.emw.mobile.util.MathExtend;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.PopMenu;
import cc.emw.mobile.view.RightMenu;

/**
 * 零售
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.retail)
public class RetailActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn; // 顶部条发布

    @ViewInject(R.id.retail_listview)
    private ListView mGoodsLv; // 购物车货物列表
    @ViewInject(R.id.retail_tv_empty)
    private TextView mEmptyView; //
    @ViewInject(R.id.retail_tv_total)
    private TextView mTotalTv; // 总金额
    @ViewInject(R.id.retail_btn_scan)
    private Button mScanBtn; // 扫描
    @ViewInject(R.id.retail_btn_submit)
    private Button mSumbitBtn; // 提交账单

    public static final String ACTION_REFRESH_RETAIL = "cc.emw.mobile.retail"; // 刷新的action
    private MyBroadcastReceive mReceive;
    private ArrayList<SaleInfo> mDataList; // 列表数据
    private RetailAdapter mListAdapter; // 列表适配器
    private Dialog mLoadingDialog; //加载框

    public static String mCookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login1000();
        initView();
        initMenu();
        IntentFilter intentFilter = new IntentFilter(ACTION_REFRESH_RETAIL);
        mReceive = new MyBroadcastReceive();
        registerReceiver(mReceive, intentFilter); //注册监听
    }

    @Override
    protected void onDestroy() {
        RetailAdapter.countMap.clear();
        unregisterReceiver(mReceive); //取消监听
        super.onDestroy();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mTotalTv.setTag(msg.obj);
            mTotalTv.setText("￥" + msg.obj);
        }

    };

    private void login1000() {
        try {
            HttpGet httpGet = new HttpGet("http://www.emw.cc:1000/Passport.axd?a=login&u=tengfei.li@zkbr.cc&p=123456&c=zkbr1");
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpGet);
            CookieStore cookieStore = httpclient.getCookieStore();
            List<Cookie> cookies = cookieStore.getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                StringBuilder cookieStr = new StringBuilder();
                for (int i = 0; i < cookies.size(); i++) {
                    cookieStr.append(cookies.get(i).getName());
                    cookieStr.append("=");
                    cookieStr.append(cookies.get(i).getValue());
                    if (i < cookies.size() - 1)
                        cookieStr.append(";");
                }
                mCookie = cookieStr.toString();
                System.out.println("cookie:" + cookieStr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("零售");
        mHeaderMoreBtn.setImageResource(R.drawable.cm_header_btn_more);
        mHeaderMoreBtn.setVisibility(View.VISIBLE);

        mGoodsLv.setEmptyView(mEmptyView);
        mDataList = new ArrayList<SaleInfo>();
        mListAdapter = new RetailAdapter(this, mDataList, mHandler);
        mGoodsLv.setAdapter(mListAdapter);
        mListAdapter.updateTotal();
    }

    private RightMenu mMenu;

    private void initMenu() {
        mMenu = new RightMenu(this);
        mMenu.addItem("订单列表", 0);
        mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
            @Override
            public void selected(View view, PopMenu.Item item, int position) {
                Intent intent = new Intent(RetailActivity.this, RetailOrderActivity.class);
                startActivity(intent);
            }
        });
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
//				HelpUtil.hideSoftInput(this, mContentEt);
                finish();
                break;
            case R.id.cm_header_btn_right:
                mMenu.showAsDropDown(v);
                break;
        }
    }

    @Event(value = {R.id.retail_btn_scan, R.id.retail_btn_submit})
    private void onFooterClick(View v) {
        switch (v.getId()) {
            case R.id.retail_btn_scan:
                Intent intent = new Intent(this, ScannerActivity.class);
                startActivityForResult(intent, 10);
                break;
            case R.id.retail_btn_submit:
                if (mDataList != null && mDataList.size() > 0) {
                    Intent createIntent = new Intent(this, RetailCreateActivity.class);
                    createIntent.putExtra("sale_list", mDataList);
                    startActivity(createIntent);
                } else {
                    Toast.makeText(RetailActivity.this, "请先扫描商品！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 10:
                    String huoNum = data.getStringExtra("huoNum");
                    getSaleInfo(huoNum);
                    break;
            }
        }
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_RETAIL.equals(action)) {
                RetailAdapter.countMap.clear();
                mDataList.clear();
                mListAdapter.setDataList(mDataList);
                mListAdapter.notifyDataSetChanged();
                mListAdapter.updateTotal();
            }
        }
    }

    /**
     * 通过货号获取商品信息
     */
    private void getSaleInfo(String huoNum) {
        RequestTestParam param = new RequestTestParam(Const.Url_Client + "?t=Pos.PosInfo&m=GetSaleInfoByStr");
        param.setStringParams(huoNum);
        x.http().post(param, new RequestCallback<SaleInfo>(SaleInfo.class) {

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在加载...");
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<SaleInfo> respList) {
                mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0) {
                    for (SaleInfo sale : respList) {
                        // 不存在该商品，加入商品列表
                        if (!RetailAdapter.countMap.containsKey(sale.ID)) {
                            mDataList.add(sale);
                            RetailAdapter.countMap.put(sale.ID, "1");
                        } else {//已存在该商品，增加商品数量
                            String count = RetailAdapter.countMap.get(sale.ID);
                            RetailAdapter.countMap.put(sale.ID, MathExtend.add(count, "1"));
                        }
                    }
                    mListAdapter.setDataList(mDataList);
                    mListAdapter.notifyDataSetChanged();
                    mListAdapter.updateTotal();
                } else {
                    Toast.makeText(RetailActivity.this, "暂无该商品信息", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(RetailActivity.this, throwable.getMessage());
            }
        });
    }
}
