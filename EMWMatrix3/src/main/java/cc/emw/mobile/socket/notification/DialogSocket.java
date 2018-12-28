package cc.emw.mobile.socket.notification;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.zf.iosdialog.widget.AlertDialog;

import cc.emw.mobile.R;
import cc.emw.mobile.socket.IOSocketTestActivity;
import cc.emw.mobile.socket.bean.DialogSocketBean;

/**
 * @author fmc
 * @package cc.emw.mobile.socket.notification
 * @data on 2018/5/24  13:29
 * @describe TODO
 */

public class DialogSocket {
    public static void createDialogSocket(final Activity activity, DialogSocketBean dialogBean){
        AlertDialog dialog = new AlertDialog(activity).builder();
        dialog.setTitle(activity.getString(R.string.time_manager));
        dialog.setMsg(dialogBean.getMsg());
        dialog.setNegativeButton(activity.getString(R.string.look_details), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看详情，跳转页面
                Intent intent = new Intent(activity, IOSocketTestActivity.class);//测试页面
                activity.startActivity(intent);
            }
        });
        dialog.setPositiveButton(activity.getString(R.string.i_know), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).setCancelable(false).show();
    }
}
