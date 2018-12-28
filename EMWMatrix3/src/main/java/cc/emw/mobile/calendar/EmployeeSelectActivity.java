package cc.emw.mobile.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.calendar.adapter.EmployeeSelectAdapter;
import cc.emw.mobile.calendar.fragment.CalendarFragmentView;
import cc.emw.mobile.entity.UserInfo;
import cc.emw.mobile.util.CharacterParser;
import cc.emw.mobile.util.PinyinComparator;
import cc.emw.mobile.view.IconTextView;
import cc.emw.mobile.view.SideBar;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

//import cc.emw.mobile.calendar.calendar.fragment.MyCalendarFragments;

//import cc.emw.mobile.calendar.fragment.MyCalendarFragments;

/**
 * 选择员工
 */
@ContentView(R.layout.activity_employee_select)
public class EmployeeSelectActivity extends BaseActivity {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv; // 顶部条标题

    @ViewInject(R.id.lv_personselect)
    private ListView mListView;
    @ViewInject(R.id.contact_sidebar)
    private SideBar mSideBar;
    @ViewInject(R.id.contact_tv_letter)
    private TextView mTextDialog;

    private EmployeeSelectAdapter mSelectAdapter;
    private ArrayList<UserInfo> mDataList, mSelectList;
    public static final String EXTRA_USER_LIST = "user_list";
    public static final String EXTRA_SELECT_IDS = "select_ids";
    public static final String EXTRA_SELECT_LIST = "select_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
        mDataList = (ArrayList<UserInfo>) getIntent().getSerializableExtra(EXTRA_USER_LIST);
        mSelectList = (ArrayList<UserInfo>) getIntent().getSerializableExtra(EXTRA_SELECT_LIST);

        sortData(mDataList); //汉字转拼音
        Collections.sort(mDataList, new PinyinComparator());// 根据a-z进行排序源数据

        initView();
    }

    private void sortData(ArrayList<UserInfo> respList) {
        CharacterParser characterParser = CharacterParser.getInstance();
        for (int i = 0; i < respList.size(); i++) {
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(respList.get(i).Name);
            if (!TextUtils.isEmpty(pinyin)) {
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    respList.get(i).setSortLetters(sortString.toUpperCase());
                } else {
                    respList.get(i).setSortLetters("#");
                }
            } else {
                respList.get(i).setSortLetters("#");
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Event(value = {R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right9:
                Intent data = new Intent();
                data.setAction(CalendarFragmentView.ACTION_REFRESH_SCHEDULE_MONTH_LIST);
                data.putExtra(EXTRA_SELECT_IDS, mSelectAdapter.getSelectIDs());
                data.putExtra(EXTRA_SELECT_LIST, mSelectAdapter.getSelectList());
                data.putExtra("person", "person");
                sendBroadcast(data);
                finish();
                break;
        }
    }

    private void initView() {
        mHeaderTitleTv.setText("选择员工");
        ((IconTextView) findViewById(R.id.cm_header_btn_left9)).setIconText("eb68");
        ((TextView) findViewById(R.id.cm_header_tv_right9)).setText("确认");

        mSideBar.setTextView(mTextDialog);
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = mSelectAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });

        mSelectAdapter = new EmployeeSelectAdapter(this);
        mSelectAdapter.setData(mDataList);
        mSelectAdapter.setSelectList(mSelectList);
        mListView.setAdapter(mSelectAdapter);
    }

}
