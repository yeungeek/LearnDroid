package com.renren.android.photos;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class PhotosUploadRequestParam extends RequestParam {
	private static final String METHOD = "photos.upload";
	private RenRen mRenRen = null;
	private File mUpload = null;
	private String mCaption = null;
	private long mAid = 0;
	private String mSign = null;
	private Map<String, String> mMap = null;

	public PhotosUploadRequestParam(RenRen renren, File upload, String caption,
			long aid) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mUpload = upload;
		mCaption = caption;
		mAid = aid;
		mSign = getSignature();
	}

	public Map<String, String> getParams() {
		mMap.put("sig", mSign);
		mMap.put("upload", mUpload.toString());
		return mMap;
	}

	public String getSignature() {
		mMap.put("method", METHOD);
		mMap.put("v", mRenRen.VERSON);
		mMap.put("access_token", mRenRen.getAccessToken());
		mMap.put("format", mRenRen.FORMAT);
		if (mCaption != null) {
			mMap.put("caption", mCaption);
		}
		if (mAid != 0) {
			mMap.put("aid", String.valueOf(mAid));
		}
		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}
}
