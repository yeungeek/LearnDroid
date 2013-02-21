package com.renren.android.user;

import com.renren.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CollectionAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	public CollectionAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
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
		convertView = mInflater.inflate(R.layout.user_collection_item, null);
		return convertView;
	}
}
