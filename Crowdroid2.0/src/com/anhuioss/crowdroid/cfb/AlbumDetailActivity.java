package com.anhuioss.crowdroid.cfb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.activity.ProfileActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;

public class AlbumDetailActivity extends Activity implements OnClickListener,
		ServiceConnection {

	private TimeLineInfo info = null;

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	int mark;
	private TextView name = null;
	private TextView time = null;
	private TextView user = null;
	private TextView desc = null;
	private TextView count = null;
	private TextView reeweet_count = null;
	private TextView count_tip = null;
	private TextView line4 = null;

	private TextView createTime = null;
	private TextView creator = null;

	private TextView rt = null;

	private RelativeLayout r_retweet = null;
	private RelativeLayout r_album_count = null;
	private RelativeLayout r_create_user = null;

	private TextView description = null;
	private StatusData statusData;

	private CrowdroidApplication crowdroidApplication;

	private AccountData currentAccount;

	private ArrayList<TimeLineInfo> timelineInfoList;

	private String album_id;

	private String id;

	private String detailDescroption;
	private String detailCreateTime;
	private String detailCreator;
	private String detailCommentCount;
	private String detailRetweetCount;

	// Progress Dialog
	private HandleProgressDialog progress = null;

	private static boolean isRunning = true;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")
					&& message != null) {
				closeProgressDialog();
				timelineInfoList = new ArrayList<TimeLineInfo>();
				ParseHandler parseHandler = new ParseHandler();
				timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
						.parser(service, type, statusCode, message);

				if (timelineInfoList != null && timelineInfoList.size() > 0) {

					detailCommentCount = timelineInfoList.get(0)
							.getCommentCount();
					detailDescroption = timelineInfoList.get(0).getStatus();
					detailCreateTime = timelineInfoList.get(0).getTime();
					detailCreator = timelineInfoList.get(0).getFavorite();
					detailRetweetCount = timelineInfoList.get(0)
							.getRetweetCount();
					time.setText(detailCreateTime);
					desc.setText(detailDescroption);
					user.setText(detailCreator);
					reeweet_count.setText(detailRetweetCount);
					count.setText(detailCommentCount);

				}

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_album);

		name = (TextView) findViewById(R.id.cfb_album_name);
		time = (TextView) findViewById(R.id.cfb_album_created_time);
		user = (TextView) findViewById(R.id.cfb_album_created_user);
		desc = (TextView) findViewById(R.id.cfb_album_description);
		count = (TextView) findViewById(R.id.cfb_album_count);
		r_retweet = (RelativeLayout) findViewById(R.id.r_retweet_count);
		r_album_count = (RelativeLayout) findViewById(R.id.r_album_count);
		reeweet_count = (TextView) findViewById(R.id.cfb_retweet_count);
		createTime = (TextView) findViewById(R.id.createTime);
		creator = (TextView) findViewById(R.id.creator);
		rt = (TextView) findViewById(R.id.rt);
		rt.setText(getResources().getString(R.string.retweet_count));
		creator.setText(getResources().getString(R.string.creator));
		createTime.setText(getResources().getString(R.string.createTime));
		line4 = (TextView) findViewById(R.id.line4);
		r_create_user = (RelativeLayout) findViewById(R.id.r_create_user);
		r_create_user.setOnClickListener(this);

		count_tip = (TextView) findViewById(R.id.cfb_album_count_tip);
		description = (TextView) findViewById(R.id.t_description);
		description.setText(getResources().getString(R.string.albumDes));
		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerHome.setBackgroundResource(R.drawable.main_app);
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);
		user.setOnClickListener(this);

	}

	@Override
	public void onStop() {
		super.onStop();

		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		isRunning = false;
		if (progress != null) {
			progress.dismiss();
		}
		TimelineActivity.isBackgroundNotificationFlag = true;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TimelineActivity.isBackgroundNotificationFlag = false;
	}

	@Override
	protected void onStart() {

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();

		statusData = crowdroidApplication.getStatusData();
		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();
		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
		headerName.setText(getResources().getString(R.string.albumMsg));
		Bundle bundle = getIntent().getExtras();
		mark = bundle.getInt("mark");
		switch (mark) {
		case 1: {
			count_tip.setText(getResources().getString(R.string.photoCount));
			break;
		}
		case 2: {
			count_tip.setText(getResources().getString(R.string.docCount));
			break;
		}
		case 3: {
			count_tip.setText(getResources().getString(R.string.vedioCount));
			break;
		}

		default:
			break;
		}
		if (bundle.containsKey("album")) {// 专辑信息
			info = (TimeLineInfo) bundle.getSerializable("album");
			if (!info.getUserInfo().getScreenName().equals("")) {
				user.setText(info.getUserInfo().getScreenName());
			}
			if (!info.getTime().equals("")) {
				time.setText(info.getTime());
			}
			if (!info.getStatus().equals("")) {
				desc.setText(info.getStatus());
			}
			if (!info.getCommentCount().equals("")) {
				count.setText(info.getCommentCount());
			}
			if (!info.getFavorite().equals("")) {
				name.setText(info.getFavorite());
			}
		}

		if (bundle.containsKey("photo")) {
			album_id = bundle.getString("album_id");
			id = bundle.getString("id");
			r_album_count.setOnClickListener(this);
			headerName.setText(getResources().getString(R.string.photoMsg));
			name.setVisibility(View.GONE);
			r_retweet.setVisibility(View.VISIBLE);
			line4.setVisibility(View.VISIBLE);
			// if (!info.getRetweetCount().equals("")) {
			// reeweet_count.setText(info.getRetweetCount());
			// }
			count_tip.setText(getResources().getString(R.string.comment_count));
			description.setText(getResources().getString(R.string.photoDes));

		}
		if (bundle.containsKey("video")) {
			album_id = bundle.getString("album_id");
			id = bundle.getString("id");
			r_album_count.setOnClickListener(this);
			headerName.setText(getResources().getString(R.string.vedioMsg));
			name.setVisibility(View.GONE);
			r_retweet.setVisibility(View.VISIBLE);
			line4.setVisibility(View.VISIBLE);
			// if (!info.getRetweetCount().equals("")) {
			// reeweet_count.setText(info.getRetweetCount());
			// }
			count_tip.setText(getResources().getString(R.string.comment_count));
			description.setText(getResources().getString(R.string.vedioDes));

		}
		if (bundle.containsKey("doc")) {
			album_id = bundle.getString("album_id");
			id = bundle.getString("id");
			r_album_count.setOnClickListener(this);
			headerName.setText(getResources().getString(R.string.docMsg));
			name.setVisibility(View.GONE);
			r_retweet.setVisibility(View.VISIBLE);
			line4.setVisibility(View.VISIBLE);
			// if (!info.getRetweetCount().equals("")) {
			// reeweet_count.setText(info.getRetweetCount());
			// }
			count_tip.setText(getResources().getString(R.string.comment_count));
			description.setText(getResources().getString(R.string.docDes));

		}

		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.r_create_user: {
			// Intent intent = new Intent(AlbumDetailActivity.this,
			// ProfileActivity.class);
			// Bundle bundle = new Bundle();
			// bundle.putString("name", "");
			// bundle.putString("user_name", "");
			// bundle.putString("uid", info.getUserInfo().getUid());
			// intent.putExtras(bundle);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
			// overridePendingTransition(R.anim.in_from_bottom,
			// R.anim.out_to_top);
			break;
		}
		case R.id.r_album_count: {
			if (detailCommentCount.equals("0") || detailCommentCount == null) {
				Toast.makeText(AlbumDetailActivity.this,
						getResources().getString(R.string.commentTip),
						Toast.LENGTH_LONG).show();
			} else {
				Intent intent = new Intent(AlbumDetailActivity.this,
						CommentListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("pid", id);
				bundle.putInt("mark", mark);
				intent.putExtra("comment", bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_bottom,
						R.anim.out_to_top);
			}

			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {
			Intent intent = new Intent(AlbumDetailActivity.this,
					DiscoveryActivity.class);
			startActivity(intent);
			break;
		}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
		if (album_id != null && id != null) {
			try {

				showProgressDialog();

				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("id", id);
				parameters.put("album_id", album_id);

				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_CFB_DETAIL_MSG, apiServiceListener,
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

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(AlbumDetailActivity.this);
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
}
