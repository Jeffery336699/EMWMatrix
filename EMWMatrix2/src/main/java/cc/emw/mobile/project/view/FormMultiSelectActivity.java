package cc.emw.mobile.project.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.project.adapter.FormMultiSelectAdapter;
import cc.emw.mobile.project.entities.Elements2;

/**
 * 表单多选页面
 * Created by jven.wu on 2016/5/24.
 */
@ContentView(R.layout.activity_form_multi_select)
public class FormMultiSelectActivity extends BaseActivity {
    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; //顶部返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;    //顶部标题
    @ViewInject(R.id.cm_header_tv_right)
    private TextView mHeaderFinishBtn;  //顶部完成按钮
    @ViewInject(R.id.muti_select_lv)
    private ListView mListView;

    private FormMultiSelectAdapter adapter;
    private Elements2 fromElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView(){
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderFinishBtn.setVisibility(View.VISIBLE);
        mHeaderFinishBtn.setText(getString(R.string.finish));
        fromElement = (Elements2)getIntent().getSerializableExtra("elem");
        mHeaderTitleTv.setText(fromElement.Title);
        adapter = new FormMultiSelectAdapter(this);
        adapter.setSelectItems(fromElement);
        mListView.setAdapter(adapter);
    }

    @Event({R.id.cm_header_btn_left,R.id.cm_header_tv_right})
    private void onHeaderClick(View v){
        switch (v.getId()){
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
            case R.id.cm_header_tv_right:
                Intent intent = getIntent();
                fromElement.Value = adapter.getRetItems();
                intent.putExtra("elem",fromElement);
                setResult(RESULT_OK, intent);
                onBackPressed();
                break;
        }
    }
}
