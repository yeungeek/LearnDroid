package com.renren.android.newsfeed;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class NewsFeedRequestParam extends RequestParam {
	private static final String METHOD = "feed.get";
	private RenRen mRenRen = null;
	private String mSign = null;
	private String mType = null;
	private String mUid = null;
	private String mPage_Id = null;
	private String mPage = null;
	private String mCount = null;
	private Map<String, String> mMap = null;

	public NewsFeedRequestParam(RenRen renren, String type, String uid,
			String page_id, String page, String count) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mType = type;
		mUid = uid;
		mPage_Id = page_id;
		mPage = page;
		mCount = count;
		mSign = getSignature();
	}

	public Map<String, String> getParams() {
		mMap.put("sig", mSign);
		return mMap;
	}

	public String getSignature() {
		mMap.put("method", METHOD);
		mMap.put("v", mRenRen.VERSON);
		mMap.put("type", mType);
		mMap.put("access_token", mRenRen.getAccessToken());
		mMap.put("format", mRenRen.FORMAT);
		if (mUid != null) {
			mMap.put("uid", String.valueOf(mUid));
		}
		if (mPage_Id != null) {
			mMap.put("page_id", String.valueOf(mPage_Id));
		}
		if (mPage != null) {
			mMap.put("page", mPage);
		}
		if (mCount != null) {
			mMap.put("count", mCount);
		}
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
