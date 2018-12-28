package cc.emw.mobile.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.GridControl.ViewInfo;
import cc.emw.mobile.main.SaleOrderActivity;
import cc.emw.mobile.project.entities.Elements2;
import cc.emw.mobile.project.entities.SelectItems;
import cc.emw.mobile.util.HelpUtil;
import in.srain.cube.request.sender.MultiPartRequestSender;

/**
 * Created by chengyong.liu on 2016/5/11.
 */
public class SaleLeftViewAadpter extends BaseExpandableListAdapter {
    private static final String TAG = "SaleLeftViewAadpter";
    private Context mContext;
    private ArrayList<ViewInfo> mViews;//视图数据
    private ArrayList<Elements2> mSearchs;//搜索数据
    private ArrayList<String> mTitles;//导航标题
    private int viewsNumber = 0;
    private int searchsNumber = 0;
    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    private TimePickerView mStartPopupWindow;// 时间表弹窗
    private OptionsPickerView<String> mOptionsPickerView;//选择器
    private TextView mTvSearchContent;//临时记录被点击的时控件

    private int mClickPosition = 0;//点击视图的位置,默认是默认视图
    private int mType = -1;//记录选择的状态
    private Map<String, String> searchMap = new HashMap<>();//储存搜索数据
    private int statePosition = -1;//记录状态的位置

    private EditText mEt;//记录文本编辑，用于关闭软键盘

    private int mChildpos;
    private Map<Integer, Boolean> propertyMap;//属性集合，记录checkBox类型的数据被点击的情况
    private int index1 = -1;
    private int index2 = -2;

    public SaleLeftViewAadpter(Context context) {
        mContext = context;
        mViews = new ArrayList<>();
        mSearchs = new ArrayList<>();
        mTitles = new ArrayList<>();
        mTitles.clear();
        mTitles.add("视图");
        mTitles.add("搜索");
        initTimePicker();


    }

    public void setArrayDataList(ArrayList<ViewInfo> views, ArrayList<Elements2> searchs) {
        propertyMap = new HashMap<>();
        if (views != null) {
            mViews.clear();
            mViews.addAll(views);
            viewsNumber = views.size();
        }
        if (searchs != null) {
            mSearchs.clear();
            mSearchs.addAll(searchs);
            searchsNumber = searchs.size();
            for (int i = 0; i < searchsNumber; i++) {
                if ("CheckBox".equals(searchs.get(i).Type)) {
                    ArrayList<SelectItems> selectItems = searchs.get(i).SelectItems;
                    if (selectItems != null) {
                        for (int j = 0; j < selectItems.size(); j++) {
                            propertyMap.put(j, false);
                        }
                    }

                }
            }
        }


        initOpionSelector();
    }

