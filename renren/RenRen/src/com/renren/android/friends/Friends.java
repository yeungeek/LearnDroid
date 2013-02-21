package com.renren.android.friends;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.RequestListener;
import com.renren.android.ui.base.FlipperLayout.OnOpenListener;
import com.renren.android.ui.base.MyLetterListView;
import com.renren.android.ui.base.MyLetterListView.OnTouchingLetterChangedListener;
import com.renren.android.ui.base.PinnedHeaderListView;

public class Friends {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private View mFriends;

	private ImageView mFlip;
	private RelativeLayout mModeLayout;
	private TextView mModeText;
	private ImageView mMenu;
	private EditText mSearch;
	private ImageButton mClear;
	private PinnedHeaderListView mDisPlay;
	private MyLetterListView mMyLetterListView;

	private TextView mOverlay;
	private FriendsAdapter mAdapter;
	private OnOpenListener mOnOpenListener;
	private WindowManager mWindowManager;
	private OverlayThread mOverlayThread;

	public Friends(BaseApplication application, Context context,
			Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
		mFriends = LayoutInflater.from(context).inflate(R.layout.friends, null);
		findViewById();
		setListener();
	}

	private void findViewById() {
		mFlip = (ImageView) mFriends.findViewById(R.id.friends_flip);
		mModeLayout = (RelativeLayout) mFriends
				.findViewById(R.id.friends_mode_layout);
		mModeText = (TextView) mFriends.findViewById(R.id.friends_mode_text);
		mMenu = (ImageView) mFriends.findViewById(R.id.friends_menu);
		mSearch = (EditText) mFriends.findViewById(R.id.friends_search);
		mClear = (ImageButton) mFriends.findViewById(R.id.friends_searchclear);
		mDisPlay = (PinnedHeaderListView) mFriends
				.findViewById(R.id.friends_display);
		mMyLetterListView = (MyLetterListView) mFriends
				.findViewById(R.id.friends_myletterlistview);
	}

	private void setListener() {
		mFlip.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
			}
		});
		mModeLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mModeText.setText("È«²¿ºÃÓÑ");
			}
		});
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
		mSearch.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (mSearch.getText().length() > 0) {
					mClear.setVisibility(View.VISIBLE);
				} else {
					mClear.setVisibility(View.INVISIBLE);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (mSearch.getText().length() > 0) {
					mClear.setVisibility(View.VISIBLE);
				} else {
					mClear.setVisibility(View.INVISIBLE);
				}
			}

			public void afterTextChanged(Editable s) {

			}
		});
		mClear.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mSearch.setText("");
			}
		});
		mMyLetterListView
				.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

					public void onTouchingLetterChanged(String s) {
						if (RenRenData.mFriendsIndexer.get(s) != null) {
							mDisPlay.setSelection(RenRenData.mFriendsIndexer
									.get(s));
						}
						mOverlay.setText(s);
						mOverlay.setVisibility(View.VISIBLE);
						handler.removeCallbacks(mOverlayThread);
						handler.postDelayed(mOverlayThread, 800);
					}
				});
	}

	public void init() {
		getFriends();
		initOverlay();
	}

	private void initOverlay() {
		mOverlayThread = new OverlayThread();
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mOverlay = (TextView) inflater.inflate(R.layout.friends_overlay, null);
		mOverlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(mOverlay, lp);
	}

	private class OverlayThread implements Runnable {

		public void run() {
			mOverlay.setVisibility(View.GONE);
		}
	}

	private void getFriends() {
		GetFriendsRequestParam param = new GetFriendsRequestParam(
				mApplication.mRenRen, null, null);
		RequestListener<GetFriendsResponseBean> listener = new RequestListener<GetFriendsResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetFriendsResponseBean bean) {
				handler.sendEmptyMessage(0);
			}
		};
		mApplication.mAsyncRenRen.getFriends(param, listener);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mAdapter = new FriendsAdapter(mApplication, mContext, mActivity);
			mDisPlay.setAdapter(mAdapter);
			mDisPlay.setOnScrollListener(mAdapter);
			mDisPlay.setPinnedHeaderView(LayoutInflater.from(mContext).inflate(
					R.layout.friends_list_section_header, mDisPlay, false));
		}

	};

	public View getView() {
		return mFriends;
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}
}
