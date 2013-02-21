package com.renren.android.location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.util.Text_Util;

public class NearByAdapter extends BaseAdapter {
	private BaseApplication mApplication;
	private Context mContext;
	private Text_Util mText_Util;
	private static final double EARTH_RADIUS = 6378137.0;

	public NearByAdapter(BaseApplication application, Context context) {
		mApplication = application;
		mContext = context;
		mText_Util = new Text_Util();
	}

	public int getCount() {
		return RenRenData.mNearByResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mNearByResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.nearby_item, null);
			holder = new ViewHolder();
			holder.mImage = (ImageView) convertView
					.findViewById(R.id.nearby_item_image);
			holder.mName = (TextView) convertView
					.findViewById(R.id.nearby_item_name);
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.nearby_item_title);
			holder.mPrice = (TextView) convertView
					.findViewById(R.id.nearby_item_price);
			holder.mValue = (TextView) convertView
					.findViewById(R.id.nearby_item_value);
			holder.mDistance = (TextView) convertView
					.findViewById(R.id.nearby_item_distance);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		NearByResult result = RenRenData.mNearByResults.get(position);
		mApplication.mNearByBitmap.display(holder.mImage, result.getImage());
		holder.mName.setText(result.getName());
		holder.mTitle.setText(result.getTitle());
		holder.mPrice.setText(result.getPrice() + "元");
		mText_Util.addStrikethrough(holder.mValue, result.getValue() + "元", 0,
				(result.getValue() + "元").length());
		holder.mDistance.setText(getDistance(mApplication.mLongitude,
				mApplication.mLatitude, result.getLongitude(),
				result.getLatitude())
				+ "米");
		return convertView;
	}

	private double getDistance(double longitude1, double latitude1,

	double longitude2, double latitude2) {

		double Lat1 = rad(latitude1);
		double Lat2 = rad(latitude2);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(Lat1) * Math.cos(Lat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	private double rad(double d) {
		return d * Math.PI / 180.0;
	}

	class ViewHolder {
		ImageView mImage;
		TextView mName;
		TextView mTitle;
		TextView mPrice;
		TextView mValue;
		TextView mDistance;
	}
}
