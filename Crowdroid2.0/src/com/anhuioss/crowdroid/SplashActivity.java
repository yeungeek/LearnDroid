package com.anhuioss.crowdroid;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.ImageView;

import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.dialog.QustionDialog;
import com.anhuioss.crowdroid.notification.NotificationService;
import com.anhuioss.crowdroid.notification.NotificationTimerReceiver;
import com.anhuioss.crowdroid.service.cfb.CfbCommHandler;
import com.anhuioss.crowdroid.util.CommResult;

public class SplashActivity extends Activity {

	private CrowdroidApplication crowdroidApplication;

	private SettingData settingData;

	String language = null;
	private boolean networkConnect = true;

	Boolean isFirstIn = true;
	SharedPreferences pref;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();

		settingData = crowdroidApplication.getSettingData();

		setContentView(R.layout.activity_splash);

		ImageView imageView = (ImageView) findViewById(R.id.splash_image);
		language = Locale.getDefault().getLanguage();
		if (language.equals("zh")) {
			imageView.setImageResource(R.drawable.splash_description_zh);
		} else if (language.equals("en")) {
			imageView.setImageResource(R.drawable.splash_description_en);
		} else if (language.equals("ja")) {
			imageView.setImageResource(R.drawable.splash_description_jp);
		} else {
			imageView.setImageResource(R.drawable.splash_description_en);
		}
		networkConnect = CheckNetwork();
		if (networkConnect) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					pref = getSharedPreferences("isFristInSharePre", 0);
					isFirstIn = pref.getBoolean("isFirstIn", true);
					editor = pref.edit();
					if (isFirstIn == true) {
						editor.putBoolean("isFirstIn", true);
						editor.commit();
					} else {
						editor.putBoolean("isFirstIn", false);
						editor.commit();
					}
					getLocalData();
					// startAutoCheckTimer();

					Intent intent = new Intent(SplashActivity.this,
							LoginActivity.class);
					startActivity(intent);

					SplashActivity.this.finish();
				}
			}, 1500);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				boolean flag = false;
				ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				if (cwjManager.getActiveNetworkInfo() != null)
					flag = cwjManager.getActiveNetworkInfo().isAvailable();
				if (flag) {

					// startAutoCheckTimer();
					getLocalData();

					Intent intent = new Intent(SplashActivity.this,
							LoginActivity.class);
					startActivity(intent);

					SplashActivity.this.finish();
				} else {
					SplashActivity.this.finish();
				}
			}
		}, 1000);
	}

	protected boolean CheckNetwork() {
		// TODO Auto-generated method stub
		boolean flag = false;
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		if (cwjManager.getActiveNetworkInfo() != null)
			flag = cwjManager.getActiveNetworkInfo().isAvailable();
		if (!flag) {
			Builder b = new AlertDialog.Builder(this).setTitle(
					R.string.network_tip).setMessage(
					R.string.network_connection_tip);
			b.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							if (getSDKVersionNumber() >= 14) {
								startActivityForResult(
										new Intent(
												android.provider.Settings.ACTION_SETTINGS),
										0);
							} else {
								startActivityForResult(
										new Intent(
												android.provider.Settings.ACTION_WIRELESS_SETTINGS),
										0);
							}

						}
					})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									SplashActivity.this.finish();
								}
							}).create();
			b.show();
		}
		return flag;
	}

	// 判断系统的版本
	// android4.0以上为>=14,
	public static int getSDKVersionNumber() {

		int sdkVersion;
		try {
			sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);

		} catch (NumberFormatException e) {

			sdkVersion = 0;
		}
		return sdkVersion;
	}

	private void getLocalData() {
		CommResult comResult = CfbCommHandler
				.getVersionMessage(new HashMap<String, Object>());
		if (comResult.getMessage() != null) {
			settingData.setNewestVersionData(comResult.getMessage());
		}
	}

}
