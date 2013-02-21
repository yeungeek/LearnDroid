package com.renren.android.ui.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.renren.android.R;

public class MenuPopBlackAdapter extends BaseAdapter {
	private String[] mName;
	private Context mContext;
	private LayoutInflater mInflater;

	public MenuPopBlackAdapter(Context context, String[] name) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mName = name;
	}

	public int getCount() {
		return mName.length;
	}

	public Object getItem(int position) {
		return mName[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.menu_popupwindow_black_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView
					.findViewById(R.id.menu_pop_black_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(mName[position]);
		convertView
				.setBackgroundResource(R.drawable.photos_popup_menu_item_background);
		return convertView;
	}

	class ViewHolder {
		TextView name;
	}
}
