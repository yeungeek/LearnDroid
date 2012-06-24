package com.anhuioss.crowdroid.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import com.anhuioss.crowdroid.data.info.TimeLineInfo;

import dalvik.system.VMRuntime;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.hardware.Camera.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GalleryFlowAdapter extends BaseAdapter {

	/*
	 * 在ImageAdapter中主要做了图片的倒影效果以及创建了对原始图片和倒影的显示区域。
	 * GalleryFlow中主要做了对图片的旋转和缩放操作，根据图片的屏幕中的位置对其进行旋转缩放操作。
	 */
	int mGalleryItemBackground;
	private Context mContext;
	private String[] mImageUrls;
	private ImageView[] mImageViews;
	int current_pos = 0;
	private ArrayList<TimeLineInfo> currentInfoList;

	private final static int OPTIONS_HEAP_SIZE = 6 * 1024 * 1024;
	private SoftReference<Bitmap> originalImage;
	static private GalleryFlowAdapter cache;
	/** 用于Chche内容的存储 */
	private Hashtable<Integer, MySoftRef> hashRefs;
	/** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
	private ReferenceQueue<Bitmap> q;

	public GalleryFlowAdapter() {

		hashRefs = new Hashtable<Integer, MySoftRef>();
		q = new ReferenceQueue<Bitmap>();

	}

	public GalleryFlowAdapter(Context context, String[] ImageIds) {
		mContext = context;
		mImageUrls = ImageIds;
		mImageViews = new ImageView[mImageUrls.length];

	}

	public GalleryFlowAdapter(Context context,
			ArrayList<TimeLineInfo> timelineInfoList) {
		hashRefs = new Hashtable<Integer, MySoftRef>();
		q = new ReferenceQueue<Bitmap>();
		mContext = context;
		currentInfoList = timelineInfoList;
		mImageUrls = new String[timelineInfoList.size()];
		for (int index = 0; index < currentInfoList.size(); index++) {
			String url = currentInfoList.get(index).getStatus();
			String largeUrl = url.substring(url.indexOf(";") + 1);
			mImageUrls[index] = largeUrl;
		}
		mImageViews = new ImageView[mImageUrls.length];
	}

	/**
	 * 创建倒影效果
	 * 
	 * @return
	 */
	public boolean createReflectedImages() {
		// 倒影图和原图之间的距离
		final int reflectionGap = 8;
		int index = 0;
		VMRuntime.getRuntime().setMinimumHeapSize(OPTIONS_HEAP_SIZE); // 设置最小heap内存为6MB大小。当然对于内存吃紧来说还可以通过手动干涉GC去处理

		for (String imageId : mImageUrls) {
			// Bitmap originalImage = BitmapFactory.decodeResource(mContext
			// .getResources(), imageId);
			try {
				// 防止OOM
				cleanCache();
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inTempStorage = new byte[6 * 1024 * 1024];
				opts.inJustDecodeBounds = true;

				opts.inSampleSize = 4;
				CookieManager cm = CookieManager.getInstance();
				cm.removeSessionCookie();
				// 返回原图解码之后的bitmap对象

				originalImage = new SoftReference<Bitmap>(getBitMap(mContext,
						imageId, opts));
				// Bitmap originalImage = getBitMap(mContext, imageId, opts);

				int width = originalImage.get().getWidth();
				int height = originalImage.get().getHeight();
				// 创建矩阵对象
				Matrix matrix = new Matrix();
				// 指定一个角度以0,0为坐标进行旋转
				// matrix.setRotate(30);
				// 指定矩阵(x轴不变，y轴相反)
				matrix.preScale(1, -1);
				// 将矩阵应用到该原图之中，返回一个宽度不变，高度为原图1/2的倒影位图
				Bitmap reflectionImage = Bitmap.createBitmap(
						originalImage.get(), 0, height / 2, width, height / 2,
						matrix, false);
				// 创建一个宽度不变，高度为原图+倒影图高度的位图
				Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
						(height + height / 2), Config.ARGB_8888);
				// 将上面创建的位图初始化到画布
				Canvas canvas = new Canvas(bitmapWithReflection);
				canvas.drawBitmap(originalImage.get(), 0, 0, null);

				Paint deafaultPaint = new Paint();
				// deafaultPaint.setAntiAlias(true);
				canvas.drawRect(0, height, width, height + reflectionGap,
						deafaultPaint);

				canvas.drawBitmap(reflectionImage, 0, height + reflectionGap,
						null);

				Paint paint = new Paint();
				// paint.setAntiAlias(true);

				/**
				 * 参数一:为渐变起初点坐标x位置， 参数二:为y轴位置， 参数三和四:分辨对应渐变终点， 最后参数为平铺方式，
				 * 这里设置为镜像Gradient是基于Shader类，所以我们通过Paint的setShader方法来设置这个渐变
				 */
				LinearGradient shader = new LinearGradient(0, originalImage
						.get().getHeight(), 0, bitmapWithReflection.getHeight()
						+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
				// 设置阴影
				paint.setShader(shader);

				paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

				// 用已经定义好的画笔构建一个矩形阴影渐变效果
				canvas.drawRect(0, height, width,
						bitmapWithReflection.getHeight() + reflectionGap, paint);
				ImageView imageView = new ImageView(mContext);
				imageView.setImageBitmap(bitmapWithReflection);
				imageView
						.setLayoutParams(new GalleryFlow.LayoutParams(180, 240));
				mImageViews[index++] = imageView;

			} catch (OutOfMemoryError e) {
				// TODO: handle exception
			}
		}
		return true;
	}

	private Resources getResources() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCount() {
		return mImageUrls.length;
	}

	public Object getItem(int position) {

		return position;
	}

	public long getItemId(int position) {
		current_pos = position;

		return position;
	}

	public int getItemId() {

		return current_pos;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// 处理内存溢出
		if (originalImage != null) {
			if (originalImage.get() != null
					&& !originalImage.get().isRecycled()) {
				originalImage.get().recycle();
				originalImage = null;
			}
		}
		return mImageViews[position];
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

	public synchronized Bitmap getBitMap(Context c, String url,
			BitmapFactory.Options options) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			// 当网络连接异常后,给个默认图片
			return bitmap;
		}
		try {
			// 打开网络连接
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream(); // 把得到的内容转换成流
			int length = (int) conn.getContentLength(); // 获取文件的长度
			if (length != -1) {
				byte[] imgData = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, imgData, destPos, readLen);
					destPos += readLen;
				}

				bitmap = BitmapFactory.decodeByteArray(imgData, 0,
						imgData.length);
			}

		} catch (IOException e) {

			return bitmap;
		}

		return bitmap;
	}

	// =====================================================================

	/**
	 * 取得缓存器实例
	 */
	public static GalleryFlowAdapter getInstance() {
		if (cache == null) {
			cache = new GalleryFlowAdapter();
		}
		return cache;
	}

	/**
	 * 以软引用的方式对一个Bitmap对象的实例进行引用并保存该引用
	 */
	private void addCacheBitmap(Bitmap bmp, Integer key) {
		cleanCache();// 清除垃圾引用
		MySoftRef ref = new MySoftRef(bmp, q, key);
		hashRefs.put(key, ref);
	}

	/**
	 * 继承SoftReference，使得每一个实例都具有可识别的标识。
	 */
	private class MySoftRef extends SoftReference<Bitmap> {
		private Integer _key = 0;

		public MySoftRef(Bitmap bmp, ReferenceQueue<Bitmap> q, int key) {
			super(bmp, q);
			_key = key;
		}
	}

	/**
	 * 清除Cache内的全部内容
	 */
	public void clearCache() {
		cleanCache();
		hashRefs.clear();
		System.gc();
		System.runFinalization();
	}

	private void cleanCache() {
		MySoftRef ref = null;
		while ((ref = (MySoftRef) q.poll()) != null) {
			hashRefs.remove(ref._key);
		}
	}

	// public static Size getBitMapSize(String path) {
	// File file = new File(path);
	// if (file.exists()) {
	// InputStream in = null;
	// try {
	// in = new FileInputStream(file);
	// BitmapFactory.decodeStream(in, null, OPTIONS_GET_SIZE);
	// return new Size(OPTIONS_GET_SIZE.outWidth,
	// OPTIONS_GET_SIZE.outHeight);
	// } catch (FileNotFoundException e) {
	// return ZERO_SIZE;
	// } finally {
	// closeInputStream(in);
	// }
	// }
	// return ZERO_SIZE;
	// }
	//
	// public static Bitmap createBitmap(String path, int width, int height) {
	// File file = new File(path);
	// if (file.exists()) {
	// InputStream in = null;
	// try {
	// in = new FileInputStream(file);
	// Size size = getBitMapSize(path);
	// if (size.equals(ZERO_SIZE)) {
	// return null;
	// }
	// int scale = 1;
	// int a = size.getWidth() / width;
	// int b = size.getHeight() / height;
	// scale = Math.max(a, b);
	// synchronized (OPTIONS_DECODE) {
	// OPTIONS_DECODE.inSampleSize = scale;
	// Bitmap bitMap = BitmapFactory.decodeStream(in, null,
	// OPTIONS_DECODE);
	// return bitMap;
	// }
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// } finally {
	// closeInputStream(in);
	// }
	// }
	// return null;
	// }
	//
	// public static void destory(Bitmap bitmap) {
	// if (null != bitmap && !bitmap.isRecycled()) {
	// bitmap.recycle();
	// bitmap = null;
	// }
	// }
	//
	// private static void closeInputStream(InputStream in) {
	// if (null != in) {
	// try {
	// in.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// class Size {
	// private int width, height;
	//
	// Size(int width, int height) {
	// this.width = width;
	// this.height = height;
	// }
	//
	// public int getWidth() {
	// return width;
	// }
	//
	// public int getHeight() {
	// return height;
	// }
	// }
}
