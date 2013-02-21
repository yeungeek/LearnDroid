package com.renren.android.blog;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class GetBlogRequestParam extends RequestParam {
	private static final String METHOD = "blog.get";
	private RenRen mRenRen = null;
	private String mSign = null;
	private int mId = 0;
	private int mUid = 0;
	private long mPageId = 0;
	private Map<String, String> mMap = null;

	public GetBlogRequestParam(RenRen renren, int id, int uid, long pageId) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mId = id;
		mUid = uid;
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
		mMap.put("id", String.valueOf(mId));
		if (mUid != 0) {
			mMap.put("uid", String.valueOf(mUid));
		}
		if (mPageId != 0) {
			mMap.put("page_id", String.valueOf(mPageId));
		}
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
