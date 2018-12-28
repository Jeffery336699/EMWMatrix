package cc.emw.mobile.chat.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * @author xiang.peng 录音管理类
 * 
 */
public class AudioManager {

	public static AudioManager mInstancel;
	private MediaRecorder mMediaRecorder;
	private String tempPath;
	private boolean isprepared;
	private static String mPath;

	private AudioManager(String path) {
		mPath = path;
	}

	// MediaRecorder准备完毕的回调
	public interface AudioStateListener {
		void wellPrepared();

	}

	public AudioStateListener mListener;

	public void setOnAudioStateListener(AudioStateListener Listener) {
		mListener = Listener;
	}

	public static AudioManager getInstance(String path) {

		if (mInstancel == null) {
			synchronized (AudioManager.class) {
				if (mInstancel == null) {
					mInstancel = new AudioManager(path);
				}
			}
		}
		return mInstancel;
	}

	@SuppressLint("InlinedApi")
	public void prepare() {
		File dir = new File(mPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		isprepared = false;
		String fileName = generateFileName();
		File file = new File(dir, fileName);
		tempPath = file.getAbsolutePath();
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置录音源
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);// 设置录音格式
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 设置录音的编码格式
		mMediaRecorder.setOutputFile(file.getAbsolutePath());
		try {
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			isprepared = true;
			if (mListener != null) {
				mListener.wellPrepared();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 随机生成文件名字
	private String generateFileName() {
		return UUID.randomUUID().toString() + ".mp3";
	}

	// 释放资源
	public void release() {
		if (mMediaRecorder != null) {
			mMediaRecorder.stop();
			mMediaRecorder.reset();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	// 取消 录音
	public void cancle() {
		release();
		if (tempPath != null) {
			File file = new File(tempPath);
			file.delete();
			tempPath = null;
		}
	}

	// 返回音量值
	public int getVoiceLevel(int maxLevel) {
		if (isprepared) {
			try {
				return maxLevel * (mMediaRecorder.getMaxAmplitude() / 32768)
						+ 1;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		return 1;
	}

	public String getPath() {
		return tempPath;
	}
}
