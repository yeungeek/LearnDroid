package com.renren.android.user;

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

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.chat.ChatInfo;
import com.renren.android.util.Text_Util;

public class VisitorAdapter extends BaseAdapter {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private Text_Util mText_Util;

	public VisitorAdapter(BaseApplication application, Context context,
			Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
		mText_Util = new Text_Util();
	}

	public int getCount() {
		return RenRenData.mVisitorResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mVisitorResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.user_visitor_item, null);
			holder = new ViewHolder();
			holder.mAvatar = (ImageView) convertView
					.findViewById(R.id.user_visitor_avatar);
			holder.mName = (TextView) convertView
					.findViewById(R.id.user_visitor_name);
			holder.mButton = (Button) convertView
					.findViewById(R.id.user_visitor_button);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.user_visitor_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final VisitorResult result = RenRenData.mVisitorResults.get(position);
		mApplication.mHeadBitmap.display(holder.mAvatar, result.getHeadurl());
		mText_Util
				.addIntentLinkToFriendInfo(mContext, mActivity, holder.mName,
						result.getName(), 0, result.getName().length(),
						result.getUid());
		holder.mTime.setText(result.getTime());
		holder.mButton.setText("к╫пе");
		holder.mButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, ChatInfo.class);
				intent.putExtra("name", result.getName());
				mContext.startActivity(intent);
				mActivity
						.overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView mAvatar;
		TextView mName;
		Button mButton;
		TextView mTime;
	}
}
