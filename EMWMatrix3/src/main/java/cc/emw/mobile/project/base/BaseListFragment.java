package cc.emw.mobile.project.base;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

//import com.mobeta.android.dslv.DragSortController;
//import com.mobeta.android.dslv.DragSortListView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseFragment;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.Logger;

/**
 * Created by jven.wu on 2016/11/9.
 */
@ContentView(R.layout.fragment_base_list)
public abstract class BaseListFragment<T> extends BaseFragment implements
        SuperRefreshLayout.SuperRefreshLayoutListener,
        AdapterView.OnItemClickListener, View.OnClickListener {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_LOADING = 1;
    public static final int TYPE_NO_MORE = 2;
    public static final int TYPE_ERROR = 3;
    public static final int TYPE_NET_ERROR = 4;

    @ViewInject(R.id.list_top_content_ll)
    protected LinearLayout listTopContent;
    @ViewInject(R.id.superRefreshLayout)
    private SuperRefreshLayout mRefreshLayout;
    @ViewInject(R.id.listView)
    private ListView mListView;
    @ViewInject(R.id.error_layout)
    private EmptyLayout mErrorLayout;

//    private DragSortController mController;
//    public int dragStartMode = DragSortController.ON_LONG_PRESS;
//    public boolean removeEnabled = false;
//    public int removeMode = DragSortController.FLING_REMOVE;
//    public boolean sortEnabled = true;
//    public boolean dragEnabled = true;

    private View mFooterView;
    private ProgressBar mFooterProgressBar;
    private TextView mFooterText;

    protected String CACHE_NAME = getClass().getName();
    protected RequestCallback mRequestCallback;
    protected BaseListAdapter<T> mAdapter;
    protected PageBean<T> mPageBean;
    private boolean mIsRefresh = false;
    protected View mTopLayout;

//    private DragSortListView.DragListener onDrag =
//            new DragSortListView.DragListener() {
//                @Override
//                public void drag(int from, int to) {
//                    Logger.w("drag_view", "drag");
//                }
//            };
//
//    private DragSortListView.DropListener onDrop =
//            new DragSortListView.DropListener() {
//                @Override
//                public void drop(int from, int to) {
//                    Logger.w("drag_view", "drop");
//                    if (from != to) {
//                        T item = mAdapter.getItem(from);
//                        mAdapter.removeItem(from);
//                        mAdapter.addItem(to,item);
//                    }
//                }
//            };
//
//    private DragSortListView.RemoveListener onRemove =
//            new DragSortListView.RemoveListener() {
//                @Override
//                public void remove(int which) {
//                    Logger.w("drag_view", "remove");
//                    mAdapter.removeItem(which);
//                }
//            };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initWidget();
        initData();
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mListView.setDragListener(onDrag);
//        mListView.setDropListener(onDrop);
//        mListView.setRemoveListener(onRemove);
    }

    @Override
    public void onFirstUserVisible() {
        onRefreshing();
    }

    protected void initWidget() {
        if (getTopContentLayoutId() != 0) {
            mTopLayout = LayoutInflater.from(getActivity()).inflate(getTopContentLayoutId(), null);
            listTopContent.addView(mTopLayout);
        }
//        int[] colors = getResources().getIntArray(R.array.google_colors);
        mRefreshLayout.setColorSchemeResources(
                R.color.ptr_blue, R.color.ptr_green,
                R.color.ptr_yellow, R.color.ptr_red);
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_list_view_footer, null);
        mFooterText = (TextView) mFooterView.findViewById(R.id.tv_footer);
        mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.pb_footer);
        mListView.setOnItemClickListener(this);
        setFooterType(TYPE_LOADING);
        mErrorLayout.setOnLayoutClickListener(this);
        if (isNeedFooter())
            mListView.addFooterView(mFooterView);

