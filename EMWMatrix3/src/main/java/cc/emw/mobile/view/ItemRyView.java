package cc.emw.mobile.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.emw.mobile.R;
import cc.emw.mobile.bean.CiclerItemBean;

public class ItemRyView extends FrameLayout {
    @BindView(R.id.tv_item_title)
    TextView tvItemTitle;
    @BindView(R.id.ry_item)
    RecyclerView ryItem;
    private Context context;
    private List<CiclerItemBean.ItemData> list=new ArrayList<>();

    public ItemRyView(@NonNull Context context) {
        super(context);
    }

    public ItemRyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initModulData();
        initView();
    }

    private void initModulData() {
        for(int i=0;i<5;i++){
            CiclerItemBean.ItemData itemData = new CiclerItemBean.ItemData();
            itemData.setContent("这是内容了。。"+i);
            itemData.setTime("12.1"+i);
            itemData.setNum((i+2)+"");
            list.add(itemData);
        }
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_item_layout, null);
        ButterKnife.bind(this, view);
        ryItem.setLayoutManager(new LinearLayoutManager(context));
        ryItem.setAdapter(new CommonAdapter<CiclerItemBean.ItemData>(context,R.layout.ry_item_layout,list) {

            @Override
            protected void convert(ViewHolder holder, CiclerItemBean.ItemData itemData, int position) {

            }
        });
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(view,layoutParams);
    }
}
