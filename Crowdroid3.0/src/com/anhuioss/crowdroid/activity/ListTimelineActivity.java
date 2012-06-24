package com.anhuioss.crowdroid.activity;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.dialog.ListDialog;
import com.anhuioss.crowdroid.service.CommHandler;

public class ListTimelineActivity extends TimelineActivity implements
		ServiceConnection {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Set Title
		setTitle(ListDialog.getListTimelineTitle());

		// Set Communication Type
		setCommType(CommHandler.TYPE_GET_LIST_TIMELINE);

	}

}
