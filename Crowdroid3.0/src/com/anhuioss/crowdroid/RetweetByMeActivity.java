package com.anhuioss.crowdroid;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.service.CommHandler;

public class RetweetByMeActivity extends TimelineActivity implements
		ServiceConnection {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Set Title
		setTitle(R.string.retweets_by_me);

		// Set Communication Type
		setCommType(CommHandler.TYPE_GET_RETWEET_BY_ME_TIME_LINE);

	}

}
