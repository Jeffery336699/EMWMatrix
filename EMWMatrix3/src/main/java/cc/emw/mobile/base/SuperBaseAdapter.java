package cc.emw.mobile.base;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by dupeng   8.30
 * 可拓展的基类
 */
public abstract class SuperBaseAdapter<T> extends MyBaseAdapter<T> {
    public SuperBaseAdapter(List<T> datas) {
        super(datas);
    }
    public SuperBaseAdapter() { }
    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
