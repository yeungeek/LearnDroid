package com.anhuioss.crowdroid.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.ListTimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.service.twitter.TwitterCommHandler;
import com.anhuioss.crowdroid.service.twitterproxy.TwitterProxyCommHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class ListDialog extends Dialog implements OnItemClickListener, ServiceConnection, OnItemLongClickListener {

	/** Context */
	private Context mContext;

	/** Time */
	private Window mWindow;

	/** Time */
	private TextView title;
	
	private Button closeButton;
	
	private Button nextButton;

	/** ProgressDialpg */
	private HandleProgressDialog progress;

	private ListView listView;

	private SimpleAdapter adapter;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	
	private ArrayList<ListInfo> listInfoListData = new ArrayList<ListInfo>();

	private StatusData statusData;

	private String screenName;
	
	private String uid = "";
	
	private static String listTimelineTitle;
	
	private int selectedItemPosition;
	
	private String selectedItemFollowStatus;

	private String nextCursor = "-1";
	
	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			// Dismiss ProgressBar
			setProgressEnable(false);
			
			if(statusCode != null && statusCode.equals("200")){
				
				if(type == CommHandler.TYPE_GET_MY_LISTS){
					// Parser
					ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();
					ParseHandler parseHandler = new ParseHandler();
					listInfoList = (ArrayList<ListInfo>) parseHandler.parser(service,
							type, statusCode, message);

					listInfoListData = listInfoList;
					
					if(listInfoListData.size() > 0) {
						nextCursor = listInfoListData.get(0).getNextCursor();
					}
					
					// Change The Status Of Next Button
					if(nextCursor.equals("0")) {
						nextButton.setVisibility(View.INVISIBLE);
					} else {
						nextButton.setVisibility(View.VISIBLE);
					}
					
					// Create ListView's Data
					createListView(listInfoListData);
					
				}else{
					
					listInfoListData.get(selectedItemPosition).setFollow(selectedItemFollowStatus);
					createListView(listInfoListData);
					
				}
				
			} else {
				Toast.makeText(mContext, ErrorMessage.getErrorMessage(mContext, statusCode), Toast.LENGTH_SHORT).show();
			}

		}
	};

	public ListDialog(Context context, final String screenName, String uid) {
		super(context);

		mWindow = getWindow();
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_list);

		mContext = context;

		this.screenName = screenName;
		this.uid = uid;

		// Find Views
		title = (TextView) findViewById(R.id.dialog_title);
		listView = (ListView) findViewById(R.id.list_name_listview);
		closeButton = (Button) findViewById(R.id.closeButton);
		nextButton = (Button) findViewById(R.id.dialog_next_button_for_list_dialog);
		nextButton.setVisibility(View.INVISIBLE);
		
		// Set Click Listener
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		closeButton.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}

		});
		
		nextButton.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setProgressEnable(true);
				
				// Prepare Parameters
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("screenName", screenName);
				parameters.put("cursor", nextCursor);
		
				// Get Service From Current Account
				try {
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_GET_MY_LISTS, apiServiceListener,
							parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

		});

		// Set Adapter
		adapter = new SimpleAdapter(mContext, data, R.layout.dialog_list_item,
				new String[] { "name", "description", "image"}, new int[] {
						R.id.list_name, R.id.list_description, R.id.list_image});
		
		adapter.setViewBinder(new MyImageBinder(String.valueOf(Color.WHITE), "16", null, mContext));
		
		listView.setAdapter(adapter);

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

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) mContext
				.getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		
		setTitle(String.format(mContext.getString(R.string.list_dialog), screenName));

		// Bind Service
		Intent intent = new Intent(mContext, ApiService.class);
		mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onStop() {
		super.onStop();

		closeProgressDialog();
		
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
			parameters.put("screenName", screenName);
			parameters.put("cursor", nextCursor);
	
			// Get Service From Current Account
			try {
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_MY_LISTS, apiServiceListener,
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		if(listInfoListData.size() > 0){
			
			// Get List Information
			ListInfo list = listInfoListData.get(position);
		
			// Set List Timeline Title
			listTimelineTitle = screenName + "'s " + list.getName();
			
			// Prepare Parameters
			Map<String, String> map = new HashMap<String, String>();
			map.put("user", list.getUserInfo().getScreenName());
			map.put("id", list.getId());
			TwitterCommHandler.setListParameter(map);
			TwitterProxyCommHandler.setListParameter(map);
			Intent intent = new Intent(mContext, ListTimelineActivity.class);
			mContext.startActivity(intent);
			
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		
		if(listInfoListData.size() > 0){
			
			selectedItemPosition = position;
			
			// Get List Information
			ListInfo list = listInfoListData.get(position);
			
			// Show Follow Dialog
			showFollowDialog(list);
	
		}
		
		return true;
		
	}

	private void setProgressEnable(boolean flag) {

		if (flag) {
			showProgressDialog();
		} else {
			closeProgressDialog();
		}

	}

	// -----------------------------------------------------------------------------
	/**
	 * Set Data To List View
	 */
	// -----------------------------------------------------------------------------
	private void createListView(ArrayList<ListInfo> listInfoList) {

		// Clear
		data.clear();

		// Change Data
		if (listInfoList.size() > 0) {
			
			Resources res = mContext.getResources();
			
			try {
				Bitmap bitmap=BitmapFactory.decodeResource(res, R.drawable.dialog_list_color);

				for (ListInfo listInfo : listInfoList) {
					
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("name", listInfo.getName() + ((listInfo.getFollow() != null && listInfo.getFollow().equals("true")) ? "(Follow)" : "(Unfollow)"));
					map.put("description", listInfo.getDescription());
					
					if(listInfo.getFollow() != null && listInfo.getFollow().equals("true")){
						map.put("image", bitmap);
					}
					
					data.add(map);
	
				}
			} catch (OutOfMemoryError e) {}
			
		} else {
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", String.format(mContext.getString(R.string.no_list_for_user), screenName));
			map.put("description", "");
			data.add(map);
			
		}

		// Notify
		adapter.notifyDataSetChanged();

	}
	
	// -----------------------------------------------------------------------------
	/**
	 * Dialog
	 */
	// -----------------------------------------------------------------------------
	private void showFollowDialog(final ListInfo list) {
		
		if(list.getFollow() == null){
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(R.string.register_error);
		
		if(list.getFollow().equals("true")){
			selectedItemFollowStatus = "false";
			builder.setMessage(R.string.whether_unfollow_list);
		}else{
			selectedItemFollowStatus = "true";
			builder.setMessage(R.string.whether_follow_list);
		}
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				setProgressEnable(true);

				// Prepare Parameters
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("user", screenName);
				parameters.put("list_id", list.getId());
				parameters.put("type", list.getFollow());

				// Get Service From Current Account
				try {
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_SET_FOLLOW_LIST, apiServiceListener,
							parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		builder.setNegativeButton(R.string.cancel, null);
		
		builder.create();
		builder.show();
	}
	
	public static String getListTimelineTitle(){
		return listTimelineTitle;
	}
	
	private void showProgressDialog(){
		if(progress == null) {
			progress = new HandleProgressDialog(mContext);
		}
		progress.show();
	}
	
	private void closeProgressDialog(){
		if(progress != null) {
			progress.dismiss();
		}
	}

}
