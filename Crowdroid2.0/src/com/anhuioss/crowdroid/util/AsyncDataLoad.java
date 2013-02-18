package com.anhuioss.crowdroid.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class AsyncDataLoad {
	private static final String TAG = "AsyncImageLoader";
	private Map<String, SoftReference<Drawable>> imageCache;

	public AsyncDataLoad() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback callback) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Drawable) msg.obj, imageUrl);
			}
		};

		new Thread() {
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				handler.sendMessage(handler.obtainMessage(0, drawable));
			};
		}.start();
		return null;
	}

	protected Drawable loadImageFromUrl(String imageUrl) {
		try {

			Bitmap bit = getHttpBitmap(imageUrl);

			Drawable drawable = new BitmapDrawable(bit);
			return drawable;

			// return Drawable.createFromStream(new URL(imageUrl).openStream(),
			// "src");
		} catch (Exception e) {
		}
		return null;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

	public static Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		byte[] imageByte = getImageFromURL(url.trim());

		// 以下是把图片转化为缩略图再加载

		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true; // 首先设置.inJustDecodeBounds为true

		bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length,
				options); // 这时获取到的bitmap是null的，尚未调用系统内存资源

		options.inJustDecodeBounds = false; // 得到图片有宽和高的options对象后，设置.inJustDecodeBounds为false。
		options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		options.inPurgeable = true;
		options.inInputShareable = true;
		int be = (int) (options.outHeight / (float) 200);

		if (be <= 0)
			be = 1;

		options.inSampleSize = be; // 计算得到图片缩小倍数

		bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length,
				options); // 获取真正的图片对象（缩略图）
		//
		//
		// try {
		// myFileURL = new URL(url);
		// // 获得连接
		// HttpURLConnection conn = (HttpURLConnection) myFileURL
		// .openConnection();
		// // 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
		// conn.setConnectTimeout(6000);
		// // 连接设置获得数据流
		// conn.setDoInput(true);
		// // 不使用缓存
		// conn.setUseCaches(false);
		// // 这句可有可无，没有影响
		// // conn.connect();
		// // 得到数据流
		// InputStream is = conn.getInputStream();
		//
		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inJustDecodeBounds = true;
		// bitmap = BitmapFactory.decodeStream(is, null, options);
		// options.inJustDecodeBounds = false;
		// int be = (int) (options.outHeight / (float) 200);
		// if (be <= 0) {
		// be = 1;
		// }
		//
		// options.inSampleSize = be;
		// options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		// options.inPurgeable = true;
		// options.inInputShareable = true;
		//
		// bitmap = BitmapFactory.decodeStream(is, null, options);
		//
		// // 关闭数据流
		// is.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		return bitmap;

	}

	/**
	 * 根据图片网络地址获取图片的byte[]类型数据
	 * 
	 * @param urlPath
	 *            图片网络地址
	 * @return 图片数据
	 */
	public static byte[] getImageFromURL(String urlPath) {
		byte[] data = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlPath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			// conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6000);
			is = conn.getInputStream();
			if (conn.getResponseCode() == 200) {
				data = readInputStream(is);
			} else
				System.out.println("发生异常！");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * 读取InputStream数据，转为byte[]数据类型
	 * 
	 * @param is
	 *            InputStream数据
	 * @return 返回byte[]数据
	 */
	public static byte[] readInputStream(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		try {
			while ((length = is.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			baos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = baos.toByteArray();
		try {
			is.close();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

}
