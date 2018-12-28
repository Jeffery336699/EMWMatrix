package cc.emw.mobile.chat.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.util.Log;

import com.farsunset.cim.client.android.CIMEventBroadcastReceiver;
import com.farsunset.cim.client.android.CIMListenerManager;
import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;
import com.mingle.headsUp.HeadsUp;
import com.mingle.headsUp.HeadsUpManager;

import org.json.JSONObject;

import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.calendar.CalendarInfoActivity2;
import cc.emw.mobile.chat.ChatActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.model.bean.Task;
import cc.emw.mobile.contact.GroupInActivity;
import cc.emw.mobile.contact.GroupJoinActivity;
import cc.emw.mobile.dynamic.DynamicDetailActivity;
import cc.emw.mobile.entity.GroupInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.ApiEnum.MessageType;
import cc.emw.mobile.task.activity.TaskDetailActivity;
import cc.emw.mobile.util.PrefsUtil;

/**
 * 消息入口，所有消息都会经过这里
 */
public final class CustomCIMMessageReceiver extends CIMEventBroadcastReceiver {
//    private static final IChatMsgDBModel mChatMsgDao= new ChatMsgDBModelImpl();
//    private void sendMessage2DB(Message message) {
//        Log.d("sunny----->","message="+message.getType());
//        if (message.getType() == ChatContent.DEFAULT_MSG
//                || message.getType() == ChatContent.SCHEDULE_MSG
//                || message.getType() == ApiEnum.MessageType.Image
//                || message.getType() == ApiEnum.MessageType.Audio
//                || message.getType() == ApiEnum.MessageType.Video
//                || message.getType() == ApiEnum.MessageType.Attach
//                || message.getType() == ApiEnum.MessageType.Flow
//                || message.getType() == ApiEnum.MessageType.Task
//                || message.getType() == ApiEnum.MessageType.Share
//                || message.getType() == ChatContent.CHAT_LOCATION
//                || message.getType() == ChatContent.CHAT_SHARE_LOCATION
//                || message.getType() == ChatContent.IMPROTANCE_MSG
//                || message.getType() == ChatContent.DYNAMIC
//                || message.getType() == 4) {
//            mChatMsgDao.addMsgToDB(message, 0, false);
//            Log.d("sunny----->","完成插入逻辑="+message.getContent());
//        }
//    }

    @Override
    public void onMessageReceived(String message) {
        Log.d("pp", "onMessageReceived：" + message);
        // 用户上下线消息过滤
        if (message != null) {
            try {
                Message msg = null;
                if (message.lastIndexOf("}{") > 0) {
                    message = message.substring(message.lastIndexOf("}{") + 1);
                    Log.d("pp", "onMessageReceived：sub:" + message);
                }
                if (message.startsWith("{") && message.endsWith("}")) {
                    msg = new Gson().fromJson(message, Message.class);
//                    this.sendMessage2DB(msg);
                    if (MessageType.LoginIn != msg.getType()
                            && MessageType.LoginOut != msg.getType()) {
                        Log.d("px", "message=" + message);
                        EMWApplication.onMessageReceive = 1;
                        for (int index = 0; index < CIMListenerManager
                                .getCIMListeners().size(); index++) {
                            CIMListenerManager.getCIMListeners().get(index)
                                    .onMessageReceived(message);
                        }
                        if (CIMListenerManager.getCIMListeners().size() > 0)
                            showNotify(context, message);
//                    if (isInBackground(context)) {
//                    }
                    }
                } else {
                    EMWApplication.tempMsg.append(message);
                    Log.d("px", "temp=" + EMWApplication.tempMsg.toString());
                    if (EMWApplication.tempMsg.toString().startsWith("{") && EMWApplication.tempMsg.toString().endsWith("}")) {
                        msg = new Gson().fromJson(EMWApplication.tempMsg.toString(), Message.class);
                        if (MessageType.LoginIn != msg.getType()
                                && MessageType.LoginOut != msg.getType()) {
//                            this.sendMessage2DB(msg);
                            for (int index = 0; index < CIMListenerManager
                                    .getCIMListeners().size(); index++) {
                                CIMListenerManager.getCIMListeners().get(index)
                                        .onMessageReceived(EMWApplication.tempMsg.toString());
                            }
                            if (CIMListenerManager.getCIMListeners().size() > 0)
                                showNotify(context, EMWApplication.tempMsg.toString());
                        }
                        EMWApplication.tempMsg.delete(0, EMWApplication.tempMsg.length());
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
                EMWApplication.tempMsg.delete(0, EMWApplication.tempMsg.length());
                Log.d("px", "解析异常！" + message);
            }
        }
    }

    // 当手机网络连接状态变化时，会执行onNetworkChanged
    @Override
    public void onNetworkChanged(NetworkInfo info) {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index).onNetworkChanged(info);
        }
    }

