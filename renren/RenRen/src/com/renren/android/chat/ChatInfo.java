package com.renren.android.chat;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren.android.R;

public class ChatInfo extends Activity {
	private LinearLayout mLayout;
	private ImageView mBack;
	private TextView mTitle;
	private ImageView mMenu;
	private ListView mDisplay;
	private ImageButton mEmoticon;
	private EditText mContent;
	private ImageButton mSend;
	private Button mSpeak;

	private PopupWindow mPopupWindow;
	private View mPopupView;
	private RelativeLayout mOutSide;
	private LinearLayout mInSide;
	private LinearLayout mTalkMax;
	private LinearLayout mTalkMin;
	private TextView mTime;

	private int mScreenWidth;
	private int mScreenHeight;
	private int mPopLeft;
	private int mPopTop;
	private int mPopRight;
	private int mPopBottom;
	private int mRecordingTime;
	private boolean mIsOnTouch;
	private Timer mTimer;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatinfo);
		mPopupView = LayoutInflater.from(this).inflate(
				R.layout.chatinfo_popupwindow, null);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mPopupWindow = new PopupWindow(mPopupView, mScreenWidth / 3 * 2,
				mScreenWidth / 3 * 2);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		mLayout = (LinearLayout) findViewById(R.id.chatinfo_layout);
		mBack = (ImageView) findViewById(R.id.chatinfo_back);
		mTitle = (TextView) findViewById(R.id.chatinfo_title);
		mMenu = (ImageView) findViewById(R.id.chatinfo_menu);
		mDisplay = (ListView) findViewById(R.id.chatinfo_display);
		mEmoticon = (ImageButton) findViewById(R.id.chatinfo_emoticon);
		mContent = (EditText) findViewById(R.id.chatinfo_content);
		mSend = (ImageButton) findViewById(R.id.chatinfo_send);
		mSpeak = (Button) findViewById(R.id.chatinfo_speak);

		mOutSide = (RelativeLayout) mPopupView
				.findViewById(R.id.chatinfo_pop_outside);
		mInSide = (LinearLayout) mPopupView
				.findViewById(R.id.chatinfo_pop_inside);
		mTalkMax = (LinearLayout) mPopupView
				.findViewById(R.id.chatinfo_pop_talkmax);
		mTalkMin = (LinearLayout) mPopupView
				.findViewById(R.id.chatinfo_pop_talkmin);
		mTime = (TextView) mPopupView
				.findViewById(R.id.chatinfo_pop_outside_time);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
		mDisplay.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				return true;
			}
		});
		mEmoticon.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
		mSend.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
		mSpeak.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (!mIsOnTouch) {
						mIsOnTouch = true;
						mSpeak.setBackgroundResource(R.drawable.v5_0_1_chat_speak_end_btn_bg);
						mPopupWindow.showAtLocation(mLayout, Gravity.CENTER, 0,
								0);
						mPopLeft = mScreenWidth / 2 - mPopupWindow.getWidth()
								/ 2;
						mPopRight = mPopLeft + mPopupWindow.getWidth();
						mPopTop = mScreenHeight / 2 - mPopupWindow.getWidth()
								/ 2;
						mPopBottom = mPopTop + mPopupWindow.getHeight();
						mTimer = new Timer();
						TimerTask task = new TimerTask() {

							public void run() {
								mRecordingTime++;
								if (mRecordingTime < 60) {
									handler.sendEmptyMessage(0);
								} else {
									handler.sendEmptyMessage(1);
									mTimer.cancel();
								}
							}
						};
						mTimer.schedule(task, 1, 1000);
					}

					break;

				case MotionEvent.ACTION_MOVE:
					if (isInPopupWindow(event.getRawX(), event.getRawY())) {
						mInSide.setVisibility(View.VISIBLE);
						mOutSide.setVisibility(View.GONE);
					} else {
						mInSide.setVisibility(View.GONE);
						mOutSide.setVisibility(View.VISIBLE);
					}
					break;

				case MotionEvent.ACTION_UP:
					mIsOnTouch = false;
					mTimer.cancel();
					mSpeak.setBackgroundResource(R.drawable.v5_0_1_chat_speak_start_btn_bg);
					if (mRecordingTime < 3) {
						mOutSide.setVisibility(View.GONE);
						mInSide.setVisibility(View.GONE);
						mTalkMin.setVisibility(View.VISIBLE);
						handler.sendEmptyMessageDelayed(2, 500);
					} else {
						handler.sendEmptyMessage(2);
					}

					break;
				}
				return true;
			}
		});
	}

	private boolean isInPopupWindow(float x, float y) {
		if (x >= mPopLeft && x <= mPopRight && y >= mPopTop && y < mPopBottom) {
			return true;
		}
		return false;
	}

	private void init() {
		String name = getIntent().getStringExtra("name");
		mTitle.setText(name);
		mContent.setText("");
		mOutSide.setVisibility(View.VISIBLE);
		mInSide.setVisibility(View.GONE);
		mTalkMax.setVisibility(View.GONE);
		mTalkMin.setVisibility(View.GONE);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mTime.setText(mRecordingTime + "\"");
				break;

			case 1:
				mInSide.setVisibility(View.GONE);
				mOutSide.setVisibility(View.GONE);
				mTalkMax.setVisibility(View.VISIBLE);
				break;

			case 2:
				mPopupWindow.dismiss();
				mOutSide.setVisibility(View.VISIBLE);
				mInSide.setVisibility(View.GONE);
				mTalkMax.setVisibility(View.GONE);
				mTalkMin.setVisibility(View.GONE);
				mRecordingTime = 0;
				mTime.setText(mRecordingTime + "\"");
				break;
			}
		}

	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
