package cc.emw.mobile.me;

import android.os.Bundle;
import android.widget.RelativeLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.me.fragment.MyInfoFragment;

@ContentView(R.layout.activity_my_info)
public class MyInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyInfoFragment fragment = new MyInfoFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.rl_my_info, fragment)
                .show(fragment).commit();
    }
}
