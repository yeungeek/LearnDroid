package com.anhuioss.crowdroid;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.service.CommHandler;

public class FavoriteTimelineActivity extends TimelineActivity implements
		ServiceConnection {

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setTitle(R.string.favorite_timeline);

		setCommType(CommHandler.TYPE_GET_FAVORITE_LIST);
	}

}
