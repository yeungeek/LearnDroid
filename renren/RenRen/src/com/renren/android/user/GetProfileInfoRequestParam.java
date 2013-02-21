package com.renren.android.user;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class GetProfileInfoRequestParam extends RequestParam {
	private static final String METHOD = "users.getProfileInfo";
	private RenRen mRenRen = null;
	private String mSign = null;
	private int mUid = 0;
	private final String mFields = "base_info,status,visitors_count,blogs_count,albums_count,friends_count,guestbook_count,status_count";
	private Map<String, String> mMap = null;

	public GetProfileInfoRequestParam(RenRen renren, int uid) {
		mMap=new HashMap<String, String>();
		mRenRen = renren;
		mUid = uid;
		mSign=getSignature();
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
		mMap.put("uid", String.valueOf(mUid));
		mMap.put("fields", mFields);
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
