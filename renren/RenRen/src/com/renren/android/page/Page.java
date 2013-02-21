package com.renren.android.page;

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
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.RequestListener;
import com.renren.android.ui.base.FlipperLayout.OnOpenListener;
import com.renren.android.ui.base.MyLetterListView;
import com.renren.android.ui.base.MyLetterListView.OnTouchingLetterChangedListener;
import com.renren.android.ui.base.PinnedHeaderListView;

public class Page {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private View mPage;

	private ImageView mFlip;
	private ImageView mMenu;
	private EditText mSearch;
	private ImageButton mClear;
	private PinnedHeaderListView mDisPlay;
	private MyLetterListView mMyLetterListView;

	private TextView mOverlay;
	private PageAdapter mAdapter;
	private OnOpenListener mOnOpenListener;
	private WindowManager mWindowManager;

	private OverlayThread mOverlayThread;

	public Page(BaseApplication application, Context context, Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
		mPage = LayoutInflater.from(context).inflate(R.layout.page, null);
		findViewById();
		setListener();
	}

	private void findViewById() {
		mFlip = (ImageView) mPage.findViewById(R.id.page_flip);
		mMenu = (ImageView) mPage.findViewById(R.id.page_menu);
		mSearch = (EditText) mPage.findViewById(R.id.page_search);
		mClear = (ImageButton) mPage.findViewById(R.id.page_searchclear);
		mDisPlay = (PinnedHeaderListView) mPage.findViewById(R.id.page_display);
		mMyLetterListView = (MyLetterListView) mPage
				.findViewById(R.id.page_myletterlistview);
	}

	private void setListener() {
		mFlip.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
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
						if (RenRenData.mPageIndexer.get(s) != null) {
							mDisPlay.setSelection(RenRenData.mPageIndexer
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
		getPage();
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

	private void getPage() {
		GetPageRequestParam param = new GetPageRequestParam(
				mApplication.mRenRen, String.valueOf(mApplication.mRenRen
						.getUserId()), null, "500");
		RequestListener<GetPageResponseBean> listener = new RequestListener<GetPageResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetPageResponseBean bean) {
				handler.sendEmptyMessage(0);
			}
		};
		mApplication.mAsyncRenRen.getPage(param, listener);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mAdapter = new PageAdapter(mApplication, mContext, mActivity);
			mDisPlay.setAdapter(mAdapter);
			mDisPlay.setOnScrollListener(mAdapter);
			mDisPlay.setPinnedHeaderView(LayoutInflater.from(mContext).inflate(
					R.layout.friends_list_section_header, mDisPlay, false));
		}

	};

	public View getView() {
		return mPage;
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}
}
