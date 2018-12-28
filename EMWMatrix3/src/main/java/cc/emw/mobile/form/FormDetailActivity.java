package cc.emw.mobile.form;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.base.BaseHolder;
import cc.emw.mobile.base.MyBaseAdapter;
import cc.emw.mobile.form.adapter.FormDetailAdapter;
import cc.emw.mobile.form.entity.DataTable;
import cc.emw.mobile.form.entity.Elements2;
import cc.emw.mobile.form.entity.Flow;
import cc.emw.mobile.form.entity.Form;
import cc.emw.mobile.form.entity.IFlow;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.net.RequestParam;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.PopMenu;
import cc.emw.mobile.view.RightMenu;
import cc.emw.mobile.view.SwipeBackExpandableListView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * 表单详情页面
 */
@ContentView(R.layout.activity_form_detail)
public class FormDetailActivity extends BaseActivity {
    private static final String TAG = "FormDetailActivity";
    public static final int SEARCH_REQUEST_CODE = 1;
    public static final int PROCESS_REQUEST_CODE = 3;

    @ViewInject(R.id.cm_header_btn_left)
    private IconTextView mHeaderBackBtn; // 顶部返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题
    @ViewInject(R.id.cm_header_btn_flow)
    private IconTextView mHeaderFlowBtn; // 顶部条流程按钮
    @ViewInject(R.id.cm_header_btn_right)
    private IconTextView mHeaderMoreBtn; // 顶部条更多按钮

