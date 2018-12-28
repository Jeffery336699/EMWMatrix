package cc.emw.mobile.base;

import android.view.View;


/**
 * Created by dupeng   8.30
 * Holder基类
 */
public abstract class BaseHolder<T> {
    public View mHolderView;//可以提供的视图
    private T mData;


    public BaseHolder() {
        mHolderView = initHolderViewAndFindViews();
        mHolderView.setTag(this);
    }
    /**
     * 接受数据   进行数据和视图绑定操作
     * @param data
     */
    public void setDataAndRefreshHolderView(int position,T data) {
        mData=data;
        refreshHolderView(position,data);
    }
    /**
     * @des  进行数据和视图的绑定操作
     * @cal  集成BaseHolder必写方法    重载不同情况下实现
     */
    public abstract void refreshHolderView(int position,T data);

    /**
     * @des 决定根视图长什么样子并且找出孩子对象  转换成成员变量
     * @cal 集成BaseHolder必写方法
     */
    public abstract View initHolderViewAndFindViews();



}
