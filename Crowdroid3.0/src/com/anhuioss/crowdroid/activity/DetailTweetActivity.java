package com.anhuioss.crowdroid.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.BasicUserSearchActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.dialog.SelectReplayUserDialog;
import com.anhuioss.crowdroid.dialog.TranslateDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.sns.operations.DetailBlogActivity;
import com.anhuioss.crowdroid.sns.operations.SNSUserSerchActivity;
import com.anhuioss.crowdroid.util.AsyncDataLoad;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.GridViewAdapter;
import com.anhuioss.crowdroid.util.MyClickableSpan;
import com.anhuioss.crowdroid.util.TagAnalysis;
import com.anhuioss.crowdroid.util.AsyncDataLoad.ImageCallback;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DetailTweetActivity extends BasicActivity implements
		OnClickListener, OnTouchListener, ServiceConnection {

	private CrowdroidApplication crowdroidApplication;
	// content
	private WebView userHead;
	private ImageView favorite;
	private ImageView verfiedImage;
	private TextView userName;
	private TextView statusTextView;
	private TextView retweetStatusTextView;
	private TextView timeStamp;
	private RelativeLayout history;
	private TextView reply_h;
	private Button retweetCount;
	private Button commentCount;
	private LinearLayout linearStatusImage;
	private LinearLayout linearStatusFile;
	private LinearLayout linearRetweetStatusFile;
	private RelativeLayout countRelative;
	private RelativeLayout retweetRelative;
	// webview
	private WebView statusImageWebView;
	private WebView retweetStatusImageWebView;
	private WebView statusFileWebView;
	private WebView retweetStatusFileWebView;

	// head
	private Button headBack;
	private TextView headName;
	private Button headHome;

	// bottom
	private LinearLayout linearBottomRetweet;
	private LinearLayout linearBottomComment;
	private LinearLayout linearBottomQT;
	private LinearLayout linearBottomRT;
	private LinearLayout linearBottomReply;
	private LinearLayout linearBottomFavorite;
	private LinearLayout linearBottomProfile;
	private LinearLayout linearBottomEmail;
	private LinearLayout linearBottomDM;
	private LinearLayout linearBottomTranslate;
	private LinearLayout linearBottomDelete;
	private LinearLayout linearBottomDetailBlog;

	private Button bottomRetweet;
	private Button bottomComment;
	private Button bottomQT;
	private Button bottomRT;
	private Button bottomReply;
	private Button bottomFavorite;
	private Button bottomProfile;
	private Button bottomEmail;
	private Button bottomDM;
	private Button bottomTranslate;
	private Button bottomDelete;
	private Button bottomDetailBlog;

	// Data
	private StatusData statusData;
	private AccountData currentAccount;
	private SettingData settingData;

	// bundle
	private ArrayList<TimeLineInfo> timeLineDataList;
	private TimeLineInfo timeLineInfo;
	private int commType = 0;
	private String screenName;

	// Progress Dialog
	private HandleProgressDialog progress;
	/** Text */
	private String initStatus;
	private String initPreviewState;

	// image hrefUrls
	private String hrefImageUrlsData = "";
	private String hrefRetweetedImageUrlsData = "";
	private String hrefAttachUrls = "";
	private String hrefRetweetedAttachUrls = "";
	/** Flag for First Displayed */
	// private boolean isFirstDisplayed = true;

	private int page = 0;

	/** API Service Interface */
	private ApiServiceInterface apiServiceInterface;

	/** QT and Comment Dialog for Sina **/
	// private EditText commentText;
	private AlertDialog dlg;
	private AlertDialog previewDialog;
	private Handler mHandler = new Handler();

	private String fontSize;
	private String fontColor;

	// ==============================================================================
	// --------------------------------------------------------
	/**
	 * API Service Listener
	 */
	// --------------------------------------------------------
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			setProgressEnable(false);

			if (statusCode != null && statusCode.equals("200")) {

				if (type == CommHandler.TYPE_GET_EMOTION) {

					ParseHandler parseHandler = new ParseHandler();
					SendMessageActivity.emotionList = (ArrayList<EmotionInfo>) parseHandler
							.parser(service, type, statusCode, message);
					if (!SendMessageActivity.emotionList.isEmpty()) {
						initEmotionView(SendMessageActivity.emotionList);
					}

				}
				if (type == CommHandler.TYPE_DESTROY) {
					finish();
				}

				// }

			} else {
				Toast.makeText(
						DetailTweetActivity.this,
						ErrorMessage.getErrorMessage(DetailTweetActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
			}

		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		setLayoutResId(R.layout.activity_detail_tweet);

		// head-----------------------------
		headBack = (Button) findViewById(R.id.head_back);
		headHome = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);

		headBack.setOnClickListener(this);
		headHome.setOnClickListener(this);

		// bottom-----------------------------------

		linearBottomRetweet = (LinearLayout) findViewById(R.id.linear_bottom_retweet);
		linearBottomComment = (LinearLayout) findViewById(R.id.linear_bottom_comment);
		linearBottomQT = (LinearLayout) findViewById(R.id.linear_bottom_qt);
		linearBottomRT = (LinearLayout) findViewById(R.id.linear_bottom_rt);
		linearBottomReply = (LinearLayout) findViewById(R.id.linear_bottom_reply);
		linearBottomFavorite = (LinearLayout) findViewById(R.id.linear_bottom_favorite);
		linearBottomProfile = (LinearLayout) findViewById(R.id.linear_bottom_profile);
		linearBottomEmail = (LinearLayout) findViewById(R.id.linear_bottom_email);
		linearBottomDM = (LinearLayout) findViewById(R.id.linear_bottom_dm);
		linearBottomTranslate = (LinearLayout) findViewById(R.id.linear_bottom_translate);
		linearBottomDelete = (LinearLayout) findViewById(R.id.linear_bottom_delete);
		linearBottomDetailBlog = (LinearLayout) findViewById(R.id.linear_bottom_detail_blog);

		bottomRetweet = (Button) findViewById(R.id.bottom_retweet);
		bottomComment = (Button) findViewById(R.id.bottom_comment);
		bottomQT = (Button) findViewById(R.id.bottom_qt);
		bottomRT = (Button) findViewById(R.id.bottom_rt);
		bottomReply = (Button) findViewById(R.id.bottom_reply);
		bottomFavorite = (Button) findViewById(R.id.bottom_favorite);
		bottomProfile = (Button) findViewById(R.id.bottom_profile);
		bottomEmail = (Button) findViewById(R.id.bottom_email);
		bottomDM = (Button) findViewById(R.id.bottom_dm);
		bottomTranslate = (Button) findViewById(R.id.bottom_translate);
		bottomDelete = (Button) findViewById(R.id.bottom_delete);
		bottomDetailBlog = (Button) findViewById(R.id.bottom_detail_blog);

		bottomRetweet.setOnClickListener(this);
		bottomComment.setOnClickListener(this);
		bottomQT.setOnClickListener(this);
		bottomRT.setOnClickListener(this);
		bottomReply.setOnClickListener(this);
		bottomFavorite.setOnClickListener(this);
		bottomProfile.setOnClickListener(this);
		bottomEmail.setOnClickListener(this);
		bottomDM.setOnClickListener(this);
		bottomTranslate.setOnClickListener(this);
		bottomDelete.setOnClickListener(this);
		bottomDetailBlog.setOnClickListener(this);

		// content--------------------------------
		userHead = (WebView) findViewById(R.id.user_image);
		userName = (TextView) findViewById(R.id.screen_name);
		timeStamp = (TextView) findViewById(R.id.update_time);
		favorite = (ImageView) findViewById(R.id.favorite);
		history = (RelativeLayout) findViewById(R.id.history_reply);
		reply_h = (TextView) findViewById(R.id.reply_history);
		verfiedImage = (ImageView) findViewById(R.id.user_verified);
		retweetCount = (Button) findViewById(R.id.retweet_count);
		commentCount = (Button) findViewById(R.id.comment_count);
		statusTextView = (TextView) findViewById(R.id.translateStatus);
		retweetStatusTextView = (TextView) findViewById(R.id.retweetStatusTextView);
		countRelative = (RelativeLayout) findViewById(R.id.count_relative);
		retweetRelative = (RelativeLayout) findViewById(R.id.retweetRelative);
		linearStatusImage = (LinearLayout) findViewById(R.id.linearStatusImage);
		linearStatusFile = (LinearLayout) findViewById(R.id.linearStatusFile);
		linearRetweetStatusFile = (LinearLayout) findViewById(R.id.linearRetweetStatusFile);

		userHead.setClickable(true);
		favorite.setOnClickListener(this);
		reply_h.setOnClickListener(this);
		retweetCount.setOnClickListener(this);
		commentCount.setOnClickListener(this);

		statusImageWebView = (WebView) findViewById(R.id.statusImageWebView);
		statusFileWebView = (WebView) findViewById(R.id.statusFileWebView);
		retweetStatusImageWebView = (WebView) findViewById(R.id.retweetStatusImageWebView);
		retweetStatusFileWebView = (WebView) findViewById(R.id.retweetStatusFileWebView);

		// Web Settings
		statusImageWebView.setBackgroundColor(Color.TRANSPARENT);
		statusImageWebView.setVerticalScrollBarEnabled(false);
		statusImageWebView.setHorizontalScrollBarEnabled(false);

		statusFileWebView.setBackgroundColor(Color.TRANSPARENT);
		statusFileWebView.setVerticalScrollBarEnabled(false);
		statusFileWebView.setHorizontalScrollBarEnabled(false);

		retweetStatusImageWebView.setBackgroundColor(Color.TRANSPARENT);
		retweetStatusImageWebView.setVerticalScrollBarEnabled(false);
		retweetStatusImageWebView.setHorizontalScrollBarEnabled(false);

		retweetStatusFileWebView.setBackgroundColor(Color.TRANSPARENT);
		retweetStatusFileWebView.setVerticalScrollBarEnabled(false);
		retweetStatusFileWebView.setHorizontalScrollBarEnabled(false);
	}

	@Override
	public void onStart() {
		super.onStart();
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();
		initPreviewState = settingData.getSelectionShowImage();

		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();
		screenName = crowdroidApplication.getAccountList().getCurrentAccount()
				.getUserScreenName();

		fontSize = settingData.getFontSize();
		fontColor = settingData.getFontColor();

		// bundle status
		Bundle bundle = this.getIntent().getExtras();
		timeLineDataList = (ArrayList<TimeLineInfo>) bundle
				.getSerializable("timelinedatalist");
		timeLineInfo = (TimeLineInfo) bundle.getSerializable("timelineinfo");
		commType = bundle.getInt("commtype");

		initDetailView();
		initStatus = timeLineInfo.getStatus();
		setStatusState();
		if (!"".equals(timeLineInfo.getRetweetedStatus())
				|| timeLineInfo.getRetweetedStatus() == null) {
			setRetweetedStatusState();
		} else {
			retweetStatusTextView.setVisibility(View.GONE);
			retweetRelative.setVisibility(View.GONE);

		}

		// Bind Service
		Intent intent = new Intent(DetailTweetActivity.this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onStop() {
		super.onStop();

		closeProgressDialog();

		// Unbind Service
		unbindService(this);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {
		case R.id.statusImageWebView: {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (!hrefAttachUrls.equals("")
						&& hrefAttachUrls.contains("/files/")) {
					Uri uri = Uri.parse(hrefAttachUrls);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(DetailTweetActivity.this,
							PreviewImageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					Bundle bundle = new Bundle();
					bundle.putString("url", hrefImageUrlsData);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}

			break;
		}
		case R.id.retweetStatusImageWebView: {

			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (!hrefRetweetedAttachUrls.equals("")
						&& hrefRetweetedAttachUrls.contains("/files/")) {
					Uri uri = Uri.parse(hrefRetweetedAttachUrls);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(DetailTweetActivity.this,
							PreviewImageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					Bundle bundle = new Bundle();
					bundle.putString("url", hrefRetweetedImageUrlsData);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
			break;
		}
		}
		return false;
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.head_refresh: {

			Intent home = new Intent(DetailTweetActivity.this,
					HomeTimelineActivity.class);
			startActivity(home);
			finish();
			break;
		}
		case R.id.bottom_retweet: {
			// retweet
			Intent detail = new Intent(DetailTweetActivity.this,
					RetweetMessageActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("timelineinfo", timeLineInfo);
			bundle.putSerializable("timelinedatalist", timeLineDataList);
			detail.putExtras(bundle);
			startActivity(detail);
			break;
		}
		case R.id.bottom_comment: {
			comment();
			break;
		}
		case R.id.bottom_qt: {
			qt();
			break;
		}
		case R.id.bottom_rt: {
			rt();
			break;
		}
		case R.id.bottom_reply: {
			reply();
			break;
		}
		case R.id.bottom_favorite: {
			favorite();
			break;
		}
		case R.id.bottom_profile: {

			Intent intent = new Intent(DetailTweetActivity.this,
					ProfileActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", timeLineInfo.getUserInfo().getScreenName());
			bundle.putString("uid", timeLineInfo.getUserInfo().getUid());
			bundle.putString("user_name", timeLineInfo.getUserInfo()
					.getUserName());
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.bottom_email: {
			email();
			break;
		}
		case R.id.bottom_translate: {

			new TranslateDialog(DetailTweetActivity.this, statusTextView)
					.show();
			Toast.makeText(DetailTweetActivity.this,
					R.string.setting_translation, Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.bottom_dm: {

			Intent intent = new Intent(DetailTweetActivity.this,
					SendDMActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			Bundle bundle = new Bundle();
			bundle.putString("name", timeLineInfo.getUserInfo().getScreenName());
			bundle.putString("uid", timeLineInfo.getUserInfo().getUid());
			bundle.putString("userName", timeLineInfo.getUserInfo()
					.getUserName());
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.bottom_delete: {
			delete();
			break;
		}
		case R.id.bottom_detail_blog: {
			detailBlog();
			break;
		}
		case R.id.favorite: {
			favorite();
			break;
		}
		case R.id.reply_history: {
			Intent intent = new Intent(DetailTweetActivity.this,
					ReplyHistoryActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("messageId", timeLineInfo.getinReplyToStatusId());
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.retweet_count: {

			// Twitter users
			Intent intent = new Intent(DetailTweetActivity.this,
					RetweetedUserListActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("retweeted_id", timeLineInfo.getMessageId());
//			bundle.putString("retweet_id", "202999047232028672");
			intent.putExtras(bundle);
			startActivity(intent);

			break;
		}
		case R.id.comment_count: {

			// Twitter Lists
			Intent intent = new Intent(DetailTweetActivity.this,
					RetweetedListActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("retweeted_id", timeLineInfo.getMessageId());
//			bundle.putString("retweet_id", "202999047232028672");
			intent.putExtras(bundle);
			startActivity(intent);

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
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;
	}

	private void initDetailView() {

		// font size color
		statusTextView.setTextSize(Float.valueOf(fontSize) * 1.1f);
		retweetStatusTextView.setTextSize(Float.valueOf(fontSize) * 1.0f);
		if (fontColor.contains("-")) {
			statusTextView.setTextColor(Integer.valueOf(fontColor));
			retweetStatusTextView.setTextColor(Integer.valueOf(fontColor));
		} else {
			statusTextView.setTextColor(getResources().getColor(
					Integer.valueOf(fontColor)));
			retweetStatusTextView.setTextColor(getResources().getColor(
					Integer.valueOf(fontColor)));
		}

		// head
		headName.setText(timeLineInfo.getUserInfo().getScreenName());
		headHome.setBackgroundResource(R.drawable.main_home);

		// User Name
		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)) {
			String str = timeLineInfo.getUserInfo().getUserName() + " " + "@"
					+ timeLineInfo.getUserInfo().getScreenName();
			SpannableString spanString = new SpannableString(str);
			spanString.setSpan(new RelativeSizeSpan(1.0f), 0, timeLineInfo
					.getUserInfo().getUserName().length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spanString.setSpan(new RelativeSizeSpan(0.9f), timeLineInfo
					.getUserInfo().getUserName().length() + 1, str.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			userName.setText(spanString);
		} else if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TENCENT)) {
			userName.setText(timeLineInfo.getUserInfo().getScreenName()
					+ timeLineInfo.getLocation());
		} else {
			userName.setText(timeLineInfo.getUserInfo().getScreenName());
		}

		// Time
		timeStamp.setText(timeLineInfo.getFormatTime(statusData
				.getCurrentService()));

		// retweet comment
		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)) {

			retweetRelative.setVisibility(View.GONE);
			statusTextView.setVisibility(View.VISIBLE);

			if (timeLineInfo.isRetweeted()) {
				countRelative.setVisibility(View.VISIBLE);
				retweetCount.setText(getString(R.string.retweeted_user) + "("
						+ timeLineInfo.getRetweetCount() + ")");
				commentCount.setText(getString(R.string.retweeted_list) + "("
						+ timeLineInfo.getRetweetCount() + ")");
			}
		} else {
			statusTextView.setVisibility(View.VISIBLE);
			countRelative.setVisibility(View.GONE);
		}

		if ((statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA) || statusData
				.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT))
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)) {
			statusTextView.setVisibility(View.VISIBLE);

			if (!timeLineInfo.getUserInfo().getVerified()) {
				verfiedImage.setVisibility(View.GONE);
			} else {
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
			}
		}
		// preview
		if (initPreviewState.equals(BrowseModeActivity.select[0])
				|| initPreviewState.equals(BrowseModeActivity.select[1])) {
			// 人人网 参数传递（姓名和头像）
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)
					&& commType == CommHandler.TYPE_GET_STATUS_TIMELINE) {
				setUserImage(userHead,
						this.getIntent().getExtras().getString("headUrl"));
				userName.setText(this.getIntent().getExtras().getString("name"));
				headName.setText(this.getIntent().getExtras().getString("name"));
			} else {

				setUserImage(userHead, timeLineInfo.getUserInfo()
						.getUserImageURL());
			}
		} else {
			setUserImage(userHead, null);
		}
		// Favorite
		if (timeLineInfo.getFavorite() != null
				&& timeLineInfo.getFavorite().equals("true")) {
			favorite.setImageResource(R.drawable.multiselectdialog_starton);
		} else {
			favorite.setImageResource(R.drawable.multiselectdialog_startoff);
		}

		// Reply History
		if (timeLineInfo.getinReplyToStatusId() != null
				&& !timeLineInfo.getinReplyToStatusId().equals("null")) {
			history.setVisibility(View.VISIBLE);
		}
		// bottom
		// twitter QT、RT、RE
		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)) {

			linearBottomRetweet.setVisibility(View.GONE);
			linearBottomComment.setVisibility(View.GONE);
		} else {
			linearBottomQT.setVisibility(View.GONE);
			linearBottomRT.setVisibility(View.GONE);
			linearBottomReply.setVisibility(View.GONE);
		}
		// direct message
		if (currentAccount.getUserName().equals(
				timeLineInfo.getUserInfo().getUserName())
				|| currentAccount.getUserScreenName().equals(
						timeLineInfo.getUserInfo().getScreenName())) {
			linearBottomDM.setVisibility(View.GONE);
		} else {
			linearBottomDM.setVisibility(View.VISIBLE);
		}
		// delete
		if (currentAccount.getUserName().equals(
				timeLineInfo.getUserInfo().getUserName())
				|| currentAccount.getUserScreenName().equals(
						timeLineInfo.getUserInfo().getScreenName())) {
			linearBottomDelete.setVisibility(View.VISIBLE);
		} else {
			linearBottomDelete.setVisibility(View.GONE);
		}

		// renren
		if (statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_RENREN)) {
			favorite.setVisibility(View.GONE);
			linearBottomDM.setVisibility(View.GONE);
			linearBottomDelete.setVisibility(View.VISIBLE);
			linearBottomFavorite.setVisibility(View.GONE);
			linearBottomDM.setVisibility(View.GONE);
			linearBottomDelete.setVisibility(View.GONE);

			if ("10".equals(timeLineInfo.getFeedType())
					|| commType == CommHandler.TYPE_GET_MY_TIME_LINE) {
				linearBottomRetweet.setVisibility(View.VISIBLE);
			} else {
				linearBottomRetweet.setVisibility(View.GONE);
			}
			if ("20".equals(timeLineInfo.getFeedType())
					|| "21".equals(timeLineInfo.getFeedType())
					&& timeLineInfo.isRetweeted()) {
				linearBottomDetailBlog.setVisibility(View.VISIBLE);
			} else {
				linearBottomDetailBlog.setVisibility(View.GONE);
			}
		} else {
			linearBottomDetailBlog.setVisibility(View.GONE);
		}
		loadImageAndFileWebView();
	}

	private void loadImageAndFileWebView() {

		String statusImageForWebView = getStatusImageForWebView();
		String statusFileForWebView = getStatusFileForWebView();
		String retweetStatusImageForWebView = getRetweetStatusImageForWebView();
		String retweetStatusFileForWebView = getRetweetStatusFileForWebView();

		// -------------------image----------------------------
		if (statusImageForWebView != null
				&& statusImageForWebView.contains("img")
				&& !statusImageForWebView.contains("/files/")) {

			statusImageWebView.loadDataWithBaseURL("", statusImageForWebView,
					"text/html", "utf-8", "");
			linearStatusImage.setVisibility(View.VISIBLE);
			statusImageWebView.setOnTouchListener(this);

		} else {
			linearStatusImage.setVisibility(View.GONE);
		}

		if (retweetStatusImageForWebView != null
				&& retweetStatusImageForWebView.contains("img")
				&& !retweetStatusImageForWebView.contains("/files/")) {
			retweetStatusImageWebView.loadDataWithBaseURL("",
					retweetStatusImageForWebView, "text/html", "utf-8", "");
			retweetStatusImageWebView.setOnTouchListener(this);
		} else {
			retweetStatusImageWebView.setVisibility(View.GONE);
		}

		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			retweetStatusImageWebView.setVisibility(View.GONE);
		}

		// ------------------file----------------------------------------
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				&& statusFileForWebView != null
				&& statusFileForWebView.contains("/files/")) {
			linearStatusFile.setVisibility(View.VISIBLE);
			statusFileWebView.loadDataWithBaseURL("", statusFileForWebView,
					"text/html", "utf-8", "");
		}
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				&& retweetStatusFileForWebView != null
				&& retweetStatusFileForWebView.contains("/files/")) {
			linearRetweetStatusFile.setVisibility(View.VISIBLE);
			retweetStatusFileWebView.loadDataWithBaseURL("",
					retweetStatusFileForWebView, "text/html", "utf-8", "");
		}
	}

	// ----------------------status--------------------------------------

	private void setStatusState() {

		String imageUrlString = timeLineInfo.getImageInformationForWebView(
				this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
		initStatus = TagAnalysis.clearImageUrls(initStatus, imageUrlString);
		// Extract Hash
		initStatus = initStatus.replaceAll("\r", "");
		if (initStatus == null) {
			initStatus = "";
		}
		// 话题
		ArrayList<String> indexHashFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			indexHashFlag = TagAnalysis.getHashTagIndex(initStatus,
					statusData.getCurrentService());
		} else {
			indexHashFlag = TagAnalysis.getIndexForSina(initStatus, "#");
		}

		int number = indexHashFlag.size();

		final SpannableString spanString = new SpannableString(initStatus);

		for (int i = 0; i < number / 2; i++) {

			final int start = Integer.valueOf(indexHashFlag.get(i * 2));
			final int end = Integer.valueOf(indexHashFlag.get(i * 2 + 1));

			// Prepare Clickable Span
			MyClickableSpan myClickableSpan = new MyClickableSpan(
					new android.view.View.OnClickListener() {

						@Override
						public void onClick(View widget) {

							String tag = initStatus.substring(start, end);
							Intent i = new Intent(DetailTweetActivity.this,
									KeywordSearchActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("keyword", tag);
							i.putExtras(bundle);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);

						}

					});

			spanString.setSpan(myClickableSpan, start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		// @user
		ArrayList<String> indexAtFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			indexAtFlag = TagAnalysis.getIndex(initStatus, "@");
		} else {
			indexAtFlag = TagAnalysis.getIndexForSina(initStatus, "@");
		}

		int numverAtFlag = indexAtFlag.size();

		for (int i = 0; i < numverAtFlag / 2; i++) {

			final int start = Integer.valueOf(indexAtFlag.get(i * 2));
			final int end = Integer.valueOf(indexAtFlag.get(i * 2 + 1));

			// Prepare Clickable Span
			MyClickableSpan myClickableSpan = new MyClickableSpan(
					new android.view.View.OnClickListener() {
						@Override
						public void onClick(View widget) {

							String tag = initStatus.substring(start + 1, end);
							Intent i = null;
							Bundle bundle = new Bundle();
							;
							if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
									.getCurrentService())
									|| IGeneral.SERVICE_NAME_TWITTER_PROXY
											.equals(statusData
													.getCurrentService())) {
								i = new Intent(DetailTweetActivity.this,
										ProfileActivity.class);
							} else if (statusData.getCurrentService().equals(
									IGeneral.SERVICE_NAME_RENREN)) {
								i = new Intent(DetailTweetActivity.this,
										BasicUserSearchActivity.class);
								bundle.putString("flag", "name");
							} else {
								i = new Intent(DetailTweetActivity.this,
										UserSearchActivity.class);
							}
							bundle.putString("name", tag);
							i.putExtras(bundle);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
						}
					});
			spanString.setSpan(myClickableSpan, start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		// emotions replace
		AsyncDataLoad imageLoader = new AsyncDataLoad();
		String phrase = null;
		ArrayList<String> indexEmotionsFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
			indexEmotionsFlag = TagAnalysis.getEmotionsIndexFlag(initStatus,
					statusData.getCurrentService());

			int numberEmotionFlag = indexEmotionsFlag.size();
			if (numberEmotionFlag > 0) {
				for (int i = 0; i < numberEmotionFlag / 2; i++) {

					final int start = Integer.valueOf(indexEmotionsFlag
							.get(i * 2));
					final int end = Integer.valueOf(indexEmotionsFlag
							.get(i * 2 + 1));
					phrase = initStatus.substring(start, end);
					if (phrase != null) {
						for (final EmotionInfo emotion : SendMessageActivity.emotionList) {
							if (phrase.equals(emotion.getPhrase())) {
								String url = emotion.getUrl();
								if (statusData.getCurrentService().equals(
										IGeneral.SERVICE_NAME_SINA)
										|| statusData
												.getCurrentService()
												.equals(IGeneral.SERVICE_NAME_SOHU)
										|| statusData
												.getCurrentService()
												.equals(IGeneral.SERVICE_NAME_RENREN)) {
									// sina/sohu [爱心] renren(微笑)
									phrase = phrase.substring(1,
											phrase.length() - 1);

									try {
										url = url.replace(phrase, URLEncoder
												.encode(phrase, "UTF-8"));
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (statusData
										.getCurrentService()
										.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
									try {
										String encodeStr = url.substring(
												url.lastIndexOf("/") + 1,
												url.lastIndexOf("."));
										url = url.replace(encodeStr, URLEncoder
												.encode(encodeStr, "UTF-8"));
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (statusData.getCurrentService()
										.equals(IGeneral.SERVICE_NAME_TENCENT)) {
									url = emotion.getUrl();
								}
								imageLoader.loadDrawable(url,
										new ImageCallback() {
											public void imageLoaded(
													Drawable imageDrawable,
													String imageUrl) {

												imageDrawable.setBounds(0, 0,
														22, 22);
												ImageSpan span = new ImageSpan(
														imageDrawable,
														ImageSpan.ALIGN_BASELINE);
												spanString
														.setSpan(
																span,
																start,
																end,
																Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

												statusTextView
														.setText(spanString);
												statusTextView
														.setMovementMethod(LinkMovementMethod
																.getInstance());
											}
										});
							}

						}

					}
				}
			} else {
				statusTextView.setText(spanString);
				statusTextView.setMovementMethod(LinkMovementMethod
						.getInstance());
			}

		} else {
			statusTextView.setText(spanString);
			statusTextView.setMovementMethod(LinkMovementMethod.getInstance());
		}

	}

	// --------------------------retweet status
	// state-----------------------------
	private void setRetweetedStatusState() {

		String retweetedStatus;
		if (("").equals(timeLineInfo.getRetweetedStatus())
				|| timeLineInfo.getRetweetedStatus() == null) {
			retweetedStatus = "";
		} else {
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				retweetedStatus = timeLineInfo.getRetweetedStatus();
			} else {
				retweetedStatus = "@"
						+ timeLineInfo.getUserInfo().getRetweetedScreenName()
						+ ":\n" + timeLineInfo.getRetweetedStatus();
			}
			String imageUrlString = timeLineInfo.getImageInformationForWebView(
					this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_RETWEET);
			retweetedStatus = TagAnalysis.clearImageUrls(retweetedStatus,
					imageUrlString);
		}
		// Extract Hash
		retweetedStatus = retweetedStatus.replaceAll("\r", "");
		if (retweetedStatus == null) {
			retweetedStatus = "";
		}
		final String initRetweetStatus = retweetedStatus;
		// 话题
		ArrayList<String> indexHashFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			indexHashFlag = TagAnalysis.getHashTagIndex(initRetweetStatus,
					statusData.getCurrentService());
		} else {
			indexHashFlag = TagAnalysis.getIndexForSina(initRetweetStatus, "#");
		}
		int number = indexHashFlag.size();

		final SpannableString spanString = new SpannableString(
				initRetweetStatus);

		for (int i = 0; i < number / 2; i++) {

			final int start = Integer.valueOf(indexHashFlag.get(i * 2));
			final int end = Integer.valueOf(indexHashFlag.get(i * 2 + 1));

			// Prepare Clickable Span
			MyClickableSpan myClickableSpan = new MyClickableSpan(
					new android.view.View.OnClickListener() {

						@Override
						public void onClick(View widget) {

							String tag = initRetweetStatus
									.substring(start, end);
							Intent i = new Intent(DetailTweetActivity.this,
									KeywordSearchActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("keyword", tag);
							i.putExtras(bundle);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
						}
					});
			spanString.setSpan(myClickableSpan, start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		// @user
		ArrayList<String> indexAtFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_TWITTER_PROXY)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER)) {
			indexAtFlag = TagAnalysis.getIndex(initRetweetStatus, "@");
		} else {
			indexAtFlag = TagAnalysis.getIndexForSina(initRetweetStatus, "@");
		}
		int numverAtFlag = indexAtFlag.size();

		for (int i = 0; i < numverAtFlag / 2; i++) {

			final int start = Integer.valueOf(indexAtFlag.get(i * 2));
			final int end = Integer.valueOf(indexAtFlag.get(i * 2 + 1));

			// Prepare Clickable Span
			MyClickableSpan myClickableSpan = new MyClickableSpan(
					new android.view.View.OnClickListener() {
						@Override
						public void onClick(View widget) {

							String tag = initRetweetStatus.substring(start + 1,
									end);
							Intent i = null;
							if (IGeneral.SERVICE_NAME_TWITTER.equals(statusData
									.getCurrentService())
									|| IGeneral.SERVICE_NAME_TWITTER_PROXY
											.equals(statusData
													.getCurrentService())) {
								i = new Intent(DetailTweetActivity.this,
										ProfileActivity.class);
							} else {
								i = new Intent(DetailTweetActivity.this,
										UserSearchActivity.class);
							}
							Bundle bundle = new Bundle();
							bundle.putString("name", tag);
							i.putExtras(bundle);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
						}
					});
			spanString.setSpan(myClickableSpan, start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		// emotions replace
		AsyncDataLoad imageLoader = new AsyncDataLoad();
		String phrase = null;
		ArrayList<String> indexEmotionsFlag = null;
		if (statusData.getCurrentService().equals(
				IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TENCENT)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {
			indexEmotionsFlag = TagAnalysis.getEmotionsIndexFlag(
					initRetweetStatus, statusData.getCurrentService());

			int numberEmotionFlag = indexEmotionsFlag.size();
			if (numberEmotionFlag > 0) {
				for (int i = 0; i < numberEmotionFlag / 2; i++) {

					final int start = Integer.valueOf(indexEmotionsFlag
							.get(i * 2));
					final int end = Integer.valueOf(indexEmotionsFlag
							.get(i * 2 + 1));
					phrase = initRetweetStatus.substring(start, end);
					if (phrase != null) {
						for (final EmotionInfo emotion : SendMessageActivity.emotionList) {
							if (phrase.equals(emotion.getPhrase())) {
								String url = emotion.getUrl();
								if (statusData.getCurrentService().equals(
										IGeneral.SERVICE_NAME_SINA)
										|| statusData
												.getCurrentService()
												.equals(IGeneral.SERVICE_NAME_SOHU)
										|| statusData
												.getCurrentService()
												.equals(IGeneral.SERVICE_NAME_RENREN)) {
									// sina/sohu [爱心] /renren (大笑)
									phrase = phrase.substring(1,
											phrase.length() - 1);

									try {
										url = url.replace(phrase, URLEncoder
												.encode(phrase, "UTF-8"));
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (statusData
										.getCurrentService()
										.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
									try {
										String encodeStr = url.substring(
												url.lastIndexOf("/") + 1,
												url.lastIndexOf("."));
										url = url.replace(encodeStr, URLEncoder
												.encode(encodeStr, "UTF-8"));
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (statusData.getCurrentService()
										.equals(IGeneral.SERVICE_NAME_TENCENT)) {
									url = emotion.getUrl();
								}
								try {
									imageLoader.loadDrawable(url,
											new ImageCallback() {
												public void imageLoaded(
														Drawable imageDrawable,
														String imageUrl) {

													imageDrawable.setBounds(0,
															0, 22, 22);
													ImageSpan span = new ImageSpan(
															imageDrawable,
															ImageSpan.ALIGN_BASELINE);
													spanString
															.setSpan(
																	span,
																	start,
																	end,
																	Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

													retweetStatusTextView
															.setText(spanString);
													retweetStatusTextView
															.setMovementMethod(LinkMovementMethod
																	.getInstance());
												}
											});
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						}

					}
				}
			} else {
				retweetStatusTextView.setText(spanString);
				retweetStatusTextView.setMovementMethod(LinkMovementMethod
						.getInstance());
			}

		} else {
			retweetStatusTextView.setText(spanString);
			retweetStatusTextView.setMovementMethod(LinkMovementMethod
					.getInstance());
		}
	}

	public String getEmotionDataForWebView() {

		if (SendMessageActivity.emotionList != null
				&& SendMessageActivity.emotionList.size() >= 30
				&& SendMessageActivity.htmlDataListForEmotion == null) {
			SendMessageActivity.htmlDataListForEmotion = new ArrayList<String>();
			for (int i = 0; i < SendMessageActivity.emotionList.size() / 30; i++) {
				StringBuffer result = new StringBuffer();
				result.append("<table border=\"0\" align=\"center\">");

				for (int x = i * 30; x < (i + 1) * 30; x = x + 6) {
					result.append("<tr>");
					for (int y = 0; y < 6; y++) {
						EmotionInfo emotionInfo = SendMessageActivity.emotionList
								.get(x + y);
						result.append("<td><img width=\"22px\" height=\"22px\" src=\""
								+ emotionInfo.getUrl()
								+ "\" onClick=\"getImage('"
								+ emotionInfo.getPhrase() + "');\"/></td>");
					}
					result.append("</tr>");
				}

				result.append("</table>");
				SendMessageActivity.htmlDataListForEmotion.add(result
						.toString());

			}
		}

		return SendMessageActivity.htmlDataListForEmotion.get(page);
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

		imageUrl = "<img style='max-height: 60px; max-width: 60px;' src='"
				+ imageUrl + "' />";

		imageWebView.loadDataWithBaseURL("about:blank", imageUrl, "text/html",
				"utf-8", "");

	}

	public String getStatusImageForWebView() {

		if (statusData.getCurrentService()
				.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			hrefImageUrlsData = timeLineInfo.getImageInformationForWebView(
					DetailTweetActivity.this, TimeLineInfo.TYPE_DATA_IMAGE);
			return timeLineInfo.getImageInformationForWebView(
					DetailTweetActivity.this, TimeLineInfo.TYPE_DATA_IMAGE);
		}

		String imageUrlString = timeLineInfo.getImageInformationForWebView(
				this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);

		StringBuffer htmlData = new StringBuffer();
		StringBuffer previewHtmlData = new StringBuffer();
		if (!initPreviewState.equals(BrowseModeActivity.select[0])) {
			hrefImageUrlsData = "";
			return "";
		}

		if ((statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS) || statusData
				.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT))
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {

			String[] imageUrls = imageUrlString.split(";");

			if (imageUrls.length > 0) {
				htmlData.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
				htmlData.append("<center>");
				previewHtmlData
						.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
				previewHtmlData.append("<center>");

				for (int i = 0; i < imageUrls.length; i++) {
					String srcImageUrl = imageUrls[i];
					String hrefImageUrl = imageUrls[i];
					// cfb 附件attach
					if (imageUrls[i].contains("/files/")) {
						srcImageUrl = "";
						hrefImageUrl = "";
					}
					// Sina 图片显示
					if (imageUrls[i].contains("sinaimg.cn/thumbnail")) {
						hrefImageUrl = imageUrls[i].replace("thumbnail",
								"large");
					}
					// Tencent
					else if (imageUrls[i].contains("qpic.cn/mblogpic")) {

						srcImageUrl = imageUrls[i] + "/160";
						hrefImageUrl = imageUrls[i] + "/460";

					}
					// Sohu
					else if (imageUrls[i].contains("t.itc.cn/mblog/pic")) {
						hrefImageUrl = imageUrls[i].replace(" ", "f_");

					}
					if (!srcImageUrl.equals("") && !hrefImageUrl.equals("")) {
						if (statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
							htmlData.append(
									"<img style='max-width:160px;max-height:300px'  src='")
									.append(srcImageUrl).append("' />");
						} else {
							htmlData.append(
									"<img style='max-width:'+(160*scale)+'px;max-height:'+(300*scale)+'px;'  src='")
									.append(srcImageUrl).append("' />");
						}

						previewHtmlData
								.append("<img style='max-width:'+(160*scale)+'px;max-height:'+(300*scale)+'px;'  src='")
								.append(hrefImageUrl).append("' />");
						if (i != imageUrls.length - 1) {
							htmlData.append("<br>");
							previewHtmlData.append("<br>");
						}
					}
				}
				htmlData.append("<center></body></html>");
				previewHtmlData.append("<center></body></html>");
			} else {
				htmlData.append("");
				previewHtmlData.append("");
			}
		}
		hrefImageUrlsData = previewHtmlData.toString();
		return htmlData.toString();
	}

	public String getStatusFileForWebView() {

		String imageUrlString = timeLineInfo.getImageInformationForWebView(
				DetailTweetActivity.this,
				TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_STATUS);
		StringBuffer htmlData = new StringBuffer();

		if ((statusData.getCurrentService().equals(IGeneral.SERVICE_NAME_SINA)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS) || statusData
				.getCurrentService().equals(IGeneral.SERVICE_NAME_TENCENT))
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_SOHU)
				|| statusData.getCurrentService().equals(
						IGeneral.SERVICE_NAME_RENREN)) {

			String[] imageUrls = imageUrlString.split(";");

			htmlData.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
			htmlData.append("<center>");
			if (imageUrls.length > 0) {
				htmlData.append("<center>");
				for (int i = 0; i < imageUrls.length; i++) {
					String srcImageUrl = "";
					String hrefImageUrl = "";
					// 附件attach
					if (imageUrls[i].contains("/files/")) {
						srcImageUrl = "http://www.iconpng.com/png/coquette/attachment3.png";
						hrefImageUrl = imageUrls[i];
					}
					if (!srcImageUrl.equals("") && !hrefImageUrl.equals("")) {
						htmlData.append(
								"<a href='"
										+ hrefImageUrl
										+ "'><img style='max-height: 400px; max-width:200px; margin-top:4px;' src='")
								.append(srcImageUrl).append("' /></a>");

						if (i != imageUrls.length - 1) {
							htmlData.append("<br>");
						}
					}
				}

			}
			htmlData.append("<center></body></html>");
		}

		return String.valueOf(htmlData);
	}

	public String getRetweetStatusImageForWebView() {

		StringBuffer previewRetweetedHtmlData = new StringBuffer();
		// create html source
		if (timeLineInfo.isRetweeted()) {

			StringBuffer htmlData = new StringBuffer();

			String imageUrlString = timeLineInfo.getImageInformationForWebView(
					this, TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_RETWEET);

			if (!initPreviewState.equals(BrowseModeActivity.select[0])) {
				hrefRetweetedImageUrlsData = "";
				return "";
			}

			String[] imageUrls = imageUrlString.split(";");
			if (imageUrls.length > 0) {
				htmlData.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
				htmlData.append("<center>");
				previewRetweetedHtmlData
						.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
				previewRetweetedHtmlData.append("<center>");
				for (int i = 0; i < imageUrls.length; i++) {
					String srcImageUrl = imageUrls[i];
					String hrefImageUrl = imageUrls[i];
					// cfb attach
					if (imageUrls[i].contains("/files/")) {
						srcImageUrl = "";
						hrefImageUrl = "";
					}
					// Sina large image
					if (imageUrls[i].contains("sinaimg.cn/thumbnail")) {
						hrefImageUrl = imageUrls[i].replace("thumbnail",
								"large");
					} else if (imageUrls[i].contains("qpic.cn/mblogpic")) {

						srcImageUrl = imageUrls[i] + "/160";
						hrefImageUrl = imageUrls[i] + "/460";
					}
					// Sohu
					else if (imageUrls[i].contains("t.itc.cn/mblog/pic")) {
						hrefImageUrl = imageUrls[i].replace(" ", "f_");
					}
					if (!srcImageUrl.equals("") && !hrefImageUrl.equals("")) {
						if (statusData.getCurrentService().equals(
								IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
							htmlData.append(
									"<img style='max-width:160px;max-height:300px'  src='")
									.append(srcImageUrl).append("' />");
						} else {
							htmlData.append(
									"<img style='max-width:'+(160*scale)+'px;max-height:'+(300*scale)+'px;'  src='")
									.append(srcImageUrl).append("' />");
						}
						previewRetweetedHtmlData
								.append("<img style='max-width:'+(160*scale)+'px;max-height:'+(300*scale)+'px;'  src='")
								.append(hrefImageUrl).append("' />");
						if (i != imageUrls.length - 1) {
							htmlData.append("<br>");
							previewRetweetedHtmlData.append("<br>");
						}
					}
				}
				htmlData.append("<center></body></html>");
				previewRetweetedHtmlData.append("<center></body></html>");
			} else {
				htmlData.append("");
				previewRetweetedHtmlData.append("");
			}
			hrefRetweetedImageUrlsData = previewRetweetedHtmlData.toString();
			return htmlData.toString();
		} else {
			hrefRetweetedImageUrlsData = "";
			return "";
		}
	}

	public String getRetweetStatusFileForWebView() {

		// create html source
		if (timeLineInfo.isRetweeted()) {
			StringBuffer htmlData = new StringBuffer();
			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
				String imageUrlString = timeLineInfo
						.getImageInformationForWebView(this,
								TimeLineInfo.TYPE_DATA_IMAGE_URL_FOR_RETWEET);
				String[] imageUrls = imageUrlString.split(";");
				if (imageUrls.length > 0) {
					htmlData.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
					htmlData.append("<center>");
					for (int i = 0; i < imageUrls.length; i++) {
						String srcImageUrl = "";
						String hrefImageUrl = "";
						// cfb attach
						if (imageUrls[i].contains("/files/")) {
							srcImageUrl = "http://www.iconpng.com/png/coquette/attachment3.png";
							hrefImageUrl = imageUrls[i];
						}
						if (!srcImageUrl.equals("") && !hrefImageUrl.equals("")) {

							htmlData.append(
									"<a href='"
											+ hrefImageUrl
											+ "'><img style='max-height: 400px; max-width:200px; margin-top:4px;' src='")
									.append(srcImageUrl).append("' /></a>");
							if (i != imageUrls.length - 1) {
								htmlData.append("<br>");
							}
						}
					}
					htmlData.append("<center></body></html>");
				} else {
					htmlData.append("");
				}
			}
			return htmlData.toString();
		} else {
			return null;
		}
	}

	private void comment() {
		Intent intent = new Intent(DetailTweetActivity.this,
				CommentActivity.class);
		Bundle bundle = new Bundle();
		String message_id = timeLineInfo.getMessageId();
		bundle.putString("message_id", message_id);
		bundle.putString("message", timeLineInfo.getStatus());
		bundle.putString("user_id", timeLineInfo.getUserInfo().getUid());
		bundle.putString("feed_type", timeLineInfo.getFeedType());

		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void qt() {

		AlertDialog dlg = new AlertDialog.Builder(this)
				.setTitle(R.string.confirm)
				.setMessage(
						String.format(getString(R.string.whether_retweet),
								timeLineInfo.getUserInfo().getScreenName()))
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								setProgressEnable(true);
								// HTTP Communication
								// Prepare Parameters
								Map<String, Object> parameters;
								parameters = new HashMap<String, Object>();
								parameters.put("message_id",
										timeLineInfo.getMessageId());

								try {
									apiServiceInterface.request(
											statusData.getCurrentService(),
											CommHandler.TYPE_RETWEET,
											apiServiceListener, parameters);
								} catch (RemoteException e) {
									e.printStackTrace();
								}

							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						}).create();
		dlg.show();
	}

	private void rt() {

		Intent intent = new Intent(DetailTweetActivity.this,
				SendMessageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("action", "new");
		bundle.putString("target", "RT @"
				+ timeLineInfo.getUserInfo().getScreenName() + " "
				+ timeLineInfo.getStatus());
		bundle.putString("message_id", timeLineInfo.getMessageId());
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void reply() {

		if (commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE
				|| commType == CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND) {
			Intent intent = new Intent(DetailTweetActivity.this,
					SendDMActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", timeLineInfo.getUserInfo().getScreenName());
			bundle.putString("uid", timeLineInfo.getUserInfo().getUid());
			intent.putExtras(bundle);
			startActivity(intent);
		} else {

			if (IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(statusData
					.getCurrentService())
					|| IGeneral.SERVICE_NAME_TWITTER.equals(statusData
							.getCurrentService())
					|| IGeneral.SERVICE_NAME_TWITTER_PROXY.equals(statusData
							.getCurrentService())) {

				String[] replyUserScreenNames = TagAnalysis
						.getReplyUserScreenName("@"
								+ timeLineInfo.getUserInfo().getScreenName()
								+ " " + timeLineInfo.getStatus());

				if (replyUserScreenNames.length > 2) {
					new SelectReplayUserDialog(this, replyUserScreenNames,
							timeLineInfo.getMessageId()).show();
				} else {
					if (replyUserScreenNames[0].equals("@" + screenName)) {
						Intent send = new Intent(DetailTweetActivity.this,
								SendMessageActivity.class);
						Bundle bundleSend = new Bundle();
						bundleSend.putString("action", "new");
						if (replyUserScreenNames.length == 1) {
							bundleSend.putString("target", "@"
									+ timeLineInfo.getUserInfo()
											.getScreenName());
						} else {
							bundleSend.putString("target",
									replyUserScreenNames[1]);
						}
						bundleSend.putString("message_id",
								timeLineInfo.getMessageId());
						send.putExtras(bundleSend);
						startActivity(send);
					} else {
						new SelectReplayUserDialog(this, replyUserScreenNames,
								timeLineInfo.getMessageId()).show();
					}
				}

			} else {
				Intent send1 = new Intent(DetailTweetActivity.this,
						SendMessageActivity.class);
				Bundle bundleSend1 = new Bundle();
				bundleSend1.putString("action", "new");
				bundleSend1.putString("target", "@"
						+ timeLineInfo.getUserInfo().getScreenName());
				bundleSend1
						.putString("message_id", timeLineInfo.getMessageId());
				send1.putExtras(bundleSend1);
				startActivity(send1);
			}
		}
	}

	private void favorite() {
		// Favorite
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("message_id", timeLineInfo.getMessageId());
		if (timeLineInfo.getFavorite() != null
				&& timeLineInfo.getFavorite().equals("true")) {
			timeLineInfo.setFavorite("false");
			favorite.setImageResource(R.drawable.multiselectdialog_startoff);
			parameters.put("type", "destroy");
		} else {
			timeLineInfo.setFavorite("true");
			favorite.setImageResource(R.drawable.multiselectdialog_starton);
			parameters.put("type", "create");
		}

		try {
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_SET_FAVORITE, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void email() {

		Intent mEmailIntent = new Intent(android.content.Intent.ACTION_SEND);
		/* set email's format plain/text */
		mEmailIntent.setType("text/plain");
		String s = timeLineInfo.getStatus();
		mEmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
		mEmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
		mEmailIntent.putExtra(android.content.Intent.EXTRA_TEXT, s);
		startActivity(Intent.createChooser(mEmailIntent, "Select Mailer"));
	}

	private void detailBlog() {
		Intent detail = new Intent(DetailTweetActivity.this,
				DetailBlogActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("timelineinfo", timeLineInfo);
		detail.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		detail.putExtras(bundle);
		startActivity(detail);
	}

	private void delete() {

		AlertDialog dlg = new AlertDialog.Builder(this)
				.setTitle(R.string.confirm)
				.setMessage(
						String.format(getString(R.string.whether_delete),
								timeLineInfo.getUserInfo().getScreenName()))
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								setProgressEnable(true);
								// HTTP Communication
								Map<String, Object> parameters;
								parameters = new HashMap<String, Object>();
								parameters.put("message_id",
										timeLineInfo.getMessageId());

								try {
									apiServiceInterface.request(
											statusData.getCurrentService(),
											CommHandler.TYPE_DESTROY,
											apiServiceListener, parameters);
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create();
		dlg.show();
	}

	public void openUrl(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(i);
	}

	private void showProgressDialog() {
		if (progress == null) {
			progress = new HandleProgressDialog(this);
		}
		progress.show();
	}

	private void closeProgressDialog() {
		if (progress != null) {
			progress.dismiss();
		}
	}

	private void setProgressEnable(boolean flag) {

		if (flag) {
			showProgressDialog();
		} else {
			closeProgressDialog();
		}
	}

	private void loadBackGroundImage(String path) {

		if (path == null) {
			getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		} else if (path.indexOf("/") == -1) {
			getWindow().setBackgroundDrawable(
					new ColorDrawable(Integer.parseInt(path)));
		} else {

			File file = new File(path);
			FileInputStream input = null;
			if (file.canRead() && file.isFile() && file.exists()) {
				try {
					input = new FileInputStream(file);

					// Get BitMap and set to background
					BitmapDrawable drawable = new BitmapDrawable(input);
					getWindow().setBackgroundDrawable(drawable);
				} catch (Exception e) {

				} catch (OutOfMemoryError e) {

				} finally {
					if (input != null) {
						try {
							input.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	@Override
	protected void refreshByMenu() {
	}

	private String toHexString(int color) {
		String hexString = "";
		if (String.valueOf(color).contains("-")) {
			hexString = Integer.toHexString(color);
		} else {
			hexString = Integer.toHexString(getResources().getColor(color));
		}
		if (hexString.length() > 2) {
			hexString = hexString.substring(2, hexString.length());
			return "#" + hexString;
		}
		return "#000000";
	}

	public void initEmotionView(ArrayList<EmotionInfo> emotionInfoList) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				DetailTweetActivity.this);
		LayoutInflater inflater = LayoutInflater.from(DetailTweetActivity.this);
		final View textEntryView = inflater.inflate(
				R.layout.dialog_show_gridview_emotion, null);
		builder.setView(textEntryView);

		GridView gridView = (GridView) textEntryView
				.findViewById(R.id.gridView_emotions);
		final ArrayList<HashMap<String, String>> emotionData = new ArrayList<HashMap<String, String>>();
		EmotionInfo emotionInfo;

		for (int i = 0; i < emotionInfoList.size(); i++) {
			emotionInfo = emotionInfoList.get(i);
			if (emotionInfo != null) {
				HashMap<String, String> map = new HashMap<String, String>();
				// “你的json地址”+json中取图片的相对地址得到绝对地址
				map.put("itemImage", emotionInfo.getUrl());
				map.put("itemText", emotionInfo.getPhrase());
				emotionData.add(map);
			}
		}
		previewDialog = builder.show();
		gridView.setAdapter(new GridViewAdapter(this, previewDialog,
				emotionData));
		// 设置emotionDialog 透明度
		WindowManager manager = previewDialog.getWindow().getWindowManager();
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		previewDialog.getWindow().setLayout(width * 9 / 10, height * 2 / 3);
	}

	// ====================================================

	// public void jumpToProifle(String screenName) {
	//
	// Log.i("", "wangliang [screenname] : " + screenName);
	// boolean isJumpToUserSearch = true;
	//
	// if (IGeneral.SERVICE_NAME_TENCENT
	// .equals(statusData.getCurrentService())) {
	// UserInfo userInfo = null;
	// UserInfo retweetUserInfo = null;
	// for (TimeLineInfo timelineInfo : timeLineDataList) {
	// userInfo = timelineInfo.getUserInfo();
	// retweetUserInfo = timelineInfo.getRetweetUserInfo();
	// if (userInfo != null
	// && screenName.equals(userInfo.getScreenName())) {
	// isJumpToUserSearch = false;
	// screenName = userInfo.getUserName();
	// break;
	// }
	// if (retweetUserInfo != null
	// && screenName.equals(retweetUserInfo.getScreenName())) {
	// isJumpToUserSearch = false;
	// screenName = retweetUserInfo.getUserName();
	// break;
	// }
	// }
	// }
	//
	// // Start Profile Activity
	// Intent intent = null;
	// if (isJumpToUserSearch) {
	// intent = new Intent(DetailTweetActivity.this,
	// UserSearchActivity.class);
	// } else {
	// intent = new Intent(DetailTweetActivity.this, ProfileActivity.class);
	// }
	// intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	// Bundle bundle = new Bundle();
	// bundle.putString("name", screenName);
	// intent.putExtras(bundle);
	// startActivity(intent);
	//
	// }

	// public void startUserSearchActivity(String tag) {
	//
	// tag = tag.substring(1, tag.length());
	// boolean isJumpToUserSearch = true;
	//
	// if (IGeneral.SERVICE_NAME_TENCENT
	// .equals(statusData.getCurrentService())) {
	// UserInfo userInfo = null;
	// UserInfo retweetUserInfo = null;
	// for (TimeLineInfo timelineInfo : timeLineDataList) {
	// userInfo = timelineInfo.getUserInfo();
	// retweetUserInfo = timelineInfo.getRetweetUserInfo();
	// if (userInfo != null && tag.equals(userInfo.getScreenName())) {
	// isJumpToUserSearch = false;
	// tag = userInfo.getUserName();
	// break;
	// }
	// if (retweetUserInfo != null
	// && tag.equals(retweetUserInfo.getScreenName())) {
	// isJumpToUserSearch = false;
	// tag = retweetUserInfo.getUserName();
	// break;
	// }
	// }
	// }
	// // Start ProfileActivity
	// Intent intent = null;
	// if (isJumpToUserSearch) {
	// intent = new Intent(DetailTweetActivity.this,
	// UserSearchActivity.class);
	// } else {
	// intent = new Intent(DetailTweetActivity.this, ProfileActivity.class);
	// }
	// intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	// Bundle bundle = new Bundle();
	// bundle.putString("name", tag);
	// bundle.putString("user_name", tag);
	// intent.putExtras(bundle);
	// startActivity(intent);
	//
	// }

	// public void startKeywordSearchActivity(String tag) {
	//
	// Intent i = new Intent(DetailTweetActivity.this,
	// KeywordSearchActivity.class);
	// Bundle bundle = new Bundle();
	// bundle.putString("keyword", tag);
	// i.putExtras(bundle);
	// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// startActivity(i);
	// }

}