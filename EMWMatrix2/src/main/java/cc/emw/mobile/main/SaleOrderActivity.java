package cc.emw.mobile.main;

import cc.emw.mobile.entity.ChartNavigations;
import cc.emw.mobile.main.adapter.SaleChartAdapter;
import cc.emw.mobile.main.adapter.SaleLeftViewAadpter;
import cc.emw.mobile.net.Page;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.entities.Navigations;
import cc.emw.mobile.project.view.SaleFormActivity;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.MyMarkView;
import cc.emw.mobile.view.SalesChartTextView;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.DataTable;
import cc.emw.mobile.entity.GridControl;
import cc.emw.mobile.entity.GridControl.ToolInfo;
import cc.emw.mobile.main.adapter.SampleTableAdapter;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.view.PopMenu;
import cc.emw.mobile.view.RightMenu;

import com.bigkoo.pickerview.OptionsPickerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.xlf.nrl.NsRefreshLayout;
import com.xlf.nrl.NsRefreshLayout.NsRefreshLayoutListener;
import com.zf.iosdialog.widget.AlertDialog;

@ContentView(R.layout.activity_sales_order)
public class SaleOrderActivity extends BaseActivity {

    private static final String TAG = "SaleOrderActivity";
    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; // 顶部条返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_right)
    private ImageButton mHeaderMoreBtn; // 顶部条更多按钮

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    //	@ViewInject(R.id.load_more_list_view_container)
//	private LoadMoreListViewContainer loadMoreListViewContainer; // 加载更多
    @ViewInject(R.id.frameLayout)
    private FrameLayout mFrameLayout;
    @ViewInject(R.id.table)
    private TableFixHeaders tableFixHeaders;
    private NsRefreshLayout refreshLayout;


    private DrawerLayout mDrawerLayout;//侧滑抽屉
    private DrawerLayout.LayoutParams mLayoutParams;//DrawerLayout的属性集合
    private ExpandableListView mLeftListView;

    private TextView mTvSelect;//柱状图顶部选择
    private TextView mIvAll;//重置数据按钮
    private Button mBtnSearch;//搜索按钮

    private PopupWindow mPw;
    private ListView mSaleChartListView;//抽屉右边顶部柱状图选择listview
    private SaleChartAdapter mSaleChartAdapter;//右侧视图顶部选择Adapter
    private SaleLeftViewAadpter mLeftViewAdapter;//左侧视图展示Adapter

    private DataTable mDataTable;
    private GridControl.ChartInfo mChartInfo;
    private Dialog mLoadingDialog; //加载框
    private MyAdapter adapter; // adapter
    private ArrayList<DataTable> mDataList; // 列表数据
    private GridControl gridControl;

    private int page = PAGE_FIRST; //第几页，(page-1)*PAGE_COUNT+1
    public static final int PAGE_FIRST = 1; //第1页
    private static final int PAGE_COUNT = 20; //页数

    //    private int navID = 11624; //10117  //销售订单
//    private int navID = 11204; //10117  //商品列表
    private int navID = 10117; //10117    //用户列表
    private String navName;

    private BarChart mBarChart;//竖直柱状图
    private HorizontalBarChart mHorizontalBarChart;//水平柱状图
    private LineChart mLineChart;//折线图
    private PieChart mPieChart;//扇形图
    private BarData mBarData;
    private LineData mLineData;
    private PieData mPieData;
    private FrameLayout mFlLeft;//左侧侧滑容器
    private FrameLayout mFlRight;//右侧侧滑容器
    private LinearLayout mLLleft;//左侧容器中的linearlayout

    public static final int DEFAULTVIEW = 0;//默认视图
    public static final int TESTVIEW1 = 1;//测试视图1
    public static final int TESTVIEW2 = 2;//测试视图2
    public static final int SEARCHVIEW = 3;//搜索视图
    public static final int CHARTCLICKVIEW = 4;//图表被点击后 表单数据进行更新
    private int viewType = DEFAULTVIEW;//表格加载数据默认是默认的类型

    private TextView mTvH;//测试水平柱状图
    private TextView mTvV;//测试竖直柱状图
    private TextView mTvLineChart;//测试折线统计图
    private TextView mTvPieChart;//测试扇形统计图

    private ImageView mIvLeft;//左边侧滑指示图标
    private ImageView mIvRight;//右边侧滑指示图标
    private PopupWindow mChartPopupWindow;//点击图标展示的弹窗

    private TextView mTvChartSelectDataType;//选择图表数据筛选类型
    private LinearLayout mLLChartSelelcDataContainer;//点击图表筛选容器
    private OptionsPickerView<String> mOptionsPickerView;//图表选择器
    private RadioGroup mRgSelectChartType;//图表LinearLayout

    private ArrayList<ChartNavigations> mChartNavigations = new ArrayList<>();//用于储存点击图表查询的数据实体ChartNavigations;
    private ArrayList<ChartNavigations> mLoadDataChartNavigations = new ArrayList<>();//用于图表数据请求集合
    private ChartNavigations mTempchartNavigations;//临时记录图表选择实体
    private int mChartIndex = -1;//记录图表中被点击的条目索引,默认为-1，不记录任何索引
    private int mExcuteCount = 0;//记录图标执行按钮的次数。0表示一次都没执行
    private int mChartType = 0;//图表展示类型  0为竖直柱状图1为水平柱状图2折线图3饼状图

    private FlowLayout mFlowLayout;
    private DataTable mGetListDataTable;

    /**
     * 设置表单查询页码
     *
     * @param page
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * 获取当前视图类型
     *
     * @return
     */
    public int getViewType() {
        return viewType;
    }

    /**
     * 设置当前视图类型
     *
     * @param viewType
     */
    public void setViewType(int viewType) {
        this.viewType = viewType;
    }


    /**
     * 获取到下拉刷新控件
     *
     * @return
     */
    public PtrFrameLayout getmPtrFrameLayout() {
        return mPtrFrameLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order);
        setSwipeBackEnable(false);//禁止右边侧滑功能启用
        navID = getIntent().getIntExtra("nav_id", 0);
        navName = getIntent().getStringExtra("nav_name");
        mHeaderBackBtn = (ImageButton) findViewById(R.id.cm_header_btn_left);
        mHeaderTitleTv = (TextView) findViewById(R.id.cm_header_tv_title);
        mHeaderMoreBtn = (ImageButton) findViewById(R.id.cm_header_btn_right);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        mIvAll = (TextView) findViewById(R.id.sales_iv_all);
        mTvSelect = (TextView) findViewById(R.id.sales_tv_drawerlayout_select);

