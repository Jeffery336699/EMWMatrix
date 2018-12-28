package cc.emw.mobile.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dupeng   8.30
 * Adapter基类，继承该类后只需关注getView()方法即可
 */
public abstract class MyBaseAdapter <ItemBean> extends BaseAdapter {
    public List<ItemBean> mDatas =null;

    public MyBaseAdapter() {}
    public MyBaseAdapter(List<ItemBean> datas) {
        mDatas = datas;
    }
    @Override
    public int getCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mDatas != null) {
            return mDatas.get(position);
        }
        return null;
    }

    public void setData(List<ItemBean> dataList) {
        if (dataList != null) {
            if(mDatas != null) {
                mDatas.clear();
            }else{
                mDatas=new ArrayList<>();
            }
            mDatas.addAll(dataList);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    //子类实现
    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
