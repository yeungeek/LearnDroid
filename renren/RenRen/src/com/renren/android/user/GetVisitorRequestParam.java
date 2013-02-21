package com.renren.android.user;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class GetVisitorRequestParam extends RequestParam{
	private static final String METHOD = "users.getVisitors";
	private RenRen mRenRen = null;
	private String mSign = null;
	private String mPage = null;
	private String mCount = null;
	private Map<String, String> mMap = null;
	public GetVisitorRequestParam(RenRen renren,String page,String count){
		mMap=new HashMap<String, String>();
		mRenRen=renren;
		mPage=page;
		mCount=count;
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
		if (mPage != null) {
			mMap.put("page", mPage);
		}
		if (mCount != null) {
			mMap.put("count", mCount);
		}
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
