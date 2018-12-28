package cc.emw.mobile.chat.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.farsunset.cim.client.android.CIMEventBroadcastReceiver;
import com.farsunset.cim.client.android.CIMListenerManager;
import com.farsunset.cim.client.model.Message;
import com.google.gson.Gson;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.activity.ChatActivity;
import cc.emw.mobile.chat.bean.Task;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.ApiEnum.MessageType;
import cc.emw.mobile.task.TaskDetailActivity;

/**
 * 消息入口，所有消息都会经过这里
 */
public final class CustomCIMMessageReceiver extends CIMEventBroadcastReceiver {

    // 当收到消息时，会执行onMessageReceived，这里是消息第一入口
    @Override
    public void onMessageReceived(String message) {
        Log.d("px", "temp=" + message);
        if (message != null) {
            // 用户上下线消息过滤
            try {
                String temp = message.substring(0, message.indexOf("}") + 1);
                Message msg = new Gson().fromJson(temp, Message.class);
                if (MessageType.LoginIn != msg.getType()
                        && MessageType.LoginOut != msg.getType()) {
                    for (int index = 0; index < CIMListenerManager
                            .getCIMListeners().size(); index++) {
                        CIMListenerManager.getCIMListeners().get(index)
                                .onMessageReceived(message);
                    }
                    if (isInBackground(context)) {
                        showNotify(context, temp);
                    }
                }
            } catch (Exception e) {
                Log.d("px", "解析异常！");
            }
        }
    }

    // 当手机网络连接状态变化时，会执行onNetworkChanged
    @Override
    public void onNetworkChanged(NetworkInfo info) {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index)
                    .onNetworkChanged(info);
        }
    }

    // 当收到sendbody的响应时，会执行onReplyReceived
    @Override
    public void onReplyReceived(String body) {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index)
                    .onReplyReceived(body);
        }
    }

    private void showNotify(Context context, String msg) {

        Message message = new Gson().fromJson(msg, Message.class);

        if (message.getType() == MessageType.Task) {// 后台任务推送
            Task task = new Gson().fromJson(message.getContent(), Task.class);
            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.drawable.ic_launcher)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentTitle(context.getString(R.string.new_task)).setContentText(task.getTitle());
            Intent intent = new Intent(context, TaskDetailActivity.class);
            intent.putExtra(TaskDetailActivity.TASK_ID, task.getID());
            intent.putExtra("isDrawerOpen", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            nm.notify(2, builder.build());

        } else {// 后台消息推送

            UserInfo user = EMWApplication.personMap.get(message.getSenderID());// EMWApplication.groupMap
            String name = user.Name;
            NotificationManager manager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.drawable.ic_launcher)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentTitle(name).setContentText(message.getContent());
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("SenderID", message.getSenderID());
            intent.putExtra("name", name);
            intent.putExtra("isDrawerOpen", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            manager.notify(2, builder.build());
        }
    }

    public void onCIMConnectionSucceed() {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index)
                    .onCIMConnectionSucceed();
        }
    }

    @Override
    public void onConnectionStatus(boolean arg0) {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index)
                    .onConnectionStatus(arg0);
        }
    }

    @Override
    public void onCIMConnectionClosed() {
        for (int index = 0; index < CIMListenerManager.getCIMListeners().size(); index++) {
            CIMListenerManager.getCIMListeners().get(index)
                    .onCIMConnectionClosed();
        }
    }

}
