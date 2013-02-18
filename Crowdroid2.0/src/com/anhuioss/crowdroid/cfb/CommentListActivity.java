package com.anhuioss.crowdroid.cfb;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.MyImageBinder;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.TagAnalysis;

@SuppressLint("ParserError")
public class CommentListActivity extends BasicActivity implements
		OnClickListener, ServiceConnection {

	private ArrayList<TimeLineInfo> timelineInfoList;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private ListView listView = null;

	private RelativeLayout relativeRight = null;

	private RelativeLayout relativeBottom = null;

	// head
	private Button headerBack = null;

	private Button headerHome = null;

	private TextView headerName = null;

	private int size = 0;

	private SimpleAdapter adapter;

	private StatusData statusData;

	int TYPE;

	int mark;

	private AccountData accountData;

	private SettingData settingData;

	private MyImageBinder myImageBinder;

	private AccountData currentAccount;

	private String screenName;

	private String userName;

	private String uid = "";

	private String pid = "";

	/** Next Cursor (Twitter) */
	public static long nextCursor = -1;

	// Progress Dialog
	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();

		// Set Gallery
		setLayoutResId(R.layout.timeline_layout);

		// right
		relativeRight = (RelativeLayout) findViewById(R.id.layout_main_right);
		relativeBottom = (RelativeLayout) findViewById(R.id.layout_main_bottom);

		relativeRight.setVisibility(View.GONE);
		relativeBottom.setVisibility(View.GONE);
		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerName.setText(getResources().getString(R.string.commentList));
		headerHome.setBackgroundResource(R.drawable.main_app);

		// Find Views
		listView = (ListView) findViewById(R.id.list_view);
		listView.setDivider(null);

		// Set Item Click Listener
		listView.setOnItemClickListener(onItemClickListener);
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);

		adapter = new SimpleAdapter(this, data,
				R.layout.photo_comment_list_item, new String[] { "screenName",
						"comment", "userImage", "time", },
				new int[] { R.id.name, R.id.web_status, R.id.user_image,
						R.id.time });

		listView.setAdapter(adapter);
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			if (position + 1 == data.size()
					&& ((String) data.get(position).get("screenName"))
							.equals("")) {
				deleteItemForMoreTweets();
				// if (nextCursor != 0
				// || (userIdsForLookup != null && userIdsForLookup.size() > 0))
				// {
				// refresh();
				// }
			} else {

			}

		}

	};
	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			// setProgressBarIndeterminateVisibility(false);
			closeProgressDialog();

			if (statusCode != null && statusCode.equals("200")) {

				ParseHandler parseHandler = new ParseHandler();
				timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
						.parser(service, type, statusCode, message);
				if (timelineInfoList != null && timelineInfoList.size() > 0) {

					createListView(timelineInfoList);

					// Add More Tweets If Needs
					if (timelineInfoList.size() >= 20) {
						addItemForMoreTweets();
					}
				}

			} else {
				Toast.makeText(
						CommentListActivity.this,
						ErrorMessage.getErrorMessage(CommentListActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	public void onStart() {
		super.onStart();
		isRunning = true;
		nextCursor = -1;

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();

		String fontColor = settingData.getFontColor();
		String fontSize = settingData.getFontSize();

		accountData = crowdroidApplication.getAccountList().getCurrentAccount();

		Bundle bundle = getIntent().getBundleExtra("comment");
		mark = bundle.getInt("mark");
		pid = bundle.getString("pid");

		switch (mark) {
		case 1: {
			TYPE = CommHandler.TYPE_CFB_PHOTO_COMMENT_LIST;
			break;
		}
		case 2: {
			TYPE = CommHandler.TYPE_CFB_DOCUMENT_COMMENT_LIST;
			break;
		}
		case 3: {
			TYPE = CommHandler.TYPE_CFB_VIDEO_COMMENT_LIST;
			break;
		}
		}
		// pid = getIntent().getExtras().getString("pid");
		myImageBinder = new MyImageBinder(fontColor, fontSize, null, this);
		adapter.setViewBinder(myImageBinder);
		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onPause() {
		super.onPause();
		isRunning = false;
		if (progress != null) {
			progress.dismiss();
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		// Unbind Service
		unbindService(this);
	}

	private void createListView(ArrayList<TimeLineInfo> timelineInfoList) {

		// Create ListView
		ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();

		// Prepare ArrayList
		for (TimeLineInfo timelineInfo : timelineInfoList) {

			Map<String, Object> map;
			map = new HashMap<String, Object>();

			map.put("screenName", timelineInfo.getUserInfo().getScreenName());

			String statusImages = timelineInfo.getImageInformationForWebView(
					CommentListActivity.this,
					TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
			String status = TagAnalysis.clearImageUrls(
					timelineInfo.getStatus(), statusImages);

			map.put("comment", status);

			map.put("time", timelineInfo.getFormatTime(
					statusData.getCurrentService(), this));
			map.put("userImage", timelineInfo.getUserInfo().getUserImageURL());

			addDatas.add(map);

		}

		// Add
		for (Map<String, Object> addData : addDatas) {
			data.add(addData);
		}

		// Notify
		adapter.notifyDataSetChanged();

	}

	public void addItemForMoreTweets() {

		if (data.size() > 0
				&& !data.get(data.size() - 1).get("screenName").equals("")) {
			try {
				Map<String, Object> map;
				map = new HashMap<String, Object>();
				map.put("screenName", "");
				map.put("moreTweets",
						getResources().getString(R.string.get_more_tweets));
				data.add(map);
				adapter.notifyDataSetChanged();
			} catch (OutOfMemoryError e) {
				System.gc();
			}
		}
	}

	public void deleteItemForMoreTweets() {

		int size = data.size();
		for (int i = size - 1; i >= 0; i--) {
			if (data.get(i).get("screenName").equals("")) {
				data.remove(i);
				adapter.notifyDataSetChanged();
				break;
			}
		}

	}

	private void refresh() {

		// setProgressBarIndeterminateVisibility(true);
		showProgressDialog();

		if (apiServiceInterface == null) {
			return;
		}

		// currentPage++;

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();

		parameters.put("pid", pid);

		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(), TYPE,
					apiServiceListener, parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
		if (data.isEmpty()) {
			try {

				showProgressDialog();
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("pid", pid);
				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						TYPE, apiServiceListener, parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.head_refresh: {
			Intent home = new Intent(CommentListActivity.this,
					DiscoveryActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
			break;
		}
		case R.id.head_back: {

			finish();
			break;
		}
		default:
			break;
		}
	}

	@Override
	protected void refreshByMenu() {
		// TODO Auto-generated method stub

	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(this);
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

	public static Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			// 获得连接
			HttpURLConnection conn = (HttpURLConnection) myFileURL
					.openConnection();
			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 这句可有可无，没有影响
			// conn.connect();
			// 得到数据流
			InputStream is = conn.getInputStream();
			// 解析得到图片
			bitmap = BitmapFactory.decodeStream(is);
			// 关闭数据流
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;

	}
}
