package com.renren.android.blog;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class BlogPublishRequestParam extends RequestParam {
	private static final String METHOD = "blog.addBlog";
	private RenRen mRenRen = null;
	private String mSign = null;
	private String mTitle = null;
	private String mContent = null;
	private Map<String, String> mMap = null;

	public BlogPublishRequestParam(RenRen renren, String title, String content) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mTitle = title;
		mContent = content;
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
		mMap.put("title", mTitle);
		mMap.put("content", mContent);
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
