package cc.emw.mobile.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.StringUtils;

public class WaitTaskAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserFenPai> mDataList;
    private boolean flag;
    private SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat time2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public WaitTaskAdapter(Context context, boolean flag) {
        this.mContext = context;
    }

    public void setData(ArrayList<UserFenPai> mDataList) {
        this.mDataList = mDataList;
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
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_item_relation_task, null);
//            vh.img = (ImageView) convertView
//                    .findViewById(R.id.iv_waithandle_tag);
            vh.content = (TextView) convertView
                    .findViewById(R.id.tv_wait_handle_content);
//            vh.tag = (TextView) convertView
//                    .findViewById(R.id.tv_waithandle_tag);
            vh.time = (TextView) convertView
                    .findViewById(R.id.tv_wait_handle_time);
            vh.principalName = (TextView) convertView.findViewById(R.id.tv_principal_name);
//            vh.waithandleTag = (ImageView) convertView.findViewById(R.id.img_waithandle_tag);
            AnimationSet set = anmi();
            vh.set = set;
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
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
//            if (startTime.length() > 16)
//                sWcal.setTimeInMillis(time.parse(startTime).getTime());
//            else
//                sWcal.setTimeInMillis(time2.parse(startTime).getTime());
//            if (endTime.length() > 16)
//                eWcal.setTimeInMillis(time.parse(endTime).getTime());
//            else
//                eWcal.setTimeInMillis(time2.parse(endTime).getTime());
//            sCal.setTimeInMillis(time.parse(nowTime).getTime());
//            eCal.setTimeInMillis(time.parse(nowEndTime).getTime());
//            if (sWcal.getTimeInMillis() <= eCal.getTimeInMillis() && eWcal.getTimeInMillis() >= sCal.getTimeInMillis()) {
//                vh.waithandleTag.setImageResource(R.drawable.dynamic_circletime);
//            } else {
//                vh.waithandleTag.setImageResource(R.drawable.circle_calendar_other_time_tags);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        vh.img.setImageResource(R.drawable.daiben_renwu);
        String times = StringUtils.friendly_time(startTime);
//        if (startTime.length() > 10) {
//            times = startTime.substring(5, 7) + "月"
//                    + startTime.substring(8, 10) + "日";
//        }
//        Drawable left = mContext.getResources().getDrawable(HelpUtil.getTaskResId(waitInfo.Yxj));
//        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
//        vh.nameTv.setCompoundDrawables(left, null, null, null);
        vh.content.setText(waitInfo.Title);
        if (EMWApplication.personMap != null && EMWApplication.personMap.get(waitInfo.Creator) != null) {
            vh.principalName.setText(EMWApplication.personMap.get(waitInfo.Creator).Name);
        } else {
            vh.principalName.setText("未知");
        }
        vh.time.setText(times);
//        vh.tag.setText("你有一个待办任务");
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.TASK_DETAIL, waitInfo);
                intent.putExtra("start_anim", false);
                (mContext).startActivity(intent);
            }
        });
        convertView.startAnimation(vh.set);
        return convertView;
    }

    static class ViewHolder {
        TextView principalName;
        //        ImageView img;
        TextView content;
        //        TextView tag;
        TextView time;
        //        ImageView waithandleTag;
        AnimationSet set;
    }

    private AnimationSet anmi() {
        // 动画集合
        AnimationSet set = new AnimationSet(false);
        // 缩放动画
        ScaleAnimation scale = new ScaleAnimation(0.9f, 1, 0.9f, 1,
                Animation.RELATIVE_TO_SELF, 0.9f, Animation.RELATIVE_TO_SELF,
                0.9f);
        scale.setDuration(800);// 动画时间
        scale.setFillAfter(true);// 保持动画状态
        // 渐变动画
        AlphaAnimation alpha = new AlphaAnimation(0.6f, 1);
        alpha.setDuration(800);// 动画时间
        alpha.setFillAfter(true);// 保持动画状态
        set.addAnimation(scale);
        set.addAnimation(alpha);
        return set;
    }
}
