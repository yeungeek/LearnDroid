package com.anhuioss.crowdroid.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.StatusData;

public class PreviewImageActivity extends Activity implements OnClickListener {
	private static StatusData statusData;
	private static CrowdroidApplication crowdroidApplocation;
	private static String url;
	private static String now_url;
	private static URL conurl;
	private String filePath = "/sdcard/";
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog mProgressDialog;
	float scale = 0.0f;
	private boolean mapFlag = false;
	private WebView image_webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_preview_image);
		crowdroidApplocation = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplocation.getStatusData();
		scale = ((CrowdroidApplication) getApplicationContext())
				.getScaleDensity();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		url = bundle.getString("url");
		if (bundle.containsKey("map")) {
			mapFlag = bundle.getBoolean("map");
		}

		Button back = (Button) findViewById(R.id.back);

		Button save = (Button) findViewById(R.id.save);

		TextView preview_title = (TextView) findViewById(R.id.preview_title);

		preview_title.setTextSize(20.0f);
		back.setOnClickListener(this);
		save.setOnClickListener(this);

		image_webview = (WebView) findViewById(R.id.web_view);
		image_webview.setBackgroundColor(Color.TRANSPARENT);
		image_webview.getSettings().setJavaScriptEnabled(true);
		image_webview.getSettings().setSupportZoom(true);// 支持缩放
		image_webview.getSettings().setBuiltInZoomControls(true);// 设置出现缩放工具
		if (mapFlag == false) {
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TWITTER)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				url = url.substring(url.indexOf("src='") + 5,
						url.indexOf("'></center>"));

				if (url.contains("http://yfrog.com/")) {
					url = url.replace("http", "https").replace("small",
							"iphone");
				} else if (url.contains("http://twitpic.com/")
						|| url.contains("img.ly")) {
					url = url.replace("mini", "large");
				}
				String urlHtml = "<center><img style='max-height:"
						+ (600 * scale) + "px; max-width:" + (320 * scale)
						+ "px;' src='" + url + "' /><br><center>";

				image_webview.loadData(urlHtml, "text/html", "utf-8");
			} else {
				image_webview.loadDataWithBaseURL("", url, "text/html",
						"utf-8", "");
			}

			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TENCENT)) {
				save.setVisibility(View.GONE);
			}
		} else if (mapFlag == true) {
			save.setText(getString(R.string.discovery_lbs_view_detail_map));
			image_webview
					.loadDataWithBaseURL("", url, "text/html", "utf-8", "");
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back: {
			finish();
			break;

		}
		case R.id.save: {
			if (mapFlag == true) {
				image_webview.loadDataWithBaseURL("",
						url.replace("zoom=16", "zoom=18"), "text/html",
						"utf-8", "");
			} else {
				startDownload();
			}

			break;

		}

		default:
			break;
		}

	}

	private void startDownload() {
		int sub = url.lastIndexOf("=") + 2;

		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			new DownloadFileAsync().execute(url);
		} else {
			now_url = url.substring(sub, url.lastIndexOf("'"));
			new DownloadFileAsync().execute(now_url);
		}
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
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SINA)) {
				fileName = now_url.substring(28);
			} else if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SOHU)) {
				fileName = now_url.substring(41);
			} else if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				fileName = now_url.substring(66);
			} else if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TWITTER)) {
				fileName = url.substring(url.lastIndexOf("/"));
			}

			int count;

			try {

				conurl = new URL(aurl[0]);
				URLConnection conexion = conurl.openConnection();
				conexion.connect();
				File file = new File(filePath + fileName);
				int lenghtOfFile = conexion.getContentLength();
				Log.e("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

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
				Log.e("error", e.getMessage().toString());
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
			Toast.makeText(PreviewImageActivity.this,
					getString(R.string.download_success), Toast.LENGTH_SHORT)
					.show();
		}

	}

}
