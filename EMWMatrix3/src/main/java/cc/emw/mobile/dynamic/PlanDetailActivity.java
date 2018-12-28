package cc.emw.mobile.dynamic;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.dynamic.adapter.PlanAdapter;
import cc.emw.mobile.net.ApiEntity.UserPlan;
import cc.emw.mobile.view.ExListView;
import cc.emw.mobile.view.SegmentedGroup;

/**
 * 动态·工作计划详情
 * @author shaobo.zhuang
 *
 */
@ContentView(R.layout.activity_plan_detail)
public class PlanDetailActivity extends BaseActivity {

	@ViewInject(R.id.segmented_plan_type) private SegmentedGroup mRadioGroup; // 类型RadioGroup
	@ViewInject(R.id.lv_plan_planlist) private ExListView mListView; // 工作计划列表

	private ArrayList<UserPlan> mDataList; // 工作计划列表
	private PlanAdapter mWorkPlanAdapter; // 工作计划列表adapter
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		mDataList = (ArrayList<UserPlan>) getIntent().getSerializableExtra("plan_list");
		initView();
    }
    
    private void initView() {
		findViewById(R.id.cm_header_tv_right9).setVisibility(View.GONE);
		if (mDataList != null && mDataList.size() > 0) {
			int planType = mDataList.get(0).Type;
			if (planType == 1) {
				mRadioGroup.check(R.id.rb_plan_day);
			} else if (planType == 2) {
				mRadioGroup.check(R.id.rb_plan_week);
			} else if (planType == 3) {
				mRadioGroup.check(R.id.rb_plan_month);
			}
			mWorkPlanAdapter = new PlanAdapter(this, mDataList, false);
			mWorkPlanAdapter.setSwipeBackLayout(getSwipeBackLayout());
			mListView.setAdapter(mWorkPlanAdapter);
		}
    }


    @Event({R.id.cm_header_btn_left9, R.id.cm_header_tv_right9})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left9:
				onBackPressed();
				break;
			case R.id.cm_header_tv_right9:

				break;
    	}
    }

}
