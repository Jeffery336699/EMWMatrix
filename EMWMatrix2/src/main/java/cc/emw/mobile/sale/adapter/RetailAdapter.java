package cc.emw.mobile.sale.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.SaleInfo;
import cc.emw.mobile.util.MathExtend;

/**
 * 零售Adapter
 * @author shaobo.zhuang
 */
public class RetailAdapter extends BaseAdapter {
	
	private Context mContext;
	private Handler mHandler;
	private ArrayList<SaleInfo> mDataList;
	/** 存放每种商品数量 */
	public static HashMap<Integer, String> countMap = new HashMap<Integer, String>();
	
	public RetailAdapter(Context context,
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
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final SaleInfo saleInfo = mDataList.get(position);
		final ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.retail_list_item, null);
			vh.noTv = (TextView) convertView.findViewById(R.id.goods_tv_no);
			vh.nameTv = (TextView) convertView.findViewById(R.id.goods_tv_name);
			vh.priceTv = (TextView) convertView.findViewById(R.id.goods_tv_price);
			vh.countEt = (EditText) convertView.findViewById(R.id.goods_et_count);
			vh.countEt.setTag(position);
			vh.countEt.addTextChangedListener(new MyTextWatcher(vh) {
	            @Override
	            public void afterTextChanged(Editable s, ViewHolder holder) {
					if (!TextUtils.isEmpty(s.toString()) && Integer.valueOf(s.toString()) >0) {
						int position=(Integer) holder.countEt.getTag();
		                SaleInfo sale = mDataList.get(position);
		                countMap.put(sale.ID, s.toString());
		                updateTotal();
					} else {
						Toast.makeText(mContext, "请输入正确的购买数量", Toast.LENGTH_SHORT).show();
						vh.countEt.requestFocus();
					}
	            }
	        });
			
			convertView.setOnLongClickListener(new MyOnLongClickListener(vh) {
				@Override
				public boolean onLongClick(View v, final ViewHolder holder) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
					dialog.setTitle("温馨提示")
					.setMessage("你确定要删除该商品？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int position=(Integer) holder.countEt.getTag();
							countMap.remove(mDataList.get(position).ID); // 删除该数据存放的数量
							mDataList.remove(position); // 删除该数据
							notifyDataSetChanged();
							updateTotal();
						}
					}).setNegativeButton("取消", null).show();
					
					return false;
				}
			});
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.noTv.setText(saleInfo.HuoNum);
		vh.nameTv.setText(saleInfo.Name);
		vh.priceTv.setText(saleInfo.Price);
		String count = countMap.get(saleInfo.ID);
		vh.countEt.setText(count);
		
		return convertView;
	}

	static class ViewHolder {
		TextView noTv;
		TextView nameTv;
		TextView priceTv;
		EditText countEt;
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
				String count = countMap.get(sale.ID);
				total += Double.valueOf(MathExtend.multiply(sale.Price, count));
			}
			msg.obj = MathExtend.round(total+"", 2);
			mHandler.sendMessage(msg);
		}
	}
	
	
	public ArrayList<SaleInfo> getDataList(){
		return mDataList;
	}
	
	
	private abstract class MyTextWatcher implements TextWatcher{
        private ViewHolder mHolder;
        
        public MyTextWatcher(ViewHolder holder) {
            this.mHolder=holder;
        }
        
        @Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
        @Override
        public void afterTextChanged(Editable s) {
            afterTextChanged(s, mHolder);
        }
        public abstract void afterTextChanged(Editable s,ViewHolder holder);
    }
	private abstract class MyOnLongClickListener implements OnLongClickListener {
        
        private ViewHolder mHolder;
        
        public MyOnLongClickListener(ViewHolder holder) {
            this.mHolder=holder;
        }
        
        @Override
        public boolean onLongClick(View v) {
            return onLongClick(v, mHolder);
        }
        public abstract boolean onLongClick(View v,ViewHolder holder);
        
    }
}
