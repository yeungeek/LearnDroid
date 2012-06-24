package com.anhuioss.crowdroid.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class UpdateProfileActivity extends BasicActivity implements
		OnClickListener, OnTouchListener, ServiceConnection {

	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private WebView userImage;

	private TextView name;

	private TextView description;

	private TextView updateUserImage;

	private EditText nameUpdate;

	private EditText descriptionUpdate;

	private Button reset;

	private Button confirm;

	private String screenName;

	private String uid;

	private String userName;

	private AccountData currentAccount;

	private SettingData settingData;

	private UserInfo userInformation = null;

	private StatusData statusData;

	private String imageShow;

	private Button headerBack = null;

	private Button headerHome = null;

	private TextView headerName = null;

	private UserInfo userInfo = null;

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[null]")) {

				if (type == CommHandler.TYPE_UPDATE_PROFILE) {
					finish();
				}

			}
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SOHU)
					&& statusCode.equals("501")
					&& type == CommHandler.TYPE_UPDATE_PROFILE) {
				Toast.makeText(UpdateProfileActivity.this,
						getResources().getString(R.string.error_message_nick),
						Toast.LENGTH_SHORT).show();
			}

			if (!"200".equals(statusCode)) {
				Toast.makeText(
						UpdateProfileActivity.this,
						ErrorMessage.getErrorMessage(
								UpdateProfileActivity.this, statusCode),
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setLayoutResId(R.layout.activity_update_profile);

		headerBack = (Button) findViewById(R.id.head_back);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerName.setText(R.string.profile);
		headerHome.setBackgroundResource(R.drawable.main_home);
		// Find Views
		userImage = (WebView) findViewById(R.id.profile_user_image);
		name = (TextView) findViewById(R.id.profile_name);
		nameUpdate = (EditText) findViewById(R.id.name_update);
		descriptionUpdate = (EditText) findViewById(R.id.description_update);
		updateUserImage = (TextView) findViewById(R.id.update_user_image);
		updateUserImage.setText(R.string.tencent_update_user_image);
		reset = (Button) findViewById(R.id.reset);
		confirm = (Button) findViewById(R.id.confirm);

		description = (TextView) findViewById(R.id.profile_description);

		userImage.setBackgroundColor(Color.TRANSPARENT);
		userImage.setHorizontalScrollBarEnabled(false);
		userImage.setVerticalScrollBarEnabled(false);

		LinearLayout updateLinear = (LinearLayout) findViewById(R.id.updateLinear);

		// Set Listener
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);
		reset.setOnClickListener(this);
		confirm.setOnClickListener(this);
		updateLinear.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		super.onStart();
		isRunning = true;
		initprofile();

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();
		settingData = crowdroidApplication.getSettingData();

		imageShow = settingData.getSelectionShowImage();

		String fontColor = settingData.getFontColor();

		// Set Color
		setColor(fontColor);

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	public void initprofile() {
		userInfo = (UserInfo) getIntent().getExtras().getSerializable("nick");

		name.setText(userInfo.getScreenName());

		description.setText(userInfo.getDescription() == "null" ? "" : userInfo
				.getDescription());

		setUserImage(userImage, userInfo.getUserImageURL());
	}

	@Override
	public void onPause() {
		super.onPause();
		isRunning = false;
		if (progress != null) {
			progress.dismiss();
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.head_refresh: {
			Intent home = new Intent(UpdateProfileActivity.this,
					HomeTimelineActivity.class);
			startActivity(home);
			break;
		}
		case R.id.reset: {
			nameUpdate.setText("");
			descriptionUpdate.setText("");
			break;
		}
		case R.id.updateLinear: {
			Intent intent = new Intent();
			intent.setClass(UpdateProfileActivity.this,
					UpdateUserImageActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
		case R.id.confirm: {

			Map<String, String> map = new HashMap<String, String>();
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SINA)) {
				map.put("name", nameUpdate.getText().toString());
				map.put("description", descriptionUpdate.getText().toString());
			} else if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_TENCENT)) {
				map.put("nick", nameUpdate.getText().toString());
				map.put("introduction", descriptionUpdate.getText().toString());
			} else if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SOHU)) {
				map.put("nick_name", nameUpdate.getText().toString());
				map.put("description", descriptionUpdate.getText().toString());
			}

			try {
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_UPDATE_PROFILE, apiServiceListener,
						map);

			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}

		default: {

		}
		}

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;
	}

	private void setColor(String fontColor) {

		int color = Integer.valueOf(fontColor);
		name.setTextColor(color);
		description.setTextColor(color);

	}

	@Override
	protected void refreshByMenu() {

	}

	private void setUserImage(WebView imageWebView, String imageUrl) {

		// Set attributes
		imageWebView.setFocusable(false);
		imageWebView.setClickable(true);
		imageWebView.setVerticalScrollBarEnabled(false);
		imageWebView.setHorizontalScrollBarEnabled(false);
		imageWebView.setBackgroundColor(Color.TRANSPARENT);

		if (imageUrl == null || imageUrl.equals("")) {
			// Default Image URL
			imageWebView.setBackgroundResource(R.drawable.default_user_image);
			return;
		}

		StringBuffer htmlData = new StringBuffer();
		htmlData.append("<center>");
		htmlData.append("<img style='max-height: 60px; max-width:60px;' src='");
		htmlData.append(imageUrl);
		htmlData.append("' /></a>");
		htmlData.append("<br>");
		htmlData.append("<center>");
		imageWebView.loadData(htmlData.toString(), "text/html", "utf-8");
		// imageUrl = "<img style='max-height: 60px; max-width: 60px;' src='"
		// + imageUrl + "' />";
		//
		// imageWebView.loadDataWithBaseURL("about:blank", imageUrl,
		// "text/html",
		// "utf-8", "");

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
