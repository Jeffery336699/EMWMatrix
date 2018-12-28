package cc.emw.mobile.sale.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.SaleInfo;
import cc.emw.mobile.util.MathExtend;

/**
 * 零售》订单生成Adapter
 * @author shaobo.zhuang
 */
public class RetailCreateAdapter extends BaseAdapter {
	
	private Context mContext;
	private Handler mHandler;
	private ArrayList<SaleInfo> mDataList;
	
	public RetailCreateAdapter(Context context,
			ArrayList<SaleInfo> dataList, Handler handler) {
		this.mContext = context;
		this.mDataList = dataList;
		this.mHandler = handler;
	}
	
	public void setDataList(ArrayList<SaleInfo> dataList) {
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
	TextWatcher boxWatcher, countWatcher;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final SaleInfo saleInfo = mDataList.get(position);
		final ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.retail_create_list_item, null);
			vh.noTv = (TextView) convertView.findViewById(R.id.goods_tv_no);
			vh.nameTv = (TextView) convertView.findViewById(R.id.goods_tv_name);
			vh.priceTv = (TextView) convertView.findViewById(R.id.goods_tv_price);
			vh.countEt = (TextView) convertView.findViewById(R.id.goods_et_count);
			
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.noTv.setText(saleInfo.HuoNum);
		vh.nameTv.setText(saleInfo.Name);
		vh.priceTv.setText(saleInfo.Price);
		vh.countEt.setText(RetailAdapter.countMap.get(saleInfo.ID));
		
		return convertView;
	}

	static class ViewHolder {
		TextView noTv;
		TextView nameTv;
		TextView priceTv;
		TextView countEt;
	}
	
	/**
	 * 更新合计金额
	 */
	public void updateTotal(){
		if (mDataList != null) {
			Message msg = mHandler.obtainMessage();
			double total = 0;
			for (int i = 0; i < mDataList.size(); i++) {
				SaleInfo sale = mDataList.get(i);
				String count = RetailAdapter.countMap.get(sale.ID);
				total += Double.valueOf(MathExtend.multiply(sale.Price, count));
			}
			msg.obj = MathExtend.round(total+"", 2);
			mHandler.sendMessage(msg);
		}
	}
	
	public ArrayList<SaleInfo> getDataList(){
		return mDataList;
	}
	
}
