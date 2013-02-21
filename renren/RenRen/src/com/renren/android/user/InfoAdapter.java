package com.renren.android.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.renren.android.R;

public class InfoAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private int mFriendsCount;
	private int mUid;
	private String mGender;
	private String mBirth;
	private String mHomeTown;
	private String mNetWork;

	public InfoAdapter(Context context, int friendsCount, int id,
			String gender, String birth, String hometown, String network) {
		mInflater = LayoutInflater.from(context);
		mFriendsCount = friendsCount;
		mUid = id;
		mGender = gender;
		mBirth = birth;
		mHomeTown = hometown;
		mNetWork = network;
	}

	public int getCount() {
		return 1;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.user_info_item, null);
			holder = new ViewHolder();
			holder.mFriendsCount = (Button) convertView
					.findViewById(R.id.user_head_info_friendscount);
			holder.mInfo_Id = (TextView) convertView
					.findViewById(R.id.user_head_info_id);
			holder.mInfo_Gender = (TextView) convertView
					.findViewById(R.id.user_head_info_gender);
			holder.mInfo_Birth = (TextView) convertView
					.findViewById(R.id.user_head_info_birth);
			holder.mInfo_HomeTown = (TextView) convertView
					.findViewById(R.id.user_head_info_hometown);
			holder.mInfo_NetWork = (TextView) convertView
					.findViewById(R.id.user_head_info_network);
			holder.mInfo_Id_Line = (View) convertView
					.findViewById(R.id.user_head_info_id_line);
			holder.mInfo_Gender_Line = (View) convertView
					.findViewById(R.id.user_head_info_gender_line);
			holder.mInfo_Birth_Line = (View) convertView
					.findViewById(R.id.user_head_info_birth_line);
			holder.mInfo_HomeTown_Line = (View) convertView
					.findViewById(R.id.user_head_info_hometown_line);
			holder.mInfo_NetWork_Line = (View) convertView
					.findViewById(R.id.user_head_info_network_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mFriendsCount.setText(mFriendsCount + "个好友");
		holder.mInfo_Id.setText("人人ID:" + mUid);
		if (mGender == null || mGender.length() == 0) {
			holder.mInfo_Gender.setVisibility(View.GONE);
			holder.mInfo_Gender_Line.setVisibility(View.GONE);
		} else {
			holder.mInfo_Gender_Line.setVisibility(View.VISIBLE);
			holder.mInfo_Gender.setVisibility(View.VISIBLE);
			holder.mInfo_Gender.setText("性别:" + mGender);
		}
		if (mBirth == null || mBirth.length() <= 6) {
			holder.mInfo_Birth_Line.setVisibility(View.GONE);
			holder.mInfo_Birth.setVisibility(View.GONE);
		} else {
			holder.mInfo_Birth_Line.setVisibility(View.VISIBLE);
			holder.mInfo_Birth.setVisibility(View.VISIBLE);
			holder.mInfo_Birth.setText("生日:" + mBirth);
		}
		if (mHomeTown == null || mHomeTown.length() == 1) {
			holder.mInfo_HomeTown_Line.setVisibility(View.GONE);
			holder.mInfo_HomeTown.setVisibility(View.GONE);
		} else {
			holder.mInfo_HomeTown_Line.setVisibility(View.VISIBLE);
			holder.mInfo_HomeTown.setVisibility(View.VISIBLE);
			holder.mInfo_HomeTown.setText("家乡:" + mHomeTown);
		}
		if (mNetWork == null || mNetWork.length() == 0) {
			holder.mInfo_NetWork_Line.setVisibility(View.GONE);
			holder.mInfo_NetWork.setVisibility(View.GONE);
		} else {
			holder.mInfo_NetWork_Line.setVisibility(View.VISIBLE);
			holder.mInfo_NetWork.setVisibility(View.VISIBLE);
			holder.mInfo_NetWork.setText("网络:" + mNetWork);
		}
		return convertView;
	}

	class ViewHolder {
		Button mFriendsCount;
		TextView mInfo_Id;
		TextView mInfo_Gender;
		TextView mInfo_Birth;
		TextView mInfo_HomeTown;
		TextView mInfo_NetWork;
		View mInfo_Id_Line;
		View mInfo_Gender_Line;
		View mInfo_Birth_Line;
		View mInfo_HomeTown_Line;
		View mInfo_NetWork_Line;
	}
}
