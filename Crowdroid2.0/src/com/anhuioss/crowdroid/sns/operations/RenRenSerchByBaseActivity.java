package com.anhuioss.crowdroid.sns.operations;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.BasicUserSearchActivity;
import com.anhuioss.crowdroid.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class RenRenSerchByBaseActivity extends BasicActivity implements
		OnClickListener {

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	private EditText name;

	private EditText prov;

	private EditText city;

	private RadioGroup sex_radiogroup;

	private RadioButton sex_boy;

	private RadioButton sex_girl;

	private RadioButton sex_none;

	private Button serch = null;

	private String sex = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.renren_serch_by_base);
		setLayoutResId(R.layout.renren_serch_by_base);
		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerName.setText(R.string.serch_by_base);
		headerHome.setBackgroundResource(R.drawable.header_clean);
		name = (EditText) findViewById(R.id.name);

		prov = (EditText) findViewById(R.id.prov);

		city = (EditText) findViewById(R.id.city);

		sex_radiogroup = (RadioGroup) findViewById(R.id.sex_radiogroup);

		sex_boy = (RadioButton) findViewById(R.id.sex_boy);

		sex_girl = (RadioButton) findViewById(R.id.sex_girl);

		sex_none = (RadioButton) findViewById(R.id.sex_none);

		serch = (Button) findViewById(R.id.serch);

		sex_radiogroup.setOnCheckedChangeListener(checkedListener);
		sex_none.setChecked(true);
		serch.setOnClickListener(this);
		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);

	}

	private OnCheckedChangeListener checkedListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			switch (checkedId) {
			case R.id.sex_boy: {
				sex = getString(R.string.male);

				break;
			}
			case R.id.sex_girl: {
				sex = getString(R.string.female);

				break;
			}
			case R.id.sex_none: {
				sex = "";

				break;
			}

			default:
				break;
			}
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
			prov.setText("");
			city.setText("");
			break;
		}
		case R.id.serch: {

			Intent comment = new Intent(RenRenSerchByBaseActivity.this,
					BasicUserSearchActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("name", name.getText().toString());
			bundle.putString("prov", prov.getText().toString());
			bundle.putString("city", city.getText().toString());
			bundle.putString("flag", "base");
			bundle.putString("sex", sex);
			comment.putExtras(bundle);
			comment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
