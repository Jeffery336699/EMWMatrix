package cc.emw.mobile.project.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.view.AnimatedExpandableListView;

/**
 * Created by jven.wu on 2016/11/9.
 */
//public abstract class BaseListAdapter1<T> extends BaseExpandableListAdapter {
public abstract class BaseListAdapter1<T> extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    protected LayoutInflater mInflater;
    protected List<T> mDatas;
    protected Context mContext;

    public BaseListAdapter1(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mDatas = new ArrayList<T>();
    }

    @Override
    public int getGroupCount() {
        return mDatas.size();
    }

    /*@Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }*/
    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public T getGroup(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < mDatas.size())
            return mDatas.get(groupPosition);
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        T item = getGroup(groupPosition);
        int layoutId = getGroupLayoutId(groupPosition, item);
        final BaseViewHolder vh = BaseViewHolder.getViewHolder(this.mContext, convertView, parent, layoutId, groupPosition);
        convertGroup(vh, item, groupPosition,isExpanded);
        return vh.getConvertView();
    }

    /*@Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }*/
    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public List<T> getDatas() {
        return this.mDatas;
    }

    protected abstract void convertGroup(BaseViewHolder vh, T item, int position,boolean isExpanded);

    protected abstract int getGroupLayoutId(int position, T item);

    public void updateItem(int location, T item) {
        if (mDatas.isEmpty()) return;
        mDatas.set(location, item);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        checkListNull();
        mDatas.add(item);
        notifyDataSetChanged();
    }

    public void addItem(int location, T item) {
        checkListNull();
        mDatas.add(location, item);
        notifyDataSetChanged();
    }

    public void addItem(List<T> items) {
        checkListNull();
        mDatas.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(int position, List<T> items) {
        checkListNull();
        mDatas.addAll(position, items);
        notifyDataSetChanged();
    }

    public void removeItem(int location) {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.remove(location);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void checkListNull() {
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
    }

    public int getCurrentPage() {
        return getGroupCount() % 20;
    }

    protected Context getContext(){
        return this.mContext;
    }
}
