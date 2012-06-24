package com.anhuioss.crowdroid.translation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anhuioss.crowdroid.util.CommResult;
import com.anhuioss.crowdroid.util.HttpCommunicator;

public class BaiduTranslationHandler extends HttpCommunicator {

	private static String BAIDU_TRANSLATE_TOKEN = "dfeb6a780ea286be6f7b5a07d8247ab7";

	private static String BAIDU_TRANSLATE_T = "1327991045807";

	// post baidu：
	/**
	 * http://fanyi.baidu.com/transcontent
	 * @param query
	 * @param from
	 * @param to
	 */

	public static CommResult translate(String query, String from, String to)
			throws UnsupportedEncodingException, ClientProtocolException {

		CommResult result = new CommResult();

		// Prepare Parameters
		String urlStr = String
				.format("http://fanyi.baidu.com/transcontent?ie=utf-8&source=txt&from=%s&to=%s&query=%s&token=%s",
						from, to, URLEncoder.encode(query),	BAIDU_TRANSLATE_TOKEN);

		try {
			result = httpPostBasic(urlStr, new HashMap<String, String>(), "",
					"");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Parse
		if (result.getResponseCode() != null
				&& result.getResponseCode().equals("200")) {
			try {
				String translatedText = null;

				translatedText = getTranslateMessage(result.getMessage() , query);

				result.setMessage(translatedText);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			result.setResponseCode("200");
			result.setMessage(query);
		}
		return result;
	}

	private static String getTranslateMessage(String message , String query) {
		String translateMessage = null;
		try {
			JSONObject status = new JSONObject(message);
			if (status.has("data")) {
				JSONArray jArray = status.getJSONArray("data");
				JSONObject data = jArray.getJSONObject(0);
				translateMessage = data.getString("dst");
				if(translateMessage.contains("@ ")){
					translateMessage = translateMessage.replaceAll("@ ", "@");
				}
			}
			if (status.has("result")) {
				// only translate word from en to zh
				String content = status.getString("result");
				String filterReguler = "<p>1. (.*?)<\\/p>";
				Pattern p = Pattern.compile(filterReguler);
				Matcher matcher = p.matcher(content);
				if (matcher.find()) {
					translateMessage = matcher.group(1);
				}
				String filterMessage = null;
				if(translateMessage.contains(",")){
					
					filterMessage = translateMessage.substring(0 , translateMessage.indexOf(","));
					if("(".equals(filterMessage.indexOf(0))){
						filterMessage = filterMessage.substring(filterMessage.indexOf(")"));
					}
					if(filterMessage.contains("=")){
						filterMessage = query;
					}
					if(filterMessage.contains("【")){
						filterMessage = filterMessage.substring(filterMessage.indexOf("】") + 1);
					}
					
				} else if(translateMessage.contains(";")){
					
					filterMessage = translateMessage.substring(0,translateMessage.indexOf(";"));
					if(filterMessage.contains("[")){
						filterMessage = filterMessage.substring(0, filterMessage.indexOf("["));
					}
					if("(".equals(filterMessage.indexOf(0))){
						filterMessage = filterMessage.substring(filterMessage.indexOf(")"));
					}
					if(filterMessage.contains("=")){
						filterMessage = query;
					}
					if(filterMessage.contains("【")){
						filterMessage = filterMessage.substring(filterMessage.lastIndexOf("】") + 1);
					}
					
				} else if(translateMessage.contains("[(")){
					
					filterMessage = translateMessage.substring(0, translateMessage.indexOf("[("));
					
				} else if(translateMessage.equals("(")){
					
					if(translateMessage.indexOf("(") == 0){
						filterMessage = translateMessage.substring(translateMessage.indexOf(")"));
					}
					
				} else if(translateMessage.contains("[")){
					
					filterMessage = translateMessage.substring(0, translateMessage.indexOf("["));
					if(filterMessage.contains("【")){
						filterMessage = filterMessage.substring(filterMessage.lastIndexOf("】") + 1);
					}
					
				} else {
					filterMessage = query;
				}
				
				translateMessage = filterMessage;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return translateMessage;
	}
}