    @Override
    public int getGroupCount() {
        return mTitles.size();
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return groupPosition == 0 ? viewsNumber : searchsNumber;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupPosition == 0 ? mViews.get(childPosition) : mSearchs.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //展示父类视图
        GroupViewHolder gh;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_salegroup, null);
            gh = new GroupViewHolder();
            gh.name = (TextView) convertView.findViewById(R.id.tv_salegroup_title);
            convertView.setTag(gh);
        } else {
            gh = (GroupViewHolder) convertView.getTag();
        }
        String data = mTitles.get(groupPosition);
        gh.name.setText(data);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder ch;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_salechild_view, null);
            ch = new ChildViewHolder();
            //视图
            ch.viewContainer = (LinearLayout) convertView.findViewById(R.id.ll_salechild_view_container);
            ch.viewName = (TextView) convertView.findViewById(R.id.tv_salechild_name);
            ch.viewIcon = (ImageView) convertView.findViewById(R.id.iv_salechild_icon);
            //搜索
            ch.serachContainer = (RelativeLayout) convertView.findViewById(R.id.rl_salechild_search_container);
            ch.searchName = (TextView) convertView.findViewById(R.id.tv_element_name);
            ch.searchIcon = (ImageView) convertView.findViewById(R.id.iv_element_requireIcon);
            ch.etContent = (EditText) convertView.findViewById(R.id.et_element_input);
            ch.ll = (LinearLayout) convertView.findViewById(R.id.ll_element_content);
            ch.tvContent = (TextView) convertView.findViewById(R.id.tv_element_input);
            ch.ivContent = (ImageView) convertView.findViewById(R.id.iv_element_typeicon);
            ch.property = (LinearLayout) convertView.findViewById(R.id.ll_sale_property);
            convertView.setTag(ch);

        } else {
            ch = (ChildViewHolder) convertView.getTag();
        }
        //展示数据
        ch.serachContainer.setVisibility(groupPosition == 0 ? View.GONE : View.VISIBLE);
        ch.viewContainer.setVisibility(groupPosition == 0 ? View.VISIBLE : View.GONE);
        if (groupPosition == 0) {
            //视图列表数据的展示
            final ViewInfo viewInfo = mViews.get(childPosition);
            ch.viewName.setText(viewInfo.Name);
            ch.viewContainer.setBackgroundColor(mClickPosition == childPosition ? Color.YELLOW : Color.TRANSPARENT);
            //TODO 设置图标
            //点击事件
            ch.viewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickPosition = childPosition;
                    notifyDataSetChanged();
                    //调用Activity的方法实现请求数据
                    SaleOrderActivity soa = (SaleOrderActivity) SaleLeftViewAadpter.this.mContext;
                    soa.closeLeftDrawerLayout();
                    switch (viewInfo.ID) {
                        case "0":
                            soa.setPage(SaleOrderActivity.PAGE_FIRST);
                            ((SaleOrderActivity) SaleLeftViewAadpter.this.mContext).setViewType(SaleOrderActivity.DEFAULTVIEW);
                            soa.getmPtrFrameLayout().autoRefresh();
                            break;
                        case "view0":
                            soa.setPage(SaleOrderActivity.PAGE_FIRST);
                            soa.setViewType(SaleOrderActivity.TESTVIEW1);
                            soa.getmPtrFrameLayout().autoRefresh();
                            break;
                        case "view1":
                            soa.setPage(SaleOrderActivity.PAGE_FIRST);
                            ((SaleOrderActivity) SaleLeftViewAadpter.this.mContext).setViewType(SaleOrderActivity.TESTVIEW2);
                            soa.getmPtrFrameLayout().autoRefresh();
                            break;
                    }

                }
            });
        } else {
            //搜索列表数据的展示
            final Elements2 elements2 = mSearchs.get(childPosition);
            ch.searchName.setText(elements2.Name);
            ch.tvContent.setTag(childPosition);
            ch.etContent.setTag(childPosition);
            ch.etContent.addTextChangedListener(new MyTextWatcher(ch) {
                @Override
                public void afterTextChanged(Editable s, ChildViewHolder holder) {
                    int childPos = (Integer) holder.etContent.getTag();
                    Elements2 elements = mSearchs.get(childPos);
                    elements.Value = s.toString().trim();
                    mSearchs.set(childPos, elements);
                    searchMap.put(elements.ID, elements.Value);//文本改变后储存的值
//                    Log.d(TAG, elements.ID + ":" + elements.Value);
                }
            });
            //判断数据Type
            switch (elements2.Type) {
                case "Lable":
                case "Searcher":
                case "TextBox":
                    //文本框
                    ch.property.setVisibility(View.GONE);
                    ch.ll.setVisibility(View.GONE);
                    ch.etContent.setVisibility(View.VISIBLE);
                    ch.etContent.setText(elements2.Value);
//                    ch.etContent.requestFocus();
                    ch.etContent.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                index1 = childPosition;
                                index2 = groupPosition;

                            }
                            return false;
                        }
                    });
                    ch.etContent.clearFocus();
