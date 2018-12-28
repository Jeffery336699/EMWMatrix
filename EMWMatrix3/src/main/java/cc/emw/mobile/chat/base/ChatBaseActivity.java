package cc.emw.mobile.chat.base;

import android.os.Bundle;

import cc.emw.mobile.base.BaseActivity;

/**
 * Created by sunny.du on /5/8.
 * V  VIEW层接口
 * T  Presenter层的引用约束
 * 用于切断view层和persenter层的引用。避免OOM的情况发生
 * 模板模式实例化persenter
 */
public abstract class ChatBaseActivity<V, T extends BasePresenter<V>> extends BaseActivity {
    protected T mPersenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPersenter = createPresent();
        mPersenter.attachView((V) this);
    }

    /**
     * T 具体的Persenter
     * 模板方法
     * 让子类实现具体的构建过程
     * @return   返回具体的persenter层类
     */
    protected abstract T createPresent();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPersenter.dettach();
    }

}
