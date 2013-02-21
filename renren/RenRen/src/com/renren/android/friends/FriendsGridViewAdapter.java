package com.renren.android.friends;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.renren.android.R;

public class FriendsGridViewAdapter extends BaseAdapter {
	private Context mContext;
	private Map<String, Integer> mMap;

	public FriendsGridViewAdapter(Context context, Map<String, Integer> map) {
		mContext = context;
		mMap = map;
	}

	public int getCount() {
		return mMap.size();
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
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.friends_item_gridview_item, null);
			holder = new ViewHolder();
			holder.mText = (TextView) convertView
					.findViewById(R.id.friends_item_gird_item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mText.setText((CharSequence) mMap.keySet().toArray()[position]);
		return convertView;
	}

	class ViewHolder {
		TextView mText;
	}
}
