package com.anhuioss.crowdroid.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.anhuioss.crowdroid.translation.BaiduTranslationHandler;
import com.anhuioss.crowdroid.translation.BingTranslationHandler;
import com.anhuioss.crowdroid.util.CommResult;

public class TranslationHandler {

	// Type Id
	public static final int TYPE_DETECT = 0;

	public static final int TYPE_TRANSLATE = 1;

	@SuppressWarnings("unchecked")
	public CommResult request(String engine, long type, Map map) {

		CommResult result = new CommResult();

		switch ((int) type) {
		
		case TYPE_DETECT: {

			String query = (String) map.get("query");

			try {
				result = BingTranslationHandler.detect(query);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		}

		case TYPE_TRANSLATE: {

			String query = (String) map.get("query");
			String from = (String) map.get("from");
			String to = (String) map.get("to");

			if ("Baidu".equals(engine)) {
				try {
					result = BaiduTranslationHandler.translate(query, from, to);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				}
			} else if("Bing".equals(engine)){
				try {
					result = BingTranslationHandler.translate(query, from, to);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			break;
		}
		default: {

		}
		}

		return result;

	}

}
