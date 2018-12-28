package cc.emw.mobile.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cc.emw.mobile.R;
import cc.emw.mobile.base.BaseActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.function.utils.ImageFileUtil;
import cc.emw.mobile.chat.utils.ImageLoader;
import cc.emw.mobile.util.DisplayUtil;
import cc.emw.mobile.view.IconTextView;

/**
 * Created by sunny.du on 2016/11/23.
 */
public class ShowPhotoActivity extends BaseActivity {

    public static final String EXTRA_INTO_FLAG = "into_tag";
    public static final int FLAG_NORMAL = 0; //默认
    public static final int FLAG_DYNAMIC_DISCUSS = 1; //从动态评论进入
    private int flag;

    ImageView mIvPhoto;
    RelativeLayout mRlSendPhoto;
    IconTextView mItvChatPhotoDelete;
    private ImageLoader mImageLoader;
    private String photoUri;
    private ImageView mIvTurnLeft;
    private Bitmap bitmap;
    private Bitmap bitmap2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        mImageLoader = ImageLoader.getInstance(1, ImageLoader.Type.LIFO);
        Intent intent = getIntent();
        flag = getIntent().getIntExtra(EXTRA_INTO_FLAG, FLAG_NORMAL);
        photoUri = intent.getStringExtra("photo_uri");
        initView();
        initEvent();
    }

    private void initEvent() {
        mItvChatPhotoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatContent.REFRESH_CHAT_CAMARE_INFO);
                sendBroadcast(intent);
                finish();
            }
        });
        mRlSendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == FLAG_DYNAMIC_DISCUSS) {
                    Intent data = new Intent();
                    data.putExtra("send_photo_uri", photoUri);
                    setResult(Activity.RESULT_OK, data);
                } else {
                    Intent intent = new Intent(ChatContent.REFRESH_CHAT_PHOTO_INFO);
                    intent.putExtra("send_photo_uri", photoUri);
                    sendBroadcast(intent);
                }
                finish();
            }
        });
        mIvTurnLeft.setOnClickListener(new View.OnClickListener() {//旋转图片
            @Override
            public void onClick(View v) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(photoUri);
                    bitmap = BitmapFactory.decodeStream(fis);
                    bitmap2 = ImageFileUtil.rotaingImageView(90, bitmap);
                    writeImage(bitmap2);
                    mIvPhoto.setImageBitmap(bitmap2);
//                    mImageLoader.loadImage(photoUri, mIvPhoto);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (bitmap2 != null) {
            bitmap2.recycle();
        }
    }

    private void writeImage(Bitmap bitmap) {
        OutputStream outputStream = null;
        try {
            File file = new File(photoUri);
            if (ImageFileUtil.isFileExists(file, 2)) {
                outputStream = new FileOutputStream(file);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void initView() {
        mIvPhoto = (ImageView) findViewById(R.id.iv_photo);
        mRlSendPhoto = (RelativeLayout) findViewById(R.id.rl_send_photo);
        mItvChatPhotoDelete = (IconTextView) findViewById(R.id.itv_chat_photo_delete);
        mIvTurnLeft = (ImageView) findViewById(R.id.iv_turn_left);
        ViewGroup.LayoutParams layoutParams = mIvPhoto.getLayoutParams();
        layoutParams.height = (DisplayUtil.getDisplayHeight(this) / 6) * 4;
        mImageLoader.loadImage(photoUri, mIvPhoto);
    }
}
