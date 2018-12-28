package cc.emw.mobile.project.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
import cc.emw.mobile.R;
import cc.emw.mobile.view.FlowLayout;

/**
 * 多选TextView自定义视图
 * Created by jven.wu on 2016/5/25.
 */
public class MultiSelectTextView extends TextView {
    protected static final String TAG = "PersonTextView";

    public MultiSelectTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MultiSelectTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MultiSelectTextView(Context context,int BgID,boolean canDelete){
        super(context);
        this.setTextAppearance(getContext(), R.style.tv_select_person);
        if(canDelete){
            Drawable drawable=context.getResources().getDrawable(R.drawable.presonview_img_del);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            this.setCompoundDrawables(null, null, drawable, null);
        }
        this.setBackgroundResource(BgID);
        this.setPadding(10, 6, 10, 6);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 3;
        params.bottomMargin = 3;
        params.leftMargin =3;
        params.rightMargin = 3;
        this.setLayoutParams(params);
    }
    public MultiSelectTextView(Context context) {
        super(context);
        this.setTextAppearance(getContext(), R.style.tv_select_person);
        Drawable drawable=context.getResources().getDrawable(R.drawable.presonview_img_del);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        this.setCompoundDrawables(null, null, drawable, null);
        this.setBackgroundResource(R.drawable.persontext_bg);
        this.setPadding(10, 6, 10, 6);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 3;
        params.bottomMargin = 3;
        params.leftMargin =3;
        params.rightMargin = 3;
        this.setLayoutParams(params);
    }
}
