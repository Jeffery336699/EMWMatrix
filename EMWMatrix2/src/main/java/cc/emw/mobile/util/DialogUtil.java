package cc.emw.mobile.util;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.farsunset.cim.client.android.CIMPushManager;
import com.githang.androidcrash.util.AppManager;
import com.zf.iosdialog.widget.AlertDialog;
import com.zf.iosdialog.widget.DownloadDialog;

import org.apache.http.HttpStatus;

import java.io.File;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.entity.Version;
import cc.emw.mobile.login.LoginActivity;
import cn.aigestudio.downloader.bizs.DLError;
import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.SimpleDListener;

/**
 * Dialog工具
 * @author shaobo.zhuang
 *
 */
public class DialogUtil {

    /**
     * 弹出提示对话框
     * @param context
     * @param tip
     */
    public static void showTipDialog(Context context, String tip) {
        AlertDialog dialog = new AlertDialog(context).builder();
        dialog.setMsg(tip).setPositiveButton(context.getString(R.string.ok), new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    /**
     * 弹出注销对话框
     * @param context
     */
    public static void showLogoutDialog(final Context context) {
        AlertDialog dialog = new AlertDialog(context).builder();
        dialog.setMsg(context.getString(R.string.main_logout_tips))
                .setPositiveButton(context.getString(R.string.ok), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CIMPushManager.stop(context);
                        PrefsUtil.cleanUserInfo();
                        Intent intent = new Intent(
                                context.getApplicationContext(),
                                LoginActivity.class);
                        PendingIntent restartIntent = PendingIntent
                                .getActivity(
                                        context.getApplicationContext(),
                                        0,
                                        intent,
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC,
                                System.currentTimeMillis() + 0,
                                restartIntent); // 0毫秒钟后重启应用
                        AppManager
                                .AppExit(context.getApplicationContext());// 退出程序
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }

    /**
     * 弹出发现新版本对话框
     * @param context
     * @param ver
     */
    public static void showVersionDialog(final Context context, final Version ver) {
        new AlertDialog(context).builder().setTitle(context.getString(R.string.login_dialog_title))
                .setMsg(ver.explainContent).setMsgGravity(Gravity.LEFT).setCancelable(false)
                .setPositiveButton(context.getString(R.string.download) + "(" + FileUtil.getReadableFileSize(ver.size) + ")", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDownloadDialog(context, ver);
                    }
                }).setNegativeButton(context.getString(R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    /**
     * 弹出下载新版本对话框
     * @param context
     * @param ver
     */
    private static void showDownloadDialog(final Context context, Version ver) {
        if (ver.name.indexOf(".apk") == -1) {
            ver.name = ver.name + ".apk";
        }

        final DownloadDialog dialog = new DownloadDialog(context).builder();
        final int[] length = new int[1];
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Toast.makeText(context, "该资源已在下载中...", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        int progress = msg.arg1;
                        dialog.getProgressBar().setMax(length[0]);
                        dialog.getProgressBar().setProgress(progress);
                        dialog.setMsg(FileUtil.getReadableFileSize(progress) + "/" + FileUtil.getReadableFileSize(length[0]));
                        break;
                    case 4:
                        Toast.makeText(context, "您要下载的资源可能已被删除！", Toast.LENGTH_LONG).show();
                        break;
                    case 5:
                        Toast.makeText(context, "下载失败！", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }

            }
        };
        final String url = "https://pay.weixin.qq.com/wiki/doc/api/download/wechat_sdk_sample_android_v3_pay.zip";
//    			ver.getUrl();
        final String dir = EMWApplication.filePath + ver.name;
        DLManager.getInstance(context).dlStart(url, dir, new SimpleDListener() {

            @Override
            public void onPrepare() {
                dialog.setTitle(context.getString(R.string.login_dialog_downtitle))
                        .setMsg("")
                        .setCancelable(false)
                        .setNegativeButton(context.getString(R.string.cancel), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DLManager.getInstance(context).dlCancel(url);//取消下载
                            }
                        }).show();
            }

            @Override
            public void onStart(String fileName, String realUrl, int fileLength) {
                super.onStart(fileName, realUrl, fileLength);
                length[0] = fileLength;
                Message msg = handler.obtainMessage();
                msg.what = 2;
                msg.arg1 = 0;
                handler.sendMessage(msg);
            }

            @Override
            public void onProgress(int progress) {
                Message msg = handler.obtainMessage();
                msg.what = 2;
                msg.arg1 = progress;
                handler.sendMessage(msg);
            }

            @Override
            public void onStop(int progress) {
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.arg1 = progress;
                handler.sendMessage(msg);
            }

            @Override
            public void onFinish(File file) {
                dialog.dismiss();
                Intent intent = new Intent(); //安装apk
                intent.setAction("android.intent.action.VIEW");
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                context.startActivity(intent);
            }

            @Override
            public void onError(int status, String error) {
                if (status == HttpStatus.SC_NOT_FOUND) {
                    handler.sendEmptyMessage(4);
                } else if (status == DLError.ERROR_REPEAT_URL) {
                    handler.sendEmptyMessage(1);
                } else {
                    handler.sendEmptyMessage(5);
                }
            }
        });
    }

    public static Dialog createLoadingDialog(Context context, int resId) {
        return createLoadingDialog(context, context.getString(resId));
    }

    /**
     * 加载对话框
     * @param context
     * @param msg 提示信息
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        TextView tipTextView = (TextView) v.findViewById(R.id.loading_tv_tip);
        tipTextView.setText(msg);
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }
}
