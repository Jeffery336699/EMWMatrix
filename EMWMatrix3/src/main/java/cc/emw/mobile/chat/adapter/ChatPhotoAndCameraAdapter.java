package cc.emw.mobile.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cc.emw.mobile.EMWApplication;
import cc.emw.mobile.R;
import cc.emw.mobile.chat.ShowPhotoActivity;
import cc.emw.mobile.chat.base.ChatContent;
import cc.emw.mobile.chat.imagepicker.ImagePicker;
import cc.emw.mobile.chat.imagepicker.ui.ImageGridActivity;
import cc.emw.mobile.chat.utils.ImageLoader;
import cc.emw.mobile.chat.view.PicassoImageLoader;
import cc.emw.mobile.chat.view.PreSurfaceView;
import cc.emw.mobile.record.CameraActivity;
import cc.emw.mobile.util.DisplayUtil;

//import com.lzy.imagepicker.ImagePicker;

/**
 * Created by sunny.du on 2017/3/28.
 */
public class ChatPhotoAndCameraAdapter extends RecyclerView.Adapter<ChatPhotoAndCameraAdapter.MyHolder> implements PreSurfaceView.OnCameraStatusListener {
    private Context mContext;
    private List<String> mDataList;
    private LayoutInflater mLayoutInflater;
    private ImageLoader mImageLoader;
    private List<String> mPhotoListI;
    private List<String> mPhotoListII;
    private PreSurfaceView mPreSurfaceView;
    private ImagePicker imagePicker;
    private int flag; //1:动态评论进入

    public ChatPhotoAndCameraAdapter(Context context) {
        this(context, 0);
    }

