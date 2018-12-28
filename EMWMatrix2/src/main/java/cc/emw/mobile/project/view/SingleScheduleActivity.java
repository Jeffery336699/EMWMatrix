package cc.emw.mobile.project.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.ApiEntity;
import cc.emw.mobile.project.fragment.ScheduleFragment;

/**
 * 单项目排期页面
 */
@ContentView(R.layout.activity_single_schedule)
public class SingleScheduleActivity extends BaseActivity {
    private static final String TAG = "SingleScheduleActivity";

    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn;	//顶部返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;	//顶部标题
    @ViewInject(R.id.schedule_fl)
    private FrameLayout mSchedule;

    ArrayList<ApiEntity.UserProject> projects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView(){
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText(getString(R.string.project_schedule));
        projects = (ArrayList<ApiEntity.UserProject>)getIntent().getSerializableExtra("projects");
        Log.d(TAG,"count: " + projects.size());
        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setProjects(projects);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.schedule_fl,fragment).commit();
    }

    public ArrayList<ApiEntity.UserProject> getProjects(){
        return this.projects;
    }

    @Event({R.id.cm_header_btn_left})
    private void onHeaderClick(View v){
        switch (v.getId()){
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
        }
    }
}
