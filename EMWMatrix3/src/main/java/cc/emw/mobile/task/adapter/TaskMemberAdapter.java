package cc.emw.mobile.task.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zf.iosdialog.widget.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.task.activity.TaskMemberActivity;
import cc.emw.mobile.task.base.BaseHolder;
import cc.emw.mobile.task.base.SuperBaseAdapter;
import cc.emw.mobile.util.TaskUtils;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.CircleImageView;

/**
 * Created by chengyong.liu on 2016/6/25.
 * 任务人员
 */
public class TaskMemberAdapter extends SuperBaseAdapter<ApiEntity.UserInfo> {
    private Context mContext;
    private List<ApiEntity.UserInfo> mDataList;
    private boolean mCandel = false;
    private List<ApiEntity.UserInfo> mTempList = new ArrayList<>();
    private int mRequestType;


    public TaskMemberAdapter(Context context, boolean canDel) {
        mContext = context;
        mCandel = canDel;
        mDataList = new ArrayList<>();
        this.mDatas=mDataList;
    }
    public void setRequestType(int requestType) {
        mRequestType = requestType;
    }
    public List<ApiEntity.UserInfo> getData() {
        return mDataList;
    }

    @Override
    public void setData(List<ApiEntity.UserInfo> dataList) {
        super.setData(dataList);
        mDataList.clear();
        mDataList.addAll(dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView=vh.mHolderView;
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        ApiEntity.UserInfo data = mDataList.get(position);
        vh.setDataAndRefreshHolderView(position,data);
        return convertView;
    }




    public class ViewHolder extends BaseHolder<ApiEntity.UserInfo> {
        TextView tvName;
        TextView tvDel;
        CircleImageView ivHead;
        View view;

        @Override
        public View initHolderViewAndFindViews() {
            view = LayoutInflater.from(mContext).inflate(R.layout.listitem_task_member, null);
            tvName = (TextView) view.findViewById(R.id.tv_listitem_task_member_name);
            tvDel = (TextView) view.findViewById(R.id.tv_listitem_task_member_icon);
            ivHead = (CircleImageView) view.findViewById(R.id.civ_listitem_task_member_head_image);
            return view;
        }

        @Override
        public void refreshHolderView(int postion,ApiEntity.UserInfo data) {
            tvDel.setVisibility(mCandel ? View.VISIBLE : View.GONE);
            tvDel.setTag(data);
            tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ApiEntity.UserInfo userInfo = (ApiEntity.UserInfo) v.getTag();
                    new AlertDialog(mContext).builder().setMsg("确定删除该成员？")
                            .setPositiveColor(mContext.getResources().getColor(R.color.alertdialog_del_text))
                            .setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mRequestType == -1) {
                                        mTempList.clear();
                                        for (int i = 0; i < mDataList.size(); i++) {
                                            if (mDataList.get(i).ID != userInfo.ID) {
                                                mTempList.add(mDataList.get(i));
                                            }
                                        }
                                        if (((TaskMemberActivity) TaskMemberAdapter.this.mContext)
                                                .getMemberType() == 1) {
                                            if (mTempList.size() == 0) {
                                                ToastUtil.showToast(mContext, "请保留至少一个负责人!");
                                                return;
                                            }
                                        }
                                        ((TaskMemberActivity) mContext).DeleteMember(userInfo, mTempList);
                                    } else {
                                        mDataList.remove(userInfo);
                                        TaskMemberActivity ta = (TaskMemberActivity) TaskMemberAdapter.this.mContext;
                                        ta.getTvMemberNum().setText(mDataList.size() + "人");
                                        notifyDataSetChanged();
                                        if (mDataList.size() == 0) {
                                            ta.getListView().setVisibility(View.GONE);
                                            ta.getMLlBlank().setVisibility(View.VISIBLE);
                                        }
                                    }

                                }
                            }) .setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }
            });
            if(data!=null){
                //设置标题
                tvName.setText(data.Name);
                //设置负责人头像
                ivHead.setTextBg(EMWApplication.getIconColor(data.ID), data.Name, 30);
                TaskUtils.setCivImageView(data.Image, ivHead);
            }
        }
    }
}
/**
 *   被重构好的adapter取代，后续稳定后删除下面预留的原内容
 */
