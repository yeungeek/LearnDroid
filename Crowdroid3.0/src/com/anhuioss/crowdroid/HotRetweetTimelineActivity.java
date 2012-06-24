package com.anhuioss.crowdroid;

import com.anhuioss.crowdroid.service.CommHandler;

import android.content.ServiceConnection;
import android.os.Bundle;

public class HotRetweetTimelineActivity extends TimelineActivity implements ServiceConnection{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set Title
				setTitle(R.string.hot_retweet_timeline);

				// Set Communication Type
				setCommType(CommHandler.TYPE_GET_HOT_RETWEET_TIMELINE);
	}
}
