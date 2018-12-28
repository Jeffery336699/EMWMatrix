package cc.emw.mobile.chat.util;

class Speex {

	/**
	 * 语音编解码管理类
	 */

	private static final int DEFAULT_COMPRESSION = 8;

	Speex() {
	}

	public void init() {
		load();
		open(DEFAULT_COMPRESSION);
	}

	private void load() {
		try {
			System.loadLibrary("speex");
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public native int open(int compression);

	public native int getFrameSize();

	public native int decode(byte encoded[], short lin[], int size);

	public native int encode(short lin[], int offset, byte encoded[], int size);

	public native void close();

}