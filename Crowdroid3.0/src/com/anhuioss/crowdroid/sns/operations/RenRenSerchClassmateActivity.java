package com.anhuioss.crowdroid.sns.operations;

import java.util.Calendar;
import java.util.HashMap;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.BasicUserSearchActivity;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class RenRenSerchClassmateActivity extends BasicActivity implements
		OnClickListener {

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	private EditText serch_school_name;

	private EditText name;

	private Spinner schoolType;

	private Spinner schoolYear;
	
	private Button serch = null;

	private HashMap<String, Object> map = new HashMap<String, Object>();

	String[] typeItem = null;

	String[] school_Years = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.renren_serch_classmate);
		setLayoutResId(R.layout.renren_serch_classmate);
		serch_school_name = (EditText) findViewById(R.id.serch_school_name);

		name = (EditText) findViewById(R.id.name);

		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerHome.setBackgroundResource(R.drawable.header_clean);
		headerName.setText(R.string.serch_classmate);
		typeItem = getResources().getStringArray(R.array.renren_serch_type);
		schoolType = (Spinner) findViewById(R.id.schoolType);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				RenRenSerchClassmateActivity.this,
				android.R.layout.simple_spinner_item, typeItem);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		schoolYear = (Spinner) findViewById(R.id.schoolYear);

		school_Years = get_yearsArray();
		ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(
				RenRenSerchClassmateActivity.this,
				android.R.layout.simple_spinner_item, school_Years);
		schoolYear.setAdapter(adapter_year);
		adapter_year
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		schoolType.setAdapter(adapter);

		
		serch = (Button) findViewById(R.id.serch);
		serch.setOnClickListener(this);
		
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);

	}

	private String[] get_yearsArray() {

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int count = year - 1900;
		String[] years = new String[count];
		for (int i = 0; i < count; i++) {
			year--;

			years[i] = String.valueOf(year);

		}
		return years;
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

			serch_school_name.setText("");
	
			break;
		}
		case R.id.serch: {

			Intent comment = new Intent(RenRenSerchClassmateActivity.this,
					BasicUserSearchActivity.class);
			Bundle bundle = new Bundle();

			bundle.putString("serch_school_name", serch_school_name.getText()
					.toString());
			bundle.putString("name", name
					.getText().toString());
			bundle.putString("flag", "student");
			bundle.putInt("schoolType", schoolType.getSelectedItemPosition());
			bundle.putString("schoolYear",
					school_Years[schoolYear.getSelectedItemPosition()]);
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
