package cc.emw.mobile.me;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.entity.NavGroup;
import cc.emw.mobile.main.MainActivity;
import cc.emw.mobile.me.adapter.NavigationAdapter;
import cc.emw.mobile.me.view.DragNDropListView;
import cc.emw.mobile.net.API;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.util.PrefsUtil;
import cc.emw.mobile.util.ToastUtil;

/**
 * 设置·导航设置
 */
@ContentView(R.layout.activity_navigation)
public class NavigationActivity extends BaseActivity implements DragNDropListView.DragNDropListeners {

    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;
    @ViewInject(R.id.elv_navigation)
    private DragNDropListView mNavigationElv;
    @ViewInject(R.id.view_line)
    private View mLine;

    private Dialog mLoadingDialog; //加载框
    private ArrayList<NavGroup> navGroupList;
    private NavigationAdapter navigationAdapter;
    private boolean isUpdate; //是否需要刷新主界面左侧菜单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navGroupList = new ArrayList<>();
        init();
    }

    @Override
    protected void startAnimEnd(Bundle savedInstanceState) {
        getPersonTemplates();
    }

    private void init() {
        mLine.setVisibility(View.GONE);
        mHeaderTitleTv.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("导航设置");
        mHeaderTitleTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                boolean isFormWeb = PrefsUtil.isFormWeb();
                PrefsUtil.setFormWeb(!isFormWeb);
                Toast.makeText(NavigationActivity.this, isFormWeb ? "NativeForm" : "WebForm", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mNavigationElv.setGroupIndicator(null);
        mNavigationElv.setDragOnLongPress(true);
        navigationAdapter = new NavigationAdapter(this, isUpdate);
        mNavigationElv.setAdapter(navigationAdapter);
        mNavigationElv.setListeners(this);
    }

    @Override
    public void onDrag(float x, float y) {

    }

    @Override
    public void onPick(int[] position) {

    }

    @Override
    public void onDrop(int[] from, int[] to) {
        /*ApiEntity.Navigation form1 = navGroupList.get(from[0]).NavList.get(from[1]);
        ApiEntity.Navigation form2 = navGroupList.get(to[0]).NavList.get(to[1]);
        ApiEntity.Navigation form3 = navGroupList.get(to[0]).NavList.get(to[1]-1);
        modifyUpdateNavSort(navGroupList.get(from[0]).NavList.get(from[1]).TemplateID, navGroupList.get(to[0]).NavList.get(to[1]).TemplateID);*/

        //接口调用时机发生在列表位置已经改变之后，且为了防止出现数组越界，先进行坐标值判断，所以由上往下移动的item坐标为to[1]，被改变的item位置为to[1]-1，由下往上移动的item坐标为to[1]，被改变的item位置为to[1]+1
        if (from[1] > to[1]) {
            modifyUpdateNavSort(navGroupList.get(to[0]).NavList.get(to[1]).ID, navGroupList.get(to[0]).NavList.get(to[1] + 1).ID);
        } else {
            modifyUpdateNavSort(navGroupList.get(to[0]).NavList.get(to[1]).ID, navGroupList.get(to[0]).NavList.get(to[1] - 1).ID);
        }

    }

    @Event(value = {R.id.cm_header_btn_left})
    private void onHeadClick(View view) {
        switch (view.getId()) {
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isUpdate) {
            sendBroadcast(new Intent(MainActivity.ACTION_REFRESH_LEFTMENU));
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            String alias = data.getStringExtra("nav_alias");
            int groupPos = data.getIntExtra("group_position", -1);
            int childPos = data.getIntExtra("child_position", -1);
            if (childPos == -1) {
                navGroupList.get(groupPos).AliasName = alias;
            } else {
                navGroupList.get(groupPos).NavList.get(childPos).AliasName = alias;
            }
            navigationAdapter.notifyDataSetChanged(); //导航别名修改成功刷新
            isUpdate = true;
        }
    }

    private void getPersonTemplates() {
        API.TemplateAPI.LoadPersonTemplates(new RequestCallback<NavGroup>(NavGroup.class) {

            @Override
            public void onStarted() {
                mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
                mLoadingDialog.show();
            }

            @Override
            public void onParseSuccess(List<NavGroup> respList) {
                if (mLoadingDialog != null && !isFinishing())
                    mLoadingDialog.dismiss();
                if (respList != null && respList.size() > 0) {
                    navGroupList.clear();
                    navGroupList.addAll(respList);
                    navigationAdapter.setData(navGroupList);
                    navigationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                ToastUtil.showToast(NavigationActivity.this, "获取数据失败！");
            }
        });
    }

    private void modifyUpdateNavSort(int navid1, int navid2) {
        API.TemplateAPI.UpdateNavSort(navid1, navid2, new RequestCallback<String>(String.class) {

            @Override
            public void onStarted() {
                /*mLoadingDialog = createLoadingDialog(R.string.loading_dialog_tips2);
                mLoadingDialog.show();*/
            }

            @Override
            public void onParseSuccess(String result) {
                if (mLoadingDialog != null && !isFinishing())
                    mLoadingDialog.dismiss();
                //ToastUtil.showToast(NavigationActivity.this, "排序修改成功！");
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (mLoadingDialog != null)
                    mLoadingDialog.dismiss();
                //ToastUtil.showToast(NavigationActivity.this, "排序修改失败！");
            }
        });
    }
}
