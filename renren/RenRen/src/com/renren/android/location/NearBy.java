package com.renren.android.location;

import com.renren.android.BaseApplication;
import com.renren.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class NearBy extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private ListView mDisplay;
	private NearByAdapter mAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby);
		mApplication = (BaseApplication) getApplication();
		findViewById();
		setListener();
		mAdapter = new NearByAdapter(mApplication, this);
		mDisplay.setAdapter(mAdapter);
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.nearby_back);
		mDisplay = (ListView) findViewById(R.id.nearby_display);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
