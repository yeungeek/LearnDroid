package com.anhuioss.crowdroid.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.UserTimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.TrendInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.dialog.ListDialog;
import com.anhuioss.crowdroid.dialog.TrendDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.settings.SettingsActivity;
import com.anhuioss.crowdroid.sns.AlbumsTimelineActivity;
import com.anhuioss.crowdroid.sns.BlogTimelineActivity;
import com.anhuioss.crowdroid.sns.StatusTimelineActivity;
import com.anhuioss.crowdroid.util.AsyncDataLoad;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.TagAnalysis;
import com.anhuioss.crowdroid.util.AsyncDataLoad.ImageCallback;

public class ProfileActivity extends BasicActivity implements OnClickListener,
		ServiceConnection {

	private HandleProgressDialog progress;

	private static boolean isRunning = true;

	private WebView userImage;

	private RelativeLayout relative1;

	private RelativeLayout relative2;

	private RelativeLayout relative3;

	private RelativeLayout relative4;

	private RelativeLayout relative5;

	private RelativeLayout relative6;

	private RelativeLayout relative7;

	private TextView genderView;

	private TextView birthdayView;

	private TextView homeTownView;

	private TextView netWorkView;

	private TextView name;

	private TextView follow;

	private TextView profile_edit;

	private TextView followCount;

	private TextView followed;

	private TextView followedCount;

	private TextView description;

	private TextView microblog;

	private TextView microblogCount;

	private TextView trend;

	private TextView trendCount;

	private TextView Twitterlist;

	private TextView TwitterlistCount;

	private ImageView followedStatusImage;

	private TextView followedStatusSet;

	private TextView mood;

	private TextView moodCount;

	private TextView tag;

	private TextView tagCount;

	private LinearLayout baseInfo;

	private RelativeLayout setFollow;

	private ImageView renrenStar;

	private String screenName;

	private String uid;

	private String userName;

	private String ger;

	private AccountData currentAccount;

	private SettingData settingData;

	private UserInfo userInformation = null;

	private StatusData statusData;

	private String imageShow;

	private Button headerBack = null;

	private Button headerHome = null;

	private TextView headerName = null;

	ArrayList<TrendInfo> trendInfo = new ArrayList<TrendInfo>();

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if ("404".equals(statusCode)) {
				Toast.makeText(ProfileActivity.this,
						getString(R.string.user_not_exist), Toast.LENGTH_SHORT)
						.show();
				finish();
			}

			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[null]")) {

				if (type == CommHandler.TYPE_GET_USER_INFO) {

					// Parser
					UserInfo userInfo = new UserInfo();
					ParseHandler parseHandler = new ParseHandler();
					userInfo = (UserInfo) parseHandler.parser(service, type,
							statusCode, message);
					userInformation = userInfo;
					// Init Views
					String countFollow = userInfo.getFollowCount();
					String countFollowed = userInfo.getFollowerCount();
					String countStatus = userInfo.getStatusCount();
					String countBlogs = userInfo.getBlogsCount();
					String countVisitors = userInfo.getVisitorsCount();
					String countAlbums = userInfo.getAlbumsCount();
					String gender = userInfo.getGender();
					String birthday = userInfo.getBirthday();
					String homeTown = userInfo.getHomeTown();
					String netWork = userInfo.getNetWork();
					String star = userInfo.getStar();
					String tag=userInfo.getTag();
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TWITTER)) {

						String str = userInfo.getUserName() + " " + "@"
								+ userInfo.getScreenName();
						SpannableString spanString = new SpannableString(str);
						spanString.setSpan(new RelativeSizeSpan(1.0f), 0,
								userInfo.getUserName().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						spanString.setSpan(new RelativeSizeSpan(0.9f), userInfo
								.getUserName().length() + 1, str.length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						name.setText(spanString);
					} else {
						name.setText(userInfo.getScreenName());
					}
					if(statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_TENCENT)){
						String [] tags=tag.split("]");
						tagCount.setText(String.valueOf(tags.length-1));
						
					}
					if(statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)){
						tagCount.setText("查看");
					}
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
						screenName = name.getText().toString();
					}
					// followCount.setText(Html.fromHtml("<u>"
					// + (countFollow == null ? "0" : countFollow)
					// + "</u>"));
					// followedCount.setText(Html.fromHtml("<u>"
					// + (countFollowed == null ? "0" : countFollowed)
					// + "</u>"));
					followCount
							.setText(countFollow == null ? "0" : countFollow);
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_RENREN)) {
						followedCount.setText(countVisitors == null ? "0"
								: countVisitors);
					} else {
						followedCount.setText(countFollowed == null ? "0"
								: countFollowed);
					}
					if (service
							.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
						// microblogCount
						// .setText(Html.fromHtml("<u>"
						// + getResources().getString(
						// R.string.profile_user_tweets)
						// + "</u>"));

						microblogCount.setText(getResources().getString(
								R.string.profile_user_tweets));

					} else {

						// microblogCount.setText(Html.fromHtml("<u>"
						// + (countStatus == null ? "0" : countStatus)
						// + "</u>"));
						microblogCount.setText(countStatus == null ? "0"
								: countStatus);

					}

					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_RENREN)) {
						trendCount.setText(countBlogs == null ? "0"
								: countBlogs);
						if (gender.equals(String.valueOf(1))) {
							ger = getResources().getString(R.string.male);
						} else if (gender.equals(String.valueOf(0))) {
							ger = getResources().getString(R.string.female);
						} else {
							ger = "";
						}
						genderView.setText(gender == null ? " " : ": " + ger);
						birthdayView.setText(birthday == null ? " " : ": "
								+ birthday);
						homeTownView.setText(homeTown == null ? " " : ": "
								+ homeTown);
						netWorkView.setText(netWork == null ? " " : ": "
								+ netWork);
						if (star.equals(String.valueOf(1))) {
							renrenStar.setVisibility(View.VISIBLE);
						} else {
							renrenStar.setVisibility(View.GONE);
						}
					}

					description
							.setText(userInfo.getDescription() == "null" ? ""
									: userInfo.getDescription());
					String listCount = userInfo.getListCount();
					// TwitterlistCount.setText(Html.fromHtml("<u>"
					// + (listCount == null ? "0" : listCount) + "</u>"));
					if (statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_RENREN)) {
						replaceEmotion(userInfo.getDescription() == "null" ? ""
								: userInfo.getDescription());
						TwitterlistCount.setText(countAlbums == null ? "0"
								: countAlbums);
					} else {
						TwitterlistCount.setText(listCount == null ? "0"
								: listCount);
					}

					try {

						if (imageShow.equals(BrowseModeActivity.select[0])
								|| imageShow
										.equals(BrowseModeActivity.select[1])) {
							try {
								setUserImage(userImage,
										userInfo.getUserImageURL());
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							setUserImage(userImage, null);
						}

						System.gc();

					} catch (OutOfMemoryError e) {
						setUserImage(userImage, null);
						// userImage.setImageResource(R.drawable.user_profile_image);
					}

					if ((statusData.getCurrentService().equals(
							IGeneral.SERVICE_NAME_SINA) || statusData
							.getCurrentService().equals(
									IGeneral.SERVICE_NAME_TENCENT))
							|| statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_SOHU)) {

						if (userInformation.getVerified()) {
							ImageView verfiedImage = (ImageView) findViewById(R.id.sina_user_verified);
							if (statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_SINA)) {
								verfiedImage
										.setBackgroundResource(R.drawable.sina_user_verified);
							} else if (statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_TENCENT)) {
								verfiedImage
										.setBackgroundResource(R.drawable.tencent_user_verified);
							} else if (statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_SOHU)) {
								verfiedImage
										.setBackgroundResource(R.drawable.sohu_user_verified);
							}
							verfiedImage.setVisibility(View.VISIBLE);
						}

					}

					if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
							|| service.equals(IGeneral.SERVICE_NAME_SINA)
							|| service.equals(IGeneral.SERVICE_NAME_SOHU)) {

						// Prepare Parameters For Show Relation
						Map<String, Object> parameters;
						parameters = new HashMap<String, Object>();
						parameters.put("screen_name", screenName);
						parameters.put("user_name", userName);

						if (uid == null) {
							parameters.put("target_id", userInfo.getUid());
						} else {
							parameters.put("target_id", uid);
						}
						parameters.put("source_id", currentAccount.getUid());

						// For Follow5
						parameters.put("id", currentAccount.getUid());
						try {
							apiServiceInterface.request(
									currentAccount.getService(),
									CommHandler.TYPE_SHOW_RELATION,
									apiServiceListener, parameters);
						} catch (RemoteException e) {
							e.printStackTrace();
						}

					}

					if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {

						String followFlag = userInfo.getFollow();
						String followedFlag = userInfo.getFollowed();

						if ("false".equals(followFlag)
								&& "false".equals(followedFlag)) {
							// followedStatusSet
							// .setText(Html
							// .fromHtml("<u>"
							// + getResources()
							// .getString(
							// R.string.profile_activity_set_follow)
							// + "</u>"));

							followedStatusSet.setText(getResources().getString(
									R.string.profile_activity_set_follow));
							followedStatusImage
									.setImageResource(R.drawable.profile_no_followed);
						} else if ("true".equals(followFlag)
								&& "false".equals(followedFlag)) {
							// followedStatusSet
							// .setText(Html
							// .fromHtml("<u>"
							// + getResources()
							// .getString(
							// R.string.profile_activity_set_follow)
							// + "</u>"));

							followedStatusSet.setText(getResources().getString(
									R.string.profile_activity_set_follow));
							followedStatusImage
									.setImageResource(R.drawable.profile_follower);
						} else if ("true".equals(followFlag)
								&& "true".equals(followedFlag)) {
							// followedStatusSet
							// .setText(Html
							// .fromHtml("<u>"
							// + getResources()
							// .getString(
							// R.string.profile_activity_set_unfollow)
							// + "</u>"));
							//
							followedStatusSet.setText(getResources().getString(
									R.string.profile_activity_set_unfollow));
							followedStatusImage
									.setImageResource(R.drawable.profile_each_other_followed);
						} else if ("false".equals(followFlag)
								&& "true".equals(followedFlag)) {
							// followedStatusSet
							// .setText(Html
							// .fromHtml("<u>"
							// + getResources()
							// .getString(
							// R.string.profile_activity_set_unfollow)
							// + "</u>"));

							followedStatusSet.setText(getResources().getString(
									R.string.profile_activity_set_unfollow));
							followedStatusImage
									.setImageResource(R.drawable.profile_follow);
						}
					}
				} else if (type == CommHandler.TYPE_SHOW_RELATION) {

					// Initial Check Box
					// (relation[0]--following/relation[1]--followed_by)
					String[] relation = new String[2];
					// relation = TwitterParseHandler.parseRelation(message);
					ParseHandler parseHandler = new ParseHandler();
					if (Integer.parseInt(statusCode) == 200) {
						relation = (String[]) parseHandler.parser(service,
								type, statusCode, message);
					} else {
						relation[0] = "false";
					}

					userInformation.setFollow(relation[0]);
					userInformation.setFollowed(relation[1]);
					if (relation[0] != null && relation[0].equals("true")
							&& relation[1] != null
							&& relation[1].equals("true")) {
						// followedStatusSet.setText(Html.fromHtml("<u>"
						// + getResources().getString(
						// R.string.profile_activity_set_unfollow)
						// + "</u>"));

						followedStatusSet.setText(getResources().getString(
								R.string.profile_activity_set_unfollow));
						followedStatusImage
								.setImageResource(R.drawable.profile_each_other_followed);
					} else if (relation[0] != null
							&& relation[0].equals("true")
							&& relation[1] != null
							&& relation[1].equals("false")) {
						// followedStatusSet.setText(Html.fromHtml("<u>"
						// + getResources().getString(
						// R.string.profile_activity_set_follow)
						// + "</u>"));

						followedStatusSet.setText(getResources().getString(
								R.string.profile_activity_set_follow));
						followedStatusImage
								.setImageResource(R.drawable.profile_follower);
					} else if (relation[0] != null
							&& relation[0].equals("false")
							&& relation[1] != null
							&& relation[1].equals("true")) {
						// followedStatusSet.setText(Html.fromHtml("<u>"
						// + getResources().getString(
						// R.string.profile_activity_set_unfollow)
						// + "</u>"));

						followedStatusSet.setText(getResources().getString(
								R.string.profile_activity_set_unfollow));
						followedStatusImage
								.setImageResource(R.drawable.profile_follow);
					} else {
						// followedStatusSet.setText(Html.fromHtml("<u>"
						// + getResources().getString(
						// R.string.profile_activity_set_follow)
						// + "</u>"));

						followedStatusSet.setText(getResources().getString(
								R.string.profile_activity_set_follow));
						followedStatusImage
								.setImageResource(R.drawable.profile_no_followed);
					}

					if (service.equals(IGeneral.SERVICE_NAME_SINA)) {

						// Prepare Parameters For Show Relation
						Map<String, Object> parameters;
						parameters = new HashMap<String, Object>();
						if (uid == null) {
							parameters.put("uid", userInformation.getUid());
						} else {
							parameters.put("uid", uid);
						}

						try {
							apiServiceInterface.request(
									currentAccount.getService(),
									CommHandler.TYPE_GET_TREND_LIST,
									apiServiceListener, parameters);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						//
					}

				} else if (type == CommHandler.TYPE_GET_TREND_LIST) {

					ParseHandler parseHandler = new ParseHandler();
					trendInfo = (ArrayList<TrendInfo>) parseHandler.parser(
							service, type, statusCode, message);
					// trendCount.setText(Html.fromHtml("<u>"
					// + (trendInfo.size() == 0 ? 0 : trendInfo.size())
					// + "</u>"));

					trendCount.setText(trendInfo.size() == 0 ? "0" : String
							.valueOf(trendInfo.size()));
				}
			}

			if (!"200".equals(statusCode)) {
				Toast.makeText(
						ProfileActivity.this,
						ErrorMessage.getErrorMessage(ProfileActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		setLayoutResId(R.layout.activity_profile);

		headerBack = (Button) findViewById(R.id.head_back);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerName.setText(R.string.profile);
		headerHome.setBackgroundResource(R.drawable.main_home);

		relative1 = (RelativeLayout) findViewById(R.id.linear_follow);
		relative2 = (RelativeLayout) findViewById(R.id.linear_followed);
		relative3 = (RelativeLayout) findViewById(R.id.linear_microblog);
		relative4 = (RelativeLayout) findViewById(R.id.linear_trend);
		relative5 = (RelativeLayout) findViewById(R.id.linear_list);
		relative6 = (RelativeLayout) findViewById(R.id.linear_mood);
		relative7 = (RelativeLayout) findViewById(R.id.linear_tag);

		// Find Views
		profile_edit = (TextView) findViewById(R.id.profile_edit);
		userImage = (WebView) findViewById(R.id.profile_user_image);
		name = (TextView) findViewById(R.id.profile_name);
		genderView = (TextView) findViewById(R.id.gender);
		birthdayView = (TextView) findViewById(R.id.birthday);
		homeTownView = (TextView) findViewById(R.id.home);
		netWorkView = (TextView) findViewById(R.id.network);
		follow = (TextView) findViewById(R.id.profile_follow);
		followCount = (TextView) findViewById(R.id.profile_follow_count);
		followed = (TextView) findViewById(R.id.profile_followed);
		followedCount = (TextView) findViewById(R.id.profile_followed_count);
		description = (TextView) findViewById(R.id.profile_description);
		microblog = (TextView) findViewById(R.id.profile_microblog_1);
		microblogCount = (TextView) findViewById(R.id.profile_microblog_count);
		trend = (TextView) findViewById(R.id.profile_trends);
		trendCount = (TextView) findViewById(R.id.profile_trend_count);
		followedStatusImage = (ImageView) findViewById(R.id.profile_followed_status);
		followedStatusSet = (TextView) findViewById(R.id.profile_set_followed_status);
		Twitterlist = (TextView) findViewById(R.id.profile_lists);
		TwitterlistCount = (TextView) findViewById(R.id.profile_list_count);
		mood = (TextView) findViewById(R.id.profile_mood);
		moodCount = (TextView) findViewById(R.id.profile_mood_count);
		mood.setText(getString(R.string.discovery_tencent_mood));
		moodCount.setText(getString(R.string.profile_user_mood));
		tag = (TextView) findViewById(R.id.profile_tag);
		tagCount = (TextView) findViewById(R.id.profile_tag_count);
		tag.setText(getString(R.string.discovery_sina_tag));
		baseInfo = (LinearLayout) findViewById(R.id.base_info);
		setFollow = (RelativeLayout) findViewById(R.id.linear_set_follow);
		renrenStar = (ImageView) findViewById(R.id.renren_star);

		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TENCENT)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)) {
			profile_edit.setVisibility(View.VISIBLE);

		}

		userImage.setBackgroundColor(Color.TRANSPARENT);
		userImage.setHorizontalScrollBarEnabled(false);
		userImage.setVerticalScrollBarEnabled(false);

		// Set Listener
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);

		relative1.setOnClickListener(this);
		relative2.setOnClickListener(this);
		relative3.setOnClickListener(this);
		relative4.setOnClickListener(this);
		relative6.setOnClickListener(this);
		relative7.setOnClickListener(this);
		followedStatusSet.setOnClickListener(this);
		relative5.setOnClickListener(this);
		profile_edit.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		super.onStart();
		isRunning = true;
		screenName = getIntent().getExtras().getString("name");
		uid = getIntent().getExtras().getString("uid");
		userName = getIntent().getExtras().getString("user_name");

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();
		settingData = crowdroidApplication.getSettingData();

		imageShow = settingData.getSelectionShowImage();

		if (screenName.equals("")) {
			// Get Screen Name From Current Account
			screenName = currentAccount.getUserScreenName();
			userName = currentAccount.getUserName();
			uid = currentAccount.getUid();
		}
		
		initViewsByService(currentAccount.getService());

		String fontColor = settingData.getFontColor();

		// Set Color
		setColor(fontColor);

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

	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	//
	// switch (v.getId()) {
	// case R.id.profile_follow_count: {
	// if (currentAccount.getUserName().equals(
	// userName)
	// || currentAccount.getUserScreenName().equals(
	// screenName)) {
	//
	// Intent intent = new Intent(ProfileActivity.this,
	// FollowActivity.class);
	// // intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	// intent.putExtra("screenName", screenName);
	// intent.putExtra("userName", userName);
	// intent.putExtra("uid", uid);
	// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	// startActivity(intent);
	//
	// } else {
	// Toast.makeText(this, "暂时无权限查看", Toast.LENGTH_SHORT).show();
	// }
	// break;
	// }
	// case R.id.profile_followed_count: {
	//
	// Intent intent = new Intent(ProfileActivity.this,
	// FollowedActivity.class);
	// // intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	// intent.putExtra("screenName", screenName);
	// intent.putExtra("userName", userName);
	// intent.putExtra("uid", uid);
	// intent.putExtra("context", "ProfileActivity");
	// intent.putExtra("follower_count", followedCount.getText()
	// .toString());
	// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	// startActivity(intent);
	// break;
	// }
	// case R.id.profile_microblog_count: {
	//
	// Intent i = new Intent(ProfileActivity.this,
	// UserTimelineActivity.class);
	// i.putExtra("name", screenName);
	// i.putExtra("username", userName);
	// i.putExtra("uid", uid);
	// i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	// startActivity(i);
	// break;
	// }
	// case R.id.profile_list_count: {
	//
	// // Start Dialog About List
	// new ListDialog(this, screenName, uid).show();
	// break;
	// }
	// case R.id.profile_trend_count: {
	//
	// // Start Dialog About Trend
	// new TrendDialog(this, screenName, uid, trendInfo).show();
	// break;
	// }
	// case R.id.profile_set_followed_status: {
	//
	// if (userInformation != null) {
	// // Prepare Parameters
	// Map<String, Object> parameters;
	// parameters = new HashMap<String, Object>();
	// parameters.put("id", userInformation.getUid());
	// parameters.put("username", userInformation.getUserName());
	// if (userInformation.getFollow().equals("false")
	// && userInformation.getFollowed().equals("false")) {
	// parameters.put("type", "create");
	// followedStatusSet.setText(getResources().getString(
	// R.string.profile_activity_set_unfollow));
	// followedStatusImage
	// .setImageResource(R.drawable.profile_follow);
	// userInformation.setFollow("false");
	// userInformation.setFollowed("true");
	// } else if (userInformation.getFollow().equals("true")
	// && userInformation.getFollowed().equals("false")) {
	// parameters.put("type", "create");
	//
	// followedStatusSet.setText(getResources().getString(
	// R.string.profile_activity_set_unfollow));
	// followedStatusImage
	// .setImageResource(R.drawable.profile_each_other_followed);
	// userInformation.setFollow("true");
	// userInformation.setFollowed("true");
	// } else if (userInformation.getFollow().equals("true")
	// && userInformation.getFollowed().equals("true")) {
	// parameters.put("type", "destroy");
	//
	// followedStatusSet.setText(getResources().getString(
	// R.string.profile_activity_set_follow));
	// followedStatusImage
	// .setImageResource(R.drawable.profile_follower);
	// userInformation.setFollow("true");
	// userInformation.setFollowed("false");
	// } else if (userInformation.getFollow().equals("false")
	// && userInformation.getFollowed().equals("true")) {
	// parameters.put("type", "destroy");
	// followedStatusSet.setText(getResources().getString(
	// R.string.profile_activity_set_follow));
	// followedStatusImage
	// .setImageResource(R.drawable.profile_no_followed);
	// userInformation.setFollow("false");
	// userInformation.setFollowed("false");
	// }
	// try {
	// apiServiceInterface.request(currentAccount.getService(),
	// CommHandler.TYPE_SET_FOLLOW, apiServiceListener,
	// parameters);
	// } catch (RemoteException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// break;
	//
	// }
	// default: {
	//
	// }
	// }
	//
	// return false;
	// }

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.head_refresh: {
			Intent home = new Intent(ProfileActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(home);
			break;
		}

		case R.id.linear_follow: {

			Intent intent = new Intent(ProfileActivity.this,
					FollowActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra("screenName", screenName);
			intent.putExtra("userName", userName);
			intent.putExtra("uid", uid);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);

			break;
		}
		case R.id.linear_followed: {

			Intent intent = new Intent(ProfileActivity.this,
					FollowedActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.putExtra("screenName", screenName);
			intent.putExtra("userName", userName);
			intent.putExtra("uid", uid);
			intent.putExtra("context", "ProfileActivity");
			intent.putExtra("follower_count", followedCount.getText()
					.toString());
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);

			break;
		}
		case R.id.linear_microblog: {

			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				Intent i = new Intent(ProfileActivity.this,
						StatusTimelineActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("name", screenName);
				i.putExtra("username", userName);
				i.putExtra("uid", uid);
				i.putExtra("head_url", userInformation.getUserImageURL());
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			} else {

				Intent i = new Intent(ProfileActivity.this,
						UserTimelineActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("name", screenName);
				i.putExtra("username", userName);
				i.putExtra("uid", uid);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
			break;
		}
		case R.id.linear_list: {

			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				Intent i = new Intent(ProfileActivity.this,
						AlbumsTimelineActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("uid", uid);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);

			} else {
				// Start Dialog About List
				new ListDialog(this, screenName, uid).show();
			}
		}
			break;
		case R.id.linear_trend: {

			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				Intent i = new Intent(ProfileActivity.this,
						BlogTimelineActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("name", screenName);
				i.putExtra("username", userName);
				i.putExtra("uid", uid);
				i.putExtra("head_url", userInformation.getUserImageURL());
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);

			} else {

				// Start Dialog About Trend
				new TrendDialog(this, screenName, uid, trendInfo).show();
			}
			break;
		}

		case R.id.linear_mood: {
			Intent i = new Intent(ProfileActivity.this,
					MoodStatusTimelineActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("name", screenName);
			i.putExtra("user_name", userName);
			i.putExtra("uid", uid);
			i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(i);
			break;
		}
		case R.id.linear_tag: {
			Intent i = new Intent(ProfileActivity.this,
					TagsListActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("name", screenName);
			i.putExtra("user_name", userName);
			i.putExtra("uid", uid);
			i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(i);
			break;
		}
		case R.id.profile_set_followed_status: {

			if (userInformation != null) {
				// Prepare Parameters
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("id", userInformation.getUid());
				parameters.put("username", userInformation.getUserName());
				if (userInformation.getFollow() != null
						&& userInformation.getFollowed() != null) {
					if (userInformation.getFollow().equals("false")
							&& userInformation.getFollowed().equals("false")) {
						parameters.put("type", "create");

						followedStatusSet.setText(getResources().getString(
								R.string.profile_activity_set_unfollow));
						followedStatusImage
								.setImageResource(R.drawable.profile_follow);
						userInformation.setFollow("false");
						userInformation.setFollowed("true");
					} else if (userInformation.getFollow().equals("true")
							&& userInformation.getFollowed().equals("false")) {
						parameters.put("type", "create");
						followedStatusSet.setText(getResources().getString(
								R.string.profile_activity_set_unfollow));
						followedStatusImage
								.setImageResource(R.drawable.profile_each_other_followed);
						userInformation.setFollow("true");
						userInformation.setFollowed("true");
					} else if (userInformation.getFollow().equals("true")
							&& userInformation.getFollowed().equals("true")) {
						parameters.put("type", "destroy");

						followedStatusSet.setText(getResources().getString(
								R.string.profile_activity_set_follow));
						followedStatusImage
								.setImageResource(R.drawable.profile_follower);
						userInformation.setFollow("true");
						userInformation.setFollowed("false");
					} else if (userInformation.getFollow().equals("false")
							&& userInformation.getFollowed().equals("true")) {
						parameters.put("type", "destroy");
						followedStatusSet.setText(getResources().getString(
								R.string.profile_activity_set_follow));
						followedStatusImage
								.setImageResource(R.drawable.profile_no_followed);
						userInformation.setFollow("false");
						userInformation.setFollowed("false");
					}
				}

				try {
					apiServiceInterface.request(currentAccount.getService(),
							CommHandler.TYPE_SET_FOLLOW, apiServiceListener,
							parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			break;

		}
		case R.id.profile_edit: {
			Intent intent = new Intent(ProfileActivity.this,
					UpdateProfileActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("nick", userInformation);
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		default: {

		}
		}

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("screen_name", screenName);
		parameters.put("uid", uid);
		parameters.put("username", userName);

		try {
			apiServiceInterface.request(currentAccount.getService(),
					CommHandler.TYPE_GET_USER_INFO, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;
	}

	private void initViewsByService(String service) {

		headerName.setText(screenName);
		TextView line1 = (TextView) findViewById(R.id.line1);
		TextView line2 = (TextView) findViewById(R.id.line2);
		TextView line3 = (TextView) findViewById(R.id.line3);
		TextView line4 = (TextView) findViewById(R.id.line4);
		TextView line5 = (TextView) findViewById(R.id.line5);
		TextView line6 = (TextView) findViewById(R.id.line6);
		TextView line7 = (TextView) findViewById(R.id.line7);
		TextView line8 = (TextView) findViewById(R.id.line8);

		RelativeLayout relativeFollow = (RelativeLayout) findViewById(R.id.linear_follow);
		RelativeLayout relativeFollowed = (RelativeLayout) findViewById(R.id.linear_followed);
		RelativeLayout relativeMicroblog = (RelativeLayout) findViewById(R.id.linear_microblog);
		RelativeLayout relativeTrend = (RelativeLayout) findViewById(R.id.linear_trend);
		RelativeLayout relativeList = (RelativeLayout) findViewById(R.id.linear_list);
		RelativeLayout relativeSetFollow = (RelativeLayout) findViewById(R.id.linear_set_follow);
		RelativeLayout relativeMood = (RelativeLayout) findViewById(R.id.linear_mood);
		RelativeLayout relativeTag = (RelativeLayout) findViewById(R.id.linear_tag);

		if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
			relativeFollow.setVisibility(View.GONE);
			relativeFollowed.setVisibility(View.GONE);
			relativeTrend.setVisibility(View.GONE);
			relativeList.setVisibility(View.GONE);
			relativeSetFollow.setVisibility(View.GONE);
			relativeMood.setVisibility(View.GONE);
			relativeTag.setVisibility(View.GONE);
			line1.setVisibility(View.GONE);
			line2.setVisibility(View.GONE);
			line3.setVisibility(View.GONE);
			line4.setVisibility(View.GONE);
			line5.setVisibility(View.GONE);
			line6.setVisibility(View.GONE);
			line7.setVisibility(View.GONE);
			line8.setVisibility(View.GONE);

		} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER)) {
			relativeTrend.setVisibility(View.GONE);
			line5.setVisibility(View.GONE);
			relativeMood.setVisibility(View.GONE);
			line6.setVisibility(View.GONE);
			relativeTag.setVisibility(View.GONE);
			line7.setVisibility(View.GONE);

		} else if (service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			relativeTrend.setVisibility(View.GONE);
			relativeSetFollow.setVisibility(View.GONE);
			line1.setVisibility(View.GONE);
			line5.setVisibility(View.GONE);
			relativeMood.setVisibility(View.GONE);
			line6.setVisibility(View.GONE);
			relativeTag.setVisibility(View.GONE);
			line7.setVisibility(View.GONE);
		} else if (service.equals(IGeneral.SERVICE_NAME_SINA)) {
			relativeList.setVisibility(View.GONE);
			line6.setVisibility(View.GONE);
			relativeMood.setVisibility(View.GONE);
			line6.setVisibility(View.GONE);
		} else if (service.equals(IGeneral.SERVICE_NAME_TENCENT)) {
			relativeTrend.setVisibility(View.GONE);
			relativeList.setVisibility(View.GONE);
			line5.setVisibility(View.GONE);
			line6.setVisibility(View.GONE);

		} else if (service.equals(IGeneral.SERVICE_NAME_SOHU)) {
			relativeTrend.setVisibility(View.GONE);
			relativeList.setVisibility(View.GONE);
			line5.setVisibility(View.GONE);
			line6.setVisibility(View.GONE);
			relativeMood.setVisibility(View.GONE);
			line6.setVisibility(View.GONE);
			relativeTag.setVisibility(View.GONE);
			line7.setVisibility(View.GONE);

		} else if (service.equals(IGeneral.SERVICE_NAME_RENREN)) {
			baseInfo.setVisibility(View.VISIBLE);
			renrenStar.setVisibility(View.VISIBLE);
			setFollow.setVisibility(View.GONE);
			follow.setText(R.string.friends);
			followed.setText(R.string.visitors);
			microblog.setText(R.string.status);
			trend.setText(R.string.blog);
			Twitterlist.setText(R.string.album);
			relativeMood.setVisibility(View.GONE);
			line6.setVisibility(View.GONE);
			relativeTag.setVisibility(View.GONE);
			line8.setVisibility(View.GONE);

		}

		if (currentAccount.getUserName().equals(userName)
				|| (currentAccount.getUserScreenName() != null && currentAccount
						.getUserScreenName().equals(screenName))) {
			relativeSetFollow.setVisibility(View.GONE);
			line1.setVisibility(View.GONE);
		} else {
			profile_edit.setVisibility(View.GONE);
		}
	}

	private void setColor(String fontColor) {

		int color = Integer.valueOf(fontColor);
		name.setTextColor(color);
		description.setTextColor(color);

	}

	@Override
	protected void refreshByMenu() {

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

	private void setUserImage(WebView imageWebView, String imageUrl) {

		// Set attributes
		imageWebView.setFocusable(false);
		imageWebView.setClickable(true);
		imageWebView.setVerticalScrollBarEnabled(false);
		imageWebView.setHorizontalScrollBarEnabled(false);
		imageWebView.setBackgroundColor(Color.TRANSPARENT);

		if (imageUrl == null || imageUrl.equals("")) {
			// Default Image URL
			imageWebView.setBackgroundResource(R.drawable.default_user_image);
			return;
		}

		StringBuffer htmlData = new StringBuffer();
		htmlData.append("<center>");
		htmlData.append("<img style='max-height: 60px; max-width:60px;' src='");
		htmlData.append(imageUrl);
		htmlData.append("' /></a>");
		htmlData.append("<br>");
		htmlData.append("<center>");
		imageWebView.loadData(htmlData.toString(), "text/html", "utf-8");
	}

	private void replaceEmotion(String initStatus) {
		final SpannableString spanString = new SpannableString(initStatus);
		AsyncDataLoad imageLoader = new AsyncDataLoad();
		String phrase = null;
		ArrayList<String> indexEmotionsFlag = null;
		indexEmotionsFlag = TagAnalysis.getEmotionsIndexFlag(initStatus,
				statusData.getCurrentService());

		int numberEmotionFlag = indexEmotionsFlag.size();
		if (numberEmotionFlag > 0) {
			for (int i = 0; i < numberEmotionFlag / 2; i++) {

				final int start = Integer.valueOf(indexEmotionsFlag.get(i * 2));
				final int end = Integer.valueOf(indexEmotionsFlag
						.get(i * 2 + 1));
				phrase = initStatus.substring(start, end);
				if (phrase != null) {
					for (final EmotionInfo emotion : SendMessageActivity.emotionList) {
						if (phrase.equals(emotion.getPhrase())) {
							String url = emotion.getUrl();
							if (statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_RENREN)) {
								// renren(微笑)
								phrase = phrase.substring(1,
										phrase.length() - 1);

								try {
									url = url.replace(phrase,
											URLEncoder.encode(phrase, "UTF-8"));
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							imageLoader.loadDrawable(url, new ImageCallback() {
								public void imageLoaded(Drawable imageDrawable,
										String imageUrl) {

									imageDrawable.setBounds(0, 0, 22, 22);
									ImageSpan span = new ImageSpan(
											imageDrawable,
											ImageSpan.ALIGN_BASELINE);
									spanString.setSpan(span, start, end,
											Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

									description.setText(spanString);
									description
											.setMovementMethod(LinkMovementMethod
													.getInstance());
								}
							});
						}

					}
				}
			}
		}
	}

}
