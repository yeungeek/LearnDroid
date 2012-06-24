package com.anhuioss.crowdroid.sns;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.service.CommHandler;

public class MyShareTimelineActivity extends TimelineActivity implements ServiceConnection{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.my_sharing_feed);
		
		//RenRen :my share
		setCommType(CommHandler.TYPE_GET_USER_STATUS_LIST);
		 
	}

}
