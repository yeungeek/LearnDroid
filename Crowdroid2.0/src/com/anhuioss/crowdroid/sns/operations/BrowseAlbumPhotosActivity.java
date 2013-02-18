package com.anhuioss.crowdroid.sns.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.UserTimelineActivity;
import com.anhuioss.crowdroid.activity.UserSearchActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.communication.TranslationService;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.GridViewAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BrowseAlbumPhotosActivity extends Activity implements
		OnClickListener, ServiceConnection {

	private GridView gridview;

	private TextView headName;

	private Button headBack;

	private List<Map<String, String>> data = new ArrayList<Map<String, String>>();

	public static final String API_SERVICE_NAME = ".communication.ApiService";

	private StatusData statusData;

	private SettingData settingData;

	private AccountData accountData;

	private CrowdroidApplication crowdroidApplication;

	/** Auto Refresh Timer */
	// AutoRefreshHandler autoRefresh;

	/** Refresh Back */
	boolean refreshBack = false;

	private String service;

	private MyImageBinder myImageBinder;

	private String imageShow;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	ProgressDialog pd = null;

	private ApiServiceInterface apiServiceInterface;

	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> item = (HashMap<String, Object>) arg0
					.getItemAtPosition(position);

			switch (position) {
			case 0: {

				// user timeline
				Intent i = new Intent(BrowseAlbumPhotosActivity.this,
						UserTimelineActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				i.putExtra("name", "");
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);

				break;
			}
			case 1: {
				// user search
				Intent i = new Intent(BrowseAlbumPhotosActivity.this,
						UserSearchActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("name", "");
				i.putExtras(bundle);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
				break;
			}

			}
		}
	}

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[]")) {
				// Parser
				ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();
				ParseHandler parseHandler = new ParseHandler();
				timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
						.parser(service, type, statusCode, message);

				if (timelineInfoList != null && timelineInfoList.size() > 0) {
					createPhotosView(timelineInfoList);
				}
			}
			if (!"200".equals(statusCode)) {
				Toast.makeText(
						BrowseAlbumPhotosActivity.this,
						ErrorMessage.getErrorMessage(
								BrowseAlbumPhotosActivity.this, statusCode),
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_discovery_gridview);
		RelativeLayout relative = (RelativeLayout) findViewById(R.id.header);
		headName = (TextView) findViewById(R.id.head_Name);
		headBack = (Button) findViewById(R.id.head_back);
		headBack.setOnClickListener(this);
		// relative.setVisibility(View.GONE);
		gridview = (GridView) findViewById(R.id.gridview);
		gridview.setOnItemClickListener(new ItemClickListener());

	}

	protected void createPhotosView(ArrayList<TimeLineInfo> timelineInfoList) {

		ArrayList<HashMap<String, String>> listImageItem = new ArrayList<HashMap<String, String>>();

		for (TimeLineInfo timelineInfo : timelineInfoList) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("itemImage", timelineInfo.getStatus());
			listImageItem.add(map);
		}
		// SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,
		// R.layout.activity_discovery_gridview_item, new String[] {
		// "ItemImage"}, new int[] { R.id.ItemImage,});
		gridview.setAdapter(new GridViewAdapter(this, timelineInfoList,
				listImageItem, CommHandler.TYPE_GET_ALBUM_PHOTOS));

		// Add
		for (Map<String, String> addData : listImageItem) {
			data.add(addData);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		isRunning = true;
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();
		statusData = crowdroidApplication.getStatusData();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();
		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();
		imageShow = settingData.getSelectionShowImage();

		// myImageBinder = new MyImageBinder(fontColor, fontSize, null, this);
		// adapter.setViewBinder(myImageBinder);
		headName.setText(getIntent().getExtras().getString("name"));
		// Bind Api Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		// Bind Service
		Intent intentTranslation = new Intent(this, TranslationService.class);
		bindService(intentTranslation, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// Unbind Service
		unbindService(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TimelineActivity.isBackgroundNotificationFlag = true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TimelineActivity.isBackgroundNotificationFlag = false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}

		default:
			break;
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		if (name.getShortClassName().equals(API_SERVICE_NAME)) {
			apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
			if (data.isEmpty()) {
				try {

					// setProgressBarIndeterminateVisibility(true);
					showProgressDialog();

					// Prepare Parameters
					Map<String, Object> parameters;
					parameters = new HashMap<String, Object>();

					Bundle bundle = this.getIntent().getExtras();
					parameters.put("uid", bundle.getString("uid"));
					parameters.put("aid", bundle.getString("aid"));
					if ("4".equals(bundle.getString("visible"))) {
						parameters
								.put("password", bundle.getString("password"));
					}

					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_ALBUM_PHOTOS,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		// data.clear();
		apiServiceInterface = null;
	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(BrowseAlbumPhotosActivity.this);
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
