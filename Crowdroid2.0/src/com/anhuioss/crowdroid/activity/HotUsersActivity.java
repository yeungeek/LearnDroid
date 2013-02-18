package com.anhuioss.crowdroid.activity;

import com.anhuioss.crowdroid.BasicUserSearchActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.service.CommHandler;

import android.content.ServiceConnection;
import android.os.Bundle;

public class HotUsersActivity extends BasicUserSearchActivity implements
		ServiceConnection {

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setTitle(R.string.gallery_hot_users_title);

		setCommType(CommHandler.TYPE_GET_HOT_USERS);
	}

}
