package com.renren.android.photos;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class GetAlbumsRequestParam extends RequestParam {
	private static final String METHOD = "photos.getAlbums";
	private RenRen mRenRen = null;
	private String mSign = null;
	private int mUid = 0;
	private String mPage = null;
	private String mCount = null;
	private Map<String, String> mMap = null;

	public GetAlbumsRequestParam(RenRen renren, int uid, String page,
			String count) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mUid = uid;
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
		if (mPage != null) {
			mMap.put("page", mPage);
		}
		if (mCount != null) {
			mMap.put("count", mCount);
		}
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
