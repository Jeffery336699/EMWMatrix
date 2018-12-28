package cc.emw.mobile.task.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zf.iosdialog.widget.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.task.activity.TaskCreateActivity;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.task.base.BaseHolder;
import cc.emw.mobile.task.base.SuperBaseAdapter;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.TaskUtils;

/**
 * Created by chengyong.liu on 2016/7/5.
 * 子任务
 */
public class TaskDetailChildAdapter extends SuperBaseAdapter<ApiEntity.UserFenPai> {
    private Context mContext;
    private List<ApiEntity.UserFenPai> mDataList;
    private int mProjectId;
    private int mParentState;//记录父类任务的状态。
    private ApiEntity.UserProject mProject;//从父任务拉取项目中的所有成员和项目信息
    private boolean mCanSee;
    public TaskDetailChildAdapter(Context context,ApiEntity.UserProject project,boolean canSee ) {
        this.mContext = context;
        mDataList=new ArrayList<>();
        this.mDatas=mDataList;
        this.mProject=project;
        this.mCanSee=canSee;
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
        ApiEntity.UserFenPai data = mDataList.get(position);
        vh.setDataAndRefreshHolderView(position, data);
        return convertView;
    }
    @Override
    public void setData(List<ApiEntity.UserFenPai> dataList) {
        super.setData(dataList);
        if (dataList != null) {
            mDataList.clear();
            mDataList.addAll(dataList);
        }
    }
    public List<ApiEntity.UserFenPai> getDataList() {
        return mDataList;
    }
    public void setProjectId(int projectId) {
        mProjectId = projectId;
    }
    public void setParentState(int state) {
        mParentState = state;
    }

    public class ViewHolder extends BaseHolder<ApiEntity.UserFenPai> {
        View view;
        TextView tvTitle;
        ImageView ivIcon;
        @Override
        public View initHolderViewAndFindViews() {
            view = LayoutInflater.from(mContext).inflate(R.layout.listitem_task_detail_child_adapter, null);
            tvTitle = (TextView) view.findViewById(R.id.iv_task_detail_child_title);
            ivIcon = (ImageView) view.findViewById(R.id.iv_task_detail_child_icon);
            return view;
        }
        @Override
        public void refreshHolderView(int postion, final ApiEntity.UserFenPai data) {
            //设置状态图标
            ivIcon.setBackgroundResource(data.State == TaskConstant.TaskState.FINISHED? R.drawable.cm_radio_select_pre : R.drawable.task_icon);
            //设置标题
            tvTitle.setText(data.Title);
            //设置旗帜
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, TaskDetailActivity.class);
                    intent.putExtra(TaskDetailActivity.TASK_DETAIL, data);
                    intent.putExtra(TaskDetailActivity.IS_SHOW_CHILD_TASKS, false);
                    data.ProjectId = mProjectId;
                    intent.putExtra(TaskDetailActivity.PARENT_STATE, mParentState);
                    intent.putExtra(TaskDetailActivity.PARENT_STATE, mParentState);
                    intent.putExtra(TaskCreateActivity.TEAM_USERPROJECT, mProject);
                    intent.putExtra(TaskCreateActivity.WHO_CAN_SEE, mCanSee);
                    intent.putExtra("start_anim", false);
                    mContext.startActivity(intent);
                }
            });
            if (TaskUtils.iSForCharge(data.MainUser) || data.Creator == PrefsUtil.readUserInfo().ID) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog(mContext).builder().setMsg("确定删除该任务？")
                            .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
                            .setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    ((TaskDetailActivity) (mContext)).delTask(data);
                                }
                            }).setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                        }).show();
                        return true;
                    }
                });
            }
        }
    }
}
/**
 *   被重构好的adapter取代，后续稳定后删除下面预留的原内容
 */
