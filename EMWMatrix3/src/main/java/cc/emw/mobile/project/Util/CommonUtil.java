package cc.emw.mobile.project.Util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.task.constant.TaskConstant;
import cc.emw.mobile.util.CalendarUtil;

/**
 * Created by jven.wu on 2016/6/30.
 */
public class CommonUtil {
    //服务器传过来的任务时间格式
    public static final String TASK_DATE_FORMAT_SERVER = "yyyy-MM-dd HH:mm";

    //服务器传过来的项目时间格式
    public static final String PROJECT_DATE_FORMAT_SERVER = "yyyy-MM-dd HH:mm:ss";

    /**
     * 圆形图标
     *
     * @param index 颜色索引值
     * @return
     */
    public static int getProjectRoundColor(int index) {
        HashMap<Integer, Integer> projectColorMap = new HashMap<Integer, Integer>();
        projectColorMap.put(0, R.drawable.project_color5);
        projectColorMap.put(1, R.drawable.project_color1);
        projectColorMap.put(2, R.drawable.project_color2);
        projectColorMap.put(3, R.drawable.project_color3);
        projectColorMap.put(4, R.drawable.project_color4);
        projectColorMap.put(5, R.drawable.project_color5);
        if (index < 0 || index > 5) {
            return projectColorMap.get(0);
        }
        return projectColorMap.get(index);
    }

    public static int getProjectColor(int index) {
        HashMap<Integer, Integer> projectColorMap = new HashMap<>();
        projectColorMap.put(0, R.color.transparent);
        projectColorMap.put(1, R.color.project_color1);
        projectColorMap.put(2, R.color.project_color2);
        projectColorMap.put(3, R.color.project_color3);
        projectColorMap.put(4, R.color.project_color4);
        projectColorMap.put(5, R.color.transparent);
        if (index < 0 || index > 5) {
            return projectColorMap.get(0);
        }
        return projectColorMap.get(index);
    }

    public static int getProjectBg(int index) {
        HashMap<Integer, Integer> projectColorMap = new HashMap<>();
        projectColorMap.put(0, R.drawable.sbc1_project_color1);
        projectColorMap.put(1, R.drawable.sbc1_project_color1);
        projectColorMap.put(2, R.drawable.sbc1_project_color2);
        projectColorMap.put(3, R.drawable.sbc1_project_color3);
        projectColorMap.put(4, R.drawable.sbc1_project_color4);
        projectColorMap.put(5, R.drawable.sbc1_project_color0);
        if (index < 0 || index > 5) {
            return projectColorMap.get(0);
        }
        return projectColorMap.get(index);
    }

    public static int getTaskColor(int index) {
        HashMap<Integer, Integer> taskColor = new HashMap<>();
        taskColor.put(0, R.color.white);
        taskColor.put(1, R.color.task_color1);
        taskColor.put(2, R.color.task_color2);
        taskColor.put(3, R.color.task_color3);
        taskColor.put(4, R.color.white);
        if (index < 0 || index > 4) {
            return taskColor.get(0);
        }
        return taskColor.get(index);
    }

    public static int getTaskBg(int index) {
        HashMap<Integer, Integer> taskColor = new HashMap<>();
        taskColor.put(0, R.drawable.sbc1_item_bg0);
        taskColor.put(1, R.drawable.sbc1_item_bg1);
        taskColor.put(2, R.drawable.sbc1_item_bg2);
        taskColor.put(3, R.drawable.sbc1_item_bg3);
        taskColor.put(4, R.drawable.sbc1_item_bg0);
        if (index < 0 || index > 4) {
            return taskColor.get(0);
        }
        return taskColor.get(index);
    }

    public static int getProjectStateColor(int index) {
        HashMap<Integer, Integer> stateColor = new HashMap<>();
        stateColor.put(0, R.color.project_color1);
        stateColor.put(1, R.color.project_color2);
        stateColor.put(2, R.color.project_color3);
        stateColor.put(3, R.color.project_color4);
        if (index < 0 || index > 3) {
            return stateColor.get(0);
        }
        return stateColor.get(index);
    }

