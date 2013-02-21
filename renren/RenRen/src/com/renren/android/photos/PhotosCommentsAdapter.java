package com.renren.android.photos;

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
import com.renren.android.RenRen;
import com.renren.android.RenRenData;
import com.renren.android.util.Text_Util;

public class PhotosCommentsAdapter extends BaseAdapter {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private LayoutInflater mInflater;
	private RenRen mRenRen;
	private Text_Util mText_Util;

	private int mUid;
	private long mAid;
	private long mPid;

	public PhotosCommentsAdapter(BaseApplication application,Context context, Activity activity,
			RenRen renren, int uid, long aid, long pid) {
		mApplication=application;
		mContext = context;
		mActivity = activity;
		mInflater = LayoutInflater.from(context);
		mRenRen = renren;
		mUid = uid;
		mAid = aid;
		mPid = pid;
		mText_Util = new Text_Util();
	}

	public int getCount() {
		return RenRenData.mPhotosCommentsResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mPhotosCommentsResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.photoscomments_item, null);
			holder = new ViewHolder();
			holder.mAvatar = (ImageView) convertView
					.findViewById(R.id.photoscomments_item_avatar);
			holder.mText = (TextView) convertView
					.findViewById(R.id.photoscomments_item_text);
			holder.mReply = (ImageButton) convertView
					.findViewById(R.id.photoscomments_item_reply);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.photoscomments_item_time);
			holder.mWhisper = (TextView) convertView
					.findViewById(R.id.photoscomments_item_whisper);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final PhotosCommentsResult result = RenRenData.mPhotosCommentsResults
				.get(position);
		mApplication.mHeadBitmap.display(holder.mAvatar, result.getHeadurl());
		mText_Util.addIntentLinkToFriendInfo(mContext, mActivity, holder.mText,
				mText_Util.replace(result.getName() + "  " + result.getText()),
				0, result.getName().length(), result.getUid());
		holder.mTime.setText(result.getTime());
		if (result.getUid() == mRenRen.getUserId()) {
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
				intent.setClass(mContext, PhotosAddCommentActivity.class);
				intent.putExtra("title", "»Ø¸´");
				intent.putExtra("hint", "»Ø¸´ " + result.getName());
				intent.putExtra("aid", mAid);
				intent.putExtra("pid", mPid);
				intent.putExtra("uid", mUid);
				intent.putExtra("rid", result.getUid());
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
