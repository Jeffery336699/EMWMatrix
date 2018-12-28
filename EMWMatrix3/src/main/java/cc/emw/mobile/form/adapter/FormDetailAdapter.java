package cc.emw.mobile.form.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.OptionsPickerView.OnOptionsSelectListener;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePickerView.Type;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnShowListener;
import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.zf.iosdialog.widget.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import cc.emw.mobile.R;
import cc.emw.mobile.form.FormDetailActivity;
import cc.emw.mobile.form.SearchActivity;
import cc.emw.mobile.form.entity.CommonConsts;
import cc.emw.mobile.form.entity.DataTable;
import cc.emw.mobile.form.entity.Elements2;
import cc.emw.mobile.form.entity.Form;
import cc.emw.mobile.form.entity.GridControl;
import cc.emw.mobile.form.util.CommonUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.view.FlowLayout;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 表单详情adapter
 */
public class FormDetailAdapter extends BaseExpandableListAdapter implements OnShowListener, OnDismissListener {
    private static final String TAG = "FormDetailAdapter";
    private Context mContext;
    private Form mForm;
    private ArrayList<String> options; //选项数据
    private OptionsPickerView<String> mOptionsPickerView; //选项选择器
    private TimePickerView mTimePickerView; //时间选择器
    private int groupPot; //当前修改分组的位置
    private int childPot; //当前修改分组下子的位置
    private Map<String, String> delRowIDMap; //存放重复表中删除某些行的id(以“,”隔开)
    private Map<String, DataTable> searchMap; //缓存元素类型为Serach的数据，快速展示SearchActivity中的数据

    public FormDetailAdapter(Context context, Form form) {
        this.mContext = context;
        this.mForm = form;
        clearHideData(mForm);
        delRowIDMap = new HashMap<>();
        searchMap = new HashMap<>();
        options = new ArrayList<>();
        initOptionPicker();
        initTimePicker();
    }

    public void setForm(Form form) {
        this.mForm = form;
        clearHideData(mForm);
    }

    public Form getForm() {
        return mForm;
    }

    public Map<String, String> getDelRowIDMap() {
        return delRowIDMap;
    }

    public Map<String, DataTable> getSearchMap() {
        return searchMap;
    }

