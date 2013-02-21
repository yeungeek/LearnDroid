package com.renren.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.appscenter.AppsCenter;
import com.renren.android.chat.Chat;
import com.renren.android.desktop.Desktop;
import com.renren.android.desktop.Desktop.onChangeViewListener;
import com.renren.android.friends.Friends;
import com.renren.android.location.Location;
import com.renren.android.message.Message;
import com.renren.android.newsfeed.NewsFeed;
import com.renren.android.page.Page;
import com.renren.android.photos.PhotosEdit;
import com.renren.android.search.Search;
import com.renren.android.ui.base.FlipperLayout;
import com.renren.android.ui.base.FlipperLayout.OnOpenListener;
import com.renren.android.user.User;
import com.renren.android.util.View_Util;

public class DesktopActivity extends Activity implements OnOpenListener {
	private BaseApplication mApplication;
	private FlipperLayout mRoot;
	private Desktop mDesktop;
	private User mUser;
	private NewsFeed mNewsFeed;
	private Message mMessage;
	private Chat mChat;
	private Friends mFriends;
	private Page mPage;
	private Location mLocation;
	private Search mSearch;
	private AppsCenter mAppsCenter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (BaseApplication) getApplication();
		mRoot = new FlipperLayout(DesktopActivity.this);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mRoot.setLayoutParams(params);
		mDesktop = new Desktop(mApplication, this);
		mUser = new User(mApplication, this, this);
		mNewsFeed = new NewsFeed(mApplication, this, this);
		mMessage = new Message(this);
		mChat = new Chat(this);
		mFriends = new Friends(mApplication, this, this);
		mPage = new Page(mApplication, this, this);
		mLocation = new Location(mApplication, this, this);
		mSearch = new Search(mApplication, this, this);
		mAppsCenter = new AppsCenter(this);
		mRoot.addView(mDesktop.getView(), params);
		mRoot.addView(mNewsFeed.getView(), params);
		setContentView(mRoot);
		setListener();
	}

	private void setListener() {
		mNewsFeed.setOnOpenListener(this);
		mUser.setOnOpenListener(this);
		mMessage.setOnOpenListener(this);
		mChat.setOnOpenListener(this);
		mFriends.setOnOpenListener(this);
		mPage.setOnOpenListener(this);
		mLocation.setOnOpenListener(this);
		mSearch.setOnOpenListener(this);
		mDesktop.setOnChangeViewListener(new onChangeViewListener() {

			public void onChangeView(int arg0) {
				switch (arg0) {
				case View_Util.Information:
					mUser.init();
					mRoot.close(mUser.getView());
					break;

				case View_Util.NewsFeed:
					mRoot.close(mNewsFeed.getView());
					break;

				case View_Util.Message:
					mRoot.close(mMessage.getView());
					break;

				case View_Util.Chat:
					mRoot.close(mChat.getView());
					break;
				case View_Util.Friends:
					if (RenRenData.mFriendsResults.size() == 0) {
						mFriends.init();
					}
					mRoot.close(mFriends.getView());
					break;
				case View_Util.Page:
					if (RenRenData.mPageResults.size() == 0) {
						mPage.init();
					}
					mRoot.close(mPage.getView());
					break;
				case View_Util.Location:
					mLocation.init();
					mRoot.close(mLocation.getView());
					break;
				case View_Util.Search:
					mRoot.close(mSearch.getView());
					break;
				case View_Util.Apps_Center:
					mRoot.close(mAppsCenter.getView());
					break;
				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Uri uri = null;
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					Toast.makeText(this, "SD卡不可用", Toast.LENGTH_SHORT).show();
					return;
				}
				mApplication.mImageType = 0;
				startActivity(new Intent(DesktopActivity.this,
						PhotosEdit.class));
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			} else {
				Toast.makeText(this, "取消拍照", Toast.LENGTH_SHORT).show();
			}
			break;

		case 1:
			if (data == null) {
				Toast.makeText(this, "取消上传", Toast.LENGTH_SHORT).show();
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					Toast.makeText(this, "SD卡不可用", Toast.LENGTH_SHORT).show();
					return;
				}
				uri = data.getData();
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(uri, proj, null, null, null);
				if (cursor != null) {
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					if (cursor.getCount() > 0 && cursor.moveToFirst()) {
						mApplication.mImagePath = cursor
								.getString(column_index);
						if (mApplication.mImagePath != null) {
							mApplication.mImageType = 1;
							startActivity(new Intent(DesktopActivity.this,
									PhotosEdit.class));
							overridePendingTransition(R.anim.roll_up,
									R.anim.roll);
						} else {
							Toast.makeText(this, "图片未找到", Toast.LENGTH_SHORT)
									.show();
						}
					} else {
						Toast.makeText(this, "图片未找到", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(this, "图片未找到", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "获取错误", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mRoot.getScreenState() == FlipperLayout.SCREEN_STATE_CLOSE) {
				mRoot.open();
			} else {
				dialog();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void dialog() {
		AlertDialog.Builder builder = new Builder(DesktopActivity.this);
		builder.setMessage("您确定要退出吗?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	public void open() {
		if (mRoot.getScreenState() == FlipperLayout.SCREEN_STATE_CLOSE) {
			mRoot.open();
		}
	}
}
