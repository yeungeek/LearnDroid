package com.anhuioss.crowdroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.TipInfo;
import com.anhuioss.crowdroid.dialog.DownloadUpdateDialog;
import com.anhuioss.crowdroid.dialog.LicenseDialog;
import com.anhuioss.crowdroid.dialog.TipsDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.service.cfb.CfbCommHandler;
import com.anhuioss.crowdroid.service.cfb.CfbParseHandler;
import com.anhuioss.crowdroid.service.renren.RenRenCommHandler;
import com.anhuioss.crowdroid.service.renren.RenRenParserHandler;
import com.anhuioss.crowdroid.service.sina.SinaCommHandler;
import com.anhuioss.crowdroid.service.sina.SinaParserHandler;
import com.anhuioss.crowdroid.service.sohu.SohuCommHandler;
import com.anhuioss.crowdroid.service.sohu.SohuParserHandler;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;
import com.anhuioss.crowdroid.service.tencent.TencentParserHandler;
import com.anhuioss.crowdroid.service.twitter.TwitterCommHandler;
import com.anhuioss.crowdroid.service.twitterproxy.TwitterProxyCommHandler;
import com.anhuioss.crowdroid.settings.AddCrowdroidForBusinessAccountActivity;
import com.anhuioss.crowdroid.settings.AddRenRenAccountActivity;
import com.anhuioss.crowdroid.settings.AddSinaAccountActivity;
import com.anhuioss.crowdroid.settings.AddSohuAccountActivity;
import com.anhuioss.crowdroid.settings.AddTencentAccountActivity;
import com.anhuioss.crowdroid.settings.AddTwitterAccountActivity;
import com.anhuioss.crowdroid.settings.AddTwitterProxyAccountActivity;
import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.MyUncaughtExceptionHandler;

