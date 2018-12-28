package cc.emw.mobile.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.net.ApiEntity.Files;
import cc.emw.mobile.net.ApiEntity.UserInfo;
import cc.emw.mobile.net.Const;

public class TaskUtils {
    private static DisplayImageOptions options = new DisplayImageOptions.Builder()
//            .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//            .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//            .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
            // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
            .build(); // 创建配置过得DisplayImageOption对象

    /**
     * 传入头像Url资质获取头像
     *
     * @param url
     * @param iv
     */
    public static void setCivImageView(String url, ImageView iv) {
        ImageLoader.getInstance().displayImage(getHeadImageUri(url),
                iv, options);
    }

    public static void setCivImageView(Context context, String url, ImageView iv) {
        ImageLoader.getInstance().displayImage(getHeadImageUri(url), new ImageViewAware(iv), options, new ImageSize(DisplayUtil.dip2px(context, 30), DisplayUtil.dip2px(context, 30)), null, null);
    }

    public static String getHeadImageUri(String imageUrl) {
        String uri = String.format(Const.DOWN_ICON_URL, PrefsUtil
                .readUserInfo().CompanyCode, imageUrl);
        if (imageUrl != null && imageUrl.startsWith("/")) {
            uri = Const.DOWN_ICON_URL2 + imageUrl;
        }
        return uri;
    }

    /**
     * 计算进度
     *
     * @param startTime    开始时间
     * @param finishTime   结束进间
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
     * @param userString 用户数据
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
                cc.emw.mobile.entity.UserInfo user = EMWApplication.personMap.get(Integer
                        .valueOf(strings[i]));
                if (user != null) {
                    mainUsers.add(user);
                } else {
                    UserInfo userInfo = new UserInfo();
                    userInfo.ID = Integer.valueOf(strings[i]);
                    mainUsers.add(userInfo);
                }
            }
        }
        return mainUsers;
    }

    /**
     * 检验当前用户是否是负责人
     *
     * @param mainUsers
     * @return
     */
    public static  boolean iSForCharge(String mainUsers) {
        List<UserInfo> users = getUsers(mainUsers);
        if (users.contains(EMWApplication.personMap
                .get(PrefsUtil.readUserInfo().ID))) {
            return true;
        } else {
            return false;
        }
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
     * @param filesList 知识库返回来的文件对象
     * @param tempFiles 本地附件列表
     * @return 返回去顺序的附件列表
     */
    public static ArrayList<Files> deleteFiles(ArrayList<Files> filesList,
                                               ArrayList<Files> tempFiles) {
        for (int i = 0; i < tempFiles.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < filesList.size(); j++) {
                if (filesList.get(j).ID == tempFiles.get(i).ID) {
                    flag = true;    //包含
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
     * @param filesList 知识库返回来的文件对象
     * @param tempFiles 本地附件列表
     * @return 返回添加后的附件列表
     */
    public static ArrayList<Files> addFiles(ArrayList<Files> filesList,
                                            ArrayList<Files> tempFiles) {
        for (int i = 0; i < filesList.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < tempFiles.size(); j++) {
                if (filesList.get(i).ID == tempFiles.get(j).ID) {
                    flag = true;//包含
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
     * @param content "[24,35,67]"类似的字符串
     * @return 返回字符数组
     */
    public static String[] getStringID(String content) {

        if (content != null && !content.equals("") && !content.equals("[]")) {
            String[] strings;
            strings = content.substring(1, content.length() - 1).split(",");
            return strings;
        }
        return new String[]{};
    }

    /**
     * 根据时间字符串解析成时间
     *
     * @param formatString 解析的时间格式
     * @param timeString   时间字符串
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
     * 将旧的时间字符串转换成新的格式字符串
     * 将旧的时间字符串转换成新的格式字符串
     *
     * @param formatString
     * @param newFormatString
     * @param timeString
     * @return
     */
    public static String parseToNewStringTime(String formatString, String newFormatString, String timeString) {
        SimpleDateFormat old = new SimpleDateFormat(formatString, Locale.CHINA);
        Date date = null;
        try {
            date = old.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newStringTime = "";
        if (date != null) {
            SimpleDateFormat f = new SimpleDateFormat(newFormatString, Locale.CHINA);
            newStringTime = f.format(date);
        }
        return newStringTime;
    }

    public static String parseToStringTime(String formatString,String timeString){
        SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.CHINA);
        Date date = null;
        String timeStr="";
        try {
            date = format.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date != null){
            timeStr=format.format(date);
        }
        return timeStr;
    }
    /**
     * 将附件集合转换成"[1,2,3,4]"等字符串
     *
     * @param tempFiles
     * @return
     */
    public static String getRepositoryArray(ArrayList<Files> tempFiles) {
        StringBuilder sb = new StringBuilder();
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
        return sb.toString();
    }

    /**
     * 将人员列表转换成字符串
     *
     * @param users
     * @return
     */
    public static String members2string(List<ApiEntity.UserInfo> users) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < users.size(); i++) {
            sb.append(users.get(i).ID).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString().trim();
    }

    public static String names2string(List<ApiEntity.UserInfo> users) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < users.size(); i++) {
            sb.append(users.get(i).Name).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString().trim();
    }

    /**
     * 遍历迭代更改任务实体
     *
     * @param TotalUser
     * @param userFenPai
     */
    public static void changeUserFenPai(ApiEntity.UserFenPai TotalUser, ApiEntity.UserFenPai userFenPai) {
        List<ApiEntity.UserFenPai> tasks = TotalUser.Tasks;
        if (tasks != null && tasks.size() != 0) {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).ID == userFenPai.ID) {
                    tasks.set(i, userFenPai);
                    break;
                } else {
                    changeUserFenPai(tasks.get(i), userFenPai);
                }
            }
        }
    }
}