//        mController = buildController(mListView);
//        mListView.setFloatViewManager(mController);
//        mListView.setOnTouchListener(mController);
//        mListView.setDragEnabled(dragEnabled);
    }

    protected void initData() {
        mAdapter = getListAdapter();
        mListView.setAdapter(mAdapter);
//        mRequestCallback = new RequestCallback<T>() {
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                onRequestError(ex.getMessage());
//                onRequestFinish();
//            }
//
//            @Override
//            public void onFinished() {
//            }
//
//            @Override
//            public void onParseSuccess(List<T> retList) {
//                try {
//                    if (retList != null && retList.size() >= 0) {
//                        onRequestSuccess("sucess");
//                        mPageBean.setItems(retList);
//                        setListData(mPageBean);
//                    } else {
//                        setFooterType(TYPE_NO_MORE);
//                        //mRefreshLayout.setNoMoreData();
//                    }
//                    onRequestFinish();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    onError(e,false);
//                }
//            }
//        };

        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                if (Looper.myLooper() == null)
                    Looper.prepare();
                mPageBean = (PageBean<T>) CacheManager.readObject(getActivity(), CACHE_NAME);
                //if is the first loading
                if (mPageBean == null) {
                    mPageBean = new PageBean<>();
                    mPageBean.setItems(new ArrayList<T>());
                    onRefreshing();
                } else {
                    mRoot.post(new Runnable() {
                        @Override
                        public void run() {
//                            mAdapter.addItem(mBean.getItems());
                            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                            mRefreshLayout.setVisibility(View.VISIBLE);
                            onRefreshing();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        onRefreshing();
    }

    @Override
    public void onRefreshing() {
        mIsRefresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
//        requestData();
        onRequestFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * request network data
     */
    protected void requestData() {
        onRequestStart();
        setFooterType(TYPE_LOADING);
    }

    protected void onRequestStart() {

    }

    protected void onRequestSuccess(String msg) {

    }

    protected void onRequestError(String msg) {
        setFooterType(TYPE_NET_ERROR);
        if (mAdapter.getDatas().size() == 0)
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
    }

    protected void onRequestFinish() {
        onComplete();
    }

    protected void onComplete() {
        mRefreshLayout.onLoadComplete();
        mIsRefresh = false;
    }

    protected void setListData(final PageBean<T> resultBean) {
        if (mIsRefresh) {
            //cache the time
            mAdapter.clear();
            mAdapter.addItem(resultBean.getItems());
            mRefreshLayout.setCanLoadMore();
            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    CacheManager.saveObject(getActivity(), resultBean, CACHE_NAME);
                }
            });
        } else {
            mAdapter.addItem(resultBean.getItems());
        }
        if (resultBean.getItems().size() < 20) {
            if (isAdded())
                setFooterType(TYPE_NO_MORE);
            //mRefreshLayout.setNoMoreData();
        }
        if (mAdapter.getDatas().size() > 0 || true) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }
    }

    protected abstract BaseListAdapter<T> getListAdapter();

    protected abstract Type getType();

    protected boolean isNeedFooter() {
        return true;
    }

    protected void setFooterType(int type) {
        switch (type) {
            case TYPE_NORMAL:
            case TYPE_LOADING:
                mFooterText.setText(getResources().getString(R.string.footer_type_loading));
                mFooterProgressBar.setVisibility(View.VISIBLE);
                break;
            case TYPE_NET_ERROR:
                mFooterText.setText(getResources().getString(R.string.footer_type_net_error));
                mFooterProgressBar.setVisibility(View.GONE);
                break;
            case TYPE_ERROR:
                mFooterText.setText(getResources().getString(R.string.footer_type_error));
                mFooterProgressBar.setVisibility(View.GONE);
                break;
            case TYPE_NO_MORE:
                mFooterText.setText(getResources().getString(R.string.footer_type_not_more));
                mFooterProgressBar.setVisibility(View.GONE);
                break;
        }
    }

    protected int getTopContentLayoutId() {
        return 0;
    }

    protected void setTopContentLayout() {

    }

    /**
     * Called in onCreateView. Override this to provide a custom
     * DragSortController.
     */
//    public DragSortController buildController(DragSortListView dslv) {
//        // defaults are
//        //   dragStartMode = onDown
//        //   removeMode = flingRight
//        DragSortController controller = new DragSortController(dslv);
////        controller.setDragHandleId(R.id.drag_handle);
////        controller.setClickRemoveId(R.id.click_remove);
//        controller.setRemoveEnabled(removeEnabled);
//        controller.setSortEnabled(sortEnabled);
//        controller.setDragInitMode(dragStartMode);
//        controller.setRemoveMode(removeMode);
//        return controller;
//    }
}
