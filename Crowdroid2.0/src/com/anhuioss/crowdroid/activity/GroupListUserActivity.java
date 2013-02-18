package com.anhuioss.crowdroid.activity;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.BasicUserSearchActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.service.CommHandler;

public class GroupListUserActivity extends BasicUserSearchActivity implements
		ServiceConnection {

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setTitle(R.string.group_list);

		setCommType(CommHandler.TYPE_GET_GROUP_USER_LIST);
	}

}