//package cc.emw.mobile.task.adapter;
//
//        import android.app.Activity;
//        import android.content.Context;
//        import android.content.Intent;
//        import android.util.Log;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.BaseAdapter;
//        import android.widget.ImageView;
//        import android.widget.TextView;
//
//        import com.google.gson.Gson;
//        import com.zf.iosdialog.widget.AlertDialog;
//
//        import java.util.ArrayList;
//        import java.util.List;
//
//        import cc.emw.mobile.R;
//        import cc.emw.mobile.net.ApiEntity;
//        import cc.emw.mobile.task.activity.TaskDetailActivity;
//        import cc.emw.mobile.task.constant.TaskConstant;
//        import cc.emw.mobile.util.PrefsUtil;
//        import cc.emw.mobile.util.TaskUtils;
//
///**
// * Created by chengyong.liu on 2016/7/5.
// */
//public class TaskDetailChildAdapter extends BaseAdapter {
//    private static final String TAG = "TaskDetailChildAdapter";
//    private Context mContext;
//    private List<ApiEntity.UserFenPai> mDataList;
//    private int mProjectId;
//    private int mParentState;//记录父类任务的状态。
//
//    public TaskDetailChildAdapter(Context context) {
//        mContext = context;
//        mDataList = new ArrayList<>();
//    }
//
//    public void setData(List<ApiEntity.UserFenPai> dataList) {
//        if (dataList != null) {
//            mDataList.clear();
//            mDataList.addAll(dataList);
//        }
//    }
//
//    public List<ApiEntity.UserFenPai> getDataList() {
//        return mDataList;
//    }
//
//    public void setProjectId(int projectId) {
//        mProjectId = projectId;
//    }
//
//    public void setParentState(int state) {
//        mParentState = state;
//    }
//
//    @Override
//    public int getCount() {
//        return mDataList != null ? mDataList.size() : 0;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mDataList != null ? mDataList.get(position) : null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder vh;
//        if (convertView == null) {
//            vh = new ViewHolder();
//            convertView = LayoutInflater.from(mContext).inflate(
//                    R.layout.listitem_task_detail_child_adapter, null);
//            vh.tvTitle = (TextView) convertView
//                    .findViewById(R.id.iv_task_detail_child_title);
//            vh.ivIcon = (ImageView) convertView
//                    .findViewById(R.id.iv_task_detail_child_icon);
//            convertView.setTag(vh);
//        } else {
//            vh = (ViewHolder) convertView.getTag();
//        }
//        // 绑定数据
//        final ApiEntity.UserFenPai data = mDataList.get(position);
//        //设置状态图标
//        vh.ivIcon.setBackgroundResource(data.State == TaskConstant.TaskState.FINISHED
//                ? R.drawable.cm_radio_select_pre : R.drawable.task_icon);
//        //设置标题
//        vh.tvTitle.setText(data.Title);
//        //设置旗帜
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, TaskDetailActivity.class);
//                intent.putExtra(TaskDetailActivity.TASK_DETAIL, data);
////                Log.d(TAG,new Gson().toJson(data));
//                intent.putExtra(TaskDetailActivity.IS_SHOW_CHILD_TASKS, false);
//                data.ProjectId = mProjectId;
//                intent.putExtra(TaskDetailActivity.PARENT_STATE, mParentState);
//                mContext.startActivity(intent);
//                ((Activity)mContext).overridePendingTransition(R.anim.card_activity_open, R.anim.activity_out);
//            }
//        });
//        if (TaskUtils.iSForCharge(data.MainUser) || data.Creator == PrefsUtil.readUserInfo().ID) {
//            convertView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    new AlertDialog(mContext).builder().setMsg("确定删除该任务?")
//                            .setPositiveButton("确定", new View.OnClickListener() {
//
//                                @Override
//                                public void onClick(View v) {
//                                    ((TaskDetailActivity) (mContext)).delTask(data);
//                                }
//                            }).setNegativeButton("取消", new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    }).show();
//                    return true;
//                }
//            });
//        }
//        return convertView;
//    }
//
//    class ViewHolder {
//        TextView tvTitle;
//        ImageView ivIcon;
//    }
//}
