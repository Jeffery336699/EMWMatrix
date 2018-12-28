package cc.emw.mobile.project.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.Util.CommonUtil;
import cc.emw.mobile.project.Util.DisplayIcon;
import cc.emw.mobile.project.fragment.ScheduleFragment;
import cc.emw.mobile.project.view.CustomHorizontalScrollView;
import cc.emw.mobile.project.view.ProjectDetailsActivity;
import cc.emw.mobile.util.DisplayUtil;

public class ArrangeScheduleAdapter extends BaseAdapter {
	private static final String TAG = "ArrangeScheduleAdapter";
	private Context mContext;
	private ArrayList<UserProject> mProjects;
	private Date dateBegin = new Date();//项目开始日期
	private Date dateEnd = new Date();//项目结束日期
	private Date dateNow = new Date();//当前日期
	private Date dateMin;
	private SimpleDateFormat sdf;
	private Calendar cal;
    private CustomHorizontalScrollView mScrollView;
    private boolean isSingleProject = false;
    public int locationX = 0;
    public Date projectsMinTime = new Date();

	public ArrangeScheduleAdapter(Context context) {
		this.mContext = context;
		mProjects = new ArrayList<UserProject>();
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
		cal = Calendar.getInstance();
	}

	public void setArrayProjects(List<UserProject> projects) {
		if (projects != null) {
			this.mProjects.clear();
			this.mProjects.addAll(projects);
			dateMin = CommonUtil.getMinTime(mProjects);
		}
	}

    public void setSingleProjectFlag (boolean flg){
        isSingleProject = flg;
    }

    public void setScrollView(CustomHorizontalScrollView scrollView){
        this.mScrollView = scrollView;
    }

	@Override
	public int getCount() {
		return mProjects != null ? mProjects.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mProjects != null ? mProjects.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		final ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.listitem_project_arrange, parent, false);
            vh.frameLayout = (FrameLayout)convertView
                    .findViewById(R.id.frameLayout);
			vh.timespan = (TextView) convertView
					.findViewById(R.id.tv_projectarrange_timespan);
			vh.progress = (TextView) convertView
					.findViewById(R.id.tv_projectarrange_progress);
            vh.navIcon = (ImageView)convertView
                    .findViewById(R.id.nav_icon);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		final UserProject project = mProjects.get(position);
		OnClickListener onClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_projectarrange_timespan:
                    case R.id.tv_projectarrange_progress:
                        Intent intent = new Intent(parent.getContext(),ProjectDetailsActivity.class);
                        intent.putExtra(ProjectDetailsActivity.DETAILS_PROJECT, project);
                        parent.getContext().startActivity(intent);
                        break;
                    case R.id.nav_icon:
                        mScrollView.smoothScrollTo(
                                DisplayUtil.dip2px(mContext, ScheduleFragment.UNIT_WIDTH) * 3, 0);
                        break;
                }
			}
		};
        if(!isSingleProject) {
            vh.timespan.setOnClickListener(onClickListener);
            vh.progress.setOnClickListener(onClickListener);
        }
		vh.timespan
				.setText(project.Name);
		vh.timespan.setBackgroundResource(DisplayIcon.getStrokeColor(project.Color));
		vh.progress
				.setText(project.Name);
		vh.progress.setBackgroundResource(DisplayIcon.getRoundColor(project.Color));
		calculatePosition(parent.getContext(), vh.timespan, vh.progress,
                project);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                vh.navIcon.layout(locationX + 26, vh.navIcon.getTop(), vh.navIcon.getWidth() + locationX + 26, vh.navIcon.getBottom());
                vh.navIcon.setVisibility(View.VISIBLE);
            }
        });
		return convertView;
	}
	class ViewHolder {
		TextView timespan;
		TextView progress;
        ImageView navIcon;
        FrameLayout frameLayout;
	}

	private void calculatePosition(Context context,TextView times,
                                   TextView prog,UserProject project) {
		int starDate;//所有项目中最小日期的时间
		int durationDate;//项目持续日期
		float projectProgress;//项目进度
		dateBegin = new Date();//项目开始日期
		dateEnd = new Date();//项目结束日期
		dateNow = new Date();//当前日期
		cal.clear();
		cal.setTime(dateMin);
		starDate = cal.get(Calendar.DAY_OF_MONTH);
		try {
			dateBegin = sdf.parse(project.BeginTime);
			dateEnd = sdf.parse(project.EndTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		durationDate = CommonUtil.compareDate(dateBegin, dateEnd);
        float cupBeginToNowDuration = (float)CommonUtil.compareDate(dateBegin, dateNow)/(float)durationDate;
        if(cupBeginToNowDuration > 0){
            times.setText("");
        }
		projectProgress =dateNow.getTime()<dateEnd.getTime()?
                cupBeginToNowDuration : 1;
		RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(
				DisplayUtil.dip2px(context, ScheduleFragment.UNIT_WIDTH*durationDate),
                DisplayUtil.dip2px(context, 26));
		int offset = (starDate + CommonUtil.compareDate(dateMin, dateBegin))*
                ScheduleFragment.UNIT_WIDTH - ScheduleFragment.UNIT_WIDTH/2;
		lParams.setMargins(
				DisplayUtil.dip2px(
						context, offset
						), 0, 0, 0);
		lParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		times.setLayoutParams(lParams);
		RelativeLayout.LayoutParams lParams2 = new RelativeLayout.LayoutParams(
				DisplayUtil.dip2px(
                        context, ScheduleFragment.UNIT_WIDTH*durationDate*projectProgress),
                DisplayUtil.dip2px(context, 26));
		lParams2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		lParams2.addRule(RelativeLayout.ALIGN_LEFT, R.id.tv_projectarrange_timespan);
		prog.setLayoutParams(lParams2);
	}

    private void isWedgetInScreen(View view){
        Point p=new Point();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getSize(p);
        int screenWidth=p.x;
        int screenHeight=p.y;
        Rect rect=new Rect(0,0,screenWidth,screenHeight );
        int[] location = new int[2];
        view.getLocationInWindow(location);
        if (view.getLocalVisibleRect(rect)) {
        }else{
        }
    }
}
