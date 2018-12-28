package cc.emw.mobile.chat.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import cc.emw.mobile.EMWApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DownLoadImage {
	private static InputStream inputStream = null;
	private static OutputStream outputStream = null;
	private static Bitmap bitmap;
	private static String savePath = EMWApplication.imagePath;

	public static void saveImages(final String url_image, final Context context) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				URL url;
				try {
					url = new URL(url_image);
					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();
					urlConnection.connect();
					inputStream = urlConnection.getInputStream();
					if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						File file1 = new File(savePath, UUID.randomUUID()
								.toString() + ".jpg");
						outputStream = new BufferedOutputStream(
								new FileOutputStream(file1));
						bitmap = BitmapFactory.decodeStream(inputStream);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 80,
								outputStream);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			};

		}).start();
	}
}
