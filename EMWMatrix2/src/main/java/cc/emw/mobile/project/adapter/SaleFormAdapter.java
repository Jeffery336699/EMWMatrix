package cc.emw.mobile.project.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.OptionsPickerView.OnOptionsSelectListener;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.TimePickerView.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePickerView.Type;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cc.emw.mobile.R;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.entities.CommonConsts;
import cc.emw.mobile.project.entities.Elements2;
import cc.emw.mobile.project.entities.Form;
import cc.emw.mobile.project.entities.SelectItems;
import cc.emw.mobile.project.view.FormMultiSelectActivity;
import cc.emw.mobile.project.view.MultiSelectTextView;
import cc.emw.mobile.project.view.SaleFormActivity;
import cc.emw.mobile.project.view.SearchActivity;
import cc.emw.mobile.util.HelpUtil;
import cc.emw.mobile.view.FlowLayout;

public class SaleFormAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "SaleFormAdapter";
    private Context mContext;
    private Form mForm;
    private OptionsPickerView<String> mOptionsPickerView;
    private TimePickerView mStartPopupWindow;
    private String pickerTitle = "PickerTitle";
    private ArrayList<String> sorts;
    private int groupPot;
    private int childPot;
    private String timePickerTitle;
    private int groupIndex;
    private int chileIndex;

    public SaleFormAdapter(Context context, Form form) {
        this.mContext = context;
        this.mForm = form;
        initOption();
        initTimePicker();
    }

    public void setForm(Form form) {
        this.mForm = form;
    }

    @Override
    public int getGroupCount() {
        return mForm.Navigations == null ? 0 : mForm.Navigations.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mForm.Elements.get(groupPosition).Elements == null ? 0
                : mForm.Elements.get(groupPosition).Elements.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mForm.Navigations == null ? null : mForm.Navigations
                .get(groupPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        TextView tView;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.listitem_saleform_navigations, parent, false);
            tView = (TextView) convertView
                    .findViewById(R.id.tv_navigations_name);
            convertView.setTag(tView);
        } else {
            tView = (TextView) convertView.getTag();
        }
        tView.setText(mForm.Navigations.get(groupPosition).Name);
        Drawable left = mContext.getResources().getDrawable(
                isExpanded ? R.drawable.w : R.drawable.r);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        tView.setCompoundDrawables(left, null, null, null);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.listitem_saleform_element, parent, false);
            vh.name = (TextView) convertView.findViewById(R.id.tv_element_name);
            vh.requireIcon = (TextView) convertView
                    .findViewById(R.id.iv_element_requireIcon);
            vh.input = (EditText) convertView
                    .findViewById(R.id.et_element_input);
            vh.tvInput = (TextView) convertView
                    .findViewById(R.id.tv_element_input);
            vh.llContent = (LinearLayout) convertView
                    .findViewById(R.id.ll_element_content);
            vh.typeIcon = (ImageView) convertView
                    .findViewById(R.id.iv_element_typeicon);
            vh.multiSelects = (FlowLayout) convertView
                    .findViewById(R.id.multi_select_fl);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Elements2 element = mForm.Elements.get(groupPosition).Elements
                .get(childPosition);
        vh.id = element.ID;
        vh.name.setText(element.Title + ":");
        vh.requireIcon.setVisibility(element.IsAllowNull ? View.INVISIBLE : View.VISIBLE);
        vh.input.setTag(R.id.tag_first, groupPosition);
        vh.input.setTag(R.id.tag_second, childPosition);
        vh.input.addTextChangedListener(new MyTextWatcher(vh) {
            @Override
            public void afterTextChanged(Editable s, ViewHolder holder) {
                int gpos = (Integer) holder.input.getTag(R.id.tag_first);
                int cpos = (Integer) holder.input.getTag(R.id.tag_second);
                Elements2 e = mForm.Elements.get(gpos).Elements.get(cpos);
                e.Value = s.toString();
                mForm.Elements.get(gpos).Elements.set(cpos, e);
            }
        });
        switch (CommonUtil.getElementType(element.Type)) {
            case CommonConsts.ElementType.TEXTBOX:
                vh.input.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_UP){
                            groupIndex = groupPosition;
                            chileIndex = childPosition;
                        }
                        return false;
                    }
                });
                vh.input.setVisibility(View.VISIBLE);
                if(element.Length > 0) {
                    vh.input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(element.Length)});
                }
                vh.input.setText(element.Value);
                vh.llContent.setVisibility(View.GONE);
                vh.input.clearFocus();
                if(groupIndex == groupPosition && chileIndex == childPosition){
                    vh.input.requestFocus();
                    if(!TextUtils.isEmpty(vh.input.getText().toString())) {
                        vh.input.setSelection(vh.input.getText().length());
                    }
                }
                break;
            case CommonConsts.ElementType.DATETIMER:
                vh.input.setVisibility(View.GONE);
                vh.llContent.setVisibility(View.VISIBLE);
                vh.tvInput.setVisibility(View.VISIBLE);
                vh.multiSelects.setVisibility(View.GONE);
                vh.tvInput.setText(element.Value);
                vh.name.setTag(element);
                vh.typeIcon.setImageResource(R.drawable.q);
                vh.llContent.setOnClickListener(new MyOnclickListiner(vh) {
                    @Override
                    public void onClick(View v, ViewHolder holder) {
                        timePickerTitle = ((Elements2) holder.name.getTag()).Title;
                        mStartPopupWindow.setTitle(timePickerTitle);
                        groupPot = groupPosition;
                        childPot = childPosition;
                        mStartPopupWindow.show();
                    }
                });
                break;
            case CommonConsts.ElementType.RADIOBUTTON:
            case CommonConsts.ElementType.DROPDOWNLIST:
                vh.input.setVisibility(View.GONE);
                vh.llContent.setVisibility(View.VISIBLE);
                vh.tvInput.setVisibility(View.VISIBLE);
                vh.multiSelects.setVisibility(View.GONE);
                if (TextUtils.isDigitsOnly(element.Value) && !TextUtils.isEmpty(element.Value)) {
                    for (int i = 0; i < element.SelectItems.size(); i++) {
                        if (element.Value.equals(element.SelectItems.get(i).Value)) {
                            vh.tvInput.setText(element.SelectItems.get(i).Text);
                            break;
                        }
                    }
                }
                vh.name.setTag(element);
                vh.typeIcon.setImageResource(R.drawable.w);
                vh.llContent.setOnClickListener(new MyOnclickListiner(vh) {
                    @Override
                    public void onClick(View v, ViewHolder holder) {
                        HelpUtil.hideSoftInput(mContext, v);
                        Elements2 e = (Elements2) holder.name.getTag();
                        sorts.clear();
                        if (e.SelectItems != null && e.SelectItems.size() != 0) {
                            for (int i = 0; i < e.SelectItems.size(); i++) {
                                sorts.add(e.SelectItems.get(i).Text);
                            }
                        } else {
                            sorts.add("");
                        }
                        mOptionsPickerView.setPicker(sorts);
                        if (TextUtils.isDigitsOnly(e.Value) && !TextUtils.isEmpty(e.Value)) {
                            int sVal = Integer.valueOf(e.Value);
                            mOptionsPickerView.setSelectOptions(sVal - 1);
                        }
                        mOptionsPickerView.setTitle(e.Title);
                        groupPot = groupPosition;
                        childPot = childPosition;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mOptionsPickerView.show();
                            }
                        }, 200);
                    }
                });
                break;
            case CommonConsts.ElementType.SEARCHER:
                vh.input.setVisibility(View.GONE);
                vh.llContent.setVisibility(View.VISIBLE);
                vh.tvInput.setVisibility(View.VISIBLE);
                vh.multiSelects.setVisibility(View.GONE);
                vh.tvInput.setText(element.Text);
                vh.name.setTag(element);
                vh.typeIcon.setImageResource(R.drawable.r);
                vh.llContent.setOnClickListener(new MyOnclickListiner(vh) {
                    @Override
                    public void onClick(View v, ViewHolder holder) {
                        Intent intent = new Intent(mContext, SearchActivity.class);
                        intent.putExtra("elem", (Elements2) holder.name.getTag());
                        intent.putExtra("page_id", mForm.ID);
                        intent.putExtra("group_position", groupPosition);
                        intent.putExtra("child_position", childPosition);
                        ((Activity) mContext).startActivityForResult(intent,
                                SaleFormActivity.SEARCH_REQUEST_CODE);
                    }
                });
                break;
            case CommonConsts.ElementType.TIMESELECTOR:
                vh.input.setVisibility(View.GONE);
                vh.llContent.setVisibility(View.VISIBLE);
                vh.tvInput.setVisibility(View.VISIBLE);
                vh.multiSelects.setVisibility(View.GONE);
                vh.tvInput.setText(element.Text);
                vh.name.setTag(element);
                vh.typeIcon.setImageResource(R.drawable.r);
                vh.llContent.setOnClickListener(new MyOnclickListiner(vh) {
                    @Override
                    public void onClick(View v, ViewHolder holder) {
                    }
                });
                break;
            case CommonConsts.ElementType.CHECKBOX:
                vh.input.setVisibility(View.GONE);
                vh.llContent.setVisibility(View.VISIBLE);
                vh.tvInput.setVisibility(View.GONE);
                vh.name.setTag(element);
                vh.typeIcon.setImageResource(R.drawable.r);
                vh.multiSelects.setVisibility(View.VISIBLE);
                addMultiSelectItems(vh.multiSelects, element);
                vh.llContent.setOnClickListener(new MyOnclickListiner(vh) {
                    @Override
                    public void onClick(View v, ViewHolder holder) {
                        final Elements2 temElement = (Elements2) holder.name.getTag();
                        Intent intent = new Intent(mContext, FormMultiSelectActivity.class);
                        intent.putExtra("elem", (Elements2) holder.name.getTag());
                        intent.putExtra("group_position", groupPosition);
                        intent.putExtra("child_position", childPosition);
                        ((Activity) mContext).startActivityForResult(intent,
                                SaleFormActivity.CHECKBOX_REQUEST_CODE);
                    }
                });
                break;
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    class ViewHolder {
        TextView name;
        TextView requireIcon;
        EditText input;
        TextView tvInput;
        LinearLayout llContent;
        ImageView typeIcon;
        FlowLayout multiSelects;
        String id;
    }

    private void initOption() {
        // 紧急程度滚轴
        sorts = new ArrayList<String>();
        sorts.add("普通");
        sorts.add("紧急");
        sorts.add("非常紧急");
        mOptionsPickerView = new OptionsPickerView<String>(mContext);
        mOptionsPickerView.setPicker(sorts);
        mOptionsPickerView.setTitle(pickerTitle);
        mOptionsPickerView.setCancelable(true);
        mOptionsPickerView.setCyclic(false);// 无限循环
        mOptionsPickerView
                .setOnoptionsSelectListener(new OnOptionsSelectListener() {

                    @Override
                    public void onOptionsSelect(int options1, int option2,
                                                int options3) {
                        Elements2 elements2 = mForm.Elements.get(groupPot).Elements
                                .get(childPot);
                        elements2.Value = elements2.SelectItems.get(options1).Value;
                        notifyDataSetChanged();
                    }
                });
    }

    private void initTimePicker() {
        mStartPopupWindow = new TimePickerView(mContext, Type.ALL);// 时间选择器
        mStartPopupWindow.setCancelable(true);
        mStartPopupWindow.setOnTimeSelectListener(new OnTimeSelectListener() { // 时间选择后回调
            @Override
            public void onTimeSelect(Date date) {
                dateDiaplay(date);
            }
        });
    }

    // 设置时间显示
    private void dateDiaplay(Date date) {
        SimpleDateFormat f = new SimpleDateFormat(
                mContext.getResources().getString(R.string.timeformat2),
                Locale.CHINA);
        mForm.Elements.get(groupPot).Elements
                .get(childPot).Value = f.format(date);
        notifyDataSetChanged();
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

    private void addMultiSelectItems(final FlowLayout flowLayout, final Elements2 elem) {
        flowLayout.removeAllViews();
        final ArrayList<SelectItems> items = new ArrayList<>();
        String[] strings = elem.Value.split(",");
        for (int i = 0; i < elem.SelectItems.size(); i++) {
            for (int j = 0; j < strings.length; j++) {
                String val = elem.SelectItems.get(i).Value;
                if (val.equals(strings[j])) {
                    items.add(elem.SelectItems.get(i));
                    break;
                }
            }
        }
        for (int i = 0; i < items.size(); i++) {
            MultiSelectTextView childView = new MultiSelectTextView(mContext);
            childView.setTag(items.get(i));
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flowLayout.removeView(v);
                    items.remove(v.getTag());
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        builder.append(items.get(i).Value).append(",");
                        String string = builder.toString();
                        if (!TextUtils.isEmpty(string)) {
                            string = string.substring(0, string.lastIndexOf(","));
                        }
                        elem.Value = string;
                    }
                }
            });
            childView.setText(items.get(i).Text);
            flowLayout.addView(childView);
        }
    }
}
