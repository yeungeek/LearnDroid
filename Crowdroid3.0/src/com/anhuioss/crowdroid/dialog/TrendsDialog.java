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
import com.anhuioss.crowdroid.activity.KeywordSearchActivity;
import com.anhuioss.crowdroid.activity.TrendTimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.service.sina.SinaCommHandler;
import com.anhuioss.crowdroid.service.tencent.TencentCommHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class TrendsDialog extends AlertDialog implements ServiceConnection {

	private ListView trendsNameListView;

	private SimpleAdapter trendsNameAdapter;
	
	private Context mContext;

	private StatusData statusData;

	/** Spinner */
	private Spinner trendsTypeSpinner;

	/** Data For Spinner */
	private List<String> trendsTypeData = new ArrayList<String>();

	/** Adapter */
	private ArrayAdapter<String> trendsTypeAdapter;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ArrayList<TrendInfo> trendInfoListData = new ArrayList<TrendInfo>();

	private ArrayList<TrendInfo> trendSpinnertData = new ArrayList<TrendInfo>();

	private ApiServiceInterface apiServiceInterface;

	private static final int TRENDS_BY_HOUR = 0;

	private static final int TRENDS_BY_DAY = 1;

	private static final int TRENDS_BY_WEEK = 2;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")) {

				if (type == CommHandler.TYPE_GET_TRENDS_BY_TYPE
						|| type == CommHandler.TYPE_GET_TRENDS_BY_WOEID) {

					// Parser
					ArrayList<TrendInfo> listInfoList = new ArrayList<TrendInfo>();
					ParseHandler parseHandler = new ParseHandler();
					listInfoList = (ArrayList<TrendInfo>) parseHandler.parser(
							service, type, statusCode, message);

					trendInfoListData = listInfoList;
					
					// Create ListView's Data
					createListView(listInfoList);

				} else if (type == CommHandler.TYPE_GET_LOCATIONS_AVAILIABLE_TRENDS) {

					// Parser
					ArrayList<TrendInfo> listInfoList = new ArrayList<TrendInfo>();
					ParseHandler parseHandler = new ParseHandler();
					listInfoList = (ArrayList<TrendInfo>) parseHandler.parser(
							service, type, statusCode, message);

					trendSpinnertData = listInfoList;

					// Create Spinner's Data
					createSpinner(listInfoList);

				}

			} else {
				Toast.makeText(mContext, ErrorMessage.getErrorMessage(mContext, statusCode), Toast.LENGTH_SHORT).show();
			}

		}
	};

	public TrendsDialog(Context context) {
		super(context);
		
		mContext = context;

		// Set View
		LayoutInflater inflater = LayoutInflater.from(context);
		View layoutView = inflater.inflate(R.layout.dialog_trends, null);
		setView(layoutView);
		setIcon(android.R.drawable.ic_menu_myplaces);
		setTitle(getContext().getString(R.string.trends));

		CrowdroidApplication app = (CrowdroidApplication) getContext()
				.getApplicationContext();
		statusData = app.getStatusData();

		if (IGeneral.SERVICE_NAME_SINA.equals(statusData.getCurrentService())) {
			trendsTypeData.add(context.getString(R.string.trends_hourly));
			trendsTypeData.add(context.getString(R.string.trends_daily));
			trendsTypeData.add(context.getString(R.string.trends_weekly));
		} else if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
				.getCurrentService())
				|| IGeneral.SERVICE_NAME_TWITTER_PROXY.equals(statusData
						.getCurrentService())) {
			// Get trends data from network by communication.
		} else if (IGeneral.SERVICE_NAME_TENCENT.equals(statusData
				.getCurrentService())) {
			trendsTypeData.add(mContext.getString(R.string.trends_keyword));
			trendsTypeData.add(mContext.getString(R.string.trends_name));
		} else if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(statusData.getCurrentService())){
			trendsTypeData.add(mContext.getString(R.string.trends_name));
		}

		// Find Views
		trendsNameListView = (ListView) layoutView
				.findViewById(R.id.dialog_trends_list);
		trendsTypeSpinner = (Spinner) layoutView
				.findViewById(R.id.dialog_trends_type);

		// Set Click Listener
		trendsNameListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (trendInfoListData.size() > 0) {

					// Get List Information
					TrendInfo list = trendInfoListData.get(arg2);
					if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(statusData.getCurrentService())){
						TrendDialog.setTrendTimelineTitle(getContext()
								.getString(R.string.trend)
								+ ":"
								+ list.getName());
						Intent i = new Intent(getContext(),
								BasicSearchActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("keyword", list.getName());
						i.putExtras(bundle);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getContext().startActivity(i);
					} else if (IGeneral.SERVICE_NAME_SINA.equals(statusData
							.getCurrentService())) {

						// Set List Timeline Title
						TrendDialog.setTrendTimelineTitle(getContext()
								.getString(R.string.trend)
								+ ":"
								+ list.getName());

						// Prepare Parameters
						Map<String, String> map = new HashMap<String, String>();
						map.put("trend_name", list.getName());
						SinaCommHandler.setTrendParameter(map);
						Intent intent = new Intent(getContext(),
								TrendTimelineActivity.class);
						getContext().startActivity(intent);

					} else if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
							.getCurrentService())
							|| IGeneral.SERVICE_NAME_TWITTER_PROXY
									.equals(statusData.getCurrentService())) {

						Intent i = new Intent(getContext(),
								KeywordSearchActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("keyword", list.getName());
						i.putExtras(bundle);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getContext().startActivity(i);

					} else if (IGeneral.SERVICE_NAME_TENCENT.equals(statusData
							.getCurrentService())) {
						// Set List Timeline Title
						if(trendsTypeSpinner.getSelectedItemPosition() == 0) {
							TrendDialog.setTrendTimelineTitle(getContext()
									.getString(R.string.trend)
									+ ":"
									+ list.getHotword());
						}else {
							TrendDialog.setTrendTimelineTitle(getContext()
									.getString(R.string.trend)
									+ ":"
									+ list.getName());
						}
						
						// Prepare Parameters
						Map<String, String> map = new HashMap<String, String>();
						
						// Keyword
						if(trendsTypeSpinner.getSelectedItemPosition() == 0) {
							map.put("tencent_name", list.getHotword());
						}
						
						// Name
						else {
							map.put("tencent_name", list.getName());
						}
						TencentCommHandler.setTrendParameter(map);

						Intent intent = new Intent(getContext(),
								TrendTimelineActivity.class);
						getContext().startActivity(intent);
					}

				}

			}
		});

		// Create Adapter
		trendsTypeAdapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_spinner_item, trendsTypeData);
		trendsTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Set Adapter
		trendsTypeSpinner.setAdapter(trendsTypeAdapter);

		// Set Item Selected Listener For Spinner
		trendsTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						
						if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(statusData.getCurrentService())){
							Map<String, Object> parameters;
							parameters = new HashMap<String, Object>();
							// Get Service From Current Account
							try {
								apiServiceInterface.request(
										statusData.getCurrentService(),
										CommHandler.TYPE_GET_TRENDS_BY_TYPE,
										apiServiceListener, parameters);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						} else if (IGeneral.SERVICE_NAME_SINA.equals(statusData
								.getCurrentService())) {

							// Prepare Parameters
							String trendsType = "hourly";

							// Trends Type [default]
							if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
									.getCurrentService())
									|| IGeneral.SERVICE_NAME_TWITTER_PROXY
											.equals(statusData
													.getCurrentService())) {
								trendsType = "current";
							} else if (IGeneral.SERVICE_NAME_SINA
									.equals(statusData.getCurrentService())) {
								trendsType = "hourly";
							} 
