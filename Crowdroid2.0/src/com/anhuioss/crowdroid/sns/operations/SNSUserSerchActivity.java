package com.anhuioss.crowdroid.sns.operations;

import java.util.HashMap;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.BasicUserSearchActivity;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SNSUserSerchActivity extends BasicActivity implements
		OnClickListener {

	private ArrayAdapter<String> renren_adapter_history;

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	private TextView serch_classmate;

	private TextView serch_colleague;

	private TextView serch_friends_by_detail;

	private TextView serch_friends_by_hometown;

	private AutoCompleteTextView name;

	private ImageButton serch_button;

	private LinearLayout mLinearSearchClassmate;

	private LinearLayout mLinearSearchColleague;

	private LinearLayout mLinearSearchByDetail;

	private LinearLayout mLinearSearchByHomeTown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.renren_user_serch);
		setLayoutResId(R.layout.renren_user_serch);
		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerName.setText(R.string.user_search);
		headerHome.setBackgroundResource(R.drawable.main_home);

		serch_classmate = (TextView) findViewById(R.id.serch_classmate);
		serch_colleague = (TextView) findViewById(R.id.serch_colleague);
		serch_friends_by_detail = (TextView) findViewById(R.id.serch_friends_by_detail);
		serch_friends_by_hometown = (TextView) findViewById(R.id.serch_friends_by_hometown);

		mLinearSearchClassmate = (LinearLayout) findViewById(R.id.linear_search_classmate);
		mLinearSearchColleague = (LinearLayout) findViewById(R.id.linear_search_colleague);
		mLinearSearchByDetail = (LinearLayout) findViewById(R.id.linear_search_friends_by_detail);
		mLinearSearchByHomeTown = (LinearLayout) findViewById(R.id.linear_search_friends_by_hometown);

		name = (AutoCompleteTextView) findViewById(R.id.name);
		serch_button = (ImageButton) findViewById(R.id.search_button);

		serch_button.setOnClickListener(this);
		serch_classmate.setOnClickListener(this);
		serch_colleague.setOnClickListener(this);
		serch_friends_by_detail.setOnClickListener(this);
		serch_friends_by_hometown.setOnClickListener(this);
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);

		mLinearSearchClassmate.setOnClickListener(this);
		mLinearSearchColleague.setOnClickListener(this);
		mLinearSearchByDetail.setOnClickListener(this);
		mLinearSearchByHomeTown.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_button: {

			Intent comment = new Intent(SNSUserSerchActivity.this,
					BasicUserSearchActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", name.getText().toString());
			bundle.putString("flag", "name");
			comment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			comment.putExtras(bundle);
			startActivity(comment);
			String text = name.getText().toString();
			SharedPreferences sp = getSharedPreferences(
					"renren_user_history_strs", 0);
			String save_Str = sp.getString("renren_user_history", "");
			String[] hisArrays = save_Str.split(",");
			for (int i = 0; i < hisArrays.length; i++) {
				if (hisArrays[i].equals(text)) {
					return;
				}
			}
			StringBuilder sb = new StringBuilder(save_Str);
			sb.append(text + ",");
			sp.edit().putString("renren_user_history", sb.toString()).commit();
			break;
		}
		// case R.id.serch_classmate:
		case R.id.linear_search_classmate: {
			Intent intent = new Intent();
			intent.setClass(SNSUserSerchActivity.this,
					RenRenSerchClassmateActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}

		// case R.id.serch_colleague:
		case R.id.linear_search_colleague: {
			Intent intent = new Intent();
			intent.setClass(SNSUserSerchActivity.this,
					RenRenSerchColleagueActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
		// case R.id.serch_friends_by_detail:
		case R.id.linear_search_friends_by_detail: {
			Intent intent = new Intent();
			intent.setClass(SNSUserSerchActivity.this,
					RenRenSerchUserDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
		// case R.id.serch_friends_by_hometown:
		case R.id.linear_search_friends_by_hometown: {
			Intent intent = new Intent();
			intent.setClass(SNSUserSerchActivity.this,
					RenRenSerchByBaseActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}

		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {

			Intent comment = new Intent(SNSUserSerchActivity.this,
					HomeTimelineActivity.class);
			comment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(comment);
			break;
		}

		default:
			break;
		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		SharedPreferences sp = getSharedPreferences("renren_user_history_strs",
				0);
		String save_history = sp.getString("renren_user_history", "");
		String[] hisArrays = save_history.split(",");
		renren_adapter_history = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, hisArrays);
		if (hisArrays.length > 50) {
			String[] newArrays = new String[50];
			System.arraycopy(hisArrays, 0, newArrays, 0, 50);
			renren_adapter_history = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, newArrays);
		}
		name.setAdapter(renren_adapter_history);
	}

	@Override
	protected void refreshByMenu() {
		// TODO Auto-generated method stub

	}
}
