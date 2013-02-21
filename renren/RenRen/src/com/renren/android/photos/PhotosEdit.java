package com.renren.android.photos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;

public class PhotosEdit extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private Button mFinish;
	private LinearLayout mChooseAlbum;
	private TextView mAlbumName;
	private TextView mQualityHigh;
	private TextView mQualityNormal;
	private TextView mFileSize;
	private ImageView mImage;
	private ImageView mLeftRotate;
	private ImageView mRightRotate;
	private int mImageType;
	private float mScreenWidth;
	private float mScreenHeight;
	private Bitmap mBitmap_High;
	private Bitmap mBitmap_Normal;
	private String mImagePath_High;
	private String mImagePath_Normal;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photosedit);
		mApplication = (BaseApplication) getApplication();
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.photosedit_back);
		mFinish = (Button) findViewById(R.id.photosedit_finish);
		mChooseAlbum = (LinearLayout) findViewById(R.id.photosedit_choosealbum);
		mAlbumName = (TextView) findViewById(R.id.photosedit_albumname);
		mQualityHigh = (TextView) findViewById(R.id.photosedit_quality_high);
		mQualityNormal = (TextView) findViewById(R.id.photosedit_quality_normal);
		mFileSize = (TextView) findViewById(R.id.photosedit_filesize);
		mImage = (ImageView) findViewById(R.id.photosedit_img);
		mLeftRotate = (ImageView) findViewById(R.id.photosedit_left_rotate);
		mRightRotate = (ImageView) findViewById(R.id.photosedit_right_rotate);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mFinish.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PhotosEdit.this, PhotosUpload.class);
				if (mQualityHigh.isSelected()) {
					saveHighBitmap();
					intent.putExtra("path", mImagePath_High);
				} else if (mQualityNormal.isSelected()) {
					saveNormalBitmap();
					intent.putExtra("path", mImagePath_Normal);
				}
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		mChooseAlbum.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startActivityForResult((new Intent(PhotosEdit.this,
						PhotosEditChooseAlbum.class)), 0);
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		mQualityHigh.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!mQualityHigh.isSelected()) {
					mQualityHigh.setSelected(true);
					mQualityNormal.setSelected(false);
					if (mBitmap_High != null) {
						mImage.setImageBitmap(mBitmap_High);
						mFileSize.setText("文件大小 : "
								+ getFileSize(mImagePath_High));
					}
				}
			}
		});
		mQualityNormal.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!mQualityNormal.isSelected()) {
					mQualityHigh.setSelected(false);
					mQualityNormal.setSelected(true);
					if (mBitmap_Normal != null) {
						mImage.setImageBitmap(mBitmap_Normal);
						mFileSize.setText("文件大小 : "
								+ getFileSize(mImagePath_Normal));
					}
				}
			}
		});
		mLeftRotate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				rotateImg(-90);
			}
		});
		mRightRotate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				rotateImg(90);
			}
		});
	}

	private void init() {
		mAlbumName.setText("应用相册");
		mQualityHigh.setSelected(true);
		mImagePath_High = mApplication.mImagePath;
		mImageType = mApplication.mImageType;
		mApplication.mImagePath = null;
		mApplication.mImageType = -1;
		if (mImageType == 0) {
			Options options = new Options();
			options.inSampleSize = 5;
			mBitmap_High = BitmapFactory.decodeFile(mImagePath_High, options);
		} else if (mImageType == 1) {
			mBitmap_High = BitmapFactory.decodeFile(mImagePath_High);
		}

		if (mBitmap_High != null) {
			mImagePath_High = null;
			saveHighBitmap();
			mImage.setImageBitmap(mBitmap_High);
			mFileSize.setText("文件大小 : " + getFileSize(mImagePath_High));
			getNormalBitmap();
			saveNormalBitmap();
		}

	}

	private String getFileSize(String path) {
		File file = new File(path);
		FileInputStream fis = null;

		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				return formatFileSize(fis.available());
			} catch (Exception e) {
				return "未知大小";
			}
		}
		return "未知大小";
	}

	private String formatFileSize(long size) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "未知大小";
		if (size < 1024) {
			fileSizeString = df.format((double) size) + "B";
		} else if (size < 1048576) {
			fileSizeString = df.format((double) size / 1024) + "K";
		} else if (size < 1073741824) {
			fileSizeString = df.format((double) size / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) size / 1073741824) + "G";
		}
		return fileSizeString;
	}

	private void getNormalBitmap() {
		float scaleWidth = (mScreenWidth) / (mBitmap_High.getWidth() * 5);
		float scaleHeight = (mScreenHeight) / (mBitmap_High.getHeight() * 5);
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		mBitmap_Normal = Bitmap
				.createBitmap(mBitmap_High, 0, 0, mBitmap_High.getWidth(),
						mBitmap_High.getHeight(), matrix, true);
		mBitmap_Normal = Bitmap.createScaledBitmap(mBitmap_Normal,
				mBitmap_High.getWidth(), mBitmap_High.getHeight(), true);
	}

	private void saveNormalBitmap() {
		File dir = new File("/sdcard/RenRenForAndroid/Camera/");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (mImagePath_Normal == null) {
			mImagePath_Normal = "/sdcard/RenRenForAndroid/Camera/"
					+ UUID.randomUUID().toString() + ".jpg";
		}
		File file = new File(mImagePath_Normal);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {

			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			mBitmap_Normal.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}
	}

	private void saveHighBitmap() {
		File dir = new File("/sdcard/RenRenForAndroid/Camera/");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (mImagePath_High == null) {
			mImagePath_High = "/sdcard/RenRenForAndroid/Camera/"
					+ UUID.randomUUID().toString() + ".jpg";
		}
		File file = new File(mImagePath_High);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {

			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			mBitmap_High.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}
	}

	private void rotateImg(int arg0) {
		Matrix matrix = new Matrix();
		matrix.postRotate(arg0);
		mBitmap_High = Bitmap
				.createBitmap(mBitmap_High, 0, 0, mBitmap_High.getWidth(),
						mBitmap_High.getHeight(), matrix, true);
		mBitmap_Normal = Bitmap.createBitmap(mBitmap_Normal, 0, 0,
				mBitmap_Normal.getWidth(), mBitmap_Normal.getHeight(), matrix,
				true);
		if (mQualityHigh.isSelected()) {
			mImage.setImageBitmap(mBitmap_High);
		}
		if (mQualityNormal.isSelected()) {
			mImage.setImageBitmap(mBitmap_Normal);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 0:

			break;

		case 1:
			mAlbumName.setText(data.getStringExtra("name"));
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
