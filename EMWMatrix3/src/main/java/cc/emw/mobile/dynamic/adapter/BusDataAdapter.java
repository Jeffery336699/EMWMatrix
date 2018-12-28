package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.Navigation;
import cc.emw.mobile.view.IconTextView;

/**
 * 业务数据1级adapter
 */
public class BusDataAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Navigation> mDataList;

    public BusDataAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(ArrayList<Navigation> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).ID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_busdata, null);
            vh.childIconTv = (IconTextView) convertView.findViewById(R.id.tv_leftmenu_childicon);
            vh.childNameTv = (TextView) convertView.findViewById(R.id.tv_leftmenu_childname);
            convertView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) convertView.getTag(R.id.tag_first);
        }
        final Navigation nav = mDataList.get(position);
        vh.childIconTv.setIconText(IconTextView.getIconCode(nav.ICON));
        vh.childNameTv.setText(nav.Name);

        convertView.setTag(R.id.tag_second, nav);
        return convertView;
    }

    class ViewHolder {
        IconTextView childIconTv;
        TextView childNameTv;
    }

}
