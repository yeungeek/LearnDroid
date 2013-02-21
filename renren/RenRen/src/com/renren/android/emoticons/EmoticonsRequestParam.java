package com.renren.android.emoticons;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class EmoticonsRequestParam extends RequestParam {
	private static final String METHOD = "status.getEmoticons";
//	private static final String TYPE = "all";
	private RenRen mRenRen = null;
	private Map<String, String> mMap = null;
	private String mSign = null;

	public EmoticonsRequestParam(RenRen renren) {
		mRenRen = renren;
		mMap = new HashMap<String, String>();
		mSign = getSignature();
	}

	public Map<String, String> getParams() {
		mMap.put("sig", mSign);
		return mMap;
	}

	public String getSignature() {
		mMap.put("method", METHOD);
		mMap.put("v", mRenRen.VERSON);
//		mMap.put("type", TYPE);
		mMap.put("access_token", mRenRen.getAccessToken());
		mMap.put("format", mRenRen.FORMAT);
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
