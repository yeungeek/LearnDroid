package com.anhuioss.crowdroid.activity;

import org.apache.commons.httpclient.cookie.IgnoreCookiesSpec;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.LoginActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.dialog.DownloadUpdateDialog;
import com.anhuioss.crowdroid.dialog.LicenseDialog;
import com.anhuioss.crowdroid.settings.AccountManageActivity;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.settings.PersonalSettingActivity;
import com.anhuioss.crowdroid.settings.ThemeSettingActivity;
import com.anhuioss.crowdroid.settings.WeiBoSettingActivity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MoreFunctionActivity extends BasicActivity implements
		OnClickListener {

	private Button headBack = null;

	private Button headHome = null;

	private TextView headName = null;

	private TextView account_manage = null;

	private TextView themes_setting = null;

	private TextView browsemode = null;

	private TextView weibo_setting = null;

	private TextView setting = null;

	private TextView license = null;

	private TextView officeWeibo = null;

	private TextView check_update = null;

	private TextView about = null;

	private TextView logout = null;

	private TextView feedback = null;

	private int fontColor;
	private int fontSize;

	CrowdroidApplication crowdroidApplication = null;
	SettingData settingData;
	StatusData statusData;
	AccountData accountData;

	private String TAG = "HandlerTest";
	private boolean bpostRunnable = false;

	// Handler
	private Runnable mRunnable = null;
	private MyHandler myHandler = null;
	Bundle bundle1 = new Bundle();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setLayoutResId(R.layout.activity_more);

		bundle1 = this.getIntent().getExtras();

		headBack = (Button) findViewById(R.id.head_back);
		headHome = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);
		headName.setText(R.string.setting_more);

		// setting item
		account_manage = (TextView) findViewById(R.id.account_manage);
		themes_setting = (TextView) findViewById(R.id.themes_setting);
		browsemode = (TextView) findViewById(R.id.skim);
		weibo_setting = (TextView) findViewById(R.id.weibo_setting);
		setting = (TextView) findViewById(R.id.setting);
		license = (TextView) findViewById(R.id.license);
		officeWeibo = (TextView) findViewById(R.id.office_weibo);
		check_update = (TextView) findViewById(R.id.check_update);
		about = (TextView) findViewById(R.id.about);
		logout = (TextView) findViewById(R.id.logout);
		feedback = (TextView) findViewById(R.id.feedback);

		myHandler = new MyHandler();

		headBack.setOnClickListener(this);
		headHome.setOnClickListener(this);
		headHome.setBackgroundResource(R.drawable.main_home);

		account_manage.setOnClickListener(this);
		themes_setting.setOnClickListener(this);
		browsemode.setOnClickListener(this);
		weibo_setting.setOnClickListener(this);
		setting.setOnClickListener(this);
		license.setOnClickListener(this);
		officeWeibo.setOnClickListener(this);
		check_update.setOnClickListener(this);
		about.setOnClickListener(this);
		logout.setOnClickListener(this);
		feedback.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		super.onStart();
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();
		statusData = crowdroidApplication.getStatusData();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();
		String color = settingData.getFontColor();
		String size = settingData.getFontSize();
		fontColor = Integer.valueOf(color);
		fontSize = Integer.valueOf(size);
		initView();

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {
			Intent comment = new Intent(MoreFunctionActivity.this,
					HomeTimelineActivity.class);
			startActivity(comment);
			break;
		}
		case R.id.account_manage: {
			Intent intent = new Intent(MoreFunctionActivity.this,
					AccountManageActivity.class);
			intent.putExtras(bundle1);
			startActivity(intent);
			break;
		}
		case R.id.themes_setting: {
			Intent intent = new Intent(MoreFunctionActivity.this,
					ThemeSettingActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.skim: {
			Intent intent = new Intent(MoreFunctionActivity.this,
					BrowseModeActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.weibo_setting: {
			Intent intent = new Intent(MoreFunctionActivity.this,
					WeiBoSettingActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.setting: {
			Intent intent = new Intent(MoreFunctionActivity.this,
					PersonalSettingActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.license: {
			LicenseDialog lic = new LicenseDialog(this);
			lic.show();
			break;
		}
		case R.id.about: {
			Uri uri = null;
			String language = this.getApplicationContext().getResources()
					.getConfiguration().locale.getLanguage();
			if (language != null && language.equals("zh")) {
				uri = Uri.parse("http://www.anhuioss.com/cn/crowdroid");
			} else if (language != null && language.equals("ja")) {
				uri = Uri.parse("http://www.anhuioss.com/crowdroid");
			} else {
				uri = Uri.parse("http://www.anhuioss.com/en/crowdroid");
			}
			Intent i = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(i);
			break;
		}
		case R.id.office_weibo: {
			Intent intent = new Intent(MoreFunctionActivity.this,
					ProfileActivity.class);
			Bundle bundle = new Bundle();
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				bundle.putString("name", "nakao");
				bundle.putString("uid", "11");
			} else if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TWITTER)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
				String language = this.getApplicationContext().getResources()
						.getConfiguration().locale.getLanguage();
				if (language != null && language.equals("zh")) {
					bundle.putString("name", "anhuioss_cn");
				} else if (language != null && language.equals("ja")) {
					bundle.putString("name", "AnhuiOSS");
				} else {
					bundle.putString("name", "AnhuiOSS_EN");

				}

			} else if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SINA)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SOHU)) {
				bundle.putString("name", "安徽开源软件有限公司");
				bundle.putString("user_name", "anhuioss");
			} else {
				bundle.putString("name", "安徽开源软件有限公司");
				bundle.putString("user_name", "安徽开源软件有限公司");
				bundle.putString("uid", "324411430");

			}
			// bundle.putString("name", "");
			// bundle.putString("user_name", "");
			// bundle.putString("uid","");
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			break;
		}
		case R.id.check_update: {
			new DownloadUpdateDialog(this).show();
			break;
		}
		case R.id.logout: {
			confirmLogoutDialog();
			break;
		}
		case R.id.feedback: {
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				Intent intent = new Intent(MoreFunctionActivity.this,
						CommentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("message_id", "478083032");
				bundle.putString("message", "");
				bundle.putString("user_id", "324411430");
				bundle.putString("feed_type", "20");
				bundle.putString("feedback", "");
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			} else {
				Intent intent = new Intent(MoreFunctionActivity.this,
						SendMessageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("feedback", "FeedBack");
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}

			break;
		}
		default:
			break;
		}
	}

	private void initView() {

		account_manage.setTextSize(fontSize * 1.2f);
		themes_setting.setTextSize(fontSize * 1.2f);
		browsemode.setTextSize(fontSize * 1.2f);
		themes_setting.setTextSize(fontSize * 1.2f);
		weibo_setting.setTextSize(fontSize * 1.2f);
		setting.setTextSize(fontSize * 1.2f);
		license.setTextSize(fontSize * 1.2f);
		officeWeibo.setTextSize(fontSize * 1.2f);
		check_update.setTextSize(fontSize * 1.2f);
		about.setTextSize(fontSize * 1.2f);
		logout.setTextSize(fontSize * 1.2f);
		feedback.setTextSize(fontSize * 1.2f);

		if (String.valueOf(fontColor).contains("-")) {
			account_manage.setTextColor(fontColor);
			themes_setting.setTextColor(fontColor);
			browsemode.setTextColor(fontColor);
			themes_setting.setTextColor(fontColor);
			weibo_setting.setTextColor(fontColor);
			setting.setTextColor(fontColor);
			license.setTextColor(fontColor);
			officeWeibo.setTextColor(fontColor);
			check_update.setTextColor(fontColor);
			about.setTextColor(fontColor);
			logout.setTextColor(fontColor);
			feedback.setTextColor(fontColor);
		} else {
			account_manage.setTextColor(getResources().getColor(fontColor));
			themes_setting.setTextColor(getResources().getColor(fontColor));
			browsemode.setTextColor(getResources().getColor(fontColor));
			themes_setting.setTextColor(getResources().getColor(fontColor));
			weibo_setting.setTextColor(getResources().getColor(fontColor));
			setting.setTextColor(getResources().getColor(fontColor));
			license.setTextColor(getResources().getColor(fontColor));
			officeWeibo.setTextColor(getResources().getColor(fontColor));
			check_update.setTextColor(getResources().getColor(fontColor));
			about.setTextColor(getResources().getColor(fontColor));
			logout.setTextColor(getResources().getColor(fontColor));
			feedback.setTextColor(getResources().getColor(fontColor));
		}

		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			weibo_setting.setVisibility(View.VISIBLE);
		} else {
			weibo_setting.setVisibility(View.GONE);
		}

		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			officeWeibo.setText(R.string.official_account);
		}
	}

	@Override
	protected void refreshByMenu() {
		// TODO Auto-generated method stub

	}

	private void confirmLogoutDialog() {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle(R.string.logout);
		dlg.setMessage(getResources().getString(R.string.wheter_to_logout))
				.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								//
								NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
								notificationManager.cancelAll();

								Intent i = new Intent(
										MoreFunctionActivity.this,
										LoginActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								i.putExtra("autoLogin", false);
								startActivity(i);
								// android.os.Process
								// .killProcess(android.os.Process.myPid());
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create().show();

	}

	// =================================================================================
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			initView();

		}
	};

	private void updateByMessage() {
		new Thread() {
			public void run() {
				Message msg = mHandler.obtainMessage(0);
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	private class MyHandler extends Thread {
		public MyHandler() {
			super("MyHandler");
		}

		public void run() {
			updateByMessage();
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
