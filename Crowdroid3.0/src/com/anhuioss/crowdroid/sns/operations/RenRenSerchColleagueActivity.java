package com.anhuioss.crowdroid.sns.operations;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.BasicUserSearchActivity;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.R;

public class RenRenSerchColleagueActivity extends BasicActivity implements OnClickListener{
	
	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;
	
	private EditText company_name;
	
	private EditText name;
	
	private Button serch = null;
	
	
	
	
	private HashMap<String, Object> map=new HashMap<String, Object>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.renren_serch_colleague);
		setLayoutResId(R.layout.renren_serch_colleague);
		company_name=(EditText)findViewById(R.id.company_name);
		
		name=(EditText)findViewById(R.id.name);
		
		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerName.setText(R.string.serch_colleague);
		headerHome.setBackgroundResource(R.drawable.header_clean);
		serch = (Button) findViewById(R.id.serch);
		serch.setOnClickListener(this);
		
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {

		name.setText("");
		
		company_name.setText("");
			break;
		}
		case R.id.serch: {

			Intent comment = new Intent(RenRenSerchColleagueActivity.this,
					BasicUserSearchActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("name", name.getText().toString());
			bundle.putString("company_name", company_name.getText().toString());
			bundle.putString("flag", "ts");
			comment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			comment.putExtras(bundle);
			startActivity(comment);
			break;
		}

		default:
			break;
		}
		
	}

	@Override
	protected void refreshByMenu() {
		// TODO Auto-generated method stub
		
	}
	

}
