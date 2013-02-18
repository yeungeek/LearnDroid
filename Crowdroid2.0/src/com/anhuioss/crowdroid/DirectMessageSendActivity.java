package com.anhuioss.crowdroid;

import android.content.ServiceConnection;
import android.os.Bundle;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.service.CommHandler;

public class DirectMessageSendActivity extends TimelineActivity implements
		ServiceConnection {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setTitle(R.string.direct_message_sent);

		setCommType(CommHandler.TYPE_GET_DIRECT_MESSAGE_SEND);
	}

}
