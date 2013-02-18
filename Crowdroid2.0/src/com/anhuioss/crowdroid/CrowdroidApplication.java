package com.anhuioss.crowdroid;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;
import com.anhuioss.crowdroid.data.KeywordFilterList;
import com.anhuioss.crowdroid.data.ListInfoList;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.TranslationList;
import com.anhuioss.crowdroid.data.UserFilterList;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.service.cfb.CfbCommHandler;
import com.anhuioss.crowdroid.service.renren.RenRenCommHandler;
import com.anhuioss.crowdroid.service.sina.SinaCommHandler;
import com.anhuioss.crowdroid.service.sohu.SohuCommHandler;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;
import com.anhuioss.crowdroid.service.twitter.TwitterCommHandler;
import com.anhuioss.crowdroid.service.twitterproxy.TwitterProxyCommHandler;
import com.anhuioss.crowdroid.service.wangyi.WangyiCommHandler;
import com.anhuioss.crowdroid.util.Constants;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;

public class CrowdroidApplication extends Application {

	private SettingData settingData;

	private StatusData statusData;

	private AccountList accountList;

	private TranslationList translationList;

	private KeywordFilterList keywordFilterList;

	private UserFilterList userFilterList;

	private ListInfoList listInfoList;

	private float scaleDensity = 1.0f;

	// map
	private static CrowdroidApplication instance;

	private BMapManager mBMapMan = null;

	/** 0=timeline 1=comment 2=followed/unfollowed */
	private boolean isComeFromNotification[] = { false, false, false };

	/** 0=timeline 1=comment 2=followed/unfollowed */
	public void setIsComeFromNotification(int type) {
		if (type == 0 || type == 1 || type == 2) {
			this.isComeFromNotification[type] = true;
		}
	}

	public boolean isComeFromNotification(int type) {
		if (type == 0 || type == 1 || type == 2) {
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

		// startAutoCheckTimer();

		// CrashHandler crashHandler = CrashHandler.getInstance();
		// // 注册crashHandler
		// crashHandler.init(getApplicationContext());

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

	// public void refreshImageRes() {
	//
	// // Prepare List Data
	// AccountData account = accountList.getCurrentAccount();
	// ArrayList<ListInfo> list = listInfoList
	// .getListsByUserName(account == null ? "" : account
	// .getUserName());
	//
	// // Prepare Gallery Data
	// int[] resIds = settingData.getGalleryCustom(statusData
	// .getCurrentService());
	// }

	// public void startAutoCheckTimer() {
	//
	// Intent intent = new Intent(this, NotificationTimerReceiver.class);
	// intent.setAction(NotificationService.ACTION_NOTIFICATION);
	//
	// PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
	// AlarmManager alarmManager = (AlarmManager) this
	// .getSystemService(Context.ALARM_SERVICE);
	//
	// String notificationIntervalString = PreferenceManager
	// .getDefaultSharedPreferences(this).getString(
	// "notification_time_selection",
	// IGeneral.NOTIFICATION_TIME);
	// long notificationInterval = 0;
	// try {
	// if (settingData.isNotification()) {
	// notificationInterval = Long.valueOf(notificationIntervalString);
	// } else if (settingData.isAutoBroadcast()) {
	// notificationInterval = Long
	// .valueOf(IGeneral.NOTIFICATION_BROADCAST_TIME);
	// }
	//
	// } catch (NumberFormatException e) {
	// notificationInterval = Long.valueOf(IGeneral.NOTIFICATION_TIME);
	// }
	//
	// alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
	// SystemClock.elapsedRealtime() + 5000, notificationInterval,
	// sender);
	//
	// }

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
		instance = this;
		mBMapMan = new BMapManager(this);
		mBMapMan.init(Constants.API_KEY_FOR_BAIDU_MAP, new MyGeneralListener());

	}

	private void initCommHandler() {

		// CFB
		CfbCommHandler.setAppContext(this);

		// Sina
		SinaCommHandler.setAppContext(this);

		// Twitter
		TwitterCommHandler.setAppContext(this);

		// Twitter -proxy
		TwitterProxyCommHandler.setAppContext(this);

		// Tencent
		TencentCommHandler.setAppContext(this);

		// Sohu
		SohuCommHandler.setAppContext(this);

		// RenRen
		RenRenCommHandler.setAppContext(this);
		// 163
		WangyiCommHandler.setAppContext(this);

	}

	// 在初始化地图Activity时，注册一般事件监听，并实现MKGeneralListener的接口处理相应事件
	public static class MyGeneralListener implements MKGeneralListener {
		// 返回网络错误，通过错误代码判断原因，MKEvent中常量值。
		public void onGetNetworkState(int arg0) {
			Log.d("MyGeneralListener", "onGetNetworkState error is " + arg0);
			Toast.makeText(CrowdroidApplication.getInstance(),
					"GetNetworkState", Toast.LENGTH_LONG).show();
		}

		// 返回授权验证错误，通过错误代码判断原因，MKEvent中常量值。
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "
					+ iError);
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				Toast.makeText(CrowdroidApplication.getInstance(),
						"permissionDenied", Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	public void onTerminate() {
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}

	public static CrowdroidApplication getInstance() {
		return instance;
	}

	public BMapManager getMapManager() {
		return mBMapMan;
	}

}
