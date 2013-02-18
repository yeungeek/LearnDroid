package com.anhuioss.crowdroid;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.service.CommHandler;

public class HomeTimelineActivity extends TimelineActivity implements
		ServiceConnection {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Set Title
		setTitle(R.string.home);

		// Set Communication Type
		setCommType(CommHandler.TYPE_GET_HOME_TIMELINE);

	}

}
