package com.anhuioss.crowdroid.activity;

import android.content.ServiceConnection;
import android.os.Bundle;

import com.anhuioss.crowdroid.BasicSearchActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.service.CommHandler;

public class KeywordSearchActivity extends BasicSearchActivity  implements ServiceConnection {
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.keyword_search);
		
		setCommType(CommHandler.TYPE_SEARCH_INFO);
	}

}
