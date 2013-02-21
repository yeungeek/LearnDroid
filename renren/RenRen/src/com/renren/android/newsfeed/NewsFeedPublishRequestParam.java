package com.renren.android.newsfeed;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class NewsFeedPublishRequestParam extends RequestParam {
	private static final String METHOD = "status.set";
	private RenRen mRenRen = null;
	private String mStatus = null;
	private String mSign = null;
	private Map<String, String> mMap = null;

	public NewsFeedPublishRequestParam(RenRen renren, String status) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mStatus = status;
		mSign = getSignature();
	}

	public Map<String, String> getParams() {
		mMap.put("sig", mSign);
		return mMap;
	}

	public String getSignature() {
		mMap.put("method", METHOD);
		mMap.put("v", mRenRen.VERSON);
		mMap.put("access_token", mRenRen.getAccessToken());
		mMap.put("format", mRenRen.FORMAT);
		mMap.put("status", mStatus);
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
