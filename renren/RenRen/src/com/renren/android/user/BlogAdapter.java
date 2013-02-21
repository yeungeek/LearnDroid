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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.blog.BlogsResult;
import com.renren.android.util.Text_Util;

public class BlogAdapter extends BaseAdapter {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private LayoutInflater mInflater;
	private Text_Util mText_Util;
	private String mUserAvatar;
	private String mUserName;

	public BlogAdapter(String userAvatar, String userName,
			BaseApplication application,Context context, Activity activity) {
		mApplication=application;
		mContext = context;
		mActivity = activity;
		mInflater = LayoutInflater.from(context);
		mText_Util = new Text_Util();
		mUserAvatar = userAvatar;
		mUserName = userName;
	}

	public int getCount() {
		return RenRenData.mBlogResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mBlogResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.user_blog_item, null);
			holder = new ViewHolder();
			holder.mDisplay = (RelativeLayout) convertView
					.findViewById(R.id.user_blog_display);
			holder.mNoDisplay = (RelativeLayout) convertView
					.findViewById(R.id.user_blog_nodisplay);
			holder.mAvatar = (ImageView) convertView
					.findViewById(R.id.user_blog_avatar);
			holder.mUserName = (TextView) convertView
					.findViewById(R.id.user_blog_username);
			holder.mMore = (ImageButton) convertView
					.findViewById(R.id.user_blog_more);
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.user_blog_title);
			holder.mContent = (TextView) convertView
					.findViewById(R.id.user_blog_content);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.user_blog_time);
			holder.mComment = (LinearLayout) convertView
					.findViewById(R.id.user_blog_comment_layout);
			holder.mComment_Count = (TextView) convertView
					.findViewById(R.id.user_blog_comment_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BlogsResult result = RenRenData.mBlogResults.get(position);
		if (getCount() == 1 && result.getTotal() == 0) {
			holder.mDisplay.setVisibility(View.GONE);
			holder.mNoDisplay.setVisibility(View.VISIBLE);
		} else {
			holder.mDisplay.setVisibility(View.VISIBLE);
			holder.mNoDisplay.setVisibility(View.GONE);
			mApplication.mHeadBitmap.display(holder.mAvatar, mUserAvatar);
			mText_Util.addIntentLinkToFriendInfo(mContext, mActivity,
					holder.mUserName, mUserName, 0, mUserName.length(),
					result.getUid());
			holder.mTitle.setText(result.getTitle());
			holder.mContent.setText(mText_Util.replace(result.getContent()));
			holder.mTime.setText(result.getTime());
			if (result.getComment_count() == 0) {
				holder.mComment.setVisibility(View.GONE);
			} else {
				holder.mComment.setVisibility(View.VISIBLE);
				holder.mComment_Count
						.setText(result.getComment_count() + "Ãı∆¿¬€");
			}
		}
		return convertView;
	}

	class ViewHolder {
		RelativeLayout mDisplay;
		RelativeLayout mNoDisplay;
		ImageView mAvatar;
		TextView mUserName;
		ImageButton mMore;
		TextView mTitle;
		TextView mContent;
		TextView mTime;
		LinearLayout mComment;
		TextView mComment_Count;
	}
}
