package com.anhuioss.crowdroid;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.service.CommHandler;

public class UserTimelineActivity extends BasicSearchActivity implements
		ServiceConnection {

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setTitle(R.string.user_timeline);

		setCommType(CommHandler.TYPE_GET_USER_STATUS_LIST);
	}

}