public class LoginActivity extends Activity implements OnClickListener,
		ServiceConnection {

	private AnimationSet manimationSet;

	private ImageView login_cfb_icon;

	/** Text */
	private TextView textService;

	private ImageView service_icon;

	// private TextView textCopyRight;
	/** Gallery */
	private Gallery galleryService;

	/** Spinner */
	private Spinner spinnerService;

	/** Button */
	private Button buttonLogin;

	private Button cfbRegister;

	private PopupWindow window;

	private TextView cfb_detail;

	private Dialog dialog;

	/** ImageView */
	// private ImageView loginBackgroundImage;

	/** The Position For Account */
	private int[] accountIndex;

	/** Service Name */
	private String[] serviceName;

	public static final String API_SERVICE_NAME = ".communication.ApiService";

	/** API Service Interface **/
	private ApiServiceInterface apiServiceInterface;

	/** The Images For Gallery */
	private final int[] mImageIds;

	{
		// if (IGeneral.APPLICATION_MODE == 0) {
		// accountIndex = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		//
		// serviceName = new String[] { "Crowdroid for Business", "Follow5",
		// "Twitter", "Twitter-Proxy", "Sina", "Tencent", "Sohu"};
		//
		// mImageIds = new int[] { R.drawable.icon, R.drawable.follow5,
		// R.drawable.twitter, R.drawable.twitter_proxy,
		// R.drawable.sina, R.drawable.tencent, R.drawable.sohu };
		//
		// } else if (IGeneral.APPLICATION_MODE == 1) {
		// accountIndex = new int[] { 0, 0, 0, 0 ,0};
		//
		// serviceName = new String[] { "Crowdroid for Business", "Follow5",
		// "Sina", "Tencent","Sohu" };
		//
		// mImageIds = new int[] { R.drawable.icon, R.drawable.follow5,
		// R.drawable.sina, R.drawable.tencent,R.drawable.sohu };
		// }
		if (IGeneral.APPLICATION_MODE == 0) {
			accountIndex = new int[] { 0, 0, 0, 0, 0, 0, 0 };

			serviceName = new String[] { "Crowdroid for Business", "Twitter",
					"Twitter-Proxy", "Sina", "Tencent", "Sohu", "RenRen" };

			mImageIds = new int[] { R.drawable.icon, R.drawable.twitter,
					R.drawable.twitter_proxy, R.drawable.sina,
					R.drawable.tencent, R.drawable.sohu, R.drawable.renren };

		} else if (IGeneral.APPLICATION_MODE == 1) {
			accountIndex = new int[] { 0, 0, 0, 0, 0 };

			serviceName = new String[] { "Crowdroid for Business", "Sina",
					"Tencent", "Sohu", "RenRen" };

			mImageIds = new int[] { R.drawable.icon, R.drawable.sina,
					R.drawable.tencent, R.drawable.sohu, R.drawable.renren };
		}
	}

	/** Current Position For Service */
	private int lastServiceId = 0;

	private boolean isRefreshLastPosition = false;

	/** Adapter */
	private ArrayAdapter<String> spinnerServiceAdapter;

	/** Data For Spinner */
	private List<String> spinnerServiceData = new ArrayList<String>();

	/** ImageButton For Add */
	private Button mButtonAddAccount;

	private ImageView mImageServerTwitter;

	private ImageView mImageServerTwitterProxy;

	private ImageView mImageServerSina;

	private ImageView mImageServerTencent;

	private ImageView mImageServerSohu;

	private ImageView mImageServerRenRen;

	// /** Text For Help */
	private TextView textHelp;

	// /** Text For License */
	private TextView textLicense;

	private AccountList accountList;

	private StatusData statusData;

	private CrowdroidApplication crowdroidApplication;

	private SettingData settingData;

	private Resources resources = null;
	private Configuration config = null;
	private DisplayMetrics dm = null;

	SpannableString spanString = null;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {
			if (statusCode != null && statusCode.equals("200")) {
				ParseHandler parseHandler = new ParseHandler();
				String maxInputCharactor = (String) parseHandler.parser(
						service, type, statusCode, message);
				SharedPreferences status = getSharedPreferences("status",
						Context.MODE_PRIVATE);
				status.edit()
						.putString("max_input_charactor", maxInputCharactor)
						.commit();

			} else {
				Toast.makeText(
						LoginActivity.this,
						ErrorMessage.getErrorMessage(LoginActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();

		settingData = crowdroidApplication.getSettingData();

		settingSpeechLanguage(settingData.getSpeechLanguage());

		setContentView(R.layout.activity_login0);

		// String language = this.getResources().getConfiguration().locale
		// .getLanguage();
		// if (language != null && language.equals("ja")) {
		// setContentView(R.layout.activity_login_japanese);// } else {
		// setContentView(R.layout.activity_login);
		// }

		// Set Error Handler
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(
				this));

		// Display Dialog if Error has occurred last time.
		MyUncaughtExceptionHandler.showBugReportDialogIfExist(this);

		// Get Account List And Status Data

		accountList = crowdroidApplication.getAccountList();

		statusData = crowdroidApplication.getStatusData();

		// Find Views

		service_icon = (ImageView) findViewById(R.id.service_icon);
		textService = (TextView) findViewById(R.id.serviceName);
		galleryService = (Gallery) findViewById(R.id.gallery0);
		spinnerService = (Spinner) findViewById(R.id.spinner_service);
		buttonLogin = (Button) findViewById(R.id.login);
		mButtonAddAccount = (Button) findViewById(R.id.manage_account);
		cfb_detail = (TextView) findViewById(R.id.cfb_detail);

		login_cfb_icon = (ImageView) findViewById(R.id.login_cfb_icon);
		String language = Locale.getDefault().getLanguage();
		if (language.equals("en")) {
			login_cfb_icon
					.setBackgroundResource(R.drawable.login_background_02_en);
		} else if (language.equals("zh")) {
			login_cfb_icon
					.setBackgroundResource(R.drawable.login_background_02_cn);
		} else if (language.equals("ja")) {
			login_cfb_icon
					.setBackgroundResource(R.drawable.login_background_02_jp);
		}
		// cfbRegister = (Button) findViewById(R.id.cfb_register);
		// mImageServerTwitter = (ImageView)
		// findViewById(R.id.image_server_twitter);
		// mImageServerTwitterProxy = (ImageView)
		// findViewById(R.id.image_server_twitter_proxy);
		// mImageServerSina = (ImageView) findViewById(R.id.image_server_sina);
		// mImageServerTencent = (ImageView)
		// findViewById(R.id.image_server_tencent);
		// mImageServerSohu = (ImageView) findViewById(R.id.image_server_sohu);
		// mImageServerRenRen = (ImageView)
		// findViewById(R.id.image_server_renren);

		// textCopyRight = (TextView)
		// findViewById(R.id.text_copy_right_company);
		//
		// String htmlLinkText = "<a href=\"http://www.anhuioss.com/cn\"><u>"
		// + " " + getString(R.string.company_name) + " " + "</u></a>";
		// textCopyRight.setText(Html.fromHtml(htmlLinkText));
		// textCopyRight.setMovementMethod(LinkMovementMethod.getInstance());
		// String copyRight = "Copyright©2009-2012 "
		// + getString(R.string.company_name) + " "
		// + getString(R.string.right_reserved);
		// spanString = new SpannableString(copyRight);
		// spanString.setSpan(new TypefaceSpan("serif"), 0, 19,
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// spanString.setSpan(new URLSpan("http://www.anhuioss.com/cn"), 20,
		// 20 + getString(R.string.company_name).length(),
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// spanString.setSpan(new TypefaceSpan("serif"),
		// 21 + getString(R.string.company_name).length(),
		// copyRight.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// //
		// textCopyRight.setText(spanString);
		// textCopyRight.setMovementMethod(LinkMovementMethod.getInstance());

		mButtonAddAccount.setOnClickListener(this);
		buttonLogin.setOnClickListener(this);
		cfb_detail.setOnClickListener(this);
		// cfbRegister.setOnClickListener(this);
		// mImageServerTwitter.setOnClickListener(this);
		// mImageServerTwitterProxy.setOnClickListener(this);
		// mImageServerSina.setOnClickListener(this);
		// mImageServerTencent.setOnClickListener(this);
		// mImageServerSohu.setOnClickListener(this);
		// mImageServerRenRen.setOnClickListener(this);

		galleryService.setSelection(mImageIds.length / 2);
		// Set Adapter
		galleryService.setAdapter(new ImageAdapter(LoginActivity.this));

		// galleryService.setOnItemSelectedListener(this);
		galleryService.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				int mPivotX;
				int mPivotY;
				try {
					mPivotX = view.getWidth() / 2;
					mPivotY = view.getHeight() / 2;
					AnimationSet animationSet = new AnimationSet(true);
					if (manimationSet != null && manimationSet != animationSet) {
						Animation rotateAnimation = new RotateAnimation(0F,
								360F, mPivotX, mPivotY);
						rotateAnimation.setDuration(1000);
						ScaleAnimation scaleAnimation = new ScaleAnimation(2,
								0.5f, 2, 0.5f, Animation.RELATIVE_TO_SELF,
								0.5f, // 使用动画播放图片
								Animation.RELATIVE_TO_SELF, 0.5f);
						scaleAnimation.setDuration(1000);
						manimationSet.addAnimation(rotateAnimation);
						manimationSet.addAnimation(scaleAnimation);
						manimationSet.setFillAfter(true); // 让其保持动画结束时的状态。
						view.startAnimation(manimationSet);
					}
					// Animation rotateAnimation = new RotateAnimation(0F,360F,
					// mPivotX, mPivotY);
					// rotateAnimation.setDuration(1000);
					ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.5f,
							1, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					scaleAnimation.setDuration(1000);
					// animationSet.addAnimation(rotateAnimation);
					animationSet.addAnimation(scaleAnimation);
					animationSet.setFillAfter(true);
					view.startAnimation(animationSet);
					manimationSet = animationSet;

					if (isRefreshLastPosition) {
						refresh(lastServiceId);
						isRefreshLastPosition = false;
					} else {
						refresh(position);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				{

				}
			}

			//
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		// // Set Item Click Listener For Gallery
		galleryService.setOnItemClickListener(new OnItemClickListener() {
			// //
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				refresh(position);

			}
		});

		// Create Adapter
		spinnerServiceAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinnerServiceData);
		spinnerServiceAdapter
				.setDropDownViewResource(R.layout.spinner_dropdown_item);

		// Set Adapter
		spinnerService.setAdapter(spinnerServiceAdapter);

		// Set Item Selected Listener For Spinner
		spinnerService.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				accountIndex[lastServiceId] = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		// Show Tip
		showTips();
		// Show Update
		showCheckUpdateDialog();
	}

	@Override
	public void onStart() {
		super.onStart();

		isRefreshLastPosition = true;

		SharedPreferences status = getSharedPreferences("status",
				Context.MODE_PRIVATE);
		lastServiceId = status.getInt("last_service_id", 0);
		accountIndex[lastServiceId] = status.getInt("last_account_id", 0);

		refresh(lastServiceId);

		initView();

		if ((IGeneral.APPLICATION_MODE == 1 && lastServiceId == 1)
				&& !(accountList
						.getAccountByService(IGeneral.SERVICE_NAME_SINA)
						.isEmpty())) {
			boolean b = getIntent().getBooleanExtra("autoLogin", true);
			if (b) {
				checkData();
			}

		}
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();

		SharedPreferences status = getSharedPreferences("status",
				Context.MODE_PRIVATE);
		status.edit().putInt("last_service_id", lastServiceId).commit();
		status.edit().putInt("last_account_id", accountIndex[lastServiceId])
				.commit();

		unbindService(this);
	}

	// -------------------------------------------------------------------
	/**
	 * Refresh UI
	 * 
	 * @param position
	 *            of the image in the gallery
	 */
	// -------------------------------------------------------------------
	private void refresh(int position) {

		// Set Service Id
		lastServiceId = position;

		// Set Last Position
		spinnerService.setSelection(accountIndex[lastServiceId]);
		galleryService.setSelection(lastServiceId);

		// Enable Button
		// buttonLogin.setEnabled(true);

		// Service Name
		String service = serviceName[lastServiceId];

		// Set Service Name
		textService.setText(service);

		service_icon.setImageResource(mImageIds[position]);

		// Clear Data
		spinnerServiceData.clear();

		// Prepare Data For Spinner
		switch (mImageIds[lastServiceId]) {
		case R.drawable.icon: {

			// CFB
			ArrayList<AccountData> cfbAccountList = accountList
					.getAccountByService(service);
			for (AccountData cfbAccount : cfbAccountList) {
				spinnerServiceData.add(cfbAccount.getUserName() + "\n["
						+ getString(R.string.server) + ":"
						+ cfbAccount.getApiUrl() + "]");
			}
			break;
		}
		case R.drawable.twitter: {

			// Twitter
			ArrayList<AccountData> twitterAccountList = accountList
					.getAccountByService(service);
			for (AccountData twitterAccount : twitterAccountList) {
				spinnerServiceData.add(twitterAccount.getUserName());
			}
			break;
		}
		case R.drawable.twitter_proxy: {

			// Twitter-Proxy
			ArrayList<AccountData> twitterProxyAccountList = accountList
					.getAccountByService(service);
			for (AccountData twitterProxyAccount : twitterProxyAccountList) {
				spinnerServiceData.add(twitterProxyAccount.getUserName());
			}
			break;
		}
		case R.drawable.sina: {
			// Sina
			ArrayList<AccountData> sinaAccountList = accountList
					.getAccountByService(service);
			for (AccountData sinaAccount : sinaAccountList) {
				spinnerServiceData.add(sinaAccount.getUserScreenName());
			}
			break;
		}
		case R.drawable.tencent: {
			// Tencent
			ArrayList<AccountData> tencentAccountList = accountList
					.getAccountByService(service);
			for (AccountData tencentAccount : tencentAccountList) {
				spinnerServiceData.add(tencentAccount.getUserName());
			}
			break;
		}
		case R.drawable.sohu: {
			// Sohu
			ArrayList<AccountData> sohuAccountList = accountList
					.getAccountByService(service);
			for (AccountData sohuAccount : sohuAccountList) {
				spinnerServiceData.add(sohuAccount.getUserName());
			}
			break;
		}
		case R.drawable.renren: {
			// RenRen
			ArrayList<AccountData> renrenAccountList = accountList
					.getAccountByService(service);
			for (AccountData renrenAccount : renrenAccountList) {
				spinnerServiceData.add(renrenAccount.getUserName());
			}
			break;
		}
		default: {

		}
		}

		// Notify
		spinnerServiceAdapter.notifyDataSetChanged();

	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mImageIds.length;
		}

		// 获取图片在库中的位置
		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new Gallery.LayoutParams(100, 100));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(18, 18, 18, 18);
			} else {
				imageView = (ImageView) convertView;
			}
			// 给ImageView设置资源
			imageView.setImageResource(mImageIds[position]);

			return imageView;
		}

	}

	@Override
	public void onWindowAttributesChanged(
			android.view.WindowManager.LayoutParams params) {
		super.onWindowAttributesChanged(params);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		int height = getWindowManager().getDefaultDisplay().getHeight();

		if (IGeneral.isHoliday) {

			if (width >= height) {
				// 设置默认3/2图片

			}
			if (width < height) {
				// 设置默认3/2图片

			}
			if (width / height == 4 / 3) {
				// 设置4/3图片

			}
			if (width / height == 3 / 4) {
				// 设置4/3图片

			}
			if (width / height == 5 / 3) {
				// 设置5/3图片

			}
			if (width / height == 3 / 5) {
				// 设置5/3图片

			}
			if (width / height == 8 / 5) {
				// 设置8/5图片

			}
			if (width / height == 5 / 8) {
				// 设置8/5图片

			}

		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.imageButton_add_account: {

			// Add Account
			dialogSelectService();
			break;

		}
		case R.id.login: {

			// Login Check
			checkData();
			break;

		}
		case R.id.manage_account: {
			selectAccountDialog();
			break;
		}
		case R.id.add_cfb: {

			Intent intent = new Intent(LoginActivity.this,
					AddCrowdroidForBusinessAccountActivity.class);
			startActivity(intent);
			dialog.dismiss();
			break;
		}
		case R.id.add_twitter: {

			Intent intent = new Intent(LoginActivity.this,
					AddTwitterAccountActivity.class);
			startActivity(intent);
			dialog.dismiss();
			break;
		}
		case R.id.add_twitterP: {

			Intent intent = new Intent(LoginActivity.this,
					AddTwitterProxyAccountActivity.class);
			startActivity(intent);
			dialog.dismiss();
			break;
		}
		case R.id.add_sina: {

			Intent intent = new Intent(LoginActivity.this,
					AddSinaAccountActivity.class);
			startActivity(intent);
			dialog.dismiss();
			break;
		}
		case R.id.add_tencent: {

			Intent intent = new Intent(LoginActivity.this,
					AddTencentAccountActivity.class);
			startActivity(intent);
			dialog.dismiss();
			break;
		}
		case R.id.add_sohu: {

			Intent intent = new Intent(LoginActivity.this,
					AddSohuAccountActivity.class);
			startActivity(intent);
			dialog.dismiss();
			break;
		}
		case R.id.add_renren: {
			dialog.dismiss();
			Intent intent = new Intent(LoginActivity.this,
					AddRenRenAccountActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.cancel: {
			dialog.dismiss();
			break;
		}
		case R.id.cfb_detail: {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			String language = Locale.getDefault().getLanguage();
			Uri content_url = Uri
					.parse("http://www.anhuioss.com/cn/crowdroidbiz/");
			if (language.equals("en")) {
				content_url = Uri
						.parse("http://www.anhuioss.com/en/crowdroidbiz/");
			} else if (language.equals("ja")) {
				content_url = Uri
						.parse("http://www.anhuioss.com/crowdroidbiz/");
			}

			intent.setData(content_url);
			startActivity(intent);
		}
		default: {
		}
		}

	}

	private void checkData() {

		if (spinnerServiceData.isEmpty()) {
			Toast.makeText(LoginActivity.this,
					getResources().getString(R.string.no_account),
					Toast.LENGTH_SHORT).show();
		} else {
			String name = (String) spinnerService
					.getItemAtPosition(accountIndex[lastServiceId]);
			String service = serviceName[lastServiceId];
			String serviceIp = null;
			if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(service)) {
				int lastIndex = name.indexOf("\n[");
				if (lastIndex != -1) {
					serviceIp = name.substring(name.lastIndexOf(":") + 1,
							name.length() - 1);
					name = name.substring(0, lastIndex);
				}
			}
			// Check Account
			checkAccount(name, service, serviceIp);
		}
	}

	private void checkAccount(String name, String service, String serviceIp) {

		AccountData data = null;

		// Init Comm Handler(set Account Data)
		if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			data = accountList.getAccountByServiceAndName(service, name);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", data.getUserName());
			map.put("password", data.getPassword());
			CfbCommHandler.setAccount(data.getUserName(), data.getPassword(),
					serviceIp);
			// data.getApiUrl());
			try {
				apiServiceInterface.request(service,
						CommHandler.TYPE_GET_CFB_SETTING, apiServiceListener,
						map);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
			data = accountList.getAccountByServiceAndName(service, name);
			SinaCommHandler.setAccount(data.getAccessToken(),
					data.getTokenSecret());
		} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
			data = accountList.getAccountByServiceAndName(service, name);
			TwitterCommHandler.setAccount(data.getAccessToken(),
					data.getTokenSecret(), data.getUid());
		} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			data = accountList.getAccountByServiceAndName(service, name);
			TwitterProxyCommHandler.setAccount(data.getUserName(),
					data.getPassword(), data.getApiUrl());
		} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
			data = accountList.getAccountByServiceAndName(service, name);
			TencentCommHandler.setAccount(data.getAccessToken(),
					data.getTokenSecret());
		} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
			data = accountList.getAccountByServiceAndName(service, name);
			SohuCommHandler.setAccount(data.getAccessToken(),
					data.getTokenSecret());
		} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
			data = accountList.getAccountByServiceAndName(service, name);
			RenRenCommHandler.setAccount(data.getAccessToken());
		}

		recordedData(data, service);
	}

	private void recordedData(AccountData data, String service) {

		String lastService = PreferenceManager
				.getDefaultSharedPreferences(this)
				.getString("last-service", "");
		if (!service.equals(lastService)) {
			// Clear data for emotions
			SendMessageActivity.htmlDataListForEmotion = null;
			SendMessageActivity.emotionList = new ArrayList<EmotionInfo>();
			TimelineActivity.userImageMap.clear();
			System.gc();
		}

		// Record
		statusData.setCurrentService(service);
		statusData.setCurrentUid(data.getUid());

		String name = (String) spinnerService
				.getItemAtPosition(accountIndex[lastServiceId]);
		if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(service)) {
			int lastIndex = name.indexOf("\n[");
			if (lastIndex != -1) {
				name = name.substring(0, lastIndex);
			}
		}
		AccountData account = accountList.getAccountByServiceAndName(service,
				name);
		account.setService(service);
		account.setUid(data.getUid());
		account.setUserName(data.getUserName());
		account.setUserScreenName(data.getUserScreenName());

		if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(lastService)) {
			String lastApiUrl = PreferenceManager.getDefaultSharedPreferences(
					this).getString("cfb-api-url", "");
			if (!lastApiUrl.equals(account.getApiUrl())) {
				TimelineActivity.userImageMap.clear();
				System.gc();
			}
		}

		Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
				.edit();
		editor.putString("last-service", service);
		editor.putString("cfb-api-url", account.getApiUrl());
		editor.commit();

		statusData.setNewestAtMessageId(account.getLastAtMessageId());
		statusData.setNewestDirectMessageId(account.getLastDirectMessageId());
		statusData.setNewestGeneralMessageId(account.getLastGeneralMessageId());
		statusData.setNewestRetweetOfMeMessageId(account
				.getLastRetweetOfMeMessageId());

		if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
			account.setUserName((String) spinnerService
					.getItemAtPosition(accountIndex[lastServiceId]));
		}

		getHtmlDataForEmotion();

		// Login
		login();

	}

	private void login() {

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		crowdroidApplication.refreshImageRes();

		Intent intent = new Intent(LoginActivity.this,
				HomeTimelineActivity.class);
		startActivity(intent);

	}

	private void initView() {
		// if (IGeneral.APPLICATION_MODE == 0) {
		// findViewById(R.id.image_server_twitter).setVisibility(View.VISIBLE);
		// findViewById(R.id.image_server_twitter_proxy).setVisibility(
		// View.VISIBLE);
		// } else if (IGeneral.APPLICATION_MODE == 1) {
		// findViewById(R.id.image_server_twitter).setVisibility(View.GONE);
		// findViewById(R.id.image_server_twitter_proxy).setVisibility(
		// View.GONE);
		// }
	}

	private void dialogSelectService() {

		LayoutInflater factory = LayoutInflater.from(LoginActivity.this);
		final View textEntryView = factory.inflate(
				R.layout.dialog_select_service, null);
		final Spinner service = (Spinner) textEntryView
				.findViewById(R.id.spinner_service);
		ArrayAdapter<CharSequence> adapter = null;
		if (IGeneral.APPLICATION_MODE == 0) {
			adapter = ArrayAdapter
					.createFromResource(LoginActivity.this,
							R.array.services_more,
							android.R.layout.simple_spinner_item);
		} else if (IGeneral.APPLICATION_MODE == 1) {
			adapter = ArrayAdapter.createFromResource(LoginActivity.this,
					R.array.service_less, android.R.layout.simple_spinner_item);
		}

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		service.setAdapter(adapter);
		AlertDialog dlg = new AlertDialog.Builder(LoginActivity.this)
				.setTitle(R.string.add)
				.setView(textEntryView)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								switch (service.getSelectedItemPosition()) {

								case 0: {

									if (IGeneral.APPLICATION_MODE == 0) {
										// Add Twitter Account
										Intent intent = new Intent(
												LoginActivity.this,
												AddCrowdroidForBusinessAccountActivity.class);
										startActivity(intent);
									} else if (IGeneral.APPLICATION_MODE == 1) {
										Intent intent = new Intent(
												LoginActivity.this,
												AddCrowdroidForBusinessAccountActivity.class);
										startActivity(intent);
									}

									break;
								}

								case 1: {

									if (IGeneral.APPLICATION_MODE == 0) {
										// Add Twitter Account
										Intent intent = new Intent(
												LoginActivity.this,
												AddTwitterAccountActivity.class);
										startActivity(intent);
									} else if (IGeneral.APPLICATION_MODE == 1) {
										Intent intent = new Intent(
												LoginActivity.this,
												AddSinaAccountActivity.class);
										startActivity(intent);
									}

									break;
								}
								case 2: {

									if (IGeneral.APPLICATION_MODE == 0) {
										// Add Twitter-Proxy Account
										Intent intent = new Intent(
												LoginActivity.this,
												AddTwitterProxyAccountActivity.class);
										startActivity(intent);
									} else if (IGeneral.APPLICATION_MODE == 1) {
										Intent intent = new Intent(
												LoginActivity.this,
												AddTencentAccountActivity.class);
										startActivity(intent);
									}

									break;
								}
								case 3: {

									if (IGeneral.APPLICATION_MODE == 0) {
										// Add Sina Account
										Intent intent = new Intent(
												LoginActivity.this,
												AddSinaAccountActivity.class);
										startActivity(intent);
									} else if (IGeneral.APPLICATION_MODE == 1) {
										// Add Sohu Account
										Intent intent = new Intent(
												LoginActivity.this,
												AddSohuAccountActivity.class);
										startActivity(intent);
									}
									break;
								}
								case 4: {
									// Add Tencent Account
									Intent intent = new Intent(
											LoginActivity.this,
											AddTencentAccountActivity.class);
									startActivity(intent);
									break;
								}
								case 5: {
									// Add Sohu Account
									Intent intent = new Intent(
											LoginActivity.this,
											AddSohuAccountActivity.class);
									startActivity(intent);
									break;

								}
								case 6: {
									// Add RenRen Account
									Intent intent = new Intent(
											LoginActivity.this,
											AddRenRenAccountActivity.class);
									startActivity(intent);
									break;
								}
								default: {

								}
								}

							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						}).create();
		dlg.show();

	}

	private void selectAccountDialog() {
		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = lay.inflate(R.layout.service_select_popwindow, null);
		viewholder holder = new viewholder();
		holder.cfb = (ImageView) v.findViewById(R.id.add_cfb);
		holder.twitter = (ImageView) v.findViewById(R.id.add_twitter);
		holder.twitter_proxy = (ImageView) v.findViewById(R.id.add_twitterP);
		holder.sina = (ImageView) v.findViewById(R.id.add_sina);
		holder.tencent = (ImageView) v.findViewById(R.id.add_tencent);
		holder.sohu = (ImageView) v.findViewById(R.id.add_sohu);
		holder.renren = (ImageView) v.findViewById(R.id.add_renren);
		holder.cancel = (Button) v.findViewById(R.id.cancel);

		if (IGeneral.APPLICATION_MODE == 1) {
			holder.twitter.setVisibility(View.GONE);
			holder.twitter_proxy.setVisibility(View.GONE);
		}

		holder.cfb.setOnClickListener(this);
		holder.twitter.setOnClickListener(this);
		holder.twitter_proxy.setOnClickListener(this);
		holder.sina.setOnClickListener(this);
		holder.tencent.setOnClickListener(this);
		holder.sohu.setOnClickListener(this);
		holder.renren.setOnClickListener(this);
		holder.cancel.setOnClickListener(this);

		dialog = new Dialog(this, R.style.custom_dialog_style);
		WindowManager manager = dialog.getWindow().getWindowManager();
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		dialog.getWindow().setLayout(width * 9 / 10, height * 1 / 2);
		dialog.setContentView(v);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	static class viewholder {
		ImageView cfb;
		ImageView twitter;
		ImageView twitter_proxy;
		ImageView sina;
		ImageView tencent;
		ImageView sohu;
		ImageView renren;
		Button cancel;

	}

	private void showTips() {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		String version = prefs.getString("version", "0");
		String isShowTip = prefs.getString("is-show-tip", "true");

		PackageManager pm = getPackageManager();
		PackageInfo pInfo;
		try {
			pInfo = pm
					.getPackageInfo(getPackageName(),
							PackageManager.GET_ACTIVITIES
									| PackageManager.GET_SERVICES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return;
		}

		editor.putString("version", String.valueOf(pInfo.versionCode));
		editor.commit();
		if (version.equals(String.valueOf(pInfo.versionCode))) {

			if (isShowTip.equals("true")) {
				// Open Tip
				TipsDialog dialog = new TipsDialog(this);
				dialog.show();
			}
		} else {
			// Open Tip
			TipsDialog dialog = new TipsDialog(this);
			dialog.show();
		}

		// showCheckUpdateDialog();
	}

	private void showCheckUpdateDialog() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		String version = prefs.getString("version", "0");
		String isShowUpdate = prefs.getString("is-show-update", "true");

		PackageManager pm = getPackageManager();
		PackageInfo pInfo;
		try {
			pInfo = pm
					.getPackageInfo(getPackageName(),
							PackageManager.GET_ACTIVITIES
									| PackageManager.GET_SERVICES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return;
		}

		// --------------- auto detct update-------------------
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<TipInfo> tipInfoList = new ArrayList<TipInfo>();

		CommResult comResult = CfbCommHandler.getVersionMessage(map);
		if (comResult.getMessage() != null) {
			tipInfoList = CfbParseHandler.parseVersionMessage(comResult
					.getMessage());

			int maxCode = 0;
			for (TipInfo tip : tipInfoList) {

				if ("normal".equals(tip.getVersionType())) {
					maxCode = Integer.valueOf(tip.getVersionCode());
					if (maxCode > Integer.valueOf(version)) {
						if (version.equals(String.valueOf(pInfo.versionCode))) {

							if (isShowUpdate.equals("true")) {
								new DownloadUpdateDialog(LoginActivity.this)
										.show();
							}
						} else {
							editor.putString("version",
									String.valueOf(pInfo.versionCode));
							editor.commit();
							new DownloadUpdateDialog(LoginActivity.this).show();
						}
					}
				} else if ("special".equals(tip.getVersionType())) {
				}
			}
		} else {
			Toast.makeText(LoginActivity.this,
					getString(R.string.error_msg_503), Toast.LENGTH_SHORT)
					.show();
		}
	}

	// ---------------------------------------------------------
	/**
	 * get magnification
	 */
	// ---------------------------------------------------------
	private float getMagnification() {

		float magnification = 0;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) this).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		float width = (float) displayMetrics.widthPixels;
		float height = (float) displayMetrics.heightPixels;

		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

			// Vertical
			if (height - width * 1.5 > 0) {
				magnification = width / 320;
			} else {
				magnification = height / 480;
			}

		} else {
			// Horizontal
			if (height * 1.5 - width > 0) {
				magnification = width / 480;
			} else {
				magnification = height / 320;
			}
		}

		return magnification;
	}

	private void getHtmlDataForEmotion() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (SendMessageActivity.htmlDataListForEmotion == null) {
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
						CommResult commResult = CfbCommHandler.getEmotions(null);
						String responseCode = commResult.getResponseCode();
						String message = commResult.getMessage();
						if (responseCode != null && responseCode.equals("200")) {
							SendMessageActivity.emotionList = CfbParseHandler
									.parseEmotions(message);
							getEmotionDataForWebView();
						}
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SINA)) {

						CommResult commResult = SinaCommHandler
								.getEmotions(null);
						String responseCode = commResult.getResponseCode();
						String message = commResult.getMessage();
						if (responseCode != null && responseCode.equals("200")) {
							SendMessageActivity.emotionList = SinaParserHandler
									.parseEmotions(message);
							getEmotionDataForWebView();
						}

					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)) {

						CommResult commResult = TencentCommHandler
								.getEmotions(null);
						String responseCode = commResult.getResponseCode();
						String message = commResult.getMessage();
						if (responseCode != null && responseCode.equals("200")) {
							SendMessageActivity.emotionList = TencentParserHandler
									.parseEmotions(message);
						} else {
							SendMessageActivity.emotionList = TencentParserHandler
									.parseEmotionsFromFile(getApplicationContext());
						}
						getEmotionDataForWebView();

					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SOHU)) {
						SendMessageActivity.emotionList = SohuParserHandler
								.parseEmotionsFromFile(getApplicationContext());
						getEmotionDataForWebView();
					} else if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_RENREN)) {

						CommResult commResult = RenRenCommHandler
								.getEmotions(null);
						String responseCode = commResult.getResponseCode();
						String message = commResult.getMessage();
						if (responseCode != null && responseCode.equals("200")) {
							SendMessageActivity.emotionList = RenRenParserHandler
									.parseEmotions(message);
							getEmotionDataForWebView();
						}

					}

				}

			}
		}, "Thread-Emotion").start();

	}

	public void getEmotionDataForWebView() {

		if (SendMessageActivity.emotionList != null
				&& SendMessageActivity.emotionList.size() > 30
				&& SendMessageActivity.htmlDataListForEmotion == null) {
			SendMessageActivity.htmlDataListForEmotion = new ArrayList<String>();
			for (int i = 0; i < SendMessageActivity.emotionList.size() / 30; i++) {
				StringBuffer result = new StringBuffer();
				result.append("<table border=\"0\" align=\"center\">");

				for (int x = i * 30; x < (i + 1) * 30; x = x + 6) {
					result.append("<tr>");
					for (int y = 0; y < 6; y++) {
						EmotionInfo emotionInfo = SendMessageActivity.emotionList
								.get(x + y);
						result.append("<td><img width=\"22px\" height=\"22px\" src=\""
								+ emotionInfo.getUrl()
								+ "\" onClick=\"getImage('"
								+ emotionInfo.getPhrase() + "');\"/></td>");
					}
					result.append("</tr>");
				}

				result.append("</table>");
				SendMessageActivity.htmlDataListForEmotion.add(result
						.toString());

			}
		}

		/*
		 * StringBuffer result = new StringBuffer();
		 * 
		 * int size = SendMessageActivity.emotionList.size(); int i = size / 6;
		 * int j = size % 6;
		 * 
		 * result.append("<table border=\"0\" align=\"center\">");
		 * 
		 * for (int x = 0; x < i; x++) {
		 * 
		 * result.append("<tr>");
		 * 
		 * for (int y = 0; y < 6; y++) {
		 * 
		 * EmotionInfo emotionInfo = SendMessageActivity.emotionList.get(x 6 +
		 * y); result.append("<td><img width=\"22px\" height=\"22px\" src=\"" +
		 * emotionInfo.getUrl() + "\" onClick=\"getImage('" +
		 * emotionInfo.getPhrase() + "');\"/></td>");
		 * 
		 * }
		 * 
		 * result.append("</tr>");
		 * 
		 * }
		 * 
		 * if (j != 0) {
		 * 
		 * result.append("<tr>");
		 * 
		 * for (int x = 0; x < j; x++) {
		 * 
		 * EmotionInfo emotionInfo = SendMessageActivity.emotionList.get(i 6 +
		 * x); result.append("<td><img width=\"22px\" height=\"22px\" src=\"" +
		 * emotionInfo.getUrl() + "\" onClick=\"getImage('" +
		 * emotionInfo.getPhrase() + "');\"/></td>");
		 * 
		 * }
		 * 
		 * result.append("</tr>");
		 * 
		 * }
		 * 
		 * result.append("</table>");
		 * 
		 * SendMessageActivity.htmlDataForEmotion = result.toString();
		 */

	}

	// -------------setting speech language---------------------------

	private void settingSpeechLanguage(String speechLanguage) {
		resources = getResources();// 获得res资源对象

		config = resources.getConfiguration();// 获得设置对象

		if ("0".equals(speechLanguage) || speechLanguage == null) {
			config.locale = Locale.getDefault();
		}
		if ("1".equals(speechLanguage)) {
			config.locale = Locale.ENGLISH;
		}
		if ("2".equals(speechLanguage)) {
			config.locale = Locale.JAPANESE;
		}
		if ("3".equals(speechLanguage)) {
			config.locale = Locale.SIMPLIFIED_CHINESE;
		}

		dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。

		resources.updateConfiguration(config, dm);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;
	}

	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (window != null && window.isShowing()) {
			window.dismiss();
		}
		return super.onTouchEvent(event);
	}

}