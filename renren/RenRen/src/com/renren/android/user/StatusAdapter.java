package com.renren.android.user;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.util.Text_Util;

public class StatusAdapter extends BaseAdapter {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private LayoutInflater mInflater;
	private Text_Util mText_Util;
	private String mUserAvatar;
	private String mUserName;

	public StatusAdapter(String userAvatar, String userName,
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
		return RenRenData.mStatusResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mStatusResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.user_status_item, null);
			holder = new ViewHolder();
			holder.mAvatar = (ImageView) convertView
					.findViewById(R.id.user_status_avatar);
			holder.mUserName = (TextView) convertView
					.findViewById(R.id.user_status_username);
			holder.mMore = (ImageButton) convertView
					.findViewById(R.id.user_status_more);
			holder.mMessage = (TextView) convertView
					.findViewById(R.id.user_status_message);
			holder.mRoot = (LinearLayout) convertView
					.findViewById(R.id.user_status_root);
			holder.mRootName = (TextView) convertView
					.findViewById(R.id.user_status_root_username);
			holder.mRootMessage = (TextView) convertView
					.findViewById(R.id.user_status_root_message);
			holder.mLocation = (LinearLayout) convertView
					.findViewById(R.id.user_status_location);
			holder.mLocationName = (TextView) convertView
					.findViewById(R.id.user_status_location_name);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.user_status_time);
			holder.mComment = (LinearLayout) convertView
					.findViewById(R.id.user_status_comment_layout);
			holder.mCommentCount = (TextView) convertView
					.findViewById(R.id.user_status_comment_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		StatusResult result = RenRenData.mStatusResults.get(position);
		mApplication.mHeadBitmap.display(holder.mAvatar, mUserAvatar);
		mText_Util.addIntentLinkToFriendInfo(mContext, mActivity,
				holder.mUserName, mUserName, 0, mUserName.length(),
				result.getUid());
		holder.mMessage.setText(mText_Util.replace(result.getMessage()));
		if (result.getRoot_status_id() != 0 && result.getRoot_uid() != 0) {
			holder.mRoot.setVisibility(View.VISIBLE);
			holder.mRootName.setText(result.getRoot_username());
			holder.mRootMessage.setText(mText_Util.replace(result
					.getRoot_message()));
		} else {
			holder.mRoot.setVisibility(View.GONE);
		}
		if (result.getName() != null && result.getName().length() > 0) {
			holder.mLocation.setVisibility(View.VISIBLE);
			holder.mLocationName.setText(result.getName());
		} else {
			holder.mLocation.setVisibility(View.GONE);
		}
		holder.mTime
				.setText(result.getTime() + " 来自" + result.getSource_name());
		if (result.getComment_count() > 0) {
			holder.mComment.setVisibility(View.VISIBLE);
			holder.mCommentCount.setText(result.getComment_count() + "条评论");
		} else {
			holder.mComment.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView mAvatar;
		TextView mUserName;
		ImageButton mMore;
		TextView mMessage;
		LinearLayout mRoot;
		TextView mRootName;
		TextView mRootMessage;
		LinearLayout mLocation;
		TextView mLocationName;
		TextView mTime;
		LinearLayout mComment;
		TextView mCommentCount;
	}
}
