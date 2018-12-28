package cc.emw.mobile.calendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cc.emw.mobile.R;

/**
 * Created by ${zrjt} on 2016/11/10.
 */
public class TestAdapter extends BaseAdapter {

    private Context context;

    private List<String> mDatas;

    public TestAdapter(Context context, List<String> list) {
        this.context = context;
        this.mDatas = list;
    }

    @Override
    public int getCount() {
        return mDatas.size();
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
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_test1,null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_test);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(mDatas.get(position));

        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
