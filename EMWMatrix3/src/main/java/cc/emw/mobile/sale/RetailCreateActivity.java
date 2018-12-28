package cc.emw.mobile.sale;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.SaleInfo;
import cc.emw.mobile.entity.SaleList;
import cc.emw.mobile.entity.SaleOrder;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.net.RequestTestParam;
import cc.emw.mobile.sale.adapter.RetailAdapter;
import cc.emw.mobile.sale.adapter.RetailCreateAdapter;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.wxapi.WXPayEntryActivity;

/**
 * 零售》订单生成
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.retail_create)
public class RetailCreateActivity extends BaseActivity {

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
    @ViewInject(R.id.retail_btn_submit)
    private Button mSumbitBtn; // 提交

    private ArrayList<SaleInfo> mDataList; // 列表数据
    private RetailCreateAdapter mListAdapter; // 列表适配器
    private Dialog mLoadingDialog; //加载框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataList = (ArrayList<SaleInfo>) getIntent().getSerializableExtra("sale_list");
        initView();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mTotalTv.setTag(msg.obj);
            mTotalTv.setText("￥" + msg.obj);
        }

    };

    private void initView() {
//        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("提交订单");
        mHeaderMoreBtn.setImageResource(R.drawable.more_top_btn);
        mHeaderMoreBtn.setVisibility(View.GONE);

        mGoodsLv.setEmptyView(mEmptyView);
        mListAdapter = new RetailCreateAdapter(this, mDataList, mHandler);
        mGoodsLv.setAdapter(mListAdapter);
        mListAdapter.updateTotal();
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right})
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

    @Event(value = {R.id.retail_btn_scan, R.id.retail_btn_submit})
    private void onFooterClick(View v) {
        switch (v.getId()) {
            case R.id.retail_btn_scan:
                Intent intent = new Intent(this, ScannerActivity.class);
                startActivityForResult(intent, 10);
                break;
            case R.id.retail_btn_submit:
                SaleOrder order = new SaleOrder();
                order.State = 0;
                order.TotalMoney = mTotalTv.getTag().toString();
                order.Creator = PrefsUtil.readUserInfo().ID;
                SimpleDateFormat format = new SimpleDateFormat(getString(R.string.timeformat4));
                order.CreateTime = format.format(new Date());

                ArrayList<SaleList> list = new ArrayList<SaleList>();
                for (int i = 0; i < mDataList.size(); i++) {
                    SaleInfo sale = mDataList.get(i);
                    SaleList detail = new SaleList();
                    detail.Name = sale.Name;
                    detail.Count = RetailAdapter.countMap.get(sale.ID);
                    detail.Price = sale.Price;
                    detail.HuoNum = sale.HuoNum;
                    detail.TiaoXinNum = sale.TiaoXinNum;
                    list.add(detail);
                }
                addSaleOrder(order, list);
                break;
        }
    }

    /**
     * 生成订单
     */
    private void addSaleOrder(SaleOrder order, ArrayList<SaleList> list) {
        RequestTestParam param = new RequestTestParam(Const.Url_Client + "?t=Pos.PosInfo&m=AddSaleOrder");
        param.setStringParams(order, list);
        x.http().post(param, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在处理...");
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(RetailCreateActivity.this, throwable.getMessage());
            }

            @Override
            public void onSuccess(String responseInfo) {
                mLoadingDialog.dismiss();
                String orderNo = responseInfo.replaceAll("\"", "");
                if (!TextUtils.isEmpty(orderNo) && !"0".equals(responseInfo)) {
                    Intent intent = new Intent(RetailCreateActivity.this, WXPayEntryActivity.class);
                    intent.putExtra("order_no", orderNo);
                    intent.putExtra("order_total", mTotalTv.getTag().toString());
                    startActivity(intent);
                    sendBroadcast(new Intent(RetailActivity.ACTION_REFRESH_RETAIL)); // 刷新(清空扫描清单)
                    finish();
                } else {
                    Toast.makeText(RetailCreateActivity.this, "提交订单失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
