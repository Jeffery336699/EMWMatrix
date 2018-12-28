package cc.emw.mobile.project.Util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.ApiEntity.UserProject;
import cc.emw.mobile.project.entities.CommonConsts;

/**
 * 一般工具类
 * @author jven.wu
 * 
 */
public class CommonUtil {
	private static final String TAG = "CommonUtil";
	private static Map<String, Integer> typeMap;
	private static final SimpleDateFormat f = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.CHINA);

	/**
	 * 计算进度
	 * @param startTime 开始时间
	 * @param finishTime 结束进间
	 * @param formatString 时间格式化格式
	 * @return 进度
	 */
	public static int getProgress(String startTime, String finishTime,
			String formatString) {
		// 解析时间字符串
		SimpleDateFormat f = new SimpleDateFormat(formatString, Locale.CHINA);
		int value = -1;
		try {
			Date sTime = f.parse(startTime);
			Date fTime = f.parse(finishTime);
			long totalTime = fTime.getTime() - sTime.getTime();
			// 获取当前时间
			long currentTimeMillis = System.currentTimeMillis();
			long durationTime = currentTimeMillis - sTime.getTime();
			if (currentTimeMillis > fTime.getTime()) {
				value = 100;
			} else if (currentTimeMillis < sTime.getTime()) {
				value = 0;
			} else {
				value = (int) (durationTime * 100 / totalTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 过滤掉已经存在容器集合中的用户数据 如若原集合中有时不添加
	 * @param content 原集合内容
	 * @param rets 新增的返回内容
	 * @return 返回过滤后的集合
	 */
	public static ArrayList<UserInfo> filterUsers(
			ArrayList<UserInfo> content, ArrayList<UserInfo> rets) {
		for (int i = 0; i < rets.size(); i++) {
			boolean flag = false;
			for (int j = 0; j < content.size(); j++) {
				if (rets.get(i).ID == content.get(j).ID) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				content.add(rets.get(i));
			}
		}
		return content;
	}

	/**
	 * 过滤掉已经存在容器集合中的文件数据 如若原集合中有时不添加
	 * @param content 原集合内容
	 * @param rets 新增的返回内容
	 * @return 返回过滤后的集合
	 */
	public static ArrayList<Files> filterFiles(
			ArrayList<Files> content, ArrayList<Files> rets) {
		for (int i = 0; i < rets.size(); i++) {
			boolean flag = false;
			for (int j = 0; j < content.size(); j++) {
				if (rets.get(i).ID == content.get(j).ID) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				content.add(rets.get(i));
			}
		}
		return content;
	}

	/**
	 * 设置listview展开显示,使其可嵌套到ScrollView里面
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/**
	 * 计算两个时间差
	 * @param sDate 开始时间
	 * @param eDate 结束时间
	 * @return 天数
	 */
	public static int compareDate(Date sDate, Date eDate) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String start = myFormatter.format(sDate);
		String end = myFormatter.format(eDate);
		try {
			sDate = myFormatter.parse(start);
			eDate = myFormatter.parse(end);
		} catch (Exception e) {

		}
		long quot;
		// 若结束时间小于开始时间则返回零
		if (eDate.getTime() < sDate.getTime()) {
			return 0;
		}
		quot = eDate.getTime() - sDate.getTime();
		quot = quot / (24 * 60 * 60 * 1000);
		int day = new Long(quot).intValue();
		return day;
	}

	/**
	 * 根据开始时间和偏移量计算出相应的时间
	 * @param startDate 开始时间
	 * @param offsetDay 偏移日期
	 * @return 最终时间
	 */
	public static String getOffsetDate(Date startDate, int offsetDay) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
		String startString = format.format(startDate)+"/01";
		String retString = "2016/8/8";
		try {
			Date date = myFormatter.parse(startString);
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			long stdate = date.getTime() + (offsetDay * 24 * 60 * 60* 1000L);
			calendar.setTimeInMillis(stdate);
			retString = format.format(calendar.getTime());
			
		} catch (Exception e) {
			e.toString();
		}
		return retString;
	}

	/**
	 * 计算项目中最小时间
	 * @param projects 项目集合
	 * @return Date
	 */
	public static Date getMinTime(ArrayList<UserProject> projects) {
		Date d1 = new Date();
		try {
			d1 = f.parse(projects.get(0).BeginTime);
			for (int i = 1; i < projects.size(); i++) {
				Date d2 = f.parse(projects.get(i).BeginTime);
				if (d1.getTime() > d2.getTime()) {
					d1 = d2;
				}
			}
		} catch (Exception e) {
            e.printStackTrace();
		}
		return d1;
	}

	/**
	 * 计算项目中最大时间
	 * @param projects 项目集合
	 * @return Date
	 */
	public static Date getMaxTime(ArrayList<UserProject> projects) {
		Date d1 = new Date();
		try {
			d1 = f.parse(projects.get(0).EndTime);
			for (int i = 1; i < projects.size(); i++) {
				Date d2 = f.parse(projects.get(i).EndTime);
				if (d1.getTime() < d2.getTime()) {
					d1 = d2;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d1;
	}

    /**
     * 获取表单内容控件类型的int值
     * @param key 控件类型
     * @return
     */
	public static int getElementType(String key){
		typeMap = new HashMap<String, Integer>();
		typeMap.put("TextBox", CommonConsts.ElementType.TEXTBOX);
		typeMap.put("DateTimer", CommonConsts.ElementType.DATETIMER);
		typeMap.put("DropDownList", CommonConsts.ElementType.DROPDOWNLIST);
		typeMap.put("Searcher",CommonConsts.ElementType.SEARCHER);
        typeMap.put("RadioButton",CommonConsts.ElementType.RADIOBUTTON);
        typeMap.put("TimeSelector",CommonConsts.ElementType.TIMESELECTOR);
        typeMap.put("CheckBox",CommonConsts.ElementType.CHECKBOX);
		int i = 0;
		try{
			i = typeMap.get(key);
		}catch(Exception e){
			e.printStackTrace();
		}
		return i;
	}
}
