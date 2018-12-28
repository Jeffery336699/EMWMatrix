package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.ApiEntity.UserPlan;
import cc.emw.mobile.view.IconTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;


/**
 * 新建工作计划Adapter
 *
 * @author shaobo.zhuang
 */
public class PlanAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserPlan> mDataList;
    private SwipeBackLayout mSwipeBackLayout;
    private boolean canEdit;

    public PlanAdapter(Context context, ArrayList<UserPlan> dataList) {
        this(context, dataList, true);
    }

    public PlanAdapter(Context context, ArrayList<UserPlan> dataList, boolean canEdit) {
        this.mContext = context;
        this.mDataList = dataList;
        this.canEdit = canEdit;
    }

    public void setSwipeBackLayout(SwipeBackLayout swipeBackLayout) {
        mSwipeBackLayout = swipeBackLayout;
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_plan, null);
            vh.arrowItv = (IconTextView) convertView.findViewById(R.id.workplan_itv_itemarrow);
            vh.delBtn = (IconTextView) convertView.findViewById(R.id.workplan_itv_itemdel);
            vh.delBtn.setOnClickListener(new MyOnClickListener(vh) {
                @Override
                public void onClick(View v, ViewHolder holder) {
                    int position = (Integer) holder.contentEt.getTag();
                    mDataList.remove(position);
                    notifyDataSetChanged();
                }
            });
            vh.nameTv = (TextView) convertView.findViewById(R.id.workallot_tv_itemtask);
            vh.contentEt = (EditText) convertView.findViewById(R.id.workplan_et_itemcontent);
            vh.contentEt.setTag(position);
            vh.contentEt.addTextChangedListener(new MyTextWatcher(vh) {
                @Override
                public void afterTextChanged(Editable s, ViewHolder holder) {
                    int position = (Integer) holder.contentEt.getTag();
                    UserPlan p = mDataList.get(position);
                    p.Name = s.toString();
                    mDataList.set(position, p);
                }
            });
            vh.timeEt = (TextView) convertView.findViewById(R.id.workplan_et_itemtime);
            vh.timeLayout = (LinearLayout) convertView.findViewById(R.id.workplan_ll_itemtime);
//	    	vh.timeSelectBtn = (Button) convertView.findViewById(R.id.workplan_btn_timeselect);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
            vh.contentEt.setTag(position);
        }
        final UserPlan plan = mDataList.get(position);
        vh.nameTv.setText("计划" + (position + 1));
        vh.contentEt.setText(plan.Name);
        vh.timeEt.setText(plan.EndTime);
        if (canEdit) {
            final Calendar now = Calendar.getInstance();
            final DatePickerDialog dpdStart = DatePickerDialog.newInstance(
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                            String mStrStartTime = year + "-" + checkNum(monthOfYear + 1) + "-" + checkNum(dayOfMonth);
                            plan.EndTime = mStrStartTime;
                            vh.timeEt.setText(mStrStartTime);
                        }
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpdStart.setVersion(DatePickerDialog.Version.VERSION_2);
            dpdStart.setAccentColor(mContext.getResources().getColor(R.color.cm_main_text));
            vh.timeLayout.setOnClickListener(new OnClickListener() {//弹出时间选择器
                @Override
                public void onClick(View v) {
                    dpdStart.show(((BaseActivity) mContext).getFragmentManager(), "Datepickerdialog");
                }
            });
        } else {
            vh.contentEt.setEnabled(false);
            vh.arrowItv.setVisibility(View.GONE);
            vh.delBtn.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        IconTextView delBtn, arrowItv;
        TextView nameTv;
        EditText contentEt;
        TextView timeEt;
        LinearLayout timeLayout;
//		Button timeSelectBtn;
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
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

    private abstract class MyOnClickListener implements OnClickListener {

        private ViewHolder mHolder;

        public MyOnClickListener(ViewHolder holder) {
            this.mHolder = holder;
        }

        @Override
        public void onClick(View v) {
            onClick(v, mHolder);
        }

        public abstract void onClick(View v, ViewHolder holder);

    }

    /**
     * 处理时间格式
     *
     * @param num
     * @return
     */
    private String checkNum(int num) {
        if (num >= 10) {
            return num + "";
        } else {
            return "0" + num;
        }
    }
}