//							else if (IGeneral.SERVICE_NAME_TENCENT
//									.equals(statusData.getCurrentService())) {
//								trendsType = "1";
//							}

							if (position == TRENDS_BY_DAY) {
								trendsType = "daily";
							}

							if (position == TRENDS_BY_WEEK) {
								trendsType = "weekly";
							}

							Map<String, Object> parameters;
							parameters = new HashMap<String, Object>();
							parameters.put("type", trendsType);

							// Get Service From Current Account
							try {
								apiServiceInterface.request(
										statusData.getCurrentService(),
										CommHandler.TYPE_GET_TRENDS_BY_TYPE,
										apiServiceListener, parameters);
							} catch (RemoteException e) {
								e.printStackTrace();
							}

						} else if (IGeneral.SERVICE_NAME_TWITTER
								.equals(statusData.getCurrentService())
								|| IGeneral.SERVICE_NAME_TWITTER_PROXY
										.equals(statusData.getCurrentService())) {

							// Prepare Parameters
							Map<String, Object> parameters;
							parameters = new HashMap<String, Object>();
							parameters.put("type",
									trendSpinnertData.get(position).getWoeid());

							// Get Service From Current Account
							try {
								apiServiceInterface.request(
										statusData.getCurrentService(),
										CommHandler.TYPE_GET_TRENDS_BY_WOEID,
										apiServiceListener, parameters);
							} catch (RemoteException e) {
								e.printStackTrace();
							}

						} else if (IGeneral.SERVICE_NAME_TENCENT
								.equals(statusData.getCurrentService())) {
							Map<String, Object> parameters;
							parameters = new HashMap<String, Object>();
//							if (position == 0) {
								parameters.put("type", 2);

//							} else if (position == 1) {
//								parameters.put("type", 2);
//							}

							// Get Service From Current Account
							try {
								apiServiceInterface.request(
										statusData.getCurrentService(),
										CommHandler.TYPE_GET_TRENDS_BY_TYPE,
										apiServiceListener, parameters);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
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
		trendsNameAdapter = new SimpleAdapter(getContext(), data,
				R.layout.list_item_dialog_trends, new String[] { "name" },
				new int[] { R.id.name_list_item_dialog_trends });

		trendsNameListView.setAdapter(trendsNameAdapter);

		// Create List View
//		createListView(trendInfoListData);

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
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();

			// Get Service From Current Account
			try {
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_LOCATIONS_AVAILIABLE_TRENDS,
						apiServiceListener, parameters);
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
	private void createListView(ArrayList<TrendInfo> listInfoList) {

		// Clear
		data.clear();

		// Change Data
		if (listInfoList.size() > 0) {
			for (TrendInfo listInfo : listInfoList) {
				if(IGeneral.SERVICE_NAME_TENCENT.equals(statusData
						.getCurrentService())){
					Map<String, Object> map = new HashMap<String, Object>();
					if(trendsTypeSpinner.getSelectedItemPosition() == 1){
						map.put("name", listInfo.getName());
					}else if(trendsTypeSpinner.getSelectedItemPosition() == 0){
						map.put("name", listInfo.getHotword());
					}
					
					data.add(map);
				}
				else{
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("name", listInfo.getName());
					data.add(map);
				}
			}
		} else {
			if (IGeneral.SERVICE_NAME_SINA.equals(statusData
					.getCurrentService())) {
				// No data for this trend.
				Toast.makeText(getContext(),
						getContext().getString(R.string.trends_no_data),
						Toast.LENGTH_SHORT).show();
			} else if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
					.getCurrentService())
					|| IGeneral.SERVICE_NAME_TWITTER_PROXY.equals(statusData
							.getCurrentService())) {
				// Do something.
			}
		}

		// Notify
		trendsNameAdapter.notifyDataSetChanged();

	}

	private void createSpinner(ArrayList<TrendInfo> listInfoList) {

		// Clear
		trendsTypeData.clear();

		// Change Data
		if (listInfoList.size() > 0) {
			for (TrendInfo listInfo : listInfoList) {
				trendsTypeData.add(listInfo.getName());
			}
		} else {
			// No data for this trend.
			Toast.makeText(getContext(),
					getContext().getString(R.string.trends_no_data),
					Toast.LENGTH_SHORT).show();
		}

		// Notify
		trendsTypeAdapter.notifyDataSetChanged();

	}

}
