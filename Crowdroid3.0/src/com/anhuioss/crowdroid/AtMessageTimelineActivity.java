package com.anhuioss.crowdroid;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.service.CommHandler;

public class AtMessageTimelineActivity extends TimelineActivity implements ServiceConnection {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.at_message);
		
		setCommType(CommHandler.TYPE_GET_AT_MESSAGE);
		 
	}

}
