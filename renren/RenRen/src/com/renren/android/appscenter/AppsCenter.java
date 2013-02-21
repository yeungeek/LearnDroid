package com.renren.android.appscenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.renren.android.R;
import com.renren.android.ui.base.FlipperLayout.OnOpenListener;

public class AppsCenter {
	private View mAppsCenter;
	private ImageView mFlip;
	
	private OnOpenListener mOnOpenListener;
	public AppsCenter(Context context) {
		mAppsCenter = LayoutInflater.from(context).inflate(R.layout.appscenter,
				null);
		findViewById();
		setListener();
	}
	private void findViewById(){
		mFlip=(ImageView)mAppsCenter.findViewById(R.id.appscenter_flip);
	}
	private void setListener(){
		mFlip.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (mOnOpenListener!=null) {
					mOnOpenListener.open();
				}
			}
		});
	}
	public View getView() {
		return mAppsCenter;
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}
}
