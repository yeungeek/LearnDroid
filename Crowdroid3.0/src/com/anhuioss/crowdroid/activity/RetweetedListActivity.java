package com.anhuioss.crowdroid.activity;


import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.BasicSearchActivity;
import com.anhuioss.crowdroid.R;

import com.anhuioss.crowdroid.service.CommHandler;


public class RetweetedListActivity extends BasicSearchActivity implements
		ServiceConnection {
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Set Title
		setTitle(R.string.retweeted_list);

		// Set Communication Type
		setCommType(CommHandler.TYPE_GET_RETWEETED_LIST_BY_ID);

	}

}