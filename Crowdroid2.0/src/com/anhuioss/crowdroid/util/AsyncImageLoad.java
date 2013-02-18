package com.anhuioss.crowdroid.util;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.anhuioss.crowdroid.util.AsyncDataLoad.ImageCallback;

public class AsyncImageLoad {

	private Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	public Bitmap loadBitmap(final String imageUrl, final ImageCallback callback) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			if (softReference.get() != null) {
				return softReference.get();
			}
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Bitmap) msg.obj, imageUrl);
			}
		};

		new Thread() {
			public void run() {
				Bitmap drawable = loadImageFromUrl(imageUrl);
				imageCache.put(imageUrl, new SoftReference<Bitmap>(drawable));
				handler.sendMessage(handler.obtainMessage(0, drawable));
			};
		}.start();
		return null;
	}

	protected Bitmap loadImageFromUrl(String pathName) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			return BitmapFactory.decodeFile(pathName, options);
		} catch (Exception e) {
		}
		return null;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageBitmap, String imageUrl);
	}

}
