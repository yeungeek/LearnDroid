package com.anhuioss.crowdroid.activity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.util.AsyncImageLoad;
import com.anhuioss.crowdroid.util.AsyncImageLoad.ImageCallback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class DetailPictureActivity extends Activity {

	private Button button = null;
	private ImageView image = null;
	AsyncImageLoad imageLoader = new AsyncImageLoad();

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pic_detail);
		Bundle bundle = this.getIntent().getExtras();
		String path = bundle.getString("path");

		image = (ImageView) findViewById(R.id.pic_detail_image);

		// Bitmap bm=null;
		Bitmap bm = BitmapFactory.decodeFile(path);
		// try {
		// bm =BitmapFactory.decodeStream(openFileInput(path));
		// image.setImageBitmap(bm);
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		image.setImageBitmap(bm);
		button = (Button) findViewById(R.id.pic_detail_back);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TimelineActivity.isBackgroundNotificationFlag=true;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TimelineActivity.isBackgroundNotificationFlag=false;
	}
}
