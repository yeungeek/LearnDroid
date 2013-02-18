package com.anhuioss.crowdroid.settings;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.settings.AutoRefreshActivity;
import com.anhuioss.crowdroid.settings.MultiTweetActivity;
import com.anhuioss.crowdroid.settings.NotificationSettingActivity;
import com.anhuioss.crowdroid.settings.TranslationSettingActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class PersonalSettingActivity extends Activity implements
		android.view.View.OnClickListener {

	private Button btn_back;

	private RadioGroup selectItem;

	private RadioButton language;

	private RadioButton autorefresh;

	private RadioButton translation;

	private RadioButton notification;

	private RadioButton multi_tweet;

	private RadioButton broadcast;

	private StatusData statusData;

	private static int id = 0;

	public static final String select[] = { "0", "1", "2", "3" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_settings);

		btn_back = (Button) findViewById(R.id.back);
		selectItem = (RadioGroup) findViewById(R.id.selectitem);
		language = (RadioButton) findViewById(R.id.language);
		autorefresh = (RadioButton) findViewById(R.id.autorefresh);
		translation = (RadioButton) findViewById(R.id.translation);
		notification = (RadioButton) findViewById(R.id.notification);
		multi_tweet = (RadioButton) findViewById(R.id.multi_tweet);
		broadcast = (RadioButton) findViewById(R.id.setting_broadcast);

		btn_back.setOnClickListener(this);
		selectItem.setOnClickListener(this);
		language.setOnClickListener(this);
		autorefresh.setOnClickListener(this);
		translation.setOnClickListener(this);
		notification.setOnClickListener(this);
		multi_tweet.setOnClickListener(this);
		broadcast.setOnClickListener(this);

		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			multi_tweet.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStop() {

		// CrowdroidApplication crowdroidApplication = (CrowdroidApplication)
		// getApplicationContext();
		// crowdroidApplication.refreshImageRes();
		// crowdroidApplication.startAutoCheckTimer();
		super.onStop();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.language: {
			// 语言设置功能
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(PersonalSettingActivity.this);

			int choice = prefs.getInt("dialog_choince_id", 0);
			final CharSequence[] items = getResources().getStringArray(
					R.array.setting_speech_language);
			AlertDialog.Builder dlg = new AlertDialog.Builder(
					PersonalSettingActivity.this);
			dlg.setTitle(R.string.speech_language_selected)
					.setSingleChoiceItems(items, choice, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							id = which;
						}
					})
					.setPositiveButton(R.string.ok, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences prefs = PreferenceManager
									.getDefaultSharedPreferences(PersonalSettingActivity.this);
							prefs.edit()
									.putString("speechlanguageselect",
											select[id]).commit();
							prefs.edit().putInt("dialog_choince_id", id)
									.commit();
						}
					})
					.setNegativeButton(R.string.cancel, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).create().show();
			break;
		}
		case R.id.autorefresh: {
			// 自动刷新功能
			Intent intent = new Intent(PersonalSettingActivity.this,
					AutoRefreshActivity.class);
			startActivity(intent);

			break;
		}
		case R.id.translation: {
			// 翻译功能
			Intent intent = new Intent(PersonalSettingActivity.this,
					TranslationSettingActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.notification: {
			// 通知功能
			Intent intent = new Intent(PersonalSettingActivity.this,
					NotificationSettingActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.multi_tweet: {
			// 多服务器推送功能
			Intent intent = new Intent(PersonalSettingActivity.this,
					MultiTweetActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.setting_broadcast: {
			// 信息广播功能
			Intent intent = new Intent(PersonalSettingActivity.this,
					AutoBroadcastActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.back: {
			finish();
			break;
		}
		default:
			break;
		}

	}
}
