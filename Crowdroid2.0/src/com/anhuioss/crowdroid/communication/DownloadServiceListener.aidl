package com.anhuioss.crowdroid.communication;

interface DownloadServiceListener{
	
	//Request Completed
	void requestCompleted(in String uid, in String statusCode, in byte[] message);

}