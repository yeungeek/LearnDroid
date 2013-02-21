package com.renren.android.newsfeed;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RequestListener;
import com.renren.android.location.CurrentLocation;
import com.renren.android.ui.base.FlipperLayout.OnOpenListener;
import com.renren.android.ui.base.LoadingDialog;
import com.renren.android.ui.base.ModePopAdapter;

public class NewsFeed {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private View mNewsFeed;

	private ImageView mFlip;
	private RelativeLayout mModeLayout;
	private TextView mModeText;
	private ImageView mCamera;
	private ImageView mStatus;
	private ImageView mCheckIn;
	private ListView mDisplay;
	private RelativeLayout mNoDisplay;
	private ProgressBar mBar;

	private PopupWindow mModePopupWindow;
	private View mModeView;
	private ListView mModeListView;

	private NewsFeedAdapter mAdapter;
	private LoadingDialog mDialog;
	private OnOpenListener mOnOpenListener;

	private int[] mModeIcon = {
			R.drawable.v5_0_1_newsfeed_popupwindow_type_all_background,
			R.drawable.v5_0_1_newsfeed_popupwindow_type_specialfocus_background,
			R.drawable.v5_0_1_newsfeed_popupwindow_type_status_background,
			R.drawable.v5_0_1_newsfeed_popupwindow_type_photo_background,
			R.drawable.v5_0_1_newsfeed_popupwindow_type_place_background,
			R.drawable.v5_0_1_newsfeed_popupwindow_type_share_background,
			R.drawable.v5_0_1_newsfeed_popupwindow_type_blog_background };
	private String[] mModeName = { "新鲜事", "特别关注", "状态", "照片", "位置", "分享", "日志" };
	private static final String TYPE_ALL = "10,11,20,21,22,23,30,31,32,33,36";
	private static final String TYPE_SPECIALFOCUS = null;
	private static final String TYPE_STATUS = "10,11";
	private static final String TYPE_PHOTO = "30,31";
	private static final String TYPE_PLACE = null;
	private static final String TYPE_SHARE = "21,23,32,33,36";
	private static final String TYPE_BLOG = "20,22";
	private String mNewsFeedType = TYPE_ALL;
	private boolean mRefresh = false;
	private int mPage = 1;
	private boolean mIsAdd = false;
	private boolean mIsHaveData = false;
	private int mChooseId = 0;