//                    Log.d(TAG, "element:" + elements2.Name + ":index1:" + index1 + ":index2:" + index2 + "--childPosition:first:" + childPosition);
                    if (index1 != -1 && index1 == childPosition && index2 != -2 && index2 == groupPosition) {
                        ch.etContent.requestFocus();
                        ch.etContent.setSelection(ch.etContent.getText().length());
//                        Log.d(TAG, "element:" + elements2.Name + ":index1:" + index1 + ":index2:" + index2 + "--childPosition:second" + childPosition);
//                        ch.etContent.setEnabled(true);
//                        ch.etContent.setClickable(true);


                    }

                    break;
                case "DateTimer":
                    //时间选择
                    ch.property.setVisibility(View.GONE);
                    ch.ll.setVisibility(View.VISIBLE);
                    ch.etContent.setVisibility(View.GONE);
                    ch.ivContent.setImageResource(R.drawable.q);
                    ch.tvContent.setText(elements2.Value);

//                    ch.tvContent.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mChildpos = childPosition;
//                            mStartPopupWindow.show();
//                        }
//                    });
                    ch.tvContent.setOnClickListener(new MyOnclickListiner(ch) {
                        @Override
                        public void onClick(View v, ChildViewHolder holder) {
                            int childPos = (Integer) holder.tvContent.getTag();
                            Elements2 elements = mSearchs.get(childPos);
                            mChildpos = childPos;
                            mStartPopupWindow.setTitle(elements.Name);
                            mStartPopupWindow.show();
                            HelpUtil.hideSoftInput(mContext, v);
                        }
                    });
                    break;
                case "DropDownList":
                    ch.property.setVisibility(View.GONE);
                    ch.etContent.setVisibility(View.GONE);
                    ch.ll.setVisibility(View.VISIBLE);
                    ch.ivContent.setImageResource(R.drawable.w);
                    if (TextUtils.isDigitsOnly(elements2.Value) && !TextUtils.isEmpty(elements2.Value)) {
                        int sVal = Integer.valueOf(elements2.Value);
                        ch.tvContent.setText(elements2.SelectItems.get(sVal - 1).Text);
                    }
