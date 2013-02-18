package com.anhuioss.crowdroid.communication;

interface TranslationServiceListener{
	
	//Request Completed
	void requestCompleted(String engine, long type, String statusCode, String message);

}