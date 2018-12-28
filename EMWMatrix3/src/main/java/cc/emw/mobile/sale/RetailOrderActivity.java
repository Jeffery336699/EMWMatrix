package cc.emw.mobile.sale;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.SaleOrder;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestTestParam;
import cc.emw.mobile.sale.adapter.RetailOrderAdapter;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;

/**
 * 零售》订单列表
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.retail_order)
public class RetailOrderActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn; // 顶部条发布

    @ViewInject(R.id.retail_listview)
    private ListView mGoodsLv; // 订单列表
    @ViewInject(R.id.retail_tv_empty)
    private TextView mEmptyView; //

    public static final String ACTION_REFRESH_RETAIL_ORDER = "cc.emw.mobile.retail_order"; // 刷新的action
    private MyBroadcastReceive mReceive;
    private ArrayList<SaleOrder> mDataList; // 列表数据
    private RetailOrderAdapter mListAdapter; // 列表适配器
    private Dialog mLoadingDialog; //加载框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        IntentFilter intentFilter = new IntentFilter(ACTION_REFRESH_RETAIL_ORDER);
        mReceive = new MyBroadcastReceive();
        registerReceiver(mReceive, intentFilter); //注册监听

        getSaleOrder(PrefsUtil.readUserInfo().ID);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceive); //取消监听
        super.onDestroy();
    }

    private void initView() {
//        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("订单列表");
        mHeaderMoreBtn.setImageResource(R.drawable.more_top_btn);
        mHeaderMoreBtn.setVisibility(View.GONE);

        mGoodsLv.setEmptyView(mEmptyView);
        mDataList = new ArrayList<SaleOrder>();
        mListAdapter = new RetailOrderAdapter(this);
        mGoodsLv.setAdapter(mListAdapter);
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
//				HelpUtil.hideSoftInput(this, mContentEt);
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:

                break;
        }
    }

    class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_REFRESH_RETAIL_ORDER.equals(action)) {
                getSaleOrder(PrefsUtil.readUserInfo().ID);
            }
        }
    }

    /**
     * 通过用户ID获取订单列表
     */
    private void getSaleOrder(int uid) {
        RequestTestParam param = new RequestTestParam(Const.Url_Client + "?t=Pos.PosInfo&m=GetSaleOrder");
        param.setStringParams(uid);
        x.http().post(param, new RequestCallback<SaleOrder>(SaleOrder.class) {

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在加载...");
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<SaleOrder> respList) {
                mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0) {
                    mDataList.clear();
                    mDataList.addAll(respList);
                    mListAdapter.setDataList(mDataList);
                    mListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(RetailOrderActivity.this, throwable.getMessage());
            }
        });
    }
}
