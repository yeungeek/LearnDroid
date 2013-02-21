package com.renren.android.page;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class GetPageInfoRequestParam extends RequestParam {
	private static final String METHOD = "pages.getInfo";
	private RenRen mRenRen = null;
	private String mSign = null;
	private int mPageId = 0;
	private final String mFields = "status,albums_count,blogs_count,fans_count";
	private Map<String, String> mMap = null;

	public GetPageInfoRequestParam(RenRen renren, int pageId) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mPageId = pageId;
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
		mMap.put("page_id", String.valueOf(mPageId));
		mMap.put("fields", mFields);
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
