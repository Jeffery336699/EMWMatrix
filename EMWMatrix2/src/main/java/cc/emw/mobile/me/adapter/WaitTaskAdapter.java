package cc.emw.mobile.me.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserFenPai;
import cc.emw.mobile.task.TaskDetailActivity;
import cc.emw.mobile.util.HelpUtil;

public class WaitTaskAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserFenPai> mDataList;
    private boolean flag;

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
                    R.layout.listitem_wait_handle, null);
            vh.iconIv = (ImageView) convertView
                    .findViewById(R.id.iv_waithandle_tag);
            vh.nameTv = (TextView) convertView
                    .findViewById(R.id.tv_waithandle_content);
            vh.finishBtn = (Button) convertView
                    .findViewById(R.id.btn_waithandle_finish);
            vh.sort = (TextView) convertView
                    .findViewById(R.id.tv_waithandle_sort);
            vh.time = (TextView) convertView
                    .findViewById(R.id.tv_waithandle_time);
            vh.tv_state = (TextView) convertView
                    .findViewById(R.id.tv_waithandle_contents);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final UserFenPai waitInfo = mDataList.get(position);
        String times = "";
        String startTime = waitInfo.CreateTime;
        String endTime = waitInfo.FinishTime;
        if (startTime.length() > 10) {
            times = startTime.substring(5, 7) + "月"
                    + startTime.substring(8, 10) + "日";
        }
        Drawable left = mContext.getResources().getDrawable(HelpUtil.getTaskResId(waitInfo.Yxj));
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        vh.nameTv.setCompoundDrawables(left, null, null, null);
        vh.nameTv.setText(waitInfo.Title);
        vh.time.setText(times);
        vh.tv_state.setText("给你安排了新的任务");
        if (EMWApplication.personMap != null
                && EMWApplication.personMap.get(waitInfo.Creator) != null) {
            vh.sort.setText(EMWApplication.personMap.get(waitInfo.Creator).Name);
        }
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TaskDetailActivity.class);
                Bundle args = new Bundle();
                args.putSerializable(TaskDetailActivity.TASK_DETAIL, waitInfo);
                intent.putExtras(args);
                (mContext).startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView iconIv;
        TextView nameTv;
        Button finishBtn;
        TextView sort;
        TextView time;
        TextView tv_state;
    }
}
