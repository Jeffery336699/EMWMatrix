package cc.emw.mobile.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.util.ToastUtil;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

/**
 * @author yuanhang.liu
 * @package cc.emw.mobile.chat
 * @data on 2018/8/24  9:42
 * @describe TODO
 */
@ContentView(R.layout.activity_capture_tast)
public class CaptureTestActivity extends BaseActivity {

    @ViewInject(R.id.scanner_text)
    private EditText scanner_text;
    @ViewInject(R.id.scanner_img)
    private ImageView scanner_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Event(value = {R.id.scanner_test, R.id.scanner_insert, R.id.cm_header_tv_right9, R.id.cm_header_btn_left9})
    private void onHeaderClick(View v) {
        switch (v.getId()) {
            case R.id.scanner_test:
                Intent intent = new Intent(this, CaptureActivity.class);
                /*ZxingConfig是配置类
                可以设置是否显示底部布局，闪光灯，相册，
                是否播放提示音 震动
                设置扫描框颜色等
                也可以不传这个参数*/

                ZxingConfig config = new ZxingConfig();
                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                config.setShake(true);//是否震动  默认为true
                config.setDecodeBarCode(false);//是否扫描条形码 默认为true
                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为淡蓝色
                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, 400);
                break;
            case R.id.scanner_insert:
                String contentEtString = scanner_text.getText().toString().trim();

                if (TextUtils.isEmpty(contentEtString)) {
                    Toast.makeText(this, "二维码地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bitmap bitmap = null;
                try {

                    /*contentEtString：字符串内容
                    w：图片的宽
                    h：图片的高
                    logo：不需要logo的话直接传null*/

                    Bitmap
                            logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                    bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, logo);
                    scanner_img.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cm_header_tv_right9:
                scanner_img.buildDrawingCache(true);
                scanner_img.buildDrawingCache();
                Bitmap bitmap1 = scanner_img.getDrawingCache();
                if (null == bitmap1) {
                    ToastUtil.showToast(this, "未生成二维码");
                    return;
                }
                saveBitmapFile(bitmap1);
                scanner_img.setDrawingCacheEnabled(false);
                break;
            case R.id.cm_header_btn_left9:
                onBackPressed();
                break;
        }
    }

    public void saveBitmapFile(Bitmap bitmap) {

        //File temp = new File("/sdcard/1delete/");//要保存文件先创建文件夹
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "talker";
        File temp = new File(storePath);//要保存文件先创建文件夹*//**//*
        if (!temp.exists()) {
            temp.mkdir();
        }
        ////重复保存时，覆盖原同名图片
        //File file = new File("/sdcard/1delete/1.jpg");//将要保存图片的路径和图片名称
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(temp, fileName);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            ToastUtil.showToast(this, "图片保存成功：" + file.getPath());
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(View v) {////点击按钮删除这个文件
        File file = new File("/sdcard/1spray/1.png");
        if (file.exists()) {
            file.delete();
        }

    }
}
