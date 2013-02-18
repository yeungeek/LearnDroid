package com.anhuioss.crowdroid.sns.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.CommentActivity;
import com.anhuioss.crowdroid.activity.ProfileActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
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
import com.anhuioss.crowdroid.util.AsyncDataLoad;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.GridViewAdapter;
import com.anhuioss.crowdroid.util.MyClickableSpan;
import com.anhuioss.crowdroid.util.TagAnalysis;
import com.anhuioss.crowdroid.util.AsyncDataLoad.ImageCallback;

public class DetailBlogActivity extends BasicActivity implements
		OnClickListener, ServiceConnection {

	private CrowdroidApplication crowdroidApplication;
	// content
	private WebView userHead;
	private TextView userName;
	private TextView title;
	private TextView timeStamp;
	private TextView viewCount;
	// webview
	private WebView statusWebView;
	// head
	private Button headBack;
	private TextView headName;
	private Button headHome;

	// bottom
	private LinearLayout linearBottomRetweet;
	private LinearLayout linearBottomQT;
	private LinearLayout linearBottomRT;
	private LinearLayout linearBottomReply;
	private LinearLayout linearBottomFavorite;
	private LinearLayout linearBottomDM;
	private LinearLayout linearBottomDelete;
	private LinearLayout linearBottomDetailBlog;

	private Button bottomComment;
	private Button bottomProfile;
	private Button bottomEmail;
	private TextView statusTextView;
	private Button bottomTranslate;

	// Data
	private StatusData statusData;
	private AccountData currentAccount;
	private SettingData settingData;

	// bundle
	private TimeLineInfo timeLineData;
	private TimeLineInfo timeLineInfo;

	// Progress Dialog
	private HandleProgressDialog progress;

	private AlertDialog previewDialog;

	/** Text */
	private String screenName;

	private String id;

	private String uid;

	private int page = 0;

	/** API Service Interface */
	private ApiServiceInterface apiServiceInterface;

	/** QT and Comment Dialog for Sina **/
	// private EditText commentText;

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
				if (type == CommHandler.TYPE_GET_BLOG_CONTENT) {

					// Parser
					ArrayList<TimeLineInfo> timelineInfoData = new ArrayList<TimeLineInfo>();
					ParseHandler parseHandler = new ParseHandler();
					timelineInfoData = (ArrayList<TimeLineInfo>) parseHandler
							.parser(service, type, statusCode, message);
					if (timelineInfoData != null) {
						timeLineData = timelineInfoData.get(0);
						initViewData(timeLineData);
					}
				}
				if (type == CommHandler.TYPE_DESTROY) {
					finish();
				}

				// }

			} else {
				Toast.makeText(
						DetailBlogActivity.this,
						ErrorMessage.getErrorMessage(DetailBlogActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
			}

		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		setLayoutResId(R.layout.activity_detail_blog);

		// head-----------------------------
		headBack = (Button) findViewById(R.id.head_back);
		headHome = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);

		headBack.setOnClickListener(this);
		headHome.setOnClickListener(this);

		// bottom-----------------------------------

		linearBottomRetweet = (LinearLayout) findViewById(R.id.linear_bottom_retweet);
		linearBottomQT = (LinearLayout) findViewById(R.id.linear_bottom_qt);
		linearBottomRT = (LinearLayout) findViewById(R.id.linear_bottom_rt);
		linearBottomReply = (LinearLayout) findViewById(R.id.linear_bottom_reply);
		linearBottomFavorite = (LinearLayout) findViewById(R.id.linear_bottom_favorite);
		linearBottomDM = (LinearLayout) findViewById(R.id.linear_bottom_dm);
		linearBottomDelete = (LinearLayout) findViewById(R.id.linear_bottom_delete);
		linearBottomDetailBlog = (LinearLayout) findViewById(R.id.linear_bottom_detail_blog);

		bottomComment = (Button) findViewById(R.id.bottom_comment);
		bottomProfile = (Button) findViewById(R.id.bottom_profile);
		bottomEmail = (Button) findViewById(R.id.bottom_email);
		bottomTranslate = (Button) findViewById(R.id.bottom_translate);

		bottomComment.setOnClickListener(this);
		bottomProfile.setOnClickListener(this);
		bottomEmail.setOnClickListener(this);
		bottomTranslate.setOnClickListener(this);

		// content--------------------------------
		userHead = (WebView) findViewById(R.id.user_image);
		userName = (TextView) findViewById(R.id.screen_name);
		timeStamp = (TextView) findViewById(R.id.create_time);
		statusTextView = (TextView) findViewById(R.id.translateStatus);
		statusWebView = (WebView) findViewById(R.id.blog_content);
		title = (TextView) findViewById(R.id.blog_title);
		viewCount = (TextView) findViewById(R.id.view_count);

		userHead.setClickable(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		settingData = crowdroidApplication.getSettingData();

		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();
		screenName = crowdroidApplication.getAccountList().getCurrentAccount()
				.getUserScreenName();
		fontSize = settingData.getFontSize();
		fontColor = settingData.getFontColor();

		// Bind Service
		Intent intent = new Intent(DetailBlogActivity.this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		initDetailBlogView();

	}

	@Override
	public void onStop() {
		super.onStop();

		closeProgressDialog();

		// Unbind Service
		unbindService(this);

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.head_refresh: {

			Intent home = new Intent(DetailBlogActivity.this,
					HomeTimelineActivity.class);
			startActivity(home);
			finish();
			break;
		}
		case R.id.bottom_comment: {
			comment();
			break;
		}
		case R.id.bottom_profile: {

			Intent intent = new Intent(DetailBlogActivity.this,
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

			new TranslateDialog(DetailBlogActivity.this, statusTextView).show();
			Toast.makeText(DetailBlogActivity.this,
					R.string.setting_translation, Toast.LENGTH_SHORT).show();
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

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();

		// bundle status
		Bundle bundle = this.getIntent().getExtras();
		timeLineInfo = (TimeLineInfo) bundle.getSerializable("timelineinfo");
		if ("20".equals(timeLineInfo.getFeedType())) {
			uid = timeLineInfo.getUserInfo().getUid();
			id = timeLineInfo.getMessageId();
		} else if ("21".equals(timeLineInfo.getFeedType())
				&& timeLineInfo.isRetweeted()) {
			uid = timeLineInfo.getUserInfo().getRetweetUserId();
			id = timeLineInfo.getMediaId();
		}
		parameters.put("uid", uid);
		parameters.put("id", id);

		try {
			// Request
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_BLOG_CONTENT, apiServiceListener,
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

	private void initDetailBlogView() {

		statusTextView.setTextSize(Float.valueOf(fontSize) * 1.1f);

		if (fontColor.contains("-")) {
			statusTextView.setTextColor(Integer.valueOf(fontColor));
		} else {
			statusTextView.setTextColor(getResources().getColor(
					Integer.valueOf(fontColor)));
		}

		linearBottomRetweet.setVisibility(View.GONE);
		linearBottomQT.setVisibility(View.GONE);
		linearBottomRT.setVisibility(View.GONE);
		linearBottomReply.setVisibility(View.GONE);
		linearBottomFavorite.setVisibility(View.GONE);
		linearBottomDM.setVisibility(View.GONE);
		linearBottomDelete.setVisibility(View.GONE);
		linearBottomDetailBlog.setVisibility(View.GONE);

		// head
		headName.setText(R.string.blog);
		headHome.setBackgroundResource(R.drawable.main_home);

	}

	private void initViewData(TimeLineInfo timeLineData) {
		statusTextView.setText(timeLineData.getStatus());
		userName.setText(timeLineData.getUserInfo().getScreenName());
		title.setText(timeLineData.getStatusId());
		timeStamp.setText(timeLineData.getTime());
		viewCount.setText(getString(R.string.views) + "("
				+ timeLineData.getRetweetCount() + ")");
		setUserImage(userHead, timeLineData.getUserInfo().getUserImageURL());
		statusWebView.loadDataWithBaseURL("about:blank",
				timeLineData.getStatus(), "text/html", "utf-8", "");

	}

	private void comment() {
		Intent intent = new Intent(DetailBlogActivity.this,
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

	@Override
	protected void refreshByMenu() {
	}

	public void initEmotionView(ArrayList<EmotionInfo> emotionInfoList) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				DetailBlogActivity.this);
		LayoutInflater inflater = LayoutInflater.from(DetailBlogActivity.this);
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

}
