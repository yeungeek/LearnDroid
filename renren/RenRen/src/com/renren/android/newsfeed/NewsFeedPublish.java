package com.renren.android.newsfeed;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import com.renren.android.RenRenData;
import com.renren.android.RequestListener;
import com.renren.android.emoticons.EmoticonsAdapter;
import com.renren.android.location.CurrentLocation;
import com.renren.android.util.Text_Util;

public class NewsFeedPublish extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private ImageView mPublish;
	private EditText mContent;
	private Button mPlace;
	private ImageView mSperator;
	private ImageView mList;
	private TextView mCount;
	private ImageButton mVoice;
	private ImageButton mPoi;
	private ImageButton mImage;
	private ImageButton mAt;
	private ImageButton mEmoticon;
	private GridView mEmoticons;
	private EmoticonsAdapter mAdapter;

	private LocationClient mClient;
	private LocationClientOption mOption;

	private boolean mLBSIsReceiver;
	private String mLBSAddress;
	private Drawable mPoi_off_icon;
	private Drawable mPoi_on_icon;

	private ProgressDialog mPublishDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsfeedpublish);
		mApplication = (BaseApplication) getApplication();
		initLBS();
		findViewById();
		setListener();

		mPoi_off_icon = getResources().getDrawable(
				R.drawable.v5_0_1_publisher_poi_icon);
		mPoi_off_icon.setBounds(0, 0, mPoi_off_icon.getMinimumWidth(),
				mPoi_off_icon.getMinimumHeight());
		mPoi_on_icon = getResources().getDrawable(
				R.drawable.v5_0_1_publisher_poi_active_icon);
		mPoi_on_icon.setBounds(0, 0, mPoi_on_icon.getMinimumWidth(),
				mPoi_on_icon.getMinimumHeight());

		mAdapter = new EmoticonsAdapter(this);
		mEmoticons.setAdapter(mAdapter);

		mClient.start();
		mLBSIsReceiver = true;
		mClient.requestLocation();
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.newsfeedpublish_back);
		mPublish = (ImageView) findViewById(R.id.newsfeedpublish_publish);
		mContent = (EditText) findViewById(R.id.newsfeedpublish_content);
		mPlace = (Button) findViewById(R.id.newsfeedpublish_poi_place);
		mSperator = (ImageView) findViewById(R.id.newsfeedpublish_poi_sperator);
		mList = (ImageView) findViewById(R.id.newsfeedpublish_poi_list);
		mCount = (TextView) findViewById(R.id.newsfeedpublish_count);
		mVoice = (ImageButton) findViewById(R.id.newsfeedpublish_voice);
		mPoi = (ImageButton) findViewById(R.id.newsfeedpublish_poi);
		mImage = (ImageButton) findViewById(R.id.newsfeedpublish_image);
		mAt = (ImageButton) findViewById(R.id.newsfeedpublish_at);
		mEmoticon = (ImageButton) findViewById(R.id.newsfeedpublish_emoticon);
		mEmoticons = (GridView) findViewById(R.id.newsfeedpublish_emoticons);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mContent.getText().toString().trim().length() > 0) {
					backDialog();
				} else {
					finish();
					overridePendingTransition(0, R.anim.roll_down);
				}
			}
		});
		mPublish.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mContent.getText().toString().trim().length() == 0) {
					Toast.makeText(NewsFeedPublish.this, "您还未输入内容,请输入后重试",
							Toast.LENGTH_SHORT).show();
				} else {
					publishNewsFeed(mContent.getText().toString().trim());
				}
			}
		});
		mContent.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mEmoticons.isShown()) {
					mEmoticons.setVisibility(View.GONE);
					mEmoticon
							.setImageResource(R.drawable.v5_0_1_publisher_emotion_button);
				}
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
				if (temp.length() > 140) {
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
					startActivity(new Intent(NewsFeedPublish.this,
							CurrentLocation.class));
					overridePendingTransition(R.anim.roll_up, R.anim.roll);
				}
			}
		});
		mVoice.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(NewsFeedPublish.this, "暂时无法提供此功能",
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
				Toast.makeText(NewsFeedPublish.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mAt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(NewsFeedPublish.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mEmoticon.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mEmoticons.isShown()) {
					mEmoticons.setVisibility(View.GONE);
					mEmoticon
							.setImageResource(R.drawable.v5_0_1_publisher_emotion_button);
				} else {
					mEmoticons.setVisibility(View.VISIBLE);
					mEmoticon
							.setImageResource(R.drawable.v5_0_1_publisher_pad_button);
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(NewsFeedPublish.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});
		mEmoticons.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mContent.getText().length()
						+ RenRenData.mEmoticonsResults.get(position)
								.getEmotion().length() <= 140) {
					mContent.setText(new Text_Util().replace(mContent.getText()
							.toString()
							+ RenRenData.mEmoticonsResults.get(position)
									.getEmotion()));
				}
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
				handler.sendEmptyMessage(2);
			}
		});
	}

	private void initLBS() {
		mOption = new LocationClientOption();
		mOption.setOpenGps(true);
		mOption.setCoorType("bd09ll");
		mOption.setAddrType("all");
		mOption.setScanSpan(100);
		// mOption.disableCache(true);
		// mOption.setPoiNumber(20);
		// mOption.setPoiDistance(1000);
		// mOption.setPoiExtraInfo(true);
		mClient = new LocationClient(getApplicationContext(), mOption);
	}

	private void publishNewsFeed(String status) {
		NewsFeedPublishRequestParam param = new NewsFeedPublishRequestParam(
				mApplication.mRenRen, status);
		RequestListener<NewsFeedPublishResponseBean> listener = new RequestListener<NewsFeedPublishResponseBean>() {

			public void onStart() {
				handler.sendEmptyMessage(0);
			}

			public void onComplete(NewsFeedPublishResponseBean bean) {
				Message msg = handler.obtainMessage(1, bean.code);
				handler.sendMessage(msg);
			}
		};
		mApplication.mAsyncRenRen.publishNewsFeed(param, listener);
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				publishDialogShow();
				break;
			case 1:
				publishDialogDismiss();
				switch (Integer.parseInt(msg.obj.toString())) {
				case 1:
					mContent.setText("");
					Toast.makeText(NewsFeedPublish.this, "发布成功",
							Toast.LENGTH_SHORT).show();
					finish();
					overridePendingTransition(0, R.anim.roll_down);
					break;

				case 10400:
					Toast.makeText(NewsFeedPublish.this, "状态更新过于频繁",
							Toast.LENGTH_SHORT).show();
					break;
				case 10401:
					Toast.makeText(NewsFeedPublish.this, "状态字数超过限定长度",
							Toast.LENGTH_SHORT).show();
					break;

				case 10402:
					Toast.makeText(NewsFeedPublish.this, "状态的内容含有非法字符",
							Toast.LENGTH_SHORT).show();
					break;
				}
				break;

			case 2:
				// if (mClient.isStarted()) {
				// mClient.stop();
				// }
				if (mLBSAddress != null) {
					mPlace.setText(mLBSAddress);
					mPlace.setCompoundDrawables(mPoi_on_icon, null, null, null);
					mList.setVisibility(View.VISIBLE);
					mSperator.setVisibility(View.VISIBLE);
				}
				break;

			default:
				break;
			}
		}
	};

	private void backDialog() {
		AlertDialog.Builder builder = new Builder(NewsFeedPublish.this);
		builder.setTitle("提示");
		builder.setMessage("是否取消发布?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	private void publishDialogShow() {
		if (mPublishDialog == null) {
			mPublishDialog = new ProgressDialog(NewsFeedPublish.this);
			mPublishDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mPublishDialog.setMessage("正在发布");
		}
		mPublishDialog.show();
	}

	private void publishDialogDismiss() {
		if (mPublishDialog != null && mPublishDialog.isShowing()) {
			mPublishDialog.dismiss();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mContent.getText().toString().trim().length() > 0) {
				backDialog();
			} else {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
