package cc.emw.mobile.task.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.task.base.BaseHolder;
import cc.emw.mobile.task.base.SuperBaseAdapter;
import cc.emw.mobile.view.IconTextView;

/**
 * 项目选项ADAPTER
 */
public class WorkProjectAdapter extends SuperBaseAdapter<UserProject> {
    private Context mContext;//上下文
    private List<UserProject> mDataList;//保存数据的集合
    private int clickPosition = -1;// 点击的条目位置，默认为哪都没点
    private int mProjectID;//当前选中的项目圈ID

    /**
     * 构造函数
     * @param context 上下文
     * @param projectId 目前选中的项目id
     */
    public WorkProjectAdapter(Context context, int projectId) {
        mProjectID = projectId;
        this.mContext = context;
        mDataList = new ArrayList<>();
        this.mDatas = mDataList;
        //过滤数据
    }

    @Override
    public void setData(List<UserProject> dataList) {
        super.setData(dataList);
        if (dataList != null) {
            mDataList.clear();
            mDataList.addAll(dataList);
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).ID == mProjectID) {
                    clickPosition = i;
                }
            }
        }
    }

    /**
     * @des 获取选中的项目位置，-1表示都没选择。
     * @cal
     */
    public ApiEntity.UserProject getSelectProjecd() {
        if (clickPosition != -1) {
            return mDataList.get(clickPosition);
        }
        return new ApiEntity.UserProject();
    }
    /**
     * @des   返回给activity当前选中的项目名称
     * @cal   提交创建任务的时候
     */
    public int getClickPosition() {
        return clickPosition;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView=vh.mHolderView;
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.setDataAndRefreshHolderView(position, mDataList.get(position));
        return convertView;
    }



    public class ViewHolder extends BaseHolder<UserProject> {
        View VIcon;
        TextView tvName;
        IconTextView itvGouGou;
        View v;
        @Override
        public View initHolderViewAndFindViews() {
            v = LayoutInflater.from(mContext).inflate(R.layout.listitem_project, null);
            itvGouGou = (IconTextView) v.findViewById(R.id.right_icon);
            tvName = (TextView) v .findViewById(R.id.project_name_tv);
            VIcon = v .findViewById(R.id.left_stripe);
            return v;
        }

        @Override
        public void refreshHolderView(final int position, ApiEntity.UserProject data) {
            LinearLayout.LayoutParams parmas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            tvName.setText(data.Name);
            if (data.Color == 0) {
                tvName.setTextColor(mContext.getResources().getColor(R.color.cm_text));
                VIcon.setVisibility(View.INVISIBLE);
            } else if (data.Color > 0 && data.Color < 6) {
                VIcon.setVisibility(View.VISIBLE);
                VIcon.setBackgroundResource(CommonUtil.getProjectColor(data.Color));
                tvName.setTextColor(mContext.getResources().getColor(CommonUtil.getProjectColor(data.Color)));
            } else {
                tvName.setTextColor(mContext.getResources().getColor(R.color.cm_text));
                VIcon.setVisibility(View.INVISIBLE);
            }
            itvGouGou.setTextColor(mContext.getResources().getColor(R.color.task_group_finish_text));
            itvGouGou.setIconText("e924");
            parmas.gravity = Gravity.CENTER_VERTICAL;
            itvGouGou.setLayoutParams(parmas);
            itvGouGou.setTextSize(20);
            itvGouGou.setVisibility(position == clickPosition ? View.VISIBLE: View.INVISIBLE);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickPosition == position) {
                        clickPosition = -1;
                    } else {
                        clickPosition = position;
                    }
                    notifyDataSetChanged();
                }
            });
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
//        import android.view.Gravity;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.View.OnClickListener;
//        import android.view.ViewGroup;
//        import android.widget.BaseAdapter;
//        import android.widget.ImageView;
//        import android.widget.LinearLayout;
//        import android.widget.TextView;
//
//        import java.util.ArrayList;
//        import java.util.List;
//
//        import cc.emw.mobile.EMWApplication;
//        import cc.emw.mobile.R;
//        import cc.emw.mobile.net.ApiEntity.UserProject;
//        import cc.emw.mobile.project.Util.CommonUtil;
//        import cc.emw.mobile.util.DisplayUtil;
//        import cc.emw.mobile.view.IconTextView;
//
//public class WorkProjectAdapter extends BaseAdapter {
//    LinearLayout.LayoutParams parmas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT);
//    private Context mContext;
//    private List<UserProject> mDataList;
//    private int clickPosition = -1;// 点击的条目位置，默认为哪都没点
//    private int mProjectID;
//
//    public WorkProjectAdapter(Context context, int projectID) {
//        mProjectID = projectID;
//        this.mContext = context;
//        mDataList = new ArrayList<UserProject>();
//    }
//
//    public void setData(List<UserProject> dataList) {
//        if (dataList != null) {
//            mDataList.clear();
//            mDataList.addAll(dataList);
//            for (int i = 0; i < mDataList.size(); i++) {
//                if (mDataList.get(i).ID == mProjectID) {
//                    clickPosition = i;
//                }
//            }
//        }
//    }
//
//    /**
//     * 获取选中的项目位置，-1表示都没选择。
//     *
//     * @return
//     */
//    public UserProject getSelectProjecd() {
//        if (clickPosition != -1) {
//            return mDataList.get(clickPosition);
//        }
//        return new UserProject();
//    }
//
//    public int getClickPosition() {
//        return clickPosition;
//    }
//
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
//                    R.layout.listitem_project, null);
//            vh.itvGouGou = (IconTextView) convertView
//                    .findViewById(R.id.right_icon);
//            vh.tvName = (TextView) convertView
//                    .findViewById(R.id.project_name_tv);
//            vh.VIcon = convertView
//                    .findViewById(R.id.left_stripe);
//
//            convertView.setTag(vh);
//        } else {
//            vh = (ViewHolder) convertView.getTag();
//        }
//        // 绑定数据
//        UserProject data = mDataList.get(position);
//        vh.tvName.setText(data.Name);
//        if (data.Color == 0) {
//            vh.tvName.setTextColor(mContext.getResources().getColor(R.color.cm_text));
//            vh.VIcon.setVisibility(View.INVISIBLE);
//        } else if (data.Color > 0 && data.Color < 6) {
//            vh.VIcon.setVisibility(View.VISIBLE);
//            vh.VIcon.setBackgroundResource(CommonUtil.getProjectColor(data.Color));
//            vh.tvName.setTextColor(mContext.getResources().getColor(CommonUtil.getProjectColor(data.Color)));
//        } else {
//            vh.tvName.setTextColor(mContext.getResources().getColor(R.color.cm_text));
//            vh.VIcon.setVisibility(View.INVISIBLE);
//        }
//        vh.itvGouGou.setTextColor(mContext.getResources().getColor(R.color.task_group_finish_text));
//        vh.itvGouGou.setIconText("e924");
//        parmas.gravity = Gravity.CENTER_VERTICAL;
//        vh.itvGouGou.setLayoutParams(parmas);
//        vh.itvGouGou.setTextSize(20);
//        vh.itvGouGou.setVisibility(position == clickPosition ? View.VISIBLE
//                : View.INVISIBLE);
//        convertView.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (clickPosition == position) {
//                    clickPosition = -1;
//                } else {
//                    clickPosition = position;
//                }
//                notifyDataSetChanged();
//            }
//        });
//        return convertView;
//    }
//
//    static class ViewHolder {
//        View VIcon;
//        TextView tvName;
//        IconTextView itvGouGou;
//    }
//}
