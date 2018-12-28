package cc.emw.mobile.chat.activity;

import cc.emw.mobile.R;
import cc.emw.mobile.chat.util.AudioManager;

import cc.emw.mobile.chat.util.AudioManager.AudioStateListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * 录音按钮 事件处理
 *
 * @author xiang.peng
 */

@SuppressLint("HandlerLeak")
public class RecordButon extends Button implements AudioStateListener {

    public RecordButon(Context context) {
        this(context, null);
    }

    public RecordButon(Context context, AttributeSet attrs) {
        super(context, attrs);
        String path = Environment.getExternalStorageDirectory()
                + "/zkbr_audios";
        mAudioManager = AudioManager.getInstance(path);
        mAudioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mAudioManager.prepare();
                return true;
            }
        });
    }

    private boolean isRecord;
    private AudioManager mAudioManager;// 录音管理
    private long time;
    private boolean isFinshed = false;
    private boolean isCancel;

    class MyRunnable implements Runnable {

        @Override
        public void run() {
            time = 0;
            while (isRecord) {
                try {
                    Thread.sleep(1000);
                    time += 1;
                    mHandler.sendEmptyMessage(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:// 录音开始
                    isRecord = true;
                    isFinshed = false;
                    isCancel = false;
                    time = 0;
                    new Thread(new MyRunnable()).start();
                    Log.d("px", "mHandler 1");
                    break;
                case 2:// 取消录音
                    mAudioManager.cancle();
                    isCancel = true;
                    Log.d("px", "mHandler 2");
                    time = 0;
                    break;
                case 3:
                    if (mListerner != null) {
                        mListerner.audioRecordFinished(mAudioManager.getPath(),
                                time);
                        isFinshed = true;
                    }
                    time = 0;
                    break;
                case 4:
                    if (isFinshed || isCancel) {
                        mListerner.audioTimeChange(0);
                    } else {
                        mListerner.audioTimeChange(time);
                    }
                    break;
            }
        }
    };

    @Override
    public void wellPrepared() {
        Log.d("px", "wellPrepared");
        mHandler.sendEmptyMessage(1);
    }

    public interface onAudioRecordFinished {
        void audioRecordFinished(String path, long time);

        void audioTimeChange(long time);
    }

    private onAudioRecordFinished mListerner;

    public void setOnAudioRecordFinished(onAudioRecordFinished Listerner) {
        mListerner = Listerner;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int X = (int) event.getX();
        int Y = (int) event.getY();
        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE:
//                setBackgroundResource(R.drawable.im_record_down);

                if (isRecord) {
                    // 判断是否是取消录音
                    if (wantCancel(X, Y)) {
                        isRecord = false;
                        mHandler.sendEmptyMessage(2);
                        cc.emw.mobile.util.ToastUtil.showToast(getContext(), R.string.cancel_audio);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
//                setBackgroundResource(R.drawable.im_record);
                Log.d("px","up1="+isRecord+"time="+time);
                if (isRecord && time > 1) {
                    // 录音完成，回调到主界面
                    mAudioManager.release();
                    mHandler.sendEmptyMessage(3);
                } else if (!isCancel && isRecord) {
                    cc.emw.mobile.util.ToastUtil.showToast(getContext(), R.string.tooShort);
                    mHandler.sendEmptyMessage(2);
                }
                isRecord = false;
                Log.d("px","up2="+isRecord+"time="+time);
                break;
            case MotionEvent.ACTION_CANCEL:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isRecord = false;
                        mHandler.sendEmptyMessage(2);
                    }
                }, 1000);
                break;
        }
        return super.onTouchEvent(event);
    }

    // 判断是否取消录音
    private boolean wantCancel(int x, int y) {
        if (x < 0 || x > getWidth()) return true;
        return y < 0 || y >  getHeight();
    }

}
