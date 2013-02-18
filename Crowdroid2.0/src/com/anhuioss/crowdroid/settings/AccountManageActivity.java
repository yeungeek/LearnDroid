package com.anhuioss.crowdroid.settings;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.settings.AddCrowdroidForBusinessAccountActivity;
import com.anhuioss.crowdroid.settings.AddSinaAccountActivity;
import com.anhuioss.crowdroid.settings.AddSohuAccountActivity;
import com.anhuioss.crowdroid.settings.AddTencentAccountActivity;
import com.anhuioss.crowdroid.settings.AddTwitterAccountActivity;
import com.anhuioss.crowdroid.settings.AddTwitterProxyAccountActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AccountManageActivity extends Activity implements OnClickListener {

	private Button btn_back;

	private RadioGroup selectItem;

	private RadioButton cfb;

	private RadioButton twitter;

	private RadioButton twitter_proxy;

	private RadioButton sina;

	private RadioButton tencent;

	private RadioButton sohu;

	private RadioButton netEase;

	private RadioButton renren;

	Bundle bundle = new Bundle();

	// public static String nowusername="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_manage);

		bundle = this.getIntent().getExtras();
		// nowusername=bundle.getString("name");

		btn_back = (Button) findViewById(R.id.back);
		selectItem = (RadioGroup) findViewById(R.id.selectitem);
		cfb = (RadioButton) findViewById(R.id.cfb);
		twitter = (RadioButton) findViewById(R.id.twitter);
		twitter_proxy = (RadioButton) findViewById(R.id.twitter_proxy);
		sina = (RadioButton) findViewById(R.id.sina);
		tencent = (RadioButton) findViewById(R.id.tencent);
		sohu = (RadioButton) findViewById(R.id.sohu);
		netEase = (RadioButton) findViewById(R.id.netease);
		renren = (RadioButton) findViewById(R.id.renren);

		btn_back.setOnClickListener(this);
		selectItem.setOnClickListener(this);
		cfb.setOnClickListener(this);
		twitter.setOnClickListener(this);
		twitter_proxy.setOnClickListener(this);
		sina.setOnClickListener(this);
		tencent.setOnClickListener(this);
		sohu.setOnClickListener(this);
		netEase.setOnClickListener(this);
		renren.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.back: {
			finish();
			break;
		}
		case R.id.cfb: {
			Intent intent = new Intent();
			intent.setClass(this, AddCrowdroidForBusinessAccountActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.twitter: {
			Intent intent = new Intent();
			intent.setClass(this, AddTwitterAccountActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.twitter_proxy: {
			Intent intent = new Intent();
			intent.setClass(this, AddTwitterProxyAccountActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.sina: {
			Intent intent = new Intent();
			intent.setClass(this, AddSinaAccountActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}

		case R.id.tencent: {
			Intent intent = new Intent();
			intent.setClass(this, AddTencentAccountActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.sohu: {
			Intent intent = new Intent();
			intent.setClass(this, AddSohuAccountActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.netease: {
			Intent intent = new Intent();
			intent.setClass(this, AddWangyiAccountActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		case R.id.renren: {
			Intent intent = new Intent();
			intent.setClass(this, AddRenRenAccountActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		default:
			break;
		}

	}

}
