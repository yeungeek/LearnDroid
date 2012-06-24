package com.anhuioss.crowdroid.translation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.HttpCommunicator;

public class BingTranslationHandler extends HttpCommunicator {
	
	public static final String APIKEY = "9CBCCA72BFABFA9C1C3C75B8A62187487B3F9EC2"; 

	
	//-----------------------------------------------------------------------------------------------
	/**
	 * Judge the language from text.
	 * @throws UnsupportedEncodingException 
	 */
	//-----------------------------------------------------------------------------------------------
	public static CommResult detect(String query) throws UnsupportedEncodingException{
		
		CommResult result = new CommResult();
		
		//Prepare Parameters
		String url = "http://api.microsofttranslator.com/v2/Http.svc/Detect?appId=" + APIKEY
				 + "&text=" + URLEncoder.encode(query, HTTP.UTF_8);
		
		String name = "wangliang";
		String password = "oss";
		
		//Send Request
		try {
			result = httpGet(url, name, password);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Parse
		if(result.getResponseCode() != null && result.getResponseCode().equals("200")){
			try {
				String language = null;
				try {
					language = getXMLMessage(result.getMessage());
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				result.setMessage(language);
			} catch (JSONException e) {				
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		
			
		return result;
	}
	
	
	//-----------------------------------------------------------------------------------------------
	/**
	 * Translate Text.
	 * @throws UnsupportedEncodingException 
	 */
	//-----------------------------------------------------------------------------------------------
	public static CommResult translate(String query, String from, String to) throws UnsupportedEncodingException{

		CommResult result = new CommResult();
		
		//Prepare Parameters
		String url = "http://api.microsofttranslator.com/v2/Http.svc/Translate?appId=" + APIKEY
					+ "&text=" + URLEncoder.encode(query, HTTP.UTF_8)
					+ "&from=" + from 
					+ "&to=" + to;
		
		String name = "wangliang";
		String password = "oss";
		
		//Send Request
		try {
			result = httpGet(url, name, password);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Parse
		if(result.getResponseCode() != null && result.getResponseCode().equals("200")){
			try {
				String translatedText = null;
				try {
					translatedText = getXMLMessage(result.getMessage());
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				result.setMessage(translatedText);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			result.setResponseCode("200");
			result.setMessage(query);
		}
		
		
		return result;
	}
	
	//-----------------------------------------------------------------------------------
	/**
	 * Parse xml and get translatedText.
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 */
	//-----------------------------------------------------------------------------------
	private static String getXMLMessage(String msg) throws JSONException, XmlPullParserException, IOException{
		
		String xmlMessage  = null;
		InputStream is = new ByteArrayInputStream(msg.getBytes());
		// Using Pull Parser
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xmlPullParser = factory.newPullParser();
		xmlPullParser.setInput(is, "UTF-8");

		//--------------------------------------------------------
		// Start Parsing XML Data
		//--------------------------------------------------------
		while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {

			if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {	
				if (xmlPullParser.getName().equals("string")) {
					xmlPullParser.next();
					xmlMessage = xmlPullParser.getText();
					break;
				}
			}
		}	

		return xmlMessage;
	}

}
