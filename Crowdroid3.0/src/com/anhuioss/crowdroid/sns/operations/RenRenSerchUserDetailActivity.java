package com.anhuioss.crowdroid.sns.operations;

import java.util.Calendar;
import java.util.HashMap;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.BasicUserSearchActivity;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.R;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class RenRenSerchUserDetailActivity extends BasicActivity implements
		OnClickListener {

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	private EditText name;

	private Spinner birth_Year;

	private Spinner birth_Month;

	private Spinner birth_Day;

	private Button serch = null;

	private ArrayAdapter<String> year_adapter;

	private ArrayAdapter<String> month_adapter;

	private ArrayAdapter<String> day_adapter;

	private String[] yearArray;

	private String[] monthArray;

	private String[] dayArray;

	String day = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.renren_serch_friends_detail);
		setLayoutResId(R.layout.renren_serch_friends_detail);
		headerBack = (Button) findViewById(R.id.head_back);

		headerName = (TextView) findViewById(R.id.head_Name);

		headerHome = (Button) findViewById(R.id.head_refresh);
		headerName.setText(R.string.serch_by_birth);
		headerHome.setBackgroundResource(R.drawable.header_clean);

		name = (EditText) findViewById(R.id.name);

		birth_Year = (Spinner) findViewById(R.id.birth_year);

		birth_Month = (Spinner) findViewById(R.id.birth_month);

		birth_Day = (Spinner) findViewById(R.id.birth_day);

		serch = (Button) findViewById(R.id.serch);

		yearArray = getyear_adapter();

		monthArray = getmonth_adapter();

		dayArray = get31day_adapter();

		year_adapter = new ArrayAdapter<String>(
				RenRenSerchUserDetailActivity.this,
				android.R.layout.simple_spinner_item, yearArray);
		year_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		month_adapter = new ArrayAdapter<String>(
				RenRenSerchUserDetailActivity.this,
				android.R.layout.simple_spinner_item, monthArray);
		month_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		day_adapter = new ArrayAdapter<String>(
				RenRenSerchUserDetailActivity.this,
				android.R.layout.simple_spinner_item, dayArray);
		day_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		birth_Year.setAdapter(year_adapter);
		birth_Year.setOnItemSelectedListener(selectedListener2);

		birth_Month.setAdapter(month_adapter);
		birth_Month.setOnItemSelectedListener(selectionListener);
		birth_Day.setAdapter(day_adapter);

		serch.setOnClickListener(this);
		headerHome.setOnClickListener(this);
		headerBack.setOnClickListener(this);

	}

	private OnItemSelectedListener selectionListener = new OnItemSelectedListener() {// 月对应的日期数

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int pos = birth_Month.getSelectedItemPosition();

			int pos2 = birth_Year.getSelectedItemPosition();

			int pos3 = 0;

			// String current_year=yearArray[pos];

			if (pos == 3 || pos == 5 || pos == 8 || pos == 10) {
				day_adapter = new ArrayAdapter<String>(
						RenRenSerchUserDetailActivity.this,
						android.R.layout.simple_spinner_item,
						get30day_adapter());
				day_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				birth_Day.setAdapter(day_adapter);
				pos = birth_Day.getSelectedItemPosition();
				day = dayArray[pos3];
			} else if (pos == 1) {
				day_adapter = new ArrayAdapter<String>(
						RenRenSerchUserDetailActivity.this,
						android.R.layout.simple_spinner_item,
						get28day_adapter());
				day_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				birth_Day.setAdapter(day_adapter);
				day = dayArray[pos3];
			} else {
				day_adapter = new ArrayAdapter<String>(
						RenRenSerchUserDetailActivity.this,
						android.R.layout.simple_spinner_item,
						get31day_adapter());
				day_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				birth_Day.setAdapter(day_adapter);
				pos = birth_Day.getSelectedItemPosition();
				day = dayArray[pos3];
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	private OnItemSelectedListener selectedListener2 = new OnItemSelectedListener() {// 年对应的日期数

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int pos = birth_Month.getSelectedItemPosition();

			int pos2 = birth_Year.getSelectedItemPosition();

			int pos3 = 0;

			String curren_Year = yearArray[pos2];
			int temp_Year = Integer.parseInt(curren_Year);

			if (((temp_Year % 4 == 0 && temp_Year % 100 != 0) || temp_Year % 400 == 0)
					&& (pos == 1)) {
				day_adapter = new ArrayAdapter<String>(
						RenRenSerchUserDetailActivity.this,
						android.R.layout.simple_spinner_item,
						get29day_adapter());
				day_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				birth_Day.setAdapter(day_adapter);
				pos = birth_Day.getSelectedItemPosition();
				day = dayArray[pos3];
			} else {

				day_adapter = new ArrayAdapter<String>(
						RenRenSerchUserDetailActivity.this,
						android.R.layout.simple_spinner_item,
						get28day_adapter());
				day_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				birth_Day.setAdapter(day_adapter);
				pos = birth_Day.getSelectedItemPosition();
				day = dayArray[pos3];
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {

			name.setText("");
			break;
		}
		case R.id.serch: {

			Intent comment = new Intent(RenRenSerchUserDetailActivity.this,
					BasicUserSearchActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", name.getText().toString());
			bundle.putString("flag", "friend");
			bundle.putString("year",
					yearArray[birth_Year.getSelectedItemPosition()]);
			bundle.putString("month",
					monthArray[birth_Month.getSelectedItemPosition()]);
			bundle.putString("day", day);
			comment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			comment.putExtras(bundle);
			startActivity(comment);
			break;
		}
		default:
			break;
		}

	}

	private String[] getyear_adapter() {
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

	private String[] getmonth_adapter() {
		String[] months = new String[12];
		for (int i = 0; i < months.length; i++) {
			int temp = ++i;

			months[--i] = String.valueOf(temp);

		}
		return months;
	}

	private String[] get31day_adapter() {
		String[] days = new String[31];
		for (int i = 0; i < days.length; i++) {
			int temp = ++i;
			days[--i] = String.valueOf(temp);

		}
		return days;
	}

	private String[] get30day_adapter() {
		String[] days = new String[30];
		for (int i = 0; i < days.length; i++) {
			int temp = ++i;
			days[--i] = String.valueOf(temp);

		}
		return days;
	}

	private String[] get29day_adapter() {
		String[] days = new String[29];
		for (int i = 0; i < days.length; i++) {
			int temp = ++i;
			days[--i] = String.valueOf(temp);

		}
		return days;
	}

	private String[] get28day_adapter() {
		String[] days = new String[28];
		for (int i = 0; i < days.length; i++) {
			int temp = ++i;
			days[--i] = String.valueOf(temp);

		}
		return days;
	}

	@Override
	protected void refreshByMenu() {
		// TODO Auto-generated method stub

	}

}