    @ViewInject(R.id.load_more_list_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout; // 下拉刷新
    @ViewInject(R.id.lv_saleform)
    private SwipeBackExpandableListView mListView;

    private Form mForm;
    private FormDetailAdapter adapter;
    private String pageID, rowID;
    private RightMenu mMenu;
    private Dialog mLoadingDialog; //加载框
    private DisplayImageOptions options;
    private FlowInfoAdapter flowInfoAdapter;
    private boolean canShowPopMenu = false;

    private DrawerLayout mDrawerLayout;//侧滑抽屉
    @ViewInject(R.id.tv_flow_tilte)
    private TextView mFlowTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
        pageID = getIntent().getStringExtra("page_id");
        rowID = getIntent().getStringExtra("row_id");
        initView();
        refresh();
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        if (adapter != null && adapter.getSearchMap() != null) {
            adapter.getSearchMap().clear();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.hasExtra("elem") && data.hasExtra("data_table")) {
            Elements2 elements2 = (Elements2) data.getSerializableExtra("elem");
            DataTable dataTable = (DataTable) data.getSerializableExtra("data_table");
            adapter.getSearchMap().put(elements2.ID, dataTable);
        }

        if (resultCode == RESULT_OK && requestCode == SEARCH_REQUEST_CODE) {
            Elements2 elements2 = (Elements2) data.getSerializableExtra("elem");
            int groupPosition = data.getIntExtra("group_position", -1);
            int childPosition = data.getIntExtra("child_position", -1);
            if (groupPosition == -1 || childPosition == -1) {
                return;
            }
            mForm.Elements.get(groupPosition).Elements.set(childPosition, elements2);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "onActivityResult()->element value: " + elements2.Value);
        }else if(resultCode == RESULT_OK && requestCode == PROCESS_REQUEST_CODE){
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            }
            mPtrFrameLayout.autoRefresh(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawers();
        } else {
            finish();
        }
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_flow, R.id.cm_header_tv_right, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                finish();
                break;
            case R.id.cm_header_btn_flow:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;
            case R.id.cm_header_btn_right:
                mMenu.showAsDropDown(mHeaderMoreBtn);
                break;
        }
    }

    private void initView() {
        setTopBar();
        initMenu();
        mPtrFrameLayout.setPinContent(false);
        mPtrFrameLayout.setLoadingMinTime(1000);
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

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                Logger.w("sss", "onDrawerOpened"); //onDrawerOpened() >> onScroll()
                mListView.setEnableGesture(false); //侧边栏打开后，会执行SwipeBackExpandableListView中onScroll
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                Logger.w("sss", "onDrawerClosed"); //onScroll() >> onDrawerClosed()
                mListView.setEnableGesture(true); //侧边栏关闭之前，会执行SwipeBackExpandableListView中onScroll
                mListView.notifyScrollChangedListeners(); //需调用notifyScrollChangedListeners回到之前状态
            }
        });
        ListView listView = (ListView) findViewById(R.id.invite_info_lv);
        flowInfoAdapter = new FlowInfoAdapter(this);
        listView.setAdapter(flowInfoAdapter);
    }

    private void refresh() {
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                request();
            }
        });
    }

    private void setTopBar() {
        mHeaderBackBtn.setIconText("eb68");
        mHeaderBackBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 执行数据请求，获取表单详情信息
     */
    private void request() {
        String requestFormUrl;
        if (rowID == null) {
            requestFormUrl = Const.BASE_URL + "/mpage/" + pageID; //新建
        } else {
            requestFormUrl = Const.BASE_URL + "/mpage/" + pageID + "?rid=" + rowID;
        }
        Log.d(TAG, "request()->url: " + requestFormUrl);
        RequestParam params = new RequestParam(requestFormUrl);
        Callback.Cancelable cancelable = x.http().post(params, new RequestCallback<Form>(Form.class) {

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastUtil.showToast(getApplicationContext(), "获取数据失败！");
                        Log.d(TAG, "onError()->msg: " + ex.toString());
                    }

                    @Override
                    public void onFinished() {
                        mPtrFrameLayout.refreshComplete();
                    }

                    @Override
                    public void onParseSuccess(Form form) {
                        mForm = form;
                        if (mForm != null) {
                            mHeaderTitleTv.setText(mForm.Name);
                            mHeaderTitleTv.setVisibility(View.VISIBLE);

                            if (rowID == null) {
                                mForm.VerifyTime = "0";
                            }
                            if (mForm.Navigations == null || mForm.Navigations.size() == 0) {
                                mForm.Navigations = new ArrayList<>();
                                Form.Navigations nav = new Form.Navigations();
                                nav.Element = "Elem1";
                                nav.Name = "表单节";
                                mForm.Navigations.add(nav);
                            }
                            adapter = new FormDetailAdapter(FormDetailActivity.this, mForm);
                            WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
                            mListView.setAdapter(wrapperAdapter);
                            for (int i = 0; i < wrapperAdapter.getGroupCount(); i++) {
                                mListView.expandGroup(i);
                            }

                            //添加流程信息
                            if (mForm.Flow != null && mForm.Flow.Flows != null && mForm.Flow.Flows.size() > 0) {
                                mFlowTitleTv.setText(mForm.Flow.Name);
                                ArrayList<IFlow> flows = new ArrayList<>();
                                Flow flow = mForm.Flow.Flows.get(0);
                                if (flow.History != null) {
                                    for (int i = 0; i < flow.History.size(); i++) {
                                        flows.add(flow.History.get(i));
                                        Logger.d(TAG, "add History");
                                    }
                                }
                                if (flow.UserStatus != null && flow.UserStatus.size() > 0) {
                                    for (int i = 0; i < flow.UserStatus.size(); i++) {
                                        flows.add(flow.UserStatus.get(i));
                                    }
                                }
                                mMenu.clearItems();
                                if (flow.Lines != null && flow.Lines.size() > 0) {
                                    for (int i = 0; i < flow.Lines.size(); i++) {
                                        Logger.d(TAG, "add Lines");
                                        mMenu.addItem(flow.Lines.get(i).Name, i);
                                        //当遍历到最后一个时，追加两个
                                        if (i == flow.Lines.size() - 1) {
                                            mMenu.addItem("提交", i + 1);
//                                        mMenu.addItem("删除", i + 2);
                                            canShowPopMenu = true;
                                        }
                                    }
                                } else {
                                    mMenu.addItem("提交", 0);
//                                mMenu.addItem("删除", 1);
                                    canShowPopMenu = true;
                                }
                                flowInfoAdapter.setData(flows);
                                flowInfoAdapter.notifyDataSetChanged();
                                mHeaderFlowBtn.setVisibility(View.VISIBLE);
                                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT); //打开手势滑动
                            } else {
                                mMenu.clearItems();
                                mMenu.addItem("提交", 0);
//                            mMenu.addItem("删除", 1);
                                canShowPopMenu = true;
                                mHeaderFlowBtn.setVisibility(View.GONE);
                                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT); //关闭手势滑动
                            }

                            if(canShowPopMenu) {
                                mHeaderMoreBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    /**
     * 提交表单，提交对表单的相关修改
     */
    private void postForm(String postUrl, Form mForm, final String rowID) {
        if (mForm == null) {
            return;
        }
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("RecordID", mForm.RecordID);
        dataMap.put("VerifyTime", mForm.VerifyTime);
        for (int i = 0; i < mForm.Elements.size(); i++) {
            Form.Elements elem = mForm.Elements.get(i);
            if ("RepeatTable".equalsIgnoreCase(elem.Type)) {
                Map<String, String> noNullMap = new HashMap<>(); //存放值不能为空的元素ID
                for (Elements2 ele : elem.Elements) {
                    if (!ele.IsAllowNull) {
                        noNullMap.put(ele.ID, ele.Title);
                    }
                }
                List<Map<String, Object>> repeatList = new ArrayList<>();
                for (int j = 0; j < elem.Data.Rows.size(); j++) {
                    Map<String, Object> repeatMap = new LinkedHashMap<>();
                    for (int k = 0; k < elem.Data.Rows.get(j).size(); k++) {
                        if (k < elem.Data.Columns.size()) {
                            if (noNullMap.containsKey(elem.Data.Columns.get(k)) && TextUtils.isEmpty(elem.Data.Rows.get(j).get(k))) {
                                ToastUtil.showToast(this, noNullMap.get(elem.Data.Columns.get(k)) + getResources().getString(R.string.is_not_null));
                                return;
                            }
                            repeatMap.put(elem.Data.Columns.get(k), elem.Data.Rows.get(j).get(k));
                        }
                    }
                    repeatList.add(repeatMap);
                }
                dataMap.put(elem.ID, repeatList);
                String delRowIDs = adapter.getDelRowIDMap().get(elem.ID);
                if (!TextUtils.isEmpty(delRowIDs)) {
                    dataMap.put(elem.ID + "_delete", delRowIDs);
                }
            } else {
                for (int j = 0; j < elem.Elements.size(); j++) {
                    Elements2 ele = elem.Elements.get(j);
                    if (TextUtils.isEmpty(ele.Value) && !ele.IsAllowNull) {
                        ToastUtil.showToast(this, ele.Title + getResources().getString(R.string.is_not_null));
                        return;
                    }
                    dataMap.put(ele.ID, ele.Value);
                    if ("Searcher".equals(ele.Type)) {
                        dataMap.put("t_"+ele.ID, ele.Text);
                    }
                }
            }
        }

        RequestParam params = new RequestParam(postUrl);
        String body = new Gson().toJson(dataMap);
        Log.d(TAG, "postForm()->data: " + body);
        params.setStringBody(body);
        x.http().post(params, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips3);
                mLoadingDialog.show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                if (rowID == null) {
                    ToastUtil.showToast(FormDetailActivity.this, R.string.create_fail);
                } else {
                    ToastUtil.showToast(FormDetailActivity.this, R.string.modify_fail);
                }
            }

            @Override
            public void onSuccess(String result) {
                if (mLoadingDialog != null && !isFinishing()) mLoadingDialog.dismiss();
                try {
                    Log.d(TAG, "postForm()->result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("Code") && jsonObject.getInt("Code") > 0) {
                        if (rowID == null) {
                            ToastUtil.showToast(FormDetailActivity.this, R.string.create_success);
                        } else {
                            ToastUtil.showToast(FormDetailActivity.this, R.string.modify_success);
                        }
                        mPtrFrameLayout.autoRefresh(false);
                    } else {
                        onError(null, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    class FlowInfoAdapter extends MyBaseAdapter<IFlow> {
        private Context mContext;
        private ArrayList<IFlow> datas = new ArrayList<>();

        public FlowInfoAdapter(Context context) {
            mContext = context;
            this.mDatas = datas;
        }

        @Override
        public void setData(List<IFlow> dataList) {
            super.setData(dataList);
            if (dataList != null) {
                datas.clear();
                datas.addAll(dataList);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = vh.mHolderView;
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            IFlow data = mDatas.get(position);
            vh.setDataAndRefreshHolderView(position, data);
            return convertView;
        }
    }

    class ViewHolder extends BaseHolder<IFlow> {
        CircleImageView portrait;
        TextView name;
        TextView process_type;
        TextView process_content;
        LinearLayout line_tool;
        TextView time;

        @Override
        public void refreshHolderView(int position, final IFlow data) {
            line_tool.removeAllViews();
            if (data instanceof Flow.History) {
                Flow.History tempData = (Flow.History) data;
                String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, tempData.Image);
                ImageLoader.getInstance().displayImage(uri, portrait, options);
                name.setText(tempData.User);
                process_type.setText(tempData.Line);
                process_content.setText(tempData.Content);
                time.setText(tempData.Time);
                process_type.setTag("History");
            } else if (data instanceof Flow.UserStatu) {
                Flow.UserStatu tempData = (Flow.UserStatu) data;
                String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil.readUserInfo().CompanyCode, tempData.UserImage);
                ImageLoader.getInstance().displayImage(uri, portrait, options);
                name.setText(tempData.UserName);
                process_content.setText("");
                time.setText("");
                if (tempData.UserID == PrefsUtil.readUserInfo().ID) {
                    process_type.setText("");
                    if (mForm.Flow != null && mForm.Flow.Flows != null && mForm.Flow.Flows.size() > 0) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        OnLineClickListener clickListener = new OnLineClickListener();
                        Flow flow = mForm.Flow.Flows.get(0);
                        if (flow.Lines == null) {
                            flow.Lines = new ArrayList<>();
                        }
                        if (flow.AllowAppend) { //https://www.emw.cc:5678/Page/24945/lineto?NextUser=262041&Option=111&Line=-3&FlowID=501972&rid=501971
                            Flow.Line line = new Flow.Line();
                            line.Icon = "communication_import_export";
                            line.ID = -3;
                            line.Name = "转发";
                            flow.Lines.add(line);
                        }
                        if (flow.AllowTrans) { //https://www.emw.cc:5678/Page/24945/lineto?NextUser=103922&Option=222&Line=-2&FlowID=501972&rid=501971
                            Flow.Line line = new Flow.Line();
                            line.Icon = "content_redo";
                            line.ID = -2;
                            line.Name = "加签";
                            flow.Lines.add(line);
                        }
                        if (flow.AllowReturn) { //https://www.emw.cc:5678/Page/24945/lineto?Option=333&Line=-1&FlowID=501972&rid=501971
                            Flow.Line line = new Flow.Line();
                            line.Icon = "av_replay";
                            line.ID = -1;
                            line.Name = "回退到上个环节";
                            line.NoReceiver = true;
                            flow.Lines.add(line);
                        }
                        for (int i = 0; i < flow.Lines.size(); i++) {
                            Flow.Line line = flow.Lines.get(i);
                            View view = LayoutInflater.from(FormDetailActivity.this).inflate(R.layout.listitem_formdetail_tool, null);
                            view.setBackgroundResource(R.drawable.round_corner_green);
                            view.setTag(line);
                            view.setOnClickListener(clickListener);
                            IconTextView iconTextView = (IconTextView)view.findViewById(R.id.childicon1);
                            TextView textView = (TextView)view.findViewById(R.id.childtext1);
                            if (!TextUtils.isEmpty(line.Icon)) {
                                iconTextView.setIconText(IconTextView.getIconCode(line.Icon));
                                iconTextView.setVisibility(View.VISIBLE);
                            } else {
                                iconTextView.setVisibility(View.GONE);
                            }
                            textView.setText(line.Name);
                            params.topMargin = DisplayUtil.dip2px(FormDetailActivity.this, 5);
                            line_tool.addView(view, params);
                        }
                    }
                } else {
                    process_type.setText("正在处理");
                }
            }

        }

        @Override
        public View initHolderViewAndFindViews() {
            View view = LayoutInflater.from(FormDetailActivity.this).inflate(R.layout.listitem_flow_item, null, false);
            portrait = (CircleImageView) view.findViewById(R.id.portrait);
            name = (TextView) view.findViewById(R.id.name);
            process_type = (TextView) view.findViewById(R.id.process_type);
            process_content = (TextView) view.findViewById(R.id.process_content);
            line_tool = (LinearLayout) view.findViewById(R.id.line_tool);
            time = (TextView) view.findViewById(R.id.time);
            return view;
        }
    }

    private class OnLineClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Flow.Line line = (Flow.Line) v.getTag();
            Intent fpIntent = new Intent(FormDetailActivity.this, FlowProcessActivity.class);
            fpIntent.putExtra("process_type", line.Name);
            fpIntent.putExtra("page_id", pageID);
            fpIntent.putExtra("row_id", rowID);
            fpIntent.putExtra("flow_id", mForm.Flow.Flows.get(0).ID);
            fpIntent.putExtra("line_id", line.ID);
            fpIntent.putExtra("form", adapter.getForm());
            fpIntent.putExtra("no_receiver", line.NoReceiver);
            fpIntent.putExtra("start_anim", false);
            startActivityForResult(fpIntent,PROCESS_REQUEST_CODE);
        }
    }

    private void initMenu() {
        mMenu = new RightMenu(this);
        mMenu.setCustomBackground(R.drawable.bg_pop_menu_black);
        mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
            @Override
            public void selected(View view, PopMenu.Item item, int position) {
                ArrayList<Flow.Line> tmpLines = new ArrayList<>();
                if (mForm != null && mForm.Flow != null && mForm.Flow.Flows != null && mForm.Flow.Flows.size() > 0
                        && mForm.Flow.Flows.get(0) != null && mForm.Flow.Flows.get(0).Lines != null) {
                    tmpLines = mForm.Flow.Flows.get(0).Lines;
                }
                //当流程信息为0时，只显示【新建】 【删除】
                if (position < tmpLines.size()) {
                    Intent fpIntent = new Intent(FormDetailActivity.this, FlowProcessActivity.class);
                    fpIntent.putExtra("process_type", tmpLines.get(position).Name);
                    fpIntent.putExtra("page_id", pageID);
                    fpIntent.putExtra("row_id", rowID);
                    fpIntent.putExtra("flow_id", mForm.Flow.Flows.get(0).ID);
                    fpIntent.putExtra("line_id", tmpLines.get(position).ID);
                    fpIntent.putExtra("form", mForm);
                    startActivityForResult(fpIntent,PROCESS_REQUEST_CODE);
                } else if (position == tmpLines.size()) {
                    String postUrl = Const.BASE_URL + "/Page/" + pageID + "/save";
                    postForm(postUrl, adapter.getForm(), rowID);
                }/* else if (position == tmpLines.size() + 1) {
                    new AlertDialog(FormDetailActivity.this).builder().setMsg("是否确认删除？")
                            .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text))
                            .setPositiveButton(getString(R.string.confirm), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                }*/
            }
        });
    }
    /*Readme
    流程操作：
    1.分为History、UserStatus、Line三种；分别为历史节点、当前节点、后续节点
    2.当前节点需做权限判断，UserStatus中当UserID为0时表示谁都可操作，LineID为0表示未操作过，UserID不为0时登录用户在该id上方有权限操作
     */

}
