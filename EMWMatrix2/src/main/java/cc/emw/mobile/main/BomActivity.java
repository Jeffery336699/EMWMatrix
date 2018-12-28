package cc.emw.mobile.main;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;

@ContentView(R.layout.activity_bom)
public class BomActivity extends BaseActivity {

	@ViewInject(R.id.cm_header_btn_left)
	private ImageButton mHeaderBackBtn; // 顶部条返回按钮
	@ViewInject(R.id.cm_header_tv_title)
	private TextView mHeaderTitleTv; // 顶部条标题
	@ViewInject(R.id.cm_header_btn_right)
	private ImageButton mHeaderMoreBtn; // 
	
	private PopupWindow popupWindow;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHeaderBackBtn.setImageResource(R.drawable.cm_header_btn_back);
        mHeaderBackBtn.setVisibility(View.VISIBLE);
        mHeaderTitleTv.setText("BOM");
    	mHeaderMoreBtn.setImageResource(R.drawable.cm_header_btn_more);
        mHeaderMoreBtn.setVisibility(View.GONE);
        
    }

    @Event({R.id.cm_header_btn_left, R.id.cm_header_btn_right})
    private void onHeaderClick(View v) {
    	switch (v.getId()) {
			case R.id.cm_header_btn_left:
				finish();
				break;
			case R.id.cm_header_btn_right:
				break;
    	}
    }

	@Event({ R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, 
			R.id.btn8, R.id.btn9, R.id.btn10, R.id.btn11, R.id.btn12, R.id.btn13 })
	private void onItemClick(View v) {
		switch (v.getId()) {
		case R.id.btn1:
			
			break;
		case R.id.btn2:
			
			break;
		}
		showPopWindow(v.getId());
	}
	
	private void showPopWindow(int id) {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_bom, null);
        LinearLayout rootLayout = (LinearLayout) popView.findViewById(R.id.ll_bom_root);
        TextView titleTv = (TextView) popView.findViewById(R.id.tv_bom_title);
        TextView contentTv = (TextView) popView.findViewById(R.id.tv_bom_content);
        contentTv.setText(id+"");
        ImageButton closeBtn = (ImageButton) popView.findViewById(R.id.btn_bom_close);
        closeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
        rootLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});
        popupWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }
	
	
}
