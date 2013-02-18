package com.anhuioss.crowdroid.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.TrendTimelineActivity;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.service.sina.SinaCommHandler;

public class TrendDialog extends Dialog implements OnItemClickListener,
		OnItemLongClickListener {

	/** Context */
	private Context mContext;

	/** Time */
	private Window mWindow;

	/** Time */
	private TextView title;

	private Button closeButton;

	/** ProgressDialog */
	private HandleProgressDialog progress;

	private ListView listView;

	private SimpleAdapter adapter;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ArrayList<TrendInfo> trendInfoListData = new ArrayList<TrendInfo>();

	// private StatusData statusData;

	private String screenName;

	// private String uid;

	private static String trendTimelineTitle;

	// private int selectedItemPosition;
	//
	// private String selectedItemFollowStatus;

	// private ApiServiceInterface apiServiceInterface;
	//
	// private ApiServiceListener.Stub apiServiceListener = new
	// ApiServiceListener.Stub() {
	//
	// @SuppressWarnings("unchecked")
	// @Override
	// public void requestCompleted(String service, int type,
	// String statusCode, String message) throws RemoteException {
	//
	// // Dismiss ProgressBar
	// setProgressEnable(false);
	//
	// if(statusCode != null && statusCode.equals("200")){
	//
	// if(type == CommHandler.TYPE_GET_MY_LISTS){
	// // Parser
	// ArrayList<TrendInfo> listInfoList = new ArrayList<TrendInfo>();
	// ParseHandler parseHandler = new ParseHandler();
	// listInfoList = (ArrayList<TrendInfo>) parseHandler.parser(service,
	// type, statusCode, message);
	//
	// trendInfoListData = listInfoList;
	//
	// // Create ListView's Data
	// createListView(listInfoList);
	//
	// }else{
	//
	// //
	// trendInfoListData.get(selectedItemPosition).setFollow(selectedItemFollowStatus);
	// // createListView(trendInfoListData);
	//
	// }
	//
	// }
	//
	// }
	// };

	public TrendDialog(Context context, String screenName, String uid,
			ArrayList<TrendInfo> treadInfo) {
		super(context);

		mWindow = getWindow();
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_list);

		mContext = context;

		this.screenName = screenName;

		// this.uid = uid;

		trendInfoListData = treadInfo;

		// Find Views
		title = (TextView) findViewById(R.id.dialog_title);
		listView = (ListView) findViewById(R.id.list_name_listview);
		closeButton = (Button) findViewById(R.id.closeButton);

		// Set Click Listener
//		listView.setOnItemClickListener(this);
//		listView.setOnItemLongClickListener(this);
		closeButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}

		});

		// Set Adapter
		// adapter = new SimpleAdapter(mContext, data,
		// R.layout.dialog_list_item,
		// new String[] { "name", "description", "image" },
		// new int[] { R.id.list_name, R.id.list_description,
		// R.id.list_image });
		//
		adapter = new SimpleAdapter(mContext, data, R.layout.dialog_list_item,
				new String[] { "name" }, new int[] { R.id.list_name, });

		adapter.setViewBinder(new MyImageBinder(String.valueOf(Color.WHITE),
				"16", null, mContext));

		listView.setAdapter(adapter);

		createListView(trendInfoListData);
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

		// CrowdroidApplication crowdroidApplication = (CrowdroidApplication)
		// mContext
		// .getApplicationContext();
		// statusData = crowdroidApplication.getStatusData();

		setTitle(R.string.trends);

		// Bind Service
		// Intent intent = new Intent(mContext, ApiService.class);
		// mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onStop() {
		super.onStop();

		closeProgressDialog();

		// Unbind Service
		// mContext.unbindService(this);
	}

	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	//
	// // if(data.isEmpty()){
	// //
	// // setProgressEnable(true);
	// //
	// // // Prepare Parameters
	// // Map<String, Object> parameters;
	// // parameters = new HashMap<String, Object>();
	// // parameters.put("uid", uid);
	// //
	// // // Get Service From Current Account
	// // try {
	// // apiServiceInterface.request(statusData.getCurrentService(),
	// // CommHandler.TYPE_GET_TREND_LIST, apiServiceListener,
	// // parameters);
	// // } catch (RemoteException e) {
	// // e.printStackTrace();
	// // }
	// //
	// // }
	// }
	//
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	// Log.i("Activity", "onServiceDisconnected");
	// apiServiceInterface = null;
	//
	// }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (trendInfoListData.size() > 0) {

			// Get List Information
			TrendInfo list = trendInfoListData.get(position);

			// Set List Timeline Title
			trendTimelineTitle = R.string.trend + ":" + list.getHotword();

			// Prepare Parameters
			Map<String, String> map = new HashMap<String, String>();
			map.put("trend_name", list.getHotword());
			SinaCommHandler.setTrendParameter(map);
			Intent intent = new Intent(mContext, TrendTimelineActivity.class);
			mContext.startActivity(intent);

		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		if (trendInfoListData.size() > 0) {

			// selectedItemPosition = position;
			//
			// // Get List Information
			// ListInfo list = trendInfoListData.get(position);
			//
			// // Show Follow Dialog
			// showFollowDialog(list);

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
	private void createListView(ArrayList<TrendInfo> listInfoList) {

		// Clear
		data.clear();

		// Change Data
		if (listInfoList.size() > 0) {

			// Resources res = mContext.getResources();
			// Bitmap bitmap = BitmapFactory.decodeResource(res,
			// R.drawable.dialog_list_color);

			for (TrendInfo listInfo : listInfoList) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", listInfo.getHotword());
				// map.put("description", listInfo.getDescription());

				// if(listInfo.getFollow() != null &&
				// listInfo.getFollow().equals("true")){
				// map.put("image", bitmap);
				// }

				data.add(map);

			}

		} else {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", String.format(
					mContext.getString(R.string.no_list_for_user), screenName));
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
	// private void showFollowDialog(final ListInfo list) {
	//
	// if(list.getFollow() == null){
	// return;
	// }
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	// builder.setMessage(R.string.register_error);
	//
	// if(list.getFollow().equals("true")){
	// selectedItemFollowStatus = "false";
	// builder.setMessage(R.string.whether_unfollow_list);
	// }else{
	// selectedItemFollowStatus = "true";
	// builder.setMessage(R.string.whether_follow_list);
	// }
	//
	// builder.setPositiveButton(R.string.ok, new
	// DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	//
	// setProgressEnable(true);
	//
	// // Prepare Parameters
	// Map<String, Object> parameters;
	// parameters = new HashMap<String, Object>();
	// parameters.put("user", screenName);
	// parameters.put("list_id", list.getId());
	// parameters.put("type", list.getFollow());
	//
	// // Get Service From Current Account
	// try {
	// apiServiceInterface.request(statusData.getCurrentService(),
	// CommHandler.TYPE_SET_FOLLOW_LIST, apiServiceListener,
	// parameters);
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	//
	// }
	// });
	//
	// builder.setNegativeButton(R.string.cancel, null);
	//
	// builder.create();
	// builder.show();
	// }

	public static String getTrendTimelineTitle() {
		return trendTimelineTitle;
	}

	public static void setTrendTimelineTitle(String title) {
		trendTimelineTitle = title;
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
