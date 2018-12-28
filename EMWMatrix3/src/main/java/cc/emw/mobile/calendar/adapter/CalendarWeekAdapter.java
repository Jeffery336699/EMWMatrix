package cc.emw.mobile.calendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.EventGroup;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

/**
 * Created by ${zrjt} on 2016/6/24.
 */
public class CalendarWeekAdapter extends BaseExpandableListAdapter {

    private List<EventGroup> eventGroups;

    private Context mContext;

    private SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DisplayImageOptions options;

    public CalendarWeekAdapter(Context context) {
        mContext = context;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public int lastPosition = -1;

    public int groupPositions;

    public void setGroupData(List<EventGroup> eventGroups) {
        this.eventGroups = eventGroups;
    }

    @Override
    public int getGroupCount() {
        return eventGroups == null ? 0 : eventGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return eventGroups == null ? 0 : eventGroups.get(groupPosition).mDataList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
        groupPositions = groupPosition;
        GroupViewHolder gHolder = null;
        if (convertView == null) {
            gHolder = new GroupViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.elv_listitem_calendar_week, parent, false);
            gHolder.iconTextView = (IconTextView) convertView.findViewById(R.id.ic_tv_dir);
            gHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_week_group_time);
            gHolder.num = (TextView) convertView.findViewById(R.id.tv_week_group_num);
            gHolder.tag = convertView.findViewById(R.id.view_time_tag);
            AnimationSet set = anmi();
            gHolder.set = set;
            convertView.setTag(gHolder);
        } else {
            gHolder = (GroupViewHolder) convertView.getTag();
        }

        EventGroup eventGroup = eventGroups.get(groupPosition);
        gHolder.tvTime.setText(eventGroup.time);
        Calendar calendar = Calendar.getInstance();
        String nowTime = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
        if (eventGroup.time != null && eventGroup.time.equals(nowTime)) {
            gHolder.tag.setBackgroundResource(R.drawable.dynamic_circletime);
        } else {
            gHolder.tag.setBackgroundResource(R.drawable.circle_calendar_other_time_tags);
        }
        gHolder.num.setText(eventGroup.num + "");
        gHolder.iconTextView.setIconText(isExpanded ? "eb67" : "eb6a");
        if (groupPosition == lastPosition)
            convertView.startAnimation(gHolder.set);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastPosition == groupPosition) {
                    lastPosition = -1;
                } else {
                    lastPosition = groupPosition;
                }
                if (isExpanded) {
                    ((ExpandableListView) parent).collapseGroup(groupPosition);
                } else {
                    ((ExpandableListView) parent).expandGroup(groupPosition);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder cHolder = null;
        if (convertView == null) {
            cHolder = new ChildViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.elv_child_listitem_calendar, parent, false);
            cHolder.cTitle = (TextView) convertView.findViewById(R.id.tv_calendar_content);
            cHolder.cTime = (TextView) convertView.findViewById(R.id.tv_calendar_week_time);
            cHolder.cTag = (TextView) convertView.findViewById(R.id.tv_calendar_tag);
            cHolder.cIv = (CircleImageView) convertView.findViewById(R.id.iv_me_head);
            AnimationSet set = anmi();
            cHolder.set = set;
            convertView.setTag(cHolder);
        } else {
            cHolder = (ChildViewHolder) convertView.getTag();
        }
        final CalendarInfo calendarInfo = eventGroups.get(groupPosition).mDataList.get(childPosition);
        cHolder.cTitle.setText(calendarInfo.Title);
        cHolder.cIv.setVisibility(View.GONE);
        Calendar sCal = Calendar.getInstance();
        Calendar eCal = Calendar.getInstance();
        try {
            sCal.setTimeInMillis(time.parse(calendarInfo.StartTime).getTime());
            eCal.setTimeInMillis(time.parse(calendarInfo.OverTime).getTime());
            int sDay = sCal.get(Calendar.DAY_OF_MONTH);
            int eDay = eCal.get(Calendar.DAY_OF_MONTH);
            int sMonth = sCal.get(Calendar.MONTH) + 1;
            int eMonth = eCal.get(Calendar.MONTH) + 1;
            if (sDay == eDay) {
                cHolder.cTime.setText(calendarInfo.StartTime.substring(calendarInfo.StartTime.length() - 8, calendarInfo.StartTime.length() - 3) + "-"
                        + calendarInfo.OverTime.substring(calendarInfo.OverTime.length() - 8, calendarInfo.OverTime.length() - 3));
            } else {
                cHolder.cTime.setText(sMonth + "/" + sDay + "-" + eMonth + "/" + eDay);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(calendarInfo.Label)) {
            if (calendarInfo.Label.contains("#")) {
                cHolder.cTag.setText(calendarInfo.Label);
            } else {
                cHolder.cTag.setText("#" + calendarInfo.Label);
            }
        }
        if (calendarInfo.Label != null && calendarInfo.Label.equals("拜访")) {
            cHolder.cTag.setText("#拜访");
            cHolder.cIv.setVisibility(View.VISIBLE);
            String uri = String.format(Const.DOWN_ICON_URL,
                    PrefsUtil.readUserInfo().CompanyCode,
                    PrefsUtil.readUserInfo().Image);
            ImageLoader.getInstance().displayImage(uri, cHolder.cIv, options);
        }
        convertView.startAnimation(cHolder.set);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CalendarInfoActivity2.class);
                intent.putExtra("calendarInfo", calendarInfo);
                intent.putExtra("start_anim", false);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupViewHolder {
        TextView tvTime;
        TextView num;
        AnimationSet set;
        View tag;
        private IconTextView iconTextView;
    }

    class ChildViewHolder {
        AnimationSet set;
        TextView cTitle;
        TextView cTime;
        TextView cTag;
        CircleImageView cIv;
    }

    private AnimationSet anmi() {
        // 动画集合
        AnimationSet set = new AnimationSet(false);
        // 缩放动画
        ScaleAnimation scale = new ScaleAnimation(0.8f, 1, 0.8f, 1,
                Animation.RELATIVE_TO_SELF, 0.8f, Animation.RELATIVE_TO_SELF,
                0.8f);
        scale.setDuration(700);// 动画时间
        scale.setFillAfter(true);// 保持动画状态
        // 渐变动画
        AlphaAnimation alpha = new AlphaAnimation(0.6f, 1);
        alpha.setDuration(400);// 动画时间
        alpha.setFillAfter(true);// 保持动画状态
        set.addAnimation(scale);
        set.addAnimation(alpha);
        return set;
    }
}
