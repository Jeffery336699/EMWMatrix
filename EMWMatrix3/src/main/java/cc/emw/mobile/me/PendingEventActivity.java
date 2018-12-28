package cc.emw.mobile.me;

import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.me.adapter.PendingEventAdapter;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.AnimatedExpandableListView;

import static cc.emw.mobile.R.id.root_view;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.me
 * @data on 2018/8/29  14:31
 * @describe TODO
 */
@ContentView(R.layout.activity_pending_event)
public class PendingEventActivity extends BaseActivity {

    private AnimatedExpandableListView mNavigateElv;

    private PendingEventAdapter adapter;

    LinearLayout rootView;
    PercentRelativeLayout layout_groupname_min;
    View pending_view;

    List<ApiEntity.WeekInfo> weekList = new ArrayList<ApiEntity.WeekInfo>();
    String dateTime = "";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    List<List<ApiEntity.UserFenPai>> respList;
    int toDay = 0;

    TextView to_day, to_month, to_year, to_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取多级菜单控件，显示出来
        mNavigateElv = (AnimatedExpandableListView) findViewById(R.id.elv_mainmenu_navigate);
        adapter = new PendingEventAdapter(this);
        WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
        mNavigateElv.setAdapter(adapter);
        to_day = (TextView) findViewById(R.id.to_day);
        to_month = (TextView) findViewById(R.id.to_month);
        to_year = (TextView) findViewById(R.id.to_year);
        to_time = (TextView) findViewById(R.id.to_time);
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        dateTime = simpleDateFormat.format(date);
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMddHH:mm");
        String toDayTime = DateFormat.format(date);
        to_day.setText(toDayTime.substring(6, 8));
        to_month.setText(toDayTime.substring(4, 6) + "月");
        to_year.setText(toDayTime.substring(0, 4));
        to_time.setText(toDayTime.substring(8));

