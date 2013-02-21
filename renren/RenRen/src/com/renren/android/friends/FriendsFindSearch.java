package com.renren.android.friends;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.RequestListener;

public class FriendsFindSearch extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private ListView mDisplay;
	private LinearLayout mNoData;
	private ProgressBar mBar;
	private FriendsFindSearchAdapter mAdapter;

	private static final String NUMBER_FORMAT = "^\\d*$";
	private String mSeatchInfo;
	private boolean mRefresh = false;
	private int mPage = 1;
	private boolean mIsAdd = false;
	private boolean mIsOver = false;
	private boolean mIsHaveData = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendsfindsearch);
		mApplication = (BaseApplication) getApplication();
		findViewById();
		setListener();
		mSeatchInfo = getIntent().getStringExtra("searchinfo");
		mAdapter = new FriendsFindSearchAdapter(mApplication,
				FriendsFindSearch.this, FriendsFindSearch.this);
		mDisplay.setAdapter(mAdapter);
		friendsFind();
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.friendsfindsearch_back);
		mDisplay = (ListView) findViewById(R.id.friendsfindsearch_display);
		mNoData = (LinearLayout) findViewById(R.id.friendsfindsearch_nodata);
		mBar = (ProgressBar) findViewById(R.id.friendsfindsearch_progressbar);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mDisplay.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (view.getLastVisiblePosition() == view.getCount() - 1
						&& mRefresh == false && mIsOver == false
						&& mIsHaveData == true) {
					mRefresh = true;
					mDisplay.setSelection(mDisplay.getCount());
					mPage++;
					mIsAdd = true;
					mBar.setVisibility(View.VISIBLE);
					friendsFind();
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	private void friendsFind() {
		FriendsFindRequestParam param;
		if (mSeatchInfo.matches(NUMBER_FORMAT)) {
			param = new FriendsFindRequestParam(mApplication.mRenRen, null,
					mSeatchInfo, String.valueOf(mPage), "20");
		} else {
			param = new FriendsFindRequestParam(mApplication.mRenRen,
					mSeatchInfo, null, String.valueOf(mPage), "20");
		}
		RequestListener<FriendsFindResponseBean> listener = new RequestListener<FriendsFindResponseBean>() {

			public void onStart() {

			}

			public void onComplete(FriendsFindResponseBean bean) {
				mIsOver = bean.isOver;
				bean.Resolve(mIsAdd);
				handler.sendEmptyMessage(0);
			}
		};
		mApplication.mAsyncRenRen.findFriends(param, listener);
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mRefresh = false;
			mIsAdd = false;
			mIsHaveData = true;
			mBar.setVisibility(View.GONE);
			if (RenRenData.mFriendsFindResults.size() == 0) {
				mDisplay.setVisibility(View.GONE);
				mNoData.setVisibility(View.VISIBLE);
			} else {
				mAdapter.notifyDataSetChanged();
				mDisplay.setVisibility(View.VISIBLE);
				mNoData.setVisibility(View.GONE);
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