    public ChatPhotoAndCameraAdapter(Context context, int flag) {
        this.mContext = context;
        this.flag = flag;
        mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
        mLayoutInflater = LayoutInflater.from(mContext);
        mDataList = new ArrayList<>();
        mPhotoListI = new ArrayList<>();
        mPhotoListII = new ArrayList<>();

        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);  //显示拍照按钮
        imagePicker.setCrop(false);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setMultiMode(false);
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    /**
     * 获取消息列表
     * 第一次加载或者消息发生变化的时候
     */
    public void setData(List<String> dataList) {
        this.mDataList = dataList;
        for (int i = 0; i < mDataList.size(); i++) {
            if (i == 0 || i % 2 == 0) {
                mPhotoListI.add(mDataList.get(i));
            } else {
                mPhotoListII.add(mDataList.get(i));
            }
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0://全屏相机/照片入口
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_photo_window_one, parent, false), viewType);
            case 1://半屏拍照
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_photo_window_two, parent, false), viewType);
            case 2://相片预览
                return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_photo_window_there, parent, false), viewType);
        }
        return new MyHolder(mLayoutInflater.inflate(R.layout.chat_item_photo_window_there, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if (position == 0) {
            bindEventBut(holder);
        } else if (position == 1) {
            this.mPreSurfaceView = holder.mPreSurfaceView;
            bindCamera(holder);
            mPreSurfaceView.openCamera();
            mPreSurfaceView.setOnCameraStatusListener(this);
        } else if (position > 1) {
            bindPhoto(holder, position - 2);
        }
    }

    /**
     * 2
     * mIvPhoto1
     * mIvPhoto2
     */
    private void bindPhoto(MyHolder holder, int position) {
        String url1 = null;
        if (position <= mPhotoListI.size() - 1) {
            url1 = mPhotoListI.get(position);
        }
        String url2 = null;
        if (position <= mPhotoListII.size() - 1) {
            url2 = mPhotoListII.get(position);
        }

        if (!TextUtils.isEmpty(url1)) {
            holder.mIvPhoto1.setVisibility(View.VISIBLE);
            holder.mIvPhoto1.setImageResource(R.drawable.friends_sends_pictures_no);
            mImageLoader.loadImage(url1, holder.mIvPhoto1);
            final String url = url1;
            holder.mIvPhoto1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ShowPhotoActivity.class);
                    if (flag == 1) {
                        intent.putExtra(ShowPhotoActivity.EXTRA_INTO_FLAG, ShowPhotoActivity.FLAG_DYNAMIC_DISCUSS);
                    }
                    intent.putExtra("photo_uri", url);
                    ((Activity)mContext).startActivityForResult(intent, 10000);
                }
            });
        } else {
            holder.mIvPhoto1.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(url2)) {
            holder.mIvPhoto2.setVisibility(View.VISIBLE);
            holder.mIvPhoto2.setImageResource(R.drawable.friends_sends_pictures_no);
            mImageLoader.loadImage(url2, holder.mIvPhoto2);
            final String url = url2;
            holder.mIvPhoto2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ShowPhotoActivity.class);
                    if (flag == 1) {
                        intent.putExtra(ShowPhotoActivity.EXTRA_INTO_FLAG, ShowPhotoActivity.FLAG_DYNAMIC_DISCUSS);
                    }
                    intent.putExtra("photo_uri", url);
                    ((Activity)mContext).startActivityForResult(intent, 10000);
                }
            });
        } else {
            holder.mIvPhoto2.setVisibility(View.GONE);
        }
    }

    /**
     * 1
     */
    private void bindCamera(MyHolder holder) {
        //拍照按钮
        holder.mIvTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreSurfaceView.takePicture();
            }
        });
        //正反面切换
        holder.mIvChangeCamareState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreSurfaceView.changeOrientation();
                mPreSurfaceView.openCamera();
            }
        });
    }

    /**
     * 0
     */
    private void bindEventBut(final MyHolder holder) {
        holder.mItvChatMaxImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (flag == 1) {
                    intent = new Intent(mContext, cc.emw.mobile.me.imagepicker.ui.ImageGridActivity.class);
                } else {
                    intent = new Intent(mContext, ImageGridActivity.class);
                }
                ((Activity) mContext).startActivityForResult(intent, 100);
            }
        });
        holder.mItvChatMaxPhoto.setOnClickListener(new View.OnClickListener() {//全屏拍照
            @Override
            public void onClick(View v) {
                getPreSurfaceView().closeCamera();
                if (flag == 1) {
                    Intent intent = new Intent(mContext, CameraActivity.class);
                    intent.putExtra(CameraActivity.EXTRA_CAMERA_TYPE, CameraActivity.TYPE_TAKE);
                    ((Activity) mContext).startActivityForResult(intent, 101);
                } else {
                    Intent colseIntent = new Intent();
                    colseIntent.setAction(ChatContent.REFRESH_CHAT_COLSE_PHOTO_INFO);
                    mContext.sendBroadcast(colseIntent);
                    Intent intent = new Intent(mContext, CameraActivity.class);
                    intent.putExtra(CameraActivity.EXTRA_INTO_FLAG, CameraActivity.FLAG_CHAT);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return 1;
        } else {
            return 2;
        }
    }

    public PreSurfaceView getPreSurfaceView() {
        return mPreSurfaceView;
    }


    class MyHolder extends RecyclerView.ViewHolder {
        View mItemView;
        ImageView mItvChatMaxPhoto;
        ImageView mItvChatMaxImage;
        PreSurfaceView mPreSurfaceView;
        ImageView mIvPhoto1;
        ImageView mIvPhoto2;
        ImageView mIvTakePicture;
        ImageView mIvChangeCamareState;

        public MyHolder(View itemView, int itemType) {
            super(itemView);
            initItemView(itemView, itemType);
            this.mItemView = itemView;
        }

        private void initItemView(View itemView, int itemType) {
            switch (itemType) {
                case 0:
                    mItvChatMaxPhoto = (ImageView) itemView.findViewById(R.id.iv_photo_event_1);
                    mItvChatMaxImage = (ImageView) itemView.findViewById(R.id.iv_photo_event_2);
                    break;
                case 1:
                    mPreSurfaceView = (PreSurfaceView) itemView.findViewById(R.id.presurface_view);
                    mIvTakePicture = (ImageView) itemView.findViewById(R.id.iv_take_picture);
                    mIvChangeCamareState = (ImageView) itemView.findViewById(R.id.iv_change_camare_state);
                    break;
                case 2:
                    mIvPhoto1 = (ImageView) itemView.findViewById(R.id.iv_photo_1);
                    mIvPhoto2 = (ImageView) itemView.findViewById(R.id.iv_photo_2);
                    if (flag == 1) {
                        int wh = DisplayUtil.dip2px(mContext, 120);
                        mIvPhoto1.setLayoutParams(new LinearLayout.LayoutParams(wh, wh));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(wh, wh);
                        params.topMargin = DisplayUtil.dip2px(mContext, 1);
                        mIvPhoto2.setLayoutParams(params);
                    }
                    break;
            }
        }
    }
    @Override
    public void onCameraStopped(byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        try {
            long time = System.currentTimeMillis();
            writeImage(bitmap, data, time);
            mPreSurfaceView.closeCamera();
            Intent intentImage = new Intent(mContext, ShowPhotoActivity.class);
            intentImage.putExtra(ShowPhotoActivity.EXTRA_INTO_FLAG, ShowPhotoActivity.FLAG_DYNAMIC_DISCUSS);
            intentImage.putExtra("photo_uri", EMWApplication.tempPath + time + ".png");
            ((Activity)mContext).startActivityForResult(intentImage, 10000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Bitmap returnBm;
    private void writeImage(Bitmap bitmap, byte[] imageData, long timeName) throws IOException {
        OutputStream outputStream = null;
        try {
            File dir = new File(EMWApplication.tempPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(EMWApplication.tempPath, timeName + ".png");
            boolean falg = file.createNewFile();
            if (falg) {
                outputStream = new FileOutputStream(file);
                if (bitmap != null) {
                    Matrix matrix = new Matrix();
                    if (mPreSurfaceView.getCameraPosition() == 0) {
                        matrix.postRotate(90);
                    } else {
                        matrix.postRotate(270);
                    }
                    bitmap = imageZoom(bitmap);
                    returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                    bitmap = null;
                    returnBm.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                } else {
                    outputStream.write(imageData);
                    outputStream.flush();
                }
            } else {
                file.delete();
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                if (bitmap != null) {
                    Matrix matrix = new Matrix();
                    if (mPreSurfaceView.getCameraPosition() == 0) {
                        matrix.postRotate(90);
                    } else {
                        matrix.postRotate(270);
                    }
                    bitmap = imageZoom(bitmap);
                    Bitmap returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                    bitmap = null;
                    returnBm.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                } else {
                    outputStream.write(imageData);
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    returnBm.recycle();
                    returnBm = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap imageZoom(Bitmap bitMap) {
        //图片允许最大空间   单位：KB
        double maxSize = 400.00;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将字节换成KB
        double mid = b.length / 1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            return zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
                    bitMap.getHeight() / Math.sqrt(i));
        }
        return bitMap;
    }

    public Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}
