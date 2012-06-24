package com.anhuioss.crowdroid.communication;

import com.anhuioss.crowdroid.communication.ApiServiceListener;

interface ApiServiceInterface{
	
	//Request
	void request(in String service, in int type, in ApiServiceListener listener, in Map parameters);
	
}