package com.anhuioss.crowdroid.dialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.info.TipInfo;
import com.anhuioss.crowdroid.service.cfb.CfbCommHandler;
import com.anhuioss.crowdroid.service.cfb.CfbParseHandler;
import com.anhuioss.crowdroid.util.CommResult;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadUpdateDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context mContext;

	private TextView updateMessage;

	private Button btnDownUpdate;

	private Button btnDownCancel;

	private CheckBox checkUpdateNextTime;

	private CommResult comResult;

	private int packVersionCode;

	private ArrayList<TipInfo> tipInfoList = new ArrayList<TipInfo>();

	private TipInfo tipInfo;

	private String versionType = null;

	private String versionCode = null;

	private String versionName = null;

	private String downLoadUrl = null;

	private String message = null;

	private HandleProgressDialog progress = null;

	private static boolean isRunning = true;

	private Handler mHandler;

	public DownloadUpdateDialog(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.dialog_download_update_apk);

		setTitle(R.string.update_version);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
				android.R.drawable.stat_sys_download);

		updateMessage = (TextView) findViewById(R.id.text_check_update_message);
		btnDownUpdate = (Button) findViewById(R.id.btn_download_update_apk);
		btnDownCancel = (Button) findViewById(R.id.btn_exit_download_apk);
		checkUpdateNextTime = (CheckBox) findViewById(R.id.check_download_show);
		btnDownUpdate.setOnClickListener(this);
		btnDownCancel.setOnClickListener(this);

		// progressBar = (ProgressBar) findViewById(R.id.progressBar);

		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo info = pm.getPackageInfo("com.anhuioss.crowdroid", 0);
			packVersionCode = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		Map<String, Object> map = new HashMap<String, Object>();
		comResult = CfbCommHandler.getVersionMessage(map);
		if (comResult.getMessage() != null) {
			tipInfoList = CfbParseHandler.parseVersionMessage(comResult
					.getMessage());

			int maxCode = 0;
			for (TipInfo tip : tipInfoList) {

				if ("normal".equals(tip.getVersionType())) {
					maxCode = Integer.valueOf(tip.getVersionCode());
					if (maxCode <= packVersionCode) {
						btnDownUpdate.setEnabled(false);
						message = mContext
								.getString(R.string.check_no_latest_version);
					} else {
						versionCode = String.valueOf(maxCode);
						downLoadUrl = tip.getDownLoadUrl();
						message = mContext
								.getString(R.string.check_has_latest_version);
					}
					versionName = tip.getVersionName();

				} else if ("special".equals(tip.getVersionType())) {

				}

			}
		} else {
			Toast.makeText(mContext,
					mContext.getString(R.string.error_msg_503),
					Toast.LENGTH_SHORT).show();
		}

		updateMessage.setText(mContext.getString(R.string.version_name)
				+ versionName + "\n" + message);
		if (versionName == null) {
			btnDownUpdate.setEnabled(false);
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_download_update_apk) {

			new DownloadFileAsync().execute(downLoadUrl);
			dismiss();

		}
		if (v.getId() == R.id.btn_exit_download_apk) {
			closeDialog(!checkUpdateNextTime.isChecked());
			dismiss();
		}

		if (v.getId() == R.id.check_download_show) {

		}
	}

	private void closeDialog(boolean isCheckUpdateNextTime) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		Editor editor = prefs.edit();
		if (isCheckUpdateNextTime) {
			editor.putString("is-show-update", "true");
		} else {
			editor.putString("is-show-update", "false");
		}
		editor.commit();
		dismiss();
	}

	protected void downLoadFile(String httpUrl, String versionName) {

		// showProgressDialog();
		final String fileName = versionName + ".apk";
		File tmpFile = new File("/sdcard/download");
		if (!tmpFile.exists()) {
			tmpFile.mkdir();
		}
		final File file = new File("/sdcard/download/" + fileName);

		try {
			URL url = new URL(httpUrl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);

				byte[] buf = new byte[256];
				conn.connect();
				double count = 0;
				if (conn.getResponseCode() >= 400) {
					Toast.makeText(mContext, "连接超时", Toast.LENGTH_SHORT).show();
				} else {
					while (count <= 100) {
						mHandler.sendEmptyMessage(1);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (is != null) {
							int numRead = is.read(buf);
							if (numRead <= 0) {
								break;
							} else {
								fos.write(buf, 0, numRead);
							}

						} else {
							break;
						}

					}
				}
				mHandler.sendEmptyMessage(1);
				// closeProgressDialog();
				conn.disconnect();
				fos.close();
				is.close();
			} catch (IOException e) {
				Toast.makeText(mContext,
						mContext.getString(R.string.download_fail),
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {

			e.printStackTrace();
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

		String filePath = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
		}

		@Override
		protected String doInBackground(String... aurl) {

			final String fileName = versionName + ".apk";
			File tmpFile = new File("/sdcard/download");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			final File file = new File("/sdcard/download/" + fileName);

			int count;
			try {
				// downLoadFile(downLoadUrl, versionName);

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
			return null;
		}

		@Override
		protected void onPostExecute(String unused) {

			closeProgressDialog();

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			// builder.setIcon(R.drawable.update_icon);
			builder.setTitle(R.string.download_alert);
			builder.setMessage(getContext()
					.getString(R.string.download_success)
					+ "\n"
					+ getContext().getString(R.string.download_path)
					+ filePath
					+ "\n" + "确认现在安装吗？");
			builder.setNegativeButton(getContext().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 安装程序的apk文件路径
							// String apkName =
							// Environment.getExternalStorageDirectory() +
							// versionName + ".apk";
							String apkName = filePath;
							// 创建URI
							Uri uri = Uri.fromFile(new File(apkName));
							// 创建Intent意图
							Intent intent = new Intent(Intent.ACTION_VIEW);
							// 设置Uri和类型
							intent.setDataAndType(uri,
									"application/vnd.android.package-archive");
							// 执行意图进行安装
							mContext.startActivity(intent);

						}
					});
			builder.show();
		}
	}
}
