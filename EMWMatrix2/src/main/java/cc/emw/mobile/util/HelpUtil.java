package cc.emw.mobile.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import cc.emw.mobile.R;
import cc.emw.mobile.constant.InputPicConst;
import cc.emw.mobile.entity.UserNote;
import cc.emw.mobile.net.ApiEntity;

public class HelpUtil {

	/**
	 *
	 * @param i
	 * @param count
	 * @return
	 */
	public static String getPercent(int i, int count) {
		DecimalFormat format = new DecimalFormat("#");
		return format.format((i * 100) / count) + "%";
	}
	
	public static CharSequence getImageString(Context context, int imgResId, int strResId) {
		return getImageString(context, imgResId, context.getString(strResId));
	}

	public static CharSequence getImageString(Context context, int imgResId, String str) {
		Drawable drawable = context.getResources().getDrawable(imgResId);
		// drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
		// drawable.getIntrinsicHeight());
		drawable.setBounds(0, 0, 40, 40);
		String hint = " " + InputPicConst.Input + " " + str;
		SpannableString span = new SpannableString(hint);
		ImageSpan iSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		int start = hint.indexOf(InputPicConst.Input);
		span.setSpan(iSpan, start, start + InputPicConst.Input.length(),
				SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
		return span;
	}
	
	/**
	 * Md5加密
	 * @param string
	 */
	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}
	
	/**
	 * 检查当前网络是否可用
	 * @param activity
	 * @return
	 */
	public static boolean isNetworkAvailable(Activity activity) {
		Context context = activity.getApplicationContext();
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 隐藏软键盘
	 * @param context
	 * @param view
	 */
	public static void hideSoftInput(Context context, View view) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);//隐藏软键盘
	}
	
	public static int getTaskResId(int yxj) {
		int resId = R.drawable.shape_ico_blue;
		switch (yxj) {
			case 1:
				resId = R.drawable.shape_ico_blue;
				break;
			case 2:
				resId = R.drawable.shape_ico_orange;
				break;
			case 3:
				resId = R.drawable.shape_ico_red;
				break;
		}
		return resId;
	}
	
	public static int getScheduleResId(int color) {
		int resId = R.drawable.round_cal_bg_0;
		switch (color) {
			case 1:
				resId = R.drawable.round_cal_bg_1;
				break;
			case 2:
				resId = R.drawable.round_cal_bg_2;
				break;
			case 3:
				resId = R.drawable.round_cal_bg_3;
				break;
			case 4:
				resId = R.drawable.round_cal_bg_4;
				break;
		}
		return resId;
	}

	/**
	 * C#时间转为Java的字符串
	 * @param template  为目标时间的格式
	 * @param milliseconds  时间long字符串
	 */
	public static String time2String(String template, String milliseconds) {
		try {
			milliseconds = milliseconds.substring(6, milliseconds.length() - 7);
			Date date = new Date(Long.parseLong(milliseconds));
			SimpleDateFormat dateFormat1 = new SimpleDateFormat(template,
					Locale.getDefault());
			return dateFormat1.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return milliseconds;
		}
	}

	/**
	 * @param template  为目标时间的格式
	 * @param curtemplate  为当前的时间格式
	 * @param milliseconds  时间字符串
	 */
	public static String time2String(String template, String curtemplate, String milliseconds) {
		SimpleDateFormat dateFormat1 = new SimpleDateFormat(template, Locale.getDefault());
		SimpleDateFormat dateFormat2 = new SimpleDateFormat(curtemplate, Locale.getDefault());
		Date date = null;
		try {
			date = dateFormat2.parse(milliseconds);
			return dateFormat1.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * C#时间转为Date
	 * @param milliseconds  为目标时间的格式
	 * @return
	 */
	public static Date string2Time(String milliseconds) {
		try {
			milliseconds = milliseconds.substring(6, milliseconds.length() - 7);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(Long.parseLong(milliseconds));
			return calendar.getTime();
		} catch (Exception e) {
			e.printStackTrace();
			return new Date();
		}
	}

	/**
	 * 转为C#日期
	 * @param date
	 * @return
	 */
	public static String time2CSharp(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("Z");
		String zone = format.format(date);
		return "/Date(" + date.getTime() + zone + ")/";
	}

	/**
	 * Files 转换成 UserNoteFile
	 * @param files
	 * @return
	 */
	public static UserNote.UserNoteFile files2UserNoteFile(ApiEntity.Files files) {
		UserNote.UserNoteFile file = new UserNote.UserNoteFile();
		if (files != null) {
			file.FileId = files.ID;
			file.Url = files.Url;
			file.FileName = files.Name;
			file.Length = files.Length;
			file.CreateUser = files.Creator;
		}
		return file;
	}
}
