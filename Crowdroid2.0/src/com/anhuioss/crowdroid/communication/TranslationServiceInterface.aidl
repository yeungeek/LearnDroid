package com.anhuioss.crowdroid.communication;

import com.anhuioss.crowdroid.communication.TranslationServiceListener;

interface TranslationServiceInterface{
	
	//Request
	void request(in String engine, in long type, in TranslationServiceListener listener, in Map parameters);
	
}