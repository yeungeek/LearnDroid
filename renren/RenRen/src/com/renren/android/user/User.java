package com.renren.android.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
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
import android.widget.Toast;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.RequestListener;
import com.renren.android.blog.BlogPublish;
import com.renren.android.blog.GetBlogsRequestParam;
import com.renren.android.blog.GetBlogsResponseBean;
import com.renren.android.newsfeed.NewsFeedPublish;
import com.renren.android.newsfeed.NewsFeedRequestParam;
import com.renren.android.newsfeed.NewsFeedResponseBean;
import com.renren.android.photos.GetAlbumsRequestParam;
import com.renren.android.photos.GetAlbumsResponseBean;
import com.renren.android.ui.base.FlipperLayout.OnOpenListener;
import com.renren.android.ui.base.LoadingDialog;
import com.renren.android.ui.base.MenuPopAdapter;
import com.renren.android.ui.base.ModePopAdapter;
import com.renren.android.util.Text_Util;

public class User {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private View mUser;

	private ImageView mFlip;
	private RelativeLayout mModeLayout;
	private TextView mModeText;
	private ImageView mSpare;
	private ImageView mMenu;
	private ListView mDisplay;
	private ProgressBar mBar;

	private View mHead;
	private ImageView mAvatar;
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

	private LoadingDialog mDialog;
	private OnOpenListener mOnOpenListener;
	private VisitorAdapter mVisitorAdapter;
	private NewsFeedAdapter mNewsFeedAdapter;
	private InfoAdapter mInfoAdapter;
	private AlbumsAdapter mAlbumsAdapter;
	private StatusAdapter mStatusAdapter;
	private BlogAdapter mBlogAdapter;
	private CollectionAdapter mCollectionAdapter;

	private int[] mModeIcon = {
			R.drawable.v5_0_1_profile_popupwindow_type_visitor_background,
			R.drawable.v5_0_1_profile_popupwindow_type_minifeed_background,
			R.drawable.v5_0_1_profile_popupwindow_type_info_background,
			R.drawable.v5_0_1_profile_popupwindow_type_album_background,
			R.drawable.v5_0_1_profile_popupwindow_type_status_background,
			R.drawable.v5_0_1_profile_popupwindow_type_blog_background,
			R.drawable.v5_0_1_profile_popupwindow_type_share_background,
			R.drawable.v5_0_1_profile_popupwindow_type_collection_background };
	private String[] mModeName = { "最近来访", "新鲜事", "资料", "相册", "状态", "日志", "分享",
			"收藏" };
	private String[] mMenuName = { "发状态", "传照片", "报道", "写日志", "返回顶部", "刷新" };
	public static final String NEWFEED = "10,11,20,21,22,23,30,31,32,33,36";
	public static final String SHARE = "21,32,33";
	private boolean mRefresh = false;
	private int mPage = 1;
	private boolean mIsAdd = false;
	private boolean mIsOver = false;
	private int mChooseId = 0;

