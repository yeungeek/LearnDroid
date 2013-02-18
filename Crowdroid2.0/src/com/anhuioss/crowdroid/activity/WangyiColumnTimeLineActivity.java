package com.anhuioss.crowdroid.activity;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.service.CommHandler;

public class WangyiColumnTimeLineActivity extends TimelineActivity implements
		ServiceConnection {

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setCommType(CommHandler.TYPE_GET_COLUMN_TIME_LINE);
	}
}
