package com.renren.android.photos;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotosEditChooseAlbumAdapter extends BaseAdapter {

	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;

	public PhotosEditChooseAlbumAdapter(BaseApplication application,
			Context context, Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
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
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.photoseditchoosealbum_item, null);
			holder = new ViewHolder();
			holder.mImage = (ImageView) convertView
					.findViewById(R.id.choosealbum_item_image);
			holder.mName = (TextView) convertView
					.findViewById(R.id.choosealbum_item_name);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.choosealbum_item_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AlbumsResult result = RenRenData.mAlbumsResults.get(position);
		mApplication.mAlbumBitmap.display(holder.mImage, result.getUrl());
		holder.mName.setText(result.getName());
		holder.mTime.setText(result.getCreate_time() + "(" + result.getSize()
				+ ")");
		convertView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("aid", result.getAid());
				intent.putExtra("name", result.getName());
				mActivity.setResult(1, intent);
				mActivity.finish();
				mActivity.overridePendingTransition(0, R.anim.roll_down);
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView mImage;
		TextView mName;
		TextView mTime;
	}
}
