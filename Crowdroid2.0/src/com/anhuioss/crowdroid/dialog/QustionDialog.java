package com.anhuioss.crowdroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.anhuioss.crowdroid.LoginActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.InvestigateQuestionsActivity;

public class QustionDialog extends Dialog implements OnClickListener {

	private TextView tipsText;

	private CheckBox showNextTime;

	private Button okButton;

	private Button noButton;

	private Context mContext;

	// ----------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param Context
	 *            context
	 */
	// ----------------------------------------------------------------------------
	public QustionDialog(Context context) {
		super(context);
		mContext = context;
	}

	// ----------------------------------------------------------------------------
	/**
	 * onCreate Method
	 * 
	 * @param Bundle
	 *            savedInstanceState
	 */
	// ----------------------------------------------------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_question_tip);
		setTitle("有奖问卷调查");

		// Find Views
		tipsText = (TextView) findViewById(R.id.quetip);
		showNextTime = (CheckBox) findViewById(R.id.check_box_show_next_time);
		okButton = (Button) findViewById(R.id.quesOK);
		noButton = (Button) findViewById(R.id.quesNo);

		// Set Click Listener
		okButton.setOnClickListener(this);
		noButton.setOnClickListener(this);
		showNextTime.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.quesOK: {
			Intent intent = new Intent();
			intent.setClass(mContext, InvestigateQuestionsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(intent);
			dismiss();
			break;
		}
		case R.id.quesNo: {
			// Intent intent = new Intent();
			// intent.setClass(mContext, LoginActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// mContext.startActivity(intent);
			dismiss();

			break;
		}
		case R.id.check_box_show_next_time: {
			closeDialog(!showNextTime.isChecked());
			break;
		}

		default:
			break;
		}

	}

	private void closeDialog(boolean isShowNextTime) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		Editor editor = prefs.edit();
		if (isShowNextTime) {
			editor.putBoolean("is-show-ques-tip", true);
		} else {
			editor.putBoolean("is-show-ques-tip", false);
		}
		editor.commit();

	}

}
