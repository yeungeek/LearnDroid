package com.anhuioss.crowdroid;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.service.CommHandler;

public class DirectMessageReceiveActivity extends TimelineActivity implements
		ServiceConnection {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setTitle(R.string.direct_message_received);

		setCommType(CommHandler.TYPE_GET_DIRECT_MESSAGE_RECEIVE);
	}

}
