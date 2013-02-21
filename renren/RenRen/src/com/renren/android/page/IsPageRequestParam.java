package com.renren.android.page;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class IsPageRequestParam extends RequestParam {
	private static final String METHOD = "pages.isPage";
	private RenRen mRenRen = null;
	private int mUid = 0;
	private String mSign = null;
	private Map<String, String> mMap = null;

	public IsPageRequestParam(RenRen renren, int uid) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mUid = uid;
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
		mMap.put("page_id", String.valueOf(mUid));
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
