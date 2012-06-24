package com.anhuioss.crowdroid.settings;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.settings.AddListActivity;
import com.anhuioss.crowdroid.settings.CasualWatchSettingActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class WeiBoSettingActivity extends Activity implements OnClickListener {

	private Button btn_back;

	private RadioGroup selectItem;

	private RadioButton list;

	private RadioButton casual_watch;
	
	private RadioButton groupList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weibo_setting);

		btn_back = (Button) findViewById(R.id.back);
		selectItem = (RadioGroup) findViewById(R.id.selectitem);
		list = (RadioButton) findViewById(R.id.list);
		casual_watch = (RadioButton) findViewById(R.id.casualwatch);
		groupList = (RadioButton) findViewById(R.id.groupList);
		
		btn_back.setOnClickListener(this);
		selectItem.setOnClickListener(this);
		list.setOnClickListener(this);
		casual_watch.setOnClickListener(this);
		groupList.setOnClickListener(this);
		
//		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
//		SettingData settingData = crowdroidApplication.getSettingData();
//		StatusData statusdata = crowdroidApplication.getStatusData();
//		if(statusdata.getCurrentService().equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)){
//			casual_watch.setVisibility(View.GONE);
//		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.back: {
			finish();
			break;
		}
		case R.id.list: {
			Intent intent = new Intent();
			intent.setClass(this, AddListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
			break;
		}
		case R.id.casualwatch: {
			Intent intent = new Intent(WeiBoSettingActivity.this,
					CasualWatchSettingActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.groupList: {
			Intent intent = new Intent(WeiBoSettingActivity.this,
					AddGroupListActivity.class);
			startActivity(intent);
			break;
		}
		default:
			break;
		}

	}

}
