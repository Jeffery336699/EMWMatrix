package cc.emw.mobile.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.StringUtils;

public class WaitTaskAdapter2 extends RecyclerView.Adapter<WaitTaskAdapter2.ViewHolder> {

    private Context mContext;
    private ArrayList<UserFenPai> mDataList;
    private SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public WaitTaskAdapter2(Context context) {
        this.mContext = context;
    }

    public void setData(ArrayList<UserFenPai> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_relation_task, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserFenPai waitInfo = mDataList.get(position);
        String startTime = waitInfo.CreateTime;

//        String endTime = waitInfo.FinishTime;
//        Calendar sCal = Calendar.getInstance();
//        Calendar eCal = Calendar.getInstance();
//        Calendar sWcal = Calendar.getInstance();
//        Calendar eWcal = Calendar.getInstance();
//        String nowMonth = "";
//        int month = sCal.get(Calendar.MONTH) + 1;
//        if (month < 10) {
//            nowMonth = "0" + month;
//        } else {
//            nowMonth = month + "";
//        }
//        String nowTime = sCal.get(Calendar.YEAR) + "-" + nowMonth + "-" + sCal.get(Calendar.DAY_OF_MONTH) + " " + "00:00:00";
//        String nowEndTime = sCal.get(Calendar.YEAR) + "-" + nowMonth + "-" + sCal.get(Calendar.DAY_OF_MONTH) + " " + "23:59:59";
//        try {
//            sWcal.setTimeInMillis(time.parse(startTime).getTime());
//            eWcal.setTimeInMillis(time.parse(endTime).getTime());
//            sCal.setTimeInMillis(time.parse(nowTime).getTime());
//            eCal.setTimeInMillis(time.parse(nowEndTime).getTime());
//            if (sWcal.getTimeInMillis() <= eCal.getTimeInMillis() && eWcal.getTimeInMillis() >= sCal.getTimeInMillis()) {
//                holder.waithandleTag.setImageResource(R.drawable.dynamic_circletime);
//            } else {
//                holder.waithandleTag.setImageResource(R.drawable.circle_calendar_other_time_tags);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        holder.img.setImageResource(R.drawable.daiban_jihua);

        String times = StringUtils.friendly_time(startTime);
        holder.content.setText(waitInfo.Title);
        holder.time.setText(times);
        if (EMWApplication.personMap != null && EMWApplication.personMap.get(waitInfo.Creator) != null) {
            holder.principalName.setText(EMWApplication.personMap.get(waitInfo.Creator).Name);
        } else {
            holder.principalName.setText("未知");
        }
//        holder.tag.setText("你有一个待办任务");
        holder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.TASK_DETAIL, waitInfo);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                intent.putExtra("click_pos_y", location[1]);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        //        private ImageView img;
        private TextView content;
        private TextView principalName;
        //        private TextView tag;
        private TextView time;
//        private ImageView waithandleTag;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
//            img = (ImageView) itemView.findViewById(R.id.iv_waithandle_tag);
            content = (TextView) itemView.findViewById(R.id.tv_wait_handle_content);
//            tag = (TextView) itemView.findViewById(R.id.tv_waithandle_tag);
            time = (TextView) itemView.findViewById(R.id.tv_wait_handle_time);
            principalName = (TextView) itemView.findViewById(R.id.tv_principal_name);
//            waithandleTag = (ImageView) itemView.findViewById(R.id.img_waithandle_tag);
        }

    }


}
