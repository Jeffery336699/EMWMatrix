package cc.emw.mobile.project.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jven.wu on 2016/11/9.
 */
public abstract class BaseListAdapter2<T,K> extends BaseListAdapter1<T> {

    public BaseListAdapter2(Context context) {
        super(context);
    }

    /*@Override
    public int getChildrenCount(int groupPosition) {
        T groupItem = getGroup(groupPosition);
        if(groupItem != null && getChildDatas(groupItem) != null)
            return getChildDatas(groupItem).size();
        else
            return 0;
    }*/
    @Override
    public int getRealChildrenCount(int groupPosition) {
        T groupItem = getGroup(groupPosition);
        if(groupItem != null && getChildDatas(groupItem) != null)
            return getChildDatas(groupItem).size();
        else
            return 0;
    }

    @Override
    public K getChild(int groupPosition, int childPosition) {
        T groupItem = getGroup(groupPosition);
        if(groupItem != null){
            List<K> childList = getChildDatas(groupItem);
            if(childPosition >=0 && childList != null && childPosition < childList.size())
                return getChildDatas(groupItem).get(childPosition);
        }
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /*@Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        K item = getChild(groupPosition,childPosition);
        int layoutId = getChildLayoutId(groupPosition, item);
        final BaseViewHolder vh = BaseViewHolder.getViewHolder(this.mContext, convertView, parent, layoutId, groupPosition);
        convertChild(vh, item,groupPosition, childPosition,isLastChild);
        return vh.getConvertView();
    }*/
    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        K item = getChild(groupPosition,childPosition);
        int layoutId = getChildLayoutId(groupPosition, item);
        final BaseViewHolder vh = BaseViewHolder.getViewHolder(this.mContext, convertView, parent, layoutId, groupPosition);
        convertChild(vh, item,groupPosition, childPosition,isLastChild);
        return vh.getConvertView();
    }

    protected abstract void convertChild(BaseViewHolder vh, K item, int groupPosition, int position,boolean isLastChild);

    protected abstract int getChildLayoutId(int position, K item);

    protected abstract List<K> getChildDatas(T groupItem);

    protected Context getContext(){
        return this.mContext;
    }
}
