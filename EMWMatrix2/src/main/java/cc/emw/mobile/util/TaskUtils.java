package cc.emw.mobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserInfo;

public class TaskUtils {
	/**
	 * 计算列表高度
	 * 
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
	 * 获取负责人的集合
	 * 
	 * @param userString
	 *            用户数据
	 * @return
	 */
	public static List<UserInfo> getUsers(String userString) {
		List<UserInfo> mainUsers = new ArrayList<UserInfo>();// 更多用户解析
		String[] strings;
		if (userString == null) {
			return mainUsers;
		}
		strings = userString.split(",");
		for (int i = 0; i < strings.length; i++) {
			if (!strings[i].equals("")) {
				mainUsers.add(EMWApplication.personMap.get(Integer
						.valueOf(strings[i])));
			}
		}
		return mainUsers;
	}

	/**
	 * 将知识库的附件实体 转换成任务中的附件实体
	 * 
	 * @param list
	 *            知识库返回的附件实体集合
	 * @return 返回任务实体集合
	 */
	// public static ArrayList<Files> getFilesList(
	// ArrayList<Files> list) {
	// ArrayList<Files> dataList = new ArrayList<Files>();
	// if (list == null) {
	// return dataList;
	// }
	// for (Files file : list) {
	// Files Files = new Files();
	// Files.FileName = file.Name;
	// Files.FileId = file.ID;
	// Files.Length = file.Length;
	// dataList.add(Files);
	// }
	// return dataList;
	// }

	/**
	 * 将知识库传送到任务的附件与原附件列表进行对比顺序删减
	 * 
	 * @param filesList
	 *            知识库返回来的文件对象
	 * @param tempFiles
	 *            本地附件列表
	 * @return 返回去顺序的附件列表
	 */
	public static ArrayList<Files> deleteFiles(ArrayList<Files> filesList,
			ArrayList<Files> tempFiles) {
		for (int i = 0; i < tempFiles.size(); i++) {
			boolean flag = false;
			for (int j = 0; j < filesList.size(); j++) {
				if (filesList.get(j).ID == tempFiles.get(i).ID) {
					flag=true;	//包含
					break;
				}
			}
			if (!flag) {
				tempFiles.remove(i);
				i--;
			}
		}
		return tempFiles;
	}

	/**
	 * 将知识库中添加的附件添加到本地附件列表中
	 * 
	 * @param filesList
	 *            知识库返回来的文件对象
	 * @param tempFiles
	 *            本地附件列表
	 * @return 返回添加后的附件列表
	 */
	public static ArrayList<Files> addFiles(ArrayList<Files> filesList,
			ArrayList<Files> tempFiles) {
		for (int i = 0; i < filesList.size(); i++) {
			boolean flag = false;
			for (int j = 0; j < tempFiles.size(); j++) {
				if (filesList.get(i).ID == tempFiles.get(j).ID) {
					flag=true;//包含
					break;
				}
			}
			if (!flag) {
				tempFiles.add(filesList.get(i));
			}
		}
		return tempFiles;
	}

	/**
	 * 将任务附件集合转换成json字符串
	 * 
	 * @param list
	 * @return
	 */
	// public static String getFilesString(List<Files> list) {
	// if (list == null) {
	// return new Gson().toJson(new ArrayList<Files>());
	// }
	// return new Gson().toJson(list);
	// }

	/**
	 * 强制显示软键盘
	 * 
	 * @param context
	 * @param view
	 */
	public static void showSoftInput(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 根据"[24,35,67]"获取ID 字符数组
	 * 
	 * @param content
	 *            "[24,35,67]"类似的字符串
	 * @return 返回字符数组
	 */
	public static String[] getStringID(String content) {

		if (content != null && !content.equals("") && !content.equals("[]")) {
			String[] strings;
			strings = content.substring(1, content.length() - 1).split(",");
			return strings;
		}
		return new String[] {};
	}

	/**
	 * 根据时间字符串解析成时间
	 * 
	 * @param formatString
	 *            解析的时间格式
	 * @param timeString
	 *            时间字符串
	 * @return 返回毫秒值，如果解析失败就返回-1
	 */
	public static long parseStringTime(String formatString, String timeString) {
		SimpleDateFormat f = new SimpleDateFormat(formatString, Locale.CHINA);
		try {
			return f.parse(timeString).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 将附件集合转换成"[1,2,3,4]"等字符串
	 * 
	 * @param tempFiles
	 * @return
	 */
	public static String getRepositoryArray(ArrayList<Files> tempFiles) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (tempFiles != null && tempFiles.size() != 0) {
			for (int i = 0; i < tempFiles.size(); i++) {
				int fileId = tempFiles.get(i).ID;
				if (i == tempFiles.size() - 1) {
					sb.append(fileId);
				} else {
					sb.append(fileId + ",");
				}
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
