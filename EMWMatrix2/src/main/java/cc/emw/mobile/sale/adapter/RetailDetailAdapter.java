package cc.emw.mobile.sale.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.SaleList;
import cc.emw.mobile.util.MathExtend;

/**
 * 零售》订单详情Adapter
 * @author shaobo.zhuang
 */
public class RetailDetailAdapter extends BaseAdapter {
	
	private Context mContext;
	private ArrayList<SaleList> mDataList;
	
	public RetailDetailAdapter(Context context) {
		this.mContext = context;
	}
	
	public void setDataList(ArrayList<SaleList> dataList) {
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
		final SaleList order = mDataList.get(position);
		final ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.retail_detail_list_item, null);
			vh.nameTv = (TextView) convertView.findViewById(R.id.retaildetail_tv_name);
			vh.countTv = (TextView) convertView.findViewById(R.id.retaildetail_tv_count);
			vh.priceTv = (TextView) convertView.findViewById(R.id.retaildetail_tv_price);
			
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.nameTv.setText(order.Name);
		vh.countTv.setText("x"+order.Count);
		vh.priceTv.setText(MathExtend.multiply(order.Price, order.Count));
		
		
		return convertView;
	}

	static class ViewHolder {
		TextView nameTv;
		TextView countTv;
		TextView priceTv;
	}
	
}
