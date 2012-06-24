package com.anhuioss.crowdroid.data.info;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class SearchInfo extends BasicInfo{
	public static final String FROM_USER = "from_user";
	public static final String PROFILE_IMAGE_URL="profile_image_url";
	public static final String TEXT="text";
	public static final String CREATED_AT="created_at";
	public static final String MESSAGE_ID="message_id";
	private String time_search;
	
	public String getTime_search() {
		return time_search;
	}
	public void setTime_search(String timeSearch) {
		time_search = timeSearch;
	}
	
	public static String getFormatTimeCrowdroid(String time){
		//Prepare Format
		String DATE_PATTERN_OUT = "yyyy-MM-dd HH:mm:ss";
		String DATE_PATTERN_IN = null;
			DATE_PATTERN_IN = "E dd MMM yyyy HH:mm:ss z";
		
		//Get Date Object from String
		SimpleDateFormat sourceFormat = new SimpleDateFormat(DATE_PATTERN_IN);  
		Date date = null;
		try {
			date = sourceFormat.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
		    e.printStackTrace();
		}

		//Get String from Date
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_OUT);  
		String formatTime = sdf.format(date); 
		
		
		return formatTime;  
		
		
	
	}

}
