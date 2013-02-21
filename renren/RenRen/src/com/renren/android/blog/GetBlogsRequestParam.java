package com.renren.android.blog;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class GetBlogsRequestParam extends RequestParam {
	private static final String METHOD = "blog.gets";
	private RenRen mRenRen = null;
	private String mSign = null;
	private int mUid = 0;
	private long mPageId = 0;
	private String mPage = null;
	private String mCount = null;
	private Map<String, String> mMap = null;

	public GetBlogsRequestParam(RenRen renren, int uid, long pageId, String page,
			String count) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mUid = uid;
		mPageId=pageId;
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
		mMap.put("access_token", mRenRen.getAccessToken());
		mMap.put("format", mRenRen.FORMAT);
		if (mUid!=0) {
			mMap.put("uid", String.valueOf(mUid));
		}
		if (mPageId!=0) {
			mMap.put("page_id", String.valueOf(mUid));
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
