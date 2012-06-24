package com.anhuioss.crowdroid.sns;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.service.CommHandler;

public class MyFeedTimelineActivity extends TimelineActivity implements ServiceConnection{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.my_feed);
		
		//RenRen :my feed
		setCommType(CommHandler.TYPE_GET_FAVORITE_LIST);
		 
	}

}