        mBarChart = (BarChart) findViewById(R.id.barchart);
        mHorizontalBarChart = (HorizontalBarChart) findViewById(R.id.horizontalbarchart);
        mLineChart = (LineChart) findViewById(R.id.linechart);
        mPieChart = (PieChart) findViewById(R.id.piechart);
        mTvH = (TextView) findViewById(R.id.tv_horizontal_barchart);
        mTvV = (TextView) findViewById(R.id.tv_vertical_barchart);
        mTvLineChart = (TextView) findViewById(R.id.tv_line_barchart);
        mTvPieChart = (TextView) findViewById(R.id.tv_pie_barchart);

        mIvLeft = (ImageView) findViewById(R.id.iv_indicator_left);
        mIvRight = (ImageView) findViewById(R.id.iv_indicator_right);

        mLeftListView = (ExpandableListView) findViewById(R.id.left_exListview);
        mBtnSearch = (Button) findViewById(R.id.sale_btn_search);

        mFlLeft = (FrameLayout) findViewById(R.id.fl_sale_left);
        mFlRight = (FrameLayout) findViewById(R.id.fl_sale_right);
        mLLleft = (LinearLayout) findViewById(R.id.ll_sale_left);
        mLayoutParams = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT
                , DrawerLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams.gravity = Gravity.CENTER;
        initView();
        initMenu();
        tableFixHeaders = (TableFixHeaders) findViewById(R.id.table);
        mDataList = new ArrayList<DataTable>();
        adapter = new MyAdapter(this);
        tableFixHeaders.setAdapter(adapter);
        mLeftViewAdapter = new SaleLeftViewAadpter(this);//初始化左侧视图Adapter
        refreshLayout = (NsRefreshLayout) findViewById(R.id.nrl_test);
        mTvChartSelectDataType = (TextView) findViewById(R.id.tv_sales_click_select);
        mLLChartSelelcDataContainer = (LinearLayout) findViewById(R.id.ll_sales_container_click_view);
        mRgSelectChartType = (RadioGroup) findViewById(R.id.rg_sales_select_chart_type);
        mFlowLayout = (FlowLayout) findViewById(R.id.fl_sales_item);

        refreshLayout.setRefreshLayoutListener(new NsRefreshLayoutListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                page++;
                getList();
            }
        });
        refreshLayout.setRefreshLayoutController(new NsRefreshLayout.NsRefreshLayoutController() {
            @Override
            public boolean isPullRefreshEnable() {
                return false;
            }

            @Override
            public boolean isPullLoadEnable() {
                if (mGetListDataTable != null && mGetListDataTable.Rows != null) {
                    if (mGetListDataTable.Rows.size() < PAGE_COUNT) {
                        refreshLayout.mPullLoadText = "没有更多数据了!";
                        refreshLayout.hasNoMoreData = false;
                        refreshLayout.removeCircleProgress();
                    } else {
                        refreshLayout.mPullLoadText = null;
                        refreshLayout.hasNoMoreData = true;
                        refreshLayout.addCircleProgress();
                    }
                } else {
                    refreshLayout.mPullLoadText = null;
                    refreshLayout.hasNoMoreData = true;
                    refreshLayout.addCircleProgress();
                }
                return true;
            }
        });
        getGrid();
        //初始化弹窗
        initPopupWindow();
//        initChartPopupWindow();
        //自定义表格滑到底部的回调，用于对上拉加载更多的事件进行拦截
        refreshLayout.setUpScrollFlagListener(new NsRefreshLayout.UpScrollFlagListener() {
            @Override
            public boolean onUp() {
                if (tableFixHeaders.getActualScrollY() == DisplayUtil.dip2px(SaleOrderActivity.this, adapter.getRowCount() * 40)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        initOnClick();

    }

    /**
     * 初始化点击统计图的弹窗
     */
    private void initChartPopupWindow() {
        View view = View.inflate(this, R.layout.custom_markview, null);
        view.findViewById(R.id.tvContent).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SaleOrderActivity.this, "Charts Data", Toast.LENGTH_SHORT).show();
            }
        });
        mChartPopupWindow = new PopupWindow(view, -2, -2, true);
        mChartPopupWindow.setOutsideTouchable(false);
//        mChartPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mChartPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void initOnClick() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {

            }

            @Override
            public void onDrawerOpened(View view) {

            }

            @Override
            public void onDrawerClosed(View view) {
                //隐藏软键盘
                Log.d(TAG,"onDrawerClosed");
                HelpUtil.hideSoftInput(SaleOrderActivity.this,view);
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
        mBtnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件
                //获取搜索数据 点击查询
                setPage(PAGE_FIRST);
                setViewType(SEARCHVIEW);
                mPtrFrameLayout.autoRefresh();
//                closeLeftDrawerLayout();
            }
        });
        mTvSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
        mIvAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新获取数据列表
                mIvAll.setVisibility(View.INVISIBLE);
                //Toast.makeText(SaleOrderActivity.this, "im被点击了", Toast.LENGTH_SHORT).show();
                setViewType(DEFAULTVIEW);
                setPage(PAGE_FIRST);
                mPtrFrameLayout.autoRefresh();
                mFlowLayout.removeAllViews();
                resetChartData();
            }
        });
        mTvV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBarChart.setVisibility(View.VISIBLE);
                mHorizontalBarChart.setVisibility(View.GONE);
                mLineChart.setVisibility(View.GONE);
                mPieChart.setVisibility(View.GONE);
                loadBarChart();
            }
        });

        mTvH.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBarChart.setVisibility(View.GONE);
                mHorizontalBarChart.setVisibility(View.VISIBLE);
                mLineChart.setVisibility(View.GONE);
                mPieChart.setVisibility(View.GONE);
                loadVerticalBarChart();
            }
        });
        mTvLineChart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBarChart.setVisibility(View.GONE);
                mHorizontalBarChart.setVisibility(View.GONE);
                mLineChart.setVisibility(View.VISIBLE);
                mPieChart.setVisibility(View.GONE);
                loadLineChart();
            }
        });
        mTvPieChart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBarChart.setVisibility(View.GONE);
                mHorizontalBarChart.setVisibility(View.GONE);
                mLineChart.setVisibility(View.GONE);
                mPieChart.setVisibility(View.VISIBLE);
                loadPieChart();
            }
        });
        mIvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SaleOrderActivity.this, "点击左侧指示", Toast.LENGTH_SHORT).show();
                mDrawerLayout.openDrawer(mFlLeft);
            }
        });
        mIvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SaleOrderActivity.this, "点击右侧指示", Toast.LENGTH_SHORT).show();
                mDrawerLayout.openDrawer(mFlRight);
            }
        });
        mTvChartSelectDataType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionsPickerView.show();
            }
        });

        mRgSelectChartType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sales_barchart:
                        Toast.makeText(SaleOrderActivity.this, "柱状图", Toast.LENGTH_SHORT).show();
                        mChartType = 0;
                        break;
                    case R.id.rb_sales_barchart_horizontal:
                        Toast.makeText(SaleOrderActivity.this, "水平柱状图", Toast.LENGTH_SHORT).show();
                        mChartType = 1;
                        break;
                    case R.id.rb_sales_linechart:
                        Toast.makeText(SaleOrderActivity.this, "折线图", Toast.LENGTH_SHORT).show();
                        mChartType = 2;
                        break;
                    case R.id.rb_sales_piechart:
                        Toast.makeText(SaleOrderActivity.this, "饼状图", Toast.LENGTH_SHORT).show();
                        mChartType = 3;
                        break;
                    case R.id.rb_sales_excute:
                        //Toast.makeText(SaleOrderActivity.this, "执行", Toast.LENGTH_SHORT).show();
                        ChartNavigations chartNavigations = new ChartNavigations();
                        chartNavigations.ChartType = mChartType;
                        chartNavigations.CountType = mChartInfo.CountType;
                        chartNavigations.IsShowTitle = mChartInfo.IsShowTitle;
                        chartNavigations.LegendLocation = mChartInfo.LegendLocation;

                        //点击这个图形区域的X值
