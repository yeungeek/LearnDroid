package com.anhuioss.crowdroid.activity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.R.id;
import com.anhuioss.crowdroid.util.ImageAdapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class PictureshowActivity extends Activity {

	private GridView picshowGridView = null;

	private Button button_back = null;

	public static Button button_ok = null;

	private ArrayList<String> imageitemlsit = new ArrayList<String>();
	private ArrayList<String> imageitemlsits = new ArrayList<String>();
	private static ArrayList<Integer> choosed = new ArrayList<Integer>();

	private static int count = 0;
	Context context = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pictureshow);
		button_ok = (Button) findViewById(R.id.pic_ok);
		// button_ok.setEnabled(false);
		if (count == 0)
			button_ok.setVisibility(View.GONE);
		button_back = (Button) findViewById(R.id.pic_back);
		picshowGridView = (GridView) findViewById(R.id.pic_show_gridview);
		ContentResolver contentResolver = getContentResolver();
		String[] projection = new String[] { MediaStore.Images.Media.DATA };
		Cursor cursor = contentResolver.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
				null, null);
		cursor.moveToFirst();
		int fileNum = cursor.getCount();

		for (int counter = 0; counter < fileNum; counter++) {

			imageitemlsits.add(cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA)));
			if (count == 0)
				Log.v(String.valueOf(counter), cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA)));
			choosed.add(0);
			cursor.moveToNext();
		}
		cursor.close();
		for (int counter = 0; counter < fileNum; counter++) {
			imageitemlsit.add(imageitemlsits.get(fileNum - 1 - counter));
		}
		imageitemlsits.clear();
		context = this;
		picshowGridView.setAdapter(new ImageAdapter(context, imageitemlsit,
				choosed));
		picshowGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PictureshowActivity.this,
						DetailPictureActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("path", imageitemlsit.get(arg2));
				intent.putExtras(bundle);
				startActivity(intent);

			}
		});

	}

	public void Ok(View V) {
		ArrayList<String> imagechoosed = new ArrayList<String>();
		choosed = ImageAdapter.getchoosed();
		for (int i = 0; i < choosed.size(); i++) {
			if (choosed.get(i) == 1) {
				imagechoosed.add(imageitemlsit.get(i));
			}
		}
		Intent intent = new Intent();
		// intent.setClass(PictureshowActivity.this, SendMessageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("imagepath", imagechoosed);
		intent.putExtras(bundle);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}

	public void Back(View V) {
		finish();
	}

	public static void EmptyChoose() {
		choosed.clear();
	}

	public static void okenable() {
		// button_ok.setEnabled(true);
		button_ok.setVisibility(View.VISIBLE);
	}
}
