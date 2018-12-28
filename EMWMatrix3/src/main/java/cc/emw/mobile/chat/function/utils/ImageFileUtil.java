package cc.emw.mobile.chat.function.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;

/**
 * Created by sunny.du on 2016/12/28.
 * 对拍照的图片进行属性读取和角度处理的工具类
 *
 * **解决不同的手机对相机功能的封装导致拍照出来的照片旋转角度不固定的问题
 */
public class ImageFileUtil {
    /**
     * 照片相关的文件储存路径以及文件存在判断与创建的方法
     * @param dir   路径(包含文件储存的路径或者文件全路径)
     * @param num   只能传入1或者2，传入其他的无效，1判断路径是否存在，不存在则创建。2 判断文件是否存在，存在则删除创建，不存在则创建
     * @return
     * @throws IOException
     */
    public static  boolean isFileExists(File dir, int num) throws IOException {
        if (num == 1) {
            if (!dir.exists()) {
                return dir.mkdirs();
            }
        } else if (num == 2) {
            if (dir.exists()) {
                dir.delete();
            }
            return dir.createNewFile();
        }
        return true;
    }


    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
}
