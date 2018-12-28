package cc.emw.mobile.chat.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sunny.du on 2017/2/10.
 * 时间工具类
 */
public class DateUtil {
    public static Date getDate(String format, String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);//2017-02-10 00:00:00
        try {
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStringTime(String format, Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String getDay() {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /**
     * 比对时间是否相等
     */
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        return isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isSameMinutes(long startTime,long lastTime,String startTimeStr,String lastTimeStr){
        return startTime - lastTime <= 60 * 3000 &&
                DateUtil.getStringTime("mm",DateUtil.getDate("yyyy-MM-dd HH:mm:ss", startTimeStr))
                        .equals(DateUtil.getStringTime("mm",DateUtil.getDate("yyyy-MM-dd HH:mm:ss", lastTimeStr)));
    }
}
