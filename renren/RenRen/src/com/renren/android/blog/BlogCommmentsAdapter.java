package com.renren.android.blog;

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
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;

public class BlogCommmentsAdapter extends BaseAdapter {

	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private int mId;
	private int mUid;
	private String mType;

	public BlogCommmentsAdapter(BaseApplication application, Context context,
			Activity activity, int id, int uid, String type) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
		mId = id;
		mUid = uid;
		mType = type;
	}

	public int getCount() {
		return RenRenData.mBlogCommentsResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mBlogCommentsResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.blogcomments_item, null);
			holder = new ViewHolder();
			holder.mAvatar = (ImageView) convertView
					.findViewById(R.id.blogcomments_item_avatar);
			holder.mText = (TextView) convertView
					.findViewById(R.id.blogcomments_item_text);
			holder.mReply = (ImageButton) convertView
					.findViewById(R.id.blogcomments_item_reply);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.blogcomments_item_time);
			holder.mWhisper = (TextView) convertView
					.findViewById(R.id.blogcomments_item_whisper);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final BlogCommentsResult result = RenRenData.mBlogCommentsResults
				.get(position);
		mApplication.mHeadBitmap.display(holder.mAvatar, result.getHeadurl());
		mApplication.mText_Util.addIntentLinkToFriendInfo(
				mContext,
				mActivity,
				holder.mText,
				mApplication.mText_Util.replace(result.getName() + "  "
						+ result.getContent()), 0, result.getName().length(),
				result.getUid());
		holder.mTime.setText(result.getTime());
		if (result.getUid() == mApplication.mRenRen.getUserId()) {
			holder.mReply.setVisibility(View.INVISIBLE);
		} else {
			holder.mReply.setVisibility(View.VISIBLE);
		}
		if (result.getIs_whisper() == 0) {
			holder.mWhisper.setVisibility(View.GONE);
		} else {
			holder.mWhisper.setVisibility(View.VISIBLE);
		}
		holder.mReply.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, BlogAddCommment.class);
				intent.putExtra("title", "»Ø¸´");
				intent.putExtra("hint", "»Ø¸´ " + result.getName());
				intent.putExtra("id", mId);
				intent.putExtra("uid", mUid);
				intent.putExtra("rid", result.getUid());
				intent.putExtra("type", mType);
				mActivity.startActivityForResult(intent, 0);
				mActivity
						.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView mAvatar;
		TextView mText;
		ImageButton mReply;
		TextView mTime;
		TextView mWhisper;
	}
}
