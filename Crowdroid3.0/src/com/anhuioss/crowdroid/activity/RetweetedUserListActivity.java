package com.anhuioss.crowdroid.activity;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.BasicUserSearchActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.service.CommHandler;

public class RetweetedUserListActivity extends BasicUserSearchActivity
		implements ServiceConnection {
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Set Title
		setTitle(R.string.retweeted_user);

		// Set Communication Type
		setCommType(CommHandler.TYPE_GET_RETWEETED_USER_LIST_BY_ID);

	}
}