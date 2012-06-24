package com.anhuioss.crowdroid.activity;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.dialog.TrendDialog;
import com.anhuioss.crowdroid.service.CommHandler;

public class TrendTimelineActivity extends TimelineActivity implements
		ServiceConnection {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		// Set Title
		setTitle(TrendDialog.getTrendTimelineTitle());

		// Set Communication Type
		setCommType(CommHandler.TYPE_GET_TREND_TIMELNE);

	}

}
