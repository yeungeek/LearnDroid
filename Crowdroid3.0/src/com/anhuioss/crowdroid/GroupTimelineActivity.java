package com.anhuioss.crowdroid;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.service.CommHandler;

public class GroupTimelineActivity extends BasicSearchActivity  implements ServiceConnection {
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setTitle("Group Timeline");
		
		setCommType(CommHandler.TYPE_GET_GROUP_TIMELINE);
	}

}