package com.anhuioss.crowdroid.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.FavoriteTimelineActivity;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.HotRetweetTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.PublicTimelineActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.RetweetByMeActivity;
import com.anhuioss.crowdroid.RetweetOfMeActivity;
import com.anhuioss.crowdroid.RetweetToMeActivity;
import com.anhuioss.crowdroid.UserTimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.dialog.GroupListSlugDialog;
import com.anhuioss.crowdroid.dialog.SelectAreaDialog;
import com.anhuioss.crowdroid.dialog.SuggestionsSlugDialog;
import com.anhuioss.crowdroid.dialog.TencentFamouslistDialog;
import com.anhuioss.crowdroid.dialog.TrendsDialog;
import com.anhuioss.crowdroid.sns.operations.SNSDiscoveryActivity;
import com.anhuioss.crowdroid.util.ErrorMessage;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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

public class DiscoveryActivity extends BasicActivity implements
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
				default: {
				}
				}
			}
		}
	};

	// R.drawable.discovery_keyword_search,

	private int[] imageIds = { R.drawable.discovery_user_timeline_icon,
			R.drawable.discovery_user_search_icon, 
			R.drawable.discovery_hot_trends_icon,
			
			R.drawable.discovery_causal_watch_icon,
			R.drawable.discovery_lbs_icon,
			R.drawable.discovery_hot_users_icon,
			
			R.drawable.discovery_retweet_mention_me_icon,
			R.drawable.discovery_group_list_icon,
			
			R.drawable.discovery_area_timeline_icon,
			R.drawable.discovery_hot_retweet_icon,
			R.drawable.discovery_famous_users_icon,
			
			R.drawable.discovery_tag_icon,
			R.drawable.discovery_mood_icon,
			R.drawable.discovery_favitore_timeline_icon,};

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

		String[] appNames = { getString(R.string.user_timeline),
				getString(R.string.search),
				getString(R.string.trends),
				
				getString(R.string.casual_watch),
				getString(R.string.discovery_lbs_name),
				getString(R.string.gallery_suggestion_users),
				
				getString(R.string.retweets_mention_me),
				getString(R.string.group_list),
				
				getString(R.string.area_timeline),
				getString(R.string.hot_retweet_timeline),
				getString(R.string.discovery_famous_list),
				
				getString(R.string.discovery_sina_tag),
				getString(R.string.discovery_tencent_mood),
				getString(R.string.favorite_timeline)};

		ArrayList<HashMap<String, Object>> listImageItem = new ArrayList<HashMap<String, Object>>();
		//user Timeline /search
		for (int i = 0; i < 2; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[i]);
			map.put("ItemText", appNames[i]);
			listImageItem.add(map);
		}
		//trends
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TWITTER) ||
				statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)||
				statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[2]);
			map.put("ItemText", appNames[2]);
			listImageItem.add(map);
		}
		//casual watch
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TWITTER) ||
				statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)||
				statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT)||
				statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SOHU)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[3]);
			map.put("ItemText", appNames[3]);
			listImageItem.add(map);
		}
		//lbs
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)||
			    statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[4]);
			map.put("ItemText", appNames[4]);
			listImageItem.add(map);
		}
		//hot user
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TWITTER) ||
			    statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)||
			    statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[5]);
			map.put("ItemText", appNames[5]);
			listImageItem.add(map);
		}
		//retweet mention me/group list
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TWITTER)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[6]);
			map.put("ItemText", appNames[6]);
			listImageItem.add(map);
		}
		//group list
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TWITTER)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[7]);
			map.put("ItemText", appNames[7]);
			listImageItem.add(map);
		}
		//area/hot retweet
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[8]);
			map.put("ItemText", appNames[8]);
			listImageItem.add(map);
		}
		//hot retweet
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[9]);
			map.put("ItemText", appNames[9]);
			listImageItem.add(map);
		}
		//famous users
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[10]);
			map.put("ItemText", appNames[10]);
			listImageItem.add(map);
		}
		//tag
		if (statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[11]);
			map.put("ItemText", appNames[11]);
			listImageItem.add(map);
		}
		//mood
		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TENCENT)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[12]);
			map.put("ItemText", appNames[12]);
			listImageItem.add(map);
		}
		//favorited timeline
		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)||
						statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_SINA)
								|| statusData.getCurrentService().equals(
										IGeneral.SERVICE_NAME_TENCENT)
										||statusData.getCurrentService().equals(
												IGeneral.SERVICE_NAME_SOHU)
										|| statusData.getCurrentService().equals(
												IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageIds[13]);
			map.put("ItemText", appNames[13]);
			listImageItem.add(map);
		}
		SimpleAdapter saImageItems = new SimpleAdapter(this, listImageItem,
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

				// user timeline
				Intent i = new Intent(DiscoveryActivity.this,
						UserTimelineActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				i.putExtra("name", "");
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);

				break;
			}
			case 1: {
				// user search
				openSearchManager();
				break;
			}
			default:
				break;
			}
			if (getString(R.string.trends).equals(item.get("ItemText"))) {
				// trends
				new TrendsDialog(DiscoveryActivity.this).show();
			} else if (getString(R.string.casual_watch).equals(
					item.get("ItemText"))) {
				// casual watch
				openCasualWatchManager();			
			} else if (getString(R.string.discovery_lbs_name).equals(
					item.get("ItemText"))) {
				//lbs
				openLBSManager();
			} else if (getString(R.string.gallery_suggestion_users).equals(
					item.get("ItemText"))) {
				//suggested users
				openSuggestedUserManager();
			} else if(getString(R.string.retweets_mention_me).equals(item.get("ItemText"))){
				//open Retweeted mention me
				openRetweetedMentionMe();
			} else if (getString(R.string.group_list).equals(
					item.get("ItemText"))) {
				//group 
				new GroupListSlugDialog(DiscoveryActivity.this).show();
			} else if (getString(R.string.area_timeline).equals(
					item.get("ItemText"))) {
				//area timeline
				new SelectAreaDialog(DiscoveryActivity.this).show();
			} else if (getString(R.string.hot_retweet_timeline).equals(
					item.get("ItemText"))) {
				// hot retweet
				Intent intent = new Intent(DiscoveryActivity.this,
						HotRetweetTimelineActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			} else if (getString(R.string.discovery_famous_list).equals(
					item.get("ItemText"))) {
				//famous user
				new TencentFamouslistDialog(DiscoveryActivity.this).show();
			} else if (getString(R.string.discovery_sina_tag).equals(
					item.get("ItemText"))) {
				//tag
				openTagManager();
			}  else if (getString(R.string.discovery_tencent_mood).equals(
					item.get("ItemText"))) {
				//mood
				openMoodManager();
			} else if(getString(R.string.favorite_timeline).equals(item.get("ItemText"))){
				// favorite
				Intent i = new Intent(DiscoveryActivity.this,
						FavoriteTimelineActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {

			Intent comment = new Intent(DiscoveryActivity.this,
					HomeTimelineActivity.class);
			startActivity(comment);
		}
			break;
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	}

	// Search Interface
	private void openSearchManager() {
		String[] items = null;
		if(statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT)){
			items = getResources().getStringArray(
					R.array.discovery_tencent_search_manager);
		} else if(statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SOHU)){
			items = getResources().getStringArray(
					R.array.discovery_sohu_search_manager);
		} else{
			items = getResources().getStringArray(
					R.array.discovery_search_manager);
		}
		
		AlertDialog dialog = new AlertDialog.Builder(DiscoveryActivity.this)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0: {
							Intent i = new Intent(DiscoveryActivity.this,
									UserSearchActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("name", "");
							i.putExtras(bundle);
							i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(i);
							break;
						}
						case 1: {
							// keyword search
							Intent i = new Intent(DiscoveryActivity.this,
									KeywordSearchActivity.class);
							Bundle bundle = new Bundle();
							if (statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_SOHU)) {
								bundle.putString("SohuSearchTypeFlag",
										"KEYWORD");
							}
							bundle.putString("keyword", "");
							i.putExtras(bundle);
							i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(i);
							break;
						}
						case 2: {
							Intent i = new Intent(DiscoveryActivity.this,
									KeywordSearchActivity.class);
							Bundle bundle = new Bundle();
							if(statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT)){
								bundle.putString("TencentTrendSearchFlag",
										"TrendSearch");
							}
							bundle.putString("keyword", "");
							i.putExtras(bundle);
							i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(i);
							break;
						}
						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}
	// casual watch
	private void openCasualWatchManager() {
		
		SharedPreferences restore = PreferenceManager
				.getDefaultSharedPreferences(DiscoveryActivity.this);
		if (restore.contains("Casual_Watch_Type")) {
			String radioPreStr = restore.getString("Casual_Watch_Type",
					"");
			if (!radioPreStr.equals("")) {
				String key = radioPreStr.substring(
						radioPreStr.indexOf("[") + 1,
						radioPreStr.indexOf("]"));
				String keyword = radioPreStr.substring(
						radioPreStr.indexOf("]") + 1,
						radioPreStr.length());

				switch (Integer.valueOf(key)) {
				case 0:{
					Intent intent = new Intent(DiscoveryActivity.this,
							PublicTimelineActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
					break;
				}
				case 1:{
					Intent region = new Intent(DiscoveryActivity.this,
							KeywordSearchActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("keyword", keyword);
					region.putExtras(bundle);
					region.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(region);
					break;
				}
					
				case 2:{
					Intent searchIntent = new Intent(
							DiscoveryActivity.this,
							KeywordSearchActivity.class);
					Bundle bundle2 = new Bundle();
					bundle2.putString("keyword", keyword);
					searchIntent.putExtras(bundle2);
					searchIntent
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(searchIntent);
					break;
				}
					
				}
			}
		} else {
			Intent intent = new Intent(DiscoveryActivity.this,
					PublicTimelineActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
	}
	// lbs interface
	private void openLBSManager() {
		CharSequence[] operate = {};
		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)) {
			operate = getResources().getStringArray(
					R.array.twitter_discovery_lbs_manager);
		} else if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_SINA)) {
			operate = getResources().getStringArray(
					R.array.sina_discovery_lbs_manager);
		} else if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TENCENT)) {
			operate = getResources().getStringArray(
					R.array.tencent_discovery_lbs_manager);
		}

		AlertDialog dialog = new AlertDialog.Builder(DiscoveryActivity.this)
				.setItems(operate, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0: {
							Intent intent = new Intent(DiscoveryActivity.this,
									LBSUpdateMessageActivity.class);
							startActivity(intent);
							break;
						}
						case 1: {
							Intent intent = new Intent(DiscoveryActivity.this,
									LBSTimelineActivity.class);
							startActivity(intent);
							break;
						}
						case 2: {
							Intent intent = new Intent(DiscoveryActivity.this,
									LBSGetAroundPeopleActivity.class);
							startActivity(intent);
							break;
						}

						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}
	
	//retweet by me / to me/ of me
	private void openRetweetedMentionMe(){
		String[] items = null;
		items = getResources().getStringArray(
				R.array.discovery_twitter_retweeted_manager);
		AlertDialog dialog = new AlertDialog.Builder(DiscoveryActivity.this)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0: {
							// by me
							Intent intent = new Intent(DiscoveryActivity.this,
									RetweetByMeActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intent);
							break;
						}
						case 1: {
							// to me
							Intent intent = new Intent(DiscoveryActivity.this,
									RetweetToMeActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intent);
							break;
						}
						case 2: {
							// of me
							Intent intent = new Intent(DiscoveryActivity.this,
									RetweetOfMeActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(intent);
							break;
						}
						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}
	private void openSuggestedUserManager(){
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER)) {
			new SuggestionsSlugDialog(DiscoveryActivity.this).show();
		} else if(statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)){
			// hot user
			Intent intent = new Intent(DiscoveryActivity.this,
					HotUsersActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("keyword", "");
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		} else{
			// suggestion user
			Intent intent = new Intent(DiscoveryActivity.this,
					SuggestionUsersActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("keyword", "");
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
	}
	
	// Tencent Mood
	private void openMoodManager() {
		String[] items = null;
		items = getResources().getStringArray(
				R.array.discovery_tencent_mood_manager);
		AlertDialog dialog = new AlertDialog.Builder(DiscoveryActivity.this)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0: {
							Intent intent = new Intent(DiscoveryActivity.this,
									UpdateMoodStatusActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							break;
						}
						case 1: {
							Intent intent = new Intent(DiscoveryActivity.this,
									MoodStatusTimelineActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							Bundle bundle = new Bundle();
							bundle.putString("name", "");
							bundle.putString("uid", "");
							bundle.putString("user_name", "");
							intent.putExtras(bundle);
							startActivity(intent);
							break;
						}

						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}

	// Sina Tag Manager
	private void openTagManager() {
		String[] items = null;
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)) {
			items = getResources().getStringArray(
					R.array.discovery_sina_tag_manager);
		} else if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TENCENT)) {
			items = getResources().getStringArray(
					R.array.discovery_tencent_tag_manager);
		}

		AlertDialog dialog = new AlertDialog.Builder(DiscoveryActivity.this)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0: {
							Intent intent = new Intent(DiscoveryActivity.this,
									UpdateTagsActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							break;
						}
						case 1: {
							if (statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_TENCENT)) {

							}
							Intent intent = new Intent(DiscoveryActivity.this,
									TagsListActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("name", "");
							bundle.putString("uid", "");
							bundle.putString("user_name", "");
							intent.putExtras(bundle);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							break;
						}
						case 2: {
							if (statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_TENCENT)) {
								Intent i = new Intent(DiscoveryActivity.this,
										UserSearchActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("TencentTagSearchFlag",
										"TrendTagSearch");
								bundle.putString("keyword", "");
								i.putExtras(bundle);
								i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
								startActivity(i);

							} else if (statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_SINA)) {
								Intent intent = new Intent(
										DiscoveryActivity.this,
										SuggestionTagsListActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}

							break;
						}
						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void refreshByMenu() {
		// TODO Auto-generated method stub
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
