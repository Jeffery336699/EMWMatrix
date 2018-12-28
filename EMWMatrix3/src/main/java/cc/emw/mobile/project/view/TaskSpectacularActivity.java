package cc.emw.mobile.project.view;

import android.os.Bundle;

import org.xutils.view.annotation.ContentView;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.task.constant.TaskConstant;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by jven.wu on 2016/11/14.
 */
@ContentView(R.layout.activity_task_spectacular)
public class TaskSpectacularActivity extends BaseActivity {
    public static final String GROUP_TASK = "GROUP_TASK";
    public static final String PROJECT_TASK = "PROJECT_TASK";

    public static String currentType = GROUP_TASK;
    public static String currentState = TaskConstant.TaskStateString.UNSTART;
    public static String currentProjectId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWidget();
    }

    @Override
    protected void onDestroy() {
        currentState = TaskConstant.TaskStateString.UNSTART;
        super.onDestroy();
    }

    private void initWidget() {
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP_BOTTOM);
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.container, new StateBoardViewPagerFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
