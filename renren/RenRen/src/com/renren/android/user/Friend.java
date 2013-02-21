package com.renren.android.user;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RequestListener;
import com.renren.android.newsfeed.NewsFeedRequestParam;
import com.renren.android.newsfeed.NewsFeedResponseBean;
import com.renren.android.page.GetPageInfoRequestParam;
import com.renren.android.page.GetPageInfoResponseBean;
import com.renren.android.page.IsPageRequestParam;
import com.renren.android.page.IsPageResponseBean;
import com.renren.android.page.PageInfo;
import com.renren.android.ui.base.MenuPopAdapter;
import com.renren.android.ui.base.ModePopAdapter;

public class Friend extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private RelativeLayout mModeLayout;
	private TextView mModeText;
	private ImageView mMenu;
	private ListView mDisplay;
	private ProgressBar mBar;

	private View mHead;
	private ImageView mAvatar;
	private ImageView mUpdate;
	private TextView mName;
	private ImageView mStar;
	private Button mVip;
	private TextView mContent;
	private TextView mType;
	private TextView mCount;

	private PopupWindow mModePopupWindow;
	private View mModeView;
	private ListView mModeListView;

	private PopupWindow mMenuPopupWindow;
	private View mMenuView;
	private ListView mMenuListView;

	private int[] mUserModeIcon = {
			R.drawable.v5_0_1_profile_popupwindow_type_visitor_background,
			R.drawable.v5_0_1_profile_popupwindow_type_minifeed_background,
			R.drawable.v5_0_1_profile_popupwindow_type_info_background,
			R.drawable.v5_0_1_profile_popupwindow_type_album_background,
			R.drawable.v5_0_1_profile_popupwindow_type_status_background,
			R.drawable.v5_0_1_profile_popupwindow_type_blog_background,
			R.drawable.v5_0_1_profile_popupwindow_type_share_background };
	private String[] mUserModeName = { "最近来访", "新鲜事", "资料", "相册", "状态", "日志",
			"分享" };
	private String[] mUserMenu = { "私信", "返回顶部", "刷新" };

	private int[] mPageModeIcon = {
			R.drawable.v5_0_1_profile_popupwindow_type_minifeed_background,
			R.drawable.v5_0_1_profile_popupwindow_type_info_background,
			R.drawable.v5_0_1_profile_popupwindow_type_gossip_background,
			R.drawable.v5_0_1_profile_popupwindow_type_album_background,
			R.drawable.v5_0_1_profile_popupwindow_type_status_background,
			R.drawable.v5_0_1_profile_popupwindow_type_blog_background,
			R.drawable.v5_0_1_profile_popupwindow_type_share_background };
	private String[] mPageModeName = { "新鲜事", "资料", "留言板", "相册", "状态", "日志",
			"分享" };
	private String[] mPageMenu = { "留言", "返回顶部", "刷新" };

	public static final String NEWFEED = "10,11,20,21,22,23,30,31,32,33,36";
	public static final String SHARE = "21,32,33";
	private boolean mRefresh = false;
	private int mPage = 1;
	private boolean mIsAdd = false;
	private boolean mIsOver = false;
	private boolean mIsHaveData=false;
	private int mChooseId = 0;

	private UserInfo mUserInfo;
	private PageInfo mPageInfo;
	private int mUid;
	private int mIsPage;

	private NewsFeedAdapter mNewsFeedAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendinfo);
		mApplication = (BaseApplication) getApplication();
		mUserInfo = new UserInfo();
		mPageInfo = new PageInfo();

		mHead = LayoutInflater.from(this).inflate(R.layout.user_head, null);
		mModeView = LayoutInflater.from(this).inflate(
				R.layout.mode_popupwindow, null);
		mMenuView = LayoutInflater.from(this).inflate(
				R.layout.menu_popupwindow, null);

		mModePopupWindow = new PopupWindow(mModeView, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		mModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mModePopupWindow.setAnimationStyle(R.style.ModePopupAnimation);

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
		mBack = (ImageView) findViewById(R.id.friendinfo_back);
		mModeLayout = (RelativeLayout) findViewById(R.id.friendinfo_mode_layout);
		mModeText = (TextView) findViewById(R.id.friendinfo_mode_text);
		mMenu = (ImageView) findViewById(R.id.friendinfo_menu);
		mDisplay = (ListView) findViewById(R.id.friendinfo_display);
		mBar = (ProgressBar) findViewById(R.id.friendinfo_progressbar);

		mAvatar = (ImageView) mHead.findViewById(R.id.user_head_avatar);
		mUpdate = (ImageView) mHead
				.findViewById(R.id.user_head_headphoto_update);
		mName = (TextView) mHead.findViewById(R.id.user_head_name);
		mStar = (ImageView) mHead.findViewById(R.id.user_head_star);
		mVip = (Button) mHead.findViewById(R.id.user_head_vip);
		mContent = (TextView) mHead.findViewById(R.id.user_head_content);
		mType = (TextView) mHead.findViewById(R.id.user_head_type);
		mCount = (TextView) mHead.findViewById(R.id.user_head_count);

		mModeListView = (ListView) mModeView.findViewById(R.id.mode_pop_list);
		mMenuListView = (ListView) mMenuView.findViewById(R.id.menu_pop_list);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mModeLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				switch (mIsPage) {
				case 0:
					initModePopupWindow(mUserModeIcon, mUserModeName, mChooseId);
					break;

				case 1:
					initModePopupWindow(mPageModeIcon, mPageModeName, mChooseId);
					break;
				}
			}
		});
		mModeListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mChooseId = position;
				mModePopupWindow.dismiss();
				switch (mIsPage) {
				case 0:
					mModeText.setText(mUserModeName[position]);
					break;

				case 1:
					mModeText.setText(mPageModeName[position]);
					break;
				}
			}
		});
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				switch (mIsPage) {
				case 0:
					initMorePopupWindow(mUserMenu);
					break;

				case 1:
					initMorePopupWindow(mPageMenu);
					break;
				}
			}
		});
		mDisplay.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (view.getLastVisiblePosition() == view.getCount() - 1
						&& mRefresh == false && mIsOver == false&&mIsHaveData==true) {
					mRefresh = true;
					mDisplay.setSelection(mDisplay.getCount());
					mPage++;
					mIsAdd = true;
					mBar.setVisibility(View.VISIBLE);
					getNewsFeed(NEWFEED);
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	private void init() {
		mNewsFeedAdapter = new NewsFeedAdapter(mApplication, Friend.this,
				Friend.this);
		Intent intent = getIntent();
		mUid = intent.getIntExtra("uid", 0);
		isPage();
	}

	private void isPage() {
		IsPageRequestParam param = new IsPageRequestParam(mApplication.mRenRen,
				mUid);
		RequestListener<IsPageResponseBean> listener = new RequestListener<IsPageResponseBean>() {

			public void onStart() {

			}

			public void onComplete(IsPageResponseBean bean) {
				mIsPage = bean.mIspage;
				handler.sendEmptyMessage(0);
			}
		};
		mApplication.mAsyncRenRen.isPage(param, listener);
	}

	private void getInfo() {
		GetInfoRequestParam param = new GetInfoRequestParam(
				mApplication.mRenRen, String.valueOf(mUid));
		RequestListener<GetInfoResponseBean> listener = new RequestListener<GetInfoResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetInfoResponseBean bean) {
				bean.Resolve(mUserInfo);
				getProfileInfo();
			}
		};
		mApplication.mAsyncRenRen.getInfo(param, listener);
	}

	private void getProfileInfo() {
		GetProfileInfoRequestParam param = new GetProfileInfoRequestParam(
				mApplication.mRenRen, mUid);
		RequestListener<GetProfileInfoResponseBean> listener = new RequestListener<GetProfileInfoResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetProfileInfoResponseBean bean) {
				bean.Resolve(mUserInfo);
				handler.sendEmptyMessage(1);
			}
		};
		mApplication.mAsyncRenRen.getProfileInfo(param, listener);
	}

	private void getPageInfo() {
		GetPageInfoRequestParam param = new GetPageInfoRequestParam(
				mApplication.mRenRen, mUid);
		RequestListener<GetPageInfoResponseBean> listener = new RequestListener<GetPageInfoResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetPageInfoResponseBean bean) {
				bean.Resolve(mPageInfo);
				handler.sendEmptyMessage(1);
			}
		};
		mApplication.mAsyncRenRen.getPageInfo(param, listener);
	}

	private void getNewsFeed(String type) {
		NewsFeedRequestParam param;
		if (mIsPage == 0) {
			param = new NewsFeedRequestParam(mApplication.mRenRen, type,
					String.valueOf(mUid), null, String.valueOf(mPage), "30");
		} else {
			param = new NewsFeedRequestParam(mApplication.mRenRen, type, null,
					String.valueOf(mUid), String.valueOf(mPage), "30");
		}
		RequestListener<NewsFeedResponseBean> listener = new RequestListener<NewsFeedResponseBean>() {

			public void onStart() {

			}

			public void onComplete(NewsFeedResponseBean bean) {
				bean.Resolve(mIsAdd, false);
				mIsOver = bean.isOver;
				handler.sendEmptyMessage(2);
			}
		};
		mApplication.mAsyncRenRen.getNewsFeed(param, listener);
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				switch (mIsPage) {
				case 0:
					mChooseId = 1;
					getInfo();
					break;

				case 1:
					mChooseId = 0;
					getPageInfo();
					break;
				}
				break;

			case 1:
				getNewsFeed(NEWFEED);
				switch (mIsPage) {
				case 0:
					mName.setText(mUserInfo.getName());
					mContent.setText(mApplication.mText_Util.replace(mUserInfo
							.getContent()));
					String headurl = null;
					if (mUserInfo.getMainurl() == null
							|| mUserInfo.getMainurl().equals("")) {
						headurl = mUserInfo.getHeadurl();
					} else {
						headurl = mUserInfo.getMainurl();
					}
					mApplication.mHeadBitmap.display(mAvatar, headurl);
					if (mUserInfo.getStar() == 0) {
						mStar.setVisibility(View.GONE);
					} else {
						mStar.setVisibility(View.VISIBLE);
					}
					if (mUserInfo.getZidou() == 0) {
						mVip.setBackgroundResource(R.drawable.v5_0_1_newsfeed_vip_gray_bg);
					} else {
						mVip.setBackgroundResource(R.drawable.v5_0_1_newsfeed_vip_bg);
					}
					if (mUserInfo.getVip() == 0) {
						mVip.setText("VIP1");
					} else {
						mVip.setText("VIP" + mUserInfo.getVip());
					}

					break;
				case 1:
					mName.setText(mPageInfo.getName());
					mContent.setText(mApplication.mText_Util.replace(mPageInfo
							.getContent()));
					mApplication.mHeadBitmap.display(mAvatar,
							mPageInfo.getMainurl());
					mStar.setImageResource(R.drawable.v5_0_1_page_checked_icon);
					mVip.setVisibility(View.GONE);
					break;
				}
				mModeText.setText("新鲜事");
				mType.setText("新鲜事");
				mCount.setText("");
				mUpdate.setVisibility(View.GONE);
				mDisplay.addHeaderView(mHead);
				mDisplay.setAdapter(mNewsFeedAdapter);
				break;
			case 2:
				mBar.setVisibility(View.GONE);
				mNewsFeedAdapter.notifyDataSetChanged();

				if (!mRefresh) {
					mDisplay.setSelection(0);
				}
				mIsHaveData=true;
				mRefresh = false;
				mIsAdd = false;
				break;
			}
		}
	};

	private void initModePopupWindow(int[] modeIcon, String[] modeName,
			int chooseId) {
		ModePopAdapter adapter = new ModePopAdapter(this, modeIcon, modeName,
				chooseId);
		mModeListView.setAdapter(adapter);
		if (mModePopupWindow == null) {
			mModePopupWindow = new PopupWindow(mModeView,
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
			mModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mModePopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
		}
		if (mModePopupWindow.isShowing()) {
			mModePopupWindow.dismiss();
		} else {
			mModePopupWindow.showAsDropDown(mModeLayout, 0, 0);
		}
	}

	private void initMorePopupWindow(String[] menuName) {
		MenuPopAdapter adapter = new MenuPopAdapter(this, menuName);
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
