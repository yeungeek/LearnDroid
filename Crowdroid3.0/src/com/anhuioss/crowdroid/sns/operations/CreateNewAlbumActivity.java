package com.anhuioss.crowdroid.sns.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class CreateNewAlbumActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	// head
	private Button headBack;
	private TextView headName;
	private Button headHome;

	// content
	private EditText mEditAlbumName;
	private EditText mEditAlbumDescription;
	private Spinner mSpinnerAlbumPermission;
	private EditText mEditAlbumPassword;
	private LinearLayout mLinearAlbumPassword;
	private Button mBtnAlbumCreate;

	private CrowdroidApplication crowdroidApplication;
	private SettingData settingData;
	private StatusData statusData;
	private ApiServiceInterface apiServiceInterface;
	private HandleProgressDialog progress;
	private static boolean isRunning = true;

	private String settingVisiableParam = "";

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			// setProgressBarIndeterminateVisibility(false);
			closeProgressDialog();

			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[null]")) {
				Toast.makeText(CreateNewAlbumActivity.this,
						getString(R.string.success), Toast.LENGTH_SHORT).show();
				finish();
			}
			if (!"200".equals(statusCode)) {

				Toast.makeText(CreateNewAlbumActivity.this, ErrorMessage
						.getErrorMessage(CreateNewAlbumActivity.this,
								statusCode), Toast.LENGTH_SHORT);

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setLayoutResId(R.layout.activity_photo_create_album);

		// head-----------------------------
		headBack = (Button) findViewById(R.id.head_back);
		headHome = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);
		headName.setText(R.string.create_album);
		headHome.setBackgroundResource(R.drawable.header_clean);
		// content------------------------
		mEditAlbumName = (EditText) findViewById(R.id.edit_album_name);
		mEditAlbumDescription = (EditText) findViewById(R.id.edit_album_description);
		mSpinnerAlbumPermission = (Spinner) findViewById(R.id.spinner_album_permission);
		mEditAlbumPassword = (EditText) findViewById(R.id.edit_album_password);
		mLinearAlbumPassword = (LinearLayout) findViewById(R.id.linear_album_password);
		mBtnAlbumCreate = (Button) findViewById(R.id.btn_album_create);
		headBack.setOnClickListener(this);
		headHome.setOnClickListener(this);
		mBtnAlbumCreate.setOnClickListener(this);

		// Create Adapter
		List<String> permissionData = new ArrayList<String>();
		permissionData.add(getString(R.string.everyone));
		permissionData.add(getString(R.string.network_visible));
		permissionData.add(getString(R.string.only_friends));
		permissionData.add(getString(R.string.myself));
		permissionData.add(getString(R.string.password_accessed));
		ArrayAdapter<String> trendsTypeAdapter = new ArrayAdapter<String>(
				CreateNewAlbumActivity.this,
				android.R.layout.simple_spinner_item, permissionData);
		trendsTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Set Adapter
		mSpinnerAlbumPermission.setAdapter(trendsTypeAdapter);

		mSpinnerAlbumPermission
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View view,
							int position, long id) {
						switch (position) {
						case 0: {
							mLinearAlbumPassword.setVisibility(View.GONE);
							mEditAlbumPassword.setText("");
							settingVisiableParam = "everyone";
							break;
						}
						case 1: {
							mLinearAlbumPassword.setVisibility(View.GONE);
							mEditAlbumPassword.setText("");
							settingVisiableParam = "networks";
							break;
						}
						case 2: {
							mLinearAlbumPassword.setVisibility(View.GONE);
							mEditAlbumPassword.setText("");
							settingVisiableParam = "friends";
							break;
						}
						case 3: {
							mLinearAlbumPassword.setVisibility(View.GONE);
							mEditAlbumPassword.setText("");
							settingVisiableParam = "owner";
							break;
						}
						case 4: {
							mLinearAlbumPassword.setVisibility(View.VISIBLE);
							settingVisiableParam = "";
							break;
						}
						default:
							break;
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
	}

	@Override
	public void onStart() {
		super.onStart();
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();
		statusData = crowdroidApplication.getStatusData();
		
		// Bind Api Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unbindService(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.head_refresh: {
			mEditAlbumName.setText("");
			mEditAlbumPassword.setText("");
			mEditAlbumDescription.setText("");
			break;
		}
		case R.id.btn_album_create: {
			showProgressDialog();

			// Prepare Parameter
			Map<String, Object> parameter;
			parameter = new HashMap<String, Object>();
			parameter.put("name", mEditAlbumName.getText());
			parameter.put("description", mEditAlbumDescription.getText());
			parameter.put("visible", settingVisiableParam);
			parameter.put("password", mEditAlbumPassword.getText());
			// HTTP Communication
			try {
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_CREATE_NEW_ALBUM, apiServiceListener,
						parameter);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
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

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		apiServiceInterface = null;
	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(this);
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
