package com.anhuioss.crowdroid.cfb;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;

public class CommentPhotoActivity extends Activity implements
		ServiceConnection, OnClickListener, OnCheckedChangeListener {

	private int mark;

	private int TYPE;

	private String album_id;

	private String url;

	private String user_id;

	private StatusData statusData;

	private CrowdroidApplication crowdroidApplication;

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	private ImageView showImage;

	private TextView t_d;

	private EditText comment;

	private CheckBox retweetBox;

	// Progress Dialog
	private HandleProgressDialog progress = null;

	private static boolean isRunning = true;

	private Button up;

	Map<String, Object> parameters;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {
			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null) {
				Toast.makeText(CommentPhotoActivity.this, getResources().getString(R.string.commentSuccessTip),
						Toast.LENGTH_SHORT).show();
				finish();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.upload_photo_album);

		showImage = (ImageView) findViewById(R.id.image);
		t_d = (TextView) findViewById(R.id.t_d);
		t_d.setText(getResources().getString(R.string.commentContent));
		comment = (EditText) findViewById(R.id.description);
		up = (Button) findViewById(R.id.up);
		
		retweetBox = (CheckBox) findViewById(R.id.retweetBox);
		retweetBox.setOnCheckedChangeListener(this);
		retweetBox.setText(getResources().getString(R.string.also_tweet_to_timeline));
		up.setOnClickListener(this);
		up.setText(getResources().getString(R.string.upload));
		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);

		headerHome.setBackgroundResource(R.drawable.main_app);
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);
		parameters = new HashMap<String, Object>();
	}

	@Override
	public void onStart() {
		isRunning = true;
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();

		statusData = crowdroidApplication.getStatusData();
		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		Bundle bundle = this.getIntent().getExtras();
		album_id = bundle.getString("album_id");
		url = bundle.getString("url");
		user_id = bundle.getString("user_id");
		mark = bundle.getInt("mark");

		switch (mark) {
		case 1: {
			TYPE = CommHandler.TYPE_CFB_COMMENT_PHOTO;
			headerName.setText(getResources().getString(R.string.commentPhoto));
			showImage.setImageBitmap(getHttpBitmap(url));
			break;
		}
		case 2: {
			TYPE = CommHandler.TYPE_CFB_COMMENT_DOCUMENT;
			showImage.setVisibility(View.GONE);
			headerName.setText(getResources().getString(R.string.commentDoc));
			break;
		}
		case 3: {
			TYPE = CommHandler.TYPE_CFB_COMMENT_VIDEO;
			showImage.setVisibility(View.GONE);
			headerName.setText(getResources().getString(R.string.commentVedio));
			break;
		}
		}

		// Bundle bundle = this.getIntent().getExtras();
		// album_id = bundle.getString("album_id");
		// url = bundle.getString("url");
		// user_id = bundle.getString("user_id");

		super.onStart();
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.up: {
			try {

				showProgressDialog();

				parameters.put("id", album_id);
				parameters.put("comment", comment.getText().toString());
				parameters.put("user_id", user_id);
				// parameters.put("content", comment.getText().toString());
				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						TYPE, apiServiceListener, parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {
			Intent intent = new Intent(CommentPhotoActivity.this,
					DiscoveryActivity.class);
			startActivity(intent);
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
		apiServiceInterface = null;

	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(CommentPhotoActivity.this);
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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (retweetBox.isChecked()) {
			parameters.put("flag", "true");
		} else {
			parameters.put("flag", "false");
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
