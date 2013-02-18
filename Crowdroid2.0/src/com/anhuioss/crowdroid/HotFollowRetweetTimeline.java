package com.anhuioss.crowdroid;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.service.CommHandler;

public class HotFollowRetweetTimeline extends TimelineActivity implements
		ServiceConnection {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set Title
		setTitle(R.string.hot_followretweet_timeline);

		// Set Communication Type
		setCommType(CommHandler.TYPE_GET_HOT_FOLLOWRETWEET_TIMELINE);
	}
}
