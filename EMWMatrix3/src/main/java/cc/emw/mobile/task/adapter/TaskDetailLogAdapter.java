package cc.emw.mobile.task.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.task.base.BaseHolder;
import cc.emw.mobile.task.base.SuperBaseAdapter;
import cc.emw.mobile.task.util.StringUtil;

/**
 * Created by sunny.du on 2016/9/1.
 * 任务详情日志记录
 */
public class TaskDetailLogAdapter extends SuperBaseAdapter<ApiEntity.TaskReply> {
    private Context mContext;
    private ArrayList<ApiEntity.TaskReply> mDataList;

    public TaskDetailLogAdapter(Context context, ArrayList<ApiEntity.TaskReply> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
        this.mDatas = dataList;

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
        ApiEntity.TaskReply data = mDataList.get(position);
        vh.setDataAndRefreshHolderView(position, data);
        return convertView;
    }


    public class ViewHolder extends BaseHolder<ApiEntity.TaskReply> {
        TextView nameTv;
        TextView timeTv;
        View view;

        @Override
        public View initHolderViewAndFindViews() {
            view = LayoutInflater.from(mContext).inflate(R.layout.listitem_filedetail, null);
            nameTv = (TextView) view.findViewById(R.id.tv_filedetail_logname);
            timeTv = (TextView) view.findViewById(R.id.tv_filedetail_logtime);
            return view;
        }

        @Override
        public void refreshHolderView(int postion, ApiEntity.TaskReply data) {
            nameTv.setText(StringUtil.delHTMLTag(data.Content));
            timeTv.setText(data.CreateTime);
        }
    }
}

/**
 *   被重构好的adapter取代，后续稳定后删除下面预留的原内容
 */
//package cc.emw.mobile.task.adapter;
//
//        import android.content.Context;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.BaseAdapter;
//        import android.widget.TextView;
//
//        import java.util.ArrayList;
//
//        import cc.emw.mobile.R;
//        import cc.emw.mobile.net.ApiEntity.TaskReply;
//
//public class TaskDetailLogAdapter extends BaseAdapter {
//
//    private Context mContext;
//    private ArrayList<TaskReply> mDataList;
//
//    public TaskDetailLogAdapter(Context context,
//                                ArrayList<TaskReply> dataList) {
//        this.mContext = context;
//        this.mDataList = dataList;
//
//    }
//
//    @Override
//    public int getCount() {
//        return mDataList != null ? mDataList.size() : 0;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        final ViewHolder vh;
//        if (convertView == null) {
//            vh = new ViewHolder();
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_filedetail, null);
//            vh.nameTv = (TextView) convertView.findViewById(R.id.tv_filedetail_logname);
//            vh.timeTv = (TextView) convertView.findViewById(R.id.tv_filedetail_logtime);
//
//            convertView.setTag(vh);
//        } else {
//            vh = (ViewHolder) convertView.getTag();
//        }
//        TaskReply data = mDataList.get(position);
//        String s = "";
//        if (data.Content != null) {
//            s = data.Content.replace("<b>", "");
//            s = s.replace("</b>", "");
//        }
//        vh.nameTv.setText(s);
//        vh.timeTv.setText(data.CreateTime);
//
//        return convertView;
//    }
//
//    static class ViewHolder {
//        TextView nameTv;
//        TextView timeTv;
//    }
//
//}
