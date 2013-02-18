package com.anhuioss.crowdroid.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.LoginActivity;
import com.anhuioss.crowdroid.R;

public class InvestigateQuestionsActivity extends Activity implements
		OnClickListener, OnCheckedChangeListener {
	private Button preBtn;
	private Button nextBtn;
	private Button submit;
	private RelativeLayout top;
	private RelativeLayout mid;
	private LinearLayout index;// 首页
	private EditText userName;
	private EditText userPhone;
	private TextView question;
	// 性别组合
	private RadioGroup sexGroup;
	private RadioButton man;
	private RadioButton woman;
	// 十个题目的组合
	private RadioGroup q1Group;
	private LinearLayout q2Group;
	private RadioGroup q3Group;
	private RadioGroup q4Group;
	private RadioGroup q5Group;
	private RadioGroup q6Group;
	private RadioGroup q7Group;
	private RadioGroup q8Group;
	private RadioGroup q9Group;
	private RadioGroup q10Group;
	private RadioGroup q10_1Group;

	// 第一个题目的选项
	private RadioButton q1_item1;
	private RadioButton q1_item2;
	private RadioButton q1_item3;
	private RadioButton q1_item4;
	// 第二个题目的选项
	private CheckBox q2_item1;
	private CheckBox q2_item2;
	private CheckBox q2_item3;
	private CheckBox q2_item4;
	private CheckBox q2_item5;
	private CheckBox q2_item6;
	private CheckBox q2_item7;
	// 第三个题目的选项
	private RadioButton q3_item1;
	private RadioButton q3_item2;
	private RadioButton q3_item3;
	private RadioButton q3_item4;
	private RadioButton q3_item5;
	private RadioButton q3_item6;
	private RadioButton q3_item7;
	// 第四个题目的选项
	private RadioButton q4_item1;
	private RadioButton q4_item2;
	private RadioButton q4_item3;
	private RadioButton q4_item4;

	// 第五个题目的选项

	private RadioButton q5_item1;
	private RadioButton q5_item2;

	private RadioGroup knowGroup;
	private RadioButton knowGroup_item1;

	private RadioGroup wayGroup;
	private RadioButton wayGroup1;
	private RadioButton wayGroup2;
	private RadioButton wayGroup3;

	// 第六个题目的选项
	private RadioButton q6_item1;
	private RadioButton q6_item2;

	private RadioGroup feelGroup;
	private RadioButton feel;
	private RadioGroup feelGroupItems;
	private RadioButton feel_item1;
	private RadioButton feel_item2;

	// 第七个题目的选项
	private RadioButton q7_item1;
	private RadioButton q7_item2;
	private RadioButton q7_item3;
	private RadioButton q7_item4;

	// 第八个题目的选项
	private RadioButton q8_item1;
	private RadioButton q8_item2;
	private RadioButton q8_item3;
	private RadioButton q8_item4;

	// 第九个题目的选项
	private RadioButton q9_item1;
	private RadioButton q9_item2;
	private RadioButton q9_item3;

	// 第十个个题目的选项
	private RadioButton q10_item1;
	private RadioButton q10_item2;
	private RadioButton q10_item3;
	private RadioButton q10_item4;
	private RadioButton q10_item5;

	// 其他选项填写的Edittext
	private EditText q2_editWrite;
	private EditText q3_editWrite;
	private EditText q7_editWrite;
	private EditText q8_editWrite;

	private CheckBox showNextTime;
	// 出生年月组合
	private Spinner birth_Year;
	private Spinner birth_Month;
	private Spinner birth_Day;

	private String day = null;
	private String month = null;
	private String year = null;

	private String userNameStr = null;
	private String userSexStr = null;
	private String userBirthStr = null;
	private String userPhoneStr = null;

	private String toastStr = "";

	private ArrayAdapter<String> year_adapter;

	private ArrayAdapter<String> month_adapter;

	private ArrayAdapter<String> day_adapter;

	private String[] yearArray;

	private String[] monthArray;

	private String[] dayArray;

	private String indexStr = new String(
			"     感谢您使用本应用，请您花几分钟的时间做一个问卷调查，我们将从中抽取几名幸运者，给予一定的手机话费的奖励，请务必认真填写");

	private String[] questions = { "1: 您使用Android手机的时间是？",
			"2：您通常使用的微博服务是哪种或哪几种？", "3：您是通过何种途径了解到Crowdroid？",
			"4：您使用Crwodroid的频率？", "5：您了解我们的微薄型企业社交系统Crowdroid for Business吗？",
			"6：您使用过Crowdroid for Business吗？",
			"7：您还希望在Crwodroid的多服务器微薄中添加那个你喜欢的微薄客户端？",
			"8: 您希望Crowdroid还提供针对那种智能手机操作系统的相关版本？",
			"9：您对于Crwodroid应用整体UI的感受是？", "10：您使用Crowdroid的感受是？", };
	private String[] question1 = { "不到半年", "半年到一年", "一年到两年", "两年以上" };
	private String[] question2 = { "新浪微博", "腾讯微博", "网易微博", "搜狐微博", "人人网",
			"Crowdroid for Business", "其他" };
	private String[] question3 = { "新浪微博", "腾讯微博", "网易微博", "安徽开源软件有限公司官网",
			"朋友介绍", "各大应用广场", "其他" };
	private String[] question4 = { "每天多次", "每天一次", "几天一次", "几乎不用 " };
	private String[] question5 = { "了解", "不了解" };

	private String[] question6 = { "使用过", "未使用过" };
	private String[] question7 = { "FaceBook", "豆瓣", "人民网", "其他" };
	private String[] question8 = { "IPhone", "塞班", "MTK平台", "其他" };
	private String[] question9 = { "挺简洁的，还不错", "还好，一般", "烂透了，不喜欢" };
	private String[] question10 = { "整个过程非常流畅，没有理解成本", "我很难找到我喜欢的应用，找的过程很长",
			"我被一些功能所吸引，但我并不知道那些功能是干嘛的", "我很想把我手机里安装的好的应用分享出去", "整个过程出现各种错误" };

	private int topCount = 0;// 标题标识

	private int flag = 0;// 页标识

	String q2_1 = "";
	String q2_2 = "";
	String q2_3 = "";
	String q2_4 = "";
	String q2_5 = "";
	String q2_6 = "";
	String q2_7 = "";

	private String q1;
	private String q2;
	private String q3;
	private String q4;
	private String q5;
	private String q6;
	private String q7;
	private String q8;
	private String q9;
	private String q10;

	String toastStr1 = "";
	String toastStr2 = "";
	String toastStr3 = "";
	String toastStr4 = "";
	String toastStr5 = "";
	String toastStr6 = "";
	String toastStr7 = "";
	String toastStr8 = "";
	String toastStr9 = "";
	String toastStr10 = "";

	String toastStrName = "";

	int code = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_investigate_questions);
		findViews();

	}

	private void findViews() {
		preBtn = (Button) findViewById(R.id.up);
		nextBtn = (Button) findViewById(R.id.down);
		submit = (Button) findViewById(R.id.submit);

		top = (RelativeLayout) findViewById(R.id.top);
		mid = (RelativeLayout) findViewById(R.id.mid);
		index = (LinearLayout) findViewById(R.id.index);

		question = (TextView) findViewById(R.id.t1);

		userName = (EditText) findViewById(R.id.editUserName);
		userPhone = (EditText) findViewById(R.id.editUserPhone);

		sexGroup = (RadioGroup) findViewById(R.id.sexGroup);
		man = (RadioButton) findViewById(R.id.man);
		woman = (RadioButton) findViewById(R.id.woman);

		q2_editWrite = (EditText) findViewById(R.id.q2_editwrite);
		q3_editWrite = (EditText) findViewById(R.id.q3_editwrite);
		q7_editWrite = (EditText) findViewById(R.id.q7_editwrite);
		q8_editWrite = (EditText) findViewById(R.id.q8_editwrite);

		birth_Year = (Spinner) findViewById(R.id.year);
		birth_Month = (Spinner) findViewById(R.id.month);
		birth_Day = (Spinner) findViewById(R.id.day);

		// 十个题目的组合
		q1Group = (RadioGroup) findViewById(R.id.q1Group);
		q2Group = (LinearLayout) findViewById(R.id.q2Group);
		q3Group = (RadioGroup) findViewById(R.id.q3Group);
		q4Group = (RadioGroup) findViewById(R.id.q4Group);
		q5Group = (RadioGroup) findViewById(R.id.q5Group);
		q6Group = (RadioGroup) findViewById(R.id.q6Group);
		q7Group = (RadioGroup) findViewById(R.id.q7Group);
		q8Group = (RadioGroup) findViewById(R.id.q8Group);
		q9Group = (RadioGroup) findViewById(R.id.q9Group);
		q10Group = (RadioGroup) findViewById(R.id.q10Group);
		// 第一个
		q1_item1 = (RadioButton) findViewById(R.id.q1_item1);
		q1_item2 = (RadioButton) findViewById(R.id.q1_item2);
		q1_item3 = (RadioButton) findViewById(R.id.q1_item3);
		q1_item4 = (RadioButton) findViewById(R.id.q1_item4);
		// 第二个

		q2_item1 = (CheckBox) findViewById(R.id.q2_item1);
		q2_item2 = (CheckBox) findViewById(R.id.q2_item2);
		q2_item3 = (CheckBox) findViewById(R.id.q2_item3);
		q2_item4 = (CheckBox) findViewById(R.id.q2_item4);
		q2_item5 = (CheckBox) findViewById(R.id.q2_item5);
		q2_item6 = (CheckBox) findViewById(R.id.q2_item6);
		q2_item7 = (CheckBox) findViewById(R.id.q2_item7);

		final CheckBox[] q2_checkBoxes = {

		q2_item1, q2_item2, q2_item3, q2_item4, q2_item5, q2_item6, q2_item7

		};

		// 第三个

		q3_item1 = (RadioButton) findViewById(R.id.q3_item1);
		q3_item2 = (RadioButton) findViewById(R.id.q3_item2);
		q3_item3 = (RadioButton) findViewById(R.id.q3_item3);
		q3_item4 = (RadioButton) findViewById(R.id.q3_item4);
		q3_item5 = (RadioButton) findViewById(R.id.q3_item5);
		q3_item6 = (RadioButton) findViewById(R.id.q3_item6);
		q3_item7 = (RadioButton) findViewById(R.id.q3_item7);
		// 第四個
		q4_item1 = (RadioButton) findViewById(R.id.q4_item1);
		q4_item2 = (RadioButton) findViewById(R.id.q4_item2);
		q4_item3 = (RadioButton) findViewById(R.id.q4_item3);
		q4_item4 = (RadioButton) findViewById(R.id.q4_item4);

		// 第五个
		q5_item1 = (RadioButton) findViewById(R.id.q5_item1);
		q5_item2 = (RadioButton) findViewById(R.id.q5_item2);
		knowGroup = (RadioGroup) findViewById(R.id.konwGroup);
		knowGroup_item1 = (RadioButton) findViewById(R.id.knowGroup_item1);

		// 第六个
		q6Group = (RadioGroup) findViewById(R.id.q6Group);
		q6_item1 = (RadioButton) findViewById(R.id.q6_item1);
		q6_item2 = (RadioButton) findViewById(R.id.q6_item2);

		wayGroup = (RadioGroup) findViewById(R.id.wayGroup);
		wayGroup1 = (RadioButton) findViewById(R.id.wayGroup1);
		wayGroup2 = (RadioButton) findViewById(R.id.wayGroup2);
		wayGroup3 = (RadioButton) findViewById(R.id.wayGroup3);

		feelGroup = (RadioGroup) findViewById(R.id.feelGroup);
		feel = (RadioButton) findViewById(R.id.feel);
		feelGroupItems = (RadioGroup) findViewById(R.id.feelGroupItems);
		feel_item1 = (RadioButton) findViewById(R.id.feel_item1);
		feel_item2 = (RadioButton) findViewById(R.id.feel_item2);

		// 第七个
		q7_item1 = (RadioButton) findViewById(R.id.q7_item1);
		q7_item2 = (RadioButton) findViewById(R.id.q7_item2);
		q7_item3 = (RadioButton) findViewById(R.id.q7_item3);
		q7_item4 = (RadioButton) findViewById(R.id.q7_item4);

		// 第八个
		q8_item1 = (RadioButton) findViewById(R.id.q8_item1);
		q8_item2 = (RadioButton) findViewById(R.id.q8_item2);
		q8_item3 = (RadioButton) findViewById(R.id.q8_item3);
		q8_item4 = (RadioButton) findViewById(R.id.q8_item4);
		// 第九个
		q9_item1 = (RadioButton) findViewById(R.id.q9_item1);
		q9_item2 = (RadioButton) findViewById(R.id.q9_item2);
		q9_item3 = (RadioButton) findViewById(R.id.q9_item3);

		// 第十个
		q10_item1 = (RadioButton) findViewById(R.id.q10_item1);
		q10_item2 = (RadioButton) findViewById(R.id.q10_item2);
		q10_item3 = (RadioButton) findViewById(R.id.q10_item3);
		q10_item4 = (RadioButton) findViewById(R.id.q10_item4);
		q10_item5 = (RadioButton) findViewById(R.id.q10_item5);

		showNextTime = (CheckBox) findViewById(R.id.tip);

		preBtn.setEnabled(false);

		question.setText(indexStr);
		nextBtn.setEnabled(true);

		yearArray = getyear_adapter();

		monthArray = getmonth_adapter();

		dayArray = get31day_adapter();

		//
		year_adapter = new ArrayAdapter<String>(
				InvestigateQuestionsActivity.this,
				android.R.layout.simple_spinner_item, yearArray);
		year_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		month_adapter = new ArrayAdapter<String>(
				InvestigateQuestionsActivity.this,
				android.R.layout.simple_spinner_item, monthArray);
		month_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		day_adapter = new ArrayAdapter<String>(
				InvestigateQuestionsActivity.this,
				android.R.layout.simple_spinner_item, dayArray);
		day_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		birth_Year.setAdapter(year_adapter);
		birth_Year.setOnItemSelectedListener(selectedListener2);

		birth_Month.setAdapter(month_adapter);
		birth_Month.setOnItemSelectedListener(selectionListener);

		birth_Day.setAdapter(day_adapter);
		birth_Day.setOnItemSelectedListener(selectedListener3);

		preBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		submit.setOnClickListener(this);

		sexGroup.setOnCheckedChangeListener(this);
		q1Group.setOnCheckedChangeListener(this);
		q3Group.setOnCheckedChangeListener(this);
		q4Group.setOnCheckedChangeListener(this);
		q5Group.setOnCheckedChangeListener(this);
		q6Group.setOnCheckedChangeListener(this);
		q7Group.setOnCheckedChangeListener(this);
		q8Group.setOnCheckedChangeListener(this);
		q9Group.setOnCheckedChangeListener(this);
		q10Group.setOnCheckedChangeListener(this);

		knowGroup.setOnCheckedChangeListener(this);

		wayGroup.setOnCheckedChangeListener(this);

		feelGroup.setOnCheckedChangeListener(this);

		feelGroupItems.setOnCheckedChangeListener(this);

		for (final CheckBox checkBox : q2_checkBoxes) {

			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					if (q2_item1.isChecked()) {

						q2_1 = "A,";
					}
					if (q2_item2.isChecked()) {

						q2_2 = "B,";
					}
					if (q2_item3.isChecked()) {

						q2_3 = "C,";

					}
					if (q2_item4.isChecked()) {

						q2_4 = "D,";

					}
					if (q2_item5.isChecked()) {

						q2_5 = "E,";

					}
					if (q2_item6.isChecked()) {

						q2_6 = "F,";

					}
					if (q2_item7.isChecked()) {
						q2_editWrite.setVisibility(View.VISIBLE);
						q2_editWrite.setFocusable(true);
						if (("").equals(q2_editWrite.getText().toString())) {
							Toast.makeText(InvestigateQuestionsActivity.this,
									"内容不能为空！", Toast.LENGTH_LONG).show();
							q2_editWrite.setFocusable(true);
						}

					} else {
						q2_editWrite.setVisibility(View.GONE);

					}

				}
			});
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.up: {
			q1Group.setVisibility(View.GONE);
			q2Group.setVisibility(View.GONE);
			q3Group.setVisibility(View.GONE);
			q4Group.setVisibility(View.GONE);
			q5Group.setVisibility(View.GONE);
			q6Group.setVisibility(View.GONE);
			q7Group.setVisibility(View.GONE);
			q8Group.setVisibility(View.GONE);
			q9Group.setVisibility(View.GONE);
			q10Group.setVisibility(View.GONE);
			index.setVisibility(View.GONE);

			q2_editWrite.setVisibility(View.GONE);
			q3_editWrite.setVisibility(View.GONE);
			q8_editWrite.setVisibility(View.GONE);
			q8_editWrite.setVisibility(View.GONE);
			showNextTime.setVisibility(View.GONE);
			knowGroup.setVisibility(View.GONE);

			wayGroup.setVisibility(View.GONE);

			feelGroup.setVisibility(View.GONE);

			feelGroupItems.setVisibility(View.GONE);

			topCount--;
			preBtn.setEnabled(true);
			nextBtn.setEnabled(true);

			if (topCount == 0) {

				preBtn.setEnabled(false);
				question.setText(indexStr);
				index.setVisibility(View.VISIBLE);

				flag = 0;

			} else {
				question.setText(questions[topCount - 1]);

			}
			if (topCount == 1) {

				q1Group.setVisibility(View.VISIBLE);

				q1_item1.setText(question1[0]);
				q1_item2.setText(question1[1]);
				q1_item3.setText(question1[2]);
				q1_item4.setText(question1[3]);

				flag = 1;

			} else if (topCount == 2) {

				flag = 2;

				q2Group.setVisibility(View.VISIBLE);

				q2_item1.setText(question2[0]);
				q2_item2.setText(question2[1]);
				q2_item3.setText(question2[2]);
				q2_item4.setText(question2[3]);
				q2_item5.setText(question2[4]);
				q2_item6.setText(question2[5]);
				q2_item7.setText(question2[6]);

			} else if (topCount == 3) {

				flag = 3;

				q3Group.setVisibility(View.VISIBLE);

				q3_item2.setText(question3[1]);
				q3_item3.setText(question3[2]);
				q3_item4.setText(question3[3]);
				q3_item5.setText(question3[4]);
				q3_item6.setText(question3[5]);
				q3_item7.setText(question3[6]);

			} else if (topCount == 4) {

				flag = 4;
				q4Group.setVisibility(View.VISIBLE);

				q4_item1.setText(question4[0]);
				q4_item2.setText(question4[1]);
				q4_item3.setText(question4[2]);
				q4_item4.setText(question4[3]);

			} else if (topCount == 5) {

				flag = 5;

				q5Group.setVisibility(View.VISIBLE);
				q5_item1.setText(question5[0]);
				q5_item2.setText(question5[1]);

			} else if (topCount == 6) {

				flag = 6;

				q6Group.setVisibility(View.VISIBLE);

				q6_item1.setText(question6[0]);
				q6_item2.setText(question6[1]);

			} else if (topCount == 7) {

				flag = 7;

				q7Group.setVisibility(View.VISIBLE);

				q7_item1.setText(question7[0]);
				q7_item2.setText(question7[1]);
				q7_item3.setText(question7[2]);
				q7_item4.setText(question7[3]);

			} else if (topCount == 8) {

				flag = 8;

				q8Group.setVisibility(View.VISIBLE);

				q8_item1.setText(question8[0]);
				q8_item2.setText(question8[1]);
				q8_item3.setText(question8[2]);
				q8_item4.setText(question8[3]);

			} else if (topCount == 9) {

				flag = 9;

				q9Group.setVisibility(View.VISIBLE);

				q9_item1.setText(question9[0]);
				q9_item2.setText(question9[1]);
				q9_item3.setText(question9[2]);

			} else if (topCount == 10) {

				nextBtn.setEnabled(false);
				flag = 10;

				q10Group.setVisibility(View.VISIBLE);

				q10_item1.setText(question10[0]);
				q10_item2.setText(question10[1]);
				q10_item3.setText(question10[2]);
				q10_item4.setText(question10[3]);
				q10_item5.setText(question10[4]);

			}

			break;
		}
		case R.id.down: {

			showNextTime.setVisibility(View.GONE);
			q2_editWrite.setVisibility(View.GONE);
			q3_editWrite.setVisibility(View.GONE);
			q8_editWrite.setVisibility(View.GONE);
			q8_editWrite.setVisibility(View.GONE);

			knowGroup.setVisibility(View.GONE);

			wayGroup.setVisibility(View.GONE);

			feelGroup.setVisibility(View.GONE);

			feelGroupItems.setVisibility(View.GONE);
			topCount++;
			q1Group.setVisibility(View.GONE);
			q2Group.setVisibility(View.GONE);
			q3Group.setVisibility(View.GONE);
			q4Group.setVisibility(View.GONE);
			q5Group.setVisibility(View.GONE);
			q6Group.setVisibility(View.GONE);
			q7Group.setVisibility(View.GONE);
			q8Group.setVisibility(View.GONE);
			q9Group.setVisibility(View.GONE);
			q10Group.setVisibility(View.GONE);
			index.setVisibility(View.GONE);

			preBtn.setEnabled(true);
			nextBtn.setEnabled(true);

			if (topCount == questions.length) {
				nextBtn.setEnabled(false);

				question.setText(questions[topCount - 1]);
			} else {

				question.setText(questions[topCount - 1]);

			}

			if (topCount == 1) {

				q1Group.setVisibility(View.VISIBLE);

				q1_item1.setText(question1[0]);
				q1_item2.setText(question1[1]);
				q1_item3.setText(question1[2]);
				q1_item4.setText(question1[3]);

				flag = 1;

			} else if (topCount == 2) {

				flag = 2;

				q2Group.setVisibility(View.VISIBLE);

				q2_item1.setText(question2[0]);
				q2_item2.setText(question2[1]);
				q2_item3.setText(question2[2]);
				q2_item4.setText(question2[3]);
				q2_item5.setText(question2[4]);
				q2_item6.setText(question2[5]);
				q2_item7.setText(question2[6]);

			} else if (topCount == 3) {

				flag = 3;

				q3Group.setVisibility(View.VISIBLE);

				q3_item1.setText(question3[0]);
				q3_item2.setText(question3[1]);
				q3_item3.setText(question3[2]);
				q3_item4.setText(question3[3]);
				q3_item5.setText(question3[4]);
				q3_item6.setText(question3[5]);
				q3_item7.setText(question3[6]);

			} else if (topCount == 4) {

				flag = 4;

				q4Group.setVisibility(View.VISIBLE);

				q4_item1.setText(question4[0]);
				q4_item2.setText(question4[1]);
				q4_item3.setText(question4[2]);
				q4_item4.setText(question4[3]);

			} else if (topCount == 5) {

				flag = 5;

				q5Group.setVisibility(View.VISIBLE);

				q5_item1.setText(question5[0]);
				q5_item2.setText(question5[1]);

			} else if (topCount == 6) {

				flag = 6;

				q6Group.setVisibility(View.VISIBLE);

				q6_item1.setText(question6[0]);
				q6_item2.setText(question6[1]);

			} else if (topCount == 7) {

				flag = 7;

				q7Group.setVisibility(View.VISIBLE);

				q7_item1.setText(question7[0]);
				q7_item2.setText(question7[1]);
				q7_item3.setText(question7[2]);
				q7_item4.setText(question7[3]);

			} else if (topCount == 8) {

				flag = 8;

				q8Group.setVisibility(View.VISIBLE);

				q8_item1.setText(question8[0]);
				q8_item2.setText(question8[1]);
				q8_item3.setText(question8[2]);
				q8_item4.setText(question8[3]);

			} else if (topCount == 9) {

				flag = 9;

				q9Group.setVisibility(View.VISIBLE);

				q9_item1.setText(question9[0]);
				q9_item2.setText(question9[1]);
				q9_item3.setText(question9[2]);

			} else if (topCount == 10) {
				flag = 10;

				q10Group.setVisibility(View.VISIBLE);

				nextBtn.setEnabled(false);

				q10_item1.setText(question10[0]);
				q10_item2.setText(question10[1]);
				q10_item3.setText(question10[2]);
				q10_item4.setText(question10[3]);
				q10_item5.setText(question10[4]);

			}

			break;
		}
		case R.id.submit: {
			userNameStr = userName.getText().toString();// 姓名

			year = yearArray[birth_Year.getSelectedItemPosition()];
			month = monthArray[birth_Month.getSelectedItemPosition()];

			userBirthStr = year + "-" + month + "-" + day;// 出生年月
			userPhoneStr = userPhone.getText().toString();// 联系电话
			if (("").equals(userName.getText().toString())
					|| ("").equals(userPhone.getText().toString())
					|| ("").equals(userSexStr)) {
				// toastStrName = "您还没有填写姓名！";
				toastStrName = "您的信息还未填写完整！";
			} else {
				toastStrName = "";
			}

			if (IsFull() == false) {
				new AlertDialog.Builder(InvestigateQuestionsActivity.this)
						.setTitle("警告")
						.setMessage(
								"您还有第"
										+ toastStr.substring(0,
												toastStr.length() - 1)
										+ "题没有完成！" + toastStrName)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}

								}).create().show();
			} else if (IsFull() == true && !toastStrName.equals("")) {

				new AlertDialog.Builder(InvestigateQuestionsActivity.this)
						.setTitle("警告")
						.setMessage(toastStrName)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										q1Group.setVisibility(View.GONE);
										q2Group.setVisibility(View.GONE);
										q3Group.setVisibility(View.GONE);
										q4Group.setVisibility(View.GONE);
										q5Group.setVisibility(View.GONE);
										q6Group.setVisibility(View.GONE);
										q7Group.setVisibility(View.GONE);
										q8Group.setVisibility(View.GONE);
										q9Group.setVisibility(View.GONE);
										q10Group.setVisibility(View.GONE);
										index.setVisibility(View.VISIBLE);
										question.setText(indexStr);
										showNextTime.setVisibility(View.GONE);
									}

								}).create().show();

			} else {
				try {
					postJson();
					if (code == 200) {
						SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(InvestigateQuestionsActivity.this);
						Editor editor = prefs.edit();
						editor.putBoolean("is-show-ques-tip", false);
						editor.commit();
						finish();

						Toast.makeText(InvestigateQuestionsActivity.this,
								getString(R.string.success), Toast.LENGTH_LONG)
								.show();
					}

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

			}

			break;
		}

		default:
			break;
		}
	}

	private OnItemSelectedListener selectionListener = new OnItemSelectedListener() {// 月对应的日期数

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int pos = birth_Month.getSelectedItemPosition();

			int pos2 = birth_Year.getSelectedItemPosition();

			int pos3 = 0;

			if (pos == 3 || pos == 5 || pos == 8 || pos == 10) {
				day_adapter = new ArrayAdapter<String>(
						InvestigateQuestionsActivity.this,
						android.R.layout.simple_spinner_item,
						get30day_adapter());
				day_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				birth_Day.setAdapter(day_adapter);

			} else if (pos == 1) {
				day_adapter = new ArrayAdapter<String>(
						InvestigateQuestionsActivity.this,
						android.R.layout.simple_spinner_item,
						get28day_adapter());
				day_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				birth_Day.setAdapter(day_adapter);

			} else {
				day_adapter = new ArrayAdapter<String>(
						InvestigateQuestionsActivity.this,
						android.R.layout.simple_spinner_item,
						get31day_adapter());
				day_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				birth_Day.setAdapter(day_adapter);

			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
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
						InvestigateQuestionsActivity.this,
						android.R.layout.simple_spinner_item,
						get29day_adapter());
				day_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				birth_Day.setAdapter(day_adapter);

			} else {

				day_adapter = new ArrayAdapter<String>(
						InvestigateQuestionsActivity.this,
						android.R.layout.simple_spinner_item,
						get28day_adapter());
				day_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				birth_Day.setAdapter(day_adapter);

			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};
	private OnItemSelectedListener selectedListener3 = new OnItemSelectedListener() {// 年对应的日期数

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			day = dayArray[arg2];
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

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
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		q2_editWrite.setVisibility(View.GONE);
		q3_editWrite.setVisibility(View.GONE);
		q8_editWrite.setVisibility(View.GONE);
		q8_editWrite.setVisibility(View.GONE);
		knowGroup.setVisibility(View.GONE);

		wayGroup.setVisibility(View.GONE);

		feelGroup.setVisibility(View.GONE);

		feelGroupItems.setVisibility(View.GONE);
		switch (flag) {
		case 0: {

		}
		case 1: {

			switch (checkedId) {
			case R.id.q1_item1: {

				q1 = "A";

				break;
			}
			case R.id.q1_item2: {

				q1 = "B";

				break;
			}
			case R.id.q1_item3: {

				q1 = "C";

				break;
			}
			case R.id.q1_item4: {

				q1 = "D";

				break;
			}

			default:
				break;
			}

			break;
		}

		case 3: {

			switch (checkedId) {
			case R.id.q3_item1: {

				q3 = "A";

				break;
			}
			case R.id.q3_item2: {

				q3 = "B";

				break;
			}
			case R.id.q3_item3: {

				q3 = "C";

				break;
			}
			case R.id.q3_item4: {

				q3 = "D";

				break;
			}
			case R.id.q3_item5: {

				q3 = "E";

				break;
			}
			case R.id.q3_item6: {

				q3 = "F";

				break;
			}
			case R.id.q3_item7: {

				q3_editWrite.setVisibility(View.VISIBLE);
				q3_editWrite.setFocusable(true);
				q3_editWrite
						.setOnFocusChangeListener(new OnFocusChangeListener() {

							@Override
							public void onFocusChange(View v, boolean hasFocus) {
								if (q3_editWrite.hasFocus() == false) {
									if (q3_editWrite.getText().toString()
											.equals("")
											|| q3_editWrite.getText()
													.toString().equals(null)) {

										new AlertDialog.Builder(
												InvestigateQuestionsActivity.this)
												.setTitle("警告")
												.setMessage("输入内容为空，请填写相关内容！")
												.setPositiveButton(
														"确定",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																q3_item7.setChecked(true);
																q3_editWrite
																		.setFocusable(true);
															}

														}).create().show();

									} else {
										q3 = "O,"
												+ q3_editWrite.getText()
														.toString();
									}

								}

							}
						});

				break;
			}
			default:
				break;
			}
			break;
		}
		case 4: {

			switch (checkedId) {
			case R.id.q4_item1: {

				q4 = "A";

				break;
			}
			case R.id.q4_item2: {

				q4 = "B";

				break;
			}
			case R.id.q4_item3: {

				q4 = "C";

				break;
			}
			case R.id.q4_item4: {

				q4 = "D";

				break;
			}

			default:
				break;
			}
			break;
		}
		case 5: {

			switch (checkedId) {
			case R.id.q5_item1: {// 了解

				knowGroup.setVisibility(View.VISIBLE);

				q5 = "A";

				break;
			}
			case R.id.q5_item2: {// 不了解

				knowGroup.clearCheck();
				wayGroup.clearCheck();

				q5 = "B";

				break;
			}
			case R.id.knowGroup_item1: {// 是通过何种途径了解的
				knowGroup.setVisibility(View.VISIBLE);
				wayGroup.setVisibility(View.VISIBLE);
				break;
			}
			case R.id.wayGroup1: {// 安徽开源软件有限公司
				knowGroup.setVisibility(View.VISIBLE);
				wayGroup.setVisibility(View.VISIBLE);
				q5 = "A1_A";
				break;
			}
			case R.id.wayGroup2: {// 新浪，腾讯微博
				knowGroup.setVisibility(View.VISIBLE);
				wayGroup.setVisibility(View.VISIBLE);
				q5 = "A1_B";
				break;
			}
			case R.id.wayGroup3: {// BBS论坛
				knowGroup.setVisibility(View.VISIBLE);
				wayGroup.setVisibility(View.VISIBLE);
				q5 = "A1_C";
				break;
			}

			default:
				break;
			}
			break;
		}
		case 6: {

			switch (checkedId) {

			case R.id.q6_item1: {
				feelGroup.setVisibility(View.VISIBLE);
				q6 = "A";

				break;
			}
			case R.id.q6_item2: {

				feelGroup.clearCheck();
				feelGroupItems.clearCheck();
				q6 = "B";

				break;
			}
			case R.id.feel: {// 感觉如何

				feelGroup.setVisibility(View.VISIBLE);
				feelGroupItems.setVisibility(View.VISIBLE);
				break;

			}
			case R.id.feel_item1: {// 结合

				feelGroup.setVisibility(View.VISIBLE);
				feelGroupItems.setVisibility(View.VISIBLE);
				q6 = "A1_A";
				break;
			}
			case R.id.feel_item2: {// 一般

				feelGroup.setVisibility(View.VISIBLE);
				feelGroupItems.setVisibility(View.VISIBLE);
				q6 = "A1_B";
				break;
			}

			default:
				break;
			}
			break;
		}
		case 7: {

			switch (checkedId) {
			case R.id.q7_item1: {

				q7 = "A";

				break;
			}
			case R.id.q7_item2: {

				q7 = "B";

				break;
			}
			case R.id.q7_item3: {

				q7 = "C";

				break;
			}
			case R.id.q7_item4: {
				q7_editWrite.setVisibility(View.VISIBLE);
				q7_editWrite.setFocusable(true);
				q7_editWrite
						.setOnFocusChangeListener(new OnFocusChangeListener() {

							@Override
							public void onFocusChange(View v, boolean hasFocus) {
								if (q7_editWrite.hasFocus() == false) {
									if (q7_editWrite.getText().toString()
											.equals("")
											|| q7_editWrite.getText()
													.toString().equals(null)) {

										new AlertDialog.Builder(
												InvestigateQuestionsActivity.this)
												.setTitle("警告")
												.setMessage("输入内容为空，请填写相关内容！")
												.setPositiveButton(
														"确定",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																q7_item4.setChecked(true);
																q7_editWrite
																		.setFocusable(true);
															}

														}).create().show();

									} else {
										q7 = "O,"
												+ q7_editWrite.getText()
														.toString();
									}

								}

							}
						});

				break;
			}
			default:
				break;
			}
			break;
		}
		case 8: {

			switch (checkedId) {
			case R.id.q8_item1: {

				q8 = "A";

				break;
			}
			case R.id.q8_item2: {

				q8 = "B";

				break;
			}
			case R.id.q8_item3: {

				q8 = "C";

				break;
			}
			case R.id.q8_item4: {

				q8_editWrite.setVisibility(View.VISIBLE);
				q8_editWrite.setFocusable(true);
				q8_editWrite
						.setOnFocusChangeListener(new OnFocusChangeListener() {

							@Override
							public void onFocusChange(View v, boolean hasFocus) {
								if (q8_editWrite.hasFocus() == false) {
									if (q8_editWrite.getText().toString()
											.equals("")
											|| q8_editWrite.getText()
													.toString().equals(null)) {

										new AlertDialog.Builder(
												InvestigateQuestionsActivity.this)
												.setTitle("警告")
												.setMessage("输入内容为空，请填写相关内容！")
												.setPositiveButton(
														"确定",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																q8_item4.setChecked(true);
																q8_editWrite
																		.setFocusable(true);
															}

														}).create().show();

									} else {
										q8 = "O,"
												+ q8_editWrite.getText()
														.toString();
									}

								}

							}
						});

				break;
			}

			default:
				break;
			}
			break;
		}
		case 9: {

			switch (checkedId) {
			case R.id.q9_item1: {

				q9 = "A";

				break;
			}
			case R.id.q9_item2: {

				q9 = "B";

				break;
			}
			case R.id.q9_item3: {

				q9 = "C";

				break;
			}

			default:
				break;
			}
			break;
		}
		case 10: {

			switch (checkedId) {
			case R.id.q10_item1: {// 企业用户

				q10 = "A";

				break;
			}
			case R.id.q10_item2: {// 个人用户

				q10 = "B";

				break;
			}
			case R.id.q10_item3: {

				q10 = "C";
				break;
			}
			case R.id.q10_item4: {

				q10 = "D";
				break;
			}
			case R.id.q10_item5: {

				q10 = "E";

				break;
			}

			default:
				break;
			}
			break;
		}

		default:
			break;
		}
		switch (checkedId) {
		case R.id.man: {
			userSexStr = "M";
			break;
		}
		case R.id.woman: {
			userSexStr = "W";
			break;
		}

		default:
			break;
		}

	}

	private boolean IsFull() {

		if (q1_item1.isChecked() == false && q1_item2.isChecked() == false
				&& q1_item3.isChecked() == false
				&& q1_item4.isChecked() == false) {
			toastStr1 = "1,";
		} else {
			toastStr1 = "";
		}
		if (q2_item1.isChecked() == false && q2_item2.isChecked() == false
				&& q2_item3.isChecked() == false
				&& q2_item4.isChecked() == false
				&& q2_item5.isChecked() == false
				&& q2_item6.isChecked() == false
				&& q2_editWrite.getText().toString().equals("")) {
			toastStr2 = "2,";
		} else {
			toastStr2 = "";
		}
		if (q3_item1.isChecked() == false && q3_item2.isChecked() == false
				&& q3_item3.isChecked() == false
				&& q3_item4.isChecked() == false
				&& q3_item5.isChecked() == false
				&& q3_item6.isChecked() == false
				&& q3_editWrite.getText().toString().equals("")) {
			toastStr3 = "3,";
		} else {
			toastStr3 = "";
		}
		if (q4_item1.isChecked() == false && q4_item2.isChecked() == false
				&& q4_item3.isChecked() == false
				&& q4_item4.isChecked() == false) {
			toastStr4 = "4,";
		} else {
			toastStr4 = "";
		}
		if (q5_item2.isChecked() == false && q5_item1.isChecked() == false
				|| q5_item2.isChecked() == false
				&& q5_item1.isChecked() == true
				&& knowGroup_item1.isChecked() == true
				&& wayGroup1.isChecked() == false
				&& wayGroup2.isChecked() == false
				&& wayGroup3.isChecked() == false
				|| q5_item2.isChecked() == false
				&& q5_item1.isChecked() == true
				&& knowGroup_item1.isChecked() == false) {
			toastStr5 = "5,";
		} else {
			toastStr5 = "";
		}
		if (q6_item2.isChecked() == false && q6_item1.isChecked() == false
				|| q6_item2.isChecked() == false
				&& q6_item1.isChecked() == true && feel.isChecked() == false
				&& feel_item1.isChecked() == false
				&& feel_item2.isChecked() == false
				|| q6_item2.isChecked() == false
				&& q6_item1.isChecked() == true && feel.isChecked() == true
				&& feel_item1.isChecked() == false
				&& feel_item2.isChecked() == false

		) {
			toastStr6 = "6,";
		} else {
			toastStr6 = "";
		}
		if (q7_item1.isChecked() == false && q7_item2.isChecked() == false
				&& q7_item3.isChecked() == false
				&& q7_editWrite.getText().toString().equals("")) {
			toastStr7 = "7,";
		} else {
			toastStr7 = "";
		}
		if (q8_item1.isChecked() == false && q8_item2.isChecked() == false
				&& q8_item3.isChecked() == false
				&& q8_editWrite.getText().toString().equals("")) {
			toastStr8 = "8,";
		} else {
			toastStr8 = "";
		}
		if (q9_item1.isChecked() == false && q9_item2.isChecked() == false
				&& q9_item3.isChecked() == false) {
			toastStr9 = "9,";
		} else {
			toastStr9 = "";
		}
		if (q10_item1.isChecked() == false && q10_item2.isChecked() == false
				&& q10_item3.isChecked() == false
				&& q10_item4.isChecked() == false
				&& q10_item5.isChecked() == false) {
			toastStr10 = "10,";
		} else {
			toastStr10 = "";
		}

		toastStr = toastStr1 + toastStr2 + toastStr3 + toastStr4 + toastStr5
				+ toastStr6 + toastStr7 + toastStr8 + toastStr9 + toastStr10;

		if (toastStr.equals("")) {

			return true;

		} else {

			return false;
		}

	}

	private void postJson() throws UnsupportedEncodingException {

		PackageInfo pInfo;

		String postUrl = "http://www.crowdroid.com/questionnaire/action/getAnswers.php";

		// String postUrl =
		// "http://192.168.1.138/crowdroidquestionnaire/action/getAnswers.php";
		HttpClient httpClient = new DefaultHttpClient();
		try {

			if (q2_item7.isChecked()
					&& !("").equals(q2_editWrite.getText().toString())) {
				q2_7 = ":" + q2_editWrite.getText().toString();
			}
			q2 = q2_1 + q2_2 + q2_3 + q2_4 + q2_5 + q2_6 + q2_7;
			pInfo = InvestigateQuestionsActivity.this.getPackageManager()
					.getPackageInfo(
							InvestigateQuestionsActivity.this.getPackageName(),
							0);
			HttpPost httpPost = new HttpPost(postUrl);
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("device", Build.DEVICE);
			jsonObject.put("sdk_v", Build.VERSION.SDK);
			jsonObject.put("apk_v", pInfo.versionName);
			jsonObject.put("name", userNameStr);
			jsonObject.put("sex", userSexStr);
			jsonObject.put("birth", userBirthStr);
			jsonObject.put("tel", userPhoneStr);
			jsonObject.put("q1", q1);
			jsonObject.put("q2", q2);
			jsonObject.put("q3", q3);
			jsonObject.put("q4", q4);
			jsonObject.put("q5", q5);
			jsonObject.put("q6", q6);
			jsonObject.put("q7", q7);
			jsonObject.put("q8", q8);
			jsonObject.put("q9", q9);
			jsonObject.put("q10", q10);
			nameValuePair.add(new BasicNameValuePair("answer", jsonObject
					.toString()));
			Log.e("answer", jsonObject.toString());
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair,
					HTTP.UTF_8));
			httpClient.execute(httpPost);
			HttpResponse response = httpClient.execute(httpPost);
			code = response.getStatusLine().getStatusCode();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

	}

}