        getWeekList();
        GetTaskByTimeSpan();
        mNavigateElv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                /*List<ApiEntity.UserFenPai> list = respList.get(groupPosition);
                if (null == respList.get(groupPosition) || respList.get(groupPosition).size() == 0) {
                    ToastUtil.showToast(PendingEventActivity.this, "该日暂无待办工作");
                    return false;
                }
                if (null != rootView) {
                    rootView.clearAnimation();
                }
                layout_groupname_min = v.findViewById(R.id.layout_groupname_min);
                rootView = v.findViewById(root_view);
                pending_view = v.findViewById(R.id.pending_view);
                if (parent.isGroupExpanded(groupPosition)) {
                    rootView.clearAnimation();
                    rootView.setVisibility(View.VISIBLE);
                    pending_view.setVisibility(View.VISIBLE);
                    layout_groupname_min.setVisibility(View.GONE);
                } else {
                    AnimationSet aset_3 = new AnimationSet(true);
                    ScaleAnimation aa_3 = new ScaleAnimation(1, 0.22f, 1, 0.7f, Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, 1f);
                    aa_3.setDuration(500);
                    aa_3.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            rootView.setVisibility(View.VISIBLE);
                            pending_view.setVisibility(View.GONE);
                            layout_groupname_min.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            rootView.clearAnimation();
                            rootView.setVisibility(View.GONE);
                            pending_view.setVisibility(View.GONE);
                            layout_groupname_min.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    aset_3.addAnimation(aa_3);
                    aset_3.setFillAfter(true);
                    rootView.startAnimation(aset_3);
                }
                return false;*/
                List<ApiEntity.UserFenPai> list = respList.get(groupPosition);
                if (null == respList.get(groupPosition) || respList.get(groupPosition).size() == 0) {
                    ToastUtil.showToast(PendingEventActivity.this, "该日暂无待办工作");
                    return false;
                }
                if (null != rootView) {
                    rootView.clearAnimation();
                }
                layout_groupname_min = v.findViewById(R.id.layout_groupname_min);
                rootView = v.findViewById(root_view);
                pending_view = v.findViewById(R.id.pending_view);
                if (parent.isGroupExpanded(groupPosition)) {
                    rootView.clearAnimation();
                    rootView.setVisibility(View.VISIBLE);
                    pending_view.setVisibility(View.VISIBLE);
                    layout_groupname_min.setVisibility(View.GONE);
                    mNavigateElv.collapseGroupWithAnimation(groupPosition);
                } else {
                    AnimationSet aset_3 = new AnimationSet(true);
                    ScaleAnimation aa_3 = new ScaleAnimation(1, 0.22f, 1, 0.7f, Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, 1f);
                    aa_3.setDuration(500);
                    aa_3.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            rootView.setVisibility(View.VISIBLE);
                            pending_view.setVisibility(View.GONE);
                            layout_groupname_min.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            rootView.clearAnimation();
                            rootView.setVisibility(View.GONE);
                            pending_view.setVisibility(View.GONE);
                            layout_groupname_min.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    aset_3.addAnimation(aa_3);
                    aset_3.setFillAfter(true);
                    rootView.startAnimation(aset_3);
                    mNavigateElv.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });
    }

    /**
     * 获取代办工作列表
     */
    private void GetTaskByTimeSpan() {
        API.TalkerAPI.GetTaskByTimeSpan(0, PrefsUtil.readUserInfo().ID, getWeekStartTime(), getWeekEndTime(), new RequestCallback<ApiEntity.UserFenPai>(
                ApiEntity.UserFenPai.class) {

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(PendingEventActivity.this, "本周暂无待办工作");
            }

            @Override
            public void onParseStrSuccess(String respStr) {
                if (null != respStr && !"".equals(respStr)) {
                    respList = new ArrayList<List<ApiEntity.UserFenPai>>();
                    Gson gson = new Gson();
                    String strs = respStr.substring(8, respStr.length() - 1);
                    try {
                        ApiEntity.UserFenPai[][] str = gson.fromJson(strs, new TypeToken<ApiEntity.UserFenPai[][]>() {
                        }.getType());
                        for (ApiEntity.UserFenPai[] strings : str) {
                            List<ApiEntity.UserFenPai> list = new ArrayList<ApiEntity.UserFenPai>();
                            for (ApiEntity.UserFenPai string : strings) {
                                list.add(string);
                            }
                            respList.add(list);
                        }
                        adapter.setData(weekList, respList);
                        adapter.notifyDataSetChanged();
                        //mNavigateElv.expandGroup(toDay - 1);
                        mNavigateElv.expandGroupWithAnimation(toDay - 1);
                    } catch (Exception e) {
                        ToastUtil.showToast(PendingEventActivity.this, "数据获取失败");
                    }
                }
            }
        });
    }


    public void getWeekList() {
        for (int i = 1; i < 8; i++) {
            Calendar cal = Calendar.getInstance();
            int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (day_of_week == 0) {
                day_of_week = 7;
            }
            cal.add(Calendar.DATE, -day_of_week + i);
            String time = simpleDateFormat.format(cal.getTime());
            ApiEntity.WeekInfo week = new ApiEntity.WeekInfo();
            week.weekTime = time;
            week.state = isDateOneBigger(time, dateTime, i);
            week.weekText = getWeekText(i);
            weekList.add(week);
        }
    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     *
     * @param str1 the first date
     * @param str2 the second date
     * @return true <br/>false
     */
    public int isDateOneBigger(String str1, String str2, int i) {
        int isBigger = 1;
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = simpleDateFormat.parse(str1);
            dt2 = simpleDateFormat.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = 2;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = 0;
        } else {
            isBigger = 1;
            toDay = i;
        }
        return isBigger;
    }

    /**
     * start
     * 本周开始时间戳 - 以星期一为本周的第一天
     */
    public String getWeekStartTime() {
        Calendar cal = Calendar.getInstance();
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        cal.add(Calendar.DATE, -day_of_week + 1);
        return simpleDateFormat.format(cal.getTime());
    }

    /**
     * end
     * 本周结束时间戳 - 以星期一为本周的第一天
     */
    public String getWeekEndTime() {
        Calendar cal = Calendar.getInstance();
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        cal.add(Calendar.DATE, -day_of_week + 7);
        return simpleDateFormat.format(cal.getTime());

    }

    @Event(value = {R.id.cm_header_btn_left})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;

        }
    }

    public String getWeekText(int i) {
        String text = "";
        if (i == 1) {
            text = "周一";
        } else if (i == 2) {
            text = "周二";
        } else if (i == 3) {
            text = "周三";
        } else if (i == 4) {
            text = "周四";
        } else if (i == 5) {
            text = "周五";
        } else if (i == 6) {
            text = "周六";
        } else if (i == 7) {
            text = "周日";
        }
        return text;
    }
}
