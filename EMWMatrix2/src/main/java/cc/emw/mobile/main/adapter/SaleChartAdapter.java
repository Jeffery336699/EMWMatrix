package cc.emw.mobile.main.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cc.emw.mobile.entity.GridControl.ChartInfo;

import java.util.ArrayList;

import cc.emw.mobile.R;


/**
 * Created by chengyong.liu on 2016/5/10.
 */
public class SaleChartAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ChartInfo> mDatas;

    public SaleChartAdapter(Context context) {
        mContext = context;
        mDatas = new ArrayList<ChartInfo>();
    }

    public void setData(ArrayList<ChartInfo> data) {
        mDatas = data;
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mDatas != null ? mDatas.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.list_children, null);
            vh = new ViewHolder();
            vh.tvSelect = (TextView) convertView.findViewById(R.id.tv_navigation);
           

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        // 设置数据
        ChartInfo data = mDatas.get(position);
        vh.tvSelect.setText(data.Name);
        return convertView;
    }
    class ViewHolder {
        TextView tvSelect;
    }
}
