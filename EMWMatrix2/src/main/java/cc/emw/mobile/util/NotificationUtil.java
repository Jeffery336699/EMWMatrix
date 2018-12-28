package cc.emw.mobile.util;

import android.content.Context;
import android.content.Intent;
import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.service.DLService;

/**
 * 通知工具类
 * 
 * @author shaobo.zhuang
 */
public final class NotificationUtil {
	public static void notificationForDLAPK(Context context, String url) {
		notificationForDLAPK(context, url, EMWApplication.filePath);
	}

	public static void notificationForDLAPK(Context context, String url,
			String path) {
		notificationForDLAPK(context, url, path, "");
	}

	public static void notificationForDLAPK(Context context, String url,
			String path, String name) {
		Intent intent = new Intent(context, DLService.class);
		intent.putExtra("url", url);
		intent.putExtra("path", path);
		intent.putExtra("name", name);
		intent.putExtra("id", (int) (Math.random() * 1024));
		context.startService(intent);
	}

	/*
	 * public static void notificationForMessage(Context context, SystemMsg msg)
	 * { // 内容不等于企业代码 和 内容不为空 和 设置需要显示通知 if
	 * (!PrefsUtil.readUserInfo().getCompanyCode().equalsIgnoreCase(msg.Content)
	 * && !TextUtils.isEmpty(msg.Content) && PrefsUtil.isNotice()) { //
	 * 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag PowerManager
	 * powerManager = ((PowerManager)
	 * context.getSystemService(Context.POWER_SERVICE)); PowerManager.WakeLock
	 * wl = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
	 * PowerManager.SCREEN_DIM_WAKE_LOCK, "Test"); wl.acquire();// 点亮屏幕
	 * wl.release();// 重新启用自动加锁
	 * 
	 * if (msg.Type == SystemMessageTypes.Message) { int uid = msg.FromUser; //
	 * 通知栏不弹出当前聊天界面用户发送的消息 if (AppManager.currentActivity() instanceof
	 * ChatActivity && uid == EMWApplication.currentChatUid) { return; } String
	 * content = "FILE".equalsIgnoreCase(msg.Title)?"[文件]":msg.Content; String
	 * name = ""; if (EMWApplication.personList != null &&
	 * EMWApplication.personList.size() > 0) { for (SimpleUser user :
	 * EMWApplication.personList) { if (msg.FromUser == user.getID()) { name =
	 * user.getName(); break; } } } NotificationManager nm =
	 * (NotificationManager)
	 * context.getSystemService(Context.NOTIFICATION_SERVICE);
	 * NotificationCompat.Builder builder = new
	 * NotificationCompat.Builder(context)
	 * .setSmallIcon(R.drawable.ic_launcher).
	 * setDefaults(Notification.DEFAULT_ALL)
	 * .setContentTitle(name).setContentText(content); Intent intent = new
	 * Intent(context, ChatActivity.class);
	 * intent.putExtra(ChatActivity.INTENT_EXTRA_UID, String.valueOf(uid));
	 * intent.putExtra(ChatActivity.INTENT_EXTRA_NAME, name);
	 * intent.putExtra(ChatActivity.INTENT_EXTRA_MSGID, String.valueOf(msg.ID));
	 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); PendingIntent
	 * pendingIntent =
	 * PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT
	 * ); //使用PendingIntent builder.setContentIntent(pendingIntent);
	 * builder.setAutoCancel(true); nm.notify(uid, builder.build());
	 * 
	 * context.sendBroadcast(new
	 * Intent(MainActivity.ACTION_REFRESH_SYSMSG_LIST)); } else { int id =
	 * msg.ID; String name = "新消息"; NotificationManager nm =
	 * (NotificationManager)
	 * context.getSystemService(Context.NOTIFICATION_SERVICE);
	 * NotificationCompat.Builder builder = new
	 * NotificationCompat.Builder(context)
	 * .setSmallIcon(R.drawable.ic_launcher).
	 * setDefaults(Notification.DEFAULT_ALL)
	 * .setContentTitle(name).setContentText(msg.Content); Intent intent = new
	 * Intent(context, MainActivity.class); intent.putExtra("isDrawerOpen",
	 * true); intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); PendingIntent
	 * pendingIntent =
	 * PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT
	 * ); //使用PendingIntent builder.setContentIntent(pendingIntent);
	 * builder.setAutoCancel(true); nm.notify(id, builder.build()); }
	 * 
	 * } }
	 */

}