//                    ch.tvContent.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            statePosition = childPosition;
//                            mOptionsPickerView.show();
//                        }
//                    });
                    ch.tvContent.setOnClickListener(new MyOnclickListiner(ch) {
                        @Override
                        public void onClick(View v, ChildViewHolder holder) {
                            int childPos = (Integer) holder.tvContent.getTag();
                            statePosition = childPos;
                            Elements2 elements = mSearchs.get(childPos);
                            mOptionsPickerView.setTitle(elements.Name);
                            mOptionsPickerView.show();
                            HelpUtil.hideSoftInput(mContext, v);
                        }
                    });
                    break;
                case "CheckBox":
                    ch.property.setVisibility(View.VISIBLE);
                    ch.etContent.setVisibility(View.GONE);
                    ch.ll.setVisibility(View.GONE);
                    final ArrayList<SelectItems> selectItems = elements2.SelectItems;
                    ch.property.removeAllViews();
                    if (selectItems != null) {
                        for (int i = 0; i < selectItems.size(); i++) {
                            //添加布局
                            final TextView textView = new TextView(mContext);
//                            R.drawable.ico_duoxuan:R.drawable.cm_multi_select_nor
                            setDrawableLeft(textView, propertyMap.get(i) ? R.drawable.ico_duoxuan : R.drawable.cm_multi_select_nor);
                            textView.setText(selectItems.get(i).Text);
                            ch.property.addView(textView);
                            final int finalI = i;
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    propertyMap.put(finalI, propertyMap.get(finalI) ? false : true);
                                    //遍历属性集合
                                    StringBuilder sb = new StringBuilder();

                                    for (Map.Entry<Integer, Boolean> entry : propertyMap.entrySet()) {
                                        Integer key = entry.getKey();
                                        Boolean value = entry.getValue();
                                        if (value) {
                                            sb.append(selectItems.get(key).Value + ",");
                                        }
                                    }
                                    searchMap.put(elements2.ID, getString(sb));
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    }
                    break;

            }

        }
        return convertView;
    }

    public void setDrawableLeft(TextView textView, int resID) {
        Drawable drawable = mContext.getResources().getDrawable(resID);
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    private void initTimePicker() {
        mStartPopupWindow = new TimePickerView(mContext, TimePickerView.Type.ALL);// 时间选择器
        mStartPopupWindow.setTitle(mContext.getString(R.string.beg_time));
        mStartPopupWindow.setCancelable(true);
        mStartPopupWindow.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                updateDiaplay(date);
            }
        });
    }

    private void updateDiaplay(Date date) {
        String formatString = mContext.getResources().getString(R.string.timeformat6);
        SimpleDateFormat f = new SimpleDateFormat(formatString, Locale.CHINA);
        String format = f.format(date);
        //给布局设置值
        mSearchs.get(mChildpos).Value = format;
        searchMap.put(mSearchs.get(mChildpos).ID, format);
        notifyDataSetChanged();
    }

    private void initOpionSelector() {
        // 紧急程度滚轴
        ArrayList<String> sorts = new ArrayList<>();
        for (int i = 0; i < mSearchs.size(); i++) {
            if ("DropDownList".equals(mSearchs.get(i).Type)) {
                ArrayList<SelectItems> selectItems = mSearchs.get(i).SelectItems;
                if (selectItems != null) {
                    for (int j = 0; j < selectItems.size(); j++) {
                        sorts.add(selectItems.get(j).Text);
                    }
                }
                break;
            }
        }
        mOptionsPickerView = new OptionsPickerView<String>(mContext);
        mOptionsPickerView.setPicker(sorts);
        mOptionsPickerView.setTitle(mContext.getString(R.string.task_summarize_state));
        mOptionsPickerView.setCancelable(true);
        mOptionsPickerView.setCyclic(false);// 无限循环

        final ArrayList<String> finalSorts = sorts;
        mOptionsPickerView
                .setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int options1, int option2,
                                                int options3) {
                        //记录点击的type
                        mType = options1 + 1;
                        if (statePosition != -1) {
//                            Log.d(TAG,(mSearchs.get(statePosition).ID+":"+mType+""));
                            mSearchs.get(statePosition).Value = mType + "";
                            searchMap.put(mSearchs.get(statePosition).ID, mType + "");
                            notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class GroupViewHolder {
        TextView name;
    }

    class ChildViewHolder {
        LinearLayout viewContainer;//视图容器
        TextView viewName;
        ImageView viewIcon;
        RelativeLayout serachContainer;//搜索容器
        TextView searchName;
        ImageView searchIcon;
        EditText etContent;
        LinearLayout ll;
        TextView tvContent;
        ImageView ivContent;

        LinearLayout property;//属性容器
    }

    private abstract class MyTextWatcher implements TextWatcher {
        private ChildViewHolder holder;

        public MyTextWatcher(ChildViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            afterTextChanged(s, holder);
        }

        public abstract void afterTextChanged(Editable s, ChildViewHolder holder);
    }

    private abstract class MyOnclickListiner implements View.OnClickListener {
        private ChildViewHolder mHolder;

        public MyOnclickListiner(ChildViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }

        public abstract void onClick(View v, ChildViewHolder holder);
    }

    /**
     * 获取查询数据
     *
     * @return
     */
    public String getSearchData() {
        StringBuilder sb = new StringBuilder();
        if (mClickPosition != 0) {
            //读取视图数据
            ViewInfo searchView = mViews.get(mClickPosition);
            sb.append("\"SelectedView\":{\"ID\":\"" + searchView.ID + "\",\"Name\":\"" + searchView.Name + "\"},");
        }

        sb.append("\"SearcherValues\":{");

        for (Map.Entry<String, String> entry : searchMap.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            if (TextUtils.isEmpty(value)) {
                continue;
            }

            sb.append("\"" + key + "\"" + ":\"" + value + "\",");
        }
        String temp = getString(sb);
        return temp + "}";
    }

    /**
     * 正则去除多余的逗号
     *
     * @param sb
     * @return
     */
    public String getString(StringBuilder sb) {
        String temp = sb.toString();
        Pattern p = Pattern.compile(",$");
        Matcher m = p.matcher(temp);
        temp = m.replaceFirst("");
        return temp;
    }


}