//                        if (mChartIndex != -1) {
//                            chartNavigations.Value = mDataTable.Rows.get(mChartIndex).get(DataTable.getColIndex(mDataTable.Columns, "X"));
//                        }
                        String chartDataType = mTvChartSelectDataType.getText().toString().trim();
                        if (TextUtils.isEmpty(chartDataType)) {
                            //如果图表选择项为空，则提示用户选择图标选项
                            Toast.makeText(SaleOrderActivity.this, "请选择图表选择项！", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            chartNavigations.Name = mColInfo.Name;
                            chartNavigations.ID = mColInfo.ID;
                            String T = mDataTable.Rows.get(mChartIndex).get(DataTable.getColIndex(mDataTable.Columns, "T"));
                            if (TextUtils.isEmpty(T)) {
                                //如果T标签中的数据为空，就展示X标签的数据
                                chartNavigations.Text = mColInfo.Name + "(" + mDataTable.Rows.get(mChartIndex)
                                        .get(DataTable.getColIndex(mDataTable.Columns, "X")) + ")";
                            } else {
                                chartNavigations.Text = mColInfo.Name + "(" + T + ")";
                            }
                        }
                        if (mExcuteCount == 0) {
                            //第一次添加2个实体，第二次开始 修改上一次的value值
                            mChartNavigations.add(mTempchartNavigations);//点击一次，添加一次
                        } else {
                            mChartNavigations.get(mChartNavigations.size() - 1).Value = mDataTable.Rows
                                    .get(mChartIndex).get(DataTable.getColIndex(mDataTable.Columns, "X"));
                        }
                        mChartNavigations.add(chartNavigations);
                        mExcuteCount++;
                        getChartsDatas(mChartInfo, mChartType);
                        mLLChartSelelcDataContainer.setVisibility(View.GONE);//隐藏图表选项列表
                        showChartFlowLayout();//流式布局展示
                        Log.d(TAG, "mChartNavigations:" + new Gson().toJson(mChartNavigations));
                        break;
                }
            }
        });
    }

    /**
     * 进行顶部文本链式添加  流式布局
     */
    public void showChartFlowLayout() {
        mFlowLayout.removeAllViews();
        for (int i = 0; i < mChartNavigations.size(); i++) {
            final SalesChartTextView childView = new SalesChartTextView(SaleOrderActivity.this);
            childView.setTag(R.id.tag_first, mChartNavigations.get(i));
            childView.setTag(R.id.tag_second, i);
            if (i == 0) {
                //如果是第一条，点击后就取消后面的所有布局
                childView.setCompoundDrawables(null, null, null, null);
                childView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mColInfo.Name = ((ChartNavigations) v.getTag(R.id.tag_first)).Name;
                        mFlowLayout.removeAllViews();
                        mChartNavigations.clear();
                        resetChartData();
                    }
                });
            } else if (i == mChartNavigations.size() - 1) {
                //若果是最后条就没有点击事件
                childView.setOnClickListener(null);
                childView.setTextColor(Color.GRAY);
            } else {
                childView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mColInfo.Name = ((ChartNavigations) v.getTag(R.id.tag_first)).Name;
                        int index = (int) v.getTag(R.id.tag_second);
                        for (int a = (index + 1); a < mChartNavigations.size(); a++) {
                            mChartNavigations.remove(a);
                        }
                        mChartNavigations.get(index).Value = null;
                        getChartsDatas(mChartInfo, ((ChartNavigations) v.getTag(R.id.tag_first)).ChartType);
                        showChartFlowLayout();
                    }
                });

            }
            childView.setText(mChartNavigations.get(i).Text);
            mFlowLayout.addView(childView);
        }
    }

    /**
     * 关闭侧滑左侧菜单
     */
    public void closeLeftDrawerLayout() {
        mDrawerLayout.closeDrawer(mFlLeft);
    }

    /**
     * 初始竖直柱状图
     */
    private void loadBarChart() {
//        mBarData = getBarData(7, 100);//模拟的假数据
        mBarData = getBarData();
        showBarChart(mBarChart, mBarData);
    }

    private void loadLineChart() {
        mLineData = getLineData();
        showLineChart(mLineChart, mLineData); 
    }

    /**
     * 初始化水平柱状图
     */
    private void loadVerticalBarChart() {
        mBarData = getBarData();
        showBarChart(mHorizontalBarChart, mBarData);
    }

    /**
     * 初始化扇形图
     */
    private void loadPieChart() {
        mPieData = getPieData();
        showPieChart(mPieChart, mPieData);
    }

    /**
     * 初始化图表选择项
     */
    private GridControl.ColInfo mColInfo;

    private void initPicker() {
        final ArrayList<GridControl.ColInfo> columns = gridControl.Columns;
        final ArrayList<String> sorts = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            sorts.add(columns.get(i).Name);
        }
        mOptionsPickerView = new OptionsPickerView<>(this);
        mOptionsPickerView.setPicker(sorts);
        mOptionsPickerView.setTitle(getString(R.string.chart_select));
        mOptionsPickerView.setCancelable(true);
        mOptionsPickerView.setCyclic(false);// 无限循环
        mOptionsPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //选择后展示 在UI上
                mTvChartSelectDataType.setText(sorts.get(options1));
                Log.d(TAG, "ID:" + columns.get(options1).ID);
                //记录实体
                mColInfo = columns.get(options1);
            }
        });
    }

    /**
     * 初始化popupWindow
     */
    private void initPopupWindow() {
        mSaleChartListView = new ListView(this);
        mSaleChartAdapter = new SaleChartAdapter(this);
        mPw = new PopupWindow(mSaleChartListView, -2, 250);
        mPw.setOutsideTouchable(true);
        mPw.setFocusable(true);
//        mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPw.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        mSaleChartListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Toast.makeText(SaleOrderActivity.this, gridControl.Charts.get(position).Name, Toast.LENGTH_SHORT).show();
                //发起请求获取统计图数据
                mLLChartSelelcDataContainer.setVisibility(View.GONE);
                mChartInfo = gridControl.Charts.get(position);
                mTvSelect.setText(mChartInfo.Name);
                mColInfo = null;
                resetChartData();
                showPopupWindow();
            }
        });
    }

    /**
     * 重置表格数据
     */
    public void resetChartData() {
        mChartNavigations.clear();//每次选择的时候清空以前的图表选择请求数据集合
        mLoadDataChartNavigations.clear();
        mExcuteCount = 0;
        mChartType = mChartInfo.ChartType;
        getChartsDatas(mChartInfo, mChartType);
    }

    /**
     * 展示统计图下拉弹窗
     */
    private void showPopupWindow() {
        if (mPw != null && mPw.isShowing()) {
            mPw.dismiss();
        } else {
            mTvSelect.measure(0, 0);
            mPw.setWidth(mTvSelect.getWidth());
            mPw.showAsDropDown(mTvSelect);
        }
    }

    private void initView() {
        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderMoreBtn.setVisibility(View.GONE);
        mHeaderTitleTv.setText(navName);
        mHeaderBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpUtil.hideSoftInput(SaleOrderActivity.this, v);
//                SystemClock.sleep(200);
                onBackPressed();
            }
        });
        mHeaderMoreBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.showAsDropDown(mHeaderMoreBtn);
            }
        });

        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.load_more_list_view_ptr_frame);
