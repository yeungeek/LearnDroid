package com.anhuioss.crowdroid.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.anhuioss.crowdroid.BasicSearchActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.FollowActivity;
import com.anhuioss.crowdroid.activity.FollowedActivity;
import com.anhuioss.crowdroid.activity.KeywordSearchActivity;
import com.anhuioss.crowdroid.activity.TrendTimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.service.sina.SinaCommHandler;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;
import com.anhuioss.crowdroid.sns.operations.PublicPageListActivity;
import com.anhuioss.crowdroid.sns.operations.SNSDiscoveryActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class SNSPageDialog extends AlertDialog implements ServiceConnection {

	private SimpleAdapter categoryAdapter;

	private Context mContext;

	private StatusData statusData;

	private ListView categoryListView;

	/** Adapter */
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ArrayList<TimeLineInfo> timeLineInfoListData = new ArrayList<TimeLineInfo>();

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")) {

				if (type == CommHandler.TYPE_GET_PAGE_CATEGORY) {

					// Parser
					ArrayList<TimeLineInfo> timeLineInfoList = new ArrayList<TimeLineInfo>();
					ParseHandler parseHandler = new ParseHandler();
					timeLineInfoList = (ArrayList<TimeLineInfo>) parseHandler
							.parser(service, type, statusCode, message);

					timeLineInfoListData = timeLineInfoList;

					// Create ListView's Data
					createListView(timeLineInfoList);

				}
			} else {
				Toast.makeText(mContext,
						ErrorMessage.getErrorMessage(mContext, statusCode),
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	public SNSPageDialog(Context context) {
		super(context);

		mContext = context;

		// Set View
		LayoutInflater inflater = LayoutInflater.from(context);
		View layoutView = inflater.inflate(R.layout.dialog_trends, null);
		setView(layoutView);

		setTitle(mContext.getString(R.string.discovery_page_category));

		CrowdroidApplication app = (CrowdroidApplication) getContext()
				.getApplicationContext();
		statusData = app.getStatusData();

		// Find Views
		categoryListView = (ListView) layoutView
				.findViewById(R.id.dialog_trends_list);
		layoutView.findViewById(R.id.dialog_trends_type).setVisibility(
				View.GONE);

		// Set Click Listener
		categoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {

				if (timeLineInfoListData.size() > 0) {

					// Get List Information
					if (IGeneral.SERVICE_NAME_RENREN.equals(statusData
							.getCurrentService())) {
						Intent i = new Intent(getContext(),
								PublicPageListActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("categoryName", timeLineInfoListData
								.get(position).getStatus());
						bundle.putString("categoryId", timeLineInfoListData
								.get(position).getStatusId());
						i.putExtras(bundle);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getContext().startActivity(i);

					}

				}
			}
		});

		// OK
		setButton(DialogInterface.BUTTON_POSITIVE,
				context.getString(R.string.trends_back),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				});

		// Set Adapter
		categoryAdapter = new SimpleAdapter(getContext(), data,
				R.layout.list_item_dialog_trends,
				new String[] { "categoryName" },
				new int[] { R.id.name_list_item_dialog_trends });

		categoryListView.setAdapter(categoryAdapter);
	}

	@Override
	public void onStart() {
		super.onStart();

		// Bind Service
		Intent intent = new Intent(getContext(), ApiService.class);
		getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onStop() {
		super.onStop();

		// Unbind Service
		getContext().unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
		if (IGeneral.SERVICE_NAME_RENREN.equals(statusData.getCurrentService())) {

			// Prepare Parameters
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();

			// Get Service From Current Account
			try {
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_PAGE_CATEGORY, apiServiceListener,
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

	// -----------------------------------------------------------------------------
	/**
	 * Set Data To List View
	 */
	// -----------------------------------------------------------------------------
	private void createListView(ArrayList<TimeLineInfo> listInfoList) {

		// Clear
		data.clear();

		// Change Data
		if (listInfoList.size() > 0) {
			for (TimeLineInfo listInfo : listInfoList) {
				if (IGeneral.SERVICE_NAME_RENREN.equals(statusData
						.getCurrentService())) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("categoryName", listInfo.getStatus());
					data.add(map);
				}
			}
		}
		categoryAdapter.notifyDataSetChanged();
	}

}
