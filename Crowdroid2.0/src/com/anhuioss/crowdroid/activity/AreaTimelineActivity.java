package com.anhuioss.crowdroid.activity;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.service.CommHandler;

import android.content.ServiceConnection;
import android.os.Bundle;

public class AreaTimelineActivity extends TimelineActivity implements
		ServiceConnection {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set Title
		setTitle(R.string.area_timeline);

		// Set Communication Type
		setCommType(CommHandler.TYPE_GET_AREA_TIMELINE);
	}

}
