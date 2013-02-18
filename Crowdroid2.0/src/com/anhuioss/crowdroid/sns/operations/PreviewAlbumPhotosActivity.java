package com.anhuioss.crowdroid.sns.operations;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.CommentActivity;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.util.GalleryFlow;
import com.anhuioss.crowdroid.util.GalleryFlowAdapter;

import dalvik.system.VMRuntime;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PreviewAlbumPhotosActivity extends BasicActivity implements
		OnClickListener {

	private String currentUrl;
	private ArrayList<TimeLineInfo> timelineInfoList;
	private int currentPosition = 0;
	String down_url = null;
	String filePath = "/sdcard/";
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

	private EditText comment;

	GalleryFlowAdapter adapter;

	private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	private ProgressDialog mProgressDialog;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.browse_album_photos_view);

		Bundle bundle = getIntent().getExtras();
		currentUrl = bundle.getString("currentUrl");
		timelineInfoList = (ArrayList<TimeLineInfo>) bundle
				.getSerializable("timelineInfoList");
		currentPosition = bundle.getInt("position");
		// VMRuntime.getRuntime()
		// .setTargetHeapUtilization(TARGET_HEAP_UTILIZATION); // 处理内存溢出
		adapter = new GalleryFlowAdapter(this, timelineInfoList);
		adapter.createReflectedImages();
		GalleryFlow galleryFlow = (GalleryFlow) findViewById(R.id.Gallery01);
		galleryFlow.setAdapter(adapter);
		galleryFlow.setSpacing(5); // 图片之间的间距
		galleryFlow.setSelection(currentPosition);

		Button back = (Button) findViewById(R.id.back);

		Button save = (Button) findViewById(R.id.save);

		TextView preview_title = (TextView) findViewById(R.id.preview_title);

		comment = (EditText) findViewById(R.id.comment);
		comment.setInputType(InputType.TYPE_NULL);

		preview_title.setTextSize(20.0f);
		back.setOnClickListener(this);
		save.setOnClickListener(this);
		comment.setOnClickListener(this);

		comment.clearFocus();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save: {
			startDownload();
			break;
		}
		case R.id.back: {
			finish();
			break;
		}
		case R.id.comment: {
			Intent intent = new Intent(PreviewAlbumPhotosActivity.this,
					CommentActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("message_id",
					timelineInfoList.get(adapter.getItemId()).getStatusId());
			bundle.putString("user_id", timelineInfoList.get(currentPosition)
					.getUserInfo().getUid());
			bundle.putString("feed_type", "30");
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			break;
		}
		default:
			break;
		}

	}

	private void startDownload() {

		new DownloadFileAsync().execute(timelineInfoList
				.get(currentPosition)
				.getStatus()
				.substring(
						timelineInfoList.get(currentPosition).getStatus()
								.indexOf(";")));
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		adapter.clearCache();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		adapter.clearCache();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Downloading file..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	class DownloadFileAsync extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		@Override
		protected String doInBackground(String... aurl) {
			String fileName = "";
			int front = timelineInfoList.get(currentPosition).getStatus()
					.lastIndexOf("/") + 1;
			fileName = timelineInfoList
					.get(currentPosition)
					.getStatus()
					.substring(
							front,
							timelineInfoList.get(currentPosition).getStatus()
									.lastIndexOf("."));
			int count;
			try {

				URL conurl = new URL(aurl[0]);
				URLConnection conexion = conurl.openConnection();
				conexion.connect();
				File file = new File(filePath + fileName);
				int lenghtOfFile = conexion.getContentLength();

				InputStream input = new BufferedInputStream(conurl.openStream());
				OutputStream output = new FileOutputStream(file);
				byte data[] = new byte[1024];
				long total = 0;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();

			} catch (Exception e) {
				// Log.e("error", e.getMessage().toString());
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			mProgressDialog.setProgress(Integer.parseInt(values[0]));
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);

		}
	}

	@Override
	protected void refreshByMenu() {
		// TODO Auto-generated method stub

	}

}
