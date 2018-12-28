package cc.emw.mobile.calendar.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.net.Const;
import cc.emw.mobile.util.CalendarUtil;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.view.CircleImageView;


public class ScalingListViewMonthAdapter extends BaseAdapter {

    private List<CalendarInfo> infos;
    private Context mActivity;
    private SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat time2 = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期格式
    private DisplayImageOptions options;
    private Calendar mClickDate;
    private ImageLoader imageLoader;

    public ScalingListViewMonthAdapter(List<CalendarInfo> infos, Context mActivity) {
        this.infos = infos;
        this.mActivity = mActivity;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.trans_bg) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.trans_bg) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.trans_bg) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
        mClickDate = Calendar.getInstance();
    }

    @Override
    public int getCount() {
        return infos == null ? 0 : infos.size();
    }

    @Override
    public Object getItem(int position) {
        return this.infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setMClickDate(Calendar mClickDate) {
        this.mClickDate = mClickDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.listitem_calendar_month, parent, false);
            holder.tvCInfo = (TextView) convertView.findViewById(R.id.tv_calendar_info);
            holder.tvStartTime = (TextView) convertView.findViewById(R.id.tv_calendar_start_time);
            holder.cTag = (TextView) convertView.findViewById(R.id.tv_calendar_tag);
            holder.cStartTime = (TextView) convertView.findViewById(R.id.cal_tv_start_time);
            holder.circleImageView = (CircleImageView) convertView.findViewById(R.id.civ_cal_create_head);
            holder.mLlLayout = (LinearLayout) convertView.findViewById(R.id.ll_list_item_calendar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final CalendarInfo calInfo = infos.get(position);

        Calendar sCal = Calendar.getInstance();
        Calendar eCal = Calendar.getInstance();
        Calendar mCalFirst = Calendar.getInstance();
        Calendar mCalEnd = Calendar.getInstance();
        try {
            sCal.setTimeInMillis(time.parse(calInfo.StartTime).getTime());
            eCal.setTimeInMillis(time.parse(calInfo.OverTime).getTime());
            String mClickTime = getTimeString(time2, mClickDate);
            String mClickTimeFirst = mClickTime + " 00:00:00";
            String mClickTimeEnd = mClickTime + " 23:59:59";
            mCalFirst.setTimeInMillis(time.parse(mClickTimeFirst).getTime());
            mCalEnd.setTimeInMillis(time.parse(mClickTimeEnd).getTime());
            if (eCal.getTimeInMillis() <= mCalEnd.getTimeInMillis() &&
                    sCal.getTimeInMillis() >= mCalFirst.getTimeInMillis()) {
                holder.cStartTime.setText(calInfo.StartTime.substring(calInfo.StartTime.length() - 8, calInfo.StartTime.length() - 3));
                if (calInfo.Allday == 1) {
                    holder.cStartTime.setText("全天");
                    holder.tvStartTime.setText("");
                } else {
                    holder.tvStartTime.setText(CalendarUtil.getBetWeenTimes(calInfo.OverTime, calInfo.StartTime));
                }
            } else {
                holder.cStartTime.setText(calInfo.StartTime.substring(5, calInfo.StartTime.length() - 3));
                holder.tvStartTime.setText(calInfo.OverTime.substring(5, calInfo.OverTime.length() - 3));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(calInfo.Label)) {
            if (calInfo.Label.contains("#")) {
                holder.cTag.setText(calInfo.Label);
            } else {
                holder.cTag.setText("#" + calInfo.Label);
            }
        } else {
            holder.cTag.setText("#无");
        }
        holder.circleImageView.setTvBg(EMWApplication.getIconColor(PrefsUtil.readUserInfo().ID), calInfo.T_Creator, 28);
        String uri = String.format(Const.DOWN_ICON_URL,
                PrefsUtil.readUserInfo().CompanyCode, calInfo.T_CreatorImage);
        imageLoader.displayImage(uri, new ImageViewAware(holder.circleImageView), options,
                new ImageSize(DisplayUtil.dip2px(mActivity, 28), DisplayUtil.dip2px(mActivity, 28)), null, null);

//        Field[] fields = holder.circleImageView.getClass().getDeclaredFields();
//        int imgid = 0;
//        for (Field f : fields) {
//            if (f.getName().equals("mResource")) {
//
//                f.setAccessible(true);
//                try {
//                    imgid = f.getInt(holder.circleImageView);
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        holder.tvCInfo.setText(calInfo.Title);
        holder.mLlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,
                        CalendarInfoActivity2.class);
                intent.putExtra("calendarInfo", calInfo);
                intent.putExtra("start_anim", false);
                int[] location = new int[2];
                v.getLocationInWindow(location);
                intent.putExtra("click_pos_y", location[1]);
                mActivity.startActivity(intent);
            }
        });
        return convertView;
    }

    /**
     * 获取指点格式时间字符串
     *
     * @param time2      时间格式
     * @param mClickDate 时间
     * @return
     */
    public String getTimeString(SimpleDateFormat time2, Calendar mClickDate) {
        String hehe = time2.format(mClickDate.getTime());
        return hehe;
    }

    private static class ViewHolder {
        LinearLayout mLlLayout;
        TextView tvCInfo, tvStartTime, cTag, cStartTime;
        CircleImageView circleImageView;
    }
}
