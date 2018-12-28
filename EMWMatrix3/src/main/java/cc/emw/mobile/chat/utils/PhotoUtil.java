package cc.emw.mobile.chat.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny.du on 2016/11/23.
 * 获取手机相册的所有图片列表工具类
 * ContentResolver contentResolver = getContentResolver(); ---->On Activity
 */
public class PhotoUtil {

   public static List<String> getAllPhoto(ContentResolver contentResolver){
       List<String> photoList=new ArrayList<>();
       Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
       //获取jpeg和png格式的文件，并且按照时间进行倒序
       Cursor cursor = contentResolver.query(uri, null, MediaStore.Images.Media.MIME_TYPE + "=\"image/jpeg\" or " +
               MediaStore.Images.Media.MIME_TYPE + "=\"image/png\"", null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
       if (cursor != null) {
           while (cursor.moveToNext()) {
               String pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//图片路径
               photoList.add(pathImage);
           }
           cursor.close();
           cursor=null;
       }
       return photoList;
   }

    public static List<String> getPageAllPhoto(ContentResolver contentResolver,int num[]){
        List<String> photoList=new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //获取jpeg和png格式的文件，并且按照时间进行倒序
        Cursor cursor = contentResolver.query(uri, null, MediaStore.Images.Media.MIME_TYPE + "=\"image/jpeg\" or " +
                MediaStore.Images.Media.MIME_TYPE + "=\"image/png\"", null, MediaStore.Images.Media.DATE_MODIFIED + " desc limit "+num[0]+","+num[1]);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//图片路径
                photoList.add(pathImage);
            }
            cursor.close();
            cursor=null;
        }
        return photoList;
    }



}


/**
 /获取图片的名称
 String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
 //获取图片的生成日期
 byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
 //获取图片的详细信息
 String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
 */
