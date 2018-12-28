package cc.emw.mobile.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseHolder;
import cc.emw.mobile.base.MyBaseAdapter;
import cc.emw.mobile.project.bean.GroupViewBean;
import cc.emw.mobile.view.IconTextView;

/**
 * 团队列表适配器
 * Created by jven.wu on 2016/6/29.
 */
public class TeamListAdapter extends MyBaseAdapter<GroupViewBean> {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;
    private ArrayList<GroupViewBean> teams = new ArrayList<>();
    private int selPosition = -1;
    private int teamId = -1;
    private ViewGroup viewGroup;

    public TeamListAdapter(Context context) {
        this.mContext = context;
        this.mDatas = teams;
    }

    @Override
    public void setData(List<GroupViewBean> dataList) {
        super.setData(dataList);
        if(dataList != null){
            teams.clear();
            teams.addAll(dataList);
        }
    }

    public int getSelPosition(){
        return selPosition;
    }

    public void setSelTeamById(int id){
        teamId = id;
    }

    @Override
    public long getItemId(int position) {
        return teams.get(position).GroupId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        viewGroup = parent;
        ViewHolder vh;
        if(convertView == null){
            vh = new ViewHolder();
            convertView = vh.mHolderView;
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        GroupViewBean team = teams.get(position);
        vh.setDataAndRefreshHolderView(position,team);
        return convertView;
    }

    class ViewHolder extends BaseHolder<GroupViewBean>{
        View view;
        TextView projectName;
        IconTextView tickIcon;

        /**
         * 设置页面数据显示
         * @param position
         * @param data
         */
        @Override
        public void refreshHolderView(final int position, GroupViewBean data) {
            projectName.setText(data.GroupName + "");
            if((selPosition == position && selPosition > -1)
                    || teamId == data.GroupId){
                tickIcon.setVisibility(View.VISIBLE);
                selPosition = position;
            }else {
                tickIcon.setVisibility(View.INVISIBLE);
            }
            mHolderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teamId = -1;
                    selPosition = position;
                    notifyDataSetChanged();
                }
            });
        }

        /**
         * 初始化和绑定页面控件
         * @return
         */
        @Override
        public View initHolderViewAndFindViews() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.listitem_team_list,viewGroup,false);
            projectName = (TextView)view.findViewById(R.id.team_name_tv);
            tickIcon = (IconTextView)view.findViewById(R.id.tick_icon);
            return view;
        }
    }
}
