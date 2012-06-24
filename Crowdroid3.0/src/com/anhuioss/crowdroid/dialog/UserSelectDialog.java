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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.SendDMActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class UserSelectDialog extends Dialog implements OnItemClickListener,
		OnClickListener, ServiceConnection {

	/** Context */
	private Context mContext;

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
	
	private boolean statusFlag = false;
	
	/**true = A/false = B*/
	private boolean listFlag = true;
	
	private int limitNumver = 25;

	/** Close */
	private Button closeButton;

	/** List View */
	private ListView listView;
	
	/** Next Cursor (Twitter) */
	public static long nextCursor = -1;
	
	/** Pre Cursor (Twitter) */
	public static long preCursor = -1;
	
	private int page = 1;
	
	private int prevCount = 0;
	
	private boolean nextFlag = true; 

	ArrayList<String> data = new ArrayList<String>();

	ArrayAdapter<String> adapter;

	private AccountData accountData;
	
	ArrayList<UserInfo> userInfoData = new ArrayList<UserInfo>();
	
	ArrayList<UserInfo> userInfoListA = new ArrayList<UserInfo>();
	
	ArrayList<UserInfo> userInfoListB = new ArrayList<UserInfo>();
	
	public static List<String> userIdsForLookup;
	
	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {
			
			userInfoListA = new ArrayList<UserInfo>();
			userInfoListB = new ArrayList<UserInfo>();
			
			// Dismiss ProgressBar
			setProgressEnable(false);
			
			if(statusCode != null && statusCode.equals("200")){
				
				// Parser
				ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
				ParseHandler parseHandler = new ParseHandler();
				userInfoList = (ArrayList<UserInfo>) parseHandler.parser(service,
						type, statusCode, message);
				
				int size = userInfoList.size();
				if(size > limitNumver){
					
					statusFlag = true;
					
					for(int i = 0; i < limitNumver; i++){
						userInfoListA.add(userInfoList.get(i));
					}
					
					for(int j = limitNumver; j < size; j++){
						userInfoListB.add(userInfoList.get(j));
					}
					
				}else{
					
					statusFlag = false;
					
					userInfoListA = userInfoList;
					
				}
				if(size<14){
					nextFlag = false;
				} else{
					nextFlag = true;
				}
				
				listFlag = true;

				// Set Data
				userInfoData = userInfoList;
				
				// Create ListView's Data
				createListView(userInfoListA);
				
			} else {
				Toast.makeText(mContext, ErrorMessage.getErrorMessage(mContext, statusCode), Toast.LENGTH_SHORT).show();
			}

		}
	};

	public UserSelectDialog(Context context) {
		super(context);

		// Get Window
		mWindow = getWindow();
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);

		// Set Layout
		setContentView(R.layout.dialog_user_select);

		// Set Context
		mContext = context;

		// Find Views
		title = (TextView) findViewById(R.id.dialog_title);
		progressBar = (ProgressBar) findViewById(R.id.dialog_progress_bar);
		prevButton = (Button) findViewById(R.id.prev_button);
		nextButton = (Button) findViewById(R.id.next_button);
		closeButton = (Button) findViewById(R.id.close_button);
		listView = (ListView) findViewById(R.id.user_select_listview);

		// Set Click Listener
		prevButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		closeButton.setOnClickListener(this);
		listView.setOnItemClickListener(this);

		// Set Adapter
		adapter = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_list_item_1, data);
		listView.setAdapter(adapter);
		
		nextCursor = -1;
		preCursor = -1;

		// Set Progress Bar
		setProgressEnable(false);


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
		setTitle(R.string.action_dialog_dm);
		
		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) mContext.getApplicationContext();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();

		// Initial Views
		initViewsByService(accountData.getService());
		
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

		if(data.isEmpty()){
		
			setProgressEnable(true);
	
			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("us", "");
			parameters.put("query", "");
			parameters.put("cursor", nextCursor);
			parameters.put("page", page);
			parameters.put("screen_name", accountData.getUserScreenName());
			parameters.put("username", accountData.getUserName());
			parameters.put("uid", accountData.getUid());
	
			// Get Service From Current Account
			try {
				apiServiceInterface.request(
						accountData.getService(),
						CommHandler.TYPE_GET_USER_LIST, apiServiceListener,
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
			
			setProgressEnable(true);
			prevCount--;
			if(statusFlag && !listFlag){
				setProgressEnable(false);
				listFlag = true;
				createListView(userInfoListA);
			}else{
				
				if(page > 1){
					page = page - 1;
				}
				
				// Prepare Parameters
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("us", "");
				parameters.put("cursor", preCursor);
				parameters.put("page", page);
				parameters.put("screen_name", accountData.getUserScreenName());
				parameters.put("username", accountData.getUserName());

				// Get Service From Current Account
				try {
					apiServiceInterface.request(
							accountData.getService(),
							CommHandler.TYPE_GET_USER_LIST, apiServiceListener,
							parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				Toast.makeText(mContext, R.string.prev, Toast.LENGTH_SHORT).show();
			}
			
			break;

		}
		case R.id.next_button: {

			setProgressEnable(true);
			prevCount++;
			if(statusFlag && listFlag){
				setProgressEnable(false);
				listFlag = false;
				createListView(userInfoListB);
			}else{
				
				page = page + 1;
				
				// Prepare Parameters
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("us", "");
				parameters.put("cursor", nextCursor);
				parameters.put("page", page);
				parameters.put("screen_name", accountData.getUserScreenName());
				parameters.put("username", accountData.getUserName());

				// Get Service From Current Account
				try {
					apiServiceInterface.request(
							accountData.getService(),
							CommHandler.TYPE_GET_USER_LIST, apiServiceListener,
							parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				Toast.makeText(mContext, R.string.next, Toast.LENGTH_SHORT).show();
			}

			break;

		}
		case R.id.close_button: {

			// Close
			dismiss();
			Toast.makeText(mContext, R.string.close, Toast.LENGTH_SHORT).show();
			break;

		}
		default: {

		}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// Intent
		Intent intent = new Intent(mContext, SendDMActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		Bundle bundle = new Bundle();
		bundle.putString("name", listView.getItemAtPosition(position)
				.toString());
		bundle.putString("userName", userInfoData.get(position).getUserName());
		bundle.putString("uid", userInfoData.get(position).getUid());
		intent.putExtras(bundle);
		mContext.startActivity(intent);

		// Dismiss
		dismiss();

	}
	
	public static String getIdsForLookup() {
		
		if(userIdsForLookup == null || userIdsForLookup.size() == 0) {
			return null;
		}
		
		StringBuffer userIds = new StringBuffer();
		int i = 0;
		
		for(; i < 20 && i < userIdsForLookup.size(); i++) {
			userIds.append(userIdsForLookup.get(i)).append(",");
		}
		
		for(; i > 0 ; i--) {
			userIdsForLookup.remove(0);
		}
		
		return userIds.subSequence(0, userIds.length() - 1).toString();
	}

	private void setProgressEnable(boolean flag) {

		if (flag) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.INVISIBLE);
		}

	}

	// -----------------------------------------------------------------------------
	/**
	 * Set Data To List View
	 */
	// -----------------------------------------------------------------------------
	private void createListView(ArrayList<UserInfo> userInfoList) {

		// Initial Next And Previous Button
		changeStatus();
		
		// Clear Data
		data.clear();

		// Change Data
		for (UserInfo userInfo : userInfoList) {

			data.add(userInfo.getScreenName());

		}

		// Notify
		adapter.notifyDataSetChanged();

	}
	
	private void initViewsByService(String service) {
		
		prevButton.setEnabled(false);
		
		if(service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)){
			nextButton.setEnabled(false);
		}	
	}
	
	//-----------------------------------------------------------------------------
	/**
	 *  Change Diaplay According to Current Status
	 */
	//-----------------------------------------------------------------------------
	public void changeStatus(){
		
		//Pre Button
		if(preCursor == 0){
			
			if(listFlag){
				prevButton.setEnabled(false);
			}else{
				prevButton.setEnabled(true);
			}
			
		}else{
			prevButton.setEnabled(true);
		}
		
		//Next Button
		if(nextCursor == 0 && statusFlag == false){
			nextButton.setEnabled(false);
		}else{
			nextButton.setEnabled(true);
		}
		
		if(prevCount > 0){
			prevButton.setEnabled(true);
		} else{
			prevButton.setEnabled(false);
		}
		
		if(nextFlag){
			nextButton.setEnabled(true);
		} else{
			nextButton.setEnabled(false);
			
		}
		
	}
	


}
