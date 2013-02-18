package com.anhuioss.crowdroid.operations;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.info.CalendarInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class ScheduleDetailActivity extends Activity implements
		OnClickListener, SeekBar.OnSeekBarChangeListener {

	private TextView title;

	private TextView startTime;

	private TextView endTime;

	private TextView injoyUsers;

	private TextView creator;

	private TextView type;

	private TextView description;

	private SeekBar seekBar;

	private Button close;

	private CalendarInfo calendarInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_detail_schedule);

		title = (TextView) findViewById(R.id.schedule_title);

		startTime = (TextView) findViewById(R.id.schedule_start_time);

		endTime = (TextView) findViewById(R.id.schedule_end_time);

		injoyUsers = (TextView) findViewById(R.id.schedule_injoy_users);

		creator = (TextView) findViewById(R.id.schedule_make_user);

		type = (TextView) findViewById(R.id.schedule_type);

		description = (TextView) findViewById(R.id.schedule_detail_content);

		seekBar = (SeekBar) findViewById(R.id.schedule_seekBar);

		seekBar.setOnSeekBarChangeListener(this);

		close = (Button) findViewById(R.id.schedule_close);

		close.setOnClickListener(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();

		calendarInfo = (CalendarInfo) getIntent().getExtras().getSerializable(
				"calendarInfo");
		if (calendarInfo != null) {
			initView();
		}
	}

	private void initView() {
		title.setText(calendarInfo.getTitle());
		startTime.setText(calendarInfo.getStartTime().substring(0, 19));
		endTime.setText(calendarInfo.getEndTime().substring(0, 19));
		injoyUsers.setText(calendarInfo.getConnectUsers());
		creator.setText(calendarInfo.getScreenName());
		type.setText(calendarInfo.getType());
		description.setText(calendarInfo.getMessage());
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.schedule_close:
			finish();
			break;
		default:
			break;
		}

	}

	// 拖动中
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

	}

	// 开始拖动
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	// 结束拖动
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

}
