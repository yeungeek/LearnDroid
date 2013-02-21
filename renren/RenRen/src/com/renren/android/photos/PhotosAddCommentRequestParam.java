package com.renren.android.photos;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class PhotosAddCommentRequestParam extends RequestParam {
	private static final String METHOD = "photos.addComment";
	private RenRen mRenRen = null;
	private String mSign = null;
	private String mContent = null;
	private int mUid = 0;
	private long mAid = 0;
	private long mPid = 0;
	private int mRid = 0;
	private int mType = 0;
	private Map<String, String> mMap = null;

	public PhotosAddCommentRequestParam(RenRen renren, String content, int uid,
			long aid, long pid, int rid, int type) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mContent = content;
		mUid = uid;
		mAid = aid;
		mPid = pid;
		mRid = rid;
		mType = type;
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
		mMap.put("content", mContent);
		mMap.put("uid", String.valueOf(mUid));
		mMap.put("type", String.valueOf(mType));
		if (mAid != 0) {
			mMap.put("aid", String.valueOf(mAid));
		}
		if (mPid != 0) {
			mMap.put("pid", String.valueOf(mPid));
		}
		if (mRid != 0) {
			mMap.put("rid", String.valueOf(mRid));
		}

		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}

}