//package cc.emw.mobile.task.adapter;
//
//        import android.content.Context;
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
//        import cc.emw.mobile.task.activity.TaskMemberActivity;
//        import cc.emw.mobile.util.TaskUtils;
//        import cc.emw.mobile.util.ToastUtil;
//
///**
// * Created by chengyong.liu on 2016/6/25.
// */
//public class TaskMemberAdapter extends BaseAdapter {
//
//    private static final String TAG = "TaskMemberAdapter";
//    private Context mContext;
//    private List<ApiEntity.UserInfo> mDataList;
//    private boolean mCandel = false;
//    private List<ApiEntity.UserInfo> mTempList = new ArrayList<>();
//    private int mRequestType;
//
//    public TaskMemberAdapter(Context context, boolean canDel) {
//        mContext = context;
//        mCandel = canDel;
//        mDataList = new ArrayList<>();
//    }
//
//    public void setData(List<ApiEntity.UserInfo> dataList) {
//        mDataList.clear();
//        mDataList.addAll(dataList);
//    }
//
//    public void setRequestType(int requestType) {
//        mRequestType = requestType;
//    }
//
//    public List<ApiEntity.UserInfo> getData() {
//        return mDataList;
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
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder vh;
//        if (convertView == null) {
//            vh = new ViewHolder();
//            convertView = LayoutInflater.from(mContext).inflate(
//                    R.layout.listitem_task_member, null);
//            vh.tvName = (TextView) convertView
//                    .findViewById(R.id.tv_listitem_task_member_name);
//            vh.tvDel = (TextView) convertView
//                    .findViewById(R.id.tv_listitem_task_member_icon);
//            vh.ivHead = (ImageView) convertView
//                    .findViewById(R.id.civ_listitem_task_member_head_image);
//            convertView.setTag(vh);
//        } else {
//            vh = (ViewHolder) convertView.getTag();
//        }
//        // 绑定数据
//        ApiEntity.UserInfo data = mDataList.get(position);
//        vh.tvDel.setVisibility(mCandel ? View.VISIBLE : View.GONE);
//        vh.tvDel.setTag(data);
//        vh.tvDel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final ApiEntity.UserInfo userInfo = (ApiEntity.UserInfo) v.getTag();
//                new AlertDialog(mContext).builder()
//                        .setMsg("确定删除该成员?")
//                        .setPositiveButton(mContext.getString(R.string.ok), new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (mRequestType == -1) {
//                                    mTempList.clear();
////                                Log.d(TAG, new Gson().toJson(mDataList));
//                                    for (int i = 0; i < mDataList.size(); i++) {
//                                        if (mDataList.get(i).ID != userInfo.ID) {
//                                            mTempList.add(mDataList.get(i));
//                                        }
//                                    }
//                                    if (((TaskMemberActivity) zTaskMemberAdapter.this.mContext)
//                                            .getMemberType() == 1) {
//                                        if (mTempList.size() == 0) {
//                                            ToastUtil.showToast(mContext, "请保留至少一个负责人!");
//                                            return;
//                                        }
//                                    }
//                                    ((TaskMemberActivity) mContext).DeleteMember(userInfo, mTempList);
//                                } else {
//                                    mDataList.remove(userInfo);
//                                    TaskMemberActivity ta = (TaskMemberActivity) zTaskMemberAdapter.this.mContext;
//                                    ta.getTvMemberNum().setText(mDataList.size() + "人");
//                                    notifyDataSetChanged();
//                                    if (mDataList.size() == 0) {
//                                        ta.getListView().setVisibility(View.GONE);
//                                        ta.getMLlBlank().setVisibility(View.VISIBLE);
//                                    }
//                                }
//
//                            }
//                        })
//                        .setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                            }
//                        }).show();
//            }
//        });
//        if(data!=null){
//            //设置标题
//            vh.tvName.setText(data.Name);
//            //设置负责人头像
//            TaskUtils.setCivImageView(data.Image, vh.ivHead);
//        }
//        return convertView;
//    }
//
//    class ViewHolder {
//        TextView tvName;
//        TextView tvDel;
//        ImageView ivHead;
//    }
//}
