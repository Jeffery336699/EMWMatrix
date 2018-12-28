package cc.emw.mobile.sale;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.SaleList;
import cc.emw.mobile.entity.SaleOrder;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.net.RequestTestParam;
import cc.emw.mobile.sale.adapter.RetailDetailAdapter;
import cc.emw.mobile.util.MathExtend;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.wxapi.WXPayEntryActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 零售》订单详情
 *
 * @author shaobo.zhuang
 */
@ContentView(R.layout.retail_detail)
public class RetailDetailActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn; // 顶部条发布

    @ViewInject(R.id.retail_listview)
    private ListView mGoodsLv; // 商品清单列表
    @ViewInject(R.id.retaildetail_tv_orderno)
    private TextView mOrderNoTv; // 订单号
    @ViewInject(R.id.retaildetail_tv_ordertotal)
    private TextView mOrderTotalTv; // 订单金额
    @ViewInject(R.id.retaildetail_ll)
    private LinearLayout mOrderPaytypeLayout; // 支付方式的Layout
    @ViewInject(R.id.retaildetail_tv_orderpaytype)
    private TextView mOrderPaytypeTv; // 支付方式
    @ViewInject(R.id.retaildetail_tv_paid)
    private TextView mPaidTv; // 已支付
    @ViewInject(R.id.retaildetail_btn_pay)
    private Button mPayBtn; // 支付

    private ArrayList<SaleList> mDataList; // 列表数据
    private RetailDetailAdapter mListAdapter; // 列表适配器
    private Dialog mLoadingDialog; //加载框

    private SaleOrder saleOrder; // 列表传值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saleOrder = (SaleOrder) getIntent().getSerializableExtra("sale_order");
        initView();
        getSaleList(saleOrder.OrderNo);
    }

    private void initView() {
        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("订单信息");
        mHeaderMoreBtn.setImageResource(R.drawable.more_top_btn);
        mHeaderMoreBtn.setVisibility(View.GONE);

        mDataList = new ArrayList<SaleList>();
        mListAdapter = new RetailDetailAdapter(this);
        mGoodsLv.setAdapter(mListAdapter);

        mOrderNoTv.setText(saleOrder.OrderNo);
        mOrderTotalTv.setText(MathExtend.round(saleOrder.TotalMoney, 2));
        if (saleOrder.State == 0) { // 未支付
            mPaidTv.setVisibility(View.GONE);
            mPayBtn.setVisibility(View.VISIBLE);
            mOrderPaytypeLayout.setVisibility(View.GONE);
        } else {
            mPaidTv.setVisibility(View.VISIBLE);
            mPayBtn.setVisibility(View.GONE);
            mOrderPaytypeLayout.setVisibility(View.VISIBLE);
            if (saleOrder.PayType == 2) {
                mOrderPaytypeTv.setText("支付宝支付");
            } else if (saleOrder.PayType == 3) {
                mOrderPaytypeTv.setText("微信支付");
            } else if (saleOrder.PayType == 4) {
                mOrderPaytypeTv.setText("银联支付");
            } else {
                mOrderPaytypeTv.setText("现金支付");
            }
        }

        mPayBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RetailDetailActivity.this, WXPayEntryActivity.class);
                intent.putExtra("order_no", saleOrder.OrderNo);
                intent.putExtra("order_total", saleOrder.TotalMoney);
                startActivityForResult(intent, 10);
            }
        });
    }

    @Event(value = {R.id.cm_header_btn_left, R.id.cm_header_tv_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
//				HelpUtil.hideSoftInput(this, mContentEt);
                finish();
                break;
            case R.id.cm_header_tv_right:

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 10:
                    finish();
                    break;
            }
        }
    }

    /**
     * 通过订单号获取商品清单
     */
    private void getSaleList(String orderNo) {
        RequestTestParam param = new RequestTestParam(Const.Url_Client + "?t=Pos.PosInfo&m=GetSaleList");
        param.setStringParams(orderNo);
        x.http().post(param, new RequestCallback<SaleList>(SaleList.class) {
            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在加载...");
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<SaleList> respList) {
                mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0) {
                    mDataList.addAll(respList);
                    mListAdapter.setDataList(mDataList);
                    mListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(RetailDetailActivity.this, throwable.getMessage());
            }
        });
    }
}