	public User(BaseApplication application, Context context, Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
		mDialog = new LoadingDialog(context, R.style.dialog);
		mUser = LayoutInflater.from(context).inflate(R.layout.user, null);
		mHead = LayoutInflater.from(context).inflate(R.layout.user_head, null);
		mModeView = LayoutInflater.from(context).inflate(
				R.layout.mode_popupwindow, null);
		mMenuView = LayoutInflater.from(context).inflate(
				R.layout.menu_popupwindow, null);

		mModePopupWindow = new PopupWindow(mModeView, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		mModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mModePopupWindow.setAnimationStyle(R.style.ModePopupAnimation);

		DisplayMetrics metric = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		mMenuPopupWindow = new PopupWindow(mMenuView, width / 2,
				LayoutParams.WRAP_CONTENT, true);
		mMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mMenuPopupWindow.setAnimationStyle(R.style.ModePopupAnimation);

		findViewById();
		setListener();
		mDisplay.addHeaderView(mHead);
		mVisitorAdapter = new VisitorAdapter(mApplication, mContext, mActivity);
		mNewsFeedAdapter = new NewsFeedAdapter(mApplication, mContext,
				mActivity);
		mAlbumsAdapter = new AlbumsAdapter(
				mApplication.mRenRen.getUserHeadUrl(),
				mApplication.mRenRen.getUserName(), mApplication, mContext,
				mActivity);
		mStatusAdapter = new StatusAdapter(
				mApplication.mRenRen.getUserHeadUrl(),
				mApplication.mRenRen.getUserName(), mApplication, mContext,
				mActivity);
		mBlogAdapter = new BlogAdapter(mApplication.mRenRen.getUserHeadUrl(),
				mApplication.mRenRen.getUserName(), mApplication, mContext,
				mActivity);
		mCollectionAdapter = new CollectionAdapter(mContext);
	}

	private void findViewById() {
		mFlip = (ImageView) mUser.findViewById(R.id.user_flip);
		mModeLayout = (RelativeLayout) mUser
				.findViewById(R.id.user_mode_layout);
		mModeText = (TextView) mUser.findViewById(R.id.user_mode_text);
		mSpare = (ImageView) mUser.findViewById(R.id.user_spare);
		mMenu = (ImageView) mUser.findViewById(R.id.user_menu);
		mDisplay = (ListView) mUser.findViewById(R.id.user_display);
		mBar = (ProgressBar) mUser.findViewById(R.id.user_progressbar);

		mAvatar = (ImageView) mHead.findViewById(R.id.user_head_avatar);
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
		mFlip.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
			}
		});
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				initMorePopupWindow();
			}
		});
		mSpare.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				switch (mChooseId) {
				case 3:
					dialog("上传照片");
					break;

				case 4:
					mContext.startActivity(new Intent(mContext,
							NewsFeedPublish.class));
					mActivity.overridePendingTransition(R.anim.roll_up,
							R.anim.roll);
					break;

				case 5:
					mContext.startActivity(new Intent(mContext,
							BlogPublish.class));
					mActivity.overridePendingTransition(R.anim.roll_up,
							R.anim.roll);
					break;
				}
			}
		});
		mAvatar.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog("修改头像");
			}
		});
		mModeLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				initModePopupWindow(mChooseId);
			}
		});
		mDisplay.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (view.getLastVisiblePosition() == view.getCount() - 1
						&& mRefresh == false && mIsOver == false) {
					if (mChooseId != 2) {
						mRefresh = true;
						mDisplay.setSelection(mDisplay.getCount());
						mPage++;
						mIsAdd = true;
						mBar.setVisibility(View.VISIBLE);
					}
					switch (mChooseId) {
					case 0:
						getVisitor();
						break;

					case 1:
						getNewsFeed(NEWFEED);
						break;

					case 2:

						break;

					case 3:
						getAlbums();
						break;

					case 4:
						getStatus();
						break;

					case 5:
						getBlog();
						break;

					case 6:
						getNewsFeed(SHARE);
						break;
					case 7:

						break;
					}
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		mModeListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPage = 1;
				mIsOver = false;
				mChooseId = position;
				mModeText.setText(mModeName[position]);
				mModePopupWindow.dismiss();
				switch (position) {
				case 0:
					RenRenData.mVisitorResults.clear();
					mSpare.setVisibility(View.INVISIBLE);
					mDisplay.setAdapter(mVisitorAdapter);
					mType.setText("最近来访");
					mCount.setText("("
							+ mApplication.mUserInfo.getVisitors_count() + ")");
					getVisitor();
					break;

				case 1:
					RenRenData.mNewsFeedResults.clear();
					mSpare.setVisibility(View.INVISIBLE);
					mDisplay.setAdapter(mNewsFeedAdapter);
					mType.setText("新鲜事");
					mCount.setText("");
					getNewsFeed(NEWFEED);
					break;

				case 2:
					mSpare.setVisibility(View.INVISIBLE);
					mInfoAdapter = new InfoAdapter(mContext,
							mApplication.mUserInfo.getFriends_count(),
							mApplication.mUserInfo.getUid(),
							mApplication.mUserInfo.getGender() == 1 ? "男生"
									: "女生", mApplication.mUserInfo
									.getBirth_year()
									+ "年"
									+ mApplication.mUserInfo.getBirth_mouth()
									+ "月"
									+ mApplication.mUserInfo.getBirth_day()
									+ "日", mApplication.mUserInfo.getProvince()
									+ " " + mApplication.mUserInfo.getCity(),
							mApplication.mUserInfo.getNetwork_name());
					mDisplay.setAdapter(mInfoAdapter);
					mType.setText("好友信息");
					mCount.setText("");
					break;

				case 3:
					RenRenData.mAlbumsResults.clear();
					mSpare.setVisibility(View.VISIBLE);
					mSpare.setImageResource(R.drawable.v5_0_1_flipper_head_camera);
					mDisplay.setAdapter(mAlbumsAdapter);
					mType.setText("相册");
					mCount.setText("("
							+ mApplication.mUserInfo.getAlbums_count() + ")");
					getAlbums();
					break;

				case 4:
					RenRenData.mStatusResults.clear();
					mSpare.setVisibility(View.VISIBLE);
					mSpare.setImageResource(R.drawable.v5_0_1_flipper_head_status);
					mDisplay.setAdapter(mStatusAdapter);
					mType.setText("状态");
					mCount.setText("("
							+ mApplication.mUserInfo.getStatus_count() + ")");
					getStatus();
					break;

				case 5:
					RenRenData.mBlogResults.clear();
					mSpare.setVisibility(View.VISIBLE);
					mSpare.setImageResource(R.drawable.v5_0_1_flipper_head_write_blog);
					mDisplay.setAdapter(mBlogAdapter);
					mType.setText("日志");
					mCount.setText("("
							+ mApplication.mUserInfo.getBlogs_count() + ")");
					getBlog();
					break;

				case 6:
					RenRenData.mNewsFeedResults.clear();
					mSpare.setVisibility(View.INVISIBLE);
					mDisplay.setAdapter(mNewsFeedAdapter);
					mType.setText("分享");
					mCount.setText("");
					getNewsFeed(SHARE);
					break;
				case 7:
					mSpare.setVisibility(View.INVISIBLE);
					mDisplay.setAdapter(mCollectionAdapter);
					mType.setText("收藏");
					mCount.setText("");
					mIsOver = true;
				}
			}
		});
		mMenuListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mMenuPopupWindow.dismiss();
				switch (position) {
				case 0:
					Toast.makeText(mContext, "发状态", Toast.LENGTH_SHORT).show();
					break;

				case 1:
					Toast.makeText(mContext, "传照片", Toast.LENGTH_SHORT).show();
					break;

				case 2:
					Toast.makeText(mContext, "报道", Toast.LENGTH_SHORT).show();
					break;

				case 3:
					Toast.makeText(mContext, "写日志", Toast.LENGTH_SHORT).show();
					break;

				case 4:
					Toast.makeText(mContext, "返回顶部", Toast.LENGTH_SHORT).show();
					break;

				case 5:
					Toast.makeText(mContext, "刷新", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
	}

	public void init() {
		if (mApplication.mUserInfo.getUid() == 0) {
			getInfo();
		} else {
			handler.sendEmptyMessage(1);
		}

	}

	private void getInfo() {
		GetInfoRequestParam param = new GetInfoRequestParam(
				mApplication.mRenRen, String.valueOf(mApplication.mRenRen
						.getUserId()));
		RequestListener<GetInfoResponseBean> listener = new RequestListener<GetInfoResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetInfoResponseBean bean) {
				bean.Resolve(mApplication.mUserInfo);
				getProfileInfo();
			}
		};
		mApplication.mAsyncRenRen.getInfo(param, listener);
	}

	private void getProfileInfo() {
		GetProfileInfoRequestParam param = new GetProfileInfoRequestParam(
				mApplication.mRenRen, mApplication.mRenRen.getUserId());
		RequestListener<GetProfileInfoResponseBean> listener = new RequestListener<GetProfileInfoResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetProfileInfoResponseBean bean) {
				bean.Resolve(mApplication.mUserInfo);
				handler.sendEmptyMessage(1);
			}
		};
		mApplication.mAsyncRenRen.getProfileInfo(param, listener);
	}

	private void getVisitor() {
		GetVisitorRequestParam param = new GetVisitorRequestParam(
				mApplication.mRenRen, String.valueOf(mPage), "20");
		RequestListener<GetVisitorResponseBean> listener = new RequestListener<GetVisitorResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetVisitorResponseBean bean) {
				bean.Resolve(mIsAdd);
				mIsOver = bean.isOver;
				handler.sendEmptyMessage(2);
			}
		};
		mApplication.mAsyncRenRen.getVisitor(param, listener);
	}

	private void getNewsFeed(String type) {
		NewsFeedRequestParam param = new NewsFeedRequestParam(
				mApplication.mRenRen, type, String.valueOf(mApplication.mRenRen
						.getUserId()), null, String.valueOf(mPage), "30");
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

	private void getAlbums() {
		GetAlbumsRequestParam param = new GetAlbumsRequestParam(
				mApplication.mRenRen, mApplication.mRenRen.getUserId(),
				String.valueOf(mPage), "10");
		RequestListener<GetAlbumsResponseBean> listener = new RequestListener<GetAlbumsResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetAlbumsResponseBean bean) {
				bean.Resolve(mIsAdd);
				mIsOver = bean.isOver;
				handler.sendEmptyMessage(2);
			}
		};
		mApplication.mAsyncRenRen.getAlbums(param, listener);
	}

	private void getStatus() {
		GetStatusRequestParam param = new GetStatusRequestParam(
				mApplication.mRenRen, 0, String.valueOf(mPage), "20");
		RequestListener<GetStatusResponseBean> listener = new RequestListener<GetStatusResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetStatusResponseBean bean) {
				bean.Resolve(mIsAdd);
				mIsOver = bean.isOver;
				handler.sendEmptyMessage(2);
			}
		};
		mApplication.mAsyncRenRen.getStatus(param, listener);
	}

	private void getBlog() {
		GetBlogsRequestParam param = new GetBlogsRequestParam(
				mApplication.mRenRen, mApplication.mRenRen.getUserId(), 0,
				String.valueOf(mPage), "20");
		RequestListener<GetBlogsResponseBean> listener = new RequestListener<GetBlogsResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetBlogsResponseBean bean) {
				bean.Resolve(mIsAdd);
				mIsOver = bean.isOver;
				handler.sendEmptyMessage(2);
			}
		};
		mApplication.mAsyncRenRen.getBlogs(param, listener);
	}

	private void dialog(String title) {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(title);
		builder.setItems(new String[] { "拍照上传", "本地上传" },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(mContext, "暂时无法提供此功能",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private void initModePopupWindow(int chooseId) {
		ModePopAdapter adapter = new ModePopAdapter(mContext, mModeIcon,
				mModeName, chooseId);
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

	private void initMorePopupWindow() {
		MenuPopAdapter adapter = new MenuPopAdapter(mContext, mMenuName);
		mMenuListView.setAdapter(adapter);
		if (mMenuPopupWindow == null) {
			DisplayMetrics metric = new DisplayMetrics();
			mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
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

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mDialog.show();
				break;
			case 1:
				mDialog.dismiss();
				mName.setText(mApplication.mUserInfo.getName());
				mContent.setText(new Text_Util().replace(mApplication.mUserInfo
						.getContent()));
				mDisplay.setAdapter(mVisitorAdapter);
				mPage = 1;
				mChooseId = 0;
				mIsAdd = false;
				mRefresh = false;
				mIsOver = false;
				mModeText.setText("最近来访");
				mType.setText("最近来访");
				mCount.setText("(" + mApplication.mUserInfo.getVisitors_count()
						+ ")");
				mBar.setVisibility(View.GONE);
				mSpare.setVisibility(View.INVISIBLE);
				if (mApplication.mUserInfo.getStar() == 0) {
					mStar.setVisibility(View.GONE);
				} else {
					mStar.setVisibility(View.VISIBLE);
				}
				if (mApplication.mUserInfo.getZidou() == 0) {
					mVip.setBackgroundResource(R.drawable.v5_0_1_newsfeed_vip_gray_bg);
				} else {
					mVip.setBackgroundResource(R.drawable.v5_0_1_newsfeed_vip_bg);
				}
				if (mApplication.mUserInfo.getVip() == 0) {
					mVip.setText("VIP1");
				} else {
					mVip.setText("VIP" + mApplication.mUserInfo.getVip());
				}

				getVisitor();
				mApplication.mHeadBitmap.display(mAvatar,
						mApplication.mUserInfo.getMainurl());
				break;
			case 2:
				mBar.setVisibility(View.GONE);
				switch (mChooseId) {
				case 0:
					mVisitorAdapter.notifyDataSetChanged();
					break;

				case 1:
					mNewsFeedAdapter.notifyDataSetChanged();
					break;

				case 3:
					mAlbumsAdapter.notifyDataSetChanged();
					break;
				}
				if (!mRefresh) {
					mDisplay.setSelection(0);
				}
				mRefresh = false;
				mIsAdd = false;
				break;
			}
		}
	};

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}

	public View getView() {
		return mUser;
	}
}