//		loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more_list_view_container);
        tableFixHeaders = (TableFixHeaders) findViewById(R.id.table);

        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.disableWhenHorizontalMove(true);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // here check list view, not content.
//                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mFrameLayout, header);
                return tableFixHeaders.getActualScrollY() == 0;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = PAGE_FIRST;
                getList();
            }
        });
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

        /*loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setAutoLoadMore(false);*/

        mDataList = new ArrayList<DataTable>();
        adapter = new MyAdapter(this);
        tableFixHeaders.setAdapter(adapter);
        /*loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
            	page++;
            	getList();
            }
        });*/

    }

    private RightMenu mMenu;

    private void initMenu() {
        mMenu = new RightMenu(this);
        mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
            @Override
            public void selected(View view, PopMenu.Item item, int position) {
                ToolInfo tool = gridControl.Tools.get(position);
                if ("Create".equalsIgnoreCase(tool.ToolType)) {
                    Intent intent = new Intent(SaleOrderActivity.this, SaleFormActivity.class);
                    intent.putExtra("page_id", String.valueOf(tool.PageID));
                    startActivity(intent);
                } else if ("Edit".equalsIgnoreCase(tool.ToolType)) {
                    ArrayList<String> selectIDs = adapter.getSelectIDs();
                    if (selectIDs != null && selectIDs.size() > 0) {
                        for (int i = 0; i < adapter.getRowCount(); i++) {
                            String rowID = adapter.getCellString(i, -2);
                            if (selectIDs.contains(rowID)) {
                                Intent intent = new Intent(SaleOrderActivity.this, SaleFormActivity.class);
                                intent.putExtra("page_id", String.valueOf(tool.PageID));
                                intent.putExtra("row_id", rowID);
                                startActivity(intent);
                                break;
                            }
                        }
                    } else {
                        ToastUtil.showToast(SaleOrderActivity.this, R.string.contactselect_empty);
                    }
                } else if ("Delete".equalsIgnoreCase(tool.ToolType)) {
                    final String ids = TextUtils.join(",", adapter.getSelectIDs());
                    if (!TextUtils.isEmpty(ids)) {
                        new AlertDialog(SaleOrderActivity.this).builder()
                                .setMsg("是否确认删除？")
                                .setPositiveButton(getString(R.string.ok), new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        delete(ids);
                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel), new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                    } else {
                        ToastUtil.showToast(SaleOrderActivity.this, R.string.contactselect_empty);
                    }
                }
            }
        });
    }

    public class MyAdapter extends SampleTableAdapter {

        private final int width;
        private final int height;

        //		private final String[] TITLES = {"销售订单号", "订单描述", "订单日期", "客户编号", "送达日期从", "业务员", "状态"};
        private ArrayList<DataTable> dtList;

        public MyAdapter(Context context) {
            super(context);

            Resources resources = context.getResources();

            width = resources.getDimensionPixelSize(R.dimen.table_width);
            height = resources.getDimensionPixelSize(R.dimen.table_height);
        }

        public void setDataList(ArrayList<DataTable> dtList) {
            this.dtList = dtList;
        }

        @Override
        public int getRowCount() {
            int count = 0;
            if (dtList != null) {
                for (DataTable dt : dtList) {
                    count += dt.Rows.size();
                }
            }
            System.out.println("rowcount:" + count);
            return count;
//			return dt != null ? dt.Rows.size() : 0;
        }

        @Override
        public int getColumnCount() {
            int count = 0;
            if (dtList != null && dtList.size() > 0) {
                count = dtList.get(0).Columns.size() - 2;
            }
            System.out.println("colcount:" + count);
            return count;
//			return dt != null ? dt.Columns.size() - 2 : 0;
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
//            System.out.println("row:" + row + "；col:" + column);
            if (row == -1) {
//				return TITLES[column + 1];
                if (gridControl != null && gridControl.Columns != null && gridControl.Columns.size() > 0 && gridControl.Columns.get(column + 1) != null) {
                    return gridControl.Columns.get(column + 1).Name;
                } else {
                    return "";
                }
            } else {
                return dtList != null ? dtList.get(row / PAGE_COUNT).Rows.get(row - row / PAGE_COUNT * PAGE_COUNT).get(column + 2) : "";
//				return dt != null ? dt.Rows.get(row).get(column + 2) : "";
            }
        }

        @Override
        public int getLayoutResource(int row, int column) {
            final int layoutResource;
            switch (getItemViewType(row, column)) {
                case 0:
                    layoutResource = R.layout.item_table1_header;
                    break;
                case 1:
                    layoutResource = R.layout.item_table1;
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

        @Override
        public void onClickFirstRowCol(boolean checked) {
            if (checked) {
                for (int i = 0; i < getRowCount(); i++) {
                    String rowID = getCellString(i, -2);
                    selectIDs.add(rowID);
                }
                notifyDataSetChanged();
            } else {
                selectIDs.clear();
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 获取各区域数据
     */
    private void getGrid() {
        RequestParam params = new RequestParam("http://10.0.10.80:8000/mpage/" + navID);
        x.http().post(params, new RequestCallback<GridControl>(GridControl.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "getGrid:onError:.." + ex.toString());
                mFlLeft.setLayoutParams(mLayoutParams);
                mIvLeft.setVisibility(View.GONE);
                mFlRight.setLayoutParams(mLayoutParams);
                mIvRight.setVisibility(View.GONE);
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(SaleOrderActivity.this, "获取各区域数据失败！");
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在加载...");
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                mPtrFrameLayout.autoRefresh(false);
                gridControl = new Gson().fromJson(result, GridControl.class);
                Log.d(TAG, "gridControl:.." + new Gson().toJson(gridControl));
                if (gridControl != null) {
                    //初始化表格数据
                    if (gridControl.Columns != null && gridControl.Columns.size() > 0) {
                        initPicker();//初始化图表选择器
                    }
                    //初始化快捷操作
                    if (gridControl.Tools != null && gridControl.Tools.size() > 0) {
                        for (int i = 0, size = gridControl.Tools.size(); i < size; i++) {
                            ToolInfo tool = gridControl.Tools.get(i);
                            mMenu.addItem(tool.Name, i);
                            if (tool.Key == 1) {
                                adapter.setPageID(tool.PageID);
                            }
                        }
                        mHeaderMoreBtn.setVisibility(View.VISIBLE);
                    }
                    //初始化视图
                    if (gridControl.Views != null && gridControl.Views.size() > 0) {

                    }
                    //初始化搜索
                    if (gridControl.Searchers != null && gridControl.Searchers.size() > 0) {
                        Log.d(TAG, "gridControl.Searchers:" + new Gson().toJson(gridControl.Searchers));
//                        设置数据，初始化视图与搜索
                        GridControl.ViewInfo defaultView = new GridControl.ViewInfo();
                        defaultView.ID = "0";
                        defaultView.Name = "默认视图";
                        gridControl.Views.add(0, defaultView);
                        //gridControl.Searchers.get(0).Type="DropDownList";
                        mLeftViewAdapter.setArrayDataList(gridControl.Views, gridControl.Searchers);
                        mLeftListView.setAdapter(mLeftViewAdapter);
//                        TaskUtils.setExpandableListViewHeightBasedOnChildren(mLeftListView);
                        mIvLeft.setVisibility(View.VISIBLE);//左侧抽屉指示图表显示
                        // 默认展开所有条目
                        int groupCount = mLeftViewAdapter.getGroupCount();
                        for (int i = 0; i < groupCount; i++) {
                            mLeftListView.expandGroup(i);
                        }
                        mLeftListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                return true;
                            }
                        });
                    } else {
                        //数据为空 将左边侧滑菜单永久隐藏起来
                        mFlLeft.setLayoutParams(mLayoutParams);
                        mIvLeft.setVisibility(View.GONE);
                    }
                    //初始化图表
                    if (gridControl.Charts != null && gridControl.Charts.size() > 0) {
                        Log.d(TAG, "图表数据:" + new Gson().toJson(gridControl.Charts));
                        //初始化柱状图
                        if (gridControl.Charts != null) {
                            mChartInfo = gridControl.Charts.get(0);
                            mTvSelect.setText(mChartInfo.Name);
                            mChartType = mChartInfo.ChartType;
                            getChartsDatas(mChartInfo, mChartType);
                        }

                        mSaleChartAdapter.setData(gridControl.Charts);
                        mSaleChartListView.setAdapter(mSaleChartAdapter);
                        mIvRight.setVisibility(View.VISIBLE);//右侧指示栏图标显示
                    } else {
                        //关闭侧滑菜单
                        //mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        //数据为空 将右侧边侧滑菜单永久隐藏起来
                        mFlRight.setLayoutParams(mLayoutParams);
                        mIvRight.setVisibility(View.GONE);
                    }

                }else{
                    mFlLeft.setLayoutParams(mLayoutParams);
                    mIvLeft.setVisibility(View.GONE);
                    mFlRight.setLayoutParams(mLayoutParams);
                    mIvRight.setVisibility(View.GONE);
                }
            }
            /*@Override
            public void onParseSuccess(GridControl respInfo) {
				if (mLoadingDialog != null) mLoadingDialog.dismiss();
				titleList = respInfo.Columns;
				mPtrFrameLayout.autoRefresh(false);
			}*/
        });
    }

    public void getList() {
        RequestParam params = new RequestParam("http://10.0.10.80:8000/Page/" + navID + "/loaddata");
        String body = "";
        if (viewType == DEFAULTVIEW) {
            body = "{\"PageSize\":" + PAGE_COUNT + ",\"PageIndex\":" + page + ",\"ChartNavigations\":[]}";
        } else if (viewType == TESTVIEW1) {
            //通过选择视图展示方式刷新表单页面
            body = "{\"PageSize\":" + PAGE_COUNT + ",\"PageIndex\":" + page + ",\"SelectedView\":{\"ID\":\"view0\",\"Name\"" +
                    ":\"测试视图1\"},\"SearcherValues\":{}}";
        } else if (viewType == TESTVIEW2) {
            body = "{\"PageSize\":" + PAGE_COUNT + ",\"PageIndex\":" + page + ",\"SelectedView\":{\"ID\":\"view1\",\"Name\"" +
                    ":\"测试视图2\"},\"SearcherValues\":{}}";
        } else if (viewType == SEARCHVIEW) {
            //点击搜索按钮搜索表单数据
            body = "{\"PageSize\":" + PAGE_COUNT + ",\"PageIndex\":" + page + "," + mLeftViewAdapter.getSearchData() + "}";
            Log.d(TAG, "请求body数据：" + body);
        } else if (viewType == CHARTCLICKVIEW) {
            body = "{\"PageSize\":" + PAGE_COUNT + ",\"PageIndex\":" + page + ",\"ChartNavigations\":" +
                    new Gson().toJson(mLoadDataChartNavigations) + "}";
//            Log.d(TAG, "getList请求body数据：" + body);
        }
        params.setStringBody(body);
        x.http().post(params, new RequestCallback<DataTable>(DataTable.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(SaleOrderActivity.this, "获取列表数据失败！");
                mPtrFrameLayout.refreshComplete();
                mGetListDataTable = null;
                refreshLayout.hasNoMoreData = false;
                refreshLayout.finishPullLoad();
            }

            /*@Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog("正在加载...");
                mLoadingDialog.show();
            }*/
            @Override
            public void onParseSuccess(DataTable respInfo) {
                mGetListDataTable = respInfo;
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                mPtrFrameLayout.refreshComplete();
                refreshLayout.finishPullLoad();
                if (page == 1) {
                    mDataList.clear();
                    adapter.getSelectIDs().clear();
                    adapter.setFirstChecked(false);
                }
                mDataList.add(respInfo);
                adapter.setDataList(mDataList);
                adapter.notifyDataSetChanged();
                if (page == 1) {
                    tableFixHeaders.scrollTo(0, 0);
                }
                closeLeftDrawerLayout();
            }
        });
    }

    /**
     * 删除
     *
     * @param ids id集，用“,”分隔
     */
    private void delete(String ids) {
        Page.pageRequest(navID, "delete?rid=" + ids, null, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(SaleOrderActivity.this, "删除失败！");
            }

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips5);
                mLoadingDialog.show();
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                try {
                    JSONObject jsonObj = new JSONObject(result); // {"Code":1,"Message":""}
                    if (jsonObj.has("Code")) {
                        int code = jsonObj.getInt("Code");
                        if (code == 1) {
                            ToastUtil.showToast(SaleOrderActivity.this, "删除成功！");
                            adapter.getSelectIDs().clear();
                            mPtrFrameLayout.autoRefresh(false);
                        } else {
                            ToastUtil.showToast(SaleOrderActivity.this, "删除失败！");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToast(SaleOrderActivity.this, "删除失败！");
                }
            }
        });
    }


    /**
     * 用于模拟获取统计图展示数据
     *
     * @param count
     * @param range
     * @return
     */
    private BarData getBarData(int count, float range) {
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xValues.add("第" + (i + 1) + "季度");
        }

        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

        for (int i = 0; i < count; i++) {
            float value = (float) (Math.random() * range/*100以内的随机数*/) + 3;
            yValues.add(new BarEntry(value, i));
        }

        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, "状态统计");
        barDataSet.setColor(Color.RED);
        barDataSet.setBarSpacePercent(20f);//设置柱状图的宽度  此值越小 柱状图的宽度越大

        ArrayList<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
        barDataSets.add(barDataSet); // add the datasets
        BarData barData = new BarData(xValues, barDataSets);

        return barData;
    }

    /**
     * 获取统计图的原始数据
     */
    private void getChartsDatas(final GridControl.ChartInfo data, final int chartType) {
        RequestParam params = new RequestParam("http://10.0.10.80:8000/Page/" + navID + "/loadchartdata");
        String body = "{\"PageSize\":" + PAGE_COUNT + ",\"PageIndex\":" + page + ",\"ChartNavigations\":" + new Gson().toJson(mChartNavigations) + ",\"SelectedChart\":" + new Gson().toJson(data) + "}";
//        Log.d(TAG, "getChartsDatas；" + body);
        params.setStringBody(body);
        x.http().post(params, new RequestCallback<DataTable>(DataTable.class) {


            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(SaleOrderActivity.this, "getChartsDatas请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onParseSuccess(DataTable respInfo) {
//                Log.d(TAG, "getChartsDatas:onParseSuccess" + new Gson().toJson(respInfo));
                //mDataTable数据中保存了XYT值
                mDataTable = respInfo;
                showChartType(chartType);
//                loadBarChart();
//                loadLineChart();
//                loadPieChart();
//                loadVerticalBarChart();
            }
        });
    }

    public void showChartType(int chartType) {
        switch (chartType) {
            case 0:
                //展示柱状统计图
                mBarChart.setVisibility(View.VISIBLE);
                mHorizontalBarChart.setVisibility(View.GONE);
                mLineChart.setVisibility(View.GONE);
                mPieChart.setVisibility(View.GONE);
                loadBarChart();
                break;
            case 1:
                //展示水平柱状图
                mBarChart.setVisibility(View.GONE);
                mHorizontalBarChart.setVisibility(View.VISIBLE);
                mLineChart.setVisibility(View.GONE);
                mPieChart.setVisibility(View.GONE);
                loadVerticalBarChart();
                break;
            case 2:
                //展示折线图
                mBarChart.setVisibility(View.GONE);
                mHorizontalBarChart.setVisibility(View.GONE);
                mLineChart.setVisibility(View.VISIBLE);
                mPieChart.setVisibility(View.GONE);
                loadLineChart();
                break;
            case 3:
                //展示饼状图
                mBarChart.setVisibility(View.GONE);
                mHorizontalBarChart.setVisibility(View.GONE);
                mLineChart.setVisibility(View.GONE);
                mPieChart.setVisibility(View.VISIBLE);
                loadPieChart();
                break;
        }
    }

    /**
     * 获取统计图展示数据(将图表原始数据转换）
     *
     * @return
     */
    private BarData getBarData() {

        ArrayList<String> xValues = new ArrayList<String>();//x轴
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();//y轴数据
        //拿到数据 进行柱状图设计
        if (mDataTable != null) {
            ArrayList<ArrayList<String>> rows = mDataTable.Rows;
            if (rows != null) {
                for (int i = 0; i < rows.size(); i++) {
                    //添加x轴数据
                    ArrayList<String> strings = rows.get(i);
                    String T = strings.get(DataTable.getColIndex(mDataTable.Columns, "T"));
                    if (TextUtils.isEmpty(T)) {
                        //如果T标签中的数据为空，就展示X标签的数据
                        xValues.add(strings.get(DataTable.getColIndex(mDataTable.Columns, "X")));
                    } else {
                        xValues.add(T);
                    }
                    String Y = strings.get(DataTable.getColIndex(mDataTable.Columns, "Y"));
                    yValues.add(new BarEntry(Integer.valueOf(Y), i));
                }
            }
        }
        BarDataSet barDataSet = new BarDataSet(yValues, mColInfo == null ? mChartInfo.Name : mColInfo.Name);
        barDataSet.setColor(Color.RED);
        barDataSet.setBarSpacePercent(5f);//设置柱状图的宽度  此值越小 柱状图的宽度越大

        ArrayList<IBarDataSet> barDataSets = new ArrayList<IBarDataSet>();
        barDataSets.add(barDataSet); // add the datasets
        BarData barData = new BarData(xValues, barDataSets);
        return barData;
    }

    /**
     * 展示柱状图
     *
     * @param barChart 柱状图
     * @param barData  柱状图数据
     */
    private void showBarChart(final BarChart barChart, final BarData barData) {
        barChart.setDrawBorders(false);  ////是否在折线图上添加边框
        barChart.setDescription("");// 数据描述,柱状图底部

        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("You need to provide data for the chart.");

        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

        barChart.setTouchEnabled(true); // 设置是否可以触摸

        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(true);// 是否可以缩放

        barChart.setPinchZoom(false);//

//      barChart.setBackgroundColor();// 设置背景

//        barChart.setDrawBarShadow(false);//设置柱状图背景颜色

        barChart.setData(barData); // 设置数据

        Legend mLegend = barChart.getLegend(); // 设置比例图标示

//        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setForm(Legend.LegendForm.SQUARE);//统计状态前的图标为方形
        mLegend.setFormSize(15f);// 字体
        mLegend.setTextColor(Color.BLACK);// 颜色
//      mLegend.setTextSize(15f); //设置柱状图指示的字体大小
        mLegend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
//      X轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.animateY(2500);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinValue(0.0f);
        //控制右边Y轴数据展示
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setAxisMinValue(0.0f);
//        yAxis.setEnabled(false);

        final MyMarkView myMarkView = new MyMarkView(this, R.layout.custom_markview);
        barChart.setMarkerView(myMarkView);
        barChart.setDrawMarkerViews(false);//markView是否显示 false为不显示
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//                Toast.makeText(SaleOrderActivity.this, e.getVal() + ":" + dataSetIndex + ":" + e.getXIndex(), Toast.LENGTH_SHORT).show();
//                float[] markerPosition = barChart.getMarkerPosition(e, h);
//                Point p = new Point();
//                getWindowManager().getDefaultDisplay().getSize(p);
//                Log.d(TAG, Arrays.toString(markerPosition));
//                Log.d(TAG, p.x + ":" + p.y);
//                mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
//                if(mChartPopupWindow!=null&&mChartPopupWindow.isShowing()){
//                    mChartPopupWindow.dismiss();
//                }else{
//                    mChartPopupWindow.showAtLocation(barChart,Gravity.NO_GRAVITY,((int)markerPosition[0]+88),((int)markerPosition[1]+200));
//                }
                //在UI 上展现选择器
                mIvAll.setVisibility(View.VISIBLE);
                mLLChartSelelcDataContainer.setVisibility(View.VISIBLE);
                mChartIndex = e.getXIndex();//记录图表中点击的第几条？
                //对图表选择的实体chartnavigation进行赋值,mChartInfo是右侧抽屉中最顶上的选择下拉框中的数据
                mLoadDataChartNavigations.clear();
                ChartNavigations chartNavigations1 = new ChartNavigations();
                chartNavigations1.ChartType = mChartType;
                chartNavigations1.CountType = mChartInfo.CountType;
                chartNavigations1.IsShowTitle = mChartInfo.IsShowTitle;
                chartNavigations1.LegendLocation = mChartInfo.LegendLocation;
                chartNavigations1.Name = mChartInfo.Name;
                chartNavigations1.ID = mChartInfo.ID;
                chartNavigations1.Text = mChartInfo.Name;
                //点击这个图形区域的X值
                if (mChartIndex != -1) {
                    chartNavigations1.Value = mDataTable.Rows.get(mChartIndex).get(DataTable.getColIndex(mDataTable.Columns, "X"));
                }

                mTempchartNavigations = chartNavigations1;
                if (mExcuteCount == 0) {
                    //第一层的chartNavigations数据
                    mLoadDataChartNavigations.add(chartNavigations1);
                } else {
                    //不是第一次点击
                    mChartNavigations.get(mChartNavigations.size() - 1).Value = mDataTable.Rows
                            .get(mChartIndex).get(DataTable.getColIndex(mDataTable.Columns, "X"));
                    for (int i = 0; i < mChartNavigations.size(); i++) {
                        mLoadDataChartNavigations.add(mChartNavigations.get(i));
                    }
                }
                //请求表单数据
                setViewType(CHARTCLICKVIEW);
                mPtrFrameLayout.autoRefresh();
                Log.d(TAG, "mLoadDataChartNavigations:" + new Gson().toJson(mLoadDataChartNavigations));
            }

            @Override
            public void onNothingSelected() {
                //Toast.makeText(SaleOrderActivity.this, "onNothingSelected", Toast.LENGTH_SHORT).show();
                mLLChartSelelcDataContainer.setVisibility(View.GONE);
            }
        });
        barChart.invalidate();

    }

    /**
     * 获取折线图数据
     *
     * @return
     */
    private LineData getLineData() {
        ArrayList<String> xValues = new ArrayList<String>();//x轴
        ArrayList<Entry> yValues = new ArrayList<Entry>();//y轴数据
        //拿到数据 进行柱状图设计
        if (mDataTable != null) {
            ArrayList<ArrayList<String>> rows = mDataTable.Rows;
            if (rows != null) {
                for (int i = 0; i < rows.size(); i++) {
                    //添加x轴数据
                    ArrayList<String> strings = rows.get(i);
                    String T = strings.get(DataTable.getColIndex(mDataTable.Columns, "T"));
                    if (TextUtils.isEmpty(T)) {
                        //如果T标签中的数据为空，就展示X标签的数据
                        xValues.add(strings.get(DataTable.getColIndex(mDataTable.Columns, "X")));
                    } else {
                        xValues.add(T);
                    }
                    String Y = strings.get(DataTable.getColIndex(mDataTable.Columns, "Y"));
                    yValues.add(new BarEntry(Integer.valueOf(Y), i));
                }
            }
        }
        LineDataSet lineDataSet = new LineDataSet(yValues, mColInfo == null ? mChartInfo.Name : mColInfo.Name);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setLineWidth(1f);//线宽
        lineDataSet.setCircleRadius(5f);//现实圆形大小
        lineDataSet.setCircleColor(Color.BLUE);//圆形颜色
        lineDataSet.setHighLightColor(Color.WHITE);//高度线的颜色
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(lineDataSet);
        LineData lineData = new LineData(xValues, lineDataSets);
        return lineData;
    }


    private void showLineChart(LineChart lineChart, LineData lineData) {
        lineChart.setDrawBorders(false);//是否添加边框
        lineChart.setDescription("");//数据描述
        lineChart.setNoDataTextDescription("\"You need to provide data for the chart.\"");//没数据显示
//        lineChart.setDrawGridBackground(true);//是否显示表格颜色
//        lineChart.setBackgroundColor(Color.YELLOW);//背景颜色
        lineChart.setData(lineData);//设置数据
        Legend legend = lineChart.getLegend();//设置比例图片标示，就是那一组Y的value
        legend.setForm(Legend.LegendForm.SQUARE);//样式
        legend.setFormSize(15f);
        legend.setTextColor(Color.BLACK);// 颜色
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);
        lineChart.animateX(2500);//X轴的动画
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinValue(0.0f);
        //X轴设置底部
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //控制右边Y轴数据展示
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setAxisMinValue(0.0f);
        MyMarkView myMarkView = new MyMarkView(this, R.layout.custom_markview);
        lineChart.setMarkerView(myMarkView);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.invalidate();
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                mIvAll.setVisibility(View.VISIBLE);
                mLLChartSelelcDataContainer.setVisibility(View.VISIBLE);
                mChartIndex = e.getXIndex();//记录图表中点击的第几条？
                //对图表选择的实体chartnavigation进行赋值,mChartInfo是右侧抽屉中最顶上的选择下拉框中的数据
                mLoadDataChartNavigations.clear();
                ChartNavigations chartNavigations1 = new ChartNavigations();
                chartNavigations1.ChartType = mChartType;
                chartNavigations1.CountType = mChartInfo.CountType;
                chartNavigations1.IsShowTitle = mChartInfo.IsShowTitle;
                chartNavigations1.LegendLocation = mChartInfo.LegendLocation;
                chartNavigations1.Name = mChartInfo.Name;
                chartNavigations1.ID = mChartInfo.ID;
                chartNavigations1.Text = mChartInfo.Name;
                //点击这个图形区域的X值
                if (mChartIndex != -1) {
                    chartNavigations1.Value = mDataTable.Rows.get(mChartIndex).get(DataTable.getColIndex(mDataTable.Columns, "X"));
                }

                mTempchartNavigations = chartNavigations1;
                if (mExcuteCount == 0) {
                    //第一层的chartNavigations数据
                    mLoadDataChartNavigations.add(chartNavigations1);
                } else {
                    //不是第一次点击
                    mChartNavigations.get(mChartNavigations.size() - 1).Value = mDataTable.Rows
                            .get(mChartIndex).get(DataTable.getColIndex(mDataTable.Columns, "X"));
                    for (int i = 0; i < mChartNavigations.size(); i++) {
                        mLoadDataChartNavigations.add(mChartNavigations.get(i));
                    }
                }
                //请求表单数据
                setViewType(CHARTCLICKVIEW);
                mPtrFrameLayout.autoRefresh();
                Log.d(TAG, "mLoadDataChartNavigations:" + new Gson().toJson(mLoadDataChartNavigations));
            }

            @Override
            public void onNothingSelected() {
                mLLChartSelelcDataContainer.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 获取扇形统计图数据
     *
     * @return
     */
    private PieData getPieData() {
        ArrayList<String> xValues = new ArrayList<String>();//x轴
        ArrayList<Entry> yValues = new ArrayList<Entry>();//y轴数据
        ArrayList<Integer> colors = new ArrayList<>();
//        Random random = new Random();
        //拿到数据 进行柱状图设计
        if (mDataTable != null) {
            ArrayList<ArrayList<String>> rows = mDataTable.Rows;
            if (rows != null) {
                for (int i = 0; i < rows.size(); i++) {
                    //添加x轴数据
                    ArrayList<String> strings = rows.get(i);
                    String T = strings.get(DataTable.getColIndex(mDataTable.Columns, "T"));
                    if (TextUtils.isEmpty(T)) {
                        //如果T标签中的数据为空，就展示X标签的数据
                        xValues.add(strings.get(DataTable.getColIndex(mDataTable.Columns, "X")));
                    } else {
                        xValues.add(T);
                    }
                    String Y = strings.get(DataTable.getColIndex(mDataTable.Columns, "Y"));
                    yValues.add(new Entry(Integer.valueOf(Y), i));
//                    colors.add(Color.rgb(random.nextInt(254),random.nextInt(254),(i*10+1)));
                }
            }
        }
        PieDataSet pieDataSet = new PieDataSet(yValues, mColInfo == null ? mChartInfo.Name : mColInfo.Name);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        pieDataSet.setValueLinePart1Length(0.3f);
//        pieDataSet.setValueLinePart1OffsetPercentage(0.8f);
//        pieDataSet.setValueLinePart1Length(0.2f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // 饼图颜色
        int[] joyfulColors = ColorTemplate.MATERIAL_COLORS;
        int length = joyfulColors.length;

        if (xValues.size() > length) {
            //如果标签的数量大于默认的颜色数组长度，就重复循环添加数组中的颜色值
            int count = xValues.size() / length;
            int remainder = xValues.size() % length;
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < length; j++) {
                    colors.add(joyfulColors[j]);
                }
            }
            for (int j = 0; j < remainder; j++) {
                colors.add(joyfulColors[j]);
            }
            pieDataSet.setColors(colors);
        } else {
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        }