	public NewsFeed(BaseApplication application, Context context,
			Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;

		mDialog = new LoadingDialog(context, R.style.dialog);
		mNewsFeed = LayoutInflater.from(context).inflate(R.layout.newsfeed,
				null);
		mModeView = LayoutInflater.from(context).inflate(
				R.layout.mode_popupwindow, null);
		mModePopupWindow = new PopupWindow(mModeView, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		mModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mModePopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
		findViewById();
		setListener();
		mAdapter = new NewsFeedAdapter(mApplication, context, activity);
		mDisplay.setAdapter(mAdapter);
		getNewsFeed(mNewsFeedType);
	}

	private void findViewById() {
		mFlip = (ImageView) mNewsFeed.findViewById(R.id.newsfeed_flip);
		mModeLayout = (RelativeLayout) mNewsFeed
				.findViewById(R.id.newsfeed_mode_layout);
		mModeText = (TextView) mNewsFeed.findViewById(R.id.newsfeed_mode_text);
		mCamera = (ImageView) mNewsFeed.findViewById(R.id.newsfeed_camera);
		mStatus = (ImageView) mNewsFeed.findViewById(R.id.newsfeed_status);
		mCheckIn = (ImageView) mNewsFeed.findViewById(R.id.newsfeed_checkin);
		mDisplay = (ListView) mNewsFeed.findViewById(R.id.newsfeed_display);
		mNoDisplay = (RelativeLayout) mNewsFeed
				.findViewById(R.id.newsfeed_nodisplay);
		mBar = (ProgressBar) mNewsFeed.findViewById(R.id.newsfeed_progressbar);

		mModeListView = (ListView) mModeView.findViewById(R.id.mode_pop_list);
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
				initModePopupWindow(mChooseId);
			}
		});
		mDisplay.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (view.getLastVisiblePosition() == view.getCount() - 1
						&& mRefresh == false && mIsHaveData == true) {
					mRefresh = true;
					mDisplay.setSelection(mDisplay.getCount());
					mPage++;
					mIsAdd = true;
					mBar.setVisibility(View.VISIBLE);
					getNewsFeed(mNewsFeedType);
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
				mChooseId = position;
				mModeText.setText(mModeName[position]);
				mModePopupWindow.dismiss();
				switch (position) {
				case 0:
					mNewsFeedType = TYPE_ALL;
					mDisplay.setVisibility(View.VISIBLE);
					mNoDisplay.setVisibility(View.GONE);
					getNewsFeed(mNewsFeedType);
					break;

				case 1:
					mNewsFeedType = TYPE_SPECIALFOCUS;
					mDisplay.setVisibility(View.GONE);
					mNoDisplay.setVisibility(View.VISIBLE);
					break;

				case 2:
					mNewsFeedType = TYPE_STATUS;
					mDisplay.setVisibility(View.VISIBLE);
					mNoDisplay.setVisibility(View.GONE);
					getNewsFeed(mNewsFeedType);
					break;

				case 3:
					mNewsFeedType = TYPE_PHOTO;
					mDisplay.setVisibility(View.VISIBLE);
					mNoDisplay.setVisibility(View.GONE);
					getNewsFeed(mNewsFeedType);
					break;

				case 4:
					mNewsFeedType = TYPE_PLACE;
					mDisplay.setVisibility(View.GONE);
					mNoDisplay.setVisibility(View.VISIBLE);
					break;

				case 5:
					mNewsFeedType = TYPE_SHARE;
					mDisplay.setVisibility(View.VISIBLE);
					mNoDisplay.setVisibility(View.GONE);
					getNewsFeed(mNewsFeedType);
					break;

				case 6:
					mNewsFeedType = TYPE_BLOG;
					mDisplay.setVisibility(View.VISIBLE);
					mNoDisplay.setVisibility(View.GONE);
					getNewsFeed(mNewsFeedType);
					break;
				}
			}
		});
		mCamera.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog();
			}
		});
		mStatus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mContext.startActivity(new Intent(mContext,
						NewsFeedPublish.class));
				mActivity
						.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		mCheckIn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mContext.startActivity(new Intent(mContext,
						CurrentLocation.class));
				mActivity
						.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
	}

	private void getNewsFeed(String type) {
		NewsFeedRequestParam param = new NewsFeedRequestParam(
				mApplication.mRenRen, type, null, null, String.valueOf(mPage),
				null);
		RequestListener<NewsFeedResponseBean> listener = new RequestListener<NewsFeedResponseBean>() {

			public void onStart() {
				handler.sendEmptyMessage(0);
			}

			public void onComplete(NewsFeedResponseBean bean) {
				bean.Resolve(mIsAdd, true);
				handler.sendEmptyMessage(1);
			}
		};
		mApplication.mAsyncRenRen.getNewsFeed(param, listener);
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

	private void dialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("上传照片");
		builder.setItems(new String[] { "拍照上传", "本地上传" },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = null;
						switch (which) {
						case 0:
							intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							File dir = new File(
									"/sdcard/RenRenForAndroid/Camera/");
							if (!dir.exists()) {
								dir.mkdirs();
							}
							mApplication.mImagePath = "/sdcard/RenRenForAndroid/Camera/"
									+ UUID.randomUUID().toString();
							File file = new File(mApplication.mImagePath);
							if (!file.exists()) {
								try {
									file.createNewFile();
								} catch (IOException e) {

								}
							}
							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(file));
							mActivity.startActivityForResult(intent, 0);
							break;

						case 1:
							intent = new Intent(Intent.ACTION_PICK, null);
							intent.setDataAndType(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									"image/*");
							mActivity.startActivityForResult(intent, 1);
							break;
						}
					}
				});
		builder.create().show();
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (!mRefresh) {
					mDialog.show();
				}
				break;
			case 1:
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				mBar.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				if (!mRefresh) {
					mDisplay.setSelection(0);
				}
				mRefresh = false;
				mIsAdd = false;
				mIsHaveData = true;
				break;
			}
		}
	};

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}

	public View getView() {
		mDisplay.setSelection(0);
		return mNewsFeed;
	}
}
