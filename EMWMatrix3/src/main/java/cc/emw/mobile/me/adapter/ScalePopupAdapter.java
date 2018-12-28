package cc.emw.mobile.me.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.emw.mobile.R;

/**
 * Created by tao.zhou on 2017/7/20.
 */

public class ScalePopupAdapter extends BaseAdapter {

    private List<String> mDataList;
    private Context mContext;

    public ScalePopupAdapter(Context mContext, List<String> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (vh == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_scale_popup, parent, false);
            vh.mTvItemName = (TextView) convertView.findViewById(R.id.tv_item_scale_popup);
            vh.imageView = (ImageView) convertView.findViewById(R.id.iv_item_scale_popup);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            vh.mTvItemName.setTextSize(12);
            vh.mTvItemName.setTextColor(Color.parseColor("#FF757575"));
        } else {
            vh.mTvItemName.setTextSize(15);
            vh.mTvItemName.setTextColor(Color.parseColor("#FF202020"));
        }
        vh.mTvItemName.setText(mDataList.get(position));

        return convertView;
    }

    class ViewHolder {
        TextView mTvItemName;
        ImageView imageView;
    }
}
