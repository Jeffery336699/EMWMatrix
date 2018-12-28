package cc.emw.mobile.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview2.TimePickerView;
import com.bigkoo.pickerview2.listener.OnDismissListener;
import com.google.gson.Gson;
import com.zf.iosdialog.widget.AlertDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.me.entity.College;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.view.SwipeBackScrollView;

@ContentView(R.layout.activity_education)
public class EducationActivity extends BaseActivity {

    @ViewInject(R.id.swipe_scroll)
    private SwipeBackScrollView swipeBackScrollView;
    @ViewInject(R.id.et_company_name)
    private EditText mEtCompanyName;
    @ViewInject(R.id.tv_start_time)
    private TextView mStartTime;
    @ViewInject(R.id.tv_end_time)
    private TextView mEndTime;
    @ViewInject(R.id.et_declare)
    private EditText mEtDeclare;    //说明
    @ViewInject(R.id.ll_del_layout)
    private LinearLayout mLlDelLayout;  //删除布局

    private UserInfo mUser;
    private TimePickerView pvTime;  //时间选择器
    private College mCollege;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initTimePicker();
    }

    private void initData() {
        mUser = (UserInfo) getIntent().getSerializableExtra("UserInfo");
        mCollege = (College) getIntent().getSerializableExtra("mCollege");
        if (mCollege != null) {
            mEtCompanyName.setText(mCollege.universityName);
            mStartTime.setText(mCollege.studyStartTime);
            mEndTime.setText(mCollege.studyEndTime);
            mEtDeclare.setText(mCollege.universityIntro);
            mLlDelLayout.setVisibility(View.VISIBLE);
        } else {
            mLlDelLayout.setVisibility(View.GONE);
        }
    }

    @Event(value = {R.id.tv_finish, R.id.tv_save, R.id.ll_company_address, R.id.ll_start_time, R.id.ll_end_time,
            R.id.tv_del_experience})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.tv_finish:
                onBackPressed();
                break;
            case R.id.tv_save:
                setMUser(); //编辑/新建 工作经验
                break;
            case R.id.tv_del_experience:
                new AlertDialog(this).builder().setMsg("确认删除该条记录？")
                        .setPositiveColor(getResources().getColor(R.color.alertdialog_del_text)).setCancelable(false)
                        .setPositiveButton(getString(R.string.confirm),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mCollege = null;
                                        mUser.College = "";
                                        modifyUserPub(mUser);
                                        Intent intent = new Intent();
                                        intent.putExtra("college", mCollege);
                                        setResult(RESULT_OK, intent);
                                        onBackPressed();
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                break;
            case R.id.ll_start_time:
                pvTime.show(v);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSwipeBackEnable(false);
                    }
                }, 500);
                break;
            case R.id.ll_end_time:
                pvTime.show(v);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSwipeBackEnable(false);
                    }
                }, 500);
                break;
        }

    }

    /**
     * 编辑/新建
     */
    private void setMUser() {
        if (mCollege == null) {
            mCollege = new College();
        }
        mCollege.universityName = mEtCompanyName.getText().toString();
        mCollege.studyStartTime = mStartTime.getText().toString();
        mCollege.studyEndTime = mEndTime.getText().toString();
        mCollege.universityIntro = mEtDeclare.getText().toString();
        mCollege.universityPrivacy = 4;
        if (TextUtils.isEmpty(mEtCompanyName.getText())) {
            Toast.makeText(this, "请输入大学名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mStartTime.getText().toString()) || TextUtils.isEmpty(mEndTime.getText().toString())) {
            Toast.makeText(this, "请选择时间", Toast.LENGTH_SHORT).show();
        } else if (getTime(mStartTime.getText().toString()) >= getTime(mEndTime.getText().toString())) {
            Toast.makeText(this, "入学时间必须小于毕业时间", Toast.LENGTH_SHORT).show();
        } else {
            String strCollege = new Gson().toJson(mCollege);
            mUser.College = strCollege;
            modifyUserPub(mUser);
            Intent intent = new Intent();
            intent.putExtra("college", mCollege);
            setResult(RESULT_OK, intent);
            onBackPressed();
        }
    }


    /**
     * 修改信息
     *
     * @param ui
     */
    private void modifyUserPub(final UserInfo ui) {
        API.UserPubAPI.ModifyUserOtherById(ui, 3,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        Toast.makeText(EducationActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                            Toast.makeText(EducationActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EducationActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
//        Calendar selectedDate = Calendar.getInstance();
//        Calendar startDate = Calendar.getInstance();
//        startDate.set(2013, 0, 23);
//        Calendar endDate = Calendar.getInstance();
//        endDate.set(2019, 11, 28);
        //时间选择器
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                if (v.getId() == R.id.ll_start_time) {
                    mStartTime.setText(getTime(date));
                } else if (v.getId() == R.id.ll_end_time) {
                    mEndTime.setText(getTime(date));
                }
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.parseColor("#00000000"))
                .setTextColorCenter(Color.parseColor("#EA5404")) //设置选中项文字颜色
                .setContentSize(21)
//                .setDate(selectedDate)
//                .setRangDate(startDate, endDate)
//                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();

        pvTime.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                swipeBackScrollView.updateSwipeBackState();
            }
        });
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private long getTime(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.parse(date).getTime();
        } catch (Exception e) {
            return 0;
        }
    }
}

