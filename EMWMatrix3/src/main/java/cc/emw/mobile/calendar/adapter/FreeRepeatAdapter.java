package cc.emw.mobile.calendar.adapter;

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
import cc.emw.mobile.me.adapter.ScalePopupAdapter;

/**
 * Created by tao.zhou on 2017/7/20.
 */

public class FreeRepeatAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mDataList;

    public FreeRepeatAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<String> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_free_repeat, parent, false);
            vh.mTvItemName = (TextView) convertView.findViewById(R.id.tv_item_free_repeat);
            vh.imageView = (ImageView) convertView.findViewById(R.id.iv_item_free_repeat);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.mTvItemName.setText(mDataList.get(position));

        return convertView;
    }

    class ViewHolder {
        TextView mTvItemName;
        ImageView imageView;
    }
}
