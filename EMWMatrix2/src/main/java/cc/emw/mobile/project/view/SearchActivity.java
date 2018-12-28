package cc.emw.mobile.project.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.net.RequestCallback;
import cc.emw.mobile.project.entities.Elements2;
import cc.emw.mobile.project.entities.Searcher;

/**
 * 表单查找页面
 */
@ContentView(R.layout.activity_search)
public class SearchActivity extends BaseActivity {
    private static final String TAG = "SearchActivity";
    @ViewInject(R.id.cm_header_btn_left)
    private ImageButton mHeaderBackBtn; //顶部返回按钮
    @ViewInject(R.id.cm_header_tv_title)
    private TextView mHeaderTitleTv;    //顶部标题
    @ViewInject(R.id.search_lv)
    private ListView mListView;

    private Elements2 fromElement;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView(){
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        fromElement = (Elements2)getIntent().getSerializableExtra("elem");
        int fromPage = getIntent().getIntExtra("page_id",0);
        mHeaderTitleTv.setText(fromElement.Title);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fromElement.Text = adapter.getItem(position);
                fromElement.Value = values.get(position);
                Intent intent = getIntent();
                intent.putExtra("elem", fromElement);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });
        request(fromPage, fromElement.ID);
    }

    @Event({R.id.cm_header_btn_left})
    private void mHeaderOnClick(View v){
        switch (v.getId()){
            case R.id.cm_header_btn_left:
                onBackPressed();
                break;
        }
    }

    private void request(int page,String elem) {
        String requestUrl = "http://10.0.10.80:8000/Page/" + page
                + "/searcher?elem=" + elem ;
        Log.d(TAG,"request: " + requestUrl);
        RequestParams params = new RequestParams(requestUrl);
        Callback.Cancelable cancelable = x.http().post(params,
                new RequestCallback<Searcher>(Searcher.class) {
                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
//                        ToastUtil.showToast(getApplicationContext(),ex.getMessage());
                        Log.d(TAG,"onError()->msg: " + ex.getMessage() + ", " + ex.toString());
                    }

                    @Override
                    public void onFinished() {
                    }

                    @Override
                    public void onParseSuccess(Searcher searcher) {
                        ArrayList<String> strings = new ArrayList<String>();
                        values = new ArrayList<String>();
                        for(int i = 0;i<searcher.Rows.size();i++){
                            strings.add(searcher.Rows.get(i).get(0));
                            values.add(searcher.Rows.get(i).get(1));
                        }
                        adapter = new ArrayAdapter<String>(SearchActivity.this,
                                R.layout.listitem_sale_simple1,
                                R.id.text1,
                                strings);
                        mListView.setAdapter(adapter);
                    }
                });
    }
}
