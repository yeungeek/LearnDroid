package com.renren.android.user;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class GetInfoRequestParam extends RequestParam {
	private static final String METHOD = "users.getInfo";
	private RenRen mRenRen = null;
	private String mSign = null;
	private String mUids = null;
	private final String mFields="uid,name,sex,star,zidou,vip,tinyurl,headurl,mainurl";
	private Map<String, String> mMap = null;

	public GetInfoRequestParam(RenRen renren, String uids) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mUids = uids;
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
		mMap.put("uids", mUids);
		mMap.put("fields", mFields);
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
