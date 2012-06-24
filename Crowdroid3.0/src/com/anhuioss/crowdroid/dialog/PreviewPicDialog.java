package com.anhuioss.crowdroid.dialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.dialog.DownloadUpdateDialog.DownloadFileAsync;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PreviewPicDialog extends Dialog implements
		android.view.View.OnClickListener {

	Context mContext;

	private MediaScannerClient client = null;

	private String fileName = null;

	private String filePath = "/sdcard/download";

	private String picUrl = null;

	private String themeName = null;

	private WebView mWeb_Preview_Theme;

	private Button mBtn_Download_Theme_ok;

	private Button mBtn_Download_Theme_Cancel;

	private MediaScannerConnection mediaScanConn = null;

	private String fileType = null;

	private String[] filePaths = null;

	private HandleProgressDialog progress = null;

	private static boolean isRunning = true;

	public PreviewPicDialog(Context context, String picUrl, String themeName) {
		super(context);
		mContext = context;
		this.picUrl = picUrl;
		this.themeName = themeName;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_preview_picture);

		setTitle(R.string.setting_wallpaper_theme_preview);

		mWeb_Preview_Theme = (WebView) findViewById(R.id.webview_preview_theme);

		mBtn_Download_Theme_ok = (Button) findViewById(R.id.btn_download_theme_ok);

		mBtn_Download_Theme_Cancel = (Button) findViewById(R.id.btn_download_theme_cancel);

		mBtn_Download_Theme_ok.setOnClickListener(this);

		mBtn_Download_Theme_Cancel.setOnClickListener(this);

		mWeb_Preview_Theme.setBackgroundColor(Color.TRANSPARENT);
		mWeb_Preview_Theme.getSettings().setJavaScriptEnabled(true);
		mWeb_Preview_Theme.getSettings().setSupportZoom(true);// 支持缩放
		mWeb_Preview_Theme.getSettings().setBuiltInZoomControls(true);// 设置出现缩放工具
		StringBuffer htmlData = new StringBuffer();
		htmlData.append("<center>");
		htmlData.append(
			"<img style='max-height: 400px; max-width:200px; margin-top:4px;' src='");
		htmlData.append(picUrl+"-small.png");
		htmlData.append("' /></a>");
		htmlData.append("<br>");
		htmlData.append("<center>");
		mWeb_Preview_Theme.loadData(htmlData.toString(), "text/html", "utf-8");

//		mWeb_Preview_Theme.loadUrl(picUrl + "-small.png");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_download_theme_ok) {
			new DownloadFileAsync().execute(picUrl + ".png");
			dismiss();

		}
		if (v.getId() == R.id.btn_download_theme_cancel) {
			dismiss();
		}
	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(mContext);
		}
		progress.show();
	}

	private void closeProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress != null) {
			progress.dismiss();
		}
	}

	class DownloadFileAsync extends AsyncTask<String, String, String> {

		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
		}

		@Override
		protected String doInBackground(String... aurl) {

			final String fileName = themeName + ".png";
			File tmpFile = new File(filePath);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			final File file = new File(filePath+"/" + fileName);

			int count;
			try {

				URL url = new URL(aurl[0]);
				URLConnection conexion = url.openConnection();
				conexion.connect();
				int lenghtOfFile = conexion.getContentLength();
				Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
				InputStream input = new BufferedInputStream(url.openStream());
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
			filePath = file.toString();
			fileType = "image/png";
			return null;
		}

		@Override
		protected void onPostExecute(String unused) {
			// 图片添加到媒体库
			if (client == null) {
				client = new MediaScannerClient();
			}
			if (mediaScanConn == null) {
				mediaScanConn = new MediaScannerConnection(mContext, client);
			}

			// 更新媒体库
			scanSdCard();

			if (client == null) {
				client = new MediaScannerClient();
			}
			if (mediaScanConn == null) {
				mediaScanConn = new MediaScannerConnection(mContext, client);
			}

			closeProgressDialog();

			Toast.makeText(mContext, getContext().getString(R.string.download_success)+ "\n"+ getContext().getString(R.string.download_path) + filePath, Toast.LENGTH_SHORT).show();
//			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//			// builder.setIcon(R.drawable.update_icon);
//			builder.setTitle(R.string.download_alert);
//			builder.setMessage(getContext()
//					.getString(R.string.download_success)
//					+ "\n"
//					+ getContext().getString(R.string.download_path) + filePath);
//			builder.setNegativeButton(getContext().getString(R.string.ok),
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int id) {
//							dialog.cancel();
//						}
//					});
//			builder.show();
		}

		private void scanSdCard() {
			IntentFilter intentfilter = new IntentFilter(
					Intent.ACTION_MEDIA_SCANNER_STARTED);
			intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
			intentfilter.addDataScheme("file");
			ScanSdReceiver scanSdReceiver = new ScanSdReceiver();
			mContext.registerReceiver(scanSdReceiver, intentfilter);
			mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
					.parse("file://"
							+ Environment.getExternalStorageDirectory()
									.getAbsolutePath())));
		}
	}

	class MediaScannerClient implements
			MediaScannerConnection.MediaScannerConnectionClient {

		public void onMediaScannerConnected() {

			if (filePath != null) {
				mediaScanConn.scanFile(filePath, fileType);
			}
			if (filePaths != null) {
				for (String file : filePaths) {
					mediaScanConn.scanFile(file, fileType);
				}
			}
			filePath = null;
			fileType = null;
			filePaths = null;
		}

		public void onScanCompleted(String path, Uri uri) {
			// TODO Auto-generated method stub
			mediaScanConn.disconnect();
		}
	}

	/**
	 * 扫描文件标签信息
	 * 
	 * @param filePath
	 *            文件路径 eg:/sdcard/MediaPlayer/dahai.mp3
	 * @param fileType
	 *            文件类型 eg: audio/mp3 media /* application/ogg
	 * */
	public void scanFile(String filepath, String fileType) {
		this.filePath = filepath;
		this.fileType = fileType;
		// 连接之后调用MusicSannerClient的onMediaScannerConnected()方法
		mediaScanConn.connect();
	}

	/**
	 * @param filePaths
	 *            文件路径
	 * @param fileType
	 *            文件类型
	 * */
	public void scanFile(String[] filePaths, String fileType) {

		this.filePaths = filePaths;

		this.fileType = fileType;

		mediaScanConn.connect();

	}

	public String getFilePath() {

		return filePath;
	}

	public void setFilePath(String filePath) {

		this.filePath = filePath;
	}

	public String getFileType() {

		return fileType;
	}

	public void setFileType(String fileType) {

		this.fileType = fileType;
	}

	// =========================================================================
	class ScanSdReceiver extends BroadcastReceiver {

		private AlertDialog.Builder builder = null;
		private AlertDialog ad = null;
		private int count1;
		private int count2;
		private int count;

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// String[] projection = {MediaStore.Images.Media.DATA};
			if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
				Cursor c1 = context.getContentResolver().query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Images.Media.TITLE,
								MediaStore.Images.Media.MIME_TYPE,
								MediaStore.Images.Media.DESCRIPTION,
								MediaStore.Images.Media._ID,
								MediaStore.Images.Media.DISPLAY_NAME }, null,
						null, null);
				count1 = c1.getCount();
				System.out.println("count:" + count);
				builder = new AlertDialog.Builder(context);
				// builder.setMessage("正在扫描存储卡...");
				ad = builder.create();
				ad.show();

			} else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
				Cursor c2 = context.getContentResolver().query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Images.Media.TITLE,
								MediaStore.Images.Media.MIME_TYPE,
								MediaStore.Images.Media.DESCRIPTION,
								MediaStore.Images.Media._ID,
								MediaStore.Images.Media.DISPLAY_NAME }, null,
						null, null);
				count2 = c2.getCount();
				count = count2 - count1;
				ad.cancel();
				// if (count>=0){
				// Toast.makeText(context, "共增加" +
				// count + "张图片", Toast.LENGTH_SHORT).show();
				// } else {
				// Toast.makeText(context, "共减少" +
				// count + "张图片", Toast.LENGTH_SHORT).show();
				// }
			}
		}
	}

}