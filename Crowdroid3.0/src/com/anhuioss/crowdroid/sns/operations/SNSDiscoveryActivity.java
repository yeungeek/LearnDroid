package com.anhuioss.crowdroid.sns.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.BasicSearchActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.FavoriteTimelineActivity;
import com.anhuioss.crowdroid.GroupTimelineActivity;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.HotRetweetTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.PublicTimelineActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.RetweetByMeActivity;
import com.anhuioss.crowdroid.RetweetOfMeActivity;
import com.anhuioss.crowdroid.RetweetToMeActivity;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.UserTimelineActivity;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.activity.FollowActivity;
import com.anhuioss.crowdroid.activity.LBSUpdateMessageActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.GroupListSlugDialog;
import com.anhuioss.crowdroid.dialog.SNSPageDialog;
import com.anhuioss.crowdroid.dialog.SuggestionsSlugDialog;
import com.anhuioss.crowdroid.dialog.TrendsDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.sns.AlbumsTimelineActivity;
import com.anhuioss.crowdroid.sns.BlogTimelineActivity;
import com.anhuioss.crowdroid.sns.MyFeedTimelineActivity;
import com.anhuioss.crowdroid.sns.MyShareTimelineActivity;
import com.anhuioss.crowdroid.sns.MyStateTimelineActivity;
import com.anhuioss.crowdroid.sns.StatusTimelineActivity;
import com.anhuioss.crowdroid.sns.operations.CreateNewAlbumActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SNSDiscoveryActivity extends BasicActivity implements
		ServiceConnection, OnClickListener {

	private StatusData statusData;

	private AccountData accountData;

	private GridView gridview;

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	private String headUrl;

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")) {

				switch (type) {
				case CommHandler.TYPE_GET_USER_INFO: {

					// Parser
					UserInfo userInfo = new UserInfo();
					ParseHandler parseHandler = new ParseHandler();
					userInfo = (UserInfo) parseHandler.parser(service, type,
							statusCode, message);

					// Succeed
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_RENREN)
							&& userInfo.getUid() != null) {

						headUrl = userInfo.getUserImageURL();

					} else {
						// Error
						showErrorMessage(statusCode);
					}
				}
				default: {
				}
				}
			}
		}
	};

	private int[] imageIds = { R.drawable.discovery_user_timeline_icon,
			R.drawable.discovery_hot_users_icon, R.drawable.discovery_user_search_icon,
			R.drawable.discovery_favitore_timeline_icon,
			R.drawable.discovery_group_list_icon, R.drawable.discovery_causal_watch_icon,
			R.drawable.discovery_mood_icon, R.drawable.discovery_lbs_icon };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_discovery_gridview);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();

		gridview = (GridView) findViewById(R.id.gridview);

		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);

		headerHome.setOnClickListener(this);
		headerBack.setOnClickListener(this);
		gridview.setOnItemClickListener(new ItemClickListener());

		headerName.setText(R.string.more_app);
		headerHome.setBackgroundResource(R.drawable.main_home);

		String[] renrenAppNames = { getString(R.string.blog_management),
				getString(R.string.album_management),
				getString(R.string.user_search), getString(R.string.my_feed),
				getString(R.string.my_friends), getString(R.string.web_sign),
				getString(R.string.discovery_page_name),
				getString(R.string.discovery_lbs_name) };

		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 8; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[i]);
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				map.put("ItemText", renrenAppNames[i]);
			}
			lstImageItem.add(map);
		}
		SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,
				R.layout.activity_discovery_gridview_item, new String[] {
						"ItemImage", "ItemText" }, new int[] { R.id.ItemImage,
						R.id.ItemText });
		gridview.setAdapter(saImageItems);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onStop() {
		super.onStop();

		// Unbind Service
		unbindService(this);
	}

	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> item = (HashMap<String, Object>) arg0
					.getItemAtPosition(position);

			switch (position) {
			case 0: {
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
					openBlogManager();
				}

				break;
			}
			case 1: {
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
					openAlbumManager();
				}
				break;
			}
			case 2: {
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
					Intent i = new Intent(SNSDiscoveryActivity.this,
							SNSUserSerchActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}
				break;
			}
			case 3: {
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
					openFeedManager();

				}
				break;
			}
			case 4: {
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
					Intent intent = new Intent(SNSDiscoveryActivity.this,
							FollowActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.putExtra("screenName",
							accountData.getUserScreenName());
					intent.putExtra("userName", accountData.getUserName());
					intent.putExtra("uid", accountData.getUid());
					startActivity(intent);
				}
				break;
			}
			case 5: {
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
					Uri uri = Uri.parse("http://www.renren.com/"
							+ accountData.getUid());
					Intent it = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(it);
				}
				break;
			}
			case 6: {
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
					new SNSPageDialog(SNSDiscoveryActivity.this).show();
				}
				break;
			}
			case 7: {
				if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
					openLBSManager();
				}
				break;
			}
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {

			Intent comment = new Intent(SNSDiscoveryActivity.this,
					HomeTimelineActivity.class);
			startActivity(comment);
		}
			break;
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			parameters.put("uid", accountData.getUid());

			try {
				// HTTP Communication
				apiServiceInterface.request(IGeneral.SERVICE_NAME_RENREN,
						CommHandler.TYPE_GET_USER_INFO, apiServiceListener,
						parameters);
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
	protected void refreshByMenu() {
	}

	private void openBlogManager() {
		final CharSequence[] operate = getResources().getStringArray(
				R.array.renren_discovery_blog_manager);
		AlertDialog dialog = new AlertDialog.Builder(SNSDiscoveryActivity.this)
				.setItems(operate, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {

							Intent blog = new Intent(SNSDiscoveryActivity.this,
									UpdateBlogActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("action", "blog");
							blog.putExtras(bundle);
							blog.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(blog);

						} else if (which == 1) {

							Intent i = new Intent(SNSDiscoveryActivity.this,
									BlogTimelineActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.putExtra("name", accountData.getUserScreenName());
							i.putExtra("username", accountData.getUserName());
							i.putExtra("uid", accountData.getUid());
							i.putExtra("head_url", headUrl);
							i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(i);

						}
					}
				}).create();
		dialog.show();
	}

	private void openAlbumManager() {
		final CharSequence[] operate = getResources().getStringArray(
				R.array.renren_discovery_album_manager);
		AlertDialog dialog = new AlertDialog.Builder(SNSDiscoveryActivity.this)
				.setItems(operate, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent intent = new Intent(
									SNSDiscoveryActivity.this,
									CreateNewAlbumActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intent);
						} else if (which == 1) {
							Intent intent = new Intent(
									SNSDiscoveryActivity.this,
									UploadPhotosActivity.class);
							Bundle bundle = new Bundle();
							intent.putExtras(bundle);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intent);

						} else if (which == 2) {
							Intent i = new Intent(SNSDiscoveryActivity.this,
									AlbumsTimelineActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.putExtra("uid", accountData.getUid());
							i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(i);
						}
					}
				}).create();
		dialog.show();
	}

	private void openFeedManager() {
		final CharSequence[] operate = getResources().getStringArray(
				R.array.renren_discovery_feed_manager);
		AlertDialog dialog = new AlertDialog.Builder(SNSDiscoveryActivity.this)
				.setItems(operate, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent intent = new Intent(
									SNSDiscoveryActivity.this,
									MyFeedTimelineActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intent);
						} else if (which == 1) {
							Intent intent = new Intent(
									SNSDiscoveryActivity.this,
									MyStateTimelineActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intent);

						} else if (which == 2) {
							Intent i = new Intent(SNSDiscoveryActivity.this,
									MyShareTimelineActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(i);
						}
					}
				}).create();
		dialog.show();
	}

	private void openLBSManager() {
		final CharSequence[] operate = getResources().getStringArray(
				R.array.renren_discovery_lbs_manager);
		AlertDialog dialog = new AlertDialog.Builder(SNSDiscoveryActivity.this)
				.setItems(operate, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Intent intent = new Intent(
									SNSDiscoveryActivity.this,
									LBSUpdateMessageActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intent);
						}
					}
				}).create();
		dialog.show();
	}

	private void showErrorMessage(String statusCode) {
		if (!statusCode.equals("200")) {
			int code = Integer.parseInt(statusCode);
			selectErrorMessageToShow(code);
		}
	}

	private void selectErrorMessageToShow(int statusCode) {

		switch (statusCode) {

		case 400:

		case 401:

		case 403:

		case 404:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_INPUT,
					Toast.LENGTH_SHORT).show();
			break;

		case 500:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_SERVER,
					Toast.LENGTH_SHORT).show();
			break;

		case 502:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_BADGATEWAY,
					Toast.LENGTH_SHORT).show();
			break;

		case 503:
			Toast.makeText(this,
					ErrorMessage.ERROR_MESSAGE_SERVICE_UNAVAILABLE,
					Toast.LENGTH_SHORT).show();
			break;

		default:
			Toast.makeText(this, ErrorMessage.ERROR_MESSAGE_INPUT,
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

}
