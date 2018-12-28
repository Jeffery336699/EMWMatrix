package cc.emw.mobile.dynamic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

import cc.emw.mobile.R;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.view.RoundedImageView;

/**
 * Created by tao.zhou on 2017/7/22.
 */

public class LocationImgAdapter extends RecyclerView.Adapter<LocationImgAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mDatas;
    private DisplayImageOptions options;
    private OnRvItemClickListener listener;

    public LocationImgAdapter(Context mContext) {
        this.mContext = mContext;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.color.gray_1)
                .showImageForEmptyUri(R.color.gray) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.color.gray) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setData(List<String> mDatas) {
        this.mDatas = mDatas;
    }

    public void setOnRvItemClickListener(OnRvItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_dynamic_img, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String uri = mDatas.get(position);
        ImageLoader.getInstance().displayImage("file://" + uri, new ImageViewAware(holder.mImg), options,
                new ImageSize(DisplayUtil.dip2px(mContext, 72), DisplayUtil.dip2px(mContext, 72)), null, null);
        holder.itemView.setTag(uri);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRItemClickListener(v.getTag().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            mImg = (RoundedImageView) itemView.findViewById(R.id.iv_dynamic_img);
        }

        RoundedImageView mImg;
    }

    public interface OnRvItemClickListener {
        void onRItemClickListener(String uri);
    }
}
