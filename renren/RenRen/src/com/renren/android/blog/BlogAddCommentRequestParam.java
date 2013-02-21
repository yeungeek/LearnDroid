package com.renren.android.blog;

import java.util.HashMap;
import java.util.Map;

import com.renren.android.RenRen;
import com.renren.android.RequestParam;

public class BlogAddCommentRequestParam extends RequestParam {

	private static final String METHOD = "blog.addComment";
	private RenRen mRenRen = null;
	private String mSign = null;
	private String mContent = null;
	private int mId = 0;
	private int mUid = 0;
	private int mPageId = 0;
	private int mRid = 0;
	private int mType = 0;
	private Map<String, String> mMap = null;

	public BlogAddCommentRequestParam(RenRen renren, int id, int uid,
			int pageId, int rid, String content, int type) {
		mMap = new HashMap<String, String>();
		mRenRen = renren;
		mContent = content;
		mId = id;
		mUid = uid;
		mPageId = pageId;
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
		mMap.put("id", String.valueOf(mId));
		mMap.put("content", mContent);
		mMap.put("type", String.valueOf(mType));
		if (mUid != 0) {
			mMap.put("uid", String.valueOf(mUid));
		}
		if (mPageId != 0) {
			mMap.put("page_id", String.valueOf(mPageId));
		}
		if (mRid != 0) {
			mMap.put("rid", String.valueOf(mRid));
		}

		return mRenRen.getSignature(mMap, RenRen.RENREN_SECRET_KEY);
	}

}
