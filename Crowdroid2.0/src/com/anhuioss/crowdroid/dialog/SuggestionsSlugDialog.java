package com.anhuioss.crowdroid.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.SuggestionUsersActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class SuggestionsSlugDialog extends AlertDialog implements
		ServiceConnection {

	private ApiServiceInterface apiServiceInterface;

	private ListView slugListView;

	private SimpleAdapter slugAdapter;

	private Context mContext;

	private StatusData statusData;

	private Spinner spinner;

	private ArrayList<ListInfo> slugInfoListData = new ArrayList<ListInfo>();

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")) {

				if (type == CommHandler.TYPE_GET_SUGGESTION_SLUG) {

					// Parser
					ParseHandler parseHandler = new ParseHandler();
					slugInfoListData = (ArrayList<ListInfo>) parseHandler
							.parser(service, type, statusCode, message);

					// Create ListView's Data
					createListView(slugInfoListData);
				}
			} else {
				Toast.makeText(mContext,
						ErrorMessage.getErrorMessage(mContext, statusCode),
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	public SuggestionsSlugDialog(Context context) {
		super(context);

		mContext = context;

		// Set View
		LayoutInflater inflater = LayoutInflater.from(context);
		View layoutView = inflater.inflate(R.layout.dialog_trends, null);
		setView(layoutView);
		setIcon(android.R.drawable.ic_menu_myplaces);
		setTitle(getContext().getString(R.string.suggestion_slug));

		CrowdroidApplication app = (CrowdroidApplication) getContext()
				.getApplicationContext();
		statusData = app.getStatusData();

		// Find Views
		slugListView = (ListView) layoutView
				.findViewById(R.id.dialog_trends_list);

		spinner = (Spinner) layoutView.findViewById(R.id.dialog_trends_type);

		spinner.setVisibility(View.GONE);

		slugListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				if (slugInfoListData.size() > 0) {
					Intent intent = new Intent(mContext,
							SuggestionUsersActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("slugs", slugInfoListData.get(position)
							.getSlug());
					intent.putExtras(bundle);
					mContext.startActivity(intent);
				}
			}
		});
		// Set Adapter
		slugAdapter = new SimpleAdapter(mContext, data,
				R.layout.list_item_dialog_trends, new String[] { "name" },
				new int[] { R.id.name_list_item_dialog_trends });
		slugListView.setAdapter(slugAdapter);
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
		if (IGeneral.SERVICE_NAME_TWITTER
				.equals(statusData.getCurrentService())
				|| IGeneral.SERVICE_NAME_TWITTER_PROXY.equals(statusData
						.getCurrentService())) {

			// Prepare Parameters
			Map<String, Object> parameters = new HashMap<String, Object>();

			String language = Locale.getDefault().getLanguage();
			parameters.put("lang", language);
			Log.e("lang", language);

			// Get Service From Current Account
			try {
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_SUGGESTION_SLUG,
						apiServiceListener, parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;
	}

	private void createListView(ArrayList<ListInfo> slugInfoList) {

		// Clear
		data.clear();

		// Change Data
		if (slugInfoList.size() > 0) {
			for (ListInfo listInfo : slugInfoList) {
				if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
						.getCurrentService())
						|| statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("name", listInfo.getName());
					data.add(map);
				}
			}
		}

		// Notify
		slugAdapter.notifyDataSetChanged();

	}
}
