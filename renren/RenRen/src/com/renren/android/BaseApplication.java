package com.renren.android;

import net.tsz.afinal.FinalBitmap;
import android.app.Application;

import com.renren.android.user.UserInfo;
import com.renren.android.util.Text_Util;

public class BaseApplication extends Application {
	public String mLocation;
	public double mLongitude;
	public double mLatitude;
	public String mImagePath;
	public int mImageType = -1;
	public UserInfo mUserInfo = new UserInfo();
	public AsyncRenRen mAsyncRenRen = new AsyncRenRen();
	public Text_Util mText_Util = new Text_Util();
	public RenRen mRenRen = null;
	public FinalBitmap mHeadBitmap = null;
	public FinalBitmap mAlbumBitmap = null;
	public FinalBitmap mPhotoBitmap = null;
	public FinalBitmap mNearByBitmap = null;
	public static final String CACHE = "/sdcard/RenRenForAndroid/Cache/";

	public void onCreate() {
		super.onCreate();
		mRenRen = new RenRen(this);
		initHeadBitmap();
		initAlbumBitmap();
		initPhotoBitmap();
		initNearByBitmap();
	}

	private void initHeadBitmap() {
		mHeadBitmap = new FinalBitmap(this);
		mHeadBitmap.configBitmapLoadThreadSize(5);
		mHeadBitmap.configDiskCachePath(CACHE);
		mHeadBitmap.configLoadfailImage(R.drawable.v5_0_1_widget_default_head);
		mHeadBitmap.configLoadingImage(R.drawable.v5_0_1_widget_default_head);
		mHeadBitmap.configMemoryCacheSize(1);
		mHeadBitmap.init();
	}

	private void initAlbumBitmap() {
		mAlbumBitmap = new FinalBitmap(this);
		mAlbumBitmap.configBitmapLoadThreadSize(5);
		mAlbumBitmap.configDiskCachePath(CACHE);
		mAlbumBitmap
				.configLoadfailImage(R.drawable.v5_0_1_select_album_item_default_img);
		mAlbumBitmap
				.configLoadingImage(R.drawable.v5_0_1_select_album_item_default_img);
		mAlbumBitmap.configMemoryCacheSize(1);
		mAlbumBitmap.init();
	}

	private void initPhotoBitmap() {
		mPhotoBitmap = new FinalBitmap(this);
		mPhotoBitmap.configBitmapLoadThreadSize(5);
		mPhotoBitmap.configDiskCachePath(CACHE);
		mPhotoBitmap.configLoadfailImage(R.drawable.v5_0_1_photo_default_img);
		mPhotoBitmap.configLoadingImage(R.drawable.v5_0_1_photo_default_img);
		mPhotoBitmap.configMemoryCacheSize(1);
		mPhotoBitmap.init();
	}

	private void initNearByBitmap() {
		mNearByBitmap = new FinalBitmap(this);
		mNearByBitmap.configBitmapLoadThreadSize(5);
		mNearByBitmap.configDiskCachePath(CACHE);
		mNearByBitmap
				.configLoadfailImage(R.drawable.v5_0_1_nearby_activity_photo_bg);
		mNearByBitmap
				.configLoadingImage(R.drawable.v5_0_1_nearby_activity_photo_bg);
		mNearByBitmap.configMemoryCacheSize(1);
		mNearByBitmap.init();
	}
}