    // 当收到sendbody的响应时，会执行onReplyReceived
    @Override
    public void onReplyReceived(String body) {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index).onReplyReceived(body);
        }
    }

    private boolean isHide = false;//用于做群组个性化屏蔽的字段 true  不推送  false  推送。
    private void showNotify(final Context context, String msg) {

        try {
            final Message message = new Gson().fromJson(msg, Message.class);

            if (message.getType() == MessageType.Task) {// 后台任务推送
                Task task = new Gson().fromJson(message.getContent(), Task.class);
                /*NotificationManager nm = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Builder builder = new Builder(
                        context).setSmallIcon(R.drawable.ic_launcher)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentTitle(context.getString(R.string.new_task)).setContentText(task.getTitle());*/
                Intent intent = new Intent(context, TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.TASK_ID, task.getID());
                intent.putExtra("msg_id", message.getID());
                intent.putExtra("expand_anim", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//                nm.notify(message.getSenderID(), builder.build());

                HeadsUpManager manage = HeadsUpManager.getInstant(context);
                HeadsUp.Builder builder = new HeadsUp.Builder(context);
                builder.setContentTitle(context.getString(R.string.new_task)).setContentText(task.getTitle()).setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                        .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                manage.notify(message.getID(), builder.buildHeadUp());
            } else if (MessageType.RevTalker == message.getType()) { //回复通知√
                JSONObject jsonObject = new JSONObject(message.getContent());
                String title = jsonObject.getString("OldContent");
                int noteID = jsonObject.getInt("TopID");
                /*NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Builder builder = new Builder(
                        context).setSmallIcon(R.drawable.ic_launcher)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle("回复提醒").setContentText("你的动态【" + title + "】收到了新的评论。").setAutoCancel(true);*/
                Intent intent = new Intent(context, DynamicDetailActivity.class);
                intent.putExtra("note_id", noteID);
                intent.putExtra("msg_id", message.getID());
                intent.putExtra("expand_anim", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//                manager.notify(message.getSenderID(), builder.build());

                HeadsUpManager manage = HeadsUpManager.getInstant(context);
                HeadsUp.Builder builder = new HeadsUp.Builder(context);
                builder.setContentTitle("回复提醒").setContentText("你的动态【" + title + "】收到了新的评论。").setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                        .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                manage.notify(message.getID(), builder.buildHeadUp());
            } else if (MessageType.EnjoyTalker == message.getType()) { //收藏通知√
                JSONObject jsonObject = new JSONObject(message.getContent());
                String title = jsonObject.getString("OldContent");
                int noteID = jsonObject.getInt("TopID");
                String enjoy = jsonObject.getInt("ControlValue") == 0 ? "收藏" : "取消收藏";
                UserInfo userInfo = EMWApplication.personMap.get(message.getSenderID());
                String content = (userInfo != null ? userInfo.Name : "有人") + "" + enjoy + "动态【" + title + "】。";
                /*NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Builder builder = new Builder(
                        context).setSmallIcon(R.drawable.ic_launcher)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle("收藏提醒").setContentText(content).setAutoCancel(true);*/
                Intent intent = new Intent(context, DynamicDetailActivity.class);
                intent.putExtra("note_id", noteID);
                intent.putExtra("msg_id", message.getID());
                intent.putExtra("expand_anim", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//                manager.notify(message.getSenderID(), builder.build());

                HeadsUpManager manage = HeadsUpManager.getInstant(context);
                HeadsUp.Builder builder = new HeadsUp.Builder(context);
                builder.setContentTitle("收藏提醒").setContentText(content).setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                        .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                manage.notify(message.getID(), builder.buildHeadUp());
            } else if (MessageType.FollowMe == message.getType()) { //关注通知√
                JSONObject jsonObject = new JSONObject(message.getContent());
                String userName = jsonObject.getString("UserName");
                int userID = jsonObject.getInt("UserID");
                int controlValue = jsonObject.getInt("ControlType");
                /*NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Builder builder = new Builder(
                        context).setSmallIcon(R.drawable.ic_launcher)
                        .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                        .setContentTitle("关注提醒").setContentText(userName + " " + (controlValue == 1 ? "关注了你。" : "取消关注了你。")).setAutoCancel(true);*/
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("SenderID", userID);
                intent.putExtra("type", 1);
                intent.putExtra("name", userName);
                intent.putExtra("expand_anim", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//                manager.notify(message.getSenderID(), builder.build());

                HeadsUpManager manage = HeadsUpManager.getInstant(context);
                HeadsUp.Builder builder = new HeadsUp.Builder(context);
                builder.setContentTitle("关注提醒").setContentText(userName + " " + (controlValue == 1 ? "关注了你。" : "取消关注了你。")).setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                        .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                manage.notify(message.getSenderID(), builder.buildHeadUp());
            } else if (MessageType.TellOffTask == message.getType()) { //任务分派通知√
                //{"BusType":1,"CompanyCode":"emw","Content":"{\"ControlType\":3,\"Name\":'Test',\"TaskID\":305182}","CreateTime":"/Date(1472892243752+0800)/","GroupID":0,"ID":0,"IsNew":0,"ReceiverID":103922,"SenderID":103929,"Type":16,"UserID":103922}
                JSONObject jsonObject = new JSONObject(message.getContent());
                String title = jsonObject.getString("Name");
                int taskID = jsonObject.getInt("TaskID");
                /*NotificationManager nm = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Builder builder = new Builder(
                        context).setSmallIcon(R.drawable.ic_launcher)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentTitle("新任务提醒").setContentText("你被分派了一个新的任务【" + title + "】").setAutoCancel(true);*/
                Intent intent = new Intent(context, TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.TASK_ID, taskID);
                intent.putExtra("msg_id", message.getID());
                intent.putExtra("expand_anim", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//                nm.notify(message.getSenderID(), builder.build());

                HeadsUpManager manage = HeadsUpManager.getInstant(context);
                HeadsUp.Builder builder = new HeadsUp.Builder(context);
                builder.setContentTitle("新任务提醒").setContentText("你被分派了一个新的任务【" + title + "】").setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                        .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                manage.notify(message.getID(), builder.buildHeadUp());
            } else if (MessageType.CallProject == message.getType()) { //新增团队协作,通知圈内人员
                //{"BusType":1,"CompanyCode":"emw","Content":"{\"ControlType\":4,\"ProjectName\":'aaa',\"GroupID\":321944,\"ProjectID\":328571,\"UserID\":103929,\"UserName\":'周涛',\"UserImage\":'753297a399df4eb984dd9b6d430c27bf.png'}","CreateTime":"/Date(1473499165174+0800)/","GroupID":0,"ID":328572,"IsNew":0,"ReceiverID":103922,"SenderID":103929,"Type":17,"UserID":103922}
            } else if (MessageType.CallGroup == message.getType()) { //加入新建项目组通知

            } else if (MessageType.RequestEditor == message.getType()) { //在线Office请求编辑

            } else if (MessageType.CallbackEditor == message.getType()) { //在线Office回复请求编辑

            } else if (MessageType.SynchronousUpdate == message.getType()) { //在线Office数据同步更新

            } else if (MessageType.SynchronousUpdate == message.getType()) { //在线Office数据同步更新

            } else if (MessageType.StopEdit == message.getType()) { //在线Office停止编辑

            } else if (MessageType.QRConnect == message.getType()) { //

            } else if (MessageType.QRLogin == message.getType()) { //

            } else if (MessageType.TellFromTalker == message.getType()) { //新发布Talker通知
                JSONObject jsonObject = new JSONObject(message.getContent());
                String title = jsonObject.getString("Content");
                int noteID = jsonObject.getInt("TalkerID");
                UserInfo userInfo = EMWApplication.personMap.get(message.getSenderID());
                String content = (userInfo != null ? userInfo.Name : "") + " 发布了新动态【" + title + "】。";
                /*NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Builder builder = new Builder(
                        context).setSmallIcon(R.drawable.ic_launcher)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle("新动态提醒").setContentText(content).setAutoCancel(true);*/
                Intent intent = new Intent(context, DynamicDetailActivity.class);
                intent.putExtra("note_id", noteID);
                intent.putExtra("msg_id", message.getID());
                intent.putExtra("expand_anim", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//                manager.notify(message.getSenderID(), builder.build());

                HeadsUpManager manage = HeadsUpManager.getInstant(context);
                HeadsUp.Builder builder = new HeadsUp.Builder(context);
                builder.setContentTitle("新动态提醒").setContentText(content).setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                        .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                manage.notify(message.getID(), builder.buildHeadUp());
            } else if (MessageType.CallTaskReply == message.getType()) { //任务评论通知√
                String[] strs = message.getContent().split("\\|\\|");
                /*NotificationManager nm = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Builder builder = new Builder(
                        context).setSmallIcon(R.drawable.ic_launcher)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentTitle("任务回复提醒").setContentText(strs[0]).setAutoCancel(true);*/
                Intent intent = new Intent(context, TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.TASK_ID, Integer.valueOf(strs[1]));
                intent.putExtra("msg_id", message.getID());
                intent.putExtra("expand_anim", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//                nm.notify(message.getSenderID(), builder.build());

                HeadsUpManager manage = HeadsUpManager.getInstant(context);
                HeadsUp.Builder builder = new HeadsUp.Builder(context);
                builder.setContentTitle("任务回复提醒").setContentText(strs[0]).setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                        .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                manage.notify(message.getID(), builder.buildHeadUp());
            } else if (message.getType() == MessageType.CallSchedule) {  //日程提醒
                String[] strs = message.getContent().split("\\|\\|");
                /*NotificationManager nm = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Builder builder = new Builder(
                        context).setSmallIcon(R.drawable.ic_launcher)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentTitle("日程提醒").setContentText(strs[0]).setAutoCancel(true);*/
                Intent intent = new Intent(context, CalendarInfoActivity2.class);
                intent.putExtra(CalendarInfoActivity2.CALENDARID, Integer.valueOf(strs[1]));
                intent.putExtra("expand_anim", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//                nm.notify(message.getSenderID(), builder.build());

                HeadsUpManager manage = HeadsUpManager.getInstant(context);
                HeadsUp.Builder builder = new HeadsUp.Builder(context);
                builder.setContentTitle("日程提醒").setContentText(strs[0]).setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                        .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                manage.notify(message.getSenderID(), builder.buildHeadUp());
            } else if (MessageType.JoinGroup == message.getType()) { //申请加入群组通知√
                JSONObject jsonObject = new JSONObject(message.getContent());
                String groupName = jsonObject.getString("GroupName");
                int groupID = jsonObject.getInt("GroupID");
                String userName = jsonObject.getString("UserName");

                /*Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("SenderID", message.getSenderID());
                intent.putExtra("GroupID", groupID);
                intent.putExtra("name", groupName);
                intent.putExtra("type", 2);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);*/
                Intent intent = new Intent(context, GroupJoinActivity.class);
                intent.putExtra("message", message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

                HeadsUpManager manage = HeadsUpManager.getInstant(context);
                HeadsUp.Builder builder = new HeadsUp.Builder(context);
                builder.setContentTitle("圈子提醒").setContentText(userName + " 申请加入圈子【" + groupName + "】。").setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                        .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                manage.notify(message.getID(), builder.buildHeadUp());
            } else if (MessageType.RefuseJoinGroup == message.getType()) { //拒绝/同意加入群组通知√
                //{"BusType":1,"CompanyCode":"emw","Content":"{\"DisAgreeText\":\"\",\"GroupName\":\"Azrjt\",\"UserImage\":\"c0c907bae256428a93df2d6f502c3813.png\",\"UserName\":\"庄少波\",\"ControlType\":6,\"GroupID\":166670,\"State\":2,\"Type\":1,\"UserID\":103922}","CreateTime":"/Date(1472886471909+0800)/","GroupID":0,"ID":0,"IsNew":0,"ReceiverID":103922,"SenderID":103929,"Type":25,"UserID":103922}
                JSONObject jsonObject = new JSONObject(message.getContent());
                String userName = jsonObject.getString("UserName");
                String groupName = jsonObject.getString("GroupName");
                int state = jsonObject.getInt("State");
                /*NotificationManager nm = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                Builder builder = new Builder(
                        context).setSmallIcon(R.drawable.ic_launcher)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentTitle("圈子提醒").setContentText(userName + (state == 2 ? " 拒绝" : " 同意") + "你申请加入圈子【" + groupName + "】。").setAutoCancel(true);*/
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("SenderID", message.getSenderID());
                intent.putExtra("name", userName);
                intent.putExtra("type", 1);
                intent.putExtra("expand_anim", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//                nm.notify(message.getSenderID(), builder.build());

                HeadsUpManager manage = HeadsUpManager.getInstant(context);
                HeadsUp.Builder builder = new HeadsUp.Builder(context);
                builder.setContentTitle("圈子提醒").setContentText(userName + (state == 2 ? " 拒绝" : " 同意") + "你申请加入圈子【" + groupName + "】。").setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                        .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                manage.notify(message.getSenderID(), builder.buildHeadUp());
            } else {// 后台消息推送
                int number = message.getGroupID();
                if (number == 0) {
                    if (message.getSenderID() == EMWApplication.currentChatUid) {
                        context.sendBroadcast(new Intent(ChatContent.REFRESH_CHAT_RECEIVED_MSG).putExtra("msg_json", msg));
                        return;
                    }
                    UserInfo user = EMWApplication.personMap.get(message.getSenderID());
                    String name = user.Name;
                    String content = message.getContent();
                    if (content != null && content.contains("已退出") && user != null && user.ID == PrefsUtil.readUserInfo().ID) {
                        //退出圈子成员为当前用户，不在通知栏提示
                        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(message.getSenderID(), null);
                        return;
                    }

                    if (content != null && content.contains("[face:")) {
                        content = "[表情]";
                    } else if (message.getType() == MessageType.Attach) {
                        content = "[附件]";
                    } else if (message.getType() == MessageType.Image) {
                        content = "[图片]";
                    } else if (message.getType() == MessageType.Audio) {
                        content = "[语音]";
                    } else if (message.getType() == MessageType.Video) {
                        content = "[视频]";
                    } else if (message.getType() == MessageType.Flow) {
                        content = "[流程]";
                    } else if (message.getType() == MessageType.Share) {
                        content = "[分享]";
                    } else if (message.getType() == MessageType.LeaveGroup) {
                        String[] strs = message.getContent().split("\\|\\|");
                        content = strs[0];
                        Intent intent = new Intent(context, GroupInActivity.class);
                        intent.putExtra("GroupID", Integer.valueOf(strs[1]));
                        intent.putExtra("msg_id", message.getID());
                        intent.putExtra("expand_anim", false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        HeadsUpManager manage = HeadsUpManager.getInstant(context);
                        HeadsUp.Builder builder = new HeadsUp.Builder(context);
                        builder.setContentTitle(name).setContentText(content).setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                                .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                        manage.notify(message.getID(), builder.buildHeadUp());
                        return;
                    } else if (message.getType() == MessageType.Robot || message.getType() == MessageType.RobotSchedule) {
                        content = "[智能聊天回复]";
                    } else if (message.getType() == MessageType.CHAT_LOCATION) {
                        content = "[位置]";
                    } else if (message.getType() == ChatContent.DYNAMIC) {
                        content = "[分享消息]";
                    } else if (message.getType() == ChatContent.CHAT_SHARE_LOCATION) {
                        content = "[共享位置]";
                    }

                    /*final NotificationManager manager = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    Builder builder = new Builder(
                            context).setSmallIcon(R.drawable.ic_launcher)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentTitle(name).setContentText(content).setAutoCancel(true);*/

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("SenderID", message.getSenderID());
                    intent.putExtra("name", name);
                    intent.putExtra("type", 1);
                    intent.putExtra("unReadNum", 1);
                    intent.putExtra("expand_anim", false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    /*builder.setContentIntent(pendingIntent);
                    manager.notify(message.getSenderID(), builder.build());*/

                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !"Xiaomi".equalsIgnoreCase(Build.BRAND)) { //5.0以上才有效果，以下都是直接跳转到界面里
                        builder.setFullScreenIntent(pendingIntent,true);//不显示在通知栏而是以横幅的模式显示在其他应用上方
                        manager.notify(-message.getSenderID(), builder.build());
                    }*/
                    if (MessageType.PhoneStateMsg != message.getType()) {
                        HeadsUpManager manage = HeadsUpManager.getInstant(context);
                        HeadsUp.Builder builder = new HeadsUp.Builder(context);
                        builder.setContentTitle(name).setContentText(content).setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                                .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                        manage.notify(message.getSenderID(), builder.buildHeadUp());
                    }
                } else {
                    if (number == EMWApplication.currentChatUid) {
                        context.sendBroadcast(new Intent(ChatContent.REFRESH_CHAT_RECEIVED_MSG).putExtra("msg_json", msg));
                        return;
                    }
                    GroupInfo info = EMWApplication.groupMap.get(message.getGroupID());
                    UserInfo user = EMWApplication.personMap.get(message.getSenderID());
                    String content = message.getContent();
                    if (content != null && content.contains("[face:")) {
                        content = "[表情]";
                    } else if (message.getType() == MessageType.Attach) {
                        content = "[附件]";
                    } else if (message.getType() == MessageType.Image) {
                        content = "[图片]";
                    } else if (message.getType() == MessageType.Audio) {
                        content = "[语音]";
                    } else if (message.getType() == MessageType.Video) {
                        content = "[视频]";
                    } else if (message.getType() == MessageType.Flow) {
                        content = "[流程]";
                    } else if (message.getType() == MessageType.Share) {
                        content = "[分享]";
                    } else if (message.getType() == MessageType.Robot || message.getType() == MessageType.RobotSchedule) {
                        content = "[智能聊天回复]";
                    } else if (message.getType() == MessageType.CHAT_LOCATION) {
                        content = "[位置]";
                    } else if (message.getType() == ChatContent.DYNAMIC) {
                        content = "[分享消息]";
                    } else if (message.getType() == ChatContent.CHAT_SHARE_LOCATION) {
                        content = "[共享位置]";
                    }

                    /*final NotificationManager manager = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    Builder builder = new Builder(
                            context).setSmallIcon(R.drawable.ic_launcher)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentTitle(info.Name).setContentText(user != null ? user.Name + ":" + content : content).setAutoCancel(true);*/

                    PendingIntent pendingIntent = null;
                    if (content != null && content.contains("已退出") && user != null && user.ID == PrefsUtil.readUserInfo().ID) {

                    } else {
                        if (message.getGroupID() != 0) {
                            List<Integer> msgHideList = info.MsgHideList;
                            UserInfo mainUser = PrefsUtil.readUserInfo();
                            for (int id : msgHideList) {
                                if (id == mainUser.ID) {
                                    if(message.getType()==4 &&!message.getContent().contains("@"+mainUser.Name)) {
                                        isHide = true;
                                    }else{
                                        isHide = false;
                                    }
                                    break;
                                }
                            }
                        }
                        if (message.getGroupID() != 0 && isHide) {
                            isHide = false;
                        } else {
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("SenderID", message.getSenderID());
                            intent.putExtra("GroupID", info.ID);
                            intent.putExtra("unReadNum", 1);
                            intent.putExtra("type", 2);
                            intent.putExtra("expand_anim", false);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            pendingIntent = PendingIntent.getActivity(context, 0,
                                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        builder.setContentIntent(pendingIntent);
                        }
//                    manager.notify(message.getSenderID(), builder.build());
                        if (MessageType.PhoneStateMsg != message.getType()) {
                            HeadsUpManager manage = HeadsUpManager.getInstant(context);
                            HeadsUp.Builder builder = new HeadsUp.Builder(context);
                            builder.setContentTitle(info.Name).setContentText(user != null ? user.Name + ":" + content : content).setAutoCancel(true)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setSmallIcon(R.drawable.ic_launcher) //要显示通知栏通知,这个一定要设置
                                    .setContentIntent(pendingIntent); //2.3 一定要设置这个参数,负责会报错
                            manage.notify(message.getSenderID(), builder.buildHeadUp());
                        }
                    }
                }
            }
            /*//定时取消横幅通知
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(-message.getSenderID());
                }
            }, 5000);*/
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("px", "Receiver--解析异常！" + e.toString());
        }

    }

    public void onCIMConnectionSucceed() {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index).onCIMConnectionSucceed();
        }
    }

    @Override
    public void onConnectionStatus(boolean arg0) {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index).onConnectionStatus(arg0);
        }
    }

    @Override
    public void onCIMConnectionClosed() {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index).onCIMConnectionClosed();
        }
    }

}
