package com.anhuioss.crowdroid.communication;

import com.anhuioss.crowdroid.communication.DownloadServiceListener;

interface DownloadServiceInterface{
	
	//Request
	void request(in String uid, in String url, in DownloadServiceListener listener);
	
}