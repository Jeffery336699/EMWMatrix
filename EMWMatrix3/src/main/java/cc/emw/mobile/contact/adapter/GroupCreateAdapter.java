package cc.emw.mobile.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.soundcloud.android.crop.Crop;
import com.zf.iosdialog.widget.ActionSheetDialog;

import java.io.File;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.view.CircleImageView;
import cc.emw.mobile.view.IconTextView;

/**
 * Created by ${zrjt} on 2016/10/13.
 */
public class GroupCreateAdapter extends BaseAdapter {

    private final Context context;
    private DisplayImageOptions options;
    private List<String> mDataList;
    private int positions = -1;
    private static final int REQUEST_CAPTURE_PHOTO = 1010;
    private Uri uri;
    private int gBackImg;

    public GroupCreateAdapter(Context context) {
        this.context = context;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .handler(new Handler()) // default
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public void setData(List<String> mDataList) {
        this.mDataList = mDataList;
    }

    public void setPreImg(Uri uri) {
        this.uri = uri;
    }

    public int getLocalBackImgIndex() {
        return gBackImg;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_group_create_head_img, null);
            vh = new ViewHolder();
            vh.selectTag = (IconTextView) convertView.findViewById(R.id.itv_group_head_img_select);
            vh.circleImageView = (CircleImageView) convertView.findViewById(R.id.cimg_group_create_head_img_item);
            vh.shape = (CircleImageView) convertView.findViewById(R.id.cimg_select_shape);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            vh.selectTag.setVisibility(View.VISIBLE);
            vh.selectTag.setIconText("ec02");
            vh.circleImageView.setImageURI(uri);
        } else {
            vh.selectTag.setVisibility(View.GONE);
            vh.selectTag.setVisibility(positions == position ? View.VISIBLE : View.GONE);
            vh.selectTag.setIconText("e931");
            vh.shape.setVisibility(positions == position ? View.VISIBLE : View.GONE);
            ImageLoader.getInstance().displayImage(mDataList.get(position), new ImageViewAware(vh.circleImageView), options,
                    new ImageSize(DisplayUtil.dip2px(context, 75), DisplayUtil.dip2px(context, 75)), null, null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positions = position;
                vh.selectTag.setIconText(position == 0 ? "ec02" : "e931");
                if (position == 0) {
                    gBackImg = 0;
                    ActionSheetDialog dialog = new ActionSheetDialog(context).builder();
                    dialog.addSheetItem(context.getString(R.string.actionsheet_photo), null,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    Intent intent = new Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                                            .fromFile(new File(EMWApplication.tempPath
                                                    + "tempraw.png")));
                                    ((BaseActivity) context).startActivityForResult(intent,
                                            REQUEST_CAPTURE_PHOTO);
                                }
                            });
                    dialog.addSheetItem(context.getString(R.string.actionsheet_pick), null,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    Crop.pickImage((BaseActivity) context);
                                }
                            });
                    dialog.show();
                } else {
                    gBackImg = position;
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        CircleImageView circleImageView, shape;
        IconTextView selectTag;
    }
}
