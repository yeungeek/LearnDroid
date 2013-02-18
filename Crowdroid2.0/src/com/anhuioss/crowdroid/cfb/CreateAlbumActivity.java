package com.anhuioss.crowdroid.cfb;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;

public class CreateAlbumActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	private EditText name;

	private EditText description;

	private Button ok;

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;
	
	private TextView tip=null;
	
	private TextView tis=null;

	int mark;
	// Progress Dialog
	private HandleProgressDialog progress = null;

	private static boolean isRunning = true;

	private StatusData statusData;

	private AccountData currentAccount;

	private CrowdroidApplication crowdroidApplication;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {
			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null) {
				Toast.makeText(CreateAlbumActivity.this, getResources().getString(R.string.createTip),
						Toast.LENGTH_SHORT).show();
				finish();

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_album);
		findViews();
	}

	private void findViews() {

		name = (EditText) findViewById(R.id.album_name);
		description = (EditText) findViewById(R.id.album_description);

		ok = (Button) findViewById(R.id.ok);
		ok.setText(getResources().getString(R.string.ok));
		tip=(TextView)findViewById(R.id.tip);
		tis=(TextView)findViewById(R.id.tis);
		tis.setText(getResources().getString(R.string.albumDes));
		tip.setText(getResources().getString(R.string.albumName));

		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerHome.setBackgroundResource(R.drawable.main_app);

//		mark = getIntent().getIntExtra("mark", 1);
		headerName.setText(getResources().getString(R.string.createAlbum));
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);
		ok.setOnClickListener(this);
	}

	@Override
	public void onStart() {
		isRunning = true;

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();

		statusData = crowdroidApplication.getStatusData();
		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();
		Bundle bundle=getIntent().getExtras();
		mark=bundle.getInt("mark");

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();

		// Unbind Service
		unbindService(this);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {
			Intent intent = new Intent(CreateAlbumActivity.this,
					DiscoveryActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.ok: {
			try {

				showProgressDialog();
				if (name.getText().toString().equals("")
						|| name.getText().toString().equals(null)) {
					Toast.makeText(CreateAlbumActivity.this, getResources().getString(R.string.albumNameTip),
							Toast.LENGTH_SHORT).show();
				}
				String des = description.getText().toString() != null ? description
						.getText().toString() : "";
				Map<String, Object> map;
				map = new HashMap<String, Object>();
				map.put("album_name", name.getText().toString());
				map.put("album_description", des);
				if (mark == 1) {
					map.put("album_mark", "picture");
				} else if (mark == 2) {
					map.put("album_mark", "document");
				} else if (mark == 3) {
					map.put("album_mark", "video");
				}
				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_CFB_CREATE_ALBUM, apiServiceListener,
						map);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		}

		}
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
	protected void refreshByMenu() {
		// TODO Auto-generated method stub

	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(CreateAlbumActivity.this);
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
