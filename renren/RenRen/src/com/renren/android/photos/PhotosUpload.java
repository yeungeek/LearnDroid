package com.renren.android.photos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.location.CurrentLocation;

public class PhotosUpload extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private ImageView mUpload;
	private EditText mContent;
	private ImageView mPicture;
	private Button mPlace;
	private ImageView mSperator;
	private ImageView mList;
	private TextView mCount;
	private ImageButton mVoice;
	private ImageButton mPoi;
	private ImageButton mImage;
	private ImageButton mAt;

	private LocationClient mClient;
	private LocationClientOption mOption;

	private boolean mLBSIsReceiver;
	private String mLBSAddress;
	private Drawable mPoi_off_icon;
	private Drawable mPoi_on_icon;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photosupload);
		mApplication = (BaseApplication) getApplication();
		initLBS();
		findViewById();
		setListener();
		String path = getIntent().getStringExtra("path");
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		if (bitmap != null) {
			mPicture.setImageBitmap(bitmap);
		} else {
			mPicture.setImageResource(R.drawable.v5_0_1_select_album_item_default_img);
		}
		mPoi_off_icon = getResources().getDrawable(
				R.drawable.v5_0_1_publisher_poi_icon);
		mPoi_off_icon.setBounds(0, 0, mPoi_off_icon.getMinimumWidth(),
				mPoi_off_icon.getMinimumHeight());
		mPoi_on_icon = getResources().getDrawable(
				R.drawable.v5_0_1_publisher_poi_active_icon);
		mPoi_on_icon.setBounds(0, 0, mPoi_on_icon.getMinimumWidth(),
				mPoi_on_icon.getMinimumHeight());
		mClient.start();
		mLBSIsReceiver = true;
		mClient.requestLocation();
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.photosupload_back);
		mUpload = (ImageView) findViewById(R.id.photosupload_upload);
		mContent = (EditText) findViewById(R.id.photosupload_content);
		mPicture = (ImageView) findViewById(R.id.photosupload_picture);
		mPlace = (Button) findViewById(R.id.photosupload_poi_place);
		mSperator = (ImageView) findViewById(R.id.photosupload_poi_sperator);
		mList = (ImageView) findViewById(R.id.photosupload_poi_list);
		mCount = (TextView) findViewById(R.id.photosupload_count);
		mVoice = (ImageButton) findViewById(R.id.photosupload_voice);
		mPoi = (ImageButton) findViewById(R.id.photosupload_poi);
		mImage = (ImageButton) findViewById(R.id.photosupload_image);
		mAt = (ImageButton) findViewById(R.id.photosupload_at);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mUpload.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PhotosUpload.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mContent.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {
				int number = s.length();
				mCount.setText(String.valueOf(number));
				selectionStart = mContent.getSelectionStart();
				selectionEnd = mCount.getSelectionEnd();
				if (temp.length() > 240) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					mContent.setText(s);
					mContent.setSelection(tempSelection);
				}
			}
		});
		mPlace.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mPlace.getText().toString().equals("添加地点")) {
					mLBSIsReceiver = true;
					mPlace.setText("正在定位...");
					mSperator.setVisibility(View.VISIBLE);
					mPoi.setImageResource(R.drawable.v5_0_1_publisher_poi_button_on);
					if (!mClient.isStarted()) {
						mClient.start();
					}
					mClient.requestLocation();
				} else if (mPlace.getText().toString().equals("正在定位...")) {
					if (mClient.isStarted()) {
						mClient.stop();
						mLBSIsReceiver = false;
						mLBSAddress = null;
						mPlace.setCompoundDrawables(mPoi_off_icon, null, null, null);
						mPlace.setText("添加地点");
						mSperator.setVisibility(View.INVISIBLE);
						mList.setVisibility(View.INVISIBLE);
						mPoi.setImageResource(R.drawable.v5_0_1_publisher_poi_button);
					}
				} else {
					startActivity(new Intent(PhotosUpload.this,
							CurrentLocation.class));
					overridePendingTransition(R.anim.roll_up, R.anim.roll);
				}
			}
		});
		mVoice.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PhotosUpload.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mPoi.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mLBSIsReceiver) {
					mLBSIsReceiver = false;
					mLBSAddress = null;
					mPlace.setCompoundDrawables(mPoi_off_icon, null, null, null);
					mPlace.setText("添加地点");
					mSperator.setVisibility(View.INVISIBLE);
					mList.setVisibility(View.INVISIBLE);
					mPoi.setImageResource(R.drawable.v5_0_1_publisher_poi_button);
				} else {
					mLBSIsReceiver = true;
					mPlace.setText("正在定位...");
					mSperator.setVisibility(View.VISIBLE);
					mPoi.setImageResource(R.drawable.v5_0_1_publisher_poi_button_on);
					if (!mClient.isStarted()) {
						mClient.start();
					}
					mClient.requestLocation();
				}
			}
		});
		mImage.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PhotosUpload.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mAt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PhotosUpload.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mClient.registerLocationListener(new BDLocationListener() {

			public void onReceivePoi(BDLocation arg0) {

			}

			public void onReceiveLocation(BDLocation arg0) {
				mLBSAddress = arg0.getAddrStr();
				mApplication.mLocation = arg0.getAddrStr();
				mApplication.mLatitude = arg0.getLatitude();
				mApplication.mLongitude = arg0.getLongitude();
				handler.sendEmptyMessage(0);
			}
		});
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mClient.isStarted()) {
					mClient.stop();
				}
				if (mLBSAddress != null) {
					mPlace.setText(mLBSAddress);
					mPlace.setCompoundDrawables(mPoi_on_icon, null, null, null);
					mList.setVisibility(View.VISIBLE);
					mSperator.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
	};

	private void initLBS() {
		mOption = new LocationClientOption();
		mOption.setOpenGps(true);
		mOption.setCoorType("bd09ll");
		mOption.setAddrType("all");
		mOption.setScanSpan(100);
		mOption.disableCache(true);
		mOption.setPoiNumber(20);
		mOption.setPoiDistance(1000);
		mOption.setPoiExtraInfo(true);
		mClient = new LocationClient(getApplicationContext(), mOption);
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
