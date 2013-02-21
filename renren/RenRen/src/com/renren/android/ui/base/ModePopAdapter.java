package com.renren.android.ui.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.renren.android.R;

public class ModePopAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private int[] mIcon;
	private String[] mName;
	private int mChooseId;

	public ModePopAdapter(Context context, int[] icon, String[] name,
			int chooseId) {
		mInflater = LayoutInflater.from(context);
		mIcon = icon;
		mName = name;
		mChooseId = chooseId;
	}

	public int getCount() {
		return mIcon.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.mode_popupwindow_item,
					null);
			holder = new ViewHolder();
			holder.icon = (CheckBox) convertView
					.findViewById(R.id.mode_pop_icon);
			holder.checked = (ImageView) convertView
					.findViewById(R.id.mode_pop_checked);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.icon.setButtonDrawable(mIcon[position]);
		holder.icon.setText(mName[position]);
		if (position == mChooseId) {
			holder.icon.setChecked(true);
			holder.checked.setVisibility(View.VISIBLE);
		} else {
			holder.icon.setChecked(false);
			holder.checked.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		CheckBox icon;
		ImageView checked;
	}
}
