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
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.ListInfoList;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.AddListActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class AddListDialog extends Dialog implements OnItemClickListener,
		ServiceConnection {

	/** Context */
	private Context mContext;

	/** Window */
	private Window mWindow;

	/** Title */
	private TextView title;

	/** List View */
	private ListView listView;

	/** Adapter */
	private SimpleAdapter adapter;

	/** Data For Adapter */
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	/** Data For List View */
	private ArrayList<ListInfo> listInfoListData = new ArrayList<ListInfo>();

	/** Status */
	private StatusData statusData;

	/** Lists Data */
	private ListInfoList lists;

	/** ProgressDialog */
	private HandleProgressDialog progress;

	/** Screen Name */
	private String screenName;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			// Dismiss ProgressBar
			setProgressEnable(false);

			if (statusCode != null && statusCode.equals("200")) {

				// Parser
				ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();
				ParseHandler parseHandler = new ParseHandler();
				listInfoList = (ArrayList<ListInfo>) parseHandler.parser(
						service, type, statusCode, message);

				for (ListInfo listInfo : listInfoList) {
					listInfo.setMode(screenName);
					listInfoListData.add(listInfo);
				}

				if (type == CommHandler.TYPE_GET_MY_LISTS) {

					// Get Follow List
					setProgressEnable(true);

					if (statusCode != null && statusCode.equals("200")) {

						// Prepare Parameters
						Map<String, Object> parameters;
						parameters = new HashMap<String, Object>();
						parameters.put("screenName", screenName);

						// Get Service From Current Account
						try {
							apiServiceInterface.request(
									statusData.getCurrentService(),
									CommHandler.TYPE_GET_FOLLOW_LISTS,
									apiServiceListener, parameters);
						} catch (RemoteException e) {
							e.printStackTrace();
						}

					}

				} else if (type == CommHandler.TYPE_GET_FOLLOW_LISTS) {
					// Create List View
					createListView(listInfoList);
				}

			} else {
				Toast.makeText(mContext,
						ErrorMessage.getErrorMessage(mContext, statusCode),
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	// ------------------------------------------------------------
	/**
	 * Constructor
	 */
	// ------------------------------------------------------------
	public AddListDialog(Context context, String screenName) {
		super(context);

		// Request No Title
		mWindow = getWindow();
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);

		// Set Layout Resource
		setContentView(R.layout.dialog_list);

		mContext = context;

		this.screenName = screenName;

		// Find Views
		title = (TextView) findViewById(R.id.dialog_title);
		listView = (ListView) findViewById(R.id.list_name_listview);

		// Set Click Listener
		listView.setOnItemClickListener(this);

		// Set Adapter
		adapter = new SimpleAdapter(mContext, data, R.layout.dialog_list_item,
				new String[] { "name", "description" }, new int[] {
						R.id.list_name, R.id.list_description });
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

		// Get Status Data
		statusData = crowdroidApplication.getStatusData();

		// Get List Data
		lists = crowdroidApplication.getListInfoList();

		// Set Title
		setTitle(String.format(mContext.getString(R.string.user_list),
				screenName));

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

		if (data.isEmpty()) {

			setProgressEnable(true);

			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("screenName", screenName);

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

		if (listInfoListData.size() > 0) {

			boolean isExist = false;

			// Get List Information
			ListInfo list = listInfoListData.get(position);

			// Get Flag For Add Data
			for (ListInfo listInfo : lists.getAllLists()) {
				if (listInfo.getId().equals(list.getId())) {
					isExist = true;
					break;
				}
			}

			// Add Data Or Toast
			if (isExist) {
				Toast.makeText(
						mContext,
						String.format(
								mContext.getResources().getString(
										R.string.is_exist), list.getName()),
						Toast.LENGTH_SHORT).show();
			} else {

				// Add Data
				lists.addList(list);

				// Show Data
				Intent intent = new Intent();
				intent.setClass(mContext, AddListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				mContext.startActivity(intent);

				// Dismiss Dialog
				dismiss();

			}

		}

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

		// Change Data
		for (ListInfo listInfo : listInfoList) {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", listInfo.getName() + "("
					+ listInfo.getUserInfo().getScreenName() + ")");
			map.put("description", listInfo.getDescription());
			data.add(map);

		}

		// There Is No Data For List View
		if (data.size() == 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", String.format(
					mContext.getString(R.string.no_list_for_user), screenName));
			map.put("description", "");
			data.add(map);
		}

		// Notify
		adapter.notifyDataSetChanged();

	}

	private void showProgressDialog() {
		if (progress == null) {
			progress = new HandleProgressDialog(mContext);
		}
		progress.show();
	}

	private void closeProgressDialog() {
		if (progress != null) {
			progress.dismiss();
		}
	}

}