    public static int getTaskStateColor(int index) {
        HashMap<Integer, Integer> stateColor = new HashMap<>();
        stateColor.put(0, R.color.white);
        stateColor.put(1, R.color.project_color1);
        stateColor.put(2, R.color.project_color4);
        stateColor.put(3, R.color.project_color2);
        stateColor.put(4, R.color.white);
        if (index < 0 || index > 3) {
            return stateColor.get(0);
        }
        return stateColor.get(index);
    }

    public static String getProjectStateTxt(int stateIndex) {
        Map<Integer, String> projectStateTxts = new HashMap<>();
        projectStateTxts.put(0, "未开始");
        projectStateTxts.put(1, "进行中");
        projectStateTxts.put(2, "已完成");
        projectStateTxts.put(3, "延迟");
        if (stateIndex < 0 || stateIndex > 3) {
            return projectStateTxts.get(0);
        }
        return projectStateTxts.get(stateIndex);
    }

    /**
     * 计算列表高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ExpandableListView listView) {
        // 获取ListView对应的Adapter
        ExpandableListAdapter exAdapter = listView.getExpandableListAdapter();
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null || exAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < exAdapter.getGroupCount(); i++) {
            View listItem = exAdapter.getGroupView(i, true, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            for (int j = 0; j < exAdapter.getChildrenCount(i); j++) {
                boolean islastChild = j == exAdapter.getChildrenCount(i) - 1 ? true : false;
                listItem = exAdapter.getChildView(i, j, islastChild, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }
        }
//        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(0, 0); // 计算子项View 的宽高
//            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
//        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 获取任务状态
     *
     * @param index
     * @return
     */
    public static String getTaskState(int index) {
        switch (index) {
            case TaskConstant.TaskState.PROCESSING:
                return "进行中";
            case TaskConstant.TaskState.UNSTART:
                return "未开始";
            case TaskConstant.TaskState.FINISHED:
                return "已完成";
            case TaskConstant.TaskState.DELAY:
                return "延迟";
            default:
                return "index error";
        }
    }

    /**
     * 删除线
     */
    public static void addStrikeSpan(String str, TextView content_txt) {
        SpannableString spanString = new SpannableString(str);
        StrikethroughSpan span = new StrikethroughSpan();
        spanString.setSpan(span, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        content_txt.setText(spanString);
    }

    /**
     * 将字符时间转换成Date
     * @param dateStr
     * @param formatStr
     * @return
     */
    public static Date convertStringToDate(String dateStr,String formatStr){
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        try{
            Date  date = format.parse(dateStr);
            return date;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateString(Date date,String formatStr){
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }

    public static void setTaskDeadLineTimeString(TextView tv, ApiEntity.UserFenPai item) {
        Date date1 = CommonUtil.convertStringToDate(item.StartTime, CommonUtil.TASK_DATE_FORMAT_SERVER);
        Date date2 = CommonUtil.convertStringToDate(item.FinishTime, CommonUtil.TASK_DATE_FORMAT_SERVER);
        if (date2 != null) {
            String dateStr2 = CalendarUtil.convertDateToString(date2);
            String nowStr = new CalendarUtil().getNowTime("yyyy-MM-dd");
            String cmpDate = CalendarUtil.getTwoDay(nowStr, dateStr2);
            String displayStr = "";
            if ("0".equals(cmpDate)) { //今天
                displayStr = String.format("今天 %s 截止", CommonUtil.getDateString(date2, "HH:mm"));
            } else if ("1".equals(cmpDate)) { //明天
                displayStr = "明天 截止";
            } else if (date1 != null) {
                String dateStr1 = CalendarUtil.convertDateToString(date1);
                cmpDate = CalendarUtil.getTwoDay(dateStr1, dateStr2);
                if ("0".equals(cmpDate)) { // 未来某天
                    displayStr = String.format("%s 截止", CommonUtil.getDateString(date2, "MM月dd日"));
                } else { //某一时间段
                    displayStr = String.format("%s - %s",
                            CommonUtil.getDateString(date1, "yyyy年MM月dd日"),
                            CommonUtil.getDateString(date2, "yyyy年MM月dd日"));
                }
            }
            tv.setText(displayStr);
        }
    }
}
