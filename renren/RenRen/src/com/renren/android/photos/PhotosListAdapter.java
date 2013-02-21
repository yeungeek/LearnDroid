package com.renren.android.photos;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.renren.android.BaseApplication;
import com.renren.android.RenRenData;

public class PhotosListAdapter extends BaseAdapter {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;

	public PhotosListAdapter(BaseApplication application, Context context,
			Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
	}

	public int getCount() {
		return RenRenData.mPhotosResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mPhotosResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView mImageView = null;
		if (convertView == null) {
			mImageView = new ImageView(mContext);
			DisplayMetrics metric = new DisplayMetrics();
			mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
			int width = metric.widthPixels;
			mImageView.setLayoutParams(new GridView.LayoutParams(width / 3,
					width / 3));
			mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			mImageView = (ImageView) convertView;
		}
		PhotosResult result = RenRenData.mPhotosResults.get(position);
		mApplication.mPhotoBitmap.display(mImageView, result.getUrl_main());
		return mImageView;

	}
}
