package com.anhuioss.crowdroid.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class UpdateMoodStatusActivity extends Activity implements
		ServiceConnection, OnClickListener, OnTouchListener, TextWatcher {

	private String[] mood = { "狂喜", "偷乐", "无感", "难过", "咆哮" };

	private String flag = "";

	private StatusData statusData;

	private HandleProgressDialog progress;

	/** Service Handler */
	private ApiServiceInterface apiServiceInterface;

	private WebView mood1;

	private WebView mood2;

	private WebView mood3;

	private WebView mood4;

	private WebView mood5;

	private Button send;

	private TextView countText;

	private TextView showmood;

	private EditText update_text;

	private int MAX_TEXT_COUNT = 140;

	// head
	private Button headBack;
	private TextView headName;
	private Button headHome;

	private String image1 = "http://mat1.gtimg.com/www/mb/images/tFace/01_50.gif";

	private String image2 = "http://mat1.gtimg.com/www/mb/images/tFace/02_50.gif";

	private String image3 = "http://mat1.gtimg.com/www/mb/images/tFace/03_50.gif";

	private String image4 = "http://mat1.gtimg.com/www/mb/images/tFace/04_50.gif";

	private String image5 = "http://mat1.gtimg.com/www/mb/images/tFace/05_50.gif";

	/** Listener Handler */
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			closeProgressDialog();

			if (statusCode != null && statusCode.equals("200")) {
				finish();
			}
			if (!"200".equals(statusCode)) {
				if ("1".equals(statusCode)) {

					Toast.makeText(UpdateMoodStatusActivity.this,
							"别贪心哦，客户端一天只能发表一次心情哦！", Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(UpdateMoodStatusActivity.this, ErrorMessage
							.getErrorMessage(UpdateMoodStatusActivity.this,
									statusCode), Toast.LENGTH_SHORT);
				}

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatemoodstatus);

		mood1 = (WebView) findViewById(R.id.mood1);
		mood2 = (WebView) findViewById(R.id.mood2);
		mood3 = (WebView) findViewById(R.id.mood3);
		mood4 = (WebView) findViewById(R.id.mood4);
		mood5 = (WebView) findViewById(R.id.mood5);

		send = (Button) findViewById(R.id.send);
		countText = (TextView) findViewById(R.id.counterText);
		showmood = (TextView) findViewById(R.id.show_mood);
		update_text = (EditText) findViewById(R.id.update_text);

		// head-----------------------------
		headBack = (Button) findViewById(R.id.head_back);
		headHome = (Button) findViewById(R.id.head_refresh);
		headHome.setBackgroundResource(R.drawable.main_home);
		headName = (TextView) findViewById(R.id.head_Name);
		headName.setText(getString(R.string.discovery_tencent_send_mood));

		headBack.setOnClickListener(this);
		headHome.setOnClickListener(this);

		mood1.loadUrl(image1);
		mood2.loadUrl(image2);
		mood3.loadUrl(image3);
		mood4.loadUrl(image4);
		mood5.loadUrl(image5);

		update_text.addTextChangedListener(this);
		mood1.setOnTouchListener(this);
		mood2.setOnTouchListener(this);
		mood3.setOnTouchListener(this);
		mood4.setOnTouchListener(this);
		mood5.setOnTouchListener(this);
		send.setOnClickListener(this);

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		notifyTextHasChanged();
		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
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
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_refresh: {

			Intent home = new Intent(UpdateMoodStatusActivity.this,
					HomeTimelineActivity.class);
			startActivity(home);
			finish();
			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.send: {

			if (flag.equals("")) {
				Toast.makeText(
						UpdateMoodStatusActivity.this,
						getResources().getString(
								R.string.discovery_tencent_mood_type),
						Toast.LENGTH_SHORT).show();
			} else if (update_text.getText().toString().equals("")) {
				Toast.makeText(
						UpdateMoodStatusActivity.this,
						getResources().getString(
								R.string.discovery_tencent_mood_content),
						Toast.LENGTH_SHORT).show();
			} else {
				try {
					// Update
					showProgressDialog();

					// Prepare Parameters
					Map<String, Object> parameters;
					parameters = new HashMap<String, Object>();
					parameters.put("flag", flag);
					parameters.put("status", update_text.getText().toString());
					// Request (get service from status information)

					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_UPDATE_MOOD_STATUS,
							apiServiceListener, parameters);
					update_text.setText("");

				}

				catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			break;
		}

		default:
			break;
		}

	}

	private void notifyTextHasChanged() {
		int leftCount = 0;
		//
		int status_count = getTextCount(update_text.getText().toString());
		leftCount = MAX_TEXT_COUNT - status_count;
		countText.setText(String.valueOf(leftCount));

		if (leftCount < 0) {

			countText.setTextColor(Color.RED);

			// Disable Confirm Button
			send.setEnabled(false);

		} else {

			countText.setTextColor(Color.BLACK);

			// Enable Confirm Button
			send.setEnabled(true);

		}
	}

	private int getTextCount(String updateText) {
		int count = 0;
		ArrayList<String> urlDataList = new ArrayList<String>();
		// 提取Url所需的正则表达式
		String regexStr = "http://[^ ^,^!^;^`^~^\n^，^！^；]*";
		// 创建正则表达式模版
		Pattern pattern = Pattern.compile(regexStr);
		// 创建Url字段匹配器,待查询字符串Data为其参数
		Matcher m = pattern.matcher(updateText);
		while (m.find()) {
			if (!urlDataList.contains(m.group())) {
				urlDataList.add(m.group());
			}
		}
		// 获取需要创建的String数组大小
		String strBuf = "";
		for (String urlData : urlDataList) {
			strBuf = strBuf + urlData;
		}

		count = updateText.length() - strBuf.length() + 11 * urlDataList.size();

		return count;
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {
		case R.id.mood1: {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				showmood.setText("您选择了" + mood[0] + "心情");
				flag = "1";
			}

			break;
		}
		case R.id.mood2: {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				showmood.setText("您选择了" + mood[1] + "心情");
				flag = "2";
			}
			break;
		}
		case R.id.mood3: {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				showmood.setText("您选择了" + mood[2] + "心情");
				flag = "3";
			}
			break;
		}
		case R.id.mood4: {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				showmood.setText("您选择了" + mood[3] + "心情");
				flag = "4";
			}
			break;
		}
		case R.id.mood5: {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				showmood.setText("您选择了" + mood[4] + "心情");
				flag = "5";
			}
			break;
		}
		default:
			break;
		}

		return false;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		notifyTextHasChanged();

	}

}
