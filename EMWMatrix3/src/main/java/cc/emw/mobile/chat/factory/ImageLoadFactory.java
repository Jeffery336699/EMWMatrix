package cc.emw.mobile.chat.factory;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import cc.emw.mobile.R;

/**
 * Created by sunny.du on 2016/9/19.
 */
public class ImageLoadFactory {
    public static DisplayImageOptions getChatOptiones(){
        return new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head)
//                .showImageForEmptyUri(R.drawable.cm_img_head)
//                .showImageOnFail(R.drawable.cm_img_head)
                .cacheInMemory(true).cacheOnDisk(true).build();
    }
    public static DisplayImageOptions getChatTeamOptiones(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cm_img_grouphead)
                .showImageForEmptyUri(R.drawable.cm_img_grouphead)
                .showImageOnFail(R.drawable.cm_img_grouphead)
                .cacheInMemory(true).cacheOnDisk(true).build();
    }
    public static DisplayImageOptions getChatTeamTitleOptiones(){
        return  new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.desert) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.desert) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.desert) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(100)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public static DisplayImageOptions getChatVideoOption(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.trans_bg)
                .showImageForEmptyUri(R.drawable.trans_bg)
                .showImageOnFail(R.drawable.trans_bg)
                .cacheInMemory(true).cacheOnDisk(true).build();
    }
    public static DisplayImageOptions getChatPlayerOption() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.chat_jiazaishibai) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
                .delayBeforeLoading(100).bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY) // 图像将完全按比例缩小的目标大小
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
    }
    public static DisplayImageOptions getChatPageOptiones(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.chat_jiazaishibai) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.chat_jiazaishibai) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.chat_jiazaishibai) // 设置图片加载或解码过程中发生错误显示的图片
                .delayBeforeLoading(100).bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true).resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    public static DisplayImageOptions getChatTeamUserImageOption() {
        return  new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.cm_img_head) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.cm_img_head) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.cm_img_head) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
    }

    public static DisplayImageOptions getChatApdaterImage(){
        return  new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.friends_sends_pictures_no) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.friends_sends_pictures_no) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.friends_sends_pictures_no) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
    }
    public static DisplayImageOptions getChatApdaterImage2(){
        return  new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.load_error_rqcode) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.load_error_rqcode) // 设置图片加载或解码过程中发生错误显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(false) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(false) // 设置下载的图片是否缓存在SD卡中
                .build(); // 创建配置过得DisplayImageOption对象
    }

}
