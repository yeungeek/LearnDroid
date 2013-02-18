package com.anhuioss.crowdroid.cfb;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.service.CommHandler;

public class SetAlbumCoverActivity extends Activity implements
		ServiceConnection, OnClickListener {

	private Window mWindow;

	private Button ok;

	private Button cancel;

	private StatusData statusData;

	private String album_id = null;
	
	private TextView title_tip=null;

	private CrowdroidApplication crowdroidApplication;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {
			if (statusCode != null && statusCode.equals("200")
					&& message != null) {
				Toast.makeText(SetAlbumCoverActivity.this, getResources().getString(R.string.setCoverSuccess),
						Toast.LENGTH_SHORT).show();
				finish();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mWindow = getWindow();
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);
		mWindow.setGravity(Gravity.CENTER);
		setContentView(R.layout.set_album_cover);
		title_tip=(TextView)findViewById(R.id.title_tip);
		title_tip.setText(getResources().getString(R.string.setCover));
		ok = (Button) findViewById(R.id.ok);
		ok.setText(getResources().getString(R.string.ok));
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setText(getResources().getString(R.string.cancel));
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();

		statusData = crowdroidApplication.getStatusData();
		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		Bundle bundle = getIntent().getExtras();
		album_id = bundle.getString("album_id");
		super.onStart();
	}

	@Override
	public void onStop() {
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
		case R.id.ok: {
			try {
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("id", album_id);
				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_CFB_SET_COVER, apiServiceListener,
						parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.cancel: {
			finish();
			break;
		}

		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
