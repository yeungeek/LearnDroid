package com.renren.android.photos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RequestListener;

public class PhotosEditChooseAlbum extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private ListView mDisplay;
	private View mHead;
	private LinearLayout mHeadCreate;

	private PhotosEditChooseAlbumAdapter mAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoseditchoosealbum);
		mApplication = (BaseApplication) getApplication();
		mHead = LayoutInflater.from(this).inflate(
				R.layout.photoseditchoosealbum_head, null);
		findViewById();
		setListener();
		mDisplay.addHeaderView(mHead);
		mAdapter = new PhotosEditChooseAlbumAdapter(mApplication, this, this);
		mDisplay.setAdapter(mAdapter);
		getAlbums();
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.choosealbum_back);
		mDisplay = (ListView) findViewById(R.id.choosealbum_display);
		mHeadCreate = (LinearLayout) mHead
				.findViewById(R.id.choosealbum_create);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				setResult(0);
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mHeadCreate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startActivityForResult((new Intent(PhotosEditChooseAlbum.this,
						PhotosCreateAlbum.class)), 0);
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
	};

	private void getAlbums() {
		GetAlbumsRequestParam param = new GetAlbumsRequestParam(
				mApplication.mRenRen, mApplication.mRenRen.getUserId(), "1",
				"50");
		RequestListener<GetAlbumsResponseBean> listener = new RequestListener<GetAlbumsResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetAlbumsResponseBean bean) {
				bean.Resolve(false);
				handler.sendEmptyMessage(0);
			}
		};
		mApplication.mAsyncRenRen.getAlbums(param, listener);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 0:

			break;

		case 1:
			setResult(resultCode, data);
			finish();
			overridePendingTransition(0, 0);
			break;
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}

	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(0);
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
