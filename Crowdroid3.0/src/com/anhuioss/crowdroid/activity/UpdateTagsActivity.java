package com.anhuioss.crowdroid.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class UpdateTagsActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	private ApiServiceInterface apiServiceInterface;

	// head
	private Button headerBack;

	private Button headerRefresh;

	private TextView headName;

	// bottom
	private RelativeLayout bottomRelative;

	private EditText mEditStatus;

	private Button mBtnSend;

	private StatusData statusData;

	private AccountData accountData;

	private SettingData settingData;

	private CrowdroidApplication crowdroidApplication;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null) {
				if (type == CommHandler.TYPE_UPDATE_TAGS) {
					if(statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA))
					{
						if(message.equals("[]"))
						{
							Toast.makeText(UpdateTagsActivity.this,
									getString(R.string.alert_tag_add_overflow),
									Toast.LENGTH_SHORT).show();

						}
					}
					else
					{
						Toast.makeText(UpdateTagsActivity.this,
								getString(R.string.success), Toast.LENGTH_SHORT)
								.show();

						finish();
					}

					
				}

			}
			if (!"200".equals(statusCode)) {
				if ("0".equals(statusCode)
						&& statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_TENCENT)) {
					Toast.makeText(UpdateTagsActivity.this,
							getString(R.string.alert_tag_add_overflow),
							Toast.LENGTH_SHORT).show();
					finish();
				} else if ("17".equals(statusCode)) {
					Toast.makeText(UpdateTagsActivity.this,
							getString(R.string.alert_tag_add_overflow),
							Toast.LENGTH_SHORT).show();

				} else if ("18".equals(statusCode)) {
					Toast.makeText(UpdateTagsActivity.this, getString(R.string.alert_tag_same_data),
							Toast.LENGTH_SHORT).show();
					mEditStatus.setText("");

				} else {
					Toast.makeText(
							UpdateTagsActivity.this,
							ErrorMessage.getErrorMessage(
									UpdateTagsActivity.this, statusCode),
							Toast.LENGTH_SHORT).show();
				}

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setLayoutResId(R.layout.operate_update_tags);

		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerRefresh = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);
		headName.setText(getString(R.string.discovery_sina_tag));
		headerRefresh.setBackgroundResource(R.drawable.main_home);
		bottomRelative = (RelativeLayout) findViewById(R.id.toolsbottom);
		bottomRelative.setVisibility(View.GONE);

		mEditStatus = (EditText) findViewById(R.id.update_text);
		mBtnSend = (Button) findViewById(R.id.send);
		mBtnSend.setOnClickListener(this);
		headerBack.setOnClickListener(this);
		headerRefresh.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		isRunning = true;
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();
		statusData = crowdroidApplication.getStatusData();

		// Bind Api Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send: {
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_SINA)
					|| statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)) {
				if ("".equals(mEditStatus.getText())) {
					Toast.makeText(UpdateTagsActivity.this,
							getString(R.string.alert_input_data),
							Toast.LENGTH_SHORT).show();
				} else if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)
						&& mEditStatus.getText().length() > 7
						|| statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_TENCENT)
						&& mEditStatus.getText().length() > 8) {
					Toast.makeText(UpdateTagsActivity.this,
							getString(R.string.alert_input_data_overflow),
							Toast.LENGTH_SHORT).show();
				} else {
					showProgressDialog();
					// Prepare Parameters
					Map<String, Object> parameters;
					parameters = new HashMap<String, Object>();
					parameters.put("tags", mEditStatus.getText().toString());
					// Request
					try {
						apiServiceInterface.request(
								statusData.getCurrentService(),
								CommHandler.TYPE_UPDATE_TAGS,
								apiServiceListener, parameters);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			break;
		}
		case R.id.head_refresh: {
			Intent home = new Intent(UpdateTagsActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		default:
			break;
		}
	}

	@Override
	protected void refreshByMenu() {
	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(UpdateTagsActivity.this);
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
}
