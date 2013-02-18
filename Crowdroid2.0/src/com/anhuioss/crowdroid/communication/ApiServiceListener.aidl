package com.anhuioss.crowdroid.communication;

interface ApiServiceListener{
	
	//Request Completed
	void requestCompleted(String service, int type, String statusCode, String message);

}