//        colors.add(Color.rgb(205, 205, 205));
//        colors.add(Color.rgb(114, 188, 223));
//        colors.add(Color.rgb(255, 123, 124));
//        colors.add(Color.rgb(57, 135, 200));
//        colors.add(Color.rgb(30, 20, 200));
//        colors.add(Color.rgb(80, 60, 150));
//        pieDataSet.setColors(colors);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度
        PieData pieData = new PieData(xValues, pieDataSet);
        return pieData;
    }

    /**
     * 展示扇形图
     *
     * @param pieChart
     * @param pieData
     */
    private void showPieChart(PieChart pieChart, PieData pieData) {
        pieChart.setDescription("");
        pieChart.setHoleRadius(60f);  //半径
        pieChart.setTransparentCircleRadius(54f); // 半透明圈
        pieChart.setHoleRadius(50);
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(90); // 初始旋转角度

        pieChart.setRotationEnabled(true); // 可以手动旋转
        pieChart.setUsePercentValues(true);  //显示成百分比
        pieChart.setCenterText(mColInfo == null ? mChartInfo.Name : mColInfo.Name);  //饼状图中间的文字
        pieChart.setData(pieData);
        MyMarkView myMarkView = new MyMarkView(this, R.layout.custom_markview);
        pieChart.setMarkerView(myMarkView);
        Legend mLegend = pieChart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);  //最左边显示
        mLegend.setForm(Legend.LegendForm.SQUARE);  //设置比例图的形状，默认是方形 SQUARE
        mLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        mLegend.setWordWrapEnabled(true);//标签可以换行
        pieChart.animateXY(1000, 1000);  //设置动画
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//               Log.d(TAG, "pieChart:onValueSelected:" + e.getXIndex());
                mIvAll.setVisibility(View.VISIBLE);
                mLLChartSelelcDataContainer.setVisibility(View.VISIBLE);
                mChartIndex = e.getXIndex();//记录图表中点击的第几条？
                //对图表选择的实体chartnavigation进行赋值,mChartInfo是右侧抽屉中最顶上的选择下拉框中的数据
                mLoadDataChartNavigations.clear();
                ChartNavigations chartNavigations1 = new ChartNavigations();
                chartNavigations1.ChartType = mChartType;
                chartNavigations1.CountType = mChartInfo.CountType;
                chartNavigations1.IsShowTitle = mChartInfo.IsShowTitle;
                chartNavigations1.LegendLocation = mChartInfo.LegendLocation;
                chartNavigations1.Name = mChartInfo.Name;
                chartNavigations1.ID = mChartInfo.ID;
                chartNavigations1.Text = mChartInfo.Name;
                //点击这个图形区域的X值
                if (mChartIndex != -1) {
                    chartNavigations1.Value = mDataTable.Rows.get(mChartIndex).get(DataTable.getColIndex(mDataTable.Columns, "X"));
                }

                mTempchartNavigations = chartNavigations1;
                if (mExcuteCount == 0) {
                    //第一层的chartNavigations数据
                    mLoadDataChartNavigations.add(chartNavigations1);
                } else {
                    //不是第一次点击
                    mChartNavigations.get(mChartNavigations.size() - 1).Value = mDataTable.Rows
                            .get(mChartIndex).get(DataTable.getColIndex(mDataTable.Columns, "X"));
                    for (int i = 0; i < mChartNavigations.size(); i++) {
                        mLoadDataChartNavigations.add(mChartNavigations.get(i));
                    }
                }
                //请求表单数据
                setViewType(CHARTCLICKVIEW);
                mPtrFrameLayout.autoRefresh();
                Log.d(TAG, "mLoadDataChartNavigations:" + new Gson().toJson(mLoadDataChartNavigations));
            }

            @Override
            public void onNothingSelected() {
                mLLChartSelelcDataContainer.setVisibility(View.GONE);
            }
        });
        pieChart.invalidate();
    }
}