package cc.emw.mobile.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview2.OptionsPickerView;
import com.bigkoo.pickerview2.TimePickerView;
import com.bigkoo.pickerview2.listener.OnDismissListener;
import com.google.gson.Gson;
import com.zf.iosdialog.widget.AlertDialog;

import org.json.JSONArray;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.me.entity.JobExperience;
import cc.emw.mobile.me.entity.JsonBean;
import cc.emw.mobile.me.util.GetJsonDataUtil;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.view.SwipeBackScrollView;

@ContentView(R.layout.activity_work_experience)
public class WorkExperienceActivity extends BaseActivity {

    @ViewInject(R.id.swipe_scroll)
    private SwipeBackScrollView swipeBackScrollView;
    @ViewInject(R.id.et_company_name)
    private EditText mEtCompanyName;
    @ViewInject(R.id.et_job)
    private EditText mEtJob;
    @ViewInject(R.id.tv_company_address)
    private TextView mCompanyAddress;
    @ViewInject(R.id.tv_start_time)
    private TextView mStartTime;
    @ViewInject(R.id.tv_end_time)
    private TextView mEndTime;
    @ViewInject(R.id.et_declare)
    private EditText mEtDeclare;    //说明
    @ViewInject(R.id.ll_del_layout)
    private LinearLayout mLlDelLayout;  //删除布局

    private UserInfo mUser;
    private JobExperience mJobExperiences;
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private boolean isLoaded = false;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private TimePickerView pvTime;  //时间选择器
    private List<JobExperience> mDataList = new ArrayList<>();
    private int index;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    Toast.makeText(WorkExperienceActivity.this, "解析数据失败", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        initData();
        initTimePicker();
    }

    private void initData() {
        mDataList.clear();
        mUser = (UserInfo) getIntent().getSerializableExtra("UserInfo");
        mDataList = (List<JobExperience>) getIntent().getSerializableExtra("experience");
        index = getIntent().getIntExtra("index", -1);
        if (index != -1) {
            mJobExperiences = mDataList.get(index);
            mEtCompanyName.setText(mJobExperiences.userCompany);
            mEtJob.setText(mJobExperiences.userJob);
            mCompanyAddress.setText(mJobExperiences.userCounty);
            mStartTime.setText(mJobExperiences.jobStartTime);
            mEndTime.setText(mJobExperiences.jobEndtTime);
            mEtDeclare.setText(mJobExperiences.userIntro);
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
                                        if (mDataList != null && mDataList.size() > 0) {
                                            mDataList.remove(index);
                                            String jsonString = JSON.toJSONString(mDataList);
                                            mUser.JobExperience = jsonString;
                                            modifyUserPub(mUser);
                                            Intent intent = new Intent();
                                            intent.putExtra("experience", (Serializable) mDataList);
                                            setResult(RESULT_OK, intent);
                                            onBackPressed();
                                        }
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                break;
            case R.id.ll_company_address:
                if (isLoaded) {
                    ShowPickerView();
                } else {
                    Toast.makeText(WorkExperienceActivity.this, "正在更新城市列表，请等待", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_start_time:
                pvTime.show(v);
                setSwipeBackEnable(false);
                break;
            case R.id.ll_end_time:
                pvTime.show(v);
                setSwipeBackEnable(false);
                break;
        }
    }

    /**
     * 编辑/新建
     */
    private void setMUser() {
        if (mJobExperiences == null) {
            mJobExperiences = new JobExperience();
        }
        mJobExperiences.userCompany = mEtCompanyName.getText().toString();
        mJobExperiences.userJob = mEtJob.getText().toString();
        mJobExperiences.userCounty = mCompanyAddress.getText().toString();
        mJobExperiences.jobStartTime = mStartTime.getText().toString();
        mJobExperiences.jobEndtTime = mEndTime.getText().toString();
        mJobExperiences.userIntro = mEtDeclare.getText().toString();
        mJobExperiences.jobsPrivacy = 4;
        if (TextUtils.isEmpty(mEtCompanyName.getText())) {
            Toast.makeText(this, "请输入公司名称", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mStartTime.getText().toString()) || TextUtils.isEmpty(mEndTime.getText().toString())) {
            Toast.makeText(this, "请选择时间", Toast.LENGTH_SHORT).show();
        } else if (getTime(mStartTime.getText().toString()) >= getTime(mEndTime.getText().toString())) {
            Toast.makeText(this, "起始时间必须小于结束时间", Toast.LENGTH_SHORT).show();
        } else {
            if (index == -1) {
                mDataList.add(mJobExperiences);
            }
            String jsonString = JSON.toJSONString(mDataList);
            mUser.JobExperience = jsonString;
            modifyUserPub(mUser);
            Intent intent = new Intent();
            intent.putExtra("experience", (Serializable) mDataList);
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
        API.UserPubAPI.ModifyUserOtherById(ui, 1,
                new RequestCallback<String>(String.class) {

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        Toast.makeText(WorkExperienceActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result) && Integer.valueOf(result) > 0) {
                            Toast.makeText(WorkExperienceActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WorkExperienceActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
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

    /**
     * 城市列表
     */
    private void ShowPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);

//                Toast.makeText(WorkExperienceActivity.this, tx, Toast.LENGTH_SHORT).show();
                mCompanyAddress.setText(tx);
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.parseColor("#00000000"))
                .setTextColorCenter(Color.parseColor("#EA5404")) //设置选中项文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(true)// default is true
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setSwipeBackEnable(false);
            }
        }, 500);
        pvOptions.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                swipeBackScrollView.updateSwipeBackState();
            }
        });
        /*pvOptions.findViewById(R.id.options1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBackScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }
}
