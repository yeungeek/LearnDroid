package com.anhuioss.crowdroid.util;

public class CommResult {
	
	/** Response Code */
	private String responseCode;
	
	/** Message */
	private String message;
	
	
	//---------------------------------------------------------------------------
	/**
	 * Set Response Code
	 */
	//---------------------------------------------------------------------------
	public void setResponseCode(String responseCode){
		
		this.responseCode = responseCode;
		
	}
	
	//---------------------------------------------------------------------------
	/**
	 * Set Message
	 */
	//---------------------------------------------------------------------------
	public void setMessage(String message){
		
		this.message = message;
		
	}
	
	//---------------------------------------------------------------------------
	/**
	 * Get Response Code
	 */
	//---------------------------------------------------------------------------
	public String getResponseCode(){
		
		return responseCode;
		
	}
	
	//---------------------------------------------------------------------------
	/**
	 * Get Message
	 */
	//---------------------------------------------------------------------------
	public String getMessage(){
		
		return message;
		
	}

}
