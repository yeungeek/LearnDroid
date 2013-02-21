package com.renren.android.friends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.chat.ChatInfo;
import com.renren.android.user.Friend;

public class FriendsFindSearchAdapter extends BaseAdapter {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;

	public FriendsFindSearchAdapter(BaseApplication application,
			Context context, Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
	}

	public int getCount() {
		return RenRenData.mFriendsFindResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mFriendsFindResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.friendsfindsearch_item, null);
			holder = new ViewHolder();
			holder.mAvatar = (ImageView) convertView
					.findViewById(R.id.friendsfindsearch_item_avatar);
			holder.mButton = (Button) convertView
					.findViewById(R.id.friendsfindsearch_item_button);
			holder.mName = (TextView) convertView
					.findViewById(R.id.friendsfindsearch_item_name);
			holder.mInfo = (TextView) convertView
					.findViewById(R.id.friendsfindsearch_item_info);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final FriendsFindResult result = RenRenData.mFriendsFindResults
				.get(position);
		mApplication.mHeadBitmap.display(holder.mAvatar, result.getTinyurl());
		holder.mName.setText(result.getName());
		holder.mInfo.setText(result.getInfo());
		if (result.getIsFriend() == 0) {
			holder.mButton.setText("加为好友");
		} else {
			holder.mButton.setText("私信");
		}
		convertView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, Friend.class);
				intent.putExtra("uid", result.getId());
				mContext.startActivity(intent);
				mActivity
						.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		holder.mButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (result.getIsFriend() == 1) {
					Intent intent = new Intent();
					intent.setClass(mContext, ChatInfo.class);
					intent.putExtra("name", result.getName());
					mContext.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.roll_up,
							R.anim.roll);
				} else {
					Toast.makeText(mContext, "暂时无法提供此功能", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView mAvatar;
		TextView mName;
		TextView mInfo;
		Button mButton;
	}
}
