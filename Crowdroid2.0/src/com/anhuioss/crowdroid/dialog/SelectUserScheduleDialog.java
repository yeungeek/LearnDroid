package com.anhuioss.crowdroid.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.operations.AddScheduleActivity;
import com.anhuioss.crowdroid.operations.ScheduleMonthActivity;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.sns.operations.UpdateBlogActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class SelectUserScheduleDialog extends Dialog implements
		OnItemClickListener, OnClickListener, ServiceConnection {

	private boolean nextFlag = true;
	private boolean prevFlag = false;
	private int prevCount = 0;

	/** Context */
	private Context mContext;

	private String message = "";

	/** Window */
	private Window mWindow;

	/** Title */
	private TextView title;

	/** ProgressBar */
	private ProgressBar progressBar;

	/** Prev */
	private Button prevButton;

	/** Next */
	private Button nextButton;

	/** Close */
	private Button closeButton;

	/** List View */
	private ListView listView;

	ArrayList<String> data = new ArrayList<String>();
	ArrayList<String> atUserNameList = new ArrayList<String>();

	ArrayAdapter<String> adapter;

	private AccountData accountData;

	ArrayList<UserInfo> userInfoData = new ArrayList<UserInfo>();

	public static List<String> userIdsForLookup;

	private ApiServiceInterface apiServiceInterface;

	private ArrayAdapter<String> userAdapter;

	private ArrayList<String> userData = new ArrayList<String>();

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			// Dismiss ProgressBar
			setProgressEnable(false);

			if (statusCode != null && statusCode.equals("200")) {

				// Parser
				if (type == CommHandler.TYPE_GET_CFB_USER) {
					ParseHandler parseHandler = new ParseHandler();
					ArrayList<UserInfo> userInfoList = (ArrayList<UserInfo>) parseHandler
							.parser(service, type, statusCode, message);

					if (userInfoList != null && userInfoList.size() > 0) {
						userInfoData = userInfoList;
						createUserSpinner(userInfoList);
					}
				}

			} else {
				Toast.makeText(mContext,
						ErrorMessage.getErrorMessage(mContext, statusCode),
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	public SelectUserScheduleDialog(Context context) {
		super(context);

		// Get Window
		mWindow = getWindow();
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);

		// Set Layout
		setContentView(R.layout.at_dialog_user_select);

		// Set Context
		mContext = context;
		// Find Views
		title = (TextView) findViewById(R.id.dialog_title);
		progressBar = (ProgressBar) findViewById(R.id.dialog_progress_bar);
		prevButton = (Button) findViewById(R.id.prev_button);
		nextButton = (Button) findViewById(R.id.next_button);
		closeButton = (Button) findViewById(R.id.ok_button);
		listView = (ListView) findViewById(R.id.user_select_listview);

		// Set Click Listener
		prevButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		closeButton.setOnClickListener(this);
		listView.setOnItemClickListener(this);

		prevButton.setVisibility(View.GONE);
		nextButton.setVisibility(View.GONE);
		closeButton.setVisibility(View.GONE);

		prevButton.setEnabled(false);
		nextButton.setEnabled(false);

		// Set Progress Bar
		setProgressEnable(false);

	}

	protected void createUserSpinner(ArrayList<UserInfo> userInfoList) {

		userData.clear();
		for (UserInfo userInfo : userInfoList) {
			userData.add(userInfo.getScreenName());
		}
		// Set Adapter
		userAdapter = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_expandable_list_item_1, userData);
		listView.setAdapter(userAdapter);
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void setTitle(CharSequence title) {
		this.title.setText(title);
	}

	@Override
	public void onStart() {
		super.onStart();

		userIdsForLookup = null;

		// Set Title For This Dialog
		// setTitle(R.string.action_dialog_at);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) mContext
				.getApplicationContext();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();

		// Bind Service
		Intent intent = new Intent(mContext, ApiService.class);
		mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onStop() {
		super.onStop();

		// Unbind Service
		mContext.unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

		if (data.isEmpty()) {

			setProgressEnable(true);

			Map<String, Object> parameters = new HashMap<String, Object>();
			try {
				apiServiceInterface.request(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS,
						CommHandler.TYPE_GET_CFB_USER, apiServiceListener,
						parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.prev_button: {

			break;

		}
		case R.id.next_button: {

			break;

		}
		case R.id.ok_button: {

		}
		default: {

		}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(mContext, ScheduleMonthActivity.class);
		Bundle b = new Bundle();
		b.putString("userName", userInfoData.get(position).getScreenName());
		b.putString("userId", userInfoData.get(position).getUid());
		intent.putExtras(b);
		mContext.startActivity(intent);
	}

	private void setProgressEnable(boolean flag) {

		if (flag) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.INVISIBLE);
		}

	}

}
