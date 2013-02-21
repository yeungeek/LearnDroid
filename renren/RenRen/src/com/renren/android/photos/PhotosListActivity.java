package com.renren.android.photos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.RequestListener;
import com.renren.android.ui.base.MenuPopBlackAdapter;

public class PhotosListActivity extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private TextView mTitle;
	private ImageView mShare;
	private ImageView mMenu;
	private TextView mAlbumName;
	private GridView mGridView;
	private ProgressBar mBar;
	private ProgressBar mRefreshBar;
	private PhotosListAdapter mAdapter;

	private PopupWindow mMenuPopupWindow;
	private View mMenuView;
	private ListView mMenuListView;
	private String[] mMenuName = { "评论", "收藏", "查看源作者", "返回顶部" };

	private String mUserName;
	private int mUid;
	private long mAid;
	private int mVisable;
	private String mName;
	private int mCount;
	private boolean mRefresh = false;
	private int mPage = 1;
	private boolean mIsAdd = false;
	private boolean mIsOver = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoslist);
		mApplication = (BaseApplication) getApplication();
		mMenuView = LayoutInflater.from(this).inflate(
				R.layout.menu_popupwindow_black, null);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		mMenuPopupWindow = new PopupWindow(mMenuView, width / 2,
				LayoutParams.WRAP_CONTENT, true);
		mMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mMenuPopupWindow.setAnimationStyle(R.style.ModePopupAnimation);

		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.photoslist_back);
		mTitle = (TextView) findViewById(R.id.photoslist_title);
		mShare = (ImageView) findViewById(R.id.photoslist_share);
		mMenu = (ImageView) findViewById(R.id.photoslist_menu);
		mAlbumName = (TextView) findViewById(R.id.photoslist_albumname);
		mGridView = (GridView) findViewById(R.id.photoslist_gridview);
		mBar = (ProgressBar) findViewById(R.id.photolist_progressbar);
		mRefreshBar = (ProgressBar) findViewById(R.id.photolist_refreshbar);

		mMenuListView = (ListView) mMenuView
				.findViewById(R.id.menu_pop_black_list);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mShare.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mVisable != 99) {
					Toast.makeText(PhotosListActivity.this, "对非所有人可见的相册不能被分享",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(PhotosListActivity.this, "暂时无法提供此功能",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				initMorePopupWindow();
			}
		});
		mGridView.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (view.getLastVisiblePosition() == view.getCount() - 1
						&& mRefresh == false && mIsOver == false) {
					mRefresh = true;
					mGridView.setSelection(mGridView.getCount());
					mPage++;
					mIsAdd = true;
					mRefreshBar.setVisibility(View.VISIBLE);
					getPhotos();
				}
				if (scrollState == SCROLL_STATE_FLING
						|| scrollState == SCROLL_STATE_TOUCH_SCROLL) {
					mAlbumName.setVisibility(View.GONE);
				}
				if (scrollState == SCROLL_STATE_IDLE
						&& view.getFirstVisiblePosition() == 0) {
					mAlbumName.setVisibility(View.VISIBLE);
				}

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(PhotosListActivity.this,
						PhotosDetailActivity.class);
				intent.putExtra("count", mCount);
				intent.putExtra("albumName", mName);
				intent.putExtra("position", (int) id);
				startActivity(intent);
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		mMenuListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mMenuListView.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						mMenuPopupWindow.dismiss();
						switch (position) {
						case 0:
							Toast.makeText(PhotosListActivity.this, "评论",
									Toast.LENGTH_SHORT).show();
							break;

						case 1:
							Toast.makeText(PhotosListActivity.this, "收藏",
									Toast.LENGTH_SHORT).show();
							break;

						case 2:
							Toast.makeText(PhotosListActivity.this, "查看源作者",
									Toast.LENGTH_SHORT).show();
							break;

						case 3:
							Toast.makeText(PhotosListActivity.this, "返回顶部",
									Toast.LENGTH_SHORT).show();
							break;
						}
					}
				});
			}
		});
	}

	private void init() {
		mUserName = getIntent().getStringExtra("userName");
		mUid = getIntent().getIntExtra("uid", 0);
		mAid = getIntent().getLongExtra("aid", 0);
		mVisable = getIntent().getIntExtra("visable", 0);
		mName = getIntent().getStringExtra("name");
		mCount = getIntent().getIntExtra("count", 0);
		mTitle.setText(mUserName + "的相册");
		mAlbumName.setText(mName + "(" + mCount + ")");
		RenRenData.mPhotosResults.clear();
		mAdapter = new PhotosListAdapter(mApplication, this, this);
		mGridView.setAdapter(mAdapter);
		getPhotos();
	}

	private void getPhotos() {
		GetPhotosRequestParam param = new GetPhotosRequestParam(
				mApplication.mRenRen, mUid, mAid, 0, null,
				String.valueOf(mPage), "30");
		RequestListener<GetPhotosResponseBean> listener = new RequestListener<GetPhotosResponseBean>() {

			public void onStart() {
				if (!mRefresh) {
					handler.sendEmptyMessage(0);
				}
			}

			public void onComplete(GetPhotosResponseBean bean) {
				bean.Resolve(mIsAdd);
				mIsOver = bean.isOver;
				handler.sendEmptyMessage(1);
			}
		};
		mApplication.mAsyncRenRen.getPhotos(param, listener);
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mBar.setVisibility(View.VISIBLE);
				break;

			case 1:
				mRefresh = false;
				mIsAdd = false;
				mBar.setVisibility(View.GONE);
				mRefreshBar.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				break;
			}
		}
	};

	private void initMorePopupWindow() {
		MenuPopBlackAdapter adapter = new MenuPopBlackAdapter(this, mMenuName);
		mMenuListView.setAdapter(adapter);
		if (mMenuPopupWindow == null) {
			DisplayMetrics metric = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric);
			int width = metric.widthPixels;
			mMenuPopupWindow = new PopupWindow(mMenuView, width / 2,
					LayoutParams.WRAP_CONTENT, true);
			mMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mMenuPopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
		}
		if (mMenuPopupWindow.isShowing()) {
			mMenuPopupWindow.dismiss();
		} else {
			mMenuPopupWindow.showAsDropDown(mMenu, 0, 0);
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
