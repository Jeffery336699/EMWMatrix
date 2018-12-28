package cc.emw.mobile.sale.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.SaleOrder;
import cc.emw.mobile.sale.RetailDetailActivity;
import cc.emw.mobile.util.MathExtend;
import cc.emw.mobile.wxapi.WXPayEntryActivity;

/**
 * 零售》订单列表Adapter
 * @author shaobo.zhuang
 */
public class RetailOrderAdapter extends BaseAdapter {
	
	private Context mContext;
	private ArrayList<SaleOrder> mDataList;
	
	public RetailOrderAdapter(Context context) {
		this.mContext = context;
	}
	
	public void setDataList(ArrayList<SaleOrder> dataList) {
		this.mDataList = dataList;
	}

	@Override
	public int getCount() {
		return mDataList != null ? mDataList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final SaleOrder order = mDataList.get(position);
		final ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.retail_order_list_item, null);
			vh.noTv = (TextView) convertView.findViewById(R.id.retailorder_tv_no);
			vh.priceTv = (TextView) convertView.findViewById(R.id.retailorder_tv_price);
			vh.createtimeTv = (TextView) convertView.findViewById(R.id.retailorder_tv_createtime);
			vh.paidTv = (TextView) convertView.findViewById(R.id.retailorder_tv_paid);
			vh.payBtn = (Button) convertView.findViewById(R.id.retailorder_btn_pay);
			
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.noTv.setText(order.OrderNo);
		vh.priceTv.setText(MathExtend.round(order.TotalMoney, 2));
		vh.createtimeTv.setText(order.CreateTime);
		if (order.State == 0) {
			vh.paidTv.setVisibility(View.GONE);
			vh.payBtn.setVisibility(View.VISIBLE);
		} else {
			vh.paidTv.setVisibility(View.VISIBLE);
			vh.payBtn.setVisibility(View.GONE);
		}
		
		vh.payBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, WXPayEntryActivity.class);
				intent.putExtra("order_no", order.OrderNo);
				intent.putExtra("order_total", order.TotalMoney);
				mContext.startActivity(intent);
			}
		});
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, RetailDetailActivity.class);
				intent.putExtra("sale_order", order);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		TextView noTv;
		TextView priceTv;
		TextView createtimeTv;
		TextView paidTv;
		Button payBtn;
	}
	
}
