package cc.emw.mobile.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.util.StringUtils;

public class RecyclerCalendarAdapter extends RecyclerView.Adapter<RecyclerCalendarAdapter.MyViewHolder> {

    private Context mContext;
    private List<CalendarInfo> mDataList;

    public RecyclerCalendarAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<CalendarInfo> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_main, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (mDataList != null && mDataList.size() > 0) {
            final CalendarInfo mCal = mDataList.get(position);
            holder.tvCalendarName.setText(mCal.Title);
            holder.tvCalendarScan.setText(TextUtils.isEmpty(mCal.Remark) ? "暂无描述" : mCal.Remark);
            holder.tvCalendarTime.setText(StringUtils.friendly_time(mCal.StartTime));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,
                            CalendarInfoActivity2.class);
                    intent.putExtra("calendarInfo", mCal);
                    intent.putExtra("start_anim", false);
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    intent.putExtra("click_pos_y", location[1]);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCalendarName, tvCalendarScan, tvCalendarTime;

        public MyViewHolder(View view) {
            super(view);
            tvCalendarName = (TextView) view.findViewById(R.id.tv_calendar_name);
            tvCalendarScan = (TextView) view.findViewById(R.id.tv_calendar_scan);
            tvCalendarTime = (TextView) view.findViewById(R.id.tv_calendar_time);
        }
    }
}