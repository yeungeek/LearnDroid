package com.anhuioss.crowdroid.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.twitter.TwitterCommHandler;
import com.anhuioss.crowdroid.service.twitterproxy.TwitterProxyCommHandler;

public class ListBActivity extends TimelineActivity implements
		ServiceConnection {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		AccountData account = crowdroidApplication.getAccountList()
				.getCurrentAccount();
		ArrayList<ListInfo> listInfoList = crowdroidApplication
				.getListInfoList().getListsByUserName(account.getUserName());

		String fullName = listInfoList.get(1).getFullName();

		// Set Title
		setTitle(fullName);

		String user = fullName.split("/")[0].substring(1);

		// Prepare Parameters
		Map<String, String> map = new HashMap<String, String>();
		map.put("user", user);
		map.put("id", listInfoList.get(1).getId());
		TwitterCommHandler.setListParameter(map);
		TwitterProxyCommHandler.setListParameter(map);
		// Set Communication Type
		setCommType(CommHandler.TYPE_GET_LIST_TIMELINE);

	}

}
