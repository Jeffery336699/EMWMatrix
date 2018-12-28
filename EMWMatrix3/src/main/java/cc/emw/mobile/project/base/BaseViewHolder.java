package cc.emw.mobile.project.base;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import cc.emw.mobile.R;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.view.IconTextView;

/**
 * Created by jven.wu on 2016/11/10.
 */
public class BaseViewHolder {
    private SparseArray<View> mViews;
    private int mLayoutId;
    private View mConvertView;
    private int mPosition;
    private Context mContext;
    private DisplayImageOptions options, groupOptions;           //图片显示选项

    public BaseViewHolder(Context context,ViewGroup parent, int layoutId, int position) {
        this.mViews = new SparseArray<View>();
        this.mPosition = position;
        this.mLayoutId = layoutId;
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        this.mConvertView = inflater.inflate(layoutId, parent, false);
        this.mConvertView.setTag(this);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//				.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        groupOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_grouphead) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.cm_img_grouphead) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.cm_img_grouphead) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//				.displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    /**
     * 获取一个viewHolder
     *
     * @param convertView view
     * @param parent      parent view
     * @param layoutId    布局资源id
     * @param position    索引
     * @return
     */
    public static BaseViewHolder getViewHolder(Context context,View convertView, ViewGroup parent, int layoutId, int position) {
        boolean needCreateView = false;
        BaseViewHolder vh = null;
        if (convertView == null) {
            needCreateView = true;
        } else {
            vh = (BaseViewHolder) convertView.getTag();
        }
        if (vh != null && (vh.mLayoutId != layoutId)) {
            needCreateView = true;
        }
        if (needCreateView) {
            return new BaseViewHolder(context,parent, layoutId, position);
        }
        return (BaseViewHolder) convertView.getTag();
    }

    // 返回viewHolder的容器类
    public View getConvertView() {
        return this.mConvertView;
    }

    // 通过一个viewId来获取一个view
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    // 给TextView设置文字
    public void setText(int viewId, String text) {
        if (TextUtils.isEmpty(text)) return;
        TextView tv = getView(viewId);
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
    }

    // 给TextView设置文字
    public void setText(int viewId, SpannableString text) {
        if (text == null) return;
        TextView tv = getView(viewId);
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
    }

    public void setTextColor(int viewId, int textColor) {
        TextView tv = getView(viewId);
        tv.setTextColor(mContext.getResources().getColor(textColor));
        tv.setVisibility(View.VISIBLE);
    }

    public void setIconCode(int viewId,String code){
        IconTextView itv = getView(viewId);
        itv.setIconText(code);
        itv.setVisibility(View.VISIBLE);
    }

    public void setText(int viewId, Spanned text) {
        if (text == null) return;
        TextView tv = getView(viewId);
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
    }

    // 给TextView设置文字
    public void setText(int viewId, int textRes) {
        TextView tv = getView(viewId);
        tv.setText(textRes);
        tv.setVisibility(View.VISIBLE);
    }

    public void setText(int viewId, int textRes, int bgRes, int textColor) {
        TextView tv = getView(viewId);
        tv.setText(textRes);
        tv.setVisibility(View.VISIBLE);
        tv.setBackgroundResource(bgRes);
        tv.setTextColor(textColor);
    }

    public void setText(int viewId, String text, boolean gone) {
        if (TextUtils.isEmpty(text) && gone) {
            getView(viewId).setVisibility(View.GONE);
            return;
        }
        setText(viewId, text);
    }

    public void setText(int viewId, String text, int emptyRes) {
        TextView tv = getView(viewId);
        if (TextUtils.isEmpty(text)) {
            tv.setText(emptyRes);
        } else {
            tv.setText(text);
        }
        tv.setVisibility(View.VISIBLE);
    }

    public void setText(int viewId, String text, String emptyText) {
        TextView tv = getView(viewId);
        if (TextUtils.isEmpty(text)) {
            tv.setText(emptyText);
        } else {
            tv.setText(text);
        }
        tv.setVisibility(View.VISIBLE);
    }

    public void setImage(int viewId, int imgRes) {
        ImageView iv = getView(viewId);
        iv.setImageResource(imgRes);
    }

    public void setGroupImageForNet(int viewId, String imgUrl) {
        ImageView iv = getView(viewId);
        ImageLoader.getInstance().displayImage(imgUrl, new ImageViewAware(iv), groupOptions, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
    }

    public void setImageForNet(int viewId, String imgUrl) {
        ImageView iv = getView(viewId);
//        ImageLoader.getInstance().displayImage(imgUrl, iv, options);
        ImageLoader.getInstance().displayImage(imgUrl, new ImageViewAware(iv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
    }

    public void setImageForNet(ImageView iv, String imgUrl) {
//        ImageLoader.getInstance().displayImage(imgUrl, iv, options);
        ImageLoader.getInstance().displayImage(imgUrl, new ImageViewAware(iv), options, new ImageSize(DisplayUtil.dip2px(mContext, 40), DisplayUtil.dip2px(mContext, 40)), null, null);
    }

    public void setButtonText(int viewId, String text) {
        Button button = getView(viewId);
        button.setText(text);
    }

    public void setOnClick(int viewId, View.OnClickListener onClickListener) {
        View view = getView(viewId);
        view.setOnClickListener(onClickListener);
    }

    public void setGone(int viewId) {
        getView(viewId).setVisibility(View.GONE);
    }

    public void setViewsGone(int... viewIds){
        for(int viewId :viewIds){
            setGone(viewId);
        }
    }

    public void setVisibility(int viewId) {
        getView(viewId).setVisibility(View.VISIBLE);
    }

    public void setViewsVisible(int... viewIds){
        for(int viewId :viewIds){
            setVisibility(viewId);
        }
    }

    public void setInVisibility(int viewId) {
        getView(viewId).setVisibility(View.INVISIBLE);
    }

    public void setViewsInVisible(int... viewIds){
        for(int viewId :viewIds){
            setInVisibility(viewId);
        }
    }

    public boolean isVisibility(int viewId) {
        return (getView(viewId).getVisibility()) == View.VISIBLE;
    }

    public void setEnabled(int viewId) {
        View view = getView(viewId);
        view.setEnabled(true);
    }

    public void setEnabled(int viewId, boolean isEnable) {
        View view = getView(viewId);
        view.setEnabled(isEnable);
    }

    public void setDisEnabled(int viewId) {
        View view = getView(viewId);
        view.setEnabled(false);
    }
}
