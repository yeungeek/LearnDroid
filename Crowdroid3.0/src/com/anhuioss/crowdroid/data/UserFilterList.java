package com.anhuioss.crowdroid.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.anhuioss.crowdroid.IGeneral;

import android.content.Context;

public class UserFilterList {
	
	private static final String FILE_NAME = "filter_user.xml";
	
	private ArrayList<UserFilterData> list;
	
	private Context context;
	
	//----------------------------------------------------------------------
	/**
	 * Constructor
	 * @param context
	 */
	//----------------------------------------------------------------------
	public UserFilterList(Context context) {
		this.context = context;
		readXml();
	}
	
	//----------------------------------------------------------------------
	/**
	 * Add user filter
	 * @param UserFilterData userFilter
	 */
	//----------------------------------------------------------------------
	public void addUserFilter(UserFilterData userFilter) {
		list.add(userFilter);
		saveXml();
	}
	
	//----------------------------------------------------------------------
	/**
	 * Remove user filter
	 * @param UserFilterData userFilter
	 */
	//----------------------------------------------------------------------
	public void removeUserFilter(UserFilterData userFilter) {
		list.remove(userFilter);
		saveXml();
	}
	
	//----------------------------------------------------------------------
	/**
	 * Get all user filter
	 * @return ArrayList<UserFilterData> list
	 */
	//----------------------------------------------------------------------
	public ArrayList<UserFilterData> getAllUserFilter() {
		return list;
	}
	
	//----------------------------------------------------------------------
	/**
	 * Get user filter
	 * @param String service
	 * @return ArrayList<UserFilterData> extractedList
	 */
	//----------------------------------------------------------------------
	public ArrayList<UserFilterData> getUserFilter(String service) {
	    ArrayList<UserFilterData> extractedList = new ArrayList<UserFilterData>();
	    if(service != null && service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)){
	    	service = IGeneral.SERVICE_NAME_TWITTER;
	    }
	    for (UserFilterData data : list) {
	    	if (data.getService().equals(service)) {
	    	    extractedList.add(data);
	    	}
	    }
		return extractedList;
	}
	
	//----------------------------------------------------------------------
	/**
	 * Save xml
	 */
	//----------------------------------------------------------------------
	public synchronized void saveXml() {
		try {
			FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			PrintWriter pw = new PrintWriter(fos);
			StringBuffer buffer = new StringBuffer();
			buffer.append("<?xml version='1.0' encoding='utf-8' ?>");
			buffer.append("<filter_list>");
			
			for (UserFilterData data : list) {
				buffer.append("<filter>");
			    buffer.append(data.getUserFilterXml());
			    buffer.append("</filter>");
			}
			
			buffer.append("</filter_list>");
			pw.write(buffer.toString());
			pw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//----------------------------------------------------------------------
	/**
	 * Read xml
	 */
	//----------------------------------------------------------------------
	public void readXml() {
		
		FileInputStream fis = null;
	    try {
	    	fis = context.openFileInput(FILE_NAME);
	        list = parseUser(fis);
	    } catch (Exception e) {
	    	list = new ArrayList<UserFilterData>();
	    } finally {
	    	try {
				if(fis != null) {
					fis.close();
				}
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	}
	
	//----------------------------------------------------------------------
	/**
	 * Parse user
	 * @param InputStream is
	 * @return ArrayList<UserFilterData> userList
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	//----------------------------------------------------------------------
	private ArrayList<UserFilterData> parseUser(InputStream is) 
	    throws XmlPullParserException, IOException {
		
		ArrayList<UserFilterData> userList = new ArrayList<UserFilterData>();
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xmlPullParser = factory.newPullParser();
		xmlPullParser.setInput(is, "UTF-8");
		//int eventType = xmlPullParser.getEventType();
		
		while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
			
		    if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
		    	if (xmlPullParser.getName().equals("filter")) {
		    	    UserFilterData info = new UserFilterData();
		    	    
		    	    while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
		    	    	if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
		    	    		if (xmlPullParser.getName().equals("service")) {
		    		            String service = xmlPullParser.nextText();
		    		            info.setService(service);
		    		            continue;
		    				}
		    		        if (xmlPullParser.getName().equals("user_name")) {
		    		            String userName = xmlPullParser.nextText();
		    		            info.setUserName(userName);
		    		            continue;
		    		        }
		    	    	}
		    	    	if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
		    	    		if (xmlPullParser.getName().equals("filter")) {
		    	    			userList.add(info);
		    	    			break;
		    	    		}
		    	    	}
		    	    }
		    	}
	        	
		    }
		          
       }
		    
		            	        
		    
		return userList;
	}
	
}
