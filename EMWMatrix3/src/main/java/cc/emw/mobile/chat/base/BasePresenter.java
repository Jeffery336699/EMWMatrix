package cc.emw.mobile.chat.base;

import java.lang.ref.WeakReference;

/**
 * Created by sunny.du on 2017/5/8.
 *   T  代表view层的某一个Activity
 *   用于切断view层和persenter层的引用。避免OOM的情况发生
 */
public class BasePresenter<T> {
    protected WeakReference<T> mActivity;
    public void attachView(T View){
        mActivity=new WeakReference<T>(View);
    }

    public void dettach(){
        if(mActivity != null){
            mActivity.clear();
            mActivity=null;
        }
    }
}
