package cc.emw.mobile.me.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zf.iosdialog.widget.AlertDialog;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.entity.CalendarInfo;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.Logger;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;
import cc.emw.mobile.view.SectorProgressView;

public class TwoStepAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UserInfo> mDataList;
    private int countDown = 0;
    private AlertDialog dialog;

    public TwoStepAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(ArrayList<UserInfo> mDataList) {
        this.mDataList = mDataList;
    }

    public void setCountDown(int countDown) {
        this.countDown = countDown;
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_twostep, null);
            vh.codeTv = (TextView) convertView.findViewById(R.id.tv_twostep_code);
            vh.countDownTv = (TextView) convertView.findViewById(R.id.tv_twostep_countdown);
            vh.progressView = (SectorProgressView) convertView.findViewById(R.id.spv);
            vh.usernameTv = (TextView) convertView.findViewById(R.id.tv_twostep_username);
            convertView.setTag(R.id.tag_first, vh);
        } else {
            vh = (ViewHolder) convertView.getTag(R.id.tag_first);
        }

        UserInfo user = mDataList.get(position);
        vh.usernameTv.setText(user.Email);
        vh.progressView.setPercent(countDown);
        if (countDown == 0) {
            getCode(vh.codeTv, user.ID);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView codeTv;
        TextView countDownTv;
        SectorProgressView progressView;
        TextView usernameTv;
    }

    private void getCode(TextView codeTv, int userID) {
        codeTv.setText("--- ---");
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6 ; i++) {
            code.append((int)(Math.random()*10));
            if (i == 2)
                code.append(" ");
        }
        addVerificationCode(codeTv, code.toString(), userID);
        codeTv.setText(code.toString());
    }

    private void addVerificationCode(final TextView codeTv, final String code, int userID) {
        String key = code.replace(" ", "");
        Logger.d("TwoStepAdapter", "key:"+key);
        API.UserAPI.AddVerificationCode(key, userID, new RequestCallback<String>(String.class) {
            @Override
            public void onError(Throwable throwable, boolean b) {
                countDown = 0;
                if (dialog != null && dialog.isShowing()) {

                } else {
                    dialog = new AlertDialog(mContext).builder();
                    dialog.setCancelable(true).setMsg("刷新动态码失败！");
                    dialog.setPositiveButton("关闭", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Activity)mContext).finish();
                        }
                    }).show();
                }
            }

            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                    codeTv.setText(code);
                    countDown = 0;
                } else {
                    onError(new Throwable(""), false);
                }
            }
        });
    }
}
