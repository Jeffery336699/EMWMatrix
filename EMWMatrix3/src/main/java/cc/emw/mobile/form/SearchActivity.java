package cc.emw.mobile.form;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.inqbarna.tablefixheaders.TableFixHeaders;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.form.adapter.RadioTableAdapter;
import cc.emw.mobile.form.entity.DataTable;
import cc.emw.mobile.form.entity.Elements2;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.ToastUtil;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.imid.swipebacklayout.lib.SwipeBackLayout;


/**
 * 表单详情(ElementType.SEARCHER)查找页面
 */
@ContentView(R.layout.activity_search)
public class SearchActivity extends BaseActivity {
    private static final String TAG = "SearchActivity";

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.table)
    private TableFixHeaders tableFixHeaders;

    private Dialog mLoadingDialog; //加载框
    private MyAdapter adapter; // adapter
    private DataTable mDataTable;
    private Elements2 formElement;
    private int formPage;
    private int khid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
        getSwipeBackLayout().setEdgeSize(DisplayUtil.getDisplayHeight(this) / 2 - 10);
        formElement = (Elements2)getIntent().getSerializableExtra("elem");
        mDataTable = (DataTable)getIntent().getSerializableExtra("data_table");
        formPage = getIntent().getIntExtra("page_id",0);
        khid = getIntent().getIntExtra("record_id", 0);
        initView();

        if (mDataTable != null) {
            adapter.setDataTable(mDataTable);
            adapter.notifyDataSetChanged();
            tableFixHeaders.scrollTo(0, 0);
        } else {
            getList(formPage, formElement.ID, khid);
        }
        tableFixHeaders.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Logger.w("scrolls", "curY:"+tableFixHeaders.getActualScrollY()+", maxY:"+tableFixHeaders.getMaxScrollY()+", height:"+tableFixHeaders.getHeight());
                if (tableFixHeaders.getActualScrollY() == 0 && tableFixHeaders.getMaxScrollY() < tableFixHeaders.getHeight()) {
                    getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
                    getSwipeBackLayout().setEnableGesture(true);
                } else if (tableFixHeaders.getActualScrollY() == 0) {
                    getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
                    getSwipeBackLayout().setEnableGesture(true);
                } else if (tableFixHeaders.getActualScrollY() == tableFixHeaders.getMaxScrollY()) {
                    getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_BOTTOM);
                    getSwipeBackLayout().setEnableGesture(true);
                } else {
                    getSwipeBackLayout().setEnableGesture(false);
                }
                return false;
            }
        });
    }

    private void initView() {
        ((TextView)findViewById(R.id.cm_header_tv_title)).setText(TextUtils.isEmpty(formElement.Title) ? formElement.Name : formElement.Title);
        ((TextView)findViewById(R.id.cm_header_tv_right9)).setText("确认");
        mPtrFrameLayout.setEnabled(false);
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        /*mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {
                // here check list view, not content.
                return tableFixHeaders.getActualScrollY() == 0;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getList(formPage, fromElement.ID, khid);
            }
        });*/

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, DisplayUtil.dip2px(this, 15));
        header.setPtrFrameLayout(mPtrFrameLayout);

        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);

        // binding view and data
        adapter = new MyAdapter(this);
        adapter.setDefaultSelect(formElement.Value, formElement.Text);
        if (formElement.Columns != null) {
            for (int i = 0; i < formElement.Columns.size(); i++) {
                Elements2.Column column = formElement.Columns.get(i);
                if (column.IsValue) {
                    adapter.setIdIndex(i - 1);
                } else if (column.IsText) {
                    adapter.setTextIndex(i - 1);
                }
            }
        }
        tableFixHeaders.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (mDataTable != null && mDataTable.MaxRow > 0) {
            Intent intent = new Intent();
            intent.putExtra("elem", formElement);
            intent.putExtra("data_table", mDataTable);
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    @Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                if (adapter.getSelectID() > 0) {
                    formElement.Text = adapter.getText();
                    formElement.Value = adapter.getSelectID() + "";
                    Intent intent = getIntent();
                    intent.putExtra("elem", formElement);
                    intent.putExtra("data_table", mDataTable);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    ToastUtil.showToast(this, "请先选择！");
                }
                break;
        }
    }


    public void getList(int page, final String elem, int khid) {
        String requestUrl = Const.BASE_URL + "/Page/" + page + "/searcher?elem=" + elem + "&khid="+ khid;
        Log.d(TAG,"request: " + requestUrl);
        RequestParam params = new RequestParam(requestUrl);
        x.http().post(params, new RequestCallback<DataTable>(DataTable.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(SearchActivity.this, "获取列表数据失败！");
                mPtrFrameLayout.refreshComplete();
//                mGetListDataTable = null;
            }

            /*@Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在加载...");
                mLoadingDialog.show();
            }*/
            @Override
            public void onParseSuccess(DataTable respInfo) {
                mDataTable = respInfo;
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                mPtrFrameLayout.refreshComplete();
                adapter.setDataTable(mDataTable);
                adapter.notifyDataSetChanged();
                tableFixHeaders.scrollTo(0, 0);
            }
        });
    }

    public class MyAdapter extends RadioTableAdapter {

        private final int width;
        private final int height;

        private DataTable dt;

        public MyAdapter(Context context) {
            super(context);

            Resources resources = context.getResources();

            width = resources.getDimensionPixelSize(R.dimen.table_width);
            height = resources.getDimensionPixelSize(R.dimen.table_height);
        }

        public void setDataTable(DataTable dataTable) {
            this.dt = dataTable;
        }

        @Override
        public int getRowCount() {
            return dt != null ? dt.Rows.size() : 0;
        }

        @Override
        public int getColumnCount() {
            if (formElement != null && formElement.Columns != null) {
                return formElement.Columns.size() - 1;
            } else {
                return dt != null ? dt.Columns.size() - 1 : 0;
            }
        }

        @Override
        public int getWidth(int column) {
            return width;
        }

        @Override
        public int getHeight(int row) {
            return height;
        }

        @Override
        public String getCellString(int row, int column) {
            if (row == -1) {
                return dt != null ? dt.Columns.get(column + 1) : "";
            } else {
                if (column == -2) {
                    return dt != null ? dt.Rows.get(row).get(0) : "";
                } else {
                    return dt != null ? dt.Rows.get(row).get(column + 1) : "";
                }
            }
        }

        @Override
        public int getLayoutResource(int row, int column) {
            final int layoutResource;
            switch (getItemViewType(row, column)) {
                case 0:
                    layoutResource = R.layout.item_table_header;
                    break;
                case 1:
                    layoutResource = R.layout.item_table;
                    break;
                default:
                    throw new RuntimeException("wtf?");
            }
            return layoutResource;
        }

        @Override
        public int getItemViewType(int row, int column) {
            if (row < 0) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }
    }
}
