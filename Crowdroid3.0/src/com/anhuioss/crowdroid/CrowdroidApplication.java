package com.anhuioss.crowdroid;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;
import com.anhuioss.crowdroid.data.KeywordFilterList;
import com.anhuioss.crowdroid.data.ListInfoList;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.TranslationList;
import com.anhuioss.crowdroid.data.UserFilterList;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.notification.NotificationService;
import com.anhuioss.crowdroid.notification.NotificationTimerReceiver;
import com.anhuioss.crowdroid.service.cfb.CfbCommHandler;
import com.anhuioss.crowdroid.service.renren.RenRenCommHandler;
import com.anhuioss.crowdroid.service.sina.SinaCommHandler;
import com.anhuioss.crowdroid.service.sohu.SohuCommHandler;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;
import com.anhuioss.crowdroid.service.twitter.TwitterCommHandler;
import com.anhuioss.crowdroid.service.twitterproxy.TwitterProxyCommHandler;

public class CrowdroidApplication extends Application {

	private SettingData settingData;

	private StatusData statusData;

	private AccountList accountList;

	private TranslationList translationList;

	private KeywordFilterList keywordFilterList;

	private UserFilterList userFilterList;

	private ListInfoList listInfoList;
	
	private float scaleDensity = 1.0f;
	
	/** 0=timeline 1=comment 2=followed/unfollowed*/
	private boolean isComeFromNotification[] = {false, false, false};
	
	/** 0=timeline 1=comment 2=followed/unfollowed*/
	public void setIsComeFromNotification(int type) {
		if(type == 0 || type == 1 || type == 2) {
			this.isComeFromNotification[type] = true;
		}
	}
	
	public boolean isComeFromNotification(int type) {
		if(type == 0 || type == 1 || type == 2) {
			boolean result = isComeFromNotification[type];
			isComeFromNotification[type] = false;
			return result;
		}
		return false;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		scaleDensity = getResources().getDisplayMetrics().scaledDensity;
		
		initData();

		initCommHandler();
		
		startAutoCheckTimer();
		
	}

	public StatusData getStatusData() {

		return statusData;

	}

	public AccountList getAccountList() {

		return accountList;

	}

	public KeywordFilterList getKeywordFilterList() {

		return keywordFilterList;

	}

	public UserFilterList getUserFilterList() {

		return userFilterList;

	}

	public TranslationList getTranslationList() {

		return translationList;

	}

	public SettingData getSettingData() {

		return settingData;

	}
	
	public float getScaleDensity() {
		
		return scaleDensity;
		
	}

	public ListInfoList getListInfoList() {

		return listInfoList;

	}

	public void refreshImageRes() {

		// Prepare List Data
		AccountData account = accountList.getCurrentAccount();
		ArrayList<ListInfo> list = listInfoList
				.getListsByUserName(account == null ? "" : account
						.getUserName());

		// Prepare Gallery Data
		int[] resIds = settingData.getGalleryCustom(statusData
				.getCurrentService());

		// Set Gallery
//		TabSelector.setImageResIdsAndListTitle(resIds, list);

	}

	public void startAutoCheckTimer() {
	
		Intent intent = new Intent(this, NotificationTimerReceiver.class);
		intent.setAction(NotificationService.ACTION_NOTIFICATION);
	
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		
		String notificationIntervalString = PreferenceManager.getDefaultSharedPreferences(this).getString("notification_time_selection", IGeneral.NOTIFICATION_TIME);
		long notificationInterval = 0;
		try {
			notificationInterval = Long.valueOf(notificationIntervalString);
		} catch (NumberFormatException e) {
			notificationInterval = Long.valueOf(IGeneral.NOTIFICATION_TIME);
		}
		
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + 5000, notificationInterval, sender);
	
	}
	
	public Context getContext() {
		return getContext();
	}

	private void initData() {
	
		statusData = new StatusData(this);
		accountList = new AccountList(this, statusData);
		keywordFilterList = new KeywordFilterList(this);
		userFilterList = new UserFilterList(this);
		translationList = new TranslationList(this);
		settingData = new SettingData(this);
		listInfoList = new ListInfoList(this);
	
		// Refresh
		// refreshImageRes();
	
	}

	private void initCommHandler() {
	
		// CFB
		CfbCommHandler.setAppContext(this);
	
		// Sina
		SinaCommHandler.setAppContext(this);
	
		// Twitter
		TwitterCommHandler.setAppContext(this);
		
		//Twitter -proxy
		TwitterProxyCommHandler.setAppContext(this);
		
		// Tencent
		TencentCommHandler.setAppContext(this);
		
		// Sohu
		SohuCommHandler.setAppContext(this);
		
		//RenRen
		RenRenCommHandler.setAppContext(this);
		
	}
	

}
