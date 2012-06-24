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
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.GroupTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.GroupListUserActivity;
import com.anhuioss.crowdroid.activity.ProfileActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.SelectThemeActivity;
import com.anhuioss.crowdroid.settings.SettingsActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class GroupListSlugDialog extends AlertDialog implements
		ServiceConnection {

	private ApiServiceInterface apiServiceInterface;

	private ListView slugListView;

	private SimpleAdapter slugAdapter;

	private Context mContext;

	private StatusData statusData;

	private AccountData currentAccount;

	private Spinner spinner;

	private SharedPreferences sharePreference;

	private ArrayList<ListInfo> listInfoList = new ArrayList<ListInfo>();

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[]")) {

				if (type == CommHandler.TYPE_GET_GROUP_LIST_SLUG) {
					ParseHandler parseHandler = new ParseHandler();

					listInfoList = (ArrayList<ListInfo>) parseHandler.parser(
							service, type, statusCode, message);
					createListView(listInfoList);
				}
			} else {
				Toast.makeText(mContext,
						ErrorMessage.getErrorMessage(mContext, statusCode),
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	public GroupListSlugDialog(Context context) {
		super(context);

		mContext = context;

		// Set View
		LayoutInflater inflater = LayoutInflater.from(context);
		View layoutView = inflater.inflate(R.layout.dialog_trends, null);
		setView(layoutView);
		setIcon(android.R.drawable.ic_menu_myplaces);
		setTitle(mContext.getString(R.string.group_list));

		CrowdroidApplication app = (CrowdroidApplication) getContext()
				.getApplicationContext();
		statusData = app.getStatusData();
		currentAccount = app.getAccountList().getCurrentAccount();

		// Find Views
		slugListView = (ListView) layoutView
				.findViewById(R.id.dialog_trends_list);

		spinner = (Spinner) layoutView.findViewById(R.id.dialog_trends_type);

		spinner.setVisibility(View.GONE);

		slugListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					final int position, long id) {
				if (listInfoList.size() > 0) {
					final CharSequence[] groupOperate = mContext.getResources().getStringArray(
							R.array.group_list_slug_operate);
					AlertDialog dialog = new AlertDialog.Builder(mContext)
							.setItems(groupOperate, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (which == 0) {
										// group timeline
										Intent timeline = new Intent(mContext , GroupTimelineActivity.class);
										Bundle bundle = new Bundle();
										bundle.putString("group_timeline_slug", listInfoList.get(position).getSlug());
										bundle.putString("group_timeline_owner", listInfoList.get(position).getUserInfo().getScreenName());
										bundle.putString("group_timeline_id", listInfoList.get(position).getId());
										timeline.putExtras(bundle);
										timeline.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
										mContext.startActivity(timeline);

									} else if (which == 1) { 
										// group users
										Intent userList = new Intent(mContext , GroupListUserActivity.class);
										Bundle bundle = new Bundle();
										bundle.putString("group_list_slug", listInfoList.get(position).getSlug());
										bundle.putString("group_list_owner", listInfoList.get(position).getUserInfo().getScreenName());
										bundle.putString("group_list_id", listInfoList.get(position).getId());
										userList.putExtras(bundle);
										userList.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
										mContext.startActivity(userList);
									}else if (which == 2) { 
										// bilder
										Intent intent = new Intent(mContext,
												ProfileActivity.class);
										Bundle bundle = new Bundle();
										bundle.putString("name", listInfoList.get(position).getUserInfo().getScreenName());
										bundle.putString("user_name", listInfoList.get(position).getUserInfo().getUserName());
										bundle.putString("uid", listInfoList.get(position).getUserInfo().getUid());
										intent.putExtras(bundle);
										intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
										mContext.startActivity(intent);
										
									}

								}
							}).create();
					dialog.show();
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

			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("screenName", currentAccount.getUserScreenName());
			try {
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_GROUP_LIST_SLUG,
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
		sharePreference = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		boolean defCheckFlag = sharePreference.getBoolean(
				"Setting_Group_List_Default", true);
		// Change Data
		if (slugInfoList.size() > 0) {
			if (defCheckFlag) {
				for (ListInfo listInfo : slugInfoList) {

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("name", listInfo.getName());
					data.add(map);

				}
			} else {
				for (ListInfo listInfo : slugInfoList) {
					boolean slugCheckFlag = sharePreference.getBoolean(
							listInfo.getSlug(), false);
					if (slugCheckFlag) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("name", listInfo.getName());
						data.add(map);
					}

				}
			}
		}

		// Notify
		slugAdapter.notifyDataSetChanged();

	}
}
