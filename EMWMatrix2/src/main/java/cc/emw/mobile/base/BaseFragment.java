package cc.emw.mobile.base;

import org.xutils.x;

import cc.emw.mobile.R;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Fragment基类
 * @author shaobo.zhuang
 *
 */
public class BaseFragment extends Fragment {
	
	/**
	 * setUserVisibleHint是在onCreateView之前调用的，那么在视图未初始化的时候，
	 * 在onFirstUserVisible当中就使用的话，就会有空指针的异常
	 */
	private boolean isPrepared;
	private boolean injected = false;

    private boolean isStartAnim = true; //是否跳转动画

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        injected = true;
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!injected) {
            x.view().inject(this, this.getView());
        }
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }
	
	/**
     * 第一次onResume中的调用onUserVisible避免操作与onFirstUserVisible操作重复
     */
    private boolean isFirstResume = true;

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }

    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            } else {
                onUserInvisible();
            }
        }
    }

    public synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
    }

    /**
     * 第一次fragment可见（进行初始化工作）
     */
    public void onFirstUserVisible() {

    }

    /**
     * fragment可见（切换回来或者onResume）
     */
    public void onUserVisible() {

    }

    /**
     * 第一次fragment不可见（不建议在此处理事件）
     */
    public void onFirstUserInvisible() {

    }

    /**
     * fragment不可见（切换掉或者onPause）
     */
    public void onUserInvisible() {

    }
	
	/**
     * 加载对话框
     * @param resId 字符串资源ID
     * @return
     */
    public Dialog createLoadingDialog(int resId) {
    	return createLoadingDialog(getString(resId));
    }
    /**
     * 加载对话框
     * @param msg 提示信息
     * @return
     */
    public Dialog createLoadingDialog(String msg) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.loading_dialog, null);
        TextView tipTextView = (TextView) v.findViewById(R.id.loading_tv_tip);
        tipTextView.setText(msg);
        Dialog loadingDialog = new Dialog(getActivity(), R.style.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (isStartAnim) {
            getActivity().overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (isStartAnim) {
            getActivity().overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
        }
    }

    public void setStartAnim(boolean isStartAnim) {
        this.isStartAnim = isStartAnim;
    }
}
