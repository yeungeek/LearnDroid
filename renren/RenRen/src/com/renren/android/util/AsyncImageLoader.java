package com.renren.android.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * 异步加载图片类(包含软引用、本地、网络)
 * 
 * @author rendongwei
 * 
 */
public class AsyncImageLoader {
	private Map<String, SoftReference<Bitmap>> imageCache = null;

	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	/**
	 * 返回当前的要获取的图片
	 * 原理:先通过软引用查询图片名称,如果软引用存在该图片,就直接返回该图片的BitMap,如果不存在,则从本地缓存文件中查询图片名称,
	 * 如果存在则返回该图片的BitMap,如果不存在则通过URL地址下载图片到软引用和本地缓存中并通过回调接口给ImageView添加图片
	 * 
	 * @param httpURL
	 *            图片的URL地址
	 * @param imageView
	 *            图片的ImageView
	 * @param imageName
	 *            图片的名字
	 * @param imageCallBack
	 *            回调接口
	 * @return 图片的BitMap
	 */
	public Bitmap loadBitmap(final String httpURL, final ImageView imageView,
			final String imageName, final ImageCallBack imageCallBack) {
		try {
			if (imageCache.containsKey(imageName)) {
				SoftReference<Bitmap> reference = imageCache.get(imageName);
				Bitmap bitmap = reference.get();
				if (bitmap != null) {
					return bitmap;
				}
			} else {
				File cacheDir = new File("/sdcard/RenRenForAndroid/Cache/");
				if (!cacheDir.exists()) {
					cacheDir.mkdirs();
				}

				File[] cacheFiles = cacheDir.listFiles();
				int i = 0;
				if (cacheFiles != null) {
					for (; i < cacheFiles.length; i++) {
						if (imageName.equals(cacheFiles[i].getName())) {
							break;
						}
					}
				}
				if (i < cacheFiles.length) {
					return BitmapFactory
							.decodeFile("/sdcard/RenRenForAndroid/Cache/"
									+ imageName);
				}
			}
			final Handler handler = new Handler() {

				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					imageCallBack.imageLoad(imageView, (Bitmap) msg.obj);
				}
			};
			new Thread(new Runnable() {

				public void run() {
					InputStream bitmapIs = null;
					Bitmap bitmap = null;
					try {
						URL url = new URL(httpURL);
						HttpURLConnection connection = (HttpURLConnection) url
								.openConnection();
						bitmapIs = connection.getInputStream();
						bitmap = BitmapFactory.decodeStream(bitmapIs);
						bitmapIs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Message msg = handler.obtainMessage(0, bitmap);
					handler.sendMessage(msg);
					imageCache
							.put(imageName, new SoftReference<Bitmap>(bitmap));
					File dir = new File("/sdcard/RenRenForAndroid/Cache/");
					if (!dir.exists()) {
						dir.mkdirs();
					}
					File bitmapFile = new File(
							"/sdcard/RenRenForAndroid/Cache/" + imageName);
					if (!bitmapFile.exists()) {
						try {
							bitmapFile.createNewFile();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					FileOutputStream fos;
					try {
						fos = new FileOutputStream(bitmapFile);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			return null;
		}

		return null;
	}

	/**
	 * 图片的回调接口
	 * 
	 * @author rendongwei
	 * 
	 */
	public interface ImageCallBack {
		public void imageLoad(ImageView imageView, Bitmap bitmap);
	}
}