    private void clearHideData(Form form) {
        if (form != null && form.Elements != null) {
            Iterator<Form.Elements> it = form.Elements.iterator();
            while (it.hasNext()) {
                Form.Elements elem = it.next();
                if (elem.State == CommonConsts.ElementStates.Hidden) { //删除隐藏的分组
                    it.remove();//注意此处不能用list.remove(it.next());
                } else {
                    if (elem.Elements != null) {
                        Iterator<Elements2> it2 = elem.Elements.iterator();
                        while (it2.hasNext()) {
                            if (it2.next().State == CommonConsts.ElementStates.Hidden) { //删除分组下隐藏的行
                                it2.remove();
                            }
                        }
                    }
                    if (elem.Data != null && elem.Data.Rows != null) { //把表格数据中数字末尾带".0"去掉
                        for (int i = 0; i < elem.Data.Rows.size(); i++) {
                            ArrayList<String> valList = elem.Data.Rows.get(i);
                            for (int j = 0; j < valList.size(); j++) {
                                String value = valList.get(j);
                                if (value != null && value.lastIndexOf(".0") > 0) {
                                    value = value.substring(0, value.length() - 2);
                                    elem.Data.Rows.get(i).set(j, value);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置行的状态
     * @param state
     * @param vh
     */
    private void setRowState(int state, ViewHolder vh) {
        if (state == CommonConsts.ElementStates.ReadOnly) {
            vh.inputEt.setBackgroundResource(R.color.white);
            vh.inputEt.setEnabled(false);
            vh.contentLayout.setBackgroundResource(R.color.white);
            vh.contentLayout.setEnabled(false);
            vh.typeItv.setVisibility(View.GONE);
        } else {
            vh.inputEt.setBackgroundResource(R.drawable.stroke_black);
            vh.inputEt.setEnabled(true);
            vh.contentLayout.setBackgroundResource(R.drawable.stroke_black);
            vh.contentLayout.setEnabled(true);
            vh.typeItv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getGroupCount() {
        return mForm.Elements == null ? 0 : mForm.Elements.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if("RepeatTable".equals(mForm.Elements.get(groupPosition).Type)){
            return 1;
        }
        return mForm.Elements.get(groupPosition).Elements == null ? 0
                : mForm.Elements.get(groupPosition).Elements.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mForm.Elements == null ? null : mForm.Elements.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mForm.Elements.get(groupPosition).Elements == null ? null
                : mForm.Elements.get(groupPosition).Elements.get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        GroupViewHolder vh;
        if (convertView == null) {
            vh = new GroupViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_formdetail_navigations, parent, false);
            vh.titleTv = (TextView) convertView.findViewById(R.id.tv_navigations_name);
            vh.arrowItv = (IconTextView)convertView.findViewById(R.id.tv_navigations_arrow);
            convertView.setTag(vh);
        } else {
            vh = (GroupViewHolder) convertView.getTag();
        }
        Form.Elements groupElement = mForm.Elements.get(groupPosition);
        vh.titleTv.setText(groupElement.Title);
        vh.arrowItv.setTextColor(mContext.getResources().getColor(isExpanded ? R.color.project_color_335B9D : R.color.project_color_CBCBCB));
        vh.arrowItv.setIconText(isExpanded ? "eb6a" : "eb67");

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_formdetail_element, parent, false);
            vh.rowRootLayout = (LinearLayout) convertView.findViewById(R.id.ll_element_row);
            vh.titleTv = (TextView) convertView.findViewById(R.id.tv_element_name);
            vh.inputEt = (EditText) convertView.findViewById(R.id.et_element_input);
            vh.selectLayout = (FlowLayout) convertView.findViewById(R.id.fl_element_select);
            vh.contentLayout = (LinearLayout) convertView.findViewById(R.id.ll_element_content);
            vh.inputTv = (TextView) convertView.findViewById(R.id.tv_element_input);
            vh.typeItv = (IconTextView) convertView.findViewById(R.id.itv_element_typeicon);
            vh.tableRootLayout = (LinearLayout) convertView.findViewById(R.id.ll_element_table);
            vh.toolLayout = (FlowLayout) convertView.findViewById(R.id.tool);
            vh.tableFixHeaders  = (TableFixHeaders) convertView.findViewById(R.id.table);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if("RepeatTable".equals(mForm.Elements.get(groupPosition).Type)){ //重复表
            final Form.Elements tempElement = mForm.Elements.get(groupPosition);
            vh.tableRootLayout.setVisibility(View.VISIBLE);
            vh.rowRootLayout.setVisibility(View.GONE);
            final MyAdapter myAdapter = new MyAdapter(mContext);
            myAdapter.setDataList(tempElement);
            vh.tableFixHeaders.setAdapter(myAdapter);

            //操作项，例如：添加、删除行
            if (tempElement != null && tempElement.Tools != null && tempElement.Tools.size() > 0) {
                vh.toolLayout.removeAllViews();
                for (int i = 0; i < tempElement.Tools.size(); i++) {
                    final GridControl.ToolInfo toolInfo = tempElement.Tools.get(i);
                    View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_formdetail_tool, null);
                    IconTextView iconTextView = (IconTextView)view.findViewById(R.id.childicon1);
                    TextView textView = (TextView)view.findViewById(R.id.childtext1);
                    if (!TextUtils.isEmpty(toolInfo.Icon)) {
                        iconTextView.setIconText(IconTextView.getIconCode(toolInfo.Icon));
                        iconTextView.setVisibility(View.VISIBLE);
                    } else {
                        iconTextView.setVisibility(View.GONE);
                    }
                    textView.setText(toolInfo.Name);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ("Create".equalsIgnoreCase(toolInfo.ToolType)) {
                                if (tempElement.Data == null || tempElement.Data.Columns == null
                                        || tempElement.Data.Columns.size() == 0 || tempElement.Data.Rows == null) {
                                    return;
                                }
                                ArrayList<String> newRow = new ArrayList<>();
                                for (int i = 0; i < tempElement.Data.Columns.size(); i++) {
                                    newRow.add("");
                                }
                                newRow.set(0, "-"+String.valueOf(System.currentTimeMillis()).substring(8));
                                for (int i = 0; i < tempElement.Elements.size(); i++) {
                                    Elements2 elem = tempElement.Elements.get(i);
                                    if (!TextUtils.isEmpty(elem.DefaultValue)) { //添加默认值
                                        newRow.set(myAdapter.getElemIndex(elem.ID), elem.DefaultValue);
                                    }
                                }
                                tempElement.Data.Rows.add(newRow);
                                notifyDataSetChanged();
                            } else if ("Delete".equalsIgnoreCase(toolInfo.ToolType)) {
                                if (myAdapter.getSelectIDs() != null && myAdapter.getSelectIDs().size() > 0) {
                                    new AlertDialog(mContext).builder().setMsg("是否确认删除选中的数据？")
                                            .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
                                            .setPositiveButton(mContext.getString(R.string.confirm), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Iterator<ArrayList<String>> it = tempElement.Data.Rows.iterator();
                                                    while (it.hasNext()) {
                                                        String rid = it.next().get(0);
                                                        if (myAdapter.getSelectIDs().contains(rid)) { //删除掉选中的行
                                                            String rowIDs = delRowIDMap.get(tempElement.ID);
                                                            if (TextUtils.isEmpty(rowIDs)) {
                                                                delRowIDMap.put(tempElement.ID, rid);
                                                            } else {
                                                                delRowIDMap.put(tempElement.ID, rowIDs + "," + rid);
                                                            }
                                                            it.remove();//注意此处不能用list.remove(it.next());
                                                        }
                                                    }
                                                    notifyDataSetChanged();
                                                }
                                            }).setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }).show();
                                }
                            }
                        }
                    });
                    vh.toolLayout.addView(view, FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        }else{
            final Elements2 element = mForm.Elements.get(groupPosition).Elements.get(childPosition);
            vh.tableRootLayout.setVisibility(View.GONE);
            vh.rowRootLayout.setVisibility(View.VISIBLE);
            vh.inputEt.setVisibility(View.GONE);
            vh.selectLayout.setVisibility(View.GONE);
            vh.contentLayout.setVisibility(View.GONE);
            vh.titleTv.setVisibility(View.VISIBLE);
            vh.titleTv.setText(element.Title);
            if (!element.IsAllowNull) {
                vh.titleTv.append("*");
                SpannableString spanStr = new SpannableString(vh.titleTv.getText());
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
                spanStr.setSpan(colorSpan, vh.titleTv.length() - 1, vh.titleTv.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                /*RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.2f);
                spanStr.setSpan(sizeSpan, vh.titleTv.length() - 1, vh.titleTv.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);*/
                vh.titleTv.setText(spanStr);
            }
            vh.inputEt.setTag(R.id.tag_first, groupPosition);
            vh.inputEt.setTag(R.id.tag_second, childPosition);
            vh.inputEt.addTextChangedListener(new MyTextWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s, ViewHolder holder) {
                    int gpos = (Integer) holder.inputEt.getTag(R.id.tag_first);
                    int cpos = (Integer) holder.inputEt.getTag(R.id.tag_second);
                    Elements2 e = mForm.Elements.get(gpos).Elements.get(cpos);
                    e.Value = s.toString();
                    mForm.Elements.get(gpos).Elements.set(cpos, e);
                }
            });

            switch (CommonUtil.getElementType(element.Type)) {
                case CommonConsts.ElementType.LABEL:
                    vh.titleTv.setVisibility(View.GONE);
                    vh.inputEt.setVisibility(View.VISIBLE);
                    vh.inputEt.setText(TextUtils.isEmpty(element.Title)?"":(Html.fromHtml(element.Title.replaceAll("</br>", "<br>"))));
                    vh.inputEt.setBackgroundResource(R.color.white);
                    vh.inputEt.setEnabled(false);
                    break;
                case CommonConsts.ElementType.TEXTBOX:
                    vh.inputEt.setVisibility(View.VISIBLE);
                    vh.inputEt.setText(element.Value);
                    vh.inputEt.clearFocus();
                    if(element.Length > 0) {
                        //限制输入最大不超过Length位
                        vh.inputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(element.Length)});
                    }
                    CharSequence text = vh.inputEt.getText();
                    if (text instanceof Spannable) {
                        Spannable spanText = (Spannable)text;
                        Selection.setSelection(spanText, text.length());
                    }

                    setRowState(element.State, vh);
                    break;
                case CommonConsts.ElementType.DATETIMER:
                    vh.contentLayout.setVisibility(View.VISIBLE);
                    vh.inputTv.setText(element.Value);
                    vh.inputTv.setTag(element);
                    vh.typeItv.setIconText("e92c");
                    vh.contentLayout.setOnClickListener(new MyOnclickListiner(vh) {
                        @Override
                        public void onClick(View v, ViewHolder holder) {
                            String timePickerTitle = ((Elements2) holder.inputTv.getTag()).Title;
                            mTimePickerView.setTitle(timePickerTitle);
                            groupPot = groupPosition;
                            childPot = childPosition;
                            mTimePickerView.show();
                        }
                    });

                    setRowState(element.State, vh);
                    break;
                case CommonConsts.ElementType.RADIOBUTTON:
                    if (element.State != CommonConsts.ElementStates.ReadOnly) {
                        vh.selectLayout.setVisibility(View.VISIBLE);
                        addRadioSelectItems(vh.selectLayout, element);
                        break;
                    }
                case CommonConsts.ElementType.DROPDOWNLIST:
                    vh.contentLayout.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(element.Value) && TextUtils.isDigitsOnly(element.Value)) {
                        for (int i = 0; i < element.SelectItems.size(); i++) {
                            if (element.Value.equals(element.SelectItems.get(i).Value)) {
                                vh.inputTv.setText(element.SelectItems.get(i).Text);
                                break;
                            }
                        }
                    }
                    vh.inputTv.setTag(element);
                    vh.typeItv.setIconText("eb67");
                    vh.contentLayout.setOnClickListener(new MyOnclickListiner(vh) {
                        @Override
                        public void onClick(View v, ViewHolder holder) {
                            HelpUtil.hideSoftInput(mContext, v);
                            Elements2 e = (Elements2) holder.inputTv.getTag();
                            options.clear();
                            if (e.SelectItems != null && e.SelectItems.size() != 0) {
                                for (int i = 0; i < e.SelectItems.size(); i++) {
                                    options.add(e.SelectItems.get(i).Text);
                                }
                            } else {
                                options.add("");
                            }
                            mOptionsPickerView.setPicker(options);
                            if (TextUtils.isDigitsOnly(e.Value) && !TextUtils.isEmpty(e.Value)) {
                                int defIndex = Integer.valueOf(e.Value);
                                mOptionsPickerView.setSelectOptions(defIndex - 1);
                            }
                            mOptionsPickerView.setTitle(e.Title);
                            groupPot = groupPosition;
                            childPot = childPosition;
                            mOptionsPickerView.show();
                        }
                    });

                    setRowState(element.State, vh);
                    break;
                case CommonConsts.ElementType.SEARCHER:
                    vh.contentLayout.setVisibility(View.VISIBLE);
                    vh.inputTv.setText(element.Text);
                    vh.inputTv.setTag(element);
                    vh.typeItv.setIconText("eb69");
                    vh.contentLayout.setOnClickListener(new MyOnclickListiner(vh) {
                        @Override
                        public void onClick(View v, ViewHolder holder) {
                            Intent intent = new Intent(mContext, SearchActivity.class);
                            Elements2 elem = (Elements2) holder.inputTv.getTag();
                            intent.putExtra("elem", elem);
                            intent.putExtra("page_id", mForm.ID);
                            intent.putExtra("record_id", mForm.RecordID);
                            intent.putExtra("group_position", groupPosition);
                            intent.putExtra("child_position", childPosition);
                            intent.putExtra("data_table", searchMap.get(elem.ID));
                            intent.putExtra("start_anim", false);
                            ((Activity) mContext).startActivityForResult(intent, FormDetailActivity.SEARCH_REQUEST_CODE);
                        }
                    });

                    setRowState(element.State, vh);
                    break;
                case CommonConsts.ElementType.TIMESELECTOR:
                    vh.contentLayout.setVisibility(View.VISIBLE);
                    vh.inputTv.setText(element.Text);
                    vh.inputTv.setTag(element);
                    vh.typeItv.setIconText("eb69");
                    vh.contentLayout.setOnClickListener(new MyOnclickListiner(vh) {
                        @Override
                        public void onClick(View v, ViewHolder holder) {
                        }
                    });

                    setRowState(element.State, vh);
                    break;
                case CommonConsts.ElementType.CHECKBOX:
                    vh.selectLayout.setVisibility(View.VISIBLE);
                    addMultiSelectItems(vh.selectLayout, element);
                    break;
            }
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private abstract class MyTextWatcher implements TextWatcher {
        private ViewHolder mHolder;

        public MyTextWatcher(ViewHolder holder) {
            this.mHolder = holder;
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
            afterTextChanged(s, mHolder);
        }

        public abstract void afterTextChanged(Editable s, ViewHolder holder);
    }
    private abstract class MyOnclickListiner implements View.OnClickListener {
        private ViewHolder mHolder;

        public MyOnclickListiner(ViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }

        public abstract void onClick(View v, ViewHolder holder);
    }

    class GroupViewHolder {
        TextView titleTv;
        IconTextView arrowItv;
    }

    class ViewHolder {
        LinearLayout rowRootLayout;
        TextView titleTv;
        EditText inputEt;
        FlowLayout selectLayout;
        LinearLayout contentLayout;
        TextView inputTv;
        IconTextView typeItv;
        
        LinearLayout tableRootLayout;
        FlowLayout toolLayout;
        TableFixHeaders tableFixHeaders;
    }

    private void initOptionPicker() {
        mOptionsPickerView = new OptionsPickerView<>(mContext);
        mOptionsPickerView.setPicker(options);
        mOptionsPickerView.setTitle("");
        mOptionsPickerView.setCancelable(true);
        mOptionsPickerView.setCyclic(false);
        mOptionsPickerView.setOnShowListener(this);
        mOptionsPickerView.setOnDismissListener(this);
        mOptionsPickerView.setOnoptionsSelectListener(new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                Elements2 elements2 = mForm.Elements.get(groupPot).Elements.get(childPot);
                elements2.Value = elements2.SelectItems.get(options1).Value;
                notifyDataSetChanged();
            }
        });
    }

    private void initTimePicker() {
        mTimePickerView = new TimePickerView(mContext, Type.ALL);// 时间选择器
        mTimePickerView.setCancelable(true);
        mTimePickerView.setOnShowListener(this);
        mTimePickerView.setOnDismissListener(this);
        mTimePickerView.setOnTimeSelectListener(new OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                dateDiaplay(date);
            }
        });
    }
    // 设置时间显示
    private void dateDiaplay(Date date) {
        SimpleDateFormat f = new SimpleDateFormat(mContext.getResources().getString(R.string.timeformat2), Locale.CHINA);
        mForm.Elements.get(groupPot).Elements.get(childPot).Value = f.format(date);
        notifyDataSetChanged();
    }

    private void addRadioSelectItems(final FlowLayout flowLayout, final Elements2 elem) {
        flowLayout.removeAllViews();
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < elem.SelectItems.size(); i++) {
            Elements2.SelectItems item = elem.SelectItems.get(i);
            final CheckBox childView = new CheckBox(mContext);
            childView.setButtonDrawable(R.drawable.cm_radio_select0);
            childView.setTextColor(mContext.getResources().getColor(R.color.cm_text));
            childView.setText(item.Text);
            if (elem.Value.equals(item.Value)) {
                childView.setChecked(true);
            }
            childView.setTag(i);
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < flowLayout.getChildCount(); i++) {
                        CheckBox checkBox = (CheckBox) flowLayout.getChildAt(i);
                        if (Integer.valueOf(v.getTag().toString()) == i) {
                            checkBox.setChecked(true);
                            elem.Value = elem.SelectItems.get(i).Value;
                        } else {
                            checkBox.setChecked(false);
                        }
                    }
                }
            });
            params.rightMargin = DisplayUtil.dip2px(mContext, 5);
            params.topMargin = DisplayUtil.dip2px(mContext, 1);
            params.bottomMargin = DisplayUtil.dip2px(mContext, 1);
            flowLayout.addView(childView, params);
        }
    }

    private void addMultiSelectItems(final FlowLayout flowLayout, final Elements2 elem) {
        try {
            flowLayout.removeAllViews();
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < elem.SelectItems.size(); i++) {
                Elements2.SelectItems item = elem.SelectItems.get(i);
                CheckBox childView = new CheckBox(mContext);
                childView.setButtonDrawable(R.drawable.cm_multi_select0);
                childView.setTextColor(mContext.getResources().getColor(R.color.cm_text));
                childView.setText(item.Text);
                //1&4:0   5&1:1   5&4:4   5&2:0  1|4:5   1|4|8:13
                if (!TextUtils.isEmpty(elem.Value) && (Integer.valueOf(elem.Value) & Integer.valueOf(item.Value)) == Integer.valueOf(item.Value)) {
                    childView.setChecked(true);
                }
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int val = 0;
                        for (int i = 0; i < flowLayout.getChildCount(); i++) {
                            CheckBox checkBox = (CheckBox) flowLayout.getChildAt(i);
                            if (checkBox.isChecked()) {
                                val = val | Integer.valueOf(elem.SelectItems.get(i).Value);
                            }
                        }
                        elem.Value = val > 0 ? String.valueOf(val) : "";
                    }
                });
                params.rightMargin = DisplayUtil.dip2px(mContext, 5);
                params.topMargin = DisplayUtil.dip2px(mContext, 1);
                params.bottomMargin = DisplayUtil.dip2px(mContext, 1);
                flowLayout.addView(childView, params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class MyAdapter extends MultiTableAdapter {

        private final int width;
        private final int height;

        private Form.Elements elements;
        private Map<String,Integer> dataMap; //存放元素ID在集合中的位置索引

        public MyAdapter(Context context) {
            super(context);

            Resources resources = context.getResources();

            width = resources.getDimensionPixelSize(R.dimen.table_width);
            height = resources.getDimensionPixelSize(R.dimen.table_height);

            dataMap = new HashMap<>();
        }

        public void setDataList(Form.Elements dataList) {
            this.elements = dataList;

            if (elements != null && elements.Data != null && elements.Data.Columns != null) {
                for (int i = 0; i < elements.Data.Columns.size(); i++) {
                    dataMap.put(elements.Data.Columns.get(i), i);
                }
            }
        }
        public int getElemIndex(String elemID) {
            return dataMap.get(elemID);
        }

        @Override
        public int getRowCount() {
            int count = 0;
            if (elements != null && elements.Data != null && elements.Data.Rows != null) {
                count = elements.Data.Rows.size();
            }
            Logger.d(TAG,"rowcount:" + count);
            return count;
        }

        @Override
        public int getColumnCount() {
            int count = 0;
            if (elements != null && elements.Elements != null && elements.Elements.size() > 0) {
                count = elements.Elements.size() - 1;
            }
            Logger.d(TAG,"colcount:" + count);
            return count;
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
            if (column == -2) { //获取行ID
                return elements.Data.Rows.get(row).get(0);
            }

            Elements2 elem = elements.Elements.get(column + 1);
            if (elem == null) {
                return "";
            }

            if (row == -1) { //顶部列标题
                StringBuilder title = new StringBuilder(elem.Title);
                if (!elem.IsAllowNull) {
                    title.append("*");
                }
                return title.toString();
            } else {
                ArrayList<String> dRow = elements.Data.Rows.get(row);
                int valIndex = dataMap.get(elem.ID);
                Logger.d(TAG,"getCellString()->valIndex: " + valIndex);
                String dValue = valIndex > dRow.size() -1 ? null : dRow.get(valIndex);
                if(!TextUtils.isEmpty(dValue) && "DropDownList".equalsIgnoreCase(elem.Type)) {
                    for (int i = 0; i < elem.SelectItems.size(); i++) {
                        //获取下拉类型值对应的文本显示
                        if (dValue.equals(elem.SelectItems.get(i).Value)) {
                            dValue = elem.SelectItems.get(i).Text;
                            break;
                        }
                    }
                }
                return dValue == null ? "":dValue;
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

        @Override
        public void onClickCell(final int row, int column) {
            final Elements2 elem = elements.Elements.get(column + 1);
            if ("DropDownList".equalsIgnoreCase(elem.Type)) {
                options.clear();
                final OptionsPickerView optionsPickerView = new OptionsPickerView<>(mContext);
                if (elem.SelectItems != null && elem.SelectItems.size() != 0) {
                    for (int i = 0; i < elem.SelectItems.size(); i++) {
                        options.add(elem.SelectItems.get(i).Text);
                    }
                } else {
                    options.add("");
                }
                optionsPickerView.setPicker(options);
                String value = elements.Data.Rows.get(row).get(dataMap.get(elem.ID));
                if (!TextUtils.isEmpty(value)) {
                    value = value.replace(".0", "");
                    if (TextUtils.isDigitsOnly(value))
                        optionsPickerView.setSelectOptions(Integer.valueOf(value) - 1); //默认选中与单元格值一致
                }
                optionsPickerView.setTitle(elem.Title);
                optionsPickerView.setCancelable(true);
                optionsPickerView.setCyclic(false);
                optionsPickerView.setOnShowListener(FormDetailAdapter.this);
                optionsPickerView.setOnDismissListener(FormDetailAdapter.this);
                optionsPickerView.setOnoptionsSelectListener(new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3) {
                        elements.Data.Rows.get(row).set(dataMap.get(elem.ID), elem.SelectItems.get(options1).Value);
                        notifyDataSetChanged();
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        optionsPickerView.show();
                    }
                }, 200);
            } else if ("DateTimer".equalsIgnoreCase(elem.Type)) {
                TimePickerView timePickerView = new TimePickerView(mContext, Type.YEAR_MONTH_DAY);// 时间选择器
                timePickerView.setCancelable(true);
                timePickerView.setOnShowListener(FormDetailAdapter.this);
                timePickerView.setOnDismissListener(FormDetailAdapter.this);
                timePickerView.setOnTimeSelectListener(new OnTimeSelectListener() { // 时间选择后回调
                    @Override
                    public void onTimeSelect(Date date) {
                        SimpleDateFormat f = new SimpleDateFormat(mContext.getResources().getString(R.string.timeformat2), Locale.CHINA);
                        elements.Data.Rows.get(row).set(dataMap.get(elem.ID), f.format(date));
                        notifyDataSetChanged();
                    }
                });
                timePickerView.show();
            } else if ("TextBox".equalsIgnoreCase(elem.Type)) {
                final Dialog editDialog = new Dialog(mContext, R.style.MaterialDialogSheet);
                View v = LayoutInflater.from(mContext).inflate(R.layout.activity_update_alias, null);
                final EditText contentEt = (EditText) v.findViewById(R.id.cm_input_et_content);
                Button cancelBtn = (Button) v.findViewById(R.id.cm_header_btn_left9);
                Button okBtn = (Button) v.findViewById(R.id.cm_header_tv_right9);
                contentEt.setHint(elem.Title);
                contentEt.setText(getCellString(row, column));
                CharSequence text = contentEt.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable)text;
                    Selection.setSelection(spanText, text.length());
                }
                contentEt.requestFocus();
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HelpUtil.hideSoftInput(mContext, contentEt);
                        editDialog.dismiss();
                    }
                });
                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HelpUtil.hideSoftInput(mContext, contentEt);
                        elements.Data.Rows.get(row).set(dataMap.get(elem.ID), contentEt.getText().toString());
                        notifyDataSetChanged();
                        editDialog.dismiss();
                    }
                });

                editDialog.setCancelable(true);
                editDialog.setCanceledOnTouchOutside(true);
                editDialog.setContentView(v, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                editDialog.show();
            }
        }
    }


    //===OnShowListener ↓=== 防止滑动到最底端，弹出选择器后无法滚动选择问题
    @Override
    public void onShow(Object o) {
        if (mContext instanceof SwipeBackActivity) {
            ((SwipeBackActivity)mContext).getSwipeBackLayout().setEnableGesture(false);
        }
    }
    @Override
    public void onDismiss(Object o) {
        if (mContext instanceof SwipeBackActivity) {
            ((SwipeBackActivity)mContext).getSwipeBackLayout().setEnableGesture(true);
        }
    }
    //===OnDismissListener ↑===
}
