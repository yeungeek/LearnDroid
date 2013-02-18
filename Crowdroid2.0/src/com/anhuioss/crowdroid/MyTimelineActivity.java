package com.anhuioss.crowdroid;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.service.CommHandler;

public class MyTimelineActivity extends TimelineActivity implements
		ServiceConnection {

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setTitle(R.string.my_timeline);

		setCommType(CommHandler.TYPE_GET_MY_TIME_LINE);
	}

}
