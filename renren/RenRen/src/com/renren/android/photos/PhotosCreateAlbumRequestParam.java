package com.renren.android.photos;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class PhotosCreateAlbumRequestParam extends RequestParam {
	private static final String METHOD = "photos.createAlbum";
	private RenRen mRenRen = null;
	private String mName = null;
	private String mVisible = null;
	private String mPassword = null;
	private String mSign = null;
	private Map<String, String> mMap = null;

	public PhotosCreateAlbumRequestParam(RenRen renren, String name,
			String visible, String password) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mName = name;
		mVisible = visible;
		mPassword = password;
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
		mMap.put("name", mName);
		if (mVisible != null) {
			mMap.put("visible", mVisible);
		}
		if (mPassword != null) {
			mMap.put("password", mPassword);
		}
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
