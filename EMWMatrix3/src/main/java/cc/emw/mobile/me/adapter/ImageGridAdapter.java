package cc.emw.mobile.me.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.me.imagepicker.bean.ImageItem;

public class ImageGridAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ImageItem> mDatas = null;

    public ImageGridAdapter(Context c) {
        this.context = c;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        } else {
            return mDatas.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void swapDatas(ArrayList<ImageItem> images) {
        if (this.mDatas != null) {
            this.mDatas.clear();
            this.mDatas = null;
        }
        this.mDatas = images;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<ImageItem> data) {
        if (data == null) {
            return;
        }
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_me_suggest_img, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_me_suggest_item_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage("file://" + mDatas.get(position).path, holder.imageView);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatas.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView imageView;
    }
}
