package com.renren.android.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.photos.AlbumsResult;
import com.renren.android.photos.PhotosListActivity;
import com.renren.android.util.Text_Util;

public class AlbumsAdapter extends BaseAdapter {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private LayoutInflater mInflater;
	private Text_Util mText_Util;
	private String mUserAvatar;
	private String mUserName;

	public AlbumsAdapter(String userAvatar, String userName,
			BaseApplication application, Context context, Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
		mInflater = LayoutInflater.from(context);
		mText_Util = new Text_Util();
		mUserAvatar = userAvatar;
		mUserName = userName;
	}

	public int getCount() {
		return RenRenData.mAlbumsResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mAlbumsResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.user_albums_item, null);
			holder = new ViewHolder();
			holder.mAvatar = (ImageView) convertView
					.findViewById(R.id.user_albums_avatar);
			holder.mUserName = (TextView) convertView
					.findViewById(R.id.user_albums_username);
			holder.mMore = (ImageButton) convertView
					.findViewById(R.id.user_albums_more);
			holder.mName = (TextView) convertView
					.findViewById(R.id.user_albums_name);
			holder.mImage = (ImageView) convertView
					.findViewById(R.id.user_albums_image);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.user_albums_time);
			holder.mComment_Layout = (LinearLayout) convertView
					.findViewById(R.id.user_albums_comment_layout);
			holder.mComment_Count = (TextView) convertView
					.findViewById(R.id.user_albums_comment_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AlbumsResult result = RenRenData.mAlbumsResults.get(position);
		mApplication.mHeadBitmap.display(holder.mAvatar, mUserAvatar);
		mText_Util.addIntentLinkToFriendInfo(mContext, mActivity,
				holder.mUserName, mUserName, 0, mUserName.length(),
				result.getUid());
		holder.mName.setText(result.getName() + "(" + result.getSize() + ")");
		mApplication.mAlbumBitmap.display(holder.mImage, result.getUrl());
		holder.mTime.setText(result.getUpdate_time());
		if (result.getComment_count() > 0) {
			holder.mComment_Layout.setVisibility(View.VISIBLE);
			holder.mComment_Count.setText(result.getComment_count() + "Ãı∆¿¬€");
		} else {
			holder.mComment_Layout.setVisibility(View.GONE);
		}
		holder.mImage.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, PhotosListActivity.class);
				intent.putExtra("userName", mUserName);
				intent.putExtra("uid", result.getUid());
				intent.putExtra("aid", result.getAid());
				intent.putExtra("visable", result.getVisible());
				intent.putExtra("name", result.getName());
				intent.putExtra("count", result.getSize());
				mContext.startActivity(intent);
				mActivity
						.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView mAvatar;
		TextView mUserName;
		ImageButton mMore;
		TextView mName;
		ImageView mImage;
		TextView mTime;
		LinearLayout mComment_Layout;
		TextView mComment_Count;
	}
}
