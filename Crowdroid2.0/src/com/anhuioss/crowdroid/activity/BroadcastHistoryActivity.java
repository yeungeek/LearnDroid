package com.anhuioss.crowdroid.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.RetweetByMeActivity;
import com.anhuioss.crowdroid.RetweetOfMeActivity;
import com.anhuioss.crowdroid.RetweetToMeActivity;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.data.info.BroadcastInfo;
import com.anhuioss.crowdroid.service.cfb.CfbParseHandler;
import com.anhuioss.crowdroid.util.FileService;

public class BroadcastHistoryActivity extends Activity implements
		OnClickListener, OnItemClickListener {

	private ListView mListView;

	private Button btnClose;

	private Button btClear;

	private String historyMessage = "";

	private BroadcastInfo broadcastInfo = new BroadcastInfo();

	private ArrayList<BroadcastInfo> broadcastInfoList = null;

	private ArrayList<String> arrayList = new ArrayList<String>();

	ArrayAdapter<String> adapter;

	private FileService fileService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_broadcast_history);
		fileService = new FileService(getApplicationContext());
		// Find Views
		mListView = (ListView) findViewById(R.id.list_view);
		mListView.setOnItemClickListener(this);
		btnClose = (Button) findViewById(R.id.broadcast_back);
		btClear = (Button) findViewById(R.id.broadcast_clear);
		btnClose.setOnClickListener(this);
		btClear.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		historyMessage = getIntent().getExtras().getString(
				"broadcast-history-message");

		initView(historyMessage);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TimelineActivity.isBackgroundNotificationFlag=true;
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		TimelineActivity.isBackgroundNotificationFlag=false;
	}

	private void initView(String message) {
		if (!message.equals("") && !message.equals("[]")) {
			broadcastInfoList = CfbParseHandler.parseBroadcastInfoList(message);

		}
		arrayList.clear();
		if (broadcastInfoList != null && broadcastInfoList.size() > 0) {
			for (int i = 0; i < broadcastInfoList.size(); i++) {
				arrayList.add(broadcastInfoList.get(i).getAdsTitle());
			}

			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, arrayList);
			mListView.setAdapter(adapter);
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		openManager(position);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.broadcast_back: {
			finish();
			break;
		}
		case R.id.broadcast_clear: {
			clear();
			break;
		}
		default:
			break;
		}
	}

	private void clear() {

		arrayList.clear();
		adapter.notifyDataSetChanged();

		historyMessage = "";
		try {
			fileService.save("notify.txt", historyMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openManager(final int position) {
		String[] items = getResources().getStringArray(
				R.array.broadcast_history_manager);
		AlertDialog dialog = new AlertDialog.Builder(
				BroadcastHistoryActivity.this).setItems(items,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int pos) {
						switch (pos) {
						case 0: {
							// look search
							Intent i = new Intent(
									BroadcastHistoryActivity.this,
									BroadcastAdvertiseActivity.class);
							Bundle b = new Bundle();
							b.putSerializable("broadcast-info",
									broadcastInfoList.get(position));
							b.putBoolean("broadcast-is-from-history", true);
							i.putExtras(b);
							startActivity(i);
							break;
						}
						case 1: {
							arrayList.remove(position);
							adapter.notifyDataSetChanged();
							deleteFile(position);
							break;
						}

						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}

	protected void deleteFile(int index) {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				if (fileIsExists()) {
					String regular = "\\{.*?\\}";// 提取各object
					int count = 0;
					String removeData = "";
					Pattern pattern = Pattern.compile(regular);
					Matcher matcher = pattern.matcher(historyMessage);
					while (matcher.find()) {
						Log.e("Reg", matcher.group());
						count++;
						if (index == count - 1) {
							removeData = matcher.group();
						}
					}
					Log.e("count", String.valueOf(count));

					if (count == 1) {// 只有一条数据
						historyMessage = "";
					} else if (count > 1) {// 多条数据
						historyMessage = historyMessage.replace(removeData, "");
						if (index == count - 1) {// 删除最后一条
							historyMessage = historyMessage.substring(0,
									historyMessage.length() - 2) + "]";
						} else if (index == 0) {// 删除第一条
							historyMessage = "[" + historyMessage.substring(2);
						} else if (index > 0 && index < count - 1) {
							historyMessage = historyMessage.replace(removeData
									+ ",", "");
						}
					}
					fileService.save("notify.txt", historyMessage);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean fileIsExists() {
		try {
			File f = new File("/mnt/sdcard/crowdroid/notify.txt");
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
