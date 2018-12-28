package cc.emw.mobile.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.adapter.FreeRepeatAdapter;
import cc.emw.mobile.view.CollapseView;
import cc.emw.mobile.view.MyListView;
import cc.emw.mobile.view.SwipeBackScrollView;

@ContentView(R.layout.activity_free_repeat)
public class FreeRepeatActivity extends BaseActivity {

    @ViewInject(R.id.scroll_sb_free_repeat)
    private SwipeBackScrollView mMainScrollView;
    @ViewInject(R.id.cv_repeat_lx)
    private CollapseView mCvType;   //自定义的类型 年/月/周/日    4/3/2/1
    @ViewInject(R.id.cv_repeat_pl)
    private CollapseView mCvHz;     //自定义的频率
    @ViewInject(R.id.ll_week_select_day)
    private LinearLayout mLlWeek;      //周重复的样式

    private CheckBox checkBox7, checkBox6, checkBox5, checkBox4, checkBox3, checkBox2, checkBox1; //星期日、六、五、四、三、二、一
    private FreeRepeatAdapter mTypeAdapter, mHzAdapter;
    private MyListView mLvType, mLvHz;
    private List<String> mListType, mListHz;
    private int type = 1;       //重复的类型
    private int hz = 1;         //重复的频率
    private String weekStr; //重复周的信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindWeekView();
        getIntentData();
        initCollapseView(type, hz);
        initItemClick();
    }

    private void getIntentData() {
        type = getIntent().getIntExtra("type", 1);
        hz = getIntent().getIntExtra("hz", 1);
        weekStr = getIntent().getStringExtra("weekStr");
        initData(type);
        mLlWeek.setVisibility(type == 2 ? View.VISIBLE : View.GONE);
        initWeekViewSelect(weekStr);
    }

    @Event(value = {R.id.ic_tv_back, R.id.tv_sure})
    private void onClicks(View view) {
        switch (view.getId()) {
            case R.id.ic_tv_back:
                finish();
                break;
            case R.id.tv_sure:
                if (type == 2) {
                    weekStr = PackagingUtils.getRepeatWeekInfo(checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7);
                }
                Intent intent = new Intent();
                intent.putExtra("type", type);
                intent.putExtra("hz", hz);
                intent.putExtra("weekStr", weekStr);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void initItemClick() {
        mLvType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListHz.clear();
                switch (position) {
                    case 0:
                        mCvType.setTitle("天");
                        mCvHz.setTitle("每1天");
                        for (int i = 0; i < 10; i++) {
                            mListHz.add((i + 1) + "天");
                        }
                        type = 1;
                        mLlWeek.setVisibility(View.GONE);
                        break;
                    case 1:
                        mCvType.setTitle("周");
                        mCvHz.setTitle("每1周");
                        for (int i = 0; i < 10; i++) {
                            mListHz.add((i + 1) + "周");
                        }
                        type = 2;
                        mLlWeek.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mCvType.setTitle("月");
                        mCvHz.setTitle("每1月");
                        for (int i = 0; i < 10; i++) {
                            mListHz.add((i + 1) + "月");
                        }
                        type = 3;
                        mLlWeek.setVisibility(View.GONE);
                        break;
                    case 3:
                        mCvType.setTitle("年");
                        mCvHz.setTitle("每1年");
                        for (int i = 0; i < 10; i++) {
                            mListHz.add((i + 1) + "年");
                        }
                        type = 4;
                        mLlWeek.setVisibility(View.GONE);
                        break;
                }
                mHzAdapter.setData(mListHz);
                mHzAdapter.notifyDataSetChanged();
                mCvType.rotateArrow();
            }
        });
        mLvHz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hz = position + 1;
                mCvHz.setTitle("每" + mListHz.get(position));
                mCvHz.rotateArrow();
            }
        });
    }

    private void bindWeekView() {
        checkBox7 = (CheckBox) findViewById(R.id.cb_week_select7);
        checkBox6 = (CheckBox) findViewById(R.id.cb_week_select6);
        checkBox5 = (CheckBox) findViewById(R.id.cb_week_select5);
        checkBox4 = (CheckBox) findViewById(R.id.cb_week_select4);
        checkBox3 = (CheckBox) findViewById(R.id.cb_week_select3);
        checkBox2 = (CheckBox) findViewById(R.id.cb_week_select2);
        checkBox1 = (CheckBox) findViewById(R.id.cb_week_select1);
    }

    private void initWeekViewSelect(String weekStr) {
        if (weekStr != null && !TextUtils.isEmpty(weekStr)) {
            String[] arr = weekStr.split(",");
            for (int i = 0; i < arr.length; i++) {
                switch (Integer.valueOf(arr[i])) {
                    case 0:
                        checkBox1.setChecked(true);
                        break;
                    case 1:
                        checkBox2.setChecked(true);
                        break;
                    case 2:
                        checkBox3.setChecked(true);
                        break;
                    case 3:
                        checkBox4.setChecked(true);
                        break;
                    case 4:
                        checkBox5.setChecked(true);
                        break;
                    case 5:
                        checkBox6.setChecked(true);
                        break;
                    case 6:
                        checkBox7.setChecked(true);
                        break;
                }
            }
        }
    }

    private void initCollapseView(int type, int hz) {
        String typeStr = "天";
        switch (type) {
            case 1:
                typeStr = "天";
                break;
            case 2:
                typeStr = "周";
                break;
            case 3:
                typeStr = "月";
                break;
            case 4:
                typeStr = "年";
                break;
        }
        //类型
        mCvType.setTitle(typeStr);
        mCvType.setTagNameStyle(15, "#FF202020");
        mCvType.setTitleNameStyle(14, "#FF757575");
        mCvType.setTagNameVis("", "类型");
        mCvType.setSwipeScrollView(mMainScrollView);
        mCvType.setContent(R.layout.collapse_free_repeat);
        mLvType = (MyListView) mCvType.findViewById(R.id.mlv_collapse_free);
        mTypeAdapter = new FreeRepeatAdapter(this);
        mTypeAdapter.setData(mListType);
        mLvType.setAdapter(mTypeAdapter);
        //频率
        mCvHz.setTagNameVis("", "频率");
        mCvHz.setTagNameStyle(15, "#FF202020");
        mCvHz.setTitleNameStyle(14, "#FF757575");
        mCvHz.setTitle("每" + hz + typeStr);
        mCvHz.setSwipeScrollView(mMainScrollView);
        mCvHz.setContent(R.layout.collapse_free_repeat);
        mLvHz = (MyListView) mCvHz.findViewById(R.id.mlv_collapse_free);
        mHzAdapter = new FreeRepeatAdapter(this);
        mHzAdapter.setData(mListHz);
        mLvHz.setAdapter(mHzAdapter);
    }

    private void initData(int type) {
        mListType = new ArrayList<>();
        mListHz = new ArrayList<>();

        mListType.add("天");
        mListType.add("周");
        mListType.add("月");
        mListType.add("年");

        String typeStr = "天";
        switch (type) {
            case 1:
                typeStr = "天";
                break;
            case 2:
                typeStr = "周";
                break;
            case 3:
                typeStr = "月";
                break;
            case 4:
                typeStr = "年";
                break;
        }
        for (int i = 0; i < 10; i++) {
            mListHz.add((i + 1) + typeStr);
        }
    }
}
