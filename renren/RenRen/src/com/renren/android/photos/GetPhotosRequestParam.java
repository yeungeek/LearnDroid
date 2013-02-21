package com.renren.android.photos;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class GetPhotosRequestParam extends RequestParam {
	private static final String METHOD = "photos.get";
	private RenRen mRenRen = null;
	private String mSign = null;
	private int mUid = 0;
	private long mAid = 0;
	private long mPid = 0;
	private String mPassword = null;
	private String mPage = null;
	private String mCount = null;
	private Map<String, String> mMap = null;

	public GetPhotosRequestParam(RenRen renren, int uid, long aid, long pid,
			String password, String page, String count) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mUid = uid;
		mAid = aid;
		mPid = pid;
		mPassword = password;
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
		mMap.put("uid", String.valueOf(mUid));
		if (mAid != 0) {
			mMap.put("aid", String.valueOf(mAid));
		}
		if (mPid != 0) {
			mMap.put("pid", String.valueOf(mPid));
		}
		if (mPage != null) {
			mMap.put("page", mPage);
		}
		if (mPassword != null) {
			mMap.put("password", mPassword);
		}
		if (mCount != null) {
			mMap.put("count", mCount);
		}
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
