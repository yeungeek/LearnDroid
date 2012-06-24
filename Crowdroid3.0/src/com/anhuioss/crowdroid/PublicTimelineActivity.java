package com.anhuioss.crowdroid;

import com.anhuioss.crowdroid.service.CommHandler;

import android.content.ServiceConnection;
import android.os.Bundle;

public class PublicTimelineActivity extends TimelineActivity implements
		ServiceConnection {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set Title
				setTitle(R.string.public_timeline);

				// Set Communication Type
				setCommType(CommHandler.TYPE_GET_PUBLIC_TIMELINE);
	}
}
