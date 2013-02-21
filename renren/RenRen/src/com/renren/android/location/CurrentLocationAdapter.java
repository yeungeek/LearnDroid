package com.renren.android.location;

import com.renren.android.R;
import com.renren.android.RenRenData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CurrentLocationAdapter extends BaseAdapter {

	private Context mContext;

	public CurrentLocationAdapter(Context context) {
		mContext = context;
	}

	public int getCount() {
		return RenRenData.mCurrentLocationResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mCurrentLocationResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.currentlocation_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView
					.findViewById(R.id.currentlocation_item_icon);
			holder.name = (TextView) convertView
					.findViewById(R.id.currentlocation_item_name);
			holder.count = (TextView) convertView
					.findViewById(R.id.currentlocation_item_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CurrentLocationResult result = RenRenData.mCurrentLocationResults
				.get(position);
		if (position == 0) {
			holder.icon
					.setImageResource(R.drawable.v5_0_1_poi_list_icon_selected);
		} else {
			holder.icon.setImageResource(R.drawable.v5_0_1_poi_list_icon);
		}
		holder.name.setText(result.getName());
		holder.count.setText(result.getCount() + "´Îµ½·Ã");
		return convertView;
	}

	class ViewHolder {
		ImageView icon;
		TextView name;
		TextView count;
	}
}